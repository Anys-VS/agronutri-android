package cl.duoc.agronutri.model

import cl.duoc.agronutri.domain.Nutrientes

data class CalculoNutricionalUiState(
    val cultivo: String = "",
    val lote: String = "",
    val metaRendimiento: String = "",
    val eficienciaRiego: String = "",
    val fotoUri: String = "",
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
