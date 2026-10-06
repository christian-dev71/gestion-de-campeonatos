package com.example.gestiondecampeonatos.ui.screens.teams

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gestiondecampeonatos.data.local.entity.TeamEntity
import com.example.gestiondecampeonatos.data.repository.AddTeamResult
import com.example.gestiondecampeonatos.data.repository.TeamRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

sealed interface TeamsListState {
    data object Loading : TeamsListState
    data class Content(val teams: List<TeamEntity>) : TeamsListState
    data object Error : TeamsListState
}

enum class TeamFormError { INVALID_NAME, DUPLICATE, SAVE_FAILED }

// MVVM y ViewModel: gestiona el formulario y coordina las operaciones del repositorio.
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class TeamsViewModel(
    private val repository: TeamRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val refresh = MutableStateFlow(0)
    // LiveData y Flujo de datos: Room → Flow → LiveData → interfaz Compose.
    val teams: LiveData<TeamsListState> = refresh.flatMapLatest {
        repository.observeTeams()
            .map<List<TeamEntity>, TeamsListState> { TeamsListState.Content(it) }
            .catch { emit(TeamsListState.Error) }
    }.asLiveData()

    // Gestión de estado: conserva el borrador ante la recreación de la actividad.
    // SavedStateHandle no sustituye a Room ni implementa DataStore.
    val name = savedStateHandle.getStateFlow("teamName", "")
    val imageUri = savedStateHandle.getStateFlow<String?>("teamImageUri", null)
    val showForm = savedStateHandle.getStateFlow("showTeamForm", false)
    var isSaving by mutableStateOf(false)
        private set
    var formError by mutableStateOf<TeamFormError?>(null)
        private set

    fun retry() { refresh.value += 1 }

    fun openForm() { savedStateHandle["showTeamForm"] = true }

    fun updateName(value: String) {
        savedStateHandle["teamName"] = value
        formError = null
    }

    fun updateImage(uri: String?) {
        if (isSaving) return
        savedStateHandle["teamImageUri"] = uri
        formError = null
    }

    fun closeForm() {
        if (isSaving) return
        savedStateHandle["showTeamForm"] = false
        savedStateHandle["teamName"] = ""
        savedStateHandle["teamImageUri"] = null
        formError = null
    }

    // Flujo de la aplicación: Guardar → validar y persistir → cerrar el formulario.
    fun saveTeam() {
        if (isSaving) return
        isSaving = true
        formError = null
        viewModelScope.launch {
            try {
                when (repository.addTeam(name.value, imageUri.value)) {
                    AddTeamResult.SUCCESS -> {
                        savedStateHandle["showTeamForm"] = false
                        savedStateHandle["teamName"] = ""
                        savedStateHandle["teamImageUri"] = null
                    }
                    AddTeamResult.INVALID_NAME -> formError = TeamFormError.INVALID_NAME
                    AddTeamResult.DUPLICATE -> formError = TeamFormError.DUPLICATE
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                formError = TeamFormError.SAVE_FAILED
            } finally {
                isSaving = false
            }
        }
    }

    companion object {
        fun factory(repository: TeamRepository) = viewModelFactory {
            initializer { TeamsViewModel(repository, createSavedStateHandle()) }
        }
    }
}
