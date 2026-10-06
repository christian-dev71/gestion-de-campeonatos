package com.example.gestiondecampeonatos.data.repository

import com.example.gestiondecampeonatos.data.local.dao.TeamDao
import com.example.gestiondecampeonatos.data.local.entity.TeamEntity
import java.util.Locale
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import com.example.gestiondecampeonatos.data.local.image.TeamImageStore

enum class AddTeamResult { SUCCESS, INVALID_NAME, DUPLICATE }

// Repository: centraliza validación, consultas y almacenamiento de equipos e imágenes.
// La interfaz Compose no ejecuta SQL ni escribe archivos directamente.
class TeamRepository(private val teamDao: TeamDao, private val imageStore: TeamImageStore) {
    fun observeTeams(): Flow<List<TeamEntity>> = teamDao.observeTeams()
    fun observeTeamCount(): Flow<Int> = teamDao.observeTeamCount()

    suspend fun addTeam(rawName: String, imageUri: String? = null): AddTeamResult {
        val name = rawName.trim().replace(Regex("\\s+"), " ")
        if (name.isBlank() || name.length > MAX_NAME_LENGTH) return AddTeamResult.INVALID_NAME
        // Completa el guardado del archivo y del registro aunque se cierre la pantalla.
        // Si la inserción falla, finally elimina la copia de imagen que no se utilizó.
        return withContext(Dispatchers.IO + NonCancellable) {
            val imageFileName = imageUri?.let(imageStore::importImage)
            var inserted = false
            try {
                val id = teamDao.insert(
                    TeamEntity(name = name, normalizedName = name.lowercase(Locale.ROOT), imageFileName = imageFileName),
                )
                inserted = id != -1L
                if (inserted) AddTeamResult.SUCCESS else AddTeamResult.DUPLICATE
            } finally {
                if (!inserted && imageFileName != null) imageStore.deleteImage(imageFileName)
            }
        }
    }

    companion object {
        const val MAX_NAME_LENGTH = 60
    }
}
