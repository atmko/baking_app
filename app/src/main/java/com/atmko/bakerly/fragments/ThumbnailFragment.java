/*
 * Copyright (C) 2019 Aayat Mimiko
 */

package com.atmko.bakerly.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.atmko.bakerly.R;
import com.atmko.bakerly.utils.AppConstants;
import com.atmko.bakerly.utils.NetworkUtils;

//fragment class for thumbnail
public class ThumbnailFragment extends Fragment {
    private Context mContext;
    private ImageView mThumbnailImageView;
    private String mThumbnailUrl;

    public ThumbnailFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_thumbnail, container, false);
        mContext = rootView.getContext();
        defineViews(rootView);

        //noinspection StatementWithEmptyBody
        if (savedInstanceState == null) {

        } else {
            restoreSavedValues(savedInstanceState);
        }

        setViewValues();

        return rootView;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.mThumbnailUrl = thumbnailUrl;
    }

    private void defineViews(View rootView) {
        mThumbnailImageView = rootView.findViewById(R.id.thumbnail_image_view);
    }

    private void restoreSavedValues(Bundle savedInstanceState) {
        mThumbnailUrl = savedInstanceState.getString(AppConstants.STEP_THUMBNAIL_URL_KEY);

    }

    private void setViewValues() {
        //load image with glide
        NetworkUtils.loadImage(
                mContext,
                mThumbnailUrl,
                mThumbnailImageView);
    }

    public void reloadMedia() {
        setViewValues();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(AppConstants.STEP_THUMBNAIL_URL_KEY, mThumbnailUrl);
    }
}