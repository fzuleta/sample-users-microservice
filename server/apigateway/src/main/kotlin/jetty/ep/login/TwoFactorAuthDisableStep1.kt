package jetty.ep.login

import com.google.gson.Gson
import com.google.gson.JsonObject
import common.StringUtils
import common.functions.s
import jetty.EndPoint
import objects.EndPointReply
import vertxl.VertX
import java.net.URLDecoder
import java.time.LocalDateTime

class TwoFactorAuthDisableStep1 : EndPoint() {
    override suspend fun doAction(gson: Gson, obj: JsonObject, endPointReply: EndPointReply, utcTime: LocalDateTime) {
        var reference   = obj.s("code") ?: return
        reference = URLDecoder.decode(reference, "UTF-8")
        reference = StringUtils.simpleDecrypt(reference) ?: return

        val o = JsonObject()
        o.addProperty("reference", reference)
        val epActivate = VertX.awaitFor("members.deactivate-two-factor", o)

        endPointReply.success = epActivate.success
    }
}