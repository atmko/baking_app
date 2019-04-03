package com.upkipp.bakingapp.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class VideoPlayerFragment extends Fragment {
    private Context mContext;
    private SimpleExoPlayerView mVideoPlayerView;
    private String mVideoUrl;
    private SimpleExoPlayer mVideoPlayer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("..........12345", Boolean.toString(mVideoUrl==null));

        View rootView = inflater.inflate(R.layout.fragment_video_player, container, false);

        Log.d("..........23456", Boolean.toString(mVideoUrl==null));

        defineViews(rootView);

        Log.d("..........34567", Boolean.toString(mVideoUrl==null));

        setViewValues();

        Log.d("..........45678", Boolean.toString(mVideoUrl==null));

        return rootView;
    }

    public String getVideoUrl() {
        return mVideoUrl;

    }

    public void setVideoUrl(String videoUrl) {
        Log.d("..........SETTING", videoUrl);

        this.mVideoUrl = videoUrl;
        Log.d("..........SET", mVideoUrl);

    }

    private void defineViews(View rootView) {
        mContext = rootView.getContext();
        mVideoPlayerView = rootView.findViewById(R.id.video_player_view);
    }

    private void setViewValues() {
        configureVideoView();
    }

    private void configureVideoView() {
        setVideoPlayerDefaultArtWork();
        createVideoPlayer();
        setVideoMediaSource();
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
        Log.d("..........QWERTY", Boolean.toString(mVideoUrl==null));
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(mContext, mContext.getString(R.string.app_name));

        //temporary bug fix: fragment is called twice,
        //mVideoUrl is sometimes null even after checking for null in activity
        if (mVideoUrl != null) {
            Uri uri = Uri.parse(mVideoUrl);
            ExtractorMediaSource mediaSource = new ExtractorMediaSource(uri, dataSourceFactory,
                    new DefaultExtractorsFactory(), null, null);

            mVideoPlayerView.setPlayer(mVideoPlayer);
            mVideoPlayer.prepare(mediaSource);
        }

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
