import error_notif.ErrorEmail
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig

object jed {
    var redisPoolGuest: JedisPool? = null
    var redisPoolUser: JedisPool? = null
    var urlGuest = ""
    var urlUser = ""
    var passGuest = ""
    var passUser = ""
    var portGuest: String = ""
    var portUser: String = ""
    var minPool: Int = 1
    var maxPool: Int = 30

    fun init(callback: () -> Unit) {
        println("Starting jedis")
        val poolConfig = JedisPoolConfig()
        poolConfig.maxTotal = maxPool
//        poolConfig.testOnBorrow = true
//        poolConfig.testOnReturn = true

        redisPoolGuest = JedisPool(poolConfig, urlGuest, portGuest.toInt(), 12000, passGuest)
        redisPoolUser = JedisPool(poolConfig, urlUser, portUser.toInt(), 12000, passUser)
        println("Jedis started")
        callback()
    }

    fun getGuest(): Jedis? {
        val j = redisPoolGuest?.resource
        if (j == null) ErrorEmail.`send error Email`("members", "jedis", "JEDIS GUEST IS NOT WORKING!")

        return j
    }
    fun getUser(): Jedis? {
        val j = redisPoolUser?.resource
        if (j == null) ErrorEmail.`send error Email`("members", "jedis", "JEDIS USER IS NOT WORKING!")

        return j
    }

    fun close(j: Jedis) = j.close()
}