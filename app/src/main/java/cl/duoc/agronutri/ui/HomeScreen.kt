package cl.duoc.agronutri.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.agronutri.model.HomeUiState
import cl.duoc.agronutri.model.RegistroResumen
import cl.duoc.agronutri.ui.theme.AgroNutriTheme

// Pantalla sin estado propio: recibe los datos y avisa las acciones.
// Así no depende de cómo se guarda la sesión ni los registros.
@Composable
fun HomeScreen(
    state: HomeUiState,
    onNuevoRegistro: () -> Unit,
    onVerHistorial: (() -> Unit)? = null,
    onCerrarSesion: (() -> Unit)? = null
) {
    Scaffold(
        containerColor = Color.White,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            EncabezadoMarca()

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Saludo(state = state, onCerrarSesion = onCerrarSesion)
                }
                item {
                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        BotonPrincipal("Nuevo registro de terreno", onClick = onNuevoRegistro)
                        if (onVerHistorial != null) {
                            BotonSecundario("Ver historial", onClick = onVerHistorial)
                        }
                    }
                }
                item {
                    Text(
                        text = "Últimos registros",
                        color = ColoresMarca.Titulo,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 12.dp)
                    )
                }
                if (state.ultimosRegistros.isEmpty()) {
                    item {
                        Text(
                            text = "Aún no hay registros. Crea el primero desde terreno, funciona sin conexión.",
                            color = ColoresMarca.Subtitulo,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                } else {
                    items(state.ultimosRegistros, key = { it.id }) { registro ->
                        TarjetaRegistro(
                            registro = registro,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
                item { Spacer(Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
private fun Saludo(state: HomeUiState, onCerrarSesion: (() -> Unit)?) {
    Column(modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Hola, ${state.nombre}",
                color = ColoresMarca.Titulo,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.weight(1f)
            )
            if (onCerrarSesion != null) {
                TextButton(onClick = onCerrarSesion) {
                    Text("Cerrar sesión", color = ColoresMarca.Verde, fontWeight = FontWeight.SemiBold)
                }
            }
        }
        Text(
            text = nombreRol(state.rol),
            color = ColoresMarca.Subtitulo,
            fontSize = 15.sp
        )
        Spacer(Modifier.height(12.dp))
        // La app trabaja offline: todo se guarda en el teléfono y se sincroniza en una etapa futura.
        ChipSinConexion(texto = "Sin conexión · ${textoPendientes(state.pendientes)}")
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun TarjetaRegistro(registro: RegistroResumen, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = ColoresMarca.ChipFondo)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(registro.cultivo, color = ColoresMarca.Titulo, fontWeight = FontWeight.Bold)
                Text(
                    "Lote ${registro.lote} · ${registro.fecha}",
                    color = ColoresMarca.Subtitulo,
                    fontSize = 13.sp
                )
            }
            Text(
                text = if (registro.sincronizado) "Sincronizado" else "Pendiente",
                color = if (registro.sincronizado) ColoresMarca.Verde else ColoresMarca.Gota,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

private fun nombreRol(rol: String): String = when (rol) {
    "AGRICULTOR" -> "Agricultor"
    "ASESOR" -> "Asesor agrónomo"
    else -> rol
}

private fun textoPendientes(n: Int): String = when (n) {
    0 -> "sin registros pendientes"
    1 -> "1 registro pendiente"
    else -> "$n registros pendientes"
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    AgroNutriTheme {
        HomeScreen(
            state = HomeUiState(
                nombre = "Usuario Demo",
                rol = "AGRICULTOR",
                pendientes = 2,
                ultimosRegistros = listOf(
                    RegistroResumen(1, "Palto (ejemplo)", "L-01", "27-10-2026", false),
                    RegistroResumen(2, "Vid (ejemplo)", "L-02", "26-10-2026", false)
                )
            ),
            onNuevoRegistro = {},
            onVerHistorial = {},
            onCerrarSesion = {}
        )
    }
}
