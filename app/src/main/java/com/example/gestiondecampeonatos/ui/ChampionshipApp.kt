package com.example.gestiondecampeonatos.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.gestiondecampeonatos.R
import com.example.gestiondecampeonatos.ui.components.ChampionshipBottomBar
import com.example.gestiondecampeonatos.ui.navigation.TopLevelDestination
import com.example.gestiondecampeonatos.ui.screens.SectionScreen
import com.example.gestiondecampeonatos.ui.screens.teams.TeamsRoute
import com.example.gestiondecampeonatos.ui.screens.home.HomeRoute
import com.example.gestiondecampeonatos.ui.theme.GestiondeCampeonatosTheme

@Composable
fun ChampionshipApp(modifier: Modifier = Modifier) {
    // Navegación: cambia entre las cuatro secciones de la barra inferior.
    // rememberSaveable conserva la selección; no se utiliza Navigation Compose/NavHost.
    var selectedDestination by rememberSaveable { mutableStateOf(TopLevelDestination.HOME) }

    BackHandler(enabled = selectedDestination != TopLevelDestination.HOME) {
        selectedDestination = TopLevelDestination.HOME
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            ChampionshipBottomBar(
                selectedDestination = selectedDestination,
                onDestinationSelected = { selectedDestination = it },
            )
        },
    ) { innerPadding ->
        if (selectedDestination == TopLevelDestination.HOME) {
            HomeRoute(
                onOpenTeams = { selectedDestination = TopLevelDestination.TEAMS },
                modifier = Modifier.padding(innerPadding),
            )
            return@Scaffold
        }
        if (selectedDestination == TopLevelDestination.TEAMS) {
            TeamsRoute(modifier = Modifier.padding(innerPadding))
            return@Scaffold
        }
        val descriptionRes = when (selectedDestination) {
            TopLevelDestination.HOME -> R.string.home_description
            TopLevelDestination.TEAMS -> R.string.teams_description
            TopLevelDestination.MATCHES -> R.string.matches_description
            TopLevelDestination.STANDINGS -> R.string.standings_description
        }
        SectionScreen(
            titleRes = selectedDestination.labelRes,
            descriptionRes = descriptionRes,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ChampionshipAppPreview() {
    GestiondeCampeonatosTheme { ChampionshipApp() }
}
