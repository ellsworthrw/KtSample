package com.diamondedge.ktsample.flickr

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.diamondedge.ktadapter.KtAdapter
import com.diamondedge.ktsample.R

class PhotoVH(view: View, adapter: KtAdapter<Photo>) : KtAdapter.ViewHolder<Photo>(view, adapter) {

    private val thumbNailView: ImageView = view.findViewById(R.id.thumbnail_image)
    private val titleView: TextView = view.findViewById(R.id.text1)

    override fun bind(position: Int, item: Photo) {
        Glide.with(thumbNailView.context).load(item.thumbnailUrl).into(thumbNailView)
        titleView.text = item.title
    }

    companion object {

        fun create(parent: ViewGroup, adapter: KtAdapter<Photo>): PhotoVH {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_photo, parent, false)
            return PhotoVH(view, adapter)
        }
    }
}