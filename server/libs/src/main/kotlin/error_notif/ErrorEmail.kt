package error_notif

import com.google.gson.JsonObject
import vertxl.VertX
import java.time.LocalDateTime
import java.time.ZoneOffset

object ErrorEmail {
    val serviceLast = mutableMapOf<String, Long>()
    val timeStart = LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC)

    fun `send error Email`(service: String, endpoint: String, error: String) {
        val now = LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC)
        val fiveM = 300000L // 5 minutes
        val tenM  = 900000L // 15 minutes
        val timeAlive = now - timeStart
        val last = serviceLast[service]

        if (timeAlive < fiveM) return
        if (last != null && now - last < tenM) return // Allow a message every 15 mins

        serviceLast[service] = now

        val newMessage = JsonObject()
        newMessage.addProperty("service", service)
        newMessage.addProperty("endpoint", endpoint)
        newMessage.addProperty("error", error)

        val oEmail = JsonObject()
        oEmail.addProperty("email", "feli@felipezuleta.com")
        oEmail.addProperty("subject", "SIMPLE EMAIL: ERROR!")
        oEmail.addProperty("template", "platform-error")
        oEmail.add("newMessage", newMessage)

        VertX.send("contact.send-email", oEmail.toString())
    }
}