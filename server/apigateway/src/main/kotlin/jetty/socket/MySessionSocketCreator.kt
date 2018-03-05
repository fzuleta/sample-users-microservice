package jetty.socket

import org.apache.shiro.SecurityUtils
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse
import org.eclipse.jetty.websocket.servlet.WebSocketCreator
import shiro.Shiro


class MySessionSocketCreator : WebSocketCreator {
    override fun createWebSocket(req: ServletUpgradeRequest, resp: ServletUpgradeResponse): Any {
        val subject     = SecurityUtils.getSubject()
        val reference   = Shiro.get_reference(subject)

        return MySessionSocket(null, reference ?: "", subject)
    }
}
