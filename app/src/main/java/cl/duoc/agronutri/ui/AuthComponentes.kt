package cl.duoc.agronutri.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.agronutri.R

// Colores tomados de la imagen de referencia de NutriExpert.
object ColoresMarca {
    val Verde = Color(0xFF1B5E20)
    val HojaRelleno = Color(0xFFDCEBDC)
    val Sol = Color(0xFFF2E7BE)
    val Gota = Color(0xFF15566B)
    val Suelo = Color(0xFF6D7569)
    val Titulo = Color(0xFF111111)
    val Subtitulo = Color(0xFF424242)
    val ChipFondo = Color(0xFFF5F5F0)
    val ChipBorde = Color(0xFF626960)
    val TextoLogo = Color(0xFF153E45) // color del nombre en el SVG del logo
}

@Composable
fun EncabezadoMarca(modifier: Modifier = Modifier, onVolver: (() -> Unit)? = null) {
    Box(
        modifier
            .fillMaxWidth()
            .background(ColoresMarca.Verde)
            .statusBarsPadding()
            .padding(vertical = 18.dp)
    ) {
        if (onVolver != null) {
            TextButton(onClick = onVolver, modifier = Modifier.align(Alignment.CenterStart)) {
                Text("← Volver", color = Color.White)
            }
        }
        Text(
            text = stringResource(R.string.nombre_marca),
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun LogoApp(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.logo),
        contentDescription = "Logo NutriExpert",
        modifier = modifier
    )
}

private fun DrawScope.dibujarHoja(w: Float, h: Float, trazo: Color, relleno: Color, g: Float) {
    val hoja = Path().apply {
        moveTo(w * 0.5f, h)
        cubicTo(-w * 0.1f, h * 0.75f, w * 0.1f, h * 0.15f, w * 0.5f, 0f)
        cubicTo(w * 0.9f, h * 0.15f, w * 1.1f, h * 0.75f, w * 0.5f, h)
        close()
    }
    drawPath(hoja, relleno)
    drawPath(hoja, trazo, style = Stroke(width = g, cap = StrokeCap.Round, join = StrokeJoin.Round))
    // Nervio central y una nervadura lateral
    drawLine(trazo, Offset(w * 0.5f, h), Offset(w * 0.5f, h * 0.22f), strokeWidth = g, cap = StrokeCap.Round)
    drawLine(trazo, Offset(w * 0.5f, h * 0.62f), Offset(w * 0.8f, h * 0.42f), strokeWidth = g, cap = StrokeCap.Round)
}

/** imagen central sol, hoja, gota y suelo. efecto de balanceo que mueve la hoja en grados. */
@Composable
fun IlustracionBienvenida(modifier: Modifier = Modifier, balanceo: Float = 0f) {
    Canvas(modifier) {
        val W = size.width
        val H = size.height
        val base = minOf(W, H * 1.3f)       // escala para que no se deforme en pantallas anchas, responsivo
        val cx = W / 2f
        val g = 3.dp.toPx()

        // Sol
        drawCircle(ColoresMarca.Sol, radius = base * 0.13f, center = Offset(cx - base * 0.27f, H * 0.2f))

        // Suelo
        val suelo = Path().apply {
            moveTo(cx - base * 0.48f, H * 0.96f)
            cubicTo(cx - base * 0.2f, H * 0.84f, cx + base * 0.2f, H * 0.84f, cx + base * 0.48f, H * 0.94f)
        }
        drawPath(suelo, ColoresMarca.Suelo, style = Stroke(width = g, cap = StrokeCap.Round))

        // Hoja
        val hw = base * 0.36f
        val hh = H * 0.8f
        val hojaCentro = Offset(cx - base * 0.04f, H * 0.5f)
        rotate(35f + balanceo, pivot = hojaCentro) {
            translate(hojaCentro.x - hw / 2f, hojaCentro.y - hh / 2f) {
                dibujarHoja(hw, hh, ColoresMarca.Verde, ColoresMarca.HojaRelleno, g)
            }
        }

        // Gota
        val r = base * 0.1f
        val gx = cx + base * 0.22f
        val arriba = H * 0.36f
        val abajo = H * 0.8f
        val gota = Path().apply {
            moveTo(gx, arriba)
            cubicTo(gx, arriba, gx + r, arriba + (abajo - arriba - r) * 0.55f, gx + r, abajo - r)
            arcTo(Rect(gx - r, abajo - 2 * r, gx + r, abajo), 0f, 180f, false)
            cubicTo(gx - r, arriba + (abajo - arriba - r) * 0.55f, gx, arriba, gx, arriba)
            close()
        }
        drawPath(gota, Color.White)
        drawPath(gota, ColoresMarca.Gota, style = Stroke(width = g, join = StrokeJoin.Round))
        drawArc(
            color = ColoresMarca.Gota,
            startAngle = 100f,
            sweepAngle = 70f,
            useCenter = false,
            topLeft = Offset(gx - r * 0.6f, abajo - r * 1.6f),
            size = Size(r * 1.2f, r * 1.2f),
            style = Stroke(width = g, cap = StrokeCap.Round)
        )
    }
}

/** Ícono de wifi tachado. */
@Composable
private fun IconoSinConexion(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val g = 1.8.dp.toPx()
        val centro = Offset(w / 2f, h * 0.85f)
        listOf(0.45f, 0.3f, 0.15f).forEach { factor ->
            val radio = w * factor
            drawArc(
                color = color,
                startAngle = 225f,
                sweepAngle = 90f,
                useCenter = false,
                topLeft = Offset(centro.x - radio, centro.y - radio),
                size = Size(radio * 2, radio * 2),
                style = Stroke(width = g, cap = StrokeCap.Round)
            )
        }
        drawCircle(color, radius = g, center = centro)
        drawLine(color, Offset(w * 0.12f, h * 0.08f), Offset(w * 0.88f, h * 0.92f), strokeWidth = g, cap = StrokeCap.Round)
    }
}

