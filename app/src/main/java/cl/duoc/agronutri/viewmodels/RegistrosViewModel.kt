package cl.duoc.agronutri.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.agronutri.data.AppDatabase
import cl.duoc.agronutri.data.RegistroTerreno
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Lista de registros de terreno guardados en el teléfono. La usan el Home y el Historial.
class RegistrosViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.obtener(application).registroTerrenoDao()

    private val _registros = MutableStateFlow<List<RegistroTerreno>>(emptyList())
    val registros: StateFlow<List<RegistroTerreno>> = _registros.asStateFlow()

    init {
        viewModelScope.launch {
            dao.observar().collect { lista ->
                _registros.value = lista
            }
        }
    }

    fun borrar(id: Long) {
        viewModelScope.launch {
            dao.borrar(id)
        }
    }
}
