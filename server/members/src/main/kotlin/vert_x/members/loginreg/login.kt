package vert_x.members.loginreg

import com.google.gson.JsonObject
import common.functions.s
import io.vertx.core.eventbus.Message
import memory.db_users
import objects.EndPointReply
import shiro.Shiro
import vertxl.LTVertexVerticle

class login : LTVertexVerticle() {
    override var address = "members.login"
    override suspend fun doAction(message: Message<Any>, ep: EndPointReply, obj: JsonObject) {
        val email = obj.s("email")?.toLowerCase() ?: return
        val password = obj.s("password") ?: return
        val m = db_users.getByEmail(email) ?: return
        if(!Shiro.validatePassword(password, m.member.password)) return

        ep.data.addProperty("emailConfirmed", m.emailConfirmed)
        ep.data.addProperty("twofactorEnabled", m.twofactorEnabled)
        ep.data.addProperty("reference", m.reference)
        ep.success = true
    }
}
