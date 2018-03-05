package vert_x.email
import com.google.gson.JsonObject
import common.functions.o
import common.functions.s
import io.vertx.core.eventbus.Message
import objects.EndPointReply
import vertxl.LTVertexVerticle
import java.util.HashMap
import vertxl.VertX
import email.EmailVertical

class send_email : LTVertexVerticle() {
    override var address = "contact.send-email"
    override var doReply = false
    override suspend fun doAction(message: Message<Any>, ep: EndPointReply, obj: JsonObject) {
        val map = HashMap<String, Any>()
        val newMessage = HashMap<String, String>()
        map.put("newMessage", newMessage)

        val subject         = obj.s("subject")      ?: return
        val email           = obj.s("email")        ?: return
        val template        = obj.s("template")     ?: return
        val newMessageSent  = obj.o("newMessage")   ?: return

        newMessageSent.keySet().map { newMessage.put(it, newMessageSent[it].asString) }


        VertX.vertx?.deployVerticle(EmailVertical(subject, email, template, map))

        ep.success = true
    }
}
