package cl.duoc.agronutri.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.agronutri.data.RegistroTerreno
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistorialScreen(
    registros: List<RegistroTerreno>,
    onBorrar: (Long) -> Unit,
    onVolver: () -> Unit
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
            EncabezadoMarca(onVolver = onVolver)

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Column(modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 24.dp)) {
                        Text(
                            "Historial",
                            color = ColoresMarca.Titulo,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            "Registros guardados en este teléfono",
                            color = ColoresMarca.Subtitulo,
                            fontSize = 15.sp
                        )
                    }
                }
                if (registros.isEmpty()) {
                    item {
                        Text(
                            "Todavía no hay registros",
                            color = ColoresMarca.Subtitulo,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
                items(registros, key = { it.id }) { registro ->
                    TarjetaHistorial(
                        registro = registro,
                        onBorrar = { onBorrar(registro.id) },
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TarjetaHistorial(
    registro: RegistroTerreno,
    onBorrar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = ColoresMarca.ChipFondo)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "${registro.cultivo} · Lote ${registro.lote}",
                    color = ColoresMarca.Titulo,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    if (registro.sincronizado) "Sincronizado" else "Pendiente",
                    color = if (registro.sincronizado) ColoresMarca.Verde else ColoresMarca.Gota,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Text(formatoFecha(registro.creadoEn), color = ColoresMarca.Subtitulo, fontSize = 13.sp)
            Text(
                "Meta ${formatoNumero(registro.metaRendimiento)} t/há · Riego ${formatoNumero(registro.eficienciaRiego)} %",
                color = ColoresMarca.Subtitulo,
                fontSize = 13.sp
            )
            Text(
                "N ${formatoNumero(registro.n)} · P₂O₅ ${formatoNumero(registro.p2o5)} · " +
                    "K₂O ${formatoNumero(registro.k2o)} kg/há",
                color = ColoresMarca.Verde,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                if (registro.fotoUri.isBlank()) "Sin foto foliar" else "Con foto foliar",
                color = ColoresMarca.Subtitulo,
                fontSize = 13.sp
            )
            TextButton(onClick = onBorrar, modifier = Modifier.align(Alignment.End)) {
                Text("Borrar", color = ColoresMarca.Verde)
            }
        }
    }
}

fun formatoFecha(millis: Long): String {
    val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return formato.format(Date(millis))
}

// Coma decimal y un decimal, como se escribe en Chile.
private fun formatoNumero(valor: Double): String =
    String.format(Locale.forLanguageTag("es-CL"), "%.1f", valor)
