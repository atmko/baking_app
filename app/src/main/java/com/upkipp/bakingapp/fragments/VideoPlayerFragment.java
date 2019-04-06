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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class VideoPlayerFragment extends Fragment implements ExoPlayer.EventListener {
    private static String MEDIA_SESSION_TAG = "MEDIA_SESSION";

    private Context mContext;
    private SimpleExoPlayerView mVideoPlayerView;
    private String mVideoUrl;
    private SimpleExoPlayer mVideoPlayer;

    private MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mPlaybackStateBuilder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_video_player, container, false);

        mContext = rootView.getContext();

        initializeMediaSession();

        defineViews(rootView);

        setViewValues();

        return rootView;
    }

    public void setVideoUrl(String videoUrl) {
        this.mVideoUrl = videoUrl;
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