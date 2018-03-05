import jetty.Jetty
import org.apache.commons.io.FileUtils
import java.io.File

fun main(args: Array<String>) {
    val LOCAL_FOLDER_LOCATION = FileUtils.getFile("").absolutePath.split("server")[0]

    println("LOCAL_FOLDER_LOCATION: $LOCAL_FOLDER_LOCATION")
    val ip:String                     = "0.0.0.0"
    val httpPort:Int                  = 8880
    val httpsPort:Int                 = 8443
    val sslEnabled:Boolean            = true
    val sslKeyStorePath:String        = System.getenv("SSLPATH") ?: LOCAL_FOLDER_LOCATION + "resources/self.keystore"
    val sslKeyStorePassword:String    = System.getenv("SSLPASS") ?: "qweqwe"
    val sslKeyManagerPassword:String  = System.getenv("SSLPASS") ?: "qweqwe"
    val sslTrustStorePath:String      = System.getenv("SSLPATH") ?: LOCAL_FOLDER_LOCATION + "resources/self.keystore"
    val sslTrustStorePassword:String  = System.getenv("SSLPASS") ?: "qweqwe"
    val staticFolderLocation:String   = System.getenv("AURELIALOCATION") ?: "$LOCAL_FOLDER_LOCATION/client"

    // Jetty
    Jetty.start(
            ip,
            httpPort,
            httpsPort,
            sslEnabled,
            sslKeyStorePath,
            sslKeyStorePassword,
            sslKeyManagerPassword,
            sslTrustStorePath,
            sslTrustStorePassword,
            staticFolderLocation
    )
}

