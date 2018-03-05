package vert_x.members.loginreg

import com.google.gson.JsonObject
import common.functions.s
import entities.Member_V1
import io.vertx.core.eventbus.Message
import memory.db_users
import objects.EndPointReply
import shiro.Shiro
import vertxl.LTVertexVerticle

class verify_password : LTVertexVerticle() {
    override var address = "members.verify-password"
    override suspend fun doAction(message: Message<Any>, ep: EndPointReply, obj: JsonObject) {
        val reference = obj.get("reference").asString ?: return
        val password = obj.get("password").asString ?: return

        val m = db_users.getByReference(reference) ?: return

        ep.data = m.toUser()
        ep.success = Shiro.validatePassword(password, m.password)
    }
}
