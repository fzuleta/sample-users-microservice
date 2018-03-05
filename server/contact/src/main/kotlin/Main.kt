import common.Constants
import email.Email
import org.apache.commons.io.FileUtils
import scheduler.EmailQueue
import shiro.Shiro
import vert_x.vertx
import vertxl.VertX


fun main(args: Array<String>) {
    val LOCAL_FOLDER_LOCATION = FileUtils.getFile("").absolutePath.split("server")[0]

    Constants.companyName             = System.getenv("COMPANYNAME") ?: "Sample Login"
    Constants.clientURL               = System.getenv("CLIENT_URL") ?:  "https://usermicroservice.com:8443"
    Constants.useHash                 = if (System.getenv("CLIENT_USE_HASH") != null) System.getenv("CLIENT_USE_HASH").toBoolean() else true

    Email.port                        = System.getenv("EMAIL_PORT") ?: "465"
    Email.primaryEmail_user           = System.getenv("EMAIL_USER") ?: "samplelogin16@gmail.com"
    Email.primaryEmail_pass           = System.getenv("EMAIL_PASSWORD") ?: "qweqwe123123"
    Email.server                      = System.getenv("EMAIL_SERVER") ?: "smtp.gmail.com"

    val vertxHost: String       = System.getenv("VERTX_HOST")  ?: "localhost"
    val vertxEnv: String        = System.getenv("VERTX_ENV")  ?: "local"

    val sslEnabled                    = true
    val sslKeyStorePath:String        = System.getenv("SSLPATH") ?: LOCAL_FOLDER_LOCATION + "resources/self.keystore"
    val sslKeyStorePassword:String    = System.getenv("SSLPASS") ?: "qweqwe"
    val sslTrustStorePath:String      = System.getenv("SSLPATH") ?: LOCAL_FOLDER_LOCATION + "resources/self.keystore"
    val sslTrustStorePassword:String  = System.getenv("SSLPASS") ?: "qweqwe"

    VertX.host = vertxHost
    VertX.env = vertxEnv
    VertX.sslEnabled = sslEnabled
    VertX.keystore = sslKeyStorePath
    VertX.keystorePassword = sslKeyStorePassword
    VertX.truststore = sslTrustStorePath
    VertX.truststorePassword = sslTrustStorePassword
    VertX.clusterName = System.getenv("VERTX_CLUSTER_NAME") ?: "mysuperawesomecluster"
    VertX.clusterPassword = System.getenv("VERTX_CLUSTER_PASSWORD") ?: "KGiy7iuhkBHVjhgv"

    vertx.start()

    println("exists")
    Shiro.exists = true
}