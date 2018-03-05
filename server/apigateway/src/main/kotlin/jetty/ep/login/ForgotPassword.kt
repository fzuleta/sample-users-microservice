package jetty.ep.login

import com.google.gson.Gson
import com.google.gson.JsonObject
import common.Google
import common.functions
import common.functions.s
import jetty.EndPoint
import jetty.contact.emails
import objects.EndPointReply
import org.apache.shiro.SecurityUtils
import shiro.Shiro
import vertxl.VertX
import java.time.LocalDateTime

class ForgotPassword: EndPoint() {
    override suspend fun doAction(gson: Gson, obj: JsonObject, endPointReply: EndPointReply, utcTime: LocalDateTime) {
        val email           = obj.s("email") ?: return
        val expectedToken   = obj.s("expected_token")  ?: return
        val captcha         = obj.s("captcha") ?: "-"
        val subject = SecurityUtils.getSubject()
        val serverExpectedToken = subject.session.getAttribute("expected_token")  == expectedToken

        if (!functions.isValidEmail(email)
            || !serverExpectedToken
            || !Google.checkCaptcha(captcha)) { return }

        // time constrain the email sending
        if (Shiro.hasUserCalledServerVeryRecently(subject)) {
            endPointReply.data.addProperty("tooSoon", true)
            return
        }

        val o = JsonObject() ; o.addProperty("email", email)

        val epGet = VertX.awaitFor("members.get", o)
        if (!epGet.success) return

        emails().`send forgot password email`(epGet.data)

        endPointReply.success = true
    }
}