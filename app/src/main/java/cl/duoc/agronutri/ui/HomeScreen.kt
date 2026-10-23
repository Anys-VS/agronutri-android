package cl.duoc.agronutri.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.agronutri.navigation.Screen
import cl.duoc.agronutri.ui.theme.AgroNutriTheme
import cl.duoc.agronutri.viewmodels.MainViewModel

// Version provisional: despues se reemplaza por fondo + logo, como en la guia
@Composable
fun HomeScreen(viewModel: MainViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("AgroNutri")
        Button(onClick = { viewModel.navigateTo(Screen.CalculoNutricional) }) {
            Text("Cálculo nutricional")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    AgroNutriTheme {
        HomeScreen(viewModel = viewModel())
    }
}
