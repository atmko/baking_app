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

import com.upkipp.bakingapp.R;
import com.upkipp.bakingapp.utils.AppConstants;
import com.upkipp.bakingapp.utils.NetworkUtils;

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