package shiro

import com.google.gson.JsonObject
import objects.EndPointReply
import org.apache.shiro.authc.AuthenticationException
import org.apache.shiro.subject.PrincipalCollection
import org.apache.shiro.subject.SimplePrincipalCollection

import java.util.ArrayList

class ODBRealm : Base_ODB_Realm() {
    override fun database_connect(reference:String): EndPointReply {
        val ep = EndPointReply()
        ep.data.addProperty("reference", reference)
        ep.success = true
        return ep
    }
    override fun createPrincipals(data: JsonObject): PrincipalCollection {
        val principals = ArrayList<Any>(1)
        principals.add(data["reference"].asString)

        return SimplePrincipalCollection(principals, name)
    }
}
