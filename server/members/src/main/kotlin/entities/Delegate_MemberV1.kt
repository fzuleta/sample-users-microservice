package entities

import com.google.gson.JsonObject
import common.functions
import shiro.Shiro
import java.util.*

data class Delegate_MemberV1(val member: Member_V1) {
    var reference
        get() = member.reference
        set(s) { member.reference = s}
    var password
        get() = member.password
        set(p:String) { member.password = p }
    var email get() =  member.email
        set(e) { member.email = e}

    val firstName get() = member.firstName
    val lastName get() = member.lastName
    val twoFactorSecretKey get() = member.twoFactorSecretKey
    val twofactorEnabled get() = member.twofactorEnabled
    val twoFactorRecoveryCode get() = member.twoFactorRecoveryCode
    val emailConfirmed get() = member.emailConfirmed
    val emailConfirmedCode get() = member.emailConfirmedCode
    var dateLastLogin
        get() = member.dateLastLogin
        set(p) { member.dateLastLogin = p }

    var anonymousAccount
        get() = member.anonymousAccount
        set(v) { member.anonymousAccount = v}
    var anonymousPassword
        get() = member.anonymousPassword
        set(v) { member.anonymousPassword = v}



    fun setTwoFactor(key:String): Boolean {
        if (member.twofactorEnabled) return false
        member.twoFactorSecretKey = key
        member.twoFactorRecoveryCode = functions.generateString(Random(), "QWERTYUIOPASDFGHJKLZXCVBNM1234567890", 12)
        return true
    }
    fun activateTwoFactor() {
        member.twofactorEnabled = true
    }
    fun deActivateTwoFactor() {
        member.twofactorEnabled = false
    }
    fun confirmEmail() {
        member.emailConfirmed = true
        member.emailConfirmedCode = 0
    }
    fun setRecoverPassword(password: String) {
        member.password = Shiro.encryptPassword(password)
    }


    // -------
    override fun toString(): String = member.toString()
    fun toJsonObject(): JsonObject = member.toJsonObject()
    fun toUser(): JsonObject = cleanUp(toJsonObject())

    companion object {
        fun cleanUp(o:JsonObject):JsonObject {
            o.remove("password")
            o.remove("twoFactorSecretKey")
            o.remove("twoFactorRecoveryCode")
            o.remove("currencies")
            o.remove("totalHistory")
            return o
        }
    }
}