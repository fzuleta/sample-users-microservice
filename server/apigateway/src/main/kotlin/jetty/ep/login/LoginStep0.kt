package jetty.ep.login

import com.google.gson.Gson
import com.google.gson.JsonObject
import common.Google
import common.functions.bool
import common.functions.isValidEmail
import common.functions.s
import common.functions.validPassword
import jetty.EndPoint
import objects.EndPointReply
import objects.SYError
import org.apache.shiro.SecurityUtils
import shiro.Shiro
import vertxl.VertX
import java.time.LocalDateTime

class LoginStep0: EndPoint() {
    override suspend fun doAction(gson: Gson, obj: JsonObject, endPointReply: EndPointReply, utcTime: LocalDateTime) {
        val subject = SecurityUtils.getSubject()
        val email               = obj.s("email")?.toLowerCase() ?: return
        val password            = obj.s("password") ?: return
        val captchaPreChecked   = obj.bool("captchaPreChecked") // this is for auto login
        val captcha             = obj.s("captcha") ?: ""

        if (!isValidEmail(email)
            || !validPassword(password, password)
            || Shiro.hasUserCalledServerVeryRecently(subject)
            || (!captchaPreChecked && !Google.checkCaptcha(captcha))
            || email.length > 150
            || password.length > 100
        ) return

        // cleanup
        subject.session.removeAttribute("prelogin")

        // Do a prelogin for 2FA (we dont want to fully login the user at this point)
        val o = JsonObject(); o.addProperty("email", email); o.addProperty("password", password)
        val prelogin = VertX.awaitFor("members.login", o)

        if (prelogin.success) {
            subject.session.setAttribute("prelogin", prelogin.data["reference"].asString)

            if (!prelogin.data["emailConfirmed"].asBoolean) {
                endPointReply.data.addProperty("confirmEmail", true)
                endPointReply.success = true
                return
            }
            if (prelogin.data["twofactorEnabled"].asBoolean) {
                endPointReply.data.addProperty("show2FA", true)
                endPointReply.success = true
                return
            }
        } else {
            endPointReply.errorCode = 9
            endPointReply.error = SYError.Error.MEMBER_WRONG_CREDENTIALS
            return
        }

        LoginStep2().doAction(gson, obj, endPointReply, utcTime)

    }
}