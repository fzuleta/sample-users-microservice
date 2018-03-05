package jetty.ep.login

import com.google.gson.Gson
import com.google.gson.JsonObject
import common.functions.s
import jetty.EndPoint
import jetty.contact.emails
import objects.EndPointReply
import org.apache.shiro.SecurityUtils
import shiro.Shiro
import vertxl.VertX
import java.time.LocalDateTime

class TwoFactorAuthDisableStep0 : EndPoint() {
    override suspend fun doAction(gson: Gson, obj: JsonObject, endPointReply: EndPointReply, utcTime: LocalDateTime) {
        val subject = SecurityUtils.getSubject()
        val expectedToken       = obj.s("expected_token") ?: return
        val reference           = Shiro.get_reference(subject) ?: return
        val password            = obj.s("password") ?: return
        if (expectedToken != subject.session.getAttribute("expected_token")) { return }

        val oV = JsonObject();
        oV.addProperty("reference", reference)
        oV.addProperty("password", password)
        val epActivate = VertX.awaitFor("members.verify-password", oV)

        if (epActivate.success) {
            emails().`send disable two factor auth confirmation email`(epActivate.data)

        }

        endPointReply.success = epActivate.success
    }
}