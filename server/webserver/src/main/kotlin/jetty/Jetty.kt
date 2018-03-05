package jetty

object Jetty {

    val j = JettyService()

    fun start(
              ip:String                     = "0.0.0.0",
              httpPort:Int                  = 8089,
              httpsPort:Int                 = 8090,
              sslEnabled:Boolean            = false,
              sslKeyStorePath:String        = "",
              sslKeyStorePassword:String    = "",
              sslKeyManagerPassword:String  = "",
              sslTrustStorePath:String      = "",
              sslTrustStorePassword:String  = "",
              staticFolderLocation:String  = ""

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
        j.staticFolderLocation = staticFolderLocation

        j.startAsync()
    }

}