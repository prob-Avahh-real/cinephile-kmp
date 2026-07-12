// MovieRepository.kt — 电影仓库 (KMP)
package com.cinephile.data

import com.cinephile.model.*
import kotlinx.serialization.Serializable

class MovieRepository(private val client: CinephileHttpClient = CinephileHttpClient.shared) {

    suspend fun getFeatured(): FeaturedResponse =
        client.get("/movies/featured")

    suspend fun getMovies(
        page: Int = 1,
        limit: Int = 20,
        genre: String? = null,
        sort: String? = null,
        query: String? = null
    ): PaginatedMovies {
        val params = buildMap {
            put("page", page.toString())
            put("limit", limit.toString())
            if (genre != null) put("genre", genre)
            if (sort != null) put("sort", sort)
            if (query != null) put("q", query)
        }
        return client.get("/movies", params)
    }

    suspend fun getMovieDetail(slug: String): MovieDetail =
        client.get("/movies/$slug")

    @Serializable
    data class FavoriteResult(val favorited: Boolean)

    suspend fun toggleFavorite(slug: String): Boolean {
        val result = client.post<FavoriteResult>("/movies/$slug/favorite")
        return result.favorited
    }

    @Serializable
    data class RateBody(val score: Int)

    suspend fun rateMovie(slug: String, score: Int) {
        client.post<Unit>("/movies/$slug/rate", RateBody(score))
    }
}
