package jetty

import com.google.common.util.concurrent.AbstractService
import org.eclipse.jetty.http.HttpVersion
import org.eclipse.jetty.jmx.MBeanContainer
import org.eclipse.jetty.rewrite.handler.RedirectPatternRule
import org.eclipse.jetty.rewrite.handler.RewriteHandler
import org.eclipse.jetty.rewrite.handler.RewritePatternRule
import org.eclipse.jetty.security.ConstraintMapping
import org.eclipse.jetty.security.ConstraintSecurityHandler
import org.eclipse.jetty.server.*
import org.eclipse.jetty.server.handler.ContextHandlerCollection
import org.eclipse.jetty.server.handler.DefaultHandler
import org.eclipse.jetty.server.handler.HandlerCollection
import org.eclipse.jetty.server.handler.RequestLogHandler
import org.eclipse.jetty.server.handler.gzip.GzipHandler
import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.servlet.ErrorPageErrorHandler
import org.eclipse.jetty.util.resource.Resource
import org.eclipse.jetty.util.security.Constraint
import org.eclipse.jetty.util.ssl.SslContextFactory
import org.eclipse.jetty.util.thread.QueuedThreadPool
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler
import org.eclipse.jetty.webapp.WebAppContext

import java.io.File
import java.lang.management.ManagementFactory


class JettyService : AbstractService() {
    var server: Server? = null
    val cors_allowedOrigins = "*"//"http://localhost:9000";
    val cors_allowedHeaders = "*"
    val cors_allowedMethods = "GET, HEAD, OPTIONS, POST"

    var httpPort = 8089
    var httpsPort = 8090
    var ip = "0.0.0.0"
    var sslEnabled = false
    var sslKeyStorePath = ""
    var sslKeyStorePassword = ""
    var sslKeyManagerPassword = ""
    var sslTrustStorePath = ""
    var sslTrustStorePassword = ""

    var staticFolderLocation = ""



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
            http_config.addCustomizer(SecureRequestCustomizer())
            http_config.sendServerVersion = false
            http_config.secureScheme = "https"
            http_config.securePort = httpsPort
            http_config.outputBufferSize = 32768
            println("setting https: " + httpsPort)

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
            println("setting http: " + httpPort)

            // Set the connector
            server!!.addConnector(http)

            if (sslEnabled.equals(true)) {
                // === jetty-https.xml ===
                // SSL Context Factory
                val sslContextFactory = SslContextFactory()
                sslContextFactory.keyStorePath = sslKeyStorePath
                sslContextFactory.setKeyStorePassword(sslKeyStorePassword)
                sslContextFactory.setKeyManagerPassword(sslKeyManagerPassword)
                sslContextFactory.setTrustStorePath(sslTrustStorePath)
                sslContextFactory.setTrustStorePassword(sslTrustStorePassword)
                //                sslContextFactory.setExcludeCipherSuites("SSL_RSA_WITH_DES_CBC_SHA",
                //                        "SSL_DHE_RSA_WITH_DES_CBC_SHA", "SSL_DHE_DSS_WITH_DES_CBC_SHA",
                //                        "SSL_RSA_EXPORT_WITH_RC4_40_MD5",
                //                        "SSL_RSA_EXPORT_WITH_DES40_CBC_SHA",
                //                        "SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA",
                //                        "SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA");

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


            // ====================================================================================
            // ONLY SERVE VIA HTTPS
            //setup the constraint that causes all http requests to return a !403 error
            val security = ConstraintSecurityHandler()

            val constraint = Constraint()
            constraint.dataConstraint = Constraint.DC_CONFIDENTIAL
            //makes the constraint apply to all uri paths
            val mapping = ConstraintMapping()
            mapping.pathSpec = "/*"
            mapping.constraint = constraint
            security.addConstraintMapping(mapping)

            security.handler = handlers
            server!!.handler = security
            // ====================================================================================


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


            // === test-realm.xml ===
            //            HashLoginService login = new HashLoginService();
            //            login.setName("Test Realm");
            //            login.setConfig(jetty_base + "/etc/realm.properties");
            //            login.setHotReload(false);
            //            server.addBean(login);


            val gzipHandler = GzipHandler()
            gzipHandler.setIncludedMimeTypes(
                    "text/html",
                    "text/plain",
                    "text/xml",
                    "text/css",
                    "application/json",
                    "application/javascript",
                    "text/javascript")

            contexts.addHandler(gzipHandler)


            val webapp = WebAppContext()
            gzipHandler.handler = webapp
            webapp.baseResource = Resource.newResource(File(staticFolderLocation))
            webapp.contextPath = "/"
            webapp.welcomeFiles = arrayOf("index.html")
            webapp.addServlet(DefaultServlet::class.java, "/")
            contexts.addHandler(webapp)

            val errorMapper = ErrorPageErrorHandler()
            errorMapper.addErrorPage(404, "/") // map all 404's to root (aka /index.html)
            webapp.errorHandler = errorMapper

            // rewrites everything
            //            RewriteHandler rewrite = new RewriteHandler();
            //            rewrite.setRewriteRequestURI(true);
            //            rewrite.setRewritePathInfo(false);
            //            rewrite.setOriginalPathAttribute("requestedPath");
            //
            //            RewritePatternRule rewritePatternRule = new RewritePatternRule();
            //            rewritePatternRule.setPattern("/*");
            //            rewritePatternRule.setReplacement("/index.html");
            //            rewritePatternRule.setTerminating(true);
            //            rewrite.addRule(rewritePatternRule);
            //
            ////            RedirectPatternRule redirect2 = new RedirectPatternRule();
            ////            redirect2.setPattern("/*");
            ////            redirect2.setLocation("/index.html");
            ////            rewrite.addRule(redirect2);
            //
            //            handlers.addHandler(rewrite);


            //Servlet -----
            //            final ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);

            //            ServletContextHandler servletContextHandler =
            //                    new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);

            // Enable CORS - cross origin resource sharing (for http and https)
            //            FilterHolder cors = new FilterHolder();
            //            cors.setInitParameter("allowedOrigins", "*");
            //            cors.setInitParameter("allowedHeaders", "*");
            //            cors.setInitParameter("allowedMethods", "GET, HEAD, OPTIONS, POST");
            //            cors.setFilter(new CrossOriginFilter());
            //            servletContextHandler.addFilter(cors, "*", EnumSet.of(
            //                    DispatcherType.REQUEST,
            //                    DispatcherType.ASYNC,
            //                    DispatcherType.FORWARD,
            //                    DispatcherType.INCLUDE,
            //                    DispatcherType.ERROR
            //            ));
            //


            server!!.start()
            server!!.join()


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    override fun doStop() {
        try {
            server!!.stop()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}
