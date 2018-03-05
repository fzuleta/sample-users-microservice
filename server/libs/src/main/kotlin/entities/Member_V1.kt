package entities

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import common.functions
import common.functions.bd
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneOffset


data class Member_V1(var reference:String = functions.aRandomString) {

    fun checkSize(v:String, s:Int=100):Boolean = v.length <= s

    var firstName:String? = null;           set(v){ if (v!=null && checkSize(v)) field=v }
    var lastName:String? = null;            set(v){ if (v!=null && checkSize(v)) field=v }

    var email:String? = null;               set(v){ if (v!=null && checkSize(v)) field=v.toLowerCase() }
    var emailConfirmed = false
    var emailConfirmedCode = functions.randInt(0, 10000)
    var password:String = ""

    val dateRegistration              = LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC)
    var dateLastLogin                 = LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC)

    var anonymousAccount = false
    var anonymousPassword = ""

    //2fa
    var twofactorEnabled = false
    var twoFactorSecretKey = ""
    var twoFactorRecoveryCode = ""


    override fun toString(): String = GsonBuilder().disableHtmlEscaping().create().toJson(this, this.javaClass)
    fun toJsonObject(): JsonObject = GsonBuilder().disableHtmlEscaping().serializeNulls().create().fromJson(toString(), JsonObject::class.java)

    companion object {
        fun fromString(s: String): Member_V1 {
            val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
            return gson.fromJson(s, Member_V1::class.java)
        }
    }
}