package jetty.ep.login

import com.google.gson.Gson
import com.google.gson.JsonObject
import common.StringUtils
import jetty.EndPoint
import objects.EndPointReply
import java.time.LocalDateTime
import common.functions.s
import org.apache.shiro.SecurityUtils
import vertxl.VertX
import java.net.URLDecoder
import java.net.URLEncoder

class ConfirmEmailStep: EndPoint() {
    override suspend fun doAction(gson: Gson, obj: JsonObject, endPointReply: EndPointReply, utcTime: LocalDateTime) {
        var code = obj.s("code") ?: return
        code = URLDecoder.decode(code, "UTF-8")
        code = StringUtils.simpleDecrypt(code) ?: return
        val codeSplit = code.split("@@@")

        val subject = SecurityUtils.getSubject()

        val uClient = JsonObject()
        uClient.addProperty("reference", codeSplit[0])
        uClient.addProperty("emailConfirmedCode", codeSplit[1])

        val epRegister = VertX.awaitFor("members.confirm-email", uClient)
        if (!epRegister.success) return

        // Login automatically
        subject.session.setAttribute("prelogin", epRegister.data["reference"].asString)
        obj.addProperty("bypassExpectedToken", true)
        LoginStep2().doAction(gson, obj, endPointReply, utcTime)

        endPointReply.data.addProperty("firstTime", true)
        endPointReply.success   = true


    }
}