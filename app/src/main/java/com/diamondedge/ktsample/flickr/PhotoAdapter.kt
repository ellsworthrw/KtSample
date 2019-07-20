package com.diamondedge.ktsample.flickr

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.diamondedge.ktadapter.KtAdapter
import com.diamondedge.ktadapter.KtListAdapter
import com.diamondedge.ktadapter.KtMutableListAdapter
import com.diamondedge.ktadapter.StringVH
import com.diamondedge.ktsample.R
import timber.log.Timber

class PhotoAdapter(values: MutableList<Photo>) : KtMutableListAdapter<Photo>(values) {

    init {
        Timber.i("init($values)")
        clickListener = { v, vh, adapter ->
            val item = adapter[vh.adapterPosition]
            val bundle = bundleOf(KEY_URL to item.url)
            v.findNavController().navigate(R.id.action_to_flickr_details, bundle)
        }
    }

    override fun createVH(parent: ViewGroup, viewType: Int, adapter: KtAdapter<Photo>): ViewHolder<Photo> {
        return PhotoVH.create(parent, adapter)
    }

    class PhotoVH(view: View, adapter: KtAdapter<Photo>) : ViewHolder<Photo>(view, adapter) {

        private val thumbNailView: ImageView = view.findViewById(R.id.thumbnail_image)
        private val titleView: TextView = view.findViewById(R.id.text1)

        override fun bind(position: Int, item: Photo) {
            Glide.with(thumbNailView.context).load(item.thumbnailUrl).into(thumbNailView)
            titleView.text = item.title
        }

        companion object {

            fun create(parent: ViewGroup, adapter: KtAdapter<Photo>): PhotoVH {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_photo, parent, false)
                return PhotoVH(view, adapter)
            }
        }
    }

    companion object {
        private const val TAG = "PhotoAdapter"
        const val KEY_URL = "url"
    }
}