package com.diamondedge.ktsample.flickr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import com.diamondedge.ktadapter.KtMutableListAdapter
import com.diamondedge.ktsample.MaterialSearchView
import com.diamondedge.ktsample.R
import com.diamondedge.ktsample.hideKeyboard
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.Unregistrar
import timber.log.Timber


open class FlickrFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private val viewAdapter = PhotoAdapter()
    private var searchField: MaterialSearchView? = null
    protected val suggestionsAdapter = KtMutableListAdapter<CharSequence>(ArrayList())
    private var keyboardVisibilityListener: Unregistrar? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Timber.d("onCreateView($savedInstanceState)")
        val view = inflater.inflate(R.layout.fragment_flickr, container, false)
        searchField = view.findViewById(R.id.search_text)

        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview).apply {
            adapter = viewAdapter
            setHasFixedSize(true)
        }

        setupSearch()

        val query = savedInstanceState?.getCharSequence(QUERY_KEY)
        if (!query.isNullOrEmpty())
            search(query)

        val searchHistory = savedInstanceState?.getCharSequenceArrayList(SEARCH_HISTORY_KEY)
        if (!searchHistory.isNullOrEmpty())
            suggestionsAdapter.items = searchHistory

        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putCharSequence(QUERY_KEY, searchField?.query)
        val items = suggestionsAdapter.items
        if (items is ArrayList)
            outState.putCharSequenceArrayList(SEARCH_HISTORY_KEY, items)
        Timber.d("onSaveInstanceState($outState)")
    }

    fun search(query: CharSequence) {
        Timber.d("search($query)")

        val config = PagedList.Config.Builder().setPageSize(PAGE_SIZE).setEnablePlaceholders(true).build()

        val factory = FlickrDataSourceFactory(query.toString())

        val liveData = LivePagedListBuilder(factory, config).build()
        val pageObserver = Observer<PagedList<Photo>> { pagedList ->
            viewAdapter.submitList(pagedList)
        }
        liveData.observe(this, pageObserver)

        if (suggestionsAdapter.indexOf(query) < 0)
            suggestionsAdapter.add(query)
    }

    private fun setupSearch() {
        searchField?.setSearchSuggestionsAdapter(suggestionsAdapter)
        searchField?.setQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                Timber.i("onQueryTextSubmit(%s)", query)
                searchField?.hideSuggestions()
                search(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean = true
        })

        keyboardVisibilityListener = KeyboardVisibilityEvent.registerEventListener(activity) { isOpen ->
            Timber.e("KeyboardVisibilityEvent isOpen: %s", isOpen)
            if (isOpen && suggestionsAdapter.itemCount > 0)
                searchField?.showSuggestions()
            else
                searchField?.hideSuggestions()
        }

        suggestionsAdapter.clickListener = { query, _, vh, adapter ->
            Timber.d("suggestionsAdapter.onItemClick")
            searchField?.setQuery(query.toString(), true)
            searchField?.hideSuggestions()
            searchField?.hideKeyboard()
        }
    }

    companion object {
        const val PAGE_SIZE = 25
        const val QUERY_KEY = "query"
        const val SEARCH_HISTORY_KEY = "search-history"
    }
}
