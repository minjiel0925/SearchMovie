package org.example.mjworkspace.searchmovie.ui

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.example.mjworkspace.searchmovie.data.model.SearchResultNetwork
import org.example.mjworkspace.searchmovie.data.remote.SearchResultRepository
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    private val repository: SearchResultRepository
): ViewModel() {

    companion object {
        private const val VISIBLE_THRESHOLD = 5
    }

    private val queryLiveData = MutableLiveData<String>()

    val searchResults: LiveData<SearchResultNetwork> =
        queryLiveData.switchMap { queryStr ->
            liveData {
                val results =
                    repository.getSearchResultsStream(queryStr)
                        .asLiveData(Dispatchers.Main)
                emitSource(results)
            }
        }

    /**
     * Search based on a query string.
     */
    fun search(queryString: String) {
        queryLiveData.postValue(queryString)
    }


    fun listScrolled(
        visibleItemCount: Int,
        lastVisibleItemPosition: Int,
        totalItemCount: Int) {

        if (visibleItemCount + lastVisibleItemPosition + VISIBLE_THRESHOLD >= totalItemCount) {
            val immutableQuery = queryLiveData.value
            if (immutableQuery != null) {
                viewModelScope.launch {
                    repository.requestMore(immutableQuery)
                }
            }
        }
    }
}

