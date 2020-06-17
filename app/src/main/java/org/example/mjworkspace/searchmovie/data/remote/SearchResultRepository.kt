package org.example.mjworkspace.searchmovie.data.remote

import android.util.Log
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import org.example.mjworkspace.searchmovie.api.TmdbService
import org.example.mjworkspace.searchmovie.data.db.SearchResultDao
import org.example.mjworkspace.searchmovie.data.model.SearchResult
import org.example.mjworkspace.searchmovie.data.model.SearchResultNetwork
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/*
class SearchResultRemote @Inject constructor (private val service: TmdbService) {

    private suspend fun search(query: String) =
        service.getSearchResults(query = query)

    suspend fun searchResultsWithPagination(query: String):
            List<SearchResult> {
        if (query.isBlank()) return listOf()
        val searchResults = mutableListOf<SearchResult>()
        val searchRequest = search(query = query)

        searchRequest.results
            .forEach {
                searchResults.add(it)
            }

        return searchResults
    }
}*/

// TMDB page API is 1 based
private const val STARTING_PAGE = 1

@ExperimentalCoroutinesApi
class SearchResultRepository @Inject constructor(
    private val service: TmdbService,
    private val dao: SearchResultDao
) {

    // keep the list of all results received
    private val inMemoryCache = mutableListOf<SearchResult>()

    private val localNoNetwork = mutableListOf<SearchResult>()

    // keep channel of results. The channel allows us to broadcast updates
    // so the subscriber will have the latest data.
    private val searchResults = ConflatedBroadcastChannel<SearchResultNetwork>()

    // keep the last requested page. When the request is successful,
    // increment the page number.
    private var lastRequestedPage = STARTING_PAGE

    // avoid triggering multiple requests in the same time
    private var isRequestInProgress = false


    suspend fun getSearchResultsStream(query: String): Flow<SearchResultNetwork> {
        Log.d("SearchResultsRemote", "New query: $query")
        lastRequestedPage = 1
        inMemoryCache.clear()
        requestAndSave(query)

        return searchResults.asFlow()
    }

    suspend fun requestMore(query: String) {
        if (isRequestInProgress) return
        val successful = requestAndSave(query)
        if (successful) {
            lastRequestedPage++
        }
    }

    suspend fun retry(query: String) {
        if (isRequestInProgress) return
        requestAndSave(query)
    }


    // network fetch & save in db
    private suspend fun requestAndSave(query: String): Boolean {
        isRequestInProgress = true
        var successful = false

        try {
            val response =
                service.getSearchResults(query = query, page = lastRequestedPage)
            Log.d("Repo_requestAndSave", "response $response")

            val movs = response?.results ?: emptyList()
            inMemoryCache.addAll(movs)
            dao.insert(inMemoryCache)
            Log.d("SearchResultsRemote", "inserting db ${inMemoryCache.size}")
            val movsByTitleRemoteList = movsByTitleRemote(query)
            searchResults.offer(SearchResultNetwork.Success(movsByTitleRemoteList))
            successful = true

        } catch (exception: IOException) {
            searchResults.offer(SearchResultNetwork.Error(exception))
        } catch (exception: HttpException) {
            searchResults.offer(SearchResultNetwork.Error(exception))
        }

        isRequestInProgress = false
        return successful
    }

    // remote
    private fun movsByTitleRemote(query: String): List<SearchResult> {
        val hasNull = inMemoryCache.filter { it.releaseDate == null || it.popularity == null }
        if (hasNull != null) {
            return inMemoryCache.sortedWith(compareByDescending<SearchResult> { it.title })
        } else if (inMemoryCache.size > 1) {
            return inMemoryCache.sortedWith(compareByDescending<SearchResult> { it.releaseDate }.thenBy { it.popularity })
        } else {
            return inMemoryCache
        }
    }

    // TODO: offline db-based function
    // local (when no network)
    private fun movsByTitleLocal(query: String): List<SearchResult> {
        val fromDb = dao.getAll() ?: emptyList()
        val localMovs =
            fromDb.filter { it.title.contains(query, true) }
                .sortedWith(compareByDescending<SearchResult> { it.releaseDate }
                    .thenBy { it.popularity })
        localNoNetwork.addAll(localMovs)
        return localNoNetwork
    }


    companion object {

        @Volatile
        private var instance: SearchResultRepository? = null

        fun getInstance(tmdbService: TmdbService, dao: SearchResultDao) =
            instance ?: synchronized(this) {
                instance
                    ?: SearchResultRepository(tmdbService, dao).also { instance = it }
            }
    }
}