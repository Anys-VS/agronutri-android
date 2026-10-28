package cl.duoc.agronutri

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.duoc.agronutri.model.HomeUiState
import cl.duoc.agronutri.navigation.NavigationEvent
import cl.duoc.agronutri.navigation.Screen
import cl.duoc.agronutri.ui.BienvenidaScreen
import cl.duoc.agronutri.ui.CalculoNutricionalScreen
import cl.duoc.agronutri.ui.HomeScreen
import cl.duoc.agronutri.ui.LoginScreen
import cl.duoc.agronutri.ui.RegistroScreen
import cl.duoc.agronutri.ui.SplashScreen
import cl.duoc.agronutri.ui.theme.AgroNutriTheme
import cl.duoc.agronutri.viewmodels.AuthViewModel
import cl.duoc.agronutri.viewmodels.MainViewModel
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AgroNutriTheme {
                val viewModel: MainViewModel = viewModel()
                val authViewModel: AuthViewModel = viewModel()
                val navController = rememberNavController()

                LaunchedEffect(Unit) {
                    viewModel.navigationEvents.collectLatest { event ->
                        when (event) {
                            is NavigationEvent.NavigateTo ->
                                navController.navigate(event.route.route) {
                                    launchSingleTop = true
                                }
                            is NavigationEvent.NavigateAndClear ->
                                navController.navigate(event.route.route) {
                                    popUpTo(navController.graph.id) { inclusive = true }
                                    launchSingleTop = true
                                }
                            NavigationEvent.PopBackStack ->
                                navController.popBackStack()
                        }
                    }
                }

                NavHost(
                    navController = navController,
                    startDestination = Screen.Splash.route
                ) {
                    composable(Screen.Splash.route) {
                        // Se borra del historial: "atrás" desde la bienvenida no vuelve al splash.
                        SplashScreen(onTerminar = { viewModel.navigateAndClear(Screen.Bienvenida) })
                    }
                    composable(Screen.Bienvenida.route) {
                        BienvenidaScreen(
                            onIniciarSesion = { viewModel.navigateTo(Screen.Login) },
                            onRegistrarme = { viewModel.navigateTo(Screen.Registro) }
                        )
                    }
                    composable(Screen.Login.route) {
                        LoginScreen(
                            authViewModel = authViewModel,
                            onVolver = { viewModel.popBackStack() },
                            onIrARegistro = { viewModel.navigateTo(Screen.Registro) },
                            onLoginExitoso = { viewModel.navigateAndClear(Screen.Home) }
                        )
                    }
                    composable(Screen.Registro.route) {
                        RegistroScreen(
                            authViewModel = authViewModel,
                            onVolver = { viewModel.popBackStack() },
                            onIrALogin = { viewModel.navigateTo(Screen.Login) },
                            onRegistroExitoso = { viewModel.navigateAndClear(Screen.Home) }
                        )
                    }
                    composable(Screen.Home.route) {
                        val sesion by authViewModel.sesion.collectAsState()
                        // TODO bloque 2: pendientes y últimos registros desde Room.
                        HomeScreen(
                            state = HomeUiState(
                                nombre = sesion?.nombre.orEmpty(),
                                rol = sesion?.rol.orEmpty()
                            ),
                            onNuevoRegistro = { viewModel.navigateTo(Screen.CalculoNutricional) },
                            onCerrarSesion = {
                                authViewModel.cerrarSesion()
                                // A la bienvenida y no al login: desde ahí "atrás" cierra la app.
                                viewModel.navigateAndClear(Screen.Bienvenida)
                            }
                        )
                    }
                    composable(Screen.CalculoNutricional.route) {
                        CalculoNutricionalScreen(onVolver = { viewModel.popBackStack() })
                    }
                }
            }
        }
    }
}
