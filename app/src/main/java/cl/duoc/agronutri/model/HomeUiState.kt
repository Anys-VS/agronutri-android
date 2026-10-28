package cl.duoc.agronutri.model

data class HomeUiState(
    val nombre: String = "",
    val rol: String = "",
    val pendientes: Int = 0,
    val ultimosRegistros: List<RegistroResumen> = emptyList()
)

data class RegistroResumen(
    val id: Long,
    val cultivo: String,
    val lote: String,
    val fecha: String,
    val sincronizado: Boolean
)
