package cl.duoc.agronutri.model

object Roles {
    const val AGRICULTOR = "AGRICULTOR"
    const val ASESOR = "ASESOR"
}

data class LoginUiState(
    val correo: String = "",
    val clave: String = "",
    val errorCorreo: String? = null,
    val errorClave: String? = null,
    val errorGeneral: String? = null,
    val cargando: Boolean = false
)

data class RegistroUiState(
    val nombre: String = "",
    val correo: String = "",
    val rol: String? = null,
    val clave: String = "",
    val confirmarClave: String = "",
    val consentimiento: Boolean = false,
    val errorNombre: String? = null,
    val errorCorreo: String? = null,
    val errorRol: String? = null,
    val errorClave: String? = null,
    val errorConfirmar: String? = null,
    val errorConsentimiento: String? = null,
    val errorGeneral: String? = null,
    val cargando: Boolean = false
)

data class SesionUsuario(
    val id: Long,
    val nombre: String,
    val correo: String,
    val rol: String
)
