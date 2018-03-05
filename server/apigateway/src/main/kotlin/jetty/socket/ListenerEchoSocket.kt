package jetty.socket

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.WebSocketListener

import java.io.IOException
import java.util.HashMap

class ListenerEchoSocket : WebSocketListener {

    private var session: Session? = null
    private var myUniqueId: String? = null

    override fun onWebSocketBinary(payload: ByteArray, offset: Int, len: Int) {}

    override fun onWebSocketClose(statusCode: Int, reason: String) {
        if (ListenerEchoSocket.sockets.containsKey(this.myUniqueId)) {
            // remove connection
            ListenerEchoSocket.sockets.remove(this.myUniqueId)

            //            // broadcast this lost connection to all other connected clients
            //            for (ListenerEchoSocket dstSocket : ListenerEchoSocket.sockets.values()) {
            //                if (dstSocket == this) {
            //                    continue;
            //                }
            //                dstSocket.sendClient(String.format("{\"msg\": \"lostClient\", \"lostClientId\": \"%s\"}",
            //                        this.myUniqueId));
            //            }
        }
        this.session = null
    }

    private fun getMyUniqueId(): String {
        // unique ID from this class' hash code
        return Integer.toHexString(this.hashCode())
    }

    override fun onWebSocketConnect(session: Session) {
        this.session = session
        this.myUniqueId = this.getMyUniqueId()

        // map this unique ID to this connection
        ListenerEchoSocket.sockets.put(this.myUniqueId!!, this)

        val gson = GsonBuilder().serializeNulls().create()
        val o = JsonObject()
        o.addProperty("message", "soid")
        o.addProperty("data", this.myUniqueId)
        sendClient(gson.toJson(o))

    }

    override fun onWebSocketError(cause: Throwable) {
        cause.printStackTrace(System.err)
    }

    override fun onWebSocketText(message: String) {
        if (session != null && session!!.isOpen) {
            //            System.out.printf("Echoing back message [%s]%n", message);
            //            session.getRemote().sendString(message, null);
            //            this.sendClient(this.myUniqueId + "  / " + message);
        }
    }

    private fun sendClient(str: String) {
        try {
            this.session!!.remote.sendString(str)
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun sendError(err: String) {
        this.sendClient(String.format("{\"msg\": \"error\", \"error\": \"%s\"}", err))
    }

    companion object {
        private val sockets = HashMap<String, ListenerEchoSocket>()
    }
}
