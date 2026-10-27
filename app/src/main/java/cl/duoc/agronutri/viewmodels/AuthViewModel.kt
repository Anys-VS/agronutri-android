package cl.duoc.agronutri.viewmodels

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.agronutri.data.AppDatabase
import cl.duoc.agronutri.data.AuthRepository
import cl.duoc.agronutri.data.ResultadoRegistro
import cl.duoc.agronutri.data.Usuario
import cl.duoc.agronutri.model.LoginUiState
import cl.duoc.agronutri.model.RegistroUiState
import cl.duoc.agronutri.model.SesionUsuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = AuthRepository(AppDatabase.obtener(application).usuarioDao())

    private val _login = MutableStateFlow(LoginUiState())
    val login: StateFlow<LoginUiState> = _login.asStateFlow()

    private val _registro = MutableStateFlow(RegistroUiState())
    val registro: StateFlow<RegistroUiState> = _registro.asStateFlow()

    private val _sesion = MutableStateFlow<SesionUsuario?>(null)
    val sesion: StateFlow<SesionUsuario?> = _sesion.asStateFlow()

    // ---------- Login ----------

    fun onLoginCorreoChange(valor: String) =
        _login.update { it.copy(correo = valor, errorCorreo = null, errorGeneral = null) }

    fun onLoginClaveChange(valor: String) =
        _login.update { it.copy(clave = valor, errorClave = null, errorGeneral = null) }

    fun onLoginErrorMostrado() = _login.update { it.copy(errorGeneral = null) }

    fun validarLogin(): Boolean {
        val s = _login.value
        val errorCorreo = validarCorreo(s.correo)
        val errorClave = if (s.clave.isBlank()) "Ingresa tu contraseña" else null
        _login.update { it.copy(errorCorreo = errorCorreo, errorClave = errorClave) }
        return errorCorreo == null && errorClave == null
    }

    fun iniciarSesion(onExito: () -> Unit) {
        if (_login.value.cargando || !validarLogin()) return
        _login.update { it.copy(cargando = true, errorGeneral = null) }
        viewModelScope.launch {
            val s = _login.value
            val usuario = try {
                repo.login(s.correo, s.clave)
            } catch (e: Exception) {
                _login.update {
                    it.copy(cargando = false, errorGeneral = "No se pudo iniciar sesión. Intenta de nuevo.")
                }
                return@launch
            }
            if (usuario != null) {
                abrirSesion(usuario)
                _login.value = LoginUiState()
                onExito()
            } else {
                // Mensaje genérico: no revela si el correo existe.
                _login.update {
                    it.copy(cargando = false, clave = "", errorGeneral = "Correo o contraseña incorrectos")
                }
            }
        }
    }

    // ---------- Registro ----------

    fun onRegistroNombreChange(valor: String) =
        _registro.update { it.copy(nombre = valor, errorNombre = null) }

    fun onRegistroCorreoChange(valor: String) =
        _registro.update { it.copy(correo = valor, errorCorreo = null) }

    fun onRegistroRolChange(valor: String) =
        _registro.update { it.copy(rol = valor, errorRol = null) }

    fun onRegistroClaveChange(valor: String) =
        _registro.update { it.copy(clave = valor, errorClave = null) }

    fun onRegistroConfirmarChange(valor: String) =
        _registro.update { it.copy(confirmarClave = valor, errorConfirmar = null) }

    fun onRegistroConsentimientoChange(valor: Boolean) =
        _registro.update { it.copy(consentimiento = valor, errorConsentimiento = null) }

    fun onRegistroErrorMostrado() = _registro.update { it.copy(errorGeneral = null) }

    fun validarRegistro(): Boolean {
        val s = _registro.value
        val errorNombre = if (s.nombre.trim().length < 2) "Ingresa tu nombre (mínimo 2 letras)" else null
        val errorCorreo = validarCorreo(s.correo)
        val errorRol = if (s.rol == null) "Elige tu perfil" else null
        val errorClave = when {
            s.clave.length < 8 -> "Mínimo 8 caracteres"
            s.clave.none { it.isLetter() } || s.clave.none { it.isDigit() } -> "Debe tener letras y números"
            else -> null
        }
        val errorConfirmar = when {
            s.confirmarClave.isEmpty() -> "Repite tu contraseña"
            s.confirmarClave != s.clave -> "Las contraseñas no coinciden"
            else -> null
        }
        val errorConsentimiento =
            if (!s.consentimiento) "Debes aceptar para crear tu cuenta" else null

        _registro.update {
            it.copy(
                errorNombre = errorNombre,
                errorCorreo = errorCorreo,
                errorRol = errorRol,
                errorClave = errorClave,
                errorConfirmar = errorConfirmar,
                errorConsentimiento = errorConsentimiento
            )
        }
        return listOf(
            errorNombre, errorCorreo, errorRol, errorClave, errorConfirmar, errorConsentimiento
        ).all { it == null }
    }

    fun registrar(onExito: () -> Unit) {
        if (_registro.value.cargando || !validarRegistro()) return
        _registro.update { it.copy(cargando = true, errorGeneral = null) }
        viewModelScope.launch {
            val s = _registro.value
            try {
                when (repo.registrar(s.nombre, s.correo, s.rol!!, s.clave)) {
                    ResultadoRegistro.Ok -> {
                        // Cuenta creada: se inicia sesión de inmediato.
                        val usuario = repo.login(s.correo, s.clave)
                        if (usuario != null) {
                            abrirSesion(usuario)
                            _registro.value = RegistroUiState()
                            onExito()
                        } else {
                            _registro.update {
                                it.copy(cargando = false, errorGeneral = "Cuenta creada. Ahora inicia sesión.")
                            }
                        }
                    }
                    ResultadoRegistro.CorreoExistente -> _registro.update {
                        it.copy(cargando = false, errorCorreo = "Este correo ya está registrado")
                    }
                }
            } catch (e: Exception) {
                _registro.update {
                    it.copy(cargando = false, errorGeneral = "No se pudo crear la cuenta. Intenta de nuevo.")
                }
            }
        }
    }

    // ---------- Sesión ----------

    fun cerrarSesion() {
        _sesion.value = null
    }

    private fun abrirSesion(usuario: Usuario) {
        _sesion.value = SesionUsuario(
            id = usuario.id,
            nombre = usuario.nombre,
            correo = usuario.correo,
            rol = usuario.rol
        )
    }

    private fun validarCorreo(correo: String): String? = when {
        correo.isBlank() -> "Ingresa tu correo"
        !Patterns.EMAIL_ADDRESS.matcher(correo.trim()).matches() -> "Correo no válido"
        else -> null
    }
}
