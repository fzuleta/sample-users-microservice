package jetty.ep.login

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.warrenstrange.googleauth.GoogleAuthenticator
import common.functions
import common.functions.s
import jetty.EndPoint
import objects.EndPointReply
import org.apache.shiro.SecurityUtils
import shiro.Shiro
import vertxl.VertX
import java.time.LocalDateTime

class TwoFactorAuthEnableStep1 : EndPoint() {
    override suspend fun doAction(gson: Gson, obj: JsonObject, endPointReply: EndPointReply, utcTime: LocalDateTime) {
        val subject = SecurityUtils.getSubject()
        val expectedToken       = obj.s("expected_token") ?: return
        val reference           = Shiro.get_reference(subject) ?: return
        val validationCode     = obj.s("code") ?: return
        if (expectedToken != subject.session.getAttribute("expected_token")) { return }
        if (!functions.isNumeric(validationCode)) { return }

        val o = JsonObject()
        o.addProperty("reference", reference)
        val epMyInfo = VertX.awaitFor("members.get-two-factor-key", o)
        if (!epMyInfo.success) return

        val secretKey = epMyInfo.data["key"].asString

        println("FOUND M $secretKey")
        endPointReply.success = GoogleAuthenticator().authorize(secretKey, validationCode.toInt())
    }
}