package cl.duoc.agronutri.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
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

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        Text("Vista previa en tiempo real")
        Text("Cultivo: ${estado.cultivo}")
        Text("Lote: ${estado.lote}")
        Text("Meta de rendimiento: ${estado.metaRendimiento} t/há")
        Text("Eficiencia de riego: ${estado.eficienciaRiego} %")

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
