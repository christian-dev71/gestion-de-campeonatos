package com.example.gestiondecampeonatos.ui.screens.teams

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import com.example.gestiondecampeonatos.data.local.image.LocalTeamImageStore
import com.example.gestiondecampeonatos.ui.components.TeamAvatar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gestiondecampeonatos.ChampionshipApplication
import com.example.gestiondecampeonatos.R

@Composable
fun TeamsRoute(modifier: Modifier = Modifier) {
    // MVVM: conecta el ViewModel con la pantalla y observa sus estados.
    val application = LocalContext.current.applicationContext as ChampionshipApplication
    val viewModel: TeamsViewModel = viewModel(
        factory = TeamsViewModel.factory(application.teamRepository),
    )
    val state by viewModel.teams.observeAsState(TeamsListState.Loading)
    val name by viewModel.name.collectAsStateWithLifecycle()
    val showForm by viewModel.showForm.collectAsStateWithLifecycle()
    val imageUri by viewModel.imageUri.collectAsStateWithLifecycle()
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            try {
                application.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (_: SecurityException) {
                // Some providers only grant temporary access; saving still imports a private copy.
            }
            viewModel.updateImage(uri.toString())
        }
    }

    TeamsScreen(state, viewModel::openForm, viewModel::retry, modifier)
    if (showForm) {
        AddTeamDialog(
            name = name,
            imageUri = imageUri,
            onPickImage = { imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
            onRemoveImage = { viewModel.updateImage(null) },
            isSaving = viewModel.isSaving,
            error = viewModel.formError,
            onNameChange = viewModel::updateName,
            onSave = viewModel::saveTeam,
            onDismiss = viewModel::closeForm,
        )
    }
}

@Composable
fun TeamsScreen(
    state: TeamsListState,
    onAddTeam: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    // Listas: LazyColumn compone los elementos necesarios; cada equipo usa su id como clave.
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Text(stringResource(R.string.nav_teams), style = MaterialTheme.typography.headlineMedium)
        }
        item {
            Button(
                onClick = onAddTeam,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5), contentColor = Color.White),
            ) { Text(stringResource(R.string.add_team)) }
        }
        when (state) {
            TeamsListState.Loading -> item { CircularProgressIndicator() }
            TeamsListState.Error -> item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(R.string.teams_load_error))
                    TextButton(onClick = onRetry) { Text(stringResource(R.string.retry)) }
                }
            }
            is TeamsListState.Content -> {
                if (state.teams.isEmpty()) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(stringResource(R.string.teams_empty_title), style = MaterialTheme.typography.titleMedium)
                            Text(stringResource(R.string.teams_empty_description))
                        }
                    }
                }
                items(state.teams, key = { it.id }) { team ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            TeamAvatar(team.imageFileName?.let { LocalTeamImageStore.imageUri(context, it).toString() })
                            Text(
                                text = team.name,
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                    }
                }
            }
        }
    }
}
