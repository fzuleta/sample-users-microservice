package vert_x.members.member_v1


import com.google.gson.JsonObject
import common.functions.s
import io.vertx.core.eventbus.Message
import memory.db_users
import objects.EndPointReply
import vertxl.LTVertexVerticle

class get_my_privates : LTVertexVerticle() {
    override var address = "members.get-my-privates"
    override suspend fun doAction(message: Message<Any>, ep: EndPointReply, obj: JsonObject) {

        val reference = obj.s("reference") ?: return
        val m = db_users.getByReference(reference) ?: return
        if (!m.anonymousAccount) return

        ep.data.addProperty("email", m.email)
        ep.data.addProperty("anonymousPassword", m.anonymousPassword)
        ep.success = true

    }
}
