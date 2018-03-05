package vert_x.members.loginreg

import com.google.gson.JsonObject
import common.functions.s
import entities.Member_V1
import io.vertx.core.eventbus.Message
import memory.db_users
import objects.EndPointReply
import vertxl.LTVertexVerticle

class two_factor_key_get : LTVertexVerticle() {
    override var address = "members.get-two-factor-key"
    override suspend fun doAction(message: Message<Any>, ep: EndPointReply, obj: JsonObject) {

        val reference = obj.s("reference")
        val email = obj.s("email")

        if (reference == null && email == null) return

        val m = ( if (reference != null) db_users.getByReference(reference) else db_users.getByEmail(email!!) ) ?: return

        ep.data.addProperty("key", m.twoFactorSecretKey)
        ep.data.addProperty("twoFactorRecoveryCode", m.twoFactorRecoveryCode)
        ep.data.addProperty("enabled", m.twofactorEnabled)
        ep.data.addProperty("firstName", m.firstName)
        ep.data.addProperty("lastName", m.lastName)
        ep.data.addProperty("email", m.email)
        ep.success = true
    }
}
