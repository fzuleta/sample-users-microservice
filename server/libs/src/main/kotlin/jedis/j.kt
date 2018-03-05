package jedis

import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig

object j {
    var redisPool: JedisPool? = null
    var url = ""
    var pass = ""
    var port: String = ""
    var minPool: Int = 1
    var maxPool: Int = 30

    fun init(callback: () -> Unit) {
        println("Starting jedis")
        val poolConfig = JedisPoolConfig()
        poolConfig.maxTotal = maxPool
        redisPool = JedisPool(poolConfig, url, port.toInt(), 4000, pass)
        println("Jedis started")
        callback()
    }

    fun get() = redisPool?.resource
    fun close(j:Jedis) = j.close()
}