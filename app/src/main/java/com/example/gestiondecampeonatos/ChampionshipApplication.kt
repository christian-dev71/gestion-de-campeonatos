package com.example.gestiondecampeonatos

import android.app.Application
import androidx.room.Room
import com.example.gestiondecampeonatos.data.local.ChampionshipDatabase
import com.example.gestiondecampeonatos.data.repository.TeamRepository
import com.example.gestiondecampeonatos.data.local.image.LocalTeamImageStore

class ChampionshipApplication : Application() {
    private val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            ChampionshipDatabase::class.java,
            "championship.db",
        ).addMigrations(ChampionshipDatabase.MIGRATION_1_2).build()
    }

    val teamRepository by lazy { TeamRepository(database.teamDao(), LocalTeamImageStore(this)) }
}
