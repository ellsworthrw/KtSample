package com.diamondedge.ktsample.flickr

import androidx.paging.PageKeyedDataSource
import com.diamondedge.ktsample.MyRequest
import com.diamondedge.ktsample.MyVolley
import com.diamondedge.ktvolley.ResponseListener
import timber.log.Timber
import java.net.URLEncoder

class FlickrDataSource(private val query: String) : PageKeyedDataSource<Int, Photo>() {

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Photo>) {
        requestFlickrSearch(query) { result ->
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
        requestFlickrSearch(query, params.key) { result ->
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

    companion object {

        fun requestFlickrSearch(searchText: String, page: Int = 1, listener: ResponseListener<FlickrPhotoResponse>) {
            MyVolley.add(
                MyRequest.create<FlickrPhotoResponse>()
                    .path("https://www.flickr.com/services/rest")
                    .errorCode("15")
                    .queryParam("method", "flickr.photos.search")
                    .queryParam("text", URLEncoder.encode(searchText.trim(), "utf-8"))
                    .queryParam("page", page)
                    .queryParam("api_key", "1508443e49213ff84d566777dc211f2a")
                    .queryParam("per_page", FlickrFragment.PAGE_SIZE)
                    .queryParam("format", "json")
                    .queryParam("nojsoncallback", "1")
                    .logging("Flickr", "requestSearch")
                    .get(listener)
            )
        }

    }
}