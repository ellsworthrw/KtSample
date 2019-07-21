package com.diamondedge.ktsample.flickr

import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import com.diamondedge.ktadapter.KtAdapter
import com.diamondedge.ktadapter.KtMutableListAdapter
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

    companion object {
        const val KEY_URL = "url"
    }
}