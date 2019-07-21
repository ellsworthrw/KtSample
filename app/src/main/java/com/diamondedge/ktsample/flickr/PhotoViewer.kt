package com.diamondedge.ktsample.flickr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.diamondedge.ktsample.R
import com.diamondedge.ktsample.flickr.PhotoAdapter.Companion.KEY_URL
import timber.log.Timber

class PhotoViewer : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_photo, container, false)

        val url = arguments?.getString(KEY_URL)
        Timber.i("showing $url")
        if (!url.isNullOrEmpty()) {
            val imageView = view.findViewById<ImageView>(R.id.image)
            Glide.with(this).load(url).into(imageView)
        }
        return view
    }
}