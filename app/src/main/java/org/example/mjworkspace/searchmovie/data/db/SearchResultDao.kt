package org.example.mjworkspace.searchmovie.data.db

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import org.example.mjworkspace.searchmovie.data.model.SearchResult
import org.example.mjworkspace.searchmovie.data.model.SearchResultNetwork
import retrofit2.http.DELETE

@Dao
interface SearchResultDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    // suspend fun insert(results: List<SearchResult>, insertFinished: () -> Unit)
    suspend fun insert(results: List<SearchResult>)

    @Query("SELECT * FROM search_result")
    fun getAll(): List<SearchResult>?

    @Query("DELETE FROM search_result")
    fun deleteAll()


//    @Query("SELECT * FROM search_result WHERE 'query' = :query")
//    fun searchResults(query: String): LiveData<SearchResultNetwork>
//
//    @Query("SELECT * FROM search_result WHERE 'query' = :query")
//    fun searchResult(query: String): SearchResult?
//
//    @Query("SELECT * FROM search_result WHERE 'query' = :query")
//    fun getPagedSearchResults(query: String): DataSource.Factory<Int, SearchResult>
}