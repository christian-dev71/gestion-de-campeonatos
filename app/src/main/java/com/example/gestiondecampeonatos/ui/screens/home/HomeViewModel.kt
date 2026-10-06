package com.example.gestiondecampeonatos.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gestiondecampeonatos.data.repository.TeamRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

sealed interface HomeState {
    data object Loading : HomeState
    data class Content(val teamCount: Int) : HomeState
    data object Error : HomeState
}

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(repository: TeamRepository) : ViewModel() {
    private val refresh = MutableStateFlow(0)
    // Consultas y LiveData: observa COUNT(*) y publica el total como LiveData para la tarjeta de Inicio.
    val state = refresh.flatMapLatest {
        repository.observeTeamCount()
            .map<Int, HomeState> { HomeState.Content(it) }
            .onStart { emit(HomeState.Loading) }
            .catch { emit(HomeState.Error) }
    }.asLiveData()

    fun retry() { refresh.value += 1 }

    companion object {
        fun factory(repository: TeamRepository) = viewModelFactory {
            initializer { HomeViewModel(repository) }
        }
    }
}
