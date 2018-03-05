package vert_x.members.loginreg

import com.google.gson.JsonObject
import common.functions.s
import io.vertx.core.eventbus.Message
import memory.db_users
import objects.EndPointReply
import vertxl.LTVertexVerticle

class recover_password : LTVertexVerticle() {
    override var address = "members.recover-password"
    override suspend fun doAction(message: Message<Any>, ep: EndPointReply, obj: JsonObject) {
        // find an individual one
        val reference = obj.s("reference")  ?: return
        val password = obj.s("password")  ?: return

        val m = db_users.getByReference(reference) ?: return
        m.setRecoverPassword(password)

        db_users.save(m)

        ep.data = m.toUser()
        ep.success = true
    }
}
