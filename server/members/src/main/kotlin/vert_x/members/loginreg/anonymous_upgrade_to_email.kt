package vert_x.members.loginreg

import com.google.gson.JsonObject
import common.functions
import common.functions.s
import io.vertx.core.eventbus.Message
import memory.db_users
import objects.EndPointReply
import vertxl.LTVertexVerticle

class anonymous_upgrade_to_email : LTVertexVerticle() {
    override var address = "members.upgrade-to-email-account"
    override suspend fun doAction(message: Message<Any>, ep: EndPointReply, obj: JsonObject) {

        val reference = obj.s("reference") ?: return
        val email = obj.s("email") ?: return
        if (db_users.getByEmail(email) != null) return

        val m = db_users.getByReference(reference) ?: return
        if(!m.anonymousAccount) return

        val previousReference = m.reference
        val previousEmail = m.email!!

        m.reference = functions.aRandomString

        m.anonymousAccount = false
        m.anonymousPassword = ""
        m.email = email

        db_users.save(m)
        db_users.deleteGuest(previousReference, previousEmail)

        ep.data = m.toUser()
        ep.success = true

    }
}
