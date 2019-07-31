package com.diamondedge.ktsample.flickr

import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import com.diamondedge.ktadapter.KtAdapter
import com.diamondedge.ktadapter.KtPagedListAdapter
import com.diamondedge.ktsample.R

class PhotoAdapter : KtPagedListAdapter<Photo>(Photo::class.java, PHOTO_COMPARATOR) {

    init {
        clickListener = { v, vh, adapter ->
            val item = adapter[vh.adapterPosition]
            val bundle = bundleOf(KEY_URL to item.url)
            v.findNavController().navigate(R.id.action_to_flickr_details, bundle)
        }
    }

    override fun createVH(parent: ViewGroup, viewType: Int, adapter: KtAdapter<Photo>): ViewHolder<Photo> {
        return PhotoVH.create(parent, adapter)
    }

    companion object {
        const val KEY_URL = "url"

        val PHOTO_COMPARATOR = object : DiffUtil.ItemCallback<Photo>() {
            override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean =
                oldItem.title == newItem.title

            override fun getChangePayload(oldItem: Photo, newItem: Photo): Any? = null
        }
    }
}