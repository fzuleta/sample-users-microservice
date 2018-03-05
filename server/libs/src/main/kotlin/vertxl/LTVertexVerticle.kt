package vertxl

import com.google.gson.JsonObject
import io.vertx.core.eventbus.Message
import io.vertx.kotlin.coroutines.CoroutineVerticle
import kotlinx.coroutines.experimental.runBlocking
import objects.EndPointReply

open class LTVertexVerticle : CoroutineVerticle() {
    open var address = "xxx"
    open var doReply = true
    suspend override fun start() {
        println("vert.x > $address")
        val gson = com.google.gson.GsonBuilder().disableHtmlEscaping().create()

        class ConsumerVerticle( val message: Message<Any>): CoroutineVerticle() {
            init {
                val ep = EndPointReply()
                try {
                    val obj: JsonObject = try { gson.fromJson(message.body().toString(), JsonObject::class.java) } catch (e: Exception) { JsonObject() }
                    runBlocking {
                        doAction(message, ep, obj)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    if (doReply) reply(message, ep)
                }
            }
        }

        vertx.eventBus().consumer<Any> (address) { message ->
            vertx.deployVerticle(ConsumerVerticle(message))
        }

    }

    open suspend fun doAction(message: Message<Any>, ep: EndPointReply, obj: JsonObject) {
        throw Exception("You must override this")
    }
    open fun reply(message: Message<Any>, ep: EndPointReply) = message.reply(ep.toString())
    open fun reply(message: Message<Any>, str: String) = message.reply(str)
}