package cuisine.de.lapin.library.blockchain

import com.google.gson.Gson
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val temp = Gson().fromJson("dfadfs", Sample::class.java)
        println("$temp")
    }

    data class Sample(val temp: String)
}