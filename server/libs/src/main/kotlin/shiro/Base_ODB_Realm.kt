package shiro

import com.google.gson.JsonObject
import objects.EndPointReply
import org.apache.shiro.authc.*
import org.apache.shiro.authz.AuthorizationInfo
import org.apache.shiro.authz.SimpleAuthorizationInfo
import org.apache.shiro.realm.AuthorizingRealm
import org.apache.shiro.subject.PrincipalCollection
import org.apache.shiro.subject.SimplePrincipalCollection

open class Base_ODB_Realm : AuthorizingRealm() {
    override fun doGetAuthenticationInfo(authenticationToken: AuthenticationToken): AuthenticationInfo {
        val reference      = (authenticationToken as UsernamePasswordToken).username

        val ep = database_connect(reference)
        if (!ep.success) throw AuthenticationException("Error during login")

        val principals  = createPrincipals(ep.data)

        super.doClearCache(principals)
        return SimpleAuthenticationInfo(principals, reference)
    }
    open fun createPrincipals(data: JsonObject): PrincipalCollection {
        val principals = mutableListOf<String>()
        principals.add(data.get("reference").asString)
        return SimplePrincipalCollection(principals, name)
    }
    protected open fun database_connect(reference:String): EndPointReply = EndPointReply()
    public override fun clearCachedAuthorizationInfo(principals: PrincipalCollection) {
        super.clearCachedAuthorizationInfo(principals)
    }
    override fun doGetAuthorizationInfo(principalCollection: PrincipalCollection): AuthorizationInfo = SimpleAuthorizationInfo()

}
