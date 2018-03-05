package jetty.ep.login

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.warrenstrange.googleauth.GoogleAuthenticator
import common.functions
import common.functions.s
import jetty.EndPoint
import objects.EndPointReply
import org.apache.shiro.SecurityUtils
import vertxl.VertX
import java.time.LocalDateTime

class LoginStep1 : EndPoint() {
    // WARN: TwoFactorAuthRecover follows this same path
    override suspend fun doAction(gson: Gson, obj: JsonObject, endPointReply: EndPointReply, utcTime: LocalDateTime) {
        val subject            = SecurityUtils.getSubject()
        val reference          = subject.session.getAttribute("prelogin")?.toString() ?: return
        val validationCode     = obj.s("code") ?: return
        if (!functions.isNumeric(validationCode)) { return }

        // verify the code
        val o = JsonObject();   o.addProperty("reference", reference)

        val epMyInfo = VertX.awaitFor("members.get-two-factor-key", o)
        if (!epMyInfo.success) return

        val secretKey = epMyInfo.data["key"].asString
        if(!GoogleAuthenticator().authorize(secretKey, validationCode.toInt())) {
            return
        }

        obj.addProperty("rememberMe", false)
        LoginStep2().doAction(gson, obj, endPointReply, utcTime)
    }
}