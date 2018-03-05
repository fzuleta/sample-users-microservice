package vert_x.members.loginreg

import com.google.gson.JsonObject
import common.functions.s
import io.vertx.core.eventbus.Message
import memory.db_users
import objects.EndPointReply
import vertxl.LTVertexVerticle

class confirm_email : LTVertexVerticle() {
    override var address = "members.confirm-email"
    override suspend fun doAction(message: Message<Any>, ep: EndPointReply, obj: JsonObject) {

        val reference = obj.s("reference") ?: return
        val emailConfirmedCode = obj.s("emailConfirmedCode") ?: return

        val m = db_users.getByReference(reference) ?: return
        if (m.emailConfirmedCode != emailConfirmedCode.toInt()) return

        m.confirmEmail()

        db_users.save(m)

        ep.data =  m.toUser()
        ep.success = true

    }
}
