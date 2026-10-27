package cl.duoc.agronutri.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.agronutri.domain.DatosEjemplo
import cl.duoc.agronutri.domain.Nutrientes
import cl.duoc.agronutri.viewmodels.CalculoNutricionalViewModel
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculoNutricionalScreen(
    viewModel: CalculoNutricionalViewModel = viewModel()
) {
    val estado by viewModel.estado.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // ---------- Cámara: foto foliar ----------
    val contexto = LocalContext.current
    // Dirección mientras la cámara está abierta; todavía no es la foto del formulario.
    var uriPendiente by remember { mutableStateOf<Uri?>(null) }

    val camaraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { guardada ->
        val pendiente = uriPendiente
        if (guardada && pendiente != null) {
            viewModel.onFotoTomada(pendiente.toString())
        }
        uriPendiente = null
    }

    fun abrirCamara() {
        val uri = viewModel.crearFoto()
        uriPendiente = uri
        camaraLauncher.launch(uri)
    }

    val permisoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            abrirCamara()
        } else {
            scope.launch {
                snackbarHostState.showSnackbar("Sin permiso de cámara no se puede tomar la foto")
            }
        }
    }

    fun pedirFoto() {
        val tieneCamara =
            contexto.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
        if (!tieneCamara) {
            scope.launch {
                snackbarHostState.showSnackbar("Este aparato no tiene cámara")
            }
            return
        }
        val permitido = ContextCompat.checkSelfPermission(
            contexto,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        if (permitido) {
            abrirCamara()
        } else {
            permisoLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    val foto = remember(estado.fotoUri) {
        if (estado.fotoUri.isBlank()) null else cargarFoto(contexto, estado.fotoUri)
    }

    // TODO confirmar con el cliente: lista de cultivos (viene del catalogo)
    val cultivos = DatosEjemplo.cultivos.map { it.nombre }

    // Al aparecer el resultado, la pantalla baja sola hasta la tarjeta.
    val scrollState = rememberScrollState()
    LaunchedEffect(estado.resultado) {
        if (estado.resultado != null) {
            delay(300) // espera a que la tarjeta termine de aparecer
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    val colorBoton by animateColorAsState(
        targetValue = when {
            estado.calculando -> Color.Gray
            estado.resultado != null -> Color(0xFF2E7D32)
            else -> MaterialTheme.colorScheme.primary
        },
        label = "colorBoton"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Cálculo nutricional",
            style = MaterialTheme.typography.headlineSmall
        )

        Text("Cultivo")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            cultivos.forEach { cultivo ->
                FilterChip(
                    selected = estado.cultivo == cultivo,
                    onClick = { viewModel.onCultivoChange(cultivo) },
                    label = { Text(cultivo) }
                )
            }
        }
        estado.errores.cultivo?.let { mensaje ->
            Text(mensaje, color = MaterialTheme.colorScheme.error)
        }

        OutlinedTextField(
            value = estado.lote,
            onValueChange = viewModel::onLoteChange,
            label = { Text("Lote / Cuartel") },
            isError = estado.errores.lote != null,
            supportingText = {
                estado.errores.lote?.let { mensaje ->
                    Text(mensaje)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = estado.metaRendimiento,
            onValueChange = viewModel::onMetaRendimientoChange,
            label = { Text("Meta de rendimiento (t/há)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            isError = estado.errores.metaRendimiento != null,
            supportingText = {
                estado.errores.metaRendimiento?.let { mensaje ->
                    Text(mensaje)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = estado.eficienciaRiego,
            onValueChange = viewModel::onEficienciaRiegoChange,
            label = { Text("Eficiencia de riego (%)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            isError = estado.errores.eficienciaRiego != null,
            supportingText = {
                estado.errores.eficienciaRiego?.let { mensaje ->
                    Text(mensaje)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Text("Foto foliar (opcional)")
        if (foto != null) {
            Image(
                bitmap = foto,
                contentDescription = "Foto foliar del lote",
                modifier = Modifier.size(160.dp),
                contentScale = ContentScale.Crop
            )
        }
        OutlinedButton(onClick = { pedirFoto() }, modifier = Modifier.fillMaxWidth()) {
            Text(if (estado.fotoUri.isBlank()) "Tomar foto foliar" else "Tomar otra foto")
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        Text("Vista previa en tiempo real")
        Text("Cultivo: ${estado.cultivo}")
        Text("Lote: ${estado.lote}")
        Text("Meta de rendimiento: ${estado.metaRendimiento} t/há")
        Text("Eficiencia de riego: ${estado.eficienciaRiego} %")
        Text("Foto foliar: ${if (estado.fotoUri.isBlank()) "sin foto" else "adjunta"}")

        Button(
            onClick = {
                if (viewModel.validar()) {
                    viewModel.calcular()
                } else {
                    scope.launch {
                        snackbarHostState.showSnackbar("Errores detectados. Revisa los campos.")
                    }
                }
            },
            enabled = !estado.calculando,
            colors = ButtonDefaults.buttonColors(containerColor = colorBoton),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (estado.calculando) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(if (estado.resultado != null) "Recalcular" else "Calcular")
            }
        }

        AnimatedVisibility(visible = estado.resultado != null) {
            estado.resultado?.let { TarjetaResultado(it) }
        }

        SnackbarHost(hostState = snackbarHostState)
    }
}

@Composable
private fun TarjetaResultado(resultado: Nutrientes) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("Demanda estimada (kg/há)", style = MaterialTheme.typography.titleMedium)
            resultado.comoLista().forEach { (nombre, valor) ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(nombre, modifier = Modifier.weight(1f))
                    Text(formatoKg(valor), fontWeight = FontWeight.Bold)
                }
            }
            Text(
                "Estimación referencial, datos de ejemplo. No usar como recomendación.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

// Formato chileno: coma decimal, un decimal.
private fun formatoKg(valor: Double): String =
    String.format(Locale.forLanguageTag("es-CL"), "%.1f", valor)

// La cámara entrega una imagen grande: inSampleSize = 4 la reduce para mostrarla.
private fun cargarFoto(contexto: Context, uri: String): ImageBitmap? {
    val opciones = BitmapFactory.Options().apply { inSampleSize = 4 }
    val bitmap = contexto.contentResolver.openInputStream(Uri.parse(uri))?.use { entrada ->
        BitmapFactory.decodeStream(entrada, null, opciones)
    } ?: return null
    return bitmap.asImageBitmap()
}
