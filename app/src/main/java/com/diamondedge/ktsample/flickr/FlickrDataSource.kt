package com.diamondedge.ktsample.flickr

import androidx.paging.PageKeyedDataSource
import timber.log.Timber

class FlickrDataSource(private val query: String) : PageKeyedDataSource<Int, Photo>() {

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Photo>) {
        FlickrFragment.requestFlickrSearch(query) { result ->
            if (result.isSuccess()) {
                val photos = result.response?.photos
                val total = result.response?.photoData?.total ?: 0
                Timber.i("loadInitial: total: $total pages: ${result.response?.photoData?.pages}")
                callback.onResult(photos ?: listOf(), 0, total, null, result.response?.nextPageNumber)
            } else {
                Timber.e(result.error?.volleyError, "Error: %s", result.error)
//                val error = result.error
//                if (error is MyError)
//                    error.show(this)
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Photo>) {
        FlickrFragment.requestFlickrSearch(query, params.key) { result ->
            if (result.isSuccess()) {
                val photos = result.response?.photos
                callback.onResult(photos ?: listOf(), result.response?.nextPageNumber)
            } else {
                Timber.e(result.error?.volleyError, "Error: %s", result.error)
//                val error = result.error
//                if (error is MyError)
//                    error.show(this)
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Photo>) {
    }
}