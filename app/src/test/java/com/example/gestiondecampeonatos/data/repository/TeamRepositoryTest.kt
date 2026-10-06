package com.example.gestiondecampeonatos.data.repository

import com.example.gestiondecampeonatos.data.local.dao.TeamDao
import com.example.gestiondecampeonatos.data.local.entity.TeamEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import com.example.gestiondecampeonatos.data.local.image.TeamImageStore

class TeamRepositoryTest {
    private val dao = FakeTeamDao()
    private val images = FakeImageStore()
    private val repository = TeamRepository(dao, images)

    @Test
    fun savesImageReferenceAndCleansUpDuplicateImage() = runBlocking {
        repository.addTeam("Club Norte", "content://first")
        val firstImage = dao.rows.value.single().imageFileName
        assertTrue(firstImage != null)
        assertEquals(AddTeamResult.DUPLICATE, repository.addTeam("CLUB NORTE", "content://second"))
        assertEquals(setOf(firstImage), images.files.toSet())
    }

    private class FakeImageStore : TeamImageStore {
        val files = mutableSetOf<String>()
        private var nextId = 0
        override fun importImage(uri: String): String = "${nextId++}.png".also { files.add(it) }
        override fun deleteImage(fileName: String) { files.remove(fileName) }
    }

    @Test
    fun emptyAndOverlongNamesAreNotInserted() = runBlocking {
        assertEquals(AddTeamResult.INVALID_NAME, repository.addTeam("   "))
        assertEquals(AddTeamResult.INVALID_NAME, repository.addTeam("a".repeat(61)))
        assertTrue(dao.rows.value.isEmpty())
    }

    @Test
    fun normalizesWhitespaceAndAcceptsMaximumLength() = runBlocking {
        assertEquals(AddTeamResult.SUCCESS, repository.addTeam("  Club   Norte  "))
        assertEquals("Club Norte", dao.rows.value.first().name)
        assertEquals("club norte", dao.rows.value.first().normalizedName)
        assertEquals(AddTeamResult.SUCCESS, repository.addTeam("a".repeat(60)))
    }

    @Test
    fun caseAndWhitespaceVariantsAreDuplicates() = runBlocking {
        repository.addTeam("Club Norte")
        assertEquals(AddTeamResult.DUPLICATE, repository.addTeam("  CLUB   NORTE "))
        assertEquals(1, dao.rows.value.size)
    }

    private class FakeTeamDao : TeamDao {
        val rows = MutableStateFlow<List<TeamEntity>>(emptyList())
        override fun observeTeams(): Flow<List<TeamEntity>> = rows
        override fun observeTeamCount(): Flow<Int> = rows.map { it.size }
        override suspend fun insert(team: TeamEntity): Long {
            if (rows.value.any { it.normalizedName == team.normalizedName }) return -1
            val id = rows.value.size.toLong() + 1
            rows.value += team.copy(id = id)
            return id
        }
    }
}
