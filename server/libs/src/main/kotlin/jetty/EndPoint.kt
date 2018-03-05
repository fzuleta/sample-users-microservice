package jetty

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import kotlinx.coroutines.experimental.runBlocking
import objects.EndPointReply
import org.apache.commons.io.IOUtils

import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.io.IOException
import java.time.LocalDateTime
import java.time.ZoneOffset


open class EndPoint : HttpServlet() {
    var showTrace = false
    var myEndPointName = ""

    @Throws(ServletException::class, IOException::class)
    public override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        resp.contentType = "application/json"
        resp.characterEncoding = "UTF-8"
        resp.status = HttpServletResponse.SC_OK

        /*  Current time */
        val utcTime = LocalDateTime.now(ZoneOffset.UTC)

        val endPointReply = EndPointReply()

        val gson = GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .serializeNulls()
                .create()


        var str_request: String? = IOUtils.toString(req.inputStream, "UTF-8")

        // Get a list of all active tournaments
        try {
            if (str_request == null || str_request == "") {
                str_request = "{}"
            }

            val obj = gson.fromJson(str_request, JsonObject::class.java)

            val me = this
            runBlocking {
                me.doAction(gson, obj, endPointReply, utcTime)
            }

        } catch (e: Exception) {
            e.printStackTrace()

        } finally {

            val str_response = gson.toJson(endPointReply, EndPointReply::class.java)

            if (showTrace) {
                println(myEndPointName + "\n" + str_response)
            }

            resp.outputStream.print(str_response)

        }
    }

    @Throws(Exception::class)
    open suspend fun doAction(gson: Gson, obj: JsonObject, endPointReply: EndPointReply, utcTime: LocalDateTime) {
        //All endpoints replace this
    }
}
