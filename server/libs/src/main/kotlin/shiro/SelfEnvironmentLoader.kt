package shiro


import org.apache.shiro.realm.AuthorizingRealm
import org.apache.shiro.web.env.EnvironmentLoader
import org.apache.shiro.web.env.WebEnvironment
import org.slf4j.LoggerFactory

import javax.servlet.ServletContext
import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener

class SelfEnvironmentLoader(val realm: AuthorizingRealm) : EnvironmentLoader(), ServletContextListener {
    override fun contextInitialized(servletContextEvent: ServletContextEvent) {
        this.initEnvironment(servletContextEvent.servletContext)
    }
    override fun contextDestroyed(servletContextEvent: ServletContextEvent) {
        this.destroyEnvironment(servletContextEvent.servletContext)
    }
    override fun createEnvironment(sc: ServletContext): WebEnvironment =
        MyShiroConstructor.getWebEnvironment(sc, realm)

}
