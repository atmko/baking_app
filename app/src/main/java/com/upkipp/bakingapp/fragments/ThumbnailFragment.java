package com.upkipp.bakingapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.upkipp.bakingapp.R;
import com.upkipp.bakingapp.utils.NetworkUtils;

public class ThumbnailFragment extends Fragment {

    Context mContext;
    ImageView mThumbnailImageView;
    private String mThumbnailUrl;

    public ThumbnailFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_thumbnail, container, false);

        defineViews(rootView);

        setViewValues();

        return rootView;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.mThumbnailUrl = thumbnailUrl;
    }

    private void defineViews(View rootView) {
        mContext = rootView.getContext();
        mThumbnailImageView = rootView.findViewById(R.id.thumbnail_image_view);
    }

    private void setViewValues() {
        //load image with glide
        NetworkUtils.loadImage(
                mContext,
                mThumbnailUrl,
                mThumbnailImageView);
    }
}