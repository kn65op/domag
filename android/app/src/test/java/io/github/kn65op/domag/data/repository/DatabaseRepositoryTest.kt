package io.github.kn65op.domag.data.repository

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.isEmpty
import com.nhaarman.mockitokotlin2.mock
import io.github.kn65op.domag.data.database.database.AppDatabase
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Test

class DatabaseRepositoryTest {
    val database: AppDatabase = mock()
    val repository = DatabaseRepository(database)

    @Before
    fun prepareDatabase() {
       
    }

    @Test
    fun `Repostory should return all categories`() {
        repository.getAllCategories()
    }
}