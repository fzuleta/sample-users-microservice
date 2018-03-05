package jetty.ep.login

import com.google.gson.Gson
import com.google.gson.JsonObject
import jetty.EndPoint
import objects.EndPointReply
import org.apache.shiro.SecurityUtils
import org.apache.shiro.mgt.RealmSecurityManager
import org.apache.shiro.realm.AuthorizingRealm
import shiro.Shiro
import java.time.LocalDateTime


class Logout: EndPoint() {
    override suspend fun doAction(gson: Gson, obj: JsonObject, endPointReply: EndPointReply, utcTime: LocalDateTime) {
        val subject = SecurityUtils.getSubject()
        val realms = (SecurityUtils.getSecurityManager() as RealmSecurityManager).realms

        for (realm in realms) {
            val authRealm = realm as AuthorizingRealm
            authRealm.authenticationCache.remove(subject.principals)
            authRealm.authorizationCache.remove(subject.principals)
            // System.out.println("Cleaning realm " + realm.getName()); // Cleaning realm oDBRealm
        }
        if (subject.session != null) {
            subject.session.stop()
        }
        subject.logout()

        endPointReply.success = true
    }
}