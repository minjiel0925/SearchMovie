package org.example.mjworkspace.searchmovie.api

/**
 * Used to notify a client of the actual state of a query
 */
@Suppress("unused")
enum class NetworkState {
    RUNNING,
    SUCCESS,
    FAILED
}