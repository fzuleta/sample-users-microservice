package jetty.ep.login

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.warrenstrange.googleauth.GoogleAuthenticator
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator
import common.Constants
import common.functions.s
import jetty.EndPoint
import objects.EndPointReply
import org.apache.shiro.SecurityUtils
import shiro.Shiro
import vertxl.VertX
import java.time.LocalDateTime

class TwoFactorAuthEnableStep0: EndPoint() {
    override suspend fun doAction(gson: Gson, obj: JsonObject, endPointReply: EndPointReply, utcTime: LocalDateTime) {
        val subject = SecurityUtils.getSubject()

        val expectedToken       = obj.s("expected_token") ?: return
        val reference           = Shiro.get_reference(subject) ?: return

        if (expectedToken != subject.session.getAttribute("expected_token")) { return }

        val gAuth = GoogleAuthenticator()
        val key = gAuth.createCredentials()

        val o = JsonObject()
        o.addProperty("reference", reference)
        o.addProperty("key", key.key)
        val epMyInfo = VertX.awaitFor("members.set-two-factor-key", o)
        if (!epMyInfo.success) return

        endPointReply.data.addProperty("secretKey", key.key)
        endPointReply.data.addProperty("recoveryCode", epMyInfo.data["recoveryCode"].asString)
        endPointReply.data.addProperty("qr", GoogleAuthenticatorQRGenerator.getOtpAuthURL(Constants.companyName, reference, key))
        endPointReply.success = true
    }
}