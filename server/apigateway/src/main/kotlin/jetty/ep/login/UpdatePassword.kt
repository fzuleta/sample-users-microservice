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

class UpdatePassword : EndPoint() {
    override suspend fun doAction(gson: Gson, obj: JsonObject, endPointReply: EndPointReply, utcTime: LocalDateTime) {
        println(obj)
        val expectedToken   = obj.s("expected_token")  ?: return
        val subject = SecurityUtils.getSubject()
        val serverExpectedToken = subject.session.getAttribute("expected_token")  == expectedToken
        val reference           = Shiro.get_reference(subject) ?: return

        val oldPassword    = obj.s("oldPassword")  ?: return
        val newPassword0   = obj.s("newPassword0")  ?: return
        val newPassword1   = obj.s("newPassword1")  ?: return

        if (!serverExpectedToken
                || !functions.validPassword(newPassword0, newPassword1)) { return }

        // time constrain the email sending
        if (Shiro.hasUserCalledServerVeryRecently(subject)) {
            endPointReply.data.addProperty("tooSoon", true)
            return
        }

        val o = JsonObject() ;
        o.addProperty("reference", reference)
        o.addProperty("oldPassword", oldPassword)
        o.addProperty("newPassword", Shiro.encryptPassword(newPassword0))

        val epGet = VertX.awaitFor("members.update-password", o)
        if (!epGet.success) return

        emails().`send password recovered email`(epGet.data)

        endPointReply.success = true
    }
}