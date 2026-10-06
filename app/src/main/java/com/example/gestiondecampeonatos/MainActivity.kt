package com.example.gestiondecampeonatos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.gestiondecampeonatos.ui.ChampionshipApp
import com.example.gestiondecampeonatos.ui.theme.GestiondeCampeonatosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GestiondeCampeonatosTheme {
                ChampionshipApp()
            }
        }
    }
}
