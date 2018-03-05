package jetty.socket


import org.eclipse.jetty.websocket.servlet.WebSocketServlet
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory
import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class Socket : WebSocketServlet() {

    public override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        println("Socket helloooo")
    }
    public override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {}

    override fun configure(factory: WebSocketServletFactory) {
        factory.policy.idleTimeout = 1800000 // Half an hour?
        //        factory.register(AdapterEchoSocket.class);
        //        factory.register(ListenerEchoSocket.class);

        val mySessionSocketCreator = MySessionSocketCreator()
        factory.creator = mySessionSocketCreator
    }
}