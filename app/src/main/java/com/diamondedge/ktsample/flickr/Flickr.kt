package com.diamondedge.ktsample.flickr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.diamondedge.ktadapter.KtMutableListAdapter
import com.diamondedge.ktsample.*
import com.diamondedge.ktvolley.ResponseListener
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.Unregistrar
import timber.log.Timber
import java.net.URLEncoder


class Flickr : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: PhotoAdapter
    private var searchField: MaterialSearchView? = null
    private val suggestionsAdapter = KtMutableListAdapter<String>(mutableListOf())
    private var keyboardVisibilityListener: Unregistrar? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_flickr, container, false)
        searchField = view.findViewById(R.id.search_text)

        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview).apply {
            if (!this@Flickr::viewAdapter.isInitialized)
                viewAdapter = PhotoAdapter(mutableListOf())
            adapter = viewAdapter
            //            setHasFixedSize(true)
        }
        setupSearch()

        return view
    }

    private fun search(query: String) {
        Timber.d("search($query)")
        requestFlickrSearch(query) { result ->
            if (result.isSuccess()) {
                val photos = result.response?.photos
                if (photos != null) {
                    viewAdapter.setItems(photos)
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

    private fun requestFlickrSearch(searchText: String, listener: ResponseListener<FlickrPhotoResponse>) {
        MyVolley.add(
            MyRequest.create<FlickrPhotoResponse>()
                .path("https://www.flickr.com/services/rest")
                .errorCode("15")
                .queryParam("method", "flickr.photos.search")
                .queryParam("api_key", "1508443e49213ff84d566777dc211f2a")
                .queryParam("text", URLEncoder.encode(searchText.trim(), "utf-8"))
                .queryParam("per_page", PAGE_SIZE)
                .queryParam("format", "json")
                .queryParam("nojsoncallback", "1")
                .logging("Flickr", "requestSearch")
                .get(listener)
        )
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

        keyboardVisibilityListener = KeyboardVisibilityEvent.registerEventListener(getActivity()) { isOpen ->
            Timber.e("KeyboardVisibilityEvent isOpen: %s", isOpen)
            if (isOpen && suggestionsAdapter.itemCount > 0)
                searchField?.showSuggestions()
            else
                searchField?.hideSuggestions()
        }

        suggestionsAdapter.clickListener = { _, vh, adapter ->
            Timber.d("suggestionsAdapter.onItemClick")
            val position = vh.adapterPosition
            val query = adapter.get(position)
            searchField?.setQuery(query, true)
            searchField?.hideSuggestions()
            searchField?.hideKeyboard()
        }
    }

    companion object {
        const val PAGE_SIZE = "25"
    }
}
