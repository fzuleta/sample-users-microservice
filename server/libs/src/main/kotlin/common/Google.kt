package common

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import org.apache.http.NameValuePair
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.HttpClients
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.UnsupportedEncodingException
import java.util.*

object Google{
    var recaptchaKey:String = ""
    var captchaEnabled = true

    fun checkCaptcha(value: String): Boolean {
        if (!captchaEnabled) return true

        var ret = false
        val gson = GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .serializeNulls()
                .create()

        try {
            val httpclient = HttpClients.createDefault()
            val post = HttpPost("https://www.google.com/recaptcha/api/siteverify")

            val postParameters = mutableListOf<NameValuePair>()
            postParameters.add(BasicNameValuePair("secret", recaptchaKey))
            postParameters.add(BasicNameValuePair("response", value))

            post.entity = UrlEncodedFormEntity(postParameters)

            // Execute the post request
            val response = httpclient.execute(post)

            val rd = BufferedReader(InputStreamReader(response.entity.content))
            val result = StringBuilder()
            rd.lines().forEach({ result.append(it) })

            val theResult = result.toString()
//            println("sending: " +postParameters.toString())
//            println("Google RETURN:")
//            println(theResult)

            try {
                val entity = response.entity
                EntityUtils.consume(entity)
            } finally {
                response.close()
                val googleResp = gson.fromJson(theResult, JsonObject::class.java)
                ret = googleResp.get("success").asBoolean
            }
        } catch (e: UnsupportedEncodingException) {
            println(Arrays.toString(e.stackTrace))
        } catch (e: ClientProtocolException) {
            println(Arrays.toString(e.stackTrace))
        } catch (e: IOException) {
            println(Arrays.toString(e.stackTrace))
        }

        return ret
    }
}