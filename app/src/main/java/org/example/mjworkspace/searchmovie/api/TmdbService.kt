package org.example.mjworkspace.searchmovie.api

import kotlinx.coroutines.Deferred
import org.example.mjworkspace.searchmovie.BuildConfig
import org.example.mjworkspace.searchmovie.data.model.SearchResult
import retrofit2.http.GET
import retrofit2.http.Query

const val END_POINT = "https://api.themoviedb.org/3/"

interface TmdbService {

    @GET("search/movie")
    suspend fun getSearchResults(
        @Query("api_key") api_key: String = BuildConfig.TMDB_API_KEY,
        @Query("query") query: String,
        @Query("page") page: Int
    ): ResultsResponse<SearchResult>?
}