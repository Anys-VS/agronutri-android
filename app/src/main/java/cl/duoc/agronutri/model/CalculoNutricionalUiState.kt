package cl.duoc.agronutri.model

import cl.duoc.agronutri.domain.Nutrientes

data class CalculoNutricionalUiState(
    val cultivo: String = "",
    val lote: String = "",
    val metaRendimiento: String = "",
    val eficienciaRiego: String = "",
    // Dirección content:// de la foto foliar. Vacía = sin foto (es opcional).
    val fotoUri: String = "",
    // null = todavía no se calcula, o se cambió un dato después de calcular.
    val resultado: Nutrientes? = null,
    val calculando: Boolean = false,
    val guardando: Boolean = false,
    val errores: CalculoNutricionalErrores = CalculoNutricionalErrores()
)

data class CalculoNutricionalErrores(
    val cultivo: String? = null,
    val lote: String? = null,
    val metaRendimiento: String? = null,
    val eficienciaRiego: String? = null
)
