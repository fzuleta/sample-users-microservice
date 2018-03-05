package jetty.ep

import com.google.gson.Gson
import com.google.gson.JsonObject
import common.Constants
import common.Google
import jetty.EndPoint
import objects.EndPointReply
import org.apache.shiro.SecurityUtils
import shiro.Shiro
import vertxl.VertX
import java.time.LocalDateTime

class refresh : EndPoint() {
    override suspend fun doAction(gson: Gson, obj: JsonObject, endPointReply: EndPointReply, utcTime: LocalDateTime) {
        val subject = SecurityUtils.getSubject()
        val expectedToken   = Shiro.getExpectedToken(subject)

        endPointReply.data.addProperty("isRemembered", subject.isRemembered)
        endPointReply.data.addProperty("isAuthenticated", subject.isAuthenticated)
        endPointReply.data.add("validCurrencies", Constants.valid_currencies)

        val loginInfo = JsonObject()
        loginInfo.addProperty("clientURL", Constants.clientURL + (if (Constants.useHashOnClientURL) "/#" else ""))
        loginInfo.addProperty("expected_token", expectedToken)
        loginInfo.addProperty("captchaEnabled", Google.captchaEnabled)
        endPointReply.data.add("login", loginInfo)

        val reference = Shiro.get_reference(subject)
        if (reference!=null) {
            val uClient = JsonObject()
            uClient.addProperty("reference", reference)

            // get my info
            val epMyInfo = VertX.awaitFor("members.get", uClient)
            endPointReply.data.add("me", epMyInfo.data)

        }

        endPointReply.success = true
    }
}