package vertxl

import com.google.gson.JsonObject
import com.hazelcast.config.Config
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.core.http.ClientAuth
import io.vertx.kotlin.core.VertxOptions
import io.vertx.kotlin.core.eventbus.EventBusOptions
import io.vertx.kotlin.core.net.JksOptions
import io.vertx.kotlin.coroutines.awaitEvent
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager
import objects.EndPointReply


object VertX {
    var started = false
    var host:String = "localhost"
    var env:String = "local"
    var sslEnabled = false
    var keystore:String = "keystore"
    var keystorePassword:String = "keystorePassword"
    var truststore:String = "keystore"
    var truststorePassword:String = "keystorePassword"
    var clusterName:String = "mysupercluster"
    var clusterPassword:String = "ClusTerPasw00r2d"
    var httpPort = 8080
    var httpsPort = 8443

    var cors_allowedOrigins = "*"//"http://localhost:9000";

    open var initCallback = {}
    var vertx: Vertx? = null

    fun start() {
        val options = getVertxOptions()
        options.clusterHost = host

        Vertx.clusteredVertx(options) { res ->
            if (res.succeeded()) {
                this.vertx = res.result()

                initCallback()

                println("vertx started")
                started = true
            } else {
                // failed!
            }
        }
    }
    suspend fun awaitFor(endpoint: String, o: JsonObject):EndPointReply {
        return awaitEvent { h ->
            vertx!!.eventBus().send<Any>(endpoint, o.toString(), { ar ->
                var res = EndPointReply()
                if (ar.succeeded()) {
                    res = EndPointReply.fromString(ar.result().body().toString())
                } else {
                    // TODO Notify someone!
                    ar.cause().printStackTrace()
                }
                h.handle(res)
            })
        }
    }
    fun send(endpoint: String, str: String) {
        vertx!!.eventBus().send(endpoint, str)
    }
    fun publish(endpoint: String, str: String) {
        vertx!!.eventBus().publish(endpoint, str)
    }

    private fun getVertxOptions(): VertxOptions {
        val mgr = HazelcastClusterManager(getHazelcastConfig())
       return if (sslEnabled)
           VertxOptions(eventBusOptions = EventBusOptions(
                   ssl = true,
                   keyStoreOptions = JksOptions(
                           path = keystore,
                           password = keystorePassword),
                   trustStoreOptions = JksOptions(
                           path = truststore,
                           password = truststorePassword),
                   clientAuth = ClientAuth.REQUIRED)

           ).setClusterManager(mgr)
        else
           VertxOptions().setClusterManager(mgr)

    }

    private fun getHazelcastConfig(): Config {
        // recommended on vert.x docs
        System.setProperty("hazelcast.shutdownhook.enabled", "false")
        System.setProperty("hazelcast.async-api", "true")

        val hazelcastConfig = Config()
        hazelcastConfig.groupConfig.name = clusterName
        hazelcastConfig.groupConfig.password = clusterPassword

        if (env == "local") {
            hazelcastConfig.networkConfig.join.tcpIpConfig.addMember(host).isEnabled = true
            hazelcastConfig.networkConfig.join.multicastConfig.isEnabled = false
        }

        return hazelcastConfig
    }

}
