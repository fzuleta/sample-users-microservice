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
import shiro.Shiro
import vertxl.VertX
import java.time.LocalDateTime

class send_me_my_info : EndPoint() {
    override suspend fun doAction(gson: Gson, obj: JsonObject, endPointReply: EndPointReply, utcTime: LocalDateTime) {
        val expectedToken  = obj.s("expected_token")  ?: return
        val subject = SecurityUtils.getSubject()
        val serverExpectedToken = subject.session.getAttribute("expected_token") == expectedToken

        val reference = Shiro.get_reference(subject) ?: return
        val email               = obj.s("email")?.toLowerCase() ?: return
        val captcha             = obj.s("captcha") ?: ""

        if (   !functions.isValidEmail(email)
            || !serverExpectedToken
            || !Google.checkCaptcha(captcha)
        ) { return }

        val uClient = JsonObject()
        uClient.addProperty("reference", reference)
        val epMyInfo = VertX.awaitFor("members.get-my-privates", uClient)

        endPointReply.success = true

        epMyInfo.data.addProperty("emailTo", email)
        emails().`send my private anonymous info`(epMyInfo.data)

    }
}