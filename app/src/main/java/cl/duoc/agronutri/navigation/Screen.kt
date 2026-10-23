package cl.duoc.agronutri.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home_page")
    data object CalculoNutricional : Screen("calculo_nutricional_page")
}
