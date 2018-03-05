package rabbitmq


import com.google.common.util.concurrent.AbstractService
import com.google.gson.JsonObject
import com.rabbitmq.client.*
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import objects.EndPointReply
import java.io.IOException
import java.util.*
import java.util.concurrent.ArrayBlockingQueue


open class RabbitMQ : AbstractService() {
    var host = "localhost"
    var port = 15672
    var user = "root"
    var pass = "root"
    var qos = 1
    var connection: Connection? = null
    var channel: Channel? = null
    open var initCallback = {}
    var started = false

    // Exchange types
    private val FANOUT = "fanout"
    private val DIRECT = "direct"
    private val HEADERS = "headers"
    private val TOPIC = "topic"

    override fun doStart() {
        val factory = ConnectionFactory()

        println("Rabbit settings: $host $port")

        factory.host = host
        factory.username = user
        factory.password = pass
        factory.port = port
        try {
            connection = factory.newConnection()
            channel = createChannel(connection, qos)
            started = true
            initCallback()


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun doStop() {
        try {
            if (channel != null) channel!!.close()
            if (connection != null) connection!!.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun add(channel:Channel?, queue:String, callback: suspend (it:String)->EndPointReply, durable:Boolean = false, exclusive:Boolean = false, autodelete:Boolean = false){
        createQueue(channel, queue, durable, exclusive, autodelete)
        createRPCConsumer(channel, queue, { callback(it) })
    }

    fun createExchange(channel: Channel, exchangeName: String, type: String): AMQP.Exchange.DeclareOk {
        val result = channel.exchangeDeclare(exchangeName, type)
        println("Declared Exchange " + exchangeName)
        return result
    }

    fun createQueue(channel: Channel?, queueName: String, durable: Boolean?, exclusive: Boolean?, autoDelete: Boolean?): AMQP.Queue.DeclareOk {
        val result = channel!!.queueDeclare(queueName, durable!!, exclusive!!, autoDelete!!, null)
        println("Declared Queue " + queueName)
        return result
    }

    val randomQueue: String
        get() = channel!!.queueDeclare().queue

    fun queueBind(channel: Channel, queueName: String, exchangeName: String, routingKey: String) {
        channel.queueBind(queueName, exchangeName, routingKey)
    }

    fun createChannel(connection: Connection?, qos: Int?): Channel {
        val channel = connection!!.createChannel()
        // qos = messages that can be sent to a server at a time
        if (qos != null) {
            channel.basicQos(qos)
        }
        return channel
    }

    fun basicPublish(exchange: String, queueName: String, properties: AMQP.BasicProperties?, message: String) =
            channel!!.basicPublish(exchange, queueName, properties, message.toByteArray())

    fun publish(exchange:String = "", queue:String, properties: AMQP.BasicProperties? = null, message:String="") =
            basicPublish(exchange, queue, properties, message)

    fun sendEmail(message:String="") {
        val props = AMQP.BasicProperties.Builder()
                .expiration("60000")
                .build()
        basicPublish("", "contact.send-email", props, message)
    }
    suspend fun ep(queue:String, o: JsonObject, callback: suspend (ep: EndPointReply)-> Unit) = call(queue, o, callback)

    private suspend fun call(requestQueue: String, o: JsonObject, callback: suspend (ep: EndPointReply)-> Unit) {
        if (!started) { async {callback( EndPointReply())}; return }

        val message = o.toString()
        val corrId = UUID.randomUUID().toString()
        val replyQueueName = randomQueue
        val response = ArrayBlockingQueue<String>(1)


        val props = AMQP.BasicProperties.Builder()
                .correlationId(corrId)
                .replyTo(replyQueueName)
                .expiration("30000")
                .build()

        channel?.basicPublish("", requestQueue, props, message.toByteArray(charset("UTF-8")))
        channel?.basicConsume(replyQueueName, true, object : DefaultConsumer(channel) {
            override fun handleDelivery(consumerTag: String?, envelope: Envelope?, properties: AMQP.BasicProperties?, body: ByteArray) {
                if (properties!!.correlationId == corrId) {
                    response.offer(String(body))
                }

                channel.queueDelete(replyQueueName)
            }
        })

        // this is a core part of the system, if while is too much, then
        val a = async {
            val millis = System.currentTimeMillis()
            while(response.count() == 0) {
                val diff = System.currentTimeMillis() - millis
                if (diff > 30000) {
                    break
                }
            }
            if (response.count() > 0) response.take() else EndPointReply().toString()
        }
        a.await()
        callback(EndPointReply.fromString(a.getCompleted()))
    }

    fun createRPCConsumer(channel:Channel?, queue: String, fcn: suspend (String)->EndPointReply) {

        // create the consumer
        val consumer = object : DefaultConsumer(channel) {
            @Throws(IOException::class)
            override fun handleDelivery(consumerTag: String?, envelope: Envelope?, properties: AMQP.BasicProperties?, body: ByteArray?) {
                val replyProps = AMQP.BasicProperties.Builder()
                        .correlationId(properties!!.correlationId)
                        .build()
                var endPointReply = EndPointReply()

                launch {
                    try {
                        val message = String(body!!)
                        endPointReply = fcn(message)

                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        try {
                            channel!!.basicPublish("", properties.replyTo, replyProps, endPointReply.toString().toByteArray(Charsets.UTF_8))
                        } catch (e: Exception) {
                            //                        println("RabbitMQ.java > createRPCConsumer > properties.routingKey is null")
                        }

                        channel!!.basicAck(envelope!!.deliveryTag, false)
                    }
                }
            }
        }

        try {
            channel!!.basicConsume(queue, false, consumer)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}
