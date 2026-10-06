package com.example.gestiondecampeonatos.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// Entidades y Entity: cada instancia representa una fila de la tabla teams.
// El índice único impide guardar dos equipos con el mismo nombre normalizado.
@Entity(tableName = "teams", indices = [Index(value = ["normalizedName"], unique = true)])
data class TeamEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val normalizedName: String,
    val imageFileName: String? = null,
)
