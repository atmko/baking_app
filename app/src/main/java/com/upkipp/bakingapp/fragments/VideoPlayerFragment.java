/*
 * Copyright (C) 2019 Aayat Mimiko
 */

package com.upkipp.bakingapp.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.upkipp.bakingapp.R;

//fragment class for video player
public class VideoPlayerFragment extends Fragment implements ExoPlayer.EventListener {
    @SuppressWarnings("FieldCanBeLocal")
    private static String MEDIA_SESSION_TAG = "MEDIA_SESSION";

    private static String VIDEO_URL_KEY = "video_url_key";
    private static String PLAYBACK_POSITION_KEY = "playback_position";
    @SuppressWarnings("FieldCanBeLocal")
    private final String errorOnClickListenerImplementation = " must implement OnClickListener";

    private View mRootView;
    private Context mContext;
    private SimpleExoPlayerView mVideoPlayerView;
    private String mVideoUrl;
    private SimpleExoPlayer mVideoPlayer;

    private long mPlaybackPosition;

    private View.OnClickListener mFullScreenClickListener;

    private MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mPlaybackStateBuilder;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //check that onclickListener is implemented
        try {
            mFullScreenClickListener = (View.OnClickListener)context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + errorOnClickListenerImplementation);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_video_player, container, false);
        mContext = mRootView.getContext();
        initializeMediaSession();
        defineViews(mRootView);
        configureFullScreenClickListener();

        if (savedInstanceState == null) {
            defineValues();
        } else {
            restoreSavedValues(savedInstanceState);
        }

        setViewValues();

        return mRootView;
    }

    public void setVideoUrl(String videoUrl) {
        this.mVideoUrl = videoUrl;
    }

    public void reloadMedia() {
        mPlaybackPosition = 0;
        setVideoMediaSource();
    }

    public void stopVideoPlayer() {
        mVideoPlayer.stop();
    }

    private void initializeMediaSession() {
        mMediaSession = new MediaSessionCompat(mContext, MEDIA_SESSION_TAG);
        mMediaSession.setFlags
                (MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        mMediaSession.setMediaButtonReceiver(null);

        mPlaybackStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_PAUSE |
                        PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mPlaybackStateBuilder.build());
        mMediaSession.setCallback(new MediaCallbacks());
        mMediaSession.setActive(true);

    }

    private void defineViews(View rootView) {
        mVideoPlayerView = rootView.findViewById(R.id.video_player_view);
    }

    private void configureFullScreenClickListener() {
        ImageButton fullScreenButton = mRootView.findViewById(R.id.exo_fullscreen);
        fullScreenButton.setOnClickListener(mFullScreenClickListener);
    }

    private void defineValues() {
        //mVideoUrl already set through activity
        mPlaybackPosition = 0;
    }

    private void restoreSavedValues(Bundle savedInstanceState) {
        mVideoUrl = savedInstanceState.getString(VIDEO_URL_KEY);
        mPlaybackPosition = savedInstanceState.getLong(PLAYBACK_POSITION_KEY, 0);

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
            //add EventListener
            mVideoPlayer.addListener(this);
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
        mVideoPlayer.seekTo(mPlaybackPosition);
        mVideoPlayer.setPlayWhenReady(true);

    }

    //media session callback class
    private class MediaCallbacks extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mVideoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mVideoPlayer.setPlayWhenReady(false);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(VIDEO_URL_KEY, mVideoUrl);
        outState.putLong(PLAYBACK_POSITION_KEY, mVideoPlayer.getCurrentPosition());
    }

    @Override
    public void onPause() {
        super.onPause();
        //pause video playback
        mVideoPlayer.setPlayWhenReady(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //stop media session and release/cleanup video player
        mMediaSession.setActive(false);
        releaseVideoPlayer();
    }

    private void releaseVideoPlayer() {
        if (mVideoPlayer != null) {
            mVideoPlayer.stop();
            mVideoPlayer.release();
            mVideoPlayer = null;
        }
    }

    //exoplayer event listener methods
    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == ExoPlayer.STATE_READY && playWhenReady) {
            mPlaybackStateBuilder.setState
                    (PlaybackStateCompat.STATE_PLAYING,
                            mVideoPlayer.getCurrentPosition(), 1f);

        } else if (playbackState == ExoPlayer.STATE_READY){
            mPlaybackStateBuilder.setState
                    (PlaybackStateCompat.STATE_PAUSED,
                            mVideoPlayer.getCurrentPosition(), 1f);
        }
        mMediaSession.setPlaybackState(mPlaybackStateBuilder.build());
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

}
