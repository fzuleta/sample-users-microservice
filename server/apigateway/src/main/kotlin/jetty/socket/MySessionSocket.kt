package jetty.socket

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import org.apache.shiro.subject.Subject
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.annotations.*
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import javax.servlet.http.HttpSession


@WebSocket(maxTextMessageSize = 64 * 1024)
class MySessionSocket(var httpSession: HttpSession?, var reference: String, private var subject: Subject?) {
    var wsSession: Session? = null

    init {
        if (this.reference != "") {
            MySessionSocket.sockets.put(this.reference, this)
        }
    }

    /* ================================================================================================= */
    @OnWebSocketConnect
    fun onOpen(wsSession: Session) {
        this.wsSession = wsSession
    }


    @OnWebSocketMessage
    fun onMessage(msg: String) {
        val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().serializeNulls().create()

        try {
            val m = gson.fromJson(msg, JsonObject::class.java)
            var ret: JsonObject? = null
            var data: JsonObject? = null
            val command = if (m.has("command")) m.get("command").asString else null

            if (command != null) {
                m.remove("command")
                when (command) {
                    "hookMeUp" -> {
                    }

                }//                        Functions.trace("====== SOCKET HOOK");

                //                Functions.trace("DATA");
                //                Functions.trace(data);

                // if user_references and message
                if (data != null && data.has("user_references") && data.has("command")) {
                    val user_references = data.get("user_references").asJsonArray
                    for (i in 0 until user_references.size()) {
                        val ref = user_references.get(i).asString
                        val socket = MySessionSocket.getClient(ref)
                        socket?.sendClient(data.toString())
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        System.out.printf("Got msg: %s%n", msg)
    }

    @OnWebSocketError
    fun onError(cause: Throwable) {
        cause.printStackTrace(System.err)
    }

    @OnWebSocketClose
    fun onClose(statusCode: Int, reason: String) {
        this.wsSession = null

        /* Cleanup */
        if (this.reference != "") {
            this.subject = null
            if (MySessionSocket.sockets.containsKey(this.reference)) {
                MySessionSocket.sockets.remove(this.reference)
            }
        }
    }

    fun sendClient(str: String) {
        try {
            this.wsSession!!.remote.sendString(str)
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    companion object {

        /* ================================================================================================= */
        /* STATIC */
        /* ================================================================================================= */
        private val sockets = ConcurrentHashMap<String, MySessionSocket>()

        fun broadcastMessage(msg: String) {
            for (client in sockets.values) {
                client.sendClient(msg)
            }
        }


        fun getClient(reference: String): MySessionSocket? {
            var client: MySessionSocket? = null
            //        Functions.trace("Finding: " + reference + "  /  " + MySessionSocket.sockets.containsKey(reference));
            if (MySessionSocket.sockets.containsKey(reference)) {
                client = MySessionSocket.sockets[reference]
            }
            return client
        }
    }

}
