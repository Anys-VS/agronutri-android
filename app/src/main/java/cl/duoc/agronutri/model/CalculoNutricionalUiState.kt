package cl.duoc.agronutri.model

data class CalculoNutricionalUiState(
    val cultivo: String = "",
    val lote: String = "",
    val metaRendimiento: String = "",
    val eficienciaRiego: String = "",
    // Dirección content:// de la foto foliar. Vacía = sin foto (es opcional).
    val fotoUri: String = "",
    val errores: CalculoNutricionalErrores = CalculoNutricionalErrores()
)

data class CalculoNutricionalErrores(
    val cultivo: String? = null,
    val lote: String? = null,
    val metaRendimiento: String? = null,
    val eficienciaRiego: String? = null
)
