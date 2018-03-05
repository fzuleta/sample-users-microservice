package email

import org.apache.velocity.app.VelocityEngine
import org.apache.velocity.runtime.RuntimeConstants
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.ui.velocity.VelocityEngineUtils
import javax.mail.Authenticator
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

object Email {
    var primaryEmail_user = ""
    var primaryEmail_pass = ""
    var server = ""
    var port = "465" // or 587

    suspend fun sendHTMLEmail(subject: String, to: String, template: String, hTemplateVariables: MutableMap<String, Any>): Boolean {
        var sent = false
        try {
            // Step 1
            val props = System.getProperties()

            props.put("mail.smtp.from", primaryEmail_user)
            props.put("mail.smtp.user", primaryEmail_user)
            props.put("mail.smtp.password", primaryEmail_pass.toCharArray())
            props.put("mail.smtp.host", server)
            props.put("mail.smtp.port", port)
            props.put("mail.smtp.auth", "true")
            props.put("mail.smtp.starttls.enable", "true")
//            props.put("mail.smtp.debug", "true")
            props.put("mail.smtp.socketFactory.port", port)
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
            props.put("mail.smtp.socketFactory.fallback", "false")

            val session = Session.getInstance(props, SMTPAuthenticator())
//            session.debug = true

            val message = MimeMessage(session)
            message.subject = subject

            val helper: MimeMessageHelper
            helper = MimeMessageHelper(message, true)
            helper.setFrom(InternetAddress(primaryEmail_user))
            helper.setTo(InternetAddress(to))

            val velocityEngine = VelocityEngine()
            velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath")
            velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader::class.java.name)

            velocityEngine.init()

            val text = VelocityEngineUtils.mergeTemplateIntoString(
                    velocityEngine,
                    "email/template-$template.html",
                    hTemplateVariables)

            helper.setText(text, true)


            val transport = session.getTransport("smtps")

            transport.connect(
                    Email.server,
                    port.toInt(),
                    Email.primaryEmail_user,
                    Email.primaryEmail_pass
            )

            transport.sendMessage(message, message.allRecipients)
            transport.close()

            sent = true
        } catch (ex: Exception) {
            ex.printStackTrace()

            // TODO Notify an admin
        }

        return sent
    }

    class SMTPAuthenticator : Authenticator() {
        override fun getPasswordAuthentication(): PasswordAuthentication
            = PasswordAuthentication(primaryEmail_user, primaryEmail_pass)

    }
}
