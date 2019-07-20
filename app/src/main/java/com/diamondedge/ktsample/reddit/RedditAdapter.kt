package com.diamondedge.ktsample.reddit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.diamondedge.ktadapter.KtAdapter
import com.diamondedge.ktadapter.KtListAdapter
import com.diamondedge.ktadapter.StringVH
import com.diamondedge.ktsample.R
import timber.log.Timber

class RedditAdapter(values: MutableList<Any>) : KtListAdapter<Any>(values) {

    init {
        Timber.i("init($values)")
        clickListener = { _, vh, adapter ->
            //            val item = adapter[vh.adapterPosition]
//            val bundle = bundleOf(KEY_URL to item.url)
//            v.findNavController().navigate(R.id.action_to_flickr_details, bundle)
        }
    }

    override fun createVH(parent: ViewGroup, viewType: Int, adapter: KtAdapter<Any>): ViewHolder<Any> {
        return when (viewType) {
            ITEM_TYPE -> RedditVH.create(parent, adapter)
            else -> StringVH(parent, adapter)
        }
    }

    class RedditVH(view: View, adapter: KtAdapter<Any>) : ViewHolder<Any>(view, adapter) {

        private val thumbNailView: ImageView = view.findViewById(R.id.thumbnail_image)
        private val titleView: TextView = view.findViewById(R.id.text1)

        override fun bind(position: Int, item: Any) {
            if (item is RedditData) {
//                Glide.with(thumbNailView.context).load(item.thumbnailUrl).into(thumbNailView)
                titleView.text = item.title
            }
        }

        companion object {

            fun create(parent: ViewGroup, adapter: KtAdapter<Any>): RedditVH {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reddit, parent, false)
                return RedditVH(view, adapter)
            }
        }
    }

    companion object {
        private const val TAG = "PhotoAdapter"
        const val KEY_URL = "url"
    }
}