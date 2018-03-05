package vert_x.members.loginreg

import com.google.gson.JsonObject
import common.functions.s
import entities.Member_V1
import io.vertx.core.eventbus.Message
import memory.db_users
import objects.EndPointReply
import vertxl.LTVertexVerticle

class two_factor_key_set : LTVertexVerticle() {
    override var address = "members.set-two-factor-key"
    override suspend fun doAction(message: Message<Any>, ep: EndPointReply, obj: JsonObject) {

        val reference = obj.s("reference") ?: return
        val secretKey = obj.s("key") ?: return

        val m = db_users.getByReference(reference) ?: return

        val b = m.setTwoFactor(secretKey)
        if (b) {
            db_users.save(m)
        }
        ep.success = b
        ep.data.addProperty("recoveryCode", m.twoFactorRecoveryCode)
    }
}
