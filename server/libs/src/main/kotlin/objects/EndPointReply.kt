package objects


import com.google.gson.GsonBuilder
import com.google.gson.JsonObject

class EndPointReply {
    var success: Boolean = false
    set(value){
        field = value
        if (!field) ok = null else ok = true
    }
    var errorDesc = ""
    var errorCode: Int? = 0
    var error = SYError.Error.OK
    var data = JsonObject()

    // used for easy failing and returning
    @Transient var ok: Boolean? = null


    override fun toString(): String {
        val gson = GsonBuilder().disableHtmlEscaping().serializeNulls().create()
        return gson.toJson(this, EndPointReply::class.java)
    }

    fun toJsonObject(): JsonObject {
        val gson = GsonBuilder().disableHtmlEscaping().create()
        return gson.fromJson(toString(), JsonObject::class.java)
    }

    companion object {
        @Throws(Exception::class)
        fun fromString(s: String): EndPointReply {
            val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
            val t = gson.fromJson(s, EndPointReply::class.java)
            // had to manually placed this since it wasn't being called
            if (t.success) t.ok = true
            return t
        }
    }
}
