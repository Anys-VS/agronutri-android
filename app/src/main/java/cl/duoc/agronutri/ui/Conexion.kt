package cl.duoc.agronutri.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay

// Pregunta al sistema si el teléfono tiene internet (wifi o datos) en este momento.
// Necesita el permiso ACCESS_NETWORK_STATE del AndroidManifest.
fun hayConexion(contexto: Context): Boolean {
    val conectividad = contexto.getSystemService(ConnectivityManager::class.java)
    val red = conectividad.activeNetwork ?: return false
    val capacidades = conectividad.getNetworkCapabilities(red) ?: return false
    return capacidades.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

// Revisa la conexión cada 2 segundos y devuelve el último valor.
// Así el indicador cambia solo al activar o quitar el modo avión.
@Composable
fun recordarConexion(): Boolean {
    val contexto = LocalContext.current
    var conectado by remember { mutableStateOf(hayConexion(contexto)) }
    LaunchedEffect(Unit) {
        while (true) {
            conectado = hayConexion(contexto)
            delay(2000)
        }
    }
    return conectado
}
