package cl.duoc.agronutri.model

// Lo que muestra el Home. La pantalla no sabe de dónde vienen los datos:
// el nombre y el rol salen de la sesión y los registros, de Room.
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
