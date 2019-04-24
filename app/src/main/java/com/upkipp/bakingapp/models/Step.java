package com.upkipp.bakingapp.models;

import org.parceler.Parcel;

import java.util.Map;

@Parcel
public class Step {
    String mId;
    String mShortDescription;
    String mDescription;
    String mVideoURL;
    String mThumbnailURL;

    Step() {
    }

    public Step(String mId, String mShortDescription, String mDescription, String mVideoURL, String mThumbnailURL) {
        this.mId = mId;
        this.mShortDescription = mShortDescription;
        this.mDescription = mDescription;
        this.mVideoURL = mVideoURL;
        this.mThumbnailURL = mThumbnailURL;
    }

    public String getId() {
        return mId;
    }

    public String getShortDescription() {
        return mShortDescription;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getVideoUrl() {
        return mVideoURL;
    }

    public String getThumbnailUrl() {
        return mThumbnailURL;
    }
}
