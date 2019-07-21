package com.diamondedge.ktsample.flickr;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.Objects;

@JsonObject
public class Photo {

    @JsonField
    String title = "";

    @JsonField
    String owner;

    @JsonField
    int farm;

    @JsonField
    String server;

    @JsonField
    String id = "";

    @JsonField
    String secret;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Photo photo = (Photo) o;
        return id.equals(photo.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public String getUrl() {
        return String.format("https://farm%s.staticflickr.com/%s/%s_%s.jpg", farm, server, id, secret);
    }

    public String getThumbnailUrl() {
        return String.format("https://farm%s.staticflickr.com/%s/%s_%s_q.jpg", farm, server, id, secret);
    }
}
