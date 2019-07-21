package com.diamondedge.ktsample.flickr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.diamondedge.ktadapter.KtAdapter
import com.diamondedge.ktadapter.KtMutableListAdapter
import com.diamondedge.ktsample.*
import com.diamondedge.ktvolley.ResponseListener
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.Unregistrar
import timber.log.Timber
import java.net.URLEncoder


open class FlickrFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: KtAdapter<Photo>
    private var searchField: MaterialSearchView? = null
    protected val suggestionsAdapter = KtMutableListAdapter<CharSequence>(ArrayList())
    private var keyboardVisibilityListener: Unregistrar? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Timber.d("onCreateView($savedInstanceState)")
        val view = inflater.inflate(R.layout.fragment_flickr, container, false)
        searchField = view.findViewById(R.id.search_text)

        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview).apply {
            if (!this@FlickrFragment::viewAdapter.isInitialized)
                viewAdapter = createAdapter()
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

    open fun createAdapter(): KtAdapter<Photo> {
        return PhotoAdapter(mutableListOf())
    }

    open fun search(query: CharSequence) {
        Timber.d("search($query)")
        requestFlickrSearch(query.toString()) { result ->
            if (result.isSuccess()) {
                val photos = result.response?.photos
                val adapter = viewAdapter
                if (photos != null && adapter is PhotoAdapter) {
                    adapter.items = photos
                }
            } else {
                Timber.e(result.error?.volleyError, "Error: %s", result.error)
                val error = result.error
                if (error is MyError)
                    error.show(this)
            }
        }
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

        suggestionsAdapter.clickListener = { _, vh, adapter ->
            Timber.d("suggestionsAdapter.onItemClick")
            val position = vh.adapterPosition
            val query = adapter[position]
            searchField?.setQuery(query.toString(), true)
            searchField?.hideSuggestions()
            searchField?.hideKeyboard()
        }
    }

    companion object {
        const val PAGE_SIZE = 25
        const val QUERY_KEY = "query"
        const val SEARCH_HISTORY_KEY = "search-history"

        fun requestFlickrSearch(searchText: String, page: Int = 1, listener: ResponseListener<FlickrPhotoResponse>) {
            MyVolley.add(
                MyRequest.create<FlickrPhotoResponse>()
                    .path("https://www.flickr.com/services/rest")
                    .errorCode("15")
                    .queryParam("method", "flickr.photos.search")
                    .queryParam("text", URLEncoder.encode(searchText.trim(), "utf-8"))
                    .queryParam("page", page)
                    .queryParam("api_key", "1508443e49213ff84d566777dc211f2a")
                    .queryParam("per_page", PAGE_SIZE)
                    .queryParam("format", "json")
                    .queryParam("nojsoncallback", "1")
                    .logging("Flickr", "requestSearch")
                    .get(listener)
            )
        }
    }
}
