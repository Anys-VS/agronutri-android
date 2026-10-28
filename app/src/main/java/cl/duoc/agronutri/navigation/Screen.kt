package cl.duoc.agronutri.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash_page")
    data object Bienvenida : Screen("bienvenida_page")
    data object Login : Screen("login_page")
    data object Registro : Screen("registro_page")
    data object Home : Screen("home_page")
    data object CalculoNutricional : Screen("calculo_nutricional_page")
    data object Historial : Screen("historial_page")
}
