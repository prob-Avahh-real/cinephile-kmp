// Movie.kt — 影迷电影模型
package com.cinephile.model

import kotlinx.serialization.Serializable

@Serializable
data class Movie(
    val id: String,
    val title: String,
    val originalTitle: String? = null,
    val slug: String,
    val year: Int,
    val posterUrl: String? = null,
    val backdropUrl: String? = null,
    val ratingAvg: Double? = null,
    val synopsis: String? = null,
    val synopsisLong: String? = null,
    val durationMin: Int? = null,
    val releaseDate: String? = null,
    val genres: List<String>? = null,
    val isHot: Boolean = false
)

@Serializable
data class MovieDetail(
    // 继承 Movie 字段
    val id: String,
    val title: String,
    val originalTitle: String? = null,
    val slug: String,
    val year: Int,
    val posterUrl: String? = null,
    val backdropUrl: String? = null,
    val ratingAvg: Double? = null,
    val synopsis: String? = null,
    val synopsisLong: String? = null,
    val durationMin: Int? = null,
    val releaseDate: String? = null,
    val genres: List<String>? = null,
    val isHot: Boolean = false,
    // MovieDetail 特有字段
    val casts: List<CastMember> = emptyList(),
    val directors: List<Director> = emptyList(),
    val myFavorite: Boolean = false,
    val myRating: Int? = null
)

@Serializable
data class CastMember(
    val id: Int,
    val name: String,
    val avatarUrl: String? = null,
    val roleName: String? = null
)

@Serializable
data class Director(
    val id: Int,
    val name: String,
    val avatarUrl: String? = null
)

@Serializable
data class FeaturedResponse(
    val banners: List<Movie> = emptyList(),
    val hot: List<Movie> = emptyList(),
    val upcoming: List<Movie> = emptyList()
)

@Serializable
data class PaginatedMovies(
    val items: List<Movie> = emptyList(),
    val total: Int,
    val hasMore: Boolean
)
