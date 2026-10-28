package cl.duoc.agronutri.navigation

sealed class NavigationEvent {
    data class NavigateTo(val route: Screen) : NavigationEvent()

    // Navega y borra el historial: después del login, "atrás" no vuelve al formulario.
    data class NavigateAndClear(val route: Screen) : NavigationEvent()

    data object PopBackStack : NavigationEvent()
}
