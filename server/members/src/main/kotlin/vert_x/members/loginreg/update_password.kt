package vert_x.members.loginreg

import com.google.gson.JsonObject
import common.functions.s
import entities.Member_V1
import io.vertx.core.eventbus.Message
import memory.db_users
import objects.EndPointReply
import shiro.Shiro
import vertxl.LTVertexVerticle

class update_password : LTVertexVerticle() {
    override var address = "members.update-password"
    override suspend fun doAction(message: Message<Any>, ep: EndPointReply, obj: JsonObject) {
        val reference = obj.get("reference").asString ?: return
        val oldPassword = obj.get("oldPassword").asString ?: return
        val newPassword = obj.get("newPassword").asString ?: return


        val m = db_users.getByReference(reference) ?: return

        if(!Shiro.validatePassword(oldPassword, m.member.password)) return

        m.password = newPassword

        db_users.save(m)

        ep.data = m.toUser()
        ep.success = true
    }
}
