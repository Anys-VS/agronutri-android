package cl.duoc.agronutri.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import cl.duoc.agronutri.viewmodels.AuthViewModel

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onVolver: () -> Unit,
    onIrARegistro: () -> Unit,
    onLoginExitoso: () -> Unit
) {
    val state by authViewModel.login.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorGeneral) {
        state.errorGeneral?.let {
            snackbarHostState.showSnackbar(it)
            authViewModel.onLoginErrorMostrado()
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
            // Sin barra verde: solo el botón para volver, sobre fondo blanco.
            TextButton(
                onClick = onVolver,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(start = 8.dp, top = 8.dp)
            ) {
                Text("← Volver", color = ColoresMarca.Verde, fontWeight = FontWeight.SemiBold)
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .navigationBarsPadding()
                    .padding(24.dp)
            ) {
                Text(
                    "Iniciar sesión",
                    color = ColoresMarca.Titulo,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Ingresa con tu correo y contraseña",
                    color = ColoresMarca.Subtitulo,
                    fontSize = 15.sp
                )
                Spacer(Modifier.height(24.dp))

                CampoTexto(
                    valor = state.correo,
                    onCambio = authViewModel::onLoginCorreoChange,
                    etiqueta = "Correo electrónico",
                    error = state.errorCorreo,
                    tipoTeclado = KeyboardType.Email,
                    habilitado = !state.cargando
                )
                Spacer(Modifier.height(8.dp))
                CampoTexto(
                    valor = state.clave,
                    onCambio = authViewModel::onLoginClaveChange,
                    etiqueta = "Contraseña",
                    error = state.errorClave,
                    tipoTeclado = KeyboardType.Password,
                    esClave = true,
                    accionTeclado = ImeAction.Done,
                    habilitado = !state.cargando
                )
                Spacer(Modifier.height(24.dp))

                BotonPrincipal(
                    texto = "Iniciar sesión",
                    onClick = { authViewModel.iniciarSesion(onLoginExitoso) },
                    cargando = state.cargando
                )
                Spacer(Modifier.height(8.dp))
                TextButton(
                    onClick = onIrARegistro,
                    enabled = !state.cargando,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        "¿No tienes cuenta? Regístrate",
                        color = ColoresMarca.Verde,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
