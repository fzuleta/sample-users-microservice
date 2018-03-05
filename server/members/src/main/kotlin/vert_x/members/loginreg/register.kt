package vert_x.members.loginreg

import com.google.gson.JsonObject
import common.functions
import common.functions.s
import entities.Delegate_MemberV1
import entities.Member_V1
import io.vertx.core.eventbus.Message
import memory.db_users
import objects.EndPointReply
import vertxl.LTVertexVerticle

class register : LTVertexVerticle() {
    override var address = "members.register"
    override var doReply = true
    override suspend fun doAction(message: Message<Any>, ep: EndPointReply, obj: JsonObject) {
        val gson = com.google.gson.GsonBuilder().disableHtmlEscaping().create()

        val email = obj.s("email")?.toLowerCase() ?: return
        obj.s("password") ?: return

        val prevEmail = db_users.getByEmail(email)
        if (prevEmail != null) return

        val sentM = gson.fromJson(obj.toString(), Member_V1::class.java)

        val j = db_users.getJedis(sentM.reference) ?: return
        val d = Delegate_MemberV1(sentM)

        // Find a valid reference ================================================================
        var exists = true
        while (exists) {
            exists = j.hget(db_users.REF, d.reference) != null
            if (exists) d.reference = functions.aRandomString
        }


        // store the user ========================================================================
        val m = db_users.save(d, j)

        // Store an email reference ==============================================================
        val email1 = if (db_users.isGuest(d.reference)) db_users.EMAIL_GUEST else db_users.EMAIL
        j.hset(email1, d.email, d.reference)


        j.close()
        ep.success = true
        ep.data = m.toUser()
    }
}
