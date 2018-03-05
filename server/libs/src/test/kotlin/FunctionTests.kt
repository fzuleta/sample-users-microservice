import common.StringUtils
import common.functions
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test

class FunctionTests {
    @Test
    fun `isValidCountry`() {
        Assert.assertTrue(functions.isValidCountry("cOloMBIa"))
        Assert.assertFalse(functions.isValidCountry("COL"))
    }
    @Test
    fun `getValidCountryCode`() {
        Assert.assertEquals(57, functions.getValidCountryCode("cOloMBIa"))
        Assert.assertEquals(57, functions.getValidCountryCode("COLOMBIA"))
        Assert.assertEquals(57, functions.getValidCountryCode("Colombia"))
    }
    @Test
    fun `isValidEmail`() {
        Assert.assertFalse(functions.isValidEmail(""))
        Assert.assertFalse(functions.isValidEmail("felipezuleta"))
        Assert.assertFalse(functions.isValidEmail("@felipe.com"))
        Assert.assertFalse(functions.isValidEmail("jaja@.com"))
        Assert.assertTrue(functions.isValidEmail("feli@felipe.com"))
    }
    @Test
    fun `validPassword`() {
        Assert.assertFalse(functions.validPassword("", ""))
//        Assert.assertFalse(functions.validPassword("InvAlid", "InvAlid"))
//        Assert.assertFalse(functions.validPassword("inva123lid", "inva123lid"))
        Assert.assertTrue(functions.validPassword("Val1d0", "Val1d0"))
        Assert.assertTrue(functions.validPassword("1qweQWE", "1qweQWE"))
    }

    @Test fun `AES Encrypt works`() {
        val str = "hello"
        val res = StringUtils.encrypt(str)
        Assert.assertEquals(str, StringUtils.decrypt(res!!))
    }
}