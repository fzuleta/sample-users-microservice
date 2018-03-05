package jetty

import jetty.socket.Socket
import org.eclipse.jetty.servlet.ServletContextHandler


object Jetty {
    class Service: JettyService(){
        override fun addServlets(s: ServletContextHandler) {
            val list = hashMapOf(
                "/socket"                               to Socket::class.java,

                "/api/refresh/"                         to jetty.ep.refresh::class.java,

                "/api/member/register/"                 to jetty.ep.login.Register::class.java,
                "/api/member/login/"                    to jetty.ep.login.LoginStep0::class.java,
                "/api/member/login-2fa/"                to jetty.ep.login.LoginStep1::class.java,
                "/api/member/logout/"                   to jetty.ep.login.Logout::class.java,
                "/api/member/update-password/"          to jetty.ep.login.UpdatePassword::class.java,
                "/api/member/forgot-password/"          to jetty.ep.login.ForgotPassword::class.java,
                "/api/member/forgot-password-recover/"  to jetty.ep.login.ForgotPasswordRecover::class.java,
                "/api/member/confirm-email/"            to jetty.ep.login.ConfirmEmailStep::class.java,
                "/api/member/confirm-email-send-again/" to jetty.ep.login.ConfirmEmailSendAgain::class.java,
                "/api/member/two-factor-recover/"       to jetty.ep.login.TwoFactorAuthRecover::class.java,
                "/api/member/two-factor-enable-0/"      to jetty.ep.login.TwoFactorAuthEnableStep0::class.java,
                "/api/member/two-factor-enable-1/"      to jetty.ep.login.TwoFactorAuthEnableStep1::class.java,
                "/api/member/two-factor-enable-2/"      to jetty.ep.login.TwoFactorAuthEnableStep2::class.java,
                "/api/member/two-factor-disable-0/"     to jetty.ep.login.TwoFactorAuthDisableStep0::class.java,
                "/api/member/two-factor-disable-1/"     to jetty.ep.login.TwoFactorAuthDisableStep1::class.java,
                "/api/member/register-anonymous/"       to jetty.ep.login.RegisterAnonymous::class.java,
                "/api/anonymous/send-me-my-info/"       to jetty.ep.anonymous.send_me_my_info::class.java,
                "/api/anonymous/upgrade-to-email/"      to jetty.ep.anonymous.upgrade_to_email_account::class.java
            )
            for ((a, b) in list) s.addServlet(b, a)
        }
    }

    val j = Service()

    fun start(
              ip:String,
              httpPort:Int,
              httpsPort:Int,
              cors_allowedOrigins:String,
              sslEnabled:Boolean,
              sslKeyStorePath:String,
              sslKeyStorePassword:String,
              sslKeyManagerPassword:String,
              sslTrustStorePath:String,
              sslTrustStorePassword:String

    ) {
        j.ip = ip
        j.httpPort = httpPort
        j.httpsPort = httpsPort
        j.sslEnabled = sslEnabled
        j.sslKeyStorePath = sslKeyStorePath
        j.sslKeyStorePassword = sslKeyStorePassword
        j.sslKeyManagerPassword = sslKeyManagerPassword
        j.sslTrustStorePath = sslTrustStorePath
        j.sslTrustStorePassword = sslTrustStorePassword
        j.cors_allowedOrigins = cors_allowedOrigins

        j.startAsync()
    }

}