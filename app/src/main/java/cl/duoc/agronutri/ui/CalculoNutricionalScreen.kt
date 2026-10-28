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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
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

@Composable
fun CalculoNutricionalScreen(
    usuarioId: Long,
    onGuardado: () -> Unit,
    viewModel: CalculoNutricionalViewModel = viewModel(),
    onVolver: (() -> Unit)? = null
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
            else -> ColoresMarca.Verde
        },
        label = "colorBoton"
    )

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
                    .verticalScroll(scrollState)
                    .navigationBarsPadding()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column {
                    Text(
                        "Registro de terreno",
                        color = ColoresMarca.Titulo,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Datos del lote y cálculo nutricional",
                        color = ColoresMarca.Subtitulo,
                        fontSize = 15.sp
                    )
                }

                Text("Cultivo", color = ColoresMarca.Subtitulo, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    cultivos.forEach { cultivo ->
                        FilterChip(
                            selected = estado.cultivo == cultivo,
                            onClick = { viewModel.onCultivoChange(cultivo) },
                            label = { Text(cultivo) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ColoresMarca.Verde,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
                estado.errores.cultivo?.let { mensaje ->
                    Text(mensaje, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }

                CampoTexto(
                    valor = estado.lote,
                    onCambio = viewModel::onLoteChange,
                    etiqueta = "Lote / Cuartel",
                    error = estado.errores.lote
                )
                CampoTexto(
                    valor = estado.metaRendimiento,
                    onCambio = viewModel::onMetaRendimientoChange,
                    etiqueta = "Meta de rendimiento (t/há)",
                    error = estado.errores.metaRendimiento,
                    tipoTeclado = KeyboardType.Decimal
                )
                CampoTexto(
                    valor = estado.eficienciaRiego,
                    onCambio = viewModel::onEficienciaRiegoChange,
                    etiqueta = "Eficiencia de riego (%)",
                    error = estado.errores.eficienciaRiego,
                    tipoTeclado = KeyboardType.Decimal,
                    accionTeclado = ImeAction.Done
                )

                Text("Foto foliar (opcional)", color = ColoresMarca.Subtitulo, fontWeight = FontWeight.SemiBold)
                if (foto != null) {
                    Image(
                        bitmap = foto,
                        contentDescription = "Foto foliar del lote",
                        modifier = Modifier
                            .size(160.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                BotonSecundario(
                    texto = if (estado.fotoUri.isBlank()) "Tomar foto foliar" else "Tomar otra foto",
                    onClick = { pedirFoto() }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text("Vista previa en tiempo real", color = ColoresMarca.Titulo, fontWeight = FontWeight.Bold)
                Column {
                    Text("Cultivo: ${estado.cultivo}", color = ColoresMarca.Subtitulo)
                    Text("Lote: ${estado.lote}", color = ColoresMarca.Subtitulo)
                    Text("Meta de rendimiento: ${estado.metaRendimiento} t/há", color = ColoresMarca.Subtitulo)
                    Text("Eficiencia de riego: ${estado.eficienciaRiego} %", color = ColoresMarca.Subtitulo)
                    Text(
                        "Foto foliar: ${if (estado.fotoUri.isBlank()) "sin foto" else "adjunta"}",
                        color = ColoresMarca.Subtitulo
                    )
                }

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
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorBoton),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    if (estado.calculando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            if (estado.resultado != null) "Recalcular" else "Calcular",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }

                AnimatedVisibility(visible = estado.resultado != null) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        estado.resultado?.let { TarjetaResultado(it) }
                        BotonPrincipal(
                            texto = "Guardar registro",
                            onClick = { viewModel.guardar(usuarioId, onGuardado) },
                            cargando = estado.guardando
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaResultado(resultado: Nutrientes) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = ColoresMarca.ChipFondo)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                "Demanda estimada (kg/há)",
                color = ColoresMarca.Titulo,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            resultado.comoLista().forEach { (nombre, valor) ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(nombre, color = ColoresMarca.Subtitulo, modifier = Modifier.weight(1f))
                    Text(formatoKg(valor), color = ColoresMarca.Verde, fontWeight = FontWeight.Bold)
                }
            }
            Text(
                "Estimación referencial, datos de ejemplo. No usar como recomendación.",
                color = ColoresMarca.Subtitulo,
                fontSize = 12.sp,
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
