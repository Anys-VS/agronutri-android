package cl.duoc.agronutri

import cl.duoc.agronutri.domain.CalculoNutricional
import cl.duoc.agronutri.domain.CultivoReferencia
import cl.duoc.agronutri.domain.Nutrientes
import org.junit.Assert.assertEquals
import org.junit.Test

class CalculoNutricionalTest {

    private val cultivo = CultivoReferencia(
        nombre = "Prueba",
        rendimientoBase = 10.0,
        requerimientoBase = Nutrientes(n = 100.0, p2o5 = 50.0, k2o = 80.0, cao = 40.0, mgo = 20.0)
    )

    @Test
    fun metaIgualAlBaseYRiegoPerfecto_devuelveElRequerimientoBase() {
        val r = CalculoNutricional.calcular(cultivo, metaRendimiento = 10.0, eficienciaRiego = 100.0)
        assertEquals(100.0, r.n, 0.001)
        assertEquals(20.0, r.mgo, 0.001)
    }

    @Test
    fun dobleMeta_doblaLaDemanda() {
        val r = CalculoNutricional.calcular(cultivo, metaRendimiento = 20.0, eficienciaRiego = 100.0)
        assertEquals(200.0, r.n, 0.001)
        assertEquals(100.0, r.p2o5, 0.001)
    }

    @Test
    fun eficienciaAl50_doblaLaDemanda() {
        val r = CalculoNutricional.calcular(cultivo, metaRendimiento = 10.0, eficienciaRiego = 50.0)
        assertEquals(200.0, r.n, 0.001)
        assertEquals(160.0, r.k2o, 0.001)
    }

    @Test(expected = IllegalArgumentException::class)
    fun eficienciaCero_lanzaError() {
        CalculoNutricional.calcular(cultivo, metaRendimiento = 10.0, eficienciaRiego = 0.0)
    }
}
