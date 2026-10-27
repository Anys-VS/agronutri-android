package cl.duoc.agronutri

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.duoc.agronutri.model.HomeUiState
import cl.duoc.agronutri.navigation.NavigationEvent
import cl.duoc.agronutri.navigation.Screen
import cl.duoc.agronutri.ui.CalculoNutricionalScreen
import cl.duoc.agronutri.ui.HomeScreen
import cl.duoc.agronutri.ui.theme.AgroNutriTheme
import cl.duoc.agronutri.viewmodels.MainViewModel
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AgroNutriTheme {
                val viewModel: MainViewModel = viewModel()
                val navController = rememberNavController()

                LaunchedEffect(Unit) {
                    viewModel.navigationEvents.collectLatest { event ->
                        when (event) {
                            is NavigationEvent.NavigateTo ->
                                navController.navigate(event.route.route)
                            NavigationEvent.PopBackStack ->
                                navController.popBackStack()
                        }
                    }
                }

                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route
                ) {
                    composable(Screen.Home.route) {
                        // TODO al unir con dfm: nombre y rol desde authViewModel.sesion,
                        // onCerrarSesion con navigateAndClear(Screen.Login).
                        // TODO bloque 2: pendientes y últimos registros desde Room.
                        HomeScreen(
                            state = HomeUiState(nombre = "Usuario Demo", rol = "AGRICULTOR"),
                            onNuevoRegistro = { viewModel.navigateTo(Screen.CalculoNutricional) }
                        )
                    }
                    composable(Screen.CalculoNutricional.route) {
                        CalculoNutricionalScreen()
                    }
                }
            }
        }
    }
}
