package com.example.gestiondecampeonatos.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.gestiondecampeonatos.data.local.dao.TeamDao
import com.example.gestiondecampeonatos.data.local.entity.TeamEntity

// Room y Database: registra las entidades, el DAO y la versión del esquema.
@Database(entities = [TeamEntity::class], version = 2, exportSchema = true)
abstract class ChampionshipDatabase : RoomDatabase() {
    abstract fun teamDao(): TeamDao

    companion object {
        // Modelo de datos: añade la imagen opcional sin borrar los equipos existentes.
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE teams ADD COLUMN imageFileName TEXT")
            }
        }
    }
}
