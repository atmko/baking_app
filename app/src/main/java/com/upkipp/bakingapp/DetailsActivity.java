/*
 * Copyright (C) 2019 Aayat Mimiko
 */

package com.upkipp.bakingapp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.upkipp.bakingapp.fragments.DescriptionFragment;
import com.upkipp.bakingapp.fragments.ThumbnailFragment;
import com.upkipp.bakingapp.fragments.VideoPlayerFragment;
import com.upkipp.bakingapp.models.Step;

import org.parceler.Parcels;

import java.util.List;

public class DetailsActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String STEPS_KEY = "steps";
    public static final String STEP_POSITION_KEY = "step_position";
    private static final String ORIENTATION_STATE_LIST_KEY = "orientation_request";

    public static final String DESCRIPTION_FRAGMENT_TAG = "description_fragment";
    public static final String VIDEO_FRAGMENT_TAG = "video_fragment";
    public static final String THUMBNAIL_FRAGMENT_TAG = "thumbnail_fragment";

    private List<Step> mAdjustedSteps;
    private int mStepPosition;

    private boolean mIsPhoneLandscape;

    //tracks the orientation state of device through recreations e.g portrait to landscape
    //boolean values are based upon mIsPhoneLandscape values where true value == mIsPhoneLandscape
    private boolean[] mOrientationStateList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        setDeviceAndVideoOrientationParameters();

        if (savedInstanceState == null) {
            if (getIntent() != null
                    && getIntent().hasExtra(STEPS_KEY)
                    && getIntent().hasExtra(STEP_POSITION_KEY)) {

                defineStepsAndPosition();
                defineOrientationStateList();
                loadFragments();
            }

        } else {
            restoreSavedValues(savedInstanceState);
            updateOrientationStateList();
        }

        configureFragmentVisibility();

        setActionBarTitle();
    }

    private void setDeviceAndVideoOrientationParameters() {
        mIsPhoneLandscape = getResources().getBoolean(R.bool.isPhoneLandscape);
    }

    private void defineStepsAndPosition() {
        Intent receivedIntent = getIntent();
        mAdjustedSteps = Parcels.unwrap(receivedIntent.getParcelableExtra(STEPS_KEY));
        mStepPosition = receivedIntent.getIntExtra(STEP_POSITION_KEY, 0);
    }

    private void defineOrientationStateList() {
        mOrientationStateList = new boolean[2];
        mOrientationStateList[0] = mIsPhoneLandscape;
        mOrientationStateList[1] = mIsPhoneLandscape;
    }

    private void loadFragments() {
        Step selectedStep = mAdjustedSteps.get(mStepPosition);
        String description = selectedStep.getDescription();
        String thumbnailUrl = selectedStep.getThumbnailUrl();
        String videoUrl = selectedStep.getVideoUrl();

        loadDescriptionFragment(description);
        loadThumbnailFragment(thumbnailUrl);
        loadVideoFragment(videoUrl);
    }

    private void loadDescriptionFragment(String description) {
        DescriptionFragment descriptionFragment = new DescriptionFragment();
        descriptionFragment.setDescription(description);
        showView(R.id.description_container);
        replaceFragment(R.id.description_container, descriptionFragment, DESCRIPTION_FRAGMENT_TAG);
    }

    private void loadThumbnailFragment(String thumbnailUrl) {
        ThumbnailFragment thumbnailFragment = new ThumbnailFragment();
        thumbnailFragment.setThumbnailUrl(thumbnailUrl);
        showView(R.id.thumbnail_container);
        replaceFragment(R.id.thumbnail_container, thumbnailFragment, THUMBNAIL_FRAGMENT_TAG);
    }

    private void loadVideoFragment(String videoUrl) {
        VideoPlayerFragment videoFragment = new VideoPlayerFragment();
        videoFragment.setVideoUrl(videoUrl);
        showView(R.id.video_container);
        replaceFragment(R.id.video_container, videoFragment, VIDEO_FRAGMENT_TAG);
    }

    private void replaceFragment(int containerId, Fragment fragment, String fragmentTag) {
        getSupportFragmentManager().beginTransaction()
                .replace(containerId, fragment, fragmentTag)
                .commit();
    }

    private void removeView(int viewId) {
        findViewById(viewId).setVisibility(View.GONE);
    }

    private void showView(int viewId) {
        findViewById(viewId).setVisibility(View.VISIBLE);
    }

    private void restoreSavedValues(Bundle savedInstanceState) {
        mAdjustedSteps =
                Parcels.unwrap(savedInstanceState.getParcelable(STEPS_KEY));
        mStepPosition = savedInstanceState.getInt(STEP_POSITION_KEY);

        mOrientationStateList = savedInstanceState.getBooleanArray(ORIENTATION_STATE_LIST_KEY);
    }

    private void updateOrientationStateList() {
        //update orientation history.
        // from (A, B) to (B, C)
        // where A is removed,
        // B is pushed back in history,
        // C is newly added value
        mOrientationStateList[0] = mOrientationStateList[1] ;
        mOrientationStateList[1] = mIsPhoneLandscape;
    }

    private void configureFragmentVisibility() {
        Step selectedStep = mAdjustedSteps.get(mStepPosition);
        String description = selectedStep.getDescription();
        String thumbnailUrl = selectedStep.getThumbnailUrl();
        String videoUrl = selectedStep.getVideoUrl();

        boolean noDescription = description == null || description.equals("");
        boolean noThumbnail = thumbnailUrl == null || thumbnailUrl.equals("");
        boolean noVideo = videoUrl == null || videoUrl.equals("");

        if (noDescription) {
            removeView(R.id.description_container);
        } else {
            showView(R.id.description_container);
        }

        if (noThumbnail) {
            removeView(R.id.thumbnail_container);
        } else {
            showView(R.id.thumbnail_container);
        }

        if (noVideo) {
            removeView(R.id.video_container);

        } else {
            showView(R.id.video_container);

            //if previous orientation was not landscape(portrait) && orientation is now landscape
            //noinspection PointlessBooleanExpression
            if (mOrientationStateList[0] == false && mIsPhoneLandscape) {
                hideUiForFullscreen();
                makeVideoFullScreen();
            }
        }

        if (noThumbnail && noVideo) {
            showView(R.id.placeholder_container);
        } else {
            removeView(R.id.placeholder_container);
        }
    }

    private void setActionBarTitle() {
        if (getSupportActionBar() != null) {
            getSupportActionBar()
                    .setTitle(mAdjustedSteps.get(mStepPosition).getShortDescription());
        }
    }

    //enable android fullscreen mode
    private void hideUiForFullscreen() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        decorView.setSystemUiVisibility(uiOptions);

        //noinspection ConstantConditions
        getSupportActionBar().hide();

        //hide layout views
        findViewById(R.id.right_panel).setVisibility(View.GONE);
        findViewById(R.id.divider_line).setVisibility(View.GONE);
        findViewById(R.id.description_container).setVisibility(View.GONE);
        findViewById(R.id.thumbnail_container).setVisibility(View.GONE);
        findViewById(R.id.placeholder_container).setVisibility(View.GONE);
    }

    private void makeVideoFullScreen() {
        FrameLayout frameLayout = findViewById(R.id.video_container);
        ViewGroup.LayoutParams params = frameLayout.getLayoutParams();

        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        frameLayout.setLayoutParams(params);

        LinearLayout topLayout = findViewById(R.id.top_layout);
        ViewGroup.MarginLayoutParams margins = (ViewGroup.MarginLayoutParams) topLayout.getLayoutParams();
        margins.setMargins(0,0,0,0);
        topLayout.setLayoutParams(margins);
    }

    public void getNextStep(View view) {
//        if position is not last position go to next step
        if (mStepPosition < mAdjustedSteps.size() - 1) {
            mStepPosition += 1;
            //update orientation state to prevent initial fullscreen on new step on landscape
            updateOrientationStateList();
            setActionBarTitle();
            setFragmentValues();
        }
    }

    public void getPreviousStep(View view) {
        //if position is not first position go to previous step
        //NOTE: ingredients index is 0, steps begin at index of 1
        if (mStepPosition > 1) {
            mStepPosition -= 1;
            //update orientation state to prevent initial fullscreen on new step on landscape
            updateOrientationStateList();
            setActionBarTitle();
            setFragmentValues();
        }
    }

    private void setFragmentValues() {
        Step selectedStep = mAdjustedSteps.get(mStepPosition);
        String description = selectedStep.getDescription();
        String thumbnailUrl = selectedStep.getThumbnailUrl();
        String videoUrl = selectedStep.getVideoUrl();

        boolean hasDescription = description != null && !description.equals("");
        boolean hasThumbnail = thumbnailUrl != null && !thumbnailUrl.equals("");
        boolean hasVideo = videoUrl != null && !videoUrl.equals("");

        FragmentManager fragmentManager = getSupportFragmentManager();
        VideoPlayerFragment videoPlayerFragment = (VideoPlayerFragment) fragmentManager.findFragmentByTag(VIDEO_FRAGMENT_TAG);

        if (hasDescription) {
            showView(R.id.description_container);
            DescriptionFragment descriptionFragment = (DescriptionFragment) fragmentManager.findFragmentByTag(DESCRIPTION_FRAGMENT_TAG);

            assert descriptionFragment != null;
            descriptionFragment.setDescription(description);
            descriptionFragment.reloadMedia();
        }

        if (hasThumbnail) {
            showView(R.id.thumbnail_container);
            ThumbnailFragment thumbnailFragment = (ThumbnailFragment) fragmentManager.findFragmentByTag(THUMBNAIL_FRAGMENT_TAG);

            assert thumbnailFragment != null;
            thumbnailFragment.setThumbnailUrl(thumbnailUrl);
            thumbnailFragment.reloadMedia();
        }

        if (hasVideo) {
            showView(R.id.video_container);

            assert videoPlayerFragment != null;
            videoPlayerFragment.setVideoUrl(videoUrl);
            videoPlayerFragment.reloadMedia();
        } else {
            assert videoPlayerFragment != null;
            videoPlayerFragment.stopVideoPlayer();
        }

        configureFragmentVisibility();
    }

    //fullscreen button click listener
    @Override
    public void onClick(View v) {
        boolean isFullScreen = findViewById(R.id.video_container).getLayoutParams().height == ViewGroup.LayoutParams.MATCH_PARENT;

        if (!mIsPhoneLandscape) {
            //video will automatically become fullscreen when setRequestedOrientation executed
            //make landscape via sensor. (videos locked to sensor landscape when button clicked)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        } else //noinspection ConstantConditions
            if (mIsPhoneLandscape && !isFullScreen) {
            hideUiForFullscreen();
            makeVideoFullScreen();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        } else //noinspection ConstantConditions
                if (mIsPhoneLandscape && isFullScreen) {
            showUiForRegularScreen();
            makeVideoRegularSize();
            //make landscape via sensor. (videos locked to sensor landscape when button clicked)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
        }
    }

    //remove android fullscreen mode
    private void showUiForRegularScreen() {
        //remove full screen.
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
        decorView.setSystemUiVisibility(uiOptions);

        //noinspection ConstantConditions
        getSupportActionBar().show();

        //show layout views
        findViewById(R.id.right_panel).setVisibility(View.VISIBLE);
        findViewById(R.id.divider_line).setVisibility(View.VISIBLE);
        findViewById(R.id.description_container).setVisibility(View.VISIBLE);
    }

    private void makeVideoRegularSize() {
        FrameLayout frameLayout = findViewById(R.id.video_container);
        ViewGroup.LayoutParams params = frameLayout.getLayoutParams();

        params.height = Math.round(getResources().getDimension(R.dimen.fragment_container_height));
        frameLayout.setLayoutParams(params);

        LinearLayout topLayout = findViewById(R.id.top_layout);
        ViewGroup.MarginLayoutParams margins = (ViewGroup.MarginLayoutParams) topLayout.getLayoutParams();
        //TODO: find out why margins have to be quadrupled(instead of using proper double margins)
        int marginSize = Math.round(getResources().getDimension(R.dimen.quadruple_layout_margin));

        margins.setMargins(marginSize, marginSize, marginSize, marginSize);
        topLayout.setLayoutParams(margins);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(STEPS_KEY, Parcels.wrap(mAdjustedSteps));
        outState.putInt(STEP_POSITION_KEY, mStepPosition);

        outState.putBooleanArray(ORIENTATION_STATE_LIST_KEY, mOrientationStateList);
    }
}
