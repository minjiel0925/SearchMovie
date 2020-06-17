package org.example.mjworkspace.searchmovie.data.model

/**
 * network error state
 */
sealed class SearchResultNetwork {
    data class Success(val data: List<SearchResult>?): SearchResultNetwork()
    data class Error(val error: Exception): SearchResultNetwork()
}