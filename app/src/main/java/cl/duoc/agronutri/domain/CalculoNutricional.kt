package cl.duoc.agronutri.domain

// Regla de tres según rendimiento base, corregida por la eficiencia de riego:
//   demanda = requerimientoBase x (meta / rendimientoBase) / (eficiencia / 100)
// TODO confirmar la fórmula con el cliente: NutriExpert no la muestra.
object CalculoNutricional {

    fun calcular(
        cultivo: CultivoReferencia,
        metaRendimiento: Double,
        eficienciaRiego: Double
    ): Nutrientes {
        require(metaRendimiento > 0.0) { "La meta debe ser mayor a 0" }
        require(eficienciaRiego > 0.0 && eficienciaRiego <= 100.0) { "La eficiencia debe estar entre 0 y 100" }
        val factor = (metaRendimiento / cultivo.rendimientoBase) / (eficienciaRiego / 100.0)
        val base = cultivo.requerimientoBase
        return Nutrientes(
            n = base.n * factor,
            p2o5 = base.p2o5 * factor,
            k2o = base.k2o * factor,
            cao = base.cao * factor,
            mgo = base.mgo * factor
        )
    }
}
