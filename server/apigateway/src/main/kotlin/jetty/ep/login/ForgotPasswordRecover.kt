package jetty.ep.login

import com.google.gson.Gson
import com.google.gson.JsonObject
import common.Google
import common.StringUtils
import common.functions
import common.functions.s
import jetty.EndPoint
import jetty.contact.emails
import objects.EndPointReply
import org.apache.shiro.SecurityUtils
import shiro.Shiro
import vertxl.VertX
import java.net.URLDecoder
import java.time.LocalDateTime


class ForgotPasswordRecover: EndPoint() {
    override suspend fun doAction(gson: Gson, obj: JsonObject, endPointReply: EndPointReply, utcTime: LocalDateTime) {
        val expectedToken       = obj.s("expected_token")  ?: return
        val captcha             = obj.s("captcha") ?: "-"
        val password0           = obj.s("password0") ?: return
        val password1           = obj.s("password1") ?: "y"
        val subject             = SecurityUtils.getSubject()
        val serverExpectedToken = subject.session.getAttribute("expected_token")  == expectedToken
        var code                = obj.s("code") ?: return
        code = URLDecoder.decode(code, "UTF-8")
        val reference           = StringUtils.simpleDecrypt(code) ?: return

        if (!functions.validPassword(password0, password1)
                || !serverExpectedToken
                || !Google.checkCaptcha(captcha)) { return }

        // time constrain the email sending
        if (Shiro.hasUserCalledServerVeryRecently(subject)) {
            endPointReply.data.addProperty("tooSoon", true)
            return
        }

//        println("yeyeeeah: $reference")

        val o = JsonObject() ;
        o.addProperty("reference", reference)
        o.addProperty("password", password0)
        val epGet = VertX.awaitFor("members.recover-password", o)
        if (!epGet.success) return

        emails().`send password recovered email`(epGet.data)

        endPointReply.success = true
    }
}