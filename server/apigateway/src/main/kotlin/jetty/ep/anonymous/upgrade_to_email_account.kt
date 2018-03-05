package jetty.ep.anonymous

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import common.Google
import common.functions
import common.functions.a
import common.functions.bd
import common.functions.bool
import common.functions.s
import jetty.EndPoint
import jetty.contact.emails
import objects.EndPointReply
import org.apache.shiro.SecurityUtils
import org.apache.shiro.authc.UsernamePasswordToken
import shiro.Shiro
import vertxl.VertX
import java.time.LocalDateTime

class upgrade_to_email_account : EndPoint() {
    override suspend fun doAction(gson: Gson, obj: JsonObject, endPointReply: EndPointReply, utcTime: LocalDateTime) {
        val expectedToken  = obj.s("expected_token")  ?: return
        val subject = SecurityUtils.getSubject()
        val serverExpectedToken = subject.session.getAttribute("expected_token") == expectedToken

        val reference           = Shiro.get_reference(subject) ?: return
        val email               = obj.s("email")?.toLowerCase() ?: return

        if (   !functions.isValidEmail(email)
            || !serverExpectedToken
        ) { return }

        val uClient = JsonObject()
        uClient.addProperty("reference", reference)
        uClient.addProperty("email", email)
        val epMyInfo = VertX.awaitFor("members.upgrade-to-email-account", uClient)

        if (epMyInfo.success) {
            val newReference = epMyInfo.data["reference"].asString
            subject.logout()
            val token = UsernamePasswordToken(newReference, newReference)
            token.isRememberMe = subject.isRemembered
            subject.login(token)

            emails().`send anonymous upgraded`(email)

            endPointReply.success = true
        }
    }
}