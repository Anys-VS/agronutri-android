package cl.duoc.agronutri.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.agronutri.model.Roles
import cl.duoc.agronutri.viewmodels.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    authViewModel: AuthViewModel,
    onVolver: () -> Unit,
    onIrALogin: () -> Unit,
    onRegistroExitoso: () -> Unit
) {
    val state by authViewModel.registro.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val colorError = MaterialTheme.colorScheme.error

    LaunchedEffect(state.errorGeneral) {
        state.errorGeneral?.let {
            snackbarHostState.showSnackbar(it)
            authViewModel.onRegistroErrorMostrado()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.White,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
        ) {
            EncabezadoMarca(onVolver = onVolver)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .navigationBarsPadding()
                    .padding(24.dp)
            ) {
                Text(
                    "Crear cuenta",
                    color = ColoresMarca.Titulo,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Solo pedimos los datos necesarios",
                    color = ColoresMarca.Subtitulo,
                    fontSize = 15.sp
                )
                Spacer(Modifier.height(20.dp))

                CampoTexto(
                    valor = state.nombre,
                    onCambio = authViewModel::onRegistroNombreChange,
                    etiqueta = "Nombre",
                    error = state.errorNombre,
                    habilitado = !state.cargando
                )
                Spacer(Modifier.height(8.dp))
                CampoTexto(
                    valor = state.correo,
                    onCambio = authViewModel::onRegistroCorreoChange,
                    etiqueta = "Correo electrónico",
                    error = state.errorCorreo,
                    tipoTeclado = KeyboardType.Email,
                    habilitado = !state.cargando
                )
                Spacer(Modifier.height(8.dp))

                // Perfil
                Text("Perfil", color = ColoresMarca.Subtitulo, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        Roles.AGRICULTOR to "Agricultor",
                        Roles.ASESOR to "Asesor agrónomo"
                    ).forEach { (valor, etiqueta) ->
                        FilterChip(
                            selected = state.rol == valor,
                            onClick = { authViewModel.onRegistroRolChange(valor) },
                            enabled = !state.cargando,
                            label = { Text(etiqueta) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ColoresMarca.Verde,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
                AnimatedVisibility(visible = state.errorRol != null) {
                    Text(state.errorRol ?: "", color = colorError, fontSize = 12.sp)
                }
                Spacer(Modifier.height(12.dp))

                CampoTexto(
                    valor = state.clave,
                    onCambio = authViewModel::onRegistroClaveChange,
                    etiqueta = "Contraseña",
                    error = state.errorClave,
                    tipoTeclado = KeyboardType.Password,
                    esClave = true,
                    habilitado = !state.cargando,
                    ayuda = "Mínimo 8 caracteres, con letras y números"
                )
                Spacer(Modifier.height(8.dp))
                CampoTexto(
                    valor = state.confirmarClave,
                    onCambio = authViewModel::onRegistroConfirmarChange,
                    etiqueta = "Repetir contraseña",
                    error = state.errorConfirmar,
                    tipoTeclado = KeyboardType.Password,
                    esClave = true,
                    accionTeclado = ImeAction.Done,
                    habilitado = !state.cargando
                )
                Spacer(Modifier.height(8.dp))

                // Consentimiento (Ley 19.628 y Ley 21.719)
                Row(verticalAlignment = Alignment.Top) {
                    Checkbox(
                        checked = state.consentimiento,
                        onCheckedChange = authViewModel::onRegistroConsentimientoChange,
                        enabled = !state.cargando,
                        colors = CheckboxDefaults.colors(checkedColor = ColoresMarca.Verde)
                    )
                    Text(
                        "Acepto que mi nombre, correo y perfil se guarden solo en este " +
                            "teléfono para usar la app. No se comparten con terceros " +
                            "(Ley 19.628 y Ley 21.719).",
                        color = ColoresMarca.Subtitulo,
                        fontSize = 13.sp,
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .clickable(enabled = !state.cargando) {
                                authViewModel.onRegistroConsentimientoChange(!state.consentimiento)
                            }
                    )
                }
                AnimatedVisibility(visible = state.errorConsentimiento != null) {
                    Text(
                        state.errorConsentimiento ?: "",
                        color = colorError,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }
                Spacer(Modifier.height(20.dp))

                BotonPrincipal(
                    texto = "Crear cuenta",
                    onClick = { authViewModel.registrar(onRegistroExitoso) },
                    cargando = state.cargando
                )
                Spacer(Modifier.height(8.dp))
                TextButton(
                    onClick = onIrALogin,
                    enabled = !state.cargando,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        "¿Ya tienes cuenta? Inicia sesión",
                        color = ColoresMarca.Verde,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
