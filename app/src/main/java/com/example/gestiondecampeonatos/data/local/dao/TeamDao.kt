package com.example.gestiondecampeonatos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gestiondecampeonatos.data.local.entity.TeamEntity
import kotlinx.coroutines.flow.Flow

// DAO: define las operaciones de acceso a los equipos almacenados con Room.
@Dao
interface TeamDao {
    // Consultas: el Flow emite el total cuando cambia la tabla, para actualizar Inicio.
    @Query("SELECT COUNT(*) FROM teams")
    fun observeTeamCount(): Flow<Int>

    @Query("SELECT * FROM teams ORDER BY normalizedName ASC")
    fun observeTeams(): Flow<List<TeamEntity>>

    // CRUD: esta inserción cubre Crear; observeTeams cubre Leer.
    // Actualizar y Eliminar todavía están pendientes.
    // El índice único también protege contra inserciones duplicadas simultáneas.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(team: TeamEntity): Long
}
