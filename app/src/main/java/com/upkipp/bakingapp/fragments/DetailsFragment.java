package com.upkipp.bakingapp.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.upkipp.bakingapp.R;
import com.upkipp.bakingapp.utils.NetworkUtils;

public class DetailsFragment extends Fragment{
    Context mContext;
    TextView mDescriptionTextView;
    SimpleExoPlayerView mVideoPlayerView;
    ImageView mThumbnailImageView;

    private String mDescription;
    private String mIngredients;
    private String mVideoUrl;
    private String mThumbnailUrl;

    private SimpleExoPlayer mVideoPlayer;

    public DetailsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_details, container, false);

        defineViews(rootView);

        setViewValues();

        return rootView;
    }

    public void setIngredients(String ingredients) {
        this.mIngredients = ingredients;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.mThumbnailUrl = thumbnailUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.mVideoUrl = videoUrl;
    }

    private void defineViews(View rootView) {
        mContext = rootView.getContext();

        mDescriptionTextView = rootView.findViewById(R.id.description_text_view);
        if (mDescription == null || mDescription.equals("")) {
            mDescriptionTextView.setVisibility(View.GONE);
        }

        mVideoPlayerView = rootView.findViewById(R.id.video_player_view);
        if (mVideoUrl == null || mVideoUrl.equals("")) {
            mVideoPlayerView.setVisibility(View.GONE);
        }

        mThumbnailImageView = rootView.findViewById(R.id.thumbnail_image_view);
        if (mThumbnailUrl == null || mThumbnailUrl.equals("")) {
            mThumbnailImageView.setVisibility(View.GONE);
        }

    }

    private void setViewValues() {
        if (mDescription != null && !mDescription.equals("")) {
            mDescriptionTextView.setText(mDescription);
        }

        if (mVideoUrl != null && !mVideoUrl.equals("")) {
            configureVideoView();
        }

        if (mThumbnailUrl != null && !mThumbnailUrl.equals("")) {
            loadThumbnail();
        }
    }

    private void loadThumbnail() {
        //load image with glide
        NetworkUtils.loadImage(
                mContext,
                mThumbnailUrl,
                mThumbnailImageView);
    }

    private void configureVideoView() {
        setVideoPlayerDefaultArtWork();
        createVideoPlayer();
        setVideoMediaSource();
        createVideoPlayer();
    }

    private void setVideoPlayerDefaultArtWork() {
        Bitmap bitmap = BitmapFactory
                .decodeResource(getResources(), R.drawable.exo_controls_play);
        mVideoPlayerView.setDefaultArtwork(bitmap);
    }

    private void createVideoPlayer() {
        if (mVideoPlayer == null) {

            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mVideoPlayer = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector, loadControl);

        }
    }

    private void setVideoMediaSource() {
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(mContext, mContext.getString(R.string.app_name));

        Uri uri = Uri.parse(mVideoUrl);

        ExtractorMediaSource mediaSource = new ExtractorMediaSource(uri, dataSourceFactory,
                new DefaultExtractorsFactory(), null, null);

        mVideoPlayerView.setPlayer(mVideoPlayer);
        mVideoPlayer.prepare(mediaSource);
    }

    private void releaseVideoPlayer() {
        if (mVideoPlayer != null) {
            mVideoPlayer.stop();
            mVideoPlayer.release();
            mVideoPlayer = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        releaseVideoPlayer();
    }
}
