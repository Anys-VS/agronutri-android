package cl.duoc.agronutri.domain

// Demanda de nutrientes en kg/há.
data class Nutrientes(
    val n: Double,
    val p2o5: Double,
    val k2o: Double,
    val cao: Double,
    val mgo: Double
) {
    // Pares nombre-valor para dibujar la tabla en el orden de NutriExpert.
    fun comoLista(): List<Pair<String, Double>> = listOf(
        "N" to n,
        "P₂O₅" to p2o5,
        "K₂O" to k2o,
        "CaO" to cao,
        "MgO" to mgo
    )
}

// Cultivo con su rendimiento base (t/há) y lo que extrae a ese rendimiento.
data class CultivoReferencia(
    val nombre: String,
    val rendimientoBase: Double,
    val requerimientoBase: Nutrientes
)
