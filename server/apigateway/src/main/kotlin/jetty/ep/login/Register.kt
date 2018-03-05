package jetty.ep.login

import com.google.gson.Gson
import com.google.gson.JsonObject
import common.Google
import common.functions
import common.functions.bool
import common.functions.isValidCountry
import common.functions.s
import entities.Member_V1
import jetty.EndPoint
import jetty.contact.emails
import objects.EndPointReply
import objects.SYError
import org.apache.shiro.SecurityUtils
import shiro.Shiro
import vertxl.VertX
import java.time.LocalDateTime
import java.time.ZoneOffset

class Register: EndPoint() {
    override suspend fun doAction(gson: Gson, obj: JsonObject, endPointReply: EndPointReply, utcTime: LocalDateTime) {
        val subject = SecurityUtils.getSubject()
        val reference   = Shiro.get_reference(subject)

        // If trying to register while logged in
        if (reference != null) return

        //Step 1
        val expectedToken  = obj.s("expected_token")  ?: return
        val email           = obj.s("email") ?: return
        val firstName       = obj.s("firstName") ?: return
        val lastName        = obj.s("lastName")  ?: return
        val password        = obj.s("password")  ?: "x"
        val password2       = obj.s("password2") ?:"y"
        val nationality     = obj.s("nationality") ?: return
        val residence       = obj.s("residence") ?: return

        //Step 3
        val approveTOS      = obj.bool("approve")
        val captcha         = obj.s("captcha") ?: "-"
        val rememberMe      = obj.bool("rememberMe")

        val isCaptchaValid  = Google.checkCaptcha(captcha)
        val serverExpectedToken = subject.session.getAttribute("expected_token")  == expectedToken

        //Do validation
        if (!approveTOS
                || !isCaptchaValid
                || !serverExpectedToken
                || !functions.isValidEmail(email)
                || !functions.validPassword(password, password2)
                || Shiro.hasUserCalledServerVeryRecently(subject)
                || email.length > 150
                || password.length > 100
//                || !isValidCountry(nationality)
//                || !isValidCountry(residence)
                ) {
            endPointReply.error = SYError.Error.GENERIC_ERROR
            return

        }

        val member = Member_V1()
        member.email        = email.toLowerCase()
        member.password     = Shiro.encryptPassword(password)
        member.firstName    = firstName
        member.lastName     = lastName


//        member.nationality  = functions.getValidCountryCode(nationality)!!
//        member.residence    = functions.getValidCountryCode(residence)!!

        val epRegister = VertX.awaitFor("members.register", member.toJsonObject())
        endPointReply.data      = epRegister.data
        endPointReply.error     = epRegister.error
        endPointReply.success   = epRegister.success

        if(!endPointReply.success) {
            endPointReply.error = SYError.Error.USER_EXISTS_OR_WRONG
            return
        }


        emails().`send registration - confirm pin email`(endPointReply.data)

    }
}
