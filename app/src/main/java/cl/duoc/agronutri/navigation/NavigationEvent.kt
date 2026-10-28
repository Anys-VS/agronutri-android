package cl.duoc.agronutri.navigation

sealed class NavigationEvent {
    data class NavigateTo(val route: Screen) : NavigationEvent()

    //TODO: Navega y borra el historial: después del login, "atrás" no vuelve al formulario, Revisarlo.
    data class NavigateAndClear(val route: Screen) : NavigationEvent()

    data object PopBackStack : NavigationEvent()
}
