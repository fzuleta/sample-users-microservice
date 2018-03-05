package odb

import com.orientechnologies.orient.client.remote.OServerAdmin
import com.orientechnologies.orient.core.sql.OCommandSQL
import com.orientechnologies.orient.server.OServer
import com.orientechnologies.orient.server.OServerMain
import com.orientechnologies.orient.server.config.*
import com.tinkerpop.blueprints.impls.orient.OrientGraph
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory
import kotlinx.coroutines.experimental.async
import java.io.File
import java.util.*

object Orientdb {
    var callback:()->Unit = {}
    var dbExists = false
    var connType = "remote"
    var connServer = "localhost"
    var classToCheckIfDBExists = "user"
    var minPool = 1
    var maxPool = 30
    private var orientDBfactory: OrientGraphFactory? = null
    var odb_db = ""
    var odb_user = ""
    var odb_pass = ""
    var odb_maxRetries = 100
    private var server: OServer? = null
    var odb_DB_TYPE = "plocal" //"memory";
    var port = "2424"
    var sslPort = "2425"
    var orientdbSecurityFile = ""
    var sslPortRange = "2425"
    var sslEnabled                    = false
    var ssl_keystore                  = ""
    var ssl_keystorePassword          = ""
    var ssl_trustStore                = ""
    var ssl_trustStorePassword        = ""
    var odbPathToCreate               = ""
    var orientdbHome                  = ""
    fun create() {
        System.setProperty("ORIENTDB_HOME", orientdbHome)

        println(orientdbHome)

        server = OServerMain.create()

        val cfg = OServerConfiguration()

        cfg.users = arrayOfNulls<OServerUserConfiguration>(1)
        cfg.users[0] = OServerUserConfiguration(odb_user, odb_pass, "*")

        cfg.network = OServerNetworkConfiguration()
        cfg.network.protocols = mutableListOf<OServerNetworkProtocolConfiguration>()
        cfg.network.protocols.add(OServerNetworkProtocolConfiguration("binary", "com.orientechnologies.orient.server.network.protocol.binary.ONetworkProtocolBinary"))


        val http = OServerNetworkListenerConfiguration()
        cfg.network.listeners = mutableListOf<OServerNetworkListenerConfiguration>()
        cfg.network.listeners.add(http)
        http.ipAddress = "0.0.0.0"
        http.socket="default"
        http.portRange = port
        http.protocol = "binary"

        enableHttp(cfg)
        enableTLS(cfg)

        cfg.properties = arrayOfNulls<OServerEntryConfiguration>(7)
        cfg.properties[0] = OServerEntryConfiguration("log.console.level", "debug")
        cfg.properties[1] = OServerEntryConfiguration("log.file.level", "fine")
        cfg.properties[2] = OServerEntryConfiguration("plugin.dynamic", "true")
        cfg.properties[3] = OServerEntryConfiguration("server.database.path", "$orientdbHome")
        cfg.properties[4] = OServerEntryConfiguration("db.pool.min", "$minPool")
        cfg.properties[5] = OServerEntryConfiguration("db.pool.max", "$maxPool")
        cfg.properties[6] = OServerEntryConfiguration("server.security.file", "$orientdbSecurityFile")
//        cfg.properties[7] = OServerEntryConfiguration("profiler.enabled", "true")
//        cfg.properties[8] = OServerEntryConfiguration("server.cache.staticResources", "false")
//        cfg.properties[9] = OServerEntryConfiguration("orientdb.www.path", orientdbHome + "/event_store/src/main/resources/www")


        DBCheck().startAsync()

        server?.startup(cfg)
        server?.activate()

    }

    private fun enableTLS(cfg: OServerConfiguration) {
        if (sslEnabled) {
            cfg.network.sockets = mutableListOf<OServerSocketFactoryConfiguration>()
            val sslsocket = OServerSocketFactoryConfiguration("ssl", "com.orientechnologies.orient.server.network.OServerTLSSocketFactory")
            sslsocket.parameters = arrayOfNulls<OServerParameterConfiguration>(5)
            sslsocket.parameters[0] = OServerParameterConfiguration("network.ssl.clientAuth", "true")
            sslsocket.parameters[1] = OServerParameterConfiguration("network.ssl.keyStore", ssl_keystore)
            sslsocket.parameters[2] = OServerParameterConfiguration("network.ssl.keyStorePassword", ssl_keystorePassword)
            sslsocket.parameters[3] = OServerParameterConfiguration("network.ssl.trustStore", ssl_trustStore)
            sslsocket.parameters[4] = OServerParameterConfiguration("network.ssl.trustStorePassword", ssl_trustStorePassword)

            val sslListener = OServerNetworkListenerConfiguration()
            sslListener.protocol = "binary"
            sslListener.socket = "ssl"
            sslListener.portRange = sslPortRange
            sslListener.ipAddress = "0.0.0.0"
            cfg.network.listeners.add(sslListener)

            println("ODBManager -> Loading ssl from " + ssl_keystore)
        }
    }
    private fun enableHttp(cfg: OServerConfiguration) {
        cfg.network.protocols.add(OServerNetworkProtocolConfiguration("http", "com.orientechnologies.orient.server.network.protocol.http.ONetworkProtocolHttpDb"))

        val ip1 = OServerNetworkListenerConfiguration()
        cfg.network.listeners.add(ip1)
        ip1.ipAddress = "0.0.0.0"
        ip1.portRange = "2480-2490"
        ip1.protocol = "http"
        ip1.commands = arrayOfNulls<OServerCommandConfiguration>(2)
        ip1.parameters = arrayOfNulls<OServerParameterConfiguration>(1)

        val ip1Command0 = OServerCommandConfiguration()
        ip1.commands[0] = ip1Command0
        ip1Command0.implementation="com.orientechnologies.orient.server.network.protocol.http.command.get.OServerCommandGetStaticContent"
        ip1Command0.pattern = "GET|www GET|studio/ GET| GET|*.htm GET|*.html GET|*.xml GET|*.jpeg GET|*.jpg GET|*.png GET|*.gif GET|*.js GET|*.css GET|*.swf GET|*.ico GET|*.txt GET|*.otf GET|*.pjs GET|*.svg GET|*.json GET|*.woff GET|*.ttf GET|*.svgz"
        ip1Command0.stateful = false
        ip1Command0.parameters = arrayOfNulls<OServerEntryConfiguration>(2)
        ip1Command0.parameters[0] = OServerEntryConfiguration("http.cache:*.htm *.html", "Cache-Control: no-cache, no-store, max-age=0, must-revalidate\r\\nPragma: no-cache")
        ip1Command0.parameters[1] = OServerEntryConfiguration("http.cache:default", "Cache-Control: max-age=120")

        val ip1Command1 = OServerCommandConfiguration()
        ip1Command1.implementation = "com.orientechnologies.orient.graph.server.command.OServerCommandGetGephi"
        ip1Command1.pattern = "GET|gephi/*"
        ip1Command1.stateful = false
        ip1.commands[1] = ip1Command1

        ip1.parameters[0] = OServerParameterConfiguration("network.http.charset", "utf-8")

    }

