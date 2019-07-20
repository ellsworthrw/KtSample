package com.diamondedge.ktsample.flickr;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.ArrayList;
import java.util.List;

@JsonObject
public class FlickrPhotoResponse {

    @JsonField
    String stat;

    @JsonField(name = "photos")
    PhotosData photoData;

    @JsonObject
    public static class PhotosData {

        @JsonField
        int page;

        @JsonField
        String pages;

        @JsonField
        int perpage;

        @JsonField
        String total;

        @JsonField(name = "photo")
        List<Photo> photoList;
    }

    public List<Photo> getPhotos() {
        if (photoData != null && photoData.photoList != null)
            return photoData.photoList;
        return new ArrayList<>();
    }
}
