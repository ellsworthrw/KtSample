package com.diamondedge.ktsample.flickr;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class Photo {

    @JsonField
    String title;

    @JsonField
    String owner;

    @JsonField
    int farm;

    @JsonField
    String server;

    @JsonField
    String id;

    @JsonField
    String secret;

    public String getUrl() {
        return String.format("https://farm%s.staticflickr.com/%s/%s_%s.jpg", farm, server, id, secret);
    }

    public String getThumbnailUrl() {
        return String.format("https://farm%s.staticflickr.com/%s/%s_%s_q.jpg", farm, server, id, secret);
    }
}
