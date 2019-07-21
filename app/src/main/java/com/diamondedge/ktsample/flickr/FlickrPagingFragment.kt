package com.diamondedge.ktsample.flickr

import androidx.lifecycle.Observer
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.diamondedge.ktadapter.KtAdapter
import timber.log.Timber

class FlickrPagingFragment : FlickrFragment() {

    private val pagedListAdapter = PhotoPagingAdapter()

    override fun createAdapter(): KtAdapter<Photo> {
        return pagedListAdapter
    }

    override fun search(query: CharSequence) {
        Timber.d("search($query)")

        val config = PagedList.Config.Builder().setPageSize(PAGE_SIZE).setEnablePlaceholders(true).build()

        val factory = FlickrDataSourceFactory(query.toString())

        val liveData = LivePagedListBuilder(factory, config).build()
        val pageObserver = Observer<PagedList<Photo>> { pagedList ->
            pagedListAdapter.submitList(pagedList)
        }
        liveData.observe(this, pageObserver)

        if (suggestionsAdapter.indexOf(query) < 0)
            suggestionsAdapter.add(query)
    }
}