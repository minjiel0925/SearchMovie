package org.example.mjworkspace.searchmovie.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class ResultsResponse<T>(
    @Json(name = "results")
    val results: List<T>
)