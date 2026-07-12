package com.cinephile.data

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertNotNull

class MovieRepositoryTest {

    @Test
    fun repository_instantiation_succeeds() = runTest {
        val repo = MovieRepository()
        assertNotNull(repo)
    }
}
