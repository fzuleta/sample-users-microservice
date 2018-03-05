package jetty

import com.google.common.util.concurrent.AbstractService
import org.apache.shiro.web.servlet.ShiroFilter
import org.apache.shiro.web.servlet.ShiroHttpServletRequest
import org.eclipse.jetty.http.HttpVersion
import org.eclipse.jetty.jmx.MBeanContainer
import org.eclipse.jetty.server.*
import org.eclipse.jetty.server.handler.ContextHandlerCollection
import org.eclipse.jetty.server.handler.DefaultHandler
import org.eclipse.jetty.server.handler.HandlerCollection
import org.eclipse.jetty.server.handler.RequestLogHandler
import org.eclipse.jetty.server.handler.gzip.GzipHandler
import org.eclipse.jetty.servlet.FilterHolder
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlets.CrossOriginFilter
import org.eclipse.jetty.util.ssl.SslContextFactory
import org.eclipse.jetty.util.thread.QueuedThreadPool
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler
import shiro.ODBRealm
import shiro.SelfEnvironmentLoader

import javax.servlet.DispatcherType
import java.lang.management.ManagementFactory
import java.util.EnumSet

open class JettyService : AbstractService() {

    var server: Server? = null
    var cors_allowedOrigins = "*"//"http://localhost:9000";
    var cors_allowedHeaders = "*"
    var cors_allowedMethods = "GET, HEAD, OPTIONS, POST"

    var httpPort = 8089
    var httpsPort = 8090
    var ip = "0.0.0.0"
    var sslEnabled = false
    var sslKeyStorePath = ""
    var sslKeyStorePassword = ""
    var sslKeyManagerPassword = ""
    var sslTrustStorePath = ""
    var sslTrustStorePassword = ""

    override fun doStart() {
        try {
            println("Starting Jetty")
            // === jetty.xml ===
            // Setup Threadpool
            val threadPool = QueuedThreadPool()
            threadPool.maxThreads = 500

            // Server
            server = Server(threadPool)

            // Scheduler
            server!!.addBean(ScheduledExecutorScheduler())

            // HTTP Configuration
            val http_config = HttpConfiguration()
            http_config.sendServerVersion = false
            http_config.secureScheme = "https"
            http_config.securePort = httpsPort
            http_config.outputBufferSize = 32768

            // Handler Structure
            val handlers = HandlerCollection()
            val contexts = ContextHandlerCollection()
            handlers.handlers = arrayOf<Handler>(contexts, DefaultHandler())
            server!!.handler = handlers

            // Extra options
            server!!.isDumpAfterStart = false
            server!!.isDumpBeforeStop = false
            server!!.stopAtShutdown = true

            // === jetty-jmx.xml ===
            val mbContainer = MBeanContainer(ManagementFactory.getPlatformMBeanServer())
            server!!.addBean(mbContainer)

            // HTTP connector
            val http = ServerConnector(server, HttpConnectionFactory(http_config))
            http.host = ip
            http.port = httpPort
            http.idleTimeout = 30000

            // Set the connector
            server!!.addConnector(http)

            if (sslEnabled) {
                // === jetty-https.xml ===
                // SSL Context Factory
                val sslContextFactory = SslContextFactory()
                sslContextFactory.keyStorePath = sslKeyStorePath
                sslContextFactory.setKeyStorePassword(sslKeyStorePassword)
                sslContextFactory.setKeyManagerPassword(sslKeyManagerPassword)
                sslContextFactory.setTrustStorePath(sslTrustStorePath)
                sslContextFactory.setTrustStorePassword(sslTrustStorePassword)
                sslContextFactory.setExcludeCipherSuites("SSL_RSA_WITH_DES_CBC_SHA",
                        "SSL_DHE_RSA_WITH_DES_CBC_SHA", "SSL_DHE_DSS_WITH_DES_CBC_SHA",
                        "SSL_RSA_EXPORT_WITH_RC4_40_MD5",
                        "SSL_RSA_EXPORT_WITH_DES40_CBC_SHA",
                        "SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA",
                        "SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA");

                // SSL HTTP Configuration
                val https_config = HttpConfiguration(http_config)
                https_config.addCustomizer(SecureRequestCustomizer())

                // SSL Connector
                val sslConnector = ServerConnector(server,
                        SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()),
                        HttpConnectionFactory(https_config))
                sslConnector.host = ip
                sslConnector.port = httpsPort
                sslConnector.idleTimeout = 500000
                server!!.addConnector(sslConnector)

            }

            // === jetty-requestlog.xml ===
            val requestLog = NCSARequestLog()
            requestLog.filename = "./log/jetty_yyyy_mm_dd.request.log"
            requestLog.filenameDateFormat = "yyyy_MM_dd"
            requestLog.retainDays = 90
            requestLog.isAppend = true
            requestLog.isExtended = true
            requestLog.logCookies = false
            requestLog.logTimeZone = "GMT"
            val requestLogHandler = RequestLogHandler()
            requestLogHandler.requestLog = requestLog
            handlers.addHandler(requestLogHandler)

            //Servlet -----
            val secured = ServletContextHandler(ServletContextHandler.SESSIONS)
            secured.contextPath = "/"

            //Limit the max size for a request
            secured.maxFormContentSize = 5000000
            secured.maxFormKeys = 10

            val gzipHandler = GzipHandler()
            gzipHandler.setIncludedMimeTypes(
                    "text/html",
                    "text/plain",
                    "text/xml",
                    "text/css",
                    "application/json",
                    "application/javascript",
                    "application/json;charset=utf-8",
                    "text/javascript")

            secured.gzipHandler = gzipHandler

            // add servlets (used when extending this)
            addServlets(secured)

            // Enable CORS - cross origin resource sharing (for http and https)
            val cors = FilterHolder()
            cors.setInitParameter("allowedOrigins", cors_allowedOrigins)
            cors.setInitParameter("allowedHeaders", cors_allowedHeaders)
            cors.setInitParameter("allowedMethods", cors_allowedMethods)
            cors.filter = CrossOriginFilter()
            secured.addFilter(cors, "*", EnumSet.of(
                    DispatcherType.REQUEST,
                    DispatcherType.ASYNC,
                    DispatcherType.FORWARD,
                    DispatcherType.INCLUDE,
                    DispatcherType.ERROR
            ))


            // SHIRO ---
            val realm = ODBRealm()
            realm.isAuthenticationCachingEnabled = true
            secured.addEventListener(SelfEnvironmentLoader(realm))

            secured.addFilter(
                ShiroFilter::class.java,
                "/*",
                EnumSet.of(
                    DispatcherType.REQUEST,
                    DispatcherType.ASYNC,
                    DispatcherType.FORWARD,
                    DispatcherType.INCLUDE,
                    DispatcherType.ERROR
                ))

            contexts.addHandler(secured)
            // ------

            server!!.start()
            server!!.join()


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
    open fun addServlets(s: ServletContextHandler) {

    }

    override fun doStop() {
        try {
            server!!.stop()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
