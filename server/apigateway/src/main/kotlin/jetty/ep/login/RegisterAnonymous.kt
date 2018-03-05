package jetty.ep.login

import com.google.gson.Gson
import com.google.gson.JsonObject
import common.Google
import common.functions
import common.functions.bool
import common.functions.isValidEmail
import common.functions.s
import common.functions.validPassword
import entities.Member_V1
import jetty.EndPoint
import objects.EndPointReply
import objects.SYError
import org.apache.shiro.SecurityUtils
import shiro.Shiro
import vertxl.VertX
import java.time.LocalDateTime

class RegisterAnonymous : EndPoint() {
    override suspend fun doAction(gson: Gson, obj: JsonObject, endPointReply: EndPointReply, utcTime: LocalDateTime) {
        val subject = SecurityUtils.getSubject()
        val expectedToken       = obj.s("expected_token")  ?: return
        val captchaPreChecked   = obj.bool("captchaPreChecked") // this is for auto login
        val captcha             = obj.s("captcha") ?: ""
        val serverExpectedToken = subject.session.getAttribute("expected_token")  == expectedToken

        if (    (!captchaPreChecked && !Google.checkCaptcha(captcha))
                || Shiro.hasUserCalledServerVeryRecently(subject)
                || !serverExpectedToken
        ) return

        val member = Member_V1()
        member.anonymousAccount = true
        member.emailConfirmed = true
        member.anonymousPassword = functions.aRandomString
        member.email = "${functions.aRandomString}@${Shiro.domain}"
        member.password = Shiro.encryptPassword(member.anonymousPassword)
        val epRegister = VertX.awaitFor("members.register-anonymous", member.toJsonObject())

        val me = epRegister.data
        endPointReply.data.add("me", me)

        subject.session.setAttribute("prelogin", me["reference"].asString)

        val autoLoginObj = JsonObject()
        autoLoginObj.addProperty("expected_token", expectedToken)
        autoLoginObj.addProperty("rememberMe", true)

        LoginStep2().doAction(gson, autoLoginObj, endPointReply, utcTime)

    }
}