package cl.duoc.agronutri.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Encabezado(state = state, onCerrarSesion = onCerrarSesion)
            }
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(onClick = onNuevoRegistro, modifier = Modifier.fillMaxWidth()) {
                        Text("Nuevo registro de terreno")
                    }
                    if (onVerHistorial != null) {
                        OutlinedButton(onClick = onVerHistorial, modifier = Modifier.fillMaxWidth()) {
                            Text("Ver historial")
                        }
                    }
                }
            }
            item {
                Text(
                    text = "Últimos registros",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
            if (state.ultimosRegistros.isEmpty()) {
                item {
                    Text(
                        text = "Aún no hay registros. Crea el primero desde terreno, funciona sin conexión.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            } else {
                items(state.ultimosRegistros, key = { it.id }) { registro ->
                    TarjetaRegistro(
                        registro = registro,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun Encabezado(state: HomeUiState, onCerrarSesion: (() -> Unit)?) {
    Surface(color = MaterialTheme.colorScheme.primary) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "AgroNutri",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                if (onCerrarSesion != null) {
                    TextButton(onClick = onCerrarSesion) {
                        Text("Cerrar sesión", color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Hola, ${state.nombre}",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = nombreRol(state.rol),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(12.dp))
            // La app trabaja offline: todo se guarda en el teléfono y se sincroniza en una etapa futura.
            AssistChip(
                onClick = {},
                label = {
                    Text(
                        text = "Sin conexión · ${textoPendientes(state.pendientes)}",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            )
        }
    }
}

@Composable
private fun TarjetaRegistro(registro: RegistroResumen, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(registro.cultivo, style = MaterialTheme.typography.titleSmall)
                Text("Lote ${registro.lote} · ${registro.fecha}", style = MaterialTheme.typography.bodySmall)
            }
            Text(
                text = if (registro.sincronizado) "Sincronizado" else "Pendiente",
                style = MaterialTheme.typography.labelMedium,
                color = if (registro.sincronizado) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.tertiary
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
