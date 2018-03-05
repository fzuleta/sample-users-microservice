package shiro

import org.apache.shiro.authc.credential.DefaultPasswordService
import org.apache.shiro.authc.credential.PasswordMatcher
import org.apache.shiro.cache.ehcache.EhCacheManager
import org.apache.shiro.crypto.hash.DefaultHashService
import org.apache.shiro.realm.AuthorizingRealm
import org.apache.shiro.session.mgt.ExecutorServiceSessionValidationScheduler
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO
import org.apache.shiro.session.mgt.eis.SessionDAO
import org.apache.shiro.util.ByteSource
import org.apache.shiro.web.env.DefaultWebEnvironment
import org.apache.shiro.web.mgt.DefaultWebSecurityManager
import org.apache.shiro.web.servlet.SimpleCookie
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager

import javax.servlet.ServletContext

//
object MyShiroConstructor {
    fun getHashService(): DefaultHashService{
        val hashService = DefaultHashService()
        hashService.hashIterations = 2048
        hashService.hashAlgorithmName = "SHA-256"
        hashService.isGeneratePublicSalt = true
        hashService.privateSalt = ByteSource.Util.bytes(Shiro.hashService)
        return hashService
    }
    fun getPasswordService(): DefaultPasswordService {
        val passwordService = DefaultPasswordService()
        passwordService.hashService = getHashService()
        val passwordMatcher = PasswordMatcher()
        passwordMatcher.passwordService = passwordService

        return passwordService
    }
    fun getWebEnvironment(sc: ServletContext, realm: AuthorizingRealm): DefaultWebEnvironment {
        val sessionManager = DefaultWebSessionManager()
        sessionManager.sessionIdCookie.domain = Shiro.domain // localhost or .domain.com
        sessionManager.sessionIdCookie.maxAge = 288000 //80 hrs , its in seconds
        sessionManager.sessionDAO = EnterpriseCacheSessionDAO()
        sessionManager.globalSessionTimeout = Shiro.sessionDuration // 3600000 = 1h in ms

        val sessionValidationScheduler = ExecutorServiceSessionValidationScheduler()
        sessionValidationScheduler.interval = 3600000 // 1h
        sessionManager.sessionValidationScheduler = sessionValidationScheduler

        val securityManager = DefaultWebSecurityManager()
        securityManager.setRealm(realm)
        securityManager.rememberMeManager = RememberMeManager()
        securityManager.cacheManager = EhCacheManager()
        securityManager.sessionManager = sessionManager

        val defaultWebEnvironment = DefaultWebEnvironment()
        defaultWebEnvironment.servletContext = sc
        defaultWebEnvironment.webSecurityManager = securityManager

        return defaultWebEnvironment
    }
}
