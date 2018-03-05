package shiro


import org.apache.shiro.crypto.AesCipherService
import org.apache.shiro.web.mgt.CookieRememberMeManager
import org.apache.shiro.web.servlet.Cookie
import org.apache.shiro.web.servlet.SimpleCookie

/**
 * This extension of the CookieRememberMeManager used a random cipher key for
 * better security.
 */
class RememberMeManager : CookieRememberMeManager() {
    init {

        cipherKey = Shiro.rememberMeCypher.toByteArray()

        // Cookie with the . in the domain sets it so that it can be used in subdomains
        val cookie = SimpleCookie(CookieRememberMeManager.DEFAULT_REMEMBER_ME_COOKIE_NAME)
        cookie.isHttpOnly   = true
        cookie.domain       = Shiro.domain
        cookie.maxAge       = Cookie.ONE_YEAR
//                cookie.setSecure(true); //test this
        this.cookie         = cookie
    }

}
