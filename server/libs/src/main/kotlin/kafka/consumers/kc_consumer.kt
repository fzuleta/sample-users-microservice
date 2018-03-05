package kafka.consumers

import com.google.common.util.concurrent.AbstractScheduledService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import kotlinx.coroutines.experimental.runBlocking
import org.apache.kafka.clients.consumer.KafkaConsumer
import java.util.*
import java.util.concurrent.TimeUnit

open class kc_consumer: AbstractScheduledService() {
    var consumer:KafkaConsumer<String, String>? = null
    var consumerConfig:Properties? = null
    var closing = false
    var running = false
    open var period = 10000L
    open var topic = ""
    override fun scheduler(): Scheduler {
        return Scheduler.newFixedRateSchedule(0, period, TimeUnit.MILLISECONDS)
    }
    override fun startUp() {
        consumer = KafkaConsumer(consumerConfig)
        consumer?.subscribe(Collections.singletonList(topic))

        println("Consuming consumer > Initial Consumer poll")
    }
    override fun runOneIteration() {
        if (running) return
        running = true
        val gson: Gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
        try {
            val consumerRecords = consumer!!.poll(0)
            if (consumerRecords.count() > 0) {
                consumerRecords.forEach { record ->
                    runBlocking {
                        val o = gson.fromJson(record.value(), JsonObject::class.java)
//                        println("Consumer Record:" + record.key() + " " + record.value() + " " + record.partition() + " " + record.offset())
                        //o.reference o.date. o.o
                        action(o)
                    }
                }
            }

            consumer?.commitAsync()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            println("ran consumer: $topic exists: ${consumer != null}")
            consumer?.commitSync()
//            consumer?.close()
            running = false
        }
    }
    open suspend fun action(obj:JsonObject) {
        TODO("Override this")
    }
    fun close() {
        consumer?.close()
        closing = true
    }
}