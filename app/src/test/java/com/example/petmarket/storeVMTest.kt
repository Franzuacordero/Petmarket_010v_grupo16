import org.junit.Assert.assertEquals
import org.junit.Test

class MathTest {

    fun total(precios: List<Int>): Int {
        return precios.sum()
    }

    @Test
    fun total_sumaCorrecto() {
        val lista = listOf(1000, 2000, 3000)
        val resultado = total(lista)

        assertEquals(6000, resultado)
    }
}
