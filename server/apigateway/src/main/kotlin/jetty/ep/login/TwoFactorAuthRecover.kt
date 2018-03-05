package jetty.ep.login

import com.google.gson.Gson
import com.google.gson.JsonObject
import common.Google
import common.functions.s
import jetty.EndPoint
import jetty.contact.emails
import objects.EndPointReply
import org.apache.shiro.SecurityUtils
import vertxl.VertX
import java.time.LocalDateTime

class TwoFactorAuthRecover : EndPoint() {
    // This follows the same setup as LoginStep1
    override suspend fun doAction(gson: Gson, obj: JsonObject, endPointReply: EndPointReply, utcTime: LocalDateTime) {
        val subject             = SecurityUtils.getSubject()
        val expectedToken       = obj.s("expected_token")  ?: return
        val code                = obj.s("code") ?: return
        val reference           = subject.session.getAttribute("prelogin")?.toString() ?: return
        val captcha             = obj.s("captcha") ?: ""

        if (subject.session.getAttribute("expected_token")  != expectedToken
            || !Google.checkCaptcha(captcha)
        ) { return }

        // verify the code
        val o = JsonObject();
        o.addProperty("reference", reference)
        val epMyInfo = VertX.awaitFor("members.get-two-factor-key", o)
        if (!epMyInfo.success) return

        val b = code == epMyInfo.data["twoFactorRecoveryCode"].asString
        if(!b) return

        endPointReply.success = b

        //Allow login
        obj.addProperty("rememberMe", false)
        LoginStep2().doAction(gson, obj, endPointReply, utcTime)

        println(epMyInfo.data)

        emails().`two-factor-password used`(epMyInfo.data)
    }
}