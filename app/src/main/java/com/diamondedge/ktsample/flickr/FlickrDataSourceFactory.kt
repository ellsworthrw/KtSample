package com.diamondedge.ktsample.flickr

import androidx.paging.DataSource

class FlickrDataSourceFactory(private val query: String) : DataSource.Factory<Int, Photo>() {

    override fun create(): DataSource<Int, Photo> {
        return FlickrDataSource(query)
    }
}