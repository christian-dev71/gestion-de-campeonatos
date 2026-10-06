package com.example.gestiondecampeonatos.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gestiondecampeonatos.ChampionshipApplication
import com.example.gestiondecampeonatos.R

@Composable
fun HomeRoute(onOpenTeams: () -> Unit, modifier: Modifier = Modifier) {
    if (LocalInspectionMode.current) {
        HomeScreen(HomeState.Content(1), onOpenTeams, {}, modifier)
        return
    }
    val application = LocalContext.current.applicationContext as ChampionshipApplication
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModel.factory(application.teamRepository))
    val state by viewModel.state.observeAsState(HomeState.Loading)
    HomeScreen(state, onOpenTeams, viewModel::retry, modifier)
}

@Composable
fun HomeScreen(
    state: HomeState,
    onOpenTeams: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(stringResource(R.string.nav_home), style = MaterialTheme.typography.headlineMedium)
        Text(stringResource(R.string.home_description), style = MaterialTheme.typography.bodyLarge)
        Card(
            onClick = onOpenTeams,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White, contentColor = Color(0xFF18213D)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Icon(
                    painterResource(R.drawable.ic_teams),
                    contentDescription = null,
                    modifier = Modifier.size(56.dp).background(Color(0xFFEEF0FF), RoundedCornerShape(16.dp)).padding(14.dp),
                    tint = Color(0xFF3F51B5),
                )
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(stringResource(R.string.nav_teams), style = MaterialTheme.typography.titleMedium, color = Color(0xFF626A80))
                    when (state) {
                        HomeState.Loading -> CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color(0xFF3F51B5))
                        HomeState.Error -> {
                            Text(stringResource(R.string.home_count_error), style = MaterialTheme.typography.bodyMedium)
                            TextButton(onClick = onRetry) { Text(stringResource(R.string.retry)) }
                        }
                        is HomeState.Content -> Text(
                            pluralStringResource(R.plurals.team_count, state.teamCount, state.teamCount),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
        MatchOfTheDayCard()
    }
}
