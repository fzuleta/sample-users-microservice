package common

import com.google.gson.JsonArray

object Constants {
    var companyName = "Sample"
    var websitehost = "http:localhost"
    var valid_currencies = JsonArray()
    var clientURL = "https://usermicroservice.com:8443"
    var useHashOnClientURL = true
    var validDepositCurrencies = mutableListOf<String>()
    var twitchId = ""
    var twitchSecret = ""
}