import org.apache.commons.io.FileUtils
import shiro.Shiro
import vert_x.vertx
import vertxl.VertX

var LOCAL_FOLDER_LOCATION = ""
fun main(args: Array<String>) {
    LOCAL_FOLDER_LOCATION = FileUtils.getFile("").absolutePath.split("server")[0]

    println("Hello")

    Shiro.hashService = System.getenv("HASHSERVICE") ?: "mhrgvxxxn777xvdkgy"
    Shiro.exists = true

    jed.urlGuest = System.getenv("JEDIS_URL_GUEST") ?: "localhost"
    jed.passGuest = System.getenv("JEDIS_PASS_GUEST") ?: "vivaredis123HHbhchgfg9"
    jed.portGuest = System.getenv("JEDIS_PORT_GUEST") ?: "16444"
    jed.urlUser = System.getenv("JEDIS_URL_USER") ?: "localhost"
    jed.passUser = System.getenv("JEDIS_PASS_USER") ?: "vivaredis123HHbhchgfg9"
    jed.portUser = System.getenv("JEDIS_PORT_USER") ?: "16444"
    jed.init {
        restoreState {
            loadVertx()
        }
    }
}
fun restoreState(callback: () -> Unit) {


    println("State restored")

    callback()
}
fun loadVertx() {
    val vertxHost: String       = System.getenv("VERTX_HOST")  ?: "localhost"
    val vertxEnv: String        = System.getenv("VERTX_ENV")   ?: "local"

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

    VertX.initCallback = {
        vertx.deployVerticals()
    }
    vertx.start()

}
