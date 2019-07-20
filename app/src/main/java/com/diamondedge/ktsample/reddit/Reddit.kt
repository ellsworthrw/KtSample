package com.diamondedge.ktsample.reddit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.diamondedge.ktsample.MyRequest
import com.diamondedge.ktsample.MyVolley
import com.diamondedge.ktsample.R
import com.diamondedge.ktvolley.ResponseListener
import com.diamondedge.ktvolley.Result
import timber.log.Timber

class Reddit : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_reddit, container, false)

        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview).apply {
            setHasFixedSize(true)
        }
        requestRedditTop(10) { result: Result<RedditTopResponse> ->
            if (result.isSuccess()) {
                val data = result.response?.getData()
                if (data != null) {
                    viewAdapter = MyAdapter(data)
                    recyclerView.adapter = viewAdapter
                }
            } else {
                Timber.e(result.error?.volleyError, "Error: %s", result.error)
                Toast.makeText(activity, "Error: ${result.error}", Toast.LENGTH_LONG)
            }
        }
        return view
    }

    private fun requestRedditTop(limit: Int, listener: ResponseListener<RedditTopResponse>) {
        val url = "https://www.reddit.com/top.json?limit=$limit"
        Timber.d("$tag reddit: url: $url")
        MyVolley.add(
            MyRequest.create<RedditTopResponse>()
                .path(url)
                .errorCode("12")
                .useCache(false)
                .get(listener)
        )
    }

    class MyAdapter(private val myDataset: MutableList<RedditData>) :
        RecyclerView.Adapter<MyAdapter.ViewHolder>() {

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder.
        // Each data item is just a string in this case that is shown in a TextView.
        class ViewHolder(val item: View) : RecyclerView.ViewHolder(item)


        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {
            // create a new view
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_reddit, parent, false)


            return ViewHolder(itemView)
        }

        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.item.findViewById<TextView>(R.id.text1).text = myDataset[position].title

//            holder.item.findViewById<ImageView>(R.id.thumbnail_image)
//                    .setImageResource(listOfAvatars[position % listOfAvatars.size])

            holder.item.setOnClickListener {
                //                val bundle = bundleOf(USERNAME_KEY to myDataset[position])

//                holder.item.findNavController().navigate(
//                        R.id.action_to_reddit_details,
//                        bundle)
            }
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = myDataset.size
    }

    companion object {
        const val TAG = "Reddit"
    }
}
