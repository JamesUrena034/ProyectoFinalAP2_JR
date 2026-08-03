package edu.ucne.proyectofinalap2_jr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import edu.ucne.proyectofinalap2_jr.presentation.navigation.MainNavigationDisplay
import edu.ucne.proyectofinalap2_jr.ui.theme.ProyectoFinalAP2_JRTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProyectoFinalAP2_JRTheme {
                MainNavigationDisplay()
            }
        }
    }
}