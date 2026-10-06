package com.example.gestiondecampeonatos.data.local

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.example.gestiondecampeonatos.data.local.entity.TeamEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.produceIn
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.UUID
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.example.gestiondecampeonatos.data.local.image.LocalTeamImageStore
import com.example.gestiondecampeonatos.data.local.image.decodeTeamImage
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import java.io.File

class TeamDatabaseTest {
    @Test
    fun teamCountUpdatesAfterEachInsertion() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val database = Room.inMemoryDatabaseBuilder(context, ChampionshipDatabase::class.java).build()
        val counts = database.teamDao().observeTeamCount().produceIn(this)
        try {
            withTimeout(5_000) {
                assertEquals(0, counts.receive())
                database.teamDao().insert(TeamEntity(name = "Norte", normalizedName = "norte"))
                assertEquals(1, counts.receive())
                database.teamDao().insert(TeamEntity(name = "Sur", normalizedName = "sur"))
                assertEquals(2, counts.receive())
            }
        } finally {
            counts.cancel()
            database.close()
        }
    }

    @Test
    fun migrationPreservesExistingTeamsWithoutImage() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "migration-test-${UUID.randomUUID()}.db"
        context.openOrCreateDatabase(name, Context.MODE_PRIVATE, null).use { old ->
            old.execSQL("CREATE TABLE teams (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name TEXT NOT NULL, normalizedName TEXT NOT NULL)")
            old.execSQL("CREATE UNIQUE INDEX index_teams_normalizedName ON teams(normalizedName)")
            old.execSQL("INSERT INTO teams(name, normalizedName) VALUES ('Club Norte', 'club norte')")
            old.version = 1
        }
        val database = Room.databaseBuilder(context, ChampionshipDatabase::class.java, name)
            .addMigrations(ChampionshipDatabase.MIGRATION_1_2).build()
        try {
            val team = database.teamDao().observeTeams().first().single()
            assertEquals("Club Norte", team.name)
            assertNull(team.imageFileName)
            database.teamDao().insert(TeamEntity(name = "Sur", normalizedName = "sur", imageFileName = "sur.png"))
            assertEquals("sur.png", database.teamDao().observeTeams().first().last().imageFileName)
        } finally {
            database.close()
            context.deleteDatabase(name)
        }
    }

    @Test
    fun importedImageIsResizedAndSurvivesOriginalDeletion() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val original = File(context.cacheDir, "image-test-${UUID.randomUUID()}.png")
        val bitmap = Bitmap.createBitmap(1200, 600, Bitmap.Config.ARGB_8888)
        original.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        bitmap.recycle()
        val store = LocalTeamImageStore(context)
        var imported: String? = null
        try {
            val fileName = store.importImage(Uri.fromFile(original).toString())
            imported = fileName
            assertTrue(original.delete())
            val saved = decodeTeamImage(context, LocalTeamImageStore.imageUri(context, fileName), 512)
            assertEquals(512, saved.width)
            assertEquals(256, saved.height)
            saved.recycle()
        } finally {
            original.delete()
            imported?.let(store::deleteImage)
        }
    }

    @Test
    fun teamsPersistAndUniqueIndexRejectsDuplicates() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "teams-test-${UUID.randomUUID()}.db"
        var database = Room.databaseBuilder(context, ChampionshipDatabase::class.java, name).build()
        try {
            val dao = database.teamDao()
            dao.insert(TeamEntity(name = "Zorros", normalizedName = "zorros"))
            dao.insert(TeamEntity(name = "Águilas", normalizedName = "águilas"))
            assertEquals(-1L, dao.insert(TeamEntity(name = "ZORROS", normalizedName = "zorros")))
            database.close()
            database = Room.databaseBuilder(context, ChampionshipDatabase::class.java, name).build()
            val teams = database.teamDao().observeTeams().first()
            assertEquals(2, teams.size)
            assertEquals(setOf("Zorros", "Águilas"), teams.map { it.name }.toSet())
        } finally {
            database.close()
            context.deleteDatabase(name)
        }
    }
}
