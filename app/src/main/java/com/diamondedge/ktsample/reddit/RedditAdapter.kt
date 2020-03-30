package com.diamondedge.ktsample.reddit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.diamondedge.ktadapter.KtAdapter
import com.diamondedge.ktadapter.KtMutableListAdapter
import com.diamondedge.ktsample.R
import timber.log.Timber

class RedditAdapter(values: MutableList<RedditData>) : KtMutableListAdapter<RedditData>(values) {

    init {
        Timber.i("init($values)")
        clickListener = { item, _, vh, adapter ->
//            val bundle = bundleOf(KEY_URL to item.url)
//            v.findNavController().navigate(R.id.action_to_flickr_details, bundle)
        }
    }

    override fun createVH(parent: ViewGroup, viewType: Int, adapter: KtAdapter<RedditData>): ViewHolder<RedditData> {
        return RedditVH.create(parent, adapter)
    }

    class RedditVH(view: View, adapter: KtAdapter<RedditData>) : ViewHolder<RedditData>(view, adapter) {

        private val thumbNailView: ImageView = view.findViewById(R.id.thumbnail_image)
        private val titleView: TextView = view.findViewById(R.id.text1)

        override fun bind(position: Int, item: RedditData) {
//                Glide.with(thumbNailView.context).load(item.thumbnailUrl).into(thumbNailView)
            titleView.text = item.title
        }

        companion object {

            fun create(parent: ViewGroup, adapter: KtAdapter<RedditData>): RedditVH {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reddit, parent, false)
                return RedditVH(view, adapter)
            }
        }
    }

    companion object {
        const val KEY_URL = "url"
    }
}