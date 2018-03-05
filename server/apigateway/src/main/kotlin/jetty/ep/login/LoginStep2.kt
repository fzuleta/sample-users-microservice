package jetty.ep.login

import com.google.gson.Gson
import com.google.gson.JsonObject
import common.functions.bool
import common.functions.s
import jetty.EndPoint
import objects.EndPointReply
import objects.SYError
import org.apache.shiro.SecurityUtils
import org.apache.shiro.authc.*
import shiro.Shiro
import java.time.LocalDateTime

class LoginStep2 : EndPoint() {
    override suspend fun doAction(gson: Gson, obj: JsonObject, endPointReply: EndPointReply, utcTime: LocalDateTime) {

        endPointReply.success = false

        val subject             = SecurityUtils.getSubject()
        val reference           = subject.session.getAttribute("prelogin")?.toString() ?: return
        val rememberMe          = obj.bool("rememberMe")
        val expectedToken       = obj.s("expected_token") ?: return
        val bypassExpectedToken = obj.bool("bypassExpectedToken")

        // Cleanup
        subject.session.removeAttribute("prelogin")

        // input check 
        if (!bypassExpectedToken && expectedToken != Shiro.getExpectedToken(subject)) { throw Exception() }

        try {
            val token = UsernamePasswordToken(reference, reference)
            token.isRememberMe = rememberMe

            subject.login(token)
            endPointReply.success = true

        } catch ( uae: UnknownAccountException) {
            endPointReply.errorCode = 5
        } catch ( ice: IncorrectCredentialsException) {
            endPointReply.errorCode = 6
        } catch ( lae:LockedAccountException) {
            endPointReply.errorCode = 7
        } catch ( ea:ExcessiveAttemptsException) {
            endPointReply.errorCode = 8
        } catch ( ae:AuthenticationException) {
            endPointReply.errorCode = 9
        } catch (e: Exception) {
            endPointReply.error = SYError.Error.SERVER_COMMUNICATION_ERROR
        }
    }
}