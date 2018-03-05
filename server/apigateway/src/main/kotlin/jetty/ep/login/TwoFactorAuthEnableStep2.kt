package jetty.ep.login

import com.google.gson.Gson
import com.google.gson.JsonObject
import common.functions.s
import jetty.EndPoint
import objects.EndPointReply
import org.apache.shiro.SecurityUtils
import shiro.Shiro
import vertxl.VertX
import java.time.LocalDateTime

class TwoFactorAuthEnableStep2 : EndPoint() {
    override suspend fun doAction(gson: Gson, obj: JsonObject, endPointReply: EndPointReply, utcTime: LocalDateTime) {
        val subject = SecurityUtils.getSubject()
        val expectedToken       = obj.s("expected_token") ?: return
        val reference           = Shiro.get_reference(subject) ?: return
        if (expectedToken != subject.session.getAttribute("expected_token")) { return }

        val o = JsonObject(); o.addProperty("reference", reference)
        val epActivate = VertX.awaitFor("members.activate-two-factor", o)

        endPointReply.success = epActivate.success
    }
}