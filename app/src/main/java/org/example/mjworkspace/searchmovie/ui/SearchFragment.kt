package org.example.mjworkspace.searchmovie.ui

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.example.mjworkspace.searchmovie.R
import org.example.mjworkspace.searchmovie.data.model.SearchResultNetwork
import org.example.mjworkspace.searchmovie.databinding.SearchFragmentBinding
import org.example.mjworkspace.searchmovie.di.Injectable
import org.example.mjworkspace.searchmovie.di.injectViewModel
import org.example.mjworkspace.searchmovie.ui.deco.GridSpacingItemDecoration
import org.example.mjworkspace.searchmovie.ui.deco.VerticalItemDecoration
import javax.inject.Inject

@ExperimentalCoroutinesApi
class SearchFragment : Fragment(), Injectable {

    companion object {
        private const val LAST_SEARCH_QUERY: String = "last_search_query"
        private const val DEFAULT_QUERY = ""
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: SearchViewModel
    private lateinit var binding: SearchFragmentBinding
    private val adapter: SearchAdapter by lazy { SearchAdapter() }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = injectViewModel(viewModelFactory)
        binding = SearchFragmentBinding.inflate(inflater, container, false)
        context ?: return binding.root

        setupScrollListener()

        initAdapter()
        val query = savedInstanceState?.getString(LAST_SEARCH_QUERY) ?: DEFAULT_QUERY
        if (viewModel.searchResults.value == null) {
            viewModel.search(query)
        }
        initSearch(query)
        return binding.root
    }

    //Save the fragment's state here
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(LAST_SEARCH_QUERY, binding.searchQuery.text.trim().toString())
    }

    private fun initAdapter() {
        binding.list.adapter = adapter
        viewModel.searchResults.observe(viewLifecycleOwner) { result ->
            when( result) {
                is SearchResultNetwork.Success -> {
                    showEmptyList(result.data!!.isEmpty())
                    adapter.submitList(result.data)
                }
                is SearchResultNetwork.Error -> {
                    Toast.makeText(activity, "${result.error}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    private fun initSearch(query: String) {
        binding.searchQuery.setText(query)

        binding.searchQuery.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updateRepoListFromInput()
                true
            } else {
                false
            }
        }
        binding.searchQuery.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updateRepoListFromInput()
                true
            } else {
                false
            }
        }
    }


    private fun updateRepoListFromInput() {
        binding.searchQuery.text.trim().let {
            if (it.isNotEmpty()) {
                binding.list.scrollToPosition(0)
                viewModel.search(it.toString())
            }
        }
    }


    private fun showEmptyList(show: Boolean) {
        if (show) {
            binding.emptyList.visibility = View.VISIBLE
            binding.list.visibility = View.GONE
        } else {
            binding.emptyList.visibility = View.GONE
            binding.list.visibility = View.VISIBLE
        }
    }

    private fun setupScrollListener() {
        val layoutManager = binding.list.layoutManager as LinearLayoutManager
        binding.list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItemCount = layoutManager.itemCount
                val visibleItemCount = layoutManager.childCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                viewModel.listScrolled(visibleItemCount, lastVisibleItem, totalItemCount)
            }
        })
    }



}