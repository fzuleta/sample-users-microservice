package memory

import common.functions
import entities.Delegate_MemberV1 
import entities.Member_V1
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import redis.clients.jedis.Jedis
import java.time.LocalDateTime
import java.time.ZoneOffset

object db_users {
    val REF = "ref"
    val EMAIL = "email"
    val REF_GUEST = "refguest"
    val EMAIL_GUEST = "emailguest"
    val REFERRALID = "referralid"

    fun init() {

    }
    /* =============================================================================
        CONVENIENCE FUNCTIONS
    ================================================================================ */
    fun isGuest(str:String) = str.startsWith("guest_") && str.endsWith("ttt")
    suspend fun getByReference(reference: String): Delegate_MemberV1? {
        val refKey = if (isGuest(reference)) REF_GUEST else REF
        val j = if (isGuest(reference)) jed.getGuest() else jed.getUser()
        val m = async {
            val res = j?.hget(refKey, reference) ?: return@async null
            Delegate_MemberV1(Member_V1.fromString(res))
        }
        m.await()
        val res = m.getCompleted()
        j?.close()
        return res
    }
    suspend fun getByEmail(email: String):Delegate_MemberV1?{
        val j = if (isGuest(email)) jed.getGuest() else jed.getUser()
        val m = async {
            val refKey = if (isGuest(email)) REF_GUEST else REF
            val emailKey = if (isGuest(email)) EMAIL_GUEST else EMAIL

            val ref = j?.hget(emailKey, email) ?: return@async null
            val m = j.hget(refKey, ref) ?: return@async null
            Delegate_MemberV1(Member_V1.fromString(m))
        }
        m.await()

        val res = m.getCompleted()

        if (res != null) {
            launch {
                res.dateLastLogin = LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC)
                save(res, j)
                j?.close()
            }
        } else {
            j?.close()
        }
        return res
    }
    fun getJedis(reference: String): Jedis? = if (isGuest(reference)) jed.getGuest() else jed.getUser()
    fun save(m:Member_V1):Delegate_MemberV1 = save(Delegate_MemberV1(m))
    fun save(m:Member_V1, j:Jedis?):Delegate_MemberV1 = save(Delegate_MemberV1(m), j)
    fun save(m:Delegate_MemberV1):Delegate_MemberV1 {
        val j = getJedis(m.reference)
        save(m, j)
        j?.close()
        return m
    }
    fun save(m:Delegate_MemberV1, j:Jedis?):Delegate_MemberV1 {
        val ref = if (isGuest(m.reference)) REF_GUEST else REF
        j?.hset(ref, m.reference, m.toString())
        return m
    }
    fun deleteGuest(reference: String, email: String) {
        val j = jed.getGuest()
        j?.hdel(REF_GUEST, reference)
        j?.hdel(EMAIL_GUEST, email)
        j?.close()
    }
    private suspend fun uniqueId(): String {
//        var reference = functions.aUniqueReference
//        while (true) {
//            var f = members.containsKey(reference)
//            if (!f) f = members.containsKey(reference)
//
//            if (f) reference = functions.aUniqueReference
//            if (!f) break
//        }
//        return reference
        return functions.aRandomString
    }

}
