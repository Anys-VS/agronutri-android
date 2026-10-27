package cl.duoc.agronutri.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.agronutri.viewmodels.CalculoNutricionalViewModel
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
    val cultivos = listOf("Palto", "Cítricos", "Tomate")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
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
                    scope.launch {
                        snackbarHostState.showSnackbar("Datos válidos")
                    }
                } else {
                    scope.launch {
                        snackbarHostState.showSnackbar("Errores detectados. Revisa los campos.")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Calcular")
        }

        SnackbarHost(hostState = snackbarHostState)
    }
}

// La cámara entrega una imagen grande: inSampleSize = 4 la reduce para mostrarla.
private fun cargarFoto(contexto: Context, uri: String): ImageBitmap? {
    val opciones = BitmapFactory.Options().apply { inSampleSize = 4 }
    val bitmap = contexto.contentResolver.openInputStream(Uri.parse(uri))?.use { entrada ->
        BitmapFactory.decodeStream(entrada, null, opciones)
    } ?: return null
    return bitmap.asImageBitmap()
}
