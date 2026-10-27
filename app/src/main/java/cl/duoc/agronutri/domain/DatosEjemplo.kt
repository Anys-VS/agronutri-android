package cl.duoc.agronutri.domain

// VALORES FICTICIOS, SOLO PARA LA DEMO.
// No son recomendaciones agronómicas: las tablas reales las entrega el cliente
// (Consultora Gestión y Proyectos) y reemplazan esta lista.
object DatosEjemplo {
    val cultivos: List<CultivoReferencia> = listOf(
        CultivoReferencia(
            nombre = "Palto",
            rendimientoBase = 10.0,
            requerimientoBase = Nutrientes(n = 60.0, p2o5 = 20.0, k2o = 80.0, cao = 30.0, mgo = 15.0)
        ),
        CultivoReferencia(
            nombre = "Cítricos",
            rendimientoBase = 40.0,
            requerimientoBase = Nutrientes(n = 120.0, p2o5 = 40.0, k2o = 150.0, cao = 80.0, mgo = 30.0)
        ),
        CultivoReferencia(
            nombre = "Tomate",
            rendimientoBase = 80.0,
            requerimientoBase = Nutrientes(n = 200.0, p2o5 = 80.0, k2o = 300.0, cao = 150.0, mgo = 40.0)
        )
    )

    fun buscar(nombre: String): CultivoReferencia? = cultivos.find { it.nombre == nombre }
}
