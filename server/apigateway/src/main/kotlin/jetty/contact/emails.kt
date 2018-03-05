package jetty.contact

import com.google.gson.JsonObject
import common.Constants
import common.StringUtils
import vertxl.VertX
import java.net.URLEncoder

class emails {
    private var hash = ""
    init {
        hash = if (Constants.useHashOnClientURL) "#/" else ""
    }
    fun `send registration - confirm pin email`(data: JsonObject) {
        val newMessage = JsonObject()
        val reference = data["reference"].asString
        val emailConfirmedCode = data["emailConfirmedCode"].asString
        val code = "$reference@@@$emailConfirmedCode"
        val firstName = data["firstName"].asString
        val lastName = data["lastName"].asString
        val email = data["email"].asString

        val encoded = URLEncoder.encode(StringUtils.simpleEncrypt(code), "UTF-8")

        newMessage.addProperty("name", "$firstName $lastName")
        newMessage.addProperty("link", "${Constants.clientURL}/${hash}confirm-email/$encoded")

        val oEmail = JsonObject()
        oEmail.addProperty("email", email)
        oEmail.addProperty("subject", "SAMPLEEMAIL:  Please Confirm Email")
        oEmail.addProperty("template", "confirmemail")
        oEmail.add("newMessage", newMessage)

//        println("registration sending email")
        VertX.send("contact.send-email", oEmail.toString())
    }
    fun `send forgot password email`(data: JsonObject) {
        val newMessage = JsonObject()
        val reference = data["reference"].asString
        val emailConfirmedCode = data["emailConfirmedCode"].asString
        val firstName = data["firstName"].asString
        val lastName = data["lastName"].asString
        val email = data["email"].asString
        val encryptedCode = StringUtils.simpleEncrypt(reference)
        val encoded = URLEncoder.encode(encryptedCode, "UTF-8")

        newMessage.addProperty("name", "$firstName $lastName")
        newMessage.addProperty("link", "${Constants.clientURL}/${hash}reset-password/$encoded")

        val oEmail = JsonObject()
        oEmail.addProperty("email", email)
        oEmail.addProperty("subject", "SAMPLEEMAIL: Recover your password")
        oEmail.addProperty("template", "forgotpassword")
        oEmail.add("newMessage", newMessage)

//        println("registration sending email")
        VertX.send("contact.send-email", oEmail.toString())
    }

    fun `send password recovered email`(data: JsonObject) {
        val newMessage = JsonObject()
        val firstName = data["firstName"].asString
        val lastName = data["lastName"].asString
        val email = data["email"].asString

        newMessage.addProperty("name", "$firstName $lastName")

        val oEmail = JsonObject()
        oEmail.addProperty("email", email)
        oEmail.addProperty("subject", "SAMPLEEMAIL: Password has been reset")
        oEmail.addProperty("template", "passwordreset")
        oEmail.add("newMessage", newMessage)

//        println("registration sending email")
        VertX.send("contact.send-email", oEmail.toString())
    }
    fun `two-factor-password used`(data: JsonObject) {
        val newMessage = JsonObject()
        val firstName = data["firstName"]?.asString
        val lastName = data["lastName"]?.asString
        val email = data["email"].asString ?: return

        newMessage.addProperty("name", "$firstName $lastName")

        val oEmail = JsonObject()
        oEmail.addProperty("email", email)
        oEmail.addProperty("subject", "SAMPLEEMAIL: 2FA - Recovery password used")
        oEmail.addProperty("template", "two-factor-pass-used")
        oEmail.add("newMessage", newMessage)

        VertX.send("contact.send-email", oEmail.toString())
    }
    fun `send disable two factor auth confirmation email`(data: JsonObject) {

        val newMessage = JsonObject()
        val encoded = URLEncoder.encode(StringUtils.simpleEncrypt(data["reference"].asString), "UTF-8")
        newMessage.addProperty("name", data["firstName"].asString + " " + data["lastName"].asString)
        newMessage.addProperty("link", "${Constants.clientURL}/${hash}two-factor-disable-cmd/$encoded")

        val oEmail = JsonObject()
        oEmail.addProperty("email", data["email"].asString)
        oEmail.addProperty("subject", "SAMPLEEMAIL: Disable 2 Factor Authentication")
        oEmail.addProperty("template", "two-factor-disable")
        oEmail.add("newMessage", newMessage)

        VertX.send("contact.send-email", oEmail.toString())
    }

    fun `send my private anonymous info`(data: JsonObject) {
        val username = data["email"].asString
        val emailTo = data["emailTo"].asString
        val anonymousPassword = data["anonymousPassword"].asString

        val newMessage = JsonObject()
        newMessage.addProperty("username", username)
        newMessage.addProperty("anonymousPassword", anonymousPassword)


        val oEmail = JsonObject()
        oEmail.addProperty("email", emailTo)
        oEmail.addProperty("subject", "SAMPLEEMAIL: Guest information")
        oEmail.addProperty("template", "anonymous-privates")
        oEmail.add("newMessage", newMessage)

        VertX.send("contact.send-email", oEmail.toString())
    }
    fun `send anonymous upgraded`(email: String) {
        val newMessage = JsonObject()
        newMessage.addProperty("email", email)


        val oEmail = JsonObject()
        oEmail.addProperty("email", email)
        oEmail.addProperty("subject", "SAMPLEEMAIL: Account upgraded")
        oEmail.addProperty("template", "anonymous-account-upgraded")
        oEmail.add("newMessage", newMessage)

        VertX.send("contact.send-email", oEmail.toString())
    }
}