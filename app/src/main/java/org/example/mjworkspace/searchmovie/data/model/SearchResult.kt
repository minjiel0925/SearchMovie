package org.example.mjworkspace.searchmovie.data.model

import androidx.room.Entity
import androidx.room.TypeConverters
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Entity(tableName = "search_result", primaryKeys = ["id"])
@JsonClass(generateAdapter = true)
data class SearchResult(
    @Json(name="id")
    val id: Int,
    @Json(name="title")
    val title: String,
    @Json(name = "release_date")
    val releaseDate: String?,
    @Json(name="vote_average")
    val voteAverage: Float,
    @Json(name="popularity")
    val popularity: Float?,
    @Json(name="poster_path")
    val posterPath: String?
)
