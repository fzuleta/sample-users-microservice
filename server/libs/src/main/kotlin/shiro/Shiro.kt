package shiro

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import common.functions
import org.apache.shiro.SecurityUtils
import org.apache.shiro.authc.credential.DefaultPasswordService
import org.apache.shiro.crypto.hash.DefaultHashService
import org.apache.shiro.crypto.hash.Sha256Hash
import org.apache.shiro.session.ExpiredSessionException
import org.apache.shiro.subject.Subject
import org.apache.shiro.util.SimpleByteSource
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset

object Shiro {
    var exists                  = false
    var domain                  = "localhost"
    var rememberMeCypher        = "" // 16 characters
    var hashService             = "LnnjousalfizuleiPsiyzzwpelvbnfpo"
    var sessionDuration:Long    = 3600000

    val hashIterations          = 2048
    private val passwordService: DefaultPasswordService by lazy {
        MyShiroConstructor.getPasswordService()
    }

    fun encryptPassword(plain:String) = passwordService.encryptPassword(plain)
    fun validatePassword(plain:String, encrypted:String)= passwordService.passwordsMatch(plain, encrypted)

    fun validateSession(s: Subject): Boolean {
        var b = false
        if (s.isAuthenticated || s.isRemembered) {
            s.session.touch()
            b = true
        }
        return b
    }

    fun touchSession(subject: Subject) {
        try {
            val session = subject.getSession(false)
            session.touch()
        } catch (e: Exception) {
        }
    }

    fun validateMe():String? {
        if(exists) {
            val subject = SecurityUtils.getSubject()
            return Shiro.get_reference(subject) ?: return null
        }
        return null
    }

    fun getExpectedToken(subject: Subject): String {
        var expectedToken = subject.session.getAttribute("expected_token")
        if (expectedToken == null) {
            expectedToken = functions.aUniqueReference
            subject.session.setAttribute("expected_token", expectedToken)
        }
        return expectedToken.toString()
    }

    fun get_reference(subject: Subject): String? {
        if (subject.principals != null)
            return subject.principals.primaryPrincipal.toString()
        return null
    }

    fun hasUserCalledServerVeryRecently(subject: Subject) : Boolean {
        val epoch = LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC)
        val lastCall = subject.session.getAttribute("lastAccessTime")?.toString()?.toLong() ?: 0L
        val diff = epoch - lastCall
        if (diff < 5) {
            return true
        }
        subject.session.setAttribute("lastAccessTime", epoch)
        return false
    }
}