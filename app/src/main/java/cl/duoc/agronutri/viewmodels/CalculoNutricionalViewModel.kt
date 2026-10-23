package cl.duoc.agronutri.viewmodels

import androidx.lifecycle.ViewModel
import cl.duoc.agronutri.model.CalculoNutricionalErrores
import cl.duoc.agronutri.model.CalculoNutricionalUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CalculoNutricionalViewModel : ViewModel() {
    private val _estado = MutableStateFlow(CalculoNutricionalUiState())
    val estado: StateFlow<CalculoNutricionalUiState> = _estado.asStateFlow()

    fun onCultivoChange(valor: String) {
        _estado.update {
            it.copy(cultivo = valor, errores = it.errores.copy(cultivo = null))
        }
    }

    fun onLoteChange(valor: String) {
        _estado.update {
            it.copy(lote = valor, errores = it.errores.copy(lote = null))
        }
    }

    fun onMetaRendimientoChange(valor: String) {
        _estado.update {
            it.copy(metaRendimiento = valor, errores = it.errores.copy(metaRendimiento = null))
        }
    }

    fun onEficienciaRiegoChange(valor: String) {
        _estado.update {
            it.copy(eficienciaRiego = valor, errores = it.errores.copy(eficienciaRiego = null))
        }
    }

    // TODO confirmar con el cliente: rangos validos de rendimiento y eficiencia
    fun validar(): Boolean {
        val actual = _estado.value
        // En Chile se escribe "6,4": se cambia la coma por punto antes de convertir
        val meta = actual.metaRendimiento.replace(',', '.').toDoubleOrNull()
        val eficiencia = actual.eficienciaRiego.replace(',', '.').toDoubleOrNull()

        val errores = CalculoNutricionalErrores(
            cultivo = if (actual.cultivo.isBlank()) "Selecciona un cultivo" else null,
            lote = if (actual.lote.isBlank()) "Indica el lote o cuartel" else null,
            metaRendimiento = when {
                actual.metaRendimiento.isBlank() -> "Campo obligatorio"
                meta == null -> "Ingresa un número válido"
                meta <= 0.0 -> "Debe ser mayor a 0"
                else -> null
            },
            eficienciaRiego = when {
                actual.eficienciaRiego.isBlank() -> "Campo obligatorio"
                eficiencia == null -> "Ingresa un número válido"
                eficiencia <= 0.0 || eficiencia > 100.0 -> "Debe estar entre 1 y 100"
                else -> null
            }
        )
        _estado.update { it.copy(errores = errores) }
        return errores == CalculoNutricionalErrores()
    }
}
