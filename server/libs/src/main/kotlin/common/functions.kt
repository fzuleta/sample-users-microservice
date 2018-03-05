package common

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.tinkerpop.blueprints.impls.orient.OrientGraph
import org.apache.commons.math3.random.RandomDataGenerator
import org.slf4j.Logger

import java.math.BigDecimal
import java.math.BigInteger
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.ParsePosition
import java.util.ArrayList
import java.util.Locale
import java.util.Random


object functions {
    var logger: Logger? = null

    val Int.bd:         BigDecimal get() = BigDecimal(this)
    val Int.bi:         BigInteger get() = BigInteger(this.toString())
    val Double.bd:      BigDecimal get() = BigDecimal(this)

    val String.bd:      BigDecimal get() = BigDecimal(this)
    val String.bi:      BigInteger get() = BigInteger(this)
    val String.p:       JsonPrimitive get() = JsonPrimitive(this)
    val BigDecimal.p:   JsonPrimitive get() = JsonPrimitive(this)

    fun JsonObject.primitive(s:String): JsonElement? {
        if (has(s) && !get(s).isJsonNull) return get(s)
        return null
    }
    fun JsonObject.s(s:String): String? = primitive(s)?.asString
    fun JsonObject.l(s:String): Long?   = primitive(s)?.asLong
    fun JsonObject.a(s:String): JsonArray?   = primitive(s)?.asJsonArray
    fun JsonObject.o(s:String): JsonObject?   = primitive(s)?.asJsonObject
    fun JsonObject.bool(s:String): Boolean   = primitive(s)!=null && primitive(s)!!.asBoolean
    fun JsonObject.bd(str:String): BigDecimal? {
        val s = this.s(str) ?: return null
        var b:BigDecimal? = null
        try { b = s.bd } catch (e:Exception) {}
        return b
    }

    fun generateString(rng: Random, characters: String, length: Int): String {
        val text = CharArray(length)
        for (i in 0..length - 1) {
            text[i] = characters[rng.nextInt(characters.length)]
        }
        return String(text)
    }

    fun randInt(min: Int, max: Int): Int {

        // Usually this can be a field rather than a method variable
        val rand = Random()

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        val randomNum = rand.nextInt(max - min + 1) + min

        return randomNum
    }
    fun isNumeric(input: String): Boolean =
        try {
            input.toDouble()
            true
        } catch(e: NumberFormatException) {
            false
        }

    fun isPowerOfTwo(x: Int): Boolean {
        return x == 1 || x == 2 || x == 4 || x == 8 || x == 16 || x == 32 ||
                x == 64 || x == 128 || x == 256 || x == 512 || x == 1024 ||
                x == 2048 || x == 4096 || x == 8192 || x == 16384 ||
                x == 32768 || x == 65536 || x == 131072 || x == 262144 ||
                x == 524288 || x == 1048576 || x == 2097152 ||
                x == 4194304 || x == 8388608 || x == 16777216 ||
                x == 33554432 || x == 67108864 || x == 134217728 ||
                x == 268435456 || x == 536870912 || x == 1073741824// ||
        //x == 2147483648); // number too large
    }

    fun findClosestLowerPOW2(x: Int): Int {
        val pow2 = object : ArrayList<Int>() {
            init {
                add(1073741824)
                add(536870912)
                add(268435456)
                add(134217728)
                add(67108864)
                add(33554432)
                add(16777216)
                add(8388608)
                add(4194304)
                add(2097152)
                add(1048576)
                add(524288)
                add(262144)
                add(131072)
                add(65536)
                add(32768)
                add(16384)
                add(8192)
                add(4096)
                add(2048)
                add(1024)
                add(512)
                add(256)
                add(128)
                add(64)
                add(32)
                add(16)
                add(8)
                add(4)
                add(2)
                add(1)
                add(0)
            }
        }
        //        for (int k = 0; k < pow2.size(); k++) {
        for (k in pow2.indices.reversed()) {
            val l = pow2[k]
            if (x < l) {
                return pow2[k + 1]
            }
        }

        return 0
    }

    fun getRandomString(amount:Int = 30):String {
        return generateString(Random(), "qwertyuiopasdfghjklzxcvbnm1234567890", amount)
    }
    val aRandomString: String
        get() {
            return getRandomString(30)
        }
    val aUniqueReference: String
        get() {
            val randomData = RandomDataGenerator()
            return randomData.nextLong(1000L, 999999999L).toString() + ""
        }

    fun getAUniqueReference(prefix: String, graph: OrientGraph, type: String, dbField: String): String {
        val randomData = RandomDataGenerator()
        var reference = prefix + randomData.nextLong(1000L, 999999999L)

        // Make sure it doesn't exist
        while (true) {
            val found = if (type.toLowerCase().startsWith("v"))
                graph.getVertices(dbField, reference).iterator().hasNext()
            else
                graph.getEdges(dbField, reference).iterator().hasNext()

            //            if (type.toLowerCase().startsWith("e")) {
            //                functions.trace("Finding... " + reference + " found: " + found);
            //            }

            if (!found) {
                //                functions.trace("Finding... " + reference + " found: " + found);
                return reference
            } else {
                reference = prefix + randomData.nextLong(1000L, 999999999L)
            }
        }

    }