@Composable
fun ChipSinConexion(modifier: Modifier = Modifier, texto: String = "Funciona sin conexión") {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = ColoresMarca.ChipFondo,
        border = BorderStroke(1.dp, ColoresMarca.ChipBorde)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconoSinConexion(Modifier.size(18.dp), color = ColoresMarca.Titulo)
            Spacer(Modifier.width(8.dp))
            Text(
                texto,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = ColoresMarca.Titulo
            )
        }
    }
}

/** Botón verde relleno. Mientras cargando es true muestra un indicador animado. */
@Composable
fun BotonPrincipal(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cargando: Boolean = false
) {
    Button(
        onClick = onClick,
        enabled = !cargando,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ColoresMarca.Verde,
            disabledContainerColor = ColoresMarca.Verde.copy(alpha = 0.75f)
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        Crossfade(targetState = cargando, label = "boton_carga") { estaCargando ->
            if (estaCargando) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = Color.White,
                    strokeWidth = 2.5.dp
                )
            } else {
                Text(texto, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

/** Botón con borde verde y fondo blanco. */
@Composable
fun BotonSecundario(texto: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.5.dp, ColoresMarca.Verde)
    ) {
        Text(texto, color = ColoresMarca.Verde, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}

/** Campo de texto con el estilo de la marca y mensaje de error debajo. */
@Composable
fun CampoTexto(
    valor: String,
    onCambio: (String) -> Unit,
    etiqueta: String,
    error: String?,
    modifier: Modifier = Modifier,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    esClave: Boolean = false,
    accionTeclado: ImeAction = ImeAction.Next,
    habilitado: Boolean = true,
    ayuda: String? = null
) {
    var verClave by rememberSaveable { mutableStateOf(false) }
    val textoAbajo = error ?: ayuda

    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(etiqueta) },
        isError = error != null,
        supportingText = if (textoAbajo != null) {
            { Text(textoAbajo) }
        } else null,
        singleLine = true,
        enabled = habilitado,
        keyboardOptions = KeyboardOptions(keyboardType = tipoTeclado, imeAction = accionTeclado),
        visualTransformation = if (esClave && !verClave) PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = if (esClave) {
            {
                TextButton(onClick = { verClave = !verClave }) {
                    Text(if (verClave) "Ocultar" else "Ver", color = ColoresMarca.Verde)
                }
            }
        } else null,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ColoresMarca.Verde,
            focusedLabelColor = ColoresMarca.Verde,
            cursorColor = ColoresMarca.Verde
        ),
        modifier = modifier.fillMaxWidth()
    )
}