    fun getOrientDBGraph(): OrientGraph? = try {
            createDB()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    private fun createDBFactory() {
        if (orientDBfactory == null) {
            val port = if (this.sslEnabled) this.sslPort else this.port
            orientDBfactory = OrientGraphFactory(
                    "$connType:$connServer:$port/$odb_db",
                    odb_user,
                    odb_pass
            ).setupPool(minPool, maxPool)
        }
    }

    private fun closeDBFactory() {
        if (orientDBfactory != null) orientDBfactory?.close()
        orientDBfactory = null
    }

    private fun createDB(): OrientGraph? {
        var graph: OrientGraph? = if (orientDBfactory != null) orientDBfactory?.tx else null
        val port = if (this.sslEnabled) this.sslPort else this.port

        if (!dbExists || graph == null) {
            graph?.shutdown()

            println("Checking if Database needs to be re-created > connType: $connType")
            val url = "$connType:$connServer:$port"
            println(url)
            val serverAdmin = OServerAdmin(url).connect(odb_user, odb_pass)
            println("Connected as Admin")
            if (serverAdmin.listDatabases()[odb_db] == null) {
                println("Database doesn't exist")
                serverAdmin.createDatabase(odb_db, "graph", odb_DB_TYPE)
                println("DB Created")
            } else {
                dbExists = true
            }
            serverAdmin.close()

            createDBFactory()
            graph = orientDBfactory?.tx
            if (orientDBfactory != null && !dbExists) { // && graph!!.getVertexType(classToCheckIfDBExists) == null) {
                println("Rebuilding the database")

                println("odbPathToCreate: " + odbPathToCreate)
                val file = File(odbPathToCreate)

                val scanner = Scanner(file)
                while (scanner.hasNextLine()) {
                    val line = scanner.nextLine()
                    // If NOT
                    if (!(line.contains("-- ")
                            || line.contains("CREATE DATABASE")
                            || line.contains("CONNECT remote:")
                            || line.contains("DISCONNECT")
                            || line.trim().isEmpty())) {
                        println(line)
                        graph?.command(OCommandSQL(line))?.execute<Any>()
                    }
                }

                scanner.close()
                graph?.commit()
                println("DB Created")
            }
            graph?.shutdown()
            closeDBFactory()

            dbExists = true
            println("DB's good to go")

            // TODO Notify a server admin that the DB was recreated
        }

        createDBFactory()
        return orientDBfactory?.tx
    }

    suspend fun db_action(func: (OrientGraph) -> Boolean): Boolean {
        //Add it to in-memory
        var graph: OrientGraph? = null
        var success: Boolean? = false

        try {
            // Functions.println("opening db...");
            graph = getOrientDBGraph()
            if (graph == null) {
                // TODO send an ALERT -> email or something
                println("DB connection is not possible, for whatever reason")
                return false
            }

            val a = async {
                (0..odb_maxRetries).forEach { retry ->
                    try {
                        success = func(graph)
                        return@forEach
                    } catch (ex:Exception) {
                        println("retrying... " + retry);
                    }
                }
            }
            a.await()

        } catch (e: Exception) {
            e.printStackTrace()

            try {
                if (graph != null) {
                    graph.rollback()
                    graph.shutdown()
                }
            } catch (e1: Exception) {
                e1.printStackTrace()
            }

        } finally {
            // println("closing db...");
            try {
                if (graph != null) {
                    if (success == false) {
                        graph.rollback()
                    }
                    graph.shutdown()

                    // close the connection?
                    // orientDBfactory.close();
                    // orientDBfactory = null;
                }
            } catch (e1: Exception) { /* Do nothing */
            }

            return success!!
        }
    }
}