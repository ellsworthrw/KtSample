package com.diamondedge.ktsample.reddit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.diamondedge.ktsample.MyError
import com.diamondedge.ktsample.MyRequest
import com.diamondedge.ktsample.MyVolley
import com.diamondedge.ktsample.R
import com.diamondedge.ktvolley.ResponseListener
import com.diamondedge.ktvolley.Result
import timber.log.Timber

class RedditFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private val viewAdapter = RedditAdapter(mutableListOf())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_reddit, container, false)

        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview).apply {
            adapter = viewAdapter
            setHasFixedSize(true)
        }

        requestRedditTop(10) { result: Result<RedditTopResponse> ->
            if (result.isSuccess()) {
                val data = result.response?.getData()
                Timber.d("data: $data")
                if (data != null) {
                    viewAdapter.items = data
                }
            } else {
                Timber.e(result.error?.volleyError, "Error: %s", result.error)
                val error = result.error
                if (error is MyError)
                    error.show(this)
            }
        }
        return view
    }

    private fun requestRedditTop(limit: Int, listener: ResponseListener<RedditTopResponse>) {
        MyVolley.add(
            MyRequest.create<RedditTopResponse>()
                .path("https://www.reddit.com/top.json?limit=$limit")
                .errorCode("12")
                .logging("Reddit", "requestRedditTop")
                .get(listener)
        )
    }
}
