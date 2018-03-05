package vert_x.members.member_v1


import com.google.gson.JsonObject
import common.functions.s
import io.vertx.core.eventbus.Message
import memory.db_users
import objects.EndPointReply
import vertxl.LTVertexVerticle

class get : LTVertexVerticle() {
    override var address = "members.get"
    override suspend fun doAction(message: Message<Any>, ep: EndPointReply, obj: JsonObject) {

        // find an individual one
        val reference = obj.s("reference")
        val email = obj.s("email")

        if(reference == null && email == null) return

        val m = when {
            reference != null   -> db_users.getByReference(reference) ?: return
            email != null       -> db_users.getByEmail(email)?: return
            else -> return
        }

        ep.data = m.toUser()
        ep.success = true

    }
}
