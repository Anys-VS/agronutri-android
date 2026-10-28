package cl.duoc.agronutri.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.agronutri.navigation.NavigationEvent
import cl.duoc.agronutri.navigation.Screen
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    fun navigateTo(screen: Screen) = emitir(NavigationEvent.NavigateTo(screen))

    fun navigateAndClear(screen: Screen) = emitir(NavigationEvent.NavigateAndClear(screen))

    fun popBackStack() = emitir(NavigationEvent.PopBackStack)

    private fun emitir(evento: NavigationEvent) {
        viewModelScope.launch { _navigationEvents.emit(evento) }
    }
}
