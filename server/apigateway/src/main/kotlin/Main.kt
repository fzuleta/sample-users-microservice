import common.Constants
import common.Google
import common.StringUtils
import jetty.Jetty
import org.apache.commons.io.FileUtils
import shiro.Shiro
import vertxl.VertX
import java.io.File


fun main(args: Array<String>) {
    val LOCAL_FOLDER_LOCATION = FileUtils.getFile("").absolutePath.split("server")[0]

    Constants.companyName             = System.getenv("COMPANYNAME") ?: "Sample Login"
    Constants.clientURL               = System.getenv("CLIENT_URL") ?:  "https://usermicroservice.com:8443"
    Constants.useHashOnClientURL      = if (System.getenv("CLIENT_USE_HASH") != null) System.getenv("CLIENT_USE_HASH").toBoolean() else true

    val recaptchaKey                  = System.getenv("CAPTCHA_SERVER_KEY") ?: "some-key"
    val captchaEnabled                = System.getenv("CAPTCHA_ENABLED")?.toBoolean() ?: false

    val domain:String                 = System.getenv("DOMAIN") ?: "usermicroservice.com"
    val rememberMeCypher:String       = System.getenv("REMEMBERME") ?: "XgTdKp97Hgfv3rYh" // 16 characters
    val hashService:String            = System.getenv("HASHSERVICE") ?: "mhrgvxxxn777xvdkgy"
    val sessionDuration:String        = System.getenv("SESSIONDURATION") ?: "3600000"

    val ip                            = "0.0.0.0"
    val httpPort                      = 11189
    val httpsPort                     = 11190
    val cors_allowedOrigins           = System.getenv("CORS") ?: Constants.clientURL //""*" //https://usermicroservice.com:8443

    val vertxHost: String             = System.getenv("VERTX_HOST")  ?: "localhost"
    val vertxEnv: String              = System.getenv("VERTX_ENV")  ?: "local"

    val sslEnabled:Boolean            = true
    val sslKeyStorePath:String        = System.getenv("SSLPATH") ?: LOCAL_FOLDER_LOCATION + "resources/self.keystore"
    val sslKeyStorePassword:String    = System.getenv("SSLPASS") ?: "qweqwe"
    val sslKeyManagerPassword:String  = System.getenv("SSLPASS") ?: "qweqwe"
    val sslTrustStorePath:String      = System.getenv("SSLPATH") ?: LOCAL_FOLDER_LOCATION + "resources/self.keystore"
    val sslTrustStorePassword:String  = System.getenv("SSLPASS") ?: "qweqwe"

    StringUtils.aes_key0              = System.getenv("AES_KEY_0") ?: "JUTRFGHytr7r6yghbjXuygtfg"
    StringUtils.aes_key1              = System.getenv("AES_KEY_1") ?: "HX8789u77TRrehhFFE%#"

    Google.recaptchaKey = recaptchaKey
    Google.captchaEnabled = captchaEnabled

    // Shiro
    Shiro.domain = domain
    Shiro.rememberMeCypher = rememberMeCypher
    Shiro.hashService = hashService
    Shiro.sessionDuration = sessionDuration.toLong()

    println(LOCAL_FOLDER_LOCATION)

    println("starting VERTX")
    VertX.host = vertxHost
    VertX.env = vertxEnv
    VertX.sslEnabled = true
    VertX.keystore = System.getenv("VERTX_SSLPATH") ?: LOCAL_FOLDER_LOCATION + "resources/self.keystore"
    VertX.keystorePassword = System.getenv("VERTX_SSLPASS") ?: "qweqwe"
    VertX.truststore = System.getenv("VERTX_SSLPATH") ?: LOCAL_FOLDER_LOCATION + "resources/self.keystore"
    VertX.truststorePassword = System.getenv("VERTX_SSLPASS") ?: "qweqwe"
    VertX.clusterName = System.getenv("VERTX_CLUSTER_NAME") ?: "mysuperawesomecluster"
    VertX.clusterPassword = System.getenv("VERTX_CLUSTER_PASSWORD") ?: "KGiy7iuhkBHVjhgv"
    VertX.start()

    println("exists")
    Shiro.exists = true

    // Jetty
    Jetty.start(
        ip,
        httpPort,
        httpsPort,
        cors_allowedOrigins,
        sslEnabled,
        sslKeyStorePath,
        sslKeyStorePassword,
        sslKeyManagerPassword,
        sslTrustStorePath,
        sslTrustStorePassword
    )

}