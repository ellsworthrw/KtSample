package com.diamondedge.ktsample.reddit

import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject
import java.util.*

@JsonObject
class RedditTopResponse {
    private val TAG = javaClass.simpleName

    @JsonField
    var kind: String? = null

    @JsonField
    var data: Data? = null

    @JsonObject
    class Data {
        @JsonField
        var children: List<Child> = ArrayList()
    }

    @JsonObject
    class Child {
        @JsonField
        var kind: String? = null

        @JsonField
        var data: RedditData? = null
    }

    fun getData(): MutableList<RedditData> {
        val list = ArrayList<RedditData>()
        val d = data
        if (d?.children != null)
            for (child in d.children) {
                child.data?.let {
                    list.add(it)
                }
            }
        return list
    }
}

@JsonObject
class RedditData {
    @JsonField
    var title: String? = null

    @JsonField
    var subreddit: String? = null

    @JsonField
    var subreddit_name_prefixed: String? = null

    @JsonField
    var name: String? = null

    @JsonField
    var thumbnail_height: Int = 0

    @JsonField
    var thumbnail_width: Int = 0

    @JsonField
    var thumbnail: String? = null


    @JsonField
    var author_fullname: String? = null

    @JsonField
    var subreddit_id: String? = null

    @JsonField
    var id: String? = null

    @JsonField
    var permalink: String? = null

    @JsonField
    var url: String? = null

//    @JsonField
//    var is_video: Boolean = false

    @JsonField
    var created_utc: Long = 0

    @JsonField
    var created: Long = 0

    @JsonField
    var subreddit_subscribers: Int = 0

    @JsonField
    var num_crossposts: Int = 0

    @JsonField
    var num_comments: Int = 0

    @JsonField
    var author: String? = null
}