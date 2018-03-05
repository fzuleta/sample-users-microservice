package vert_x.members.loginreg

import com.google.gson.JsonObject
import common.functions.s
import entities.Member_V1
import io.vertx.core.eventbus.Message
import memory.db_users
import objects.EndPointReply
import vertxl.LTVertexVerticle

class register_anonymous : LTVertexVerticle() {
    override var address = "members.register-anonymous"
    override suspend fun doAction(message: Message<Any>, ep: EndPointReply, obj: JsonObject) {

        val gson = com.google.gson.GsonBuilder().disableHtmlEscaping().create()

        val sentM = gson.fromJson(obj.toString(), Member_V1::class.java)
        sentM.reference = "guest_${sentM.reference}ttt"
        sentM.email = "guest_${sentM.email}ttt"
        val m = db_users.save(sentM)

        ep.data =  m.toUser()
        ep.success = true

    }
}