    /**
     * Note on big decimal
     * http://www.opentaps.org/docs/index.php/How_to_Use_Java_BigDecimal:_A_Tutorial
     * - Always use compareTo()  NOT equals()
     * - it is a good idea to use the string constructor: new BigDecimal("1.5");  //If not the result is 1.499887...
     * - Set the scale with rounding mode: a.setScale(0, BigDecimal.ROUND_HALF_EVEN).toString() // => 2

     * ARITMETIC:  amount = amount.add( thisAmount );   NOT amount = amount + 123

     * @param str BD to convert from string
     * *
     * @return Big decimal
     */
    var BigDecimalScale = 8
    var BigDecimalRounding = BigDecimal.ROUND_DOWN
    private fun convertStringToBigDecimal(str: String): BigDecimal {
        val in_ID = Locale("en", "US")

        val nf = NumberFormat.getInstance(in_ID) as DecimalFormat
        nf.isParseBigDecimal = true

        val bigDecimal = nf.parse(str, ParsePosition(0)) as BigDecimal
        bigDecimal.setScale(BigDecimalScale, BigDecimalRounding)

        return bigDecimal
    }

    fun getCountries(): MutableMap<Int, String> {
        val countries: MutableMap<Int, String> = mutableMapOf()
        countries.put(213,  "Algeria")
        countries.put(54,   "Argentina")
        countries.put(61,   "Australia")
        countries.put(43,   "Austria")
        countries.put(880,  "Bangladesh")
        countries.put(32,   "Belgium")
        countries.put(375,  "Belarus")
        countries.put(591,  "Bolivia")
        countries.put(55,  "Brazil")
        countries.put(673, "Brunei")
        countries.put(359, "Bulgaria")
        countries.put(1, "Canada")
        countries.put(56, "Chile")
        countries.put(86, "China")
        countries.put(852, "Hong Kong")
        countries.put(57, "Colombia")
        countries.put(385, "Croatia")
        countries.put(357, "Cyprus")
        countries.put(420, "Czech Republic")
        countries.put(45, "Denmark")
        countries.put(809,  "Dominican Republic")
        countries.put(20,  "Egypt")
        countries.put(372,  "Estonia")
        countries.put(358,  "Finland")
        countries.put(33,  "France & territories")
        countries.put(49,  "Germany")
        countries.put(233,  "Ghana")
        countries.put(30,  "Greece")
        countries.put(504,  "Honduras")
        countries.put(36,  "Hungary")
        countries.put(354,  "Iceland")
        countries.put(91,  "India")
        countries.put(62,  "Indonesia")
        countries.put(353,  "Ireland")
        countries.put(972,  "Israel")
        countries.put(39,  "Italy")
        countries.put(7,  "Kazakhstan")
        countries.put(81,  "Japan")
        countries.put(965,  "Kuwait")
        countries.put(371,  "Latvia")
        countries.put(423,  "Liechtenstein")
        countries.put(370,  "Lithuania")
        countries.put(352,  "Luxembourg")
        countries.put(853,  "Macao")
        countries.put(60,  "Malaysia")
        countries.put(356,  "Malta")
        countries.put(52,  "Mexico")
        countries.put(377,  "Monaco")
        countries.put(212,  "Morocco")
        countries.put(264,  "Namibia")
        countries.put(31,  "Netherlands & territories")
        countries.put(64,  "New Zealand")
        countries.put(505,  "Nicaragua ")
        countries.put(234,  "Nigeria")
        countries.put(47,  "Norway")
        countries.put(595,  "Paraguay")
        countries.put(92,  "Pakistan")
        countries.put(51,  "Peru")
        countries.put(63,  "Philippines")
        countries.put(48,  "Poland")
        countries.put(351,  "Portugal")
        countries.put(974,  "Qatar")
        countries.put(82,  "Republic of Korea")
        countries.put(40,  "Romania")
        countries.put(7,  "Russia")
        countries.put(378,  "San Marino")
        countries.put(966,  "Saudi Arabia")
        countries.put(221,  "Senegal")
        countries.put(381,  "Serbia")
        countries.put(65,  "Singapore")
        countries.put(421,  "Slovakia")
        countries.put(386,  "Slovenia")
        countries.put(27,  "South Africa")
        countries.put(34,  "Spain")
        countries.put(46,  "Sweden")
        countries.put(41,  "Switzerland")
        countries.put(886, "Taiwan")
        countries.put(66,  "Thailand")
        countries.put(216, "Tunesia")
        countries.put(90,  "Turkey")
        countries.put(971, "UAE")
        countries.put(380, "Ukraine")
        countries.put(44,  "United Kingdom & U.K. other overseas territories")
        countries.put(58,  "Venezuela")
        countries.put(84,  "Vietnam")
        return countries
    }

    fun getValidCountryCode(country: String): Int? = getCountries().filter { it.value.toLowerCase() == country.toLowerCase() }.keys.firstOrNull()
    fun isValidCountry(country: String): Boolean = getCountries().values.firstOrNull({ it.toLowerCase() == country.toLowerCase()}) != null

    fun validPassword(p0: String, p1: String): Boolean {
        if (p0 != p1) { return false; }
        if (p0.length < 6) { return false; }
//        if (!Regex("[A-Z]").containsMatchIn(p0)) { return false }
//        if (!Regex("[a-z]").containsMatchIn(p0)) { return false }
//        if (!Regex("[0-9]").containsMatchIn(p0)) { return false }
        return true
    }
    fun isValidEmail(email: String): Boolean {
        if (!email.contains("@")
                || !email.contains(".")
                || email.length < 6
                || email[0] == "@"[0]
                || email.contains("@.")
                ) {
            return false
        }
        return true
    }
}
