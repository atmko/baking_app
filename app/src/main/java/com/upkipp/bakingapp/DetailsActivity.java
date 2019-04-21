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

import com.upkipp.bakingapp.fragments.DescriptionFragment;
import com.upkipp.bakingapp.fragments.ThumbnailFragment;
import com.upkipp.bakingapp.fragments.VideoPlayerFragment;
import com.upkipp.bakingapp.utils.AppConstants;

import org.parceler.Parcels;

import java.util.List;
import java.util.Map;

public class DetailsActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String STEPS_KEY = "steps";
    public static final String STEP_POSITION_KEY = "step_position";
    public static final String ORIENTATION_STATE_LIST_KEY = "orientation_request";

    public static final String DESCRIPTION_FRAGMENT_TAG = "description_fragment";
    public static final String VIDEO_FRAGMENT_TAG = "video_fragment";
    public static final String THUMBNAIL_FRAGMENT_TAG = "thumbnail_fragment";

    private List<Map<String, String>> mSteps;
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
        mSteps = Parcels.unwrap(receivedIntent.getParcelableExtra(STEPS_KEY));
        mStepPosition = receivedIntent.getIntExtra(STEP_POSITION_KEY, 0);
    }

    private void defineOrientationStateList() {
        mOrientationStateList = new boolean[2];
        mOrientationStateList[0] = mIsPhoneLandscape;
        mOrientationStateList[1] = mIsPhoneLandscape;
    }

    private void loadFragments() {
        Map<String, String> selectedStep = mSteps.get(mStepPosition);
        String description = selectedStep.get(AppConstants.STEP_DESCRIPTION_KEY);
        String thumbnailUrl = selectedStep.get(AppConstants.STEP_THUMBNAIL_URL_KEY);
        String videoUrl = selectedStep.get(AppConstants.STEP_VIDEO_URL_KEY);

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
        mSteps =
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
        Map<String, String> selectedStep = mSteps.get(mStepPosition);
        String description = selectedStep.get(AppConstants.STEP_DESCRIPTION_KEY);
        String thumbnailUrl = selectedStep.get(AppConstants.STEP_THUMBNAIL_URL_KEY);
        String videoUrl = selectedStep.get(AppConstants.STEP_VIDEO_URL_KEY);

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
                    .setTitle(mSteps.get(mStepPosition).get(AppConstants.STEP_SHORT_DESCRIPTION_KEY));
        }
    }

    private void hideUiForFullscreen() {
        getSupportActionBar().hide();
        findViewById(R.id.right_panel).setVisibility(View.GONE);
        findViewById(R.id.divider_line).setVisibility(View.GONE);
        findViewById(R.id.description_container).setVisibility(View.GONE);
        findViewById(R.id.thumbnail_container).setVisibility(View.GONE);
        findViewById(R.id.placeholder_container).setVisibility(View.GONE);
    }

    private void makeVideoFullScreen() {
        FrameLayout frameLayout = findViewById(R.id.video_container);
        ViewGroup.LayoutParams params = frameLayout.getLayoutParams();
        ViewGroup.MarginLayoutParams margins = (ViewGroup.MarginLayoutParams) frameLayout.getLayoutParams();
        margins.setMargins(0,0,0,0);
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;

        frameLayout.setLayoutParams(margins);
        frameLayout.setLayoutParams(params);
    }

    public void getNextStep(View view) {
        if (mStepPosition < mSteps.size() - 1) {
            mStepPosition += 1;
            //update orientation state to prevent initial fullscreen on new step on landscape
            updateOrientationStateList();
            setActionBarTitle();
            setFragmentValues();
        }
    }

    public void getPreviousStep(View view) {
        if (mStepPosition > 0) {
            mStepPosition -= 1;
            //update orientation state to prevent initial fullscreen on new step on landscape
            updateOrientationStateList();
            setActionBarTitle();
            setFragmentValues();
        }
    }

    private void setFragmentValues() {
        Map<String, String> selectedStep = mSteps.get(mStepPosition);
        String description = selectedStep.get(AppConstants.STEP_DESCRIPTION_KEY);
        String thumbnailUrl = selectedStep.get(AppConstants.STEP_THUMBNAIL_URL_KEY);
        String videoUrl = selectedStep.get(AppConstants.STEP_VIDEO_URL_KEY);

        boolean hasDescription = description != null && !description.equals("");
        boolean hasThumbnail = thumbnailUrl != null && !thumbnailUrl.equals("");
        boolean hasVideo = videoUrl != null && !videoUrl.equals("");

        FragmentManager fragmentManager = getSupportFragmentManager();
        VideoPlayerFragment videoPlayerFragment = (VideoPlayerFragment) fragmentManager.findFragmentByTag(VIDEO_FRAGMENT_TAG);

        if (hasDescription) {
            showView(R.id.description_container);
            DescriptionFragment descriptionFragment = (DescriptionFragment) fragmentManager.findFragmentByTag(DESCRIPTION_FRAGMENT_TAG);
            descriptionFragment.setDescription(description);
            descriptionFragment.reloadMedia();
        }

        if (hasThumbnail) {
            showView(R.id.thumbnail_container);
            ThumbnailFragment thumbnailFragment = (ThumbnailFragment) fragmentManager.findFragmentByTag(THUMBNAIL_FRAGMENT_TAG);
            thumbnailFragment.setThumbnailUrl(thumbnailUrl);
            thumbnailFragment.reloadMedia();
        }

        if (hasVideo) {
            showView(R.id.video_container);
            videoPlayerFragment.setVideoUrl(videoUrl);
            videoPlayerFragment.reloadMedia();
        } else {
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

        } else if (mIsPhoneLandscape && !isFullScreen) {
            hideUiForFullscreen();
            makeVideoFullScreen();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        } else if (mIsPhoneLandscape && isFullScreen) {
            showUiForRegularScreen();
            makeVideoRegularSize();
            //make landscape via sensor. (videos locked to sensor landscape when button clicked)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);

        }
    }

    private void showUiForRegularScreen() {
        findViewById(R.id.right_panel).setVisibility(View.VISIBLE);
        findViewById(R.id.divider_line).setVisibility(View.VISIBLE);
        findViewById(R.id.description_container).setVisibility(View.VISIBLE);

        getSupportActionBar().show();
    }

    private void makeVideoRegularSize() {
        FrameLayout frameLayout = findViewById(R.id.video_container);
        ViewGroup.LayoutParams params = frameLayout.getLayoutParams();
        ViewGroup.MarginLayoutParams margins = (ViewGroup.MarginLayoutParams) frameLayout.getLayoutParams();
        margins.setMargins(8,8,8,8);
        params.height = Math.round(getResources().getDimension(R.dimen.fragment_container_height));

        frameLayout.setLayoutParams(margins);
        frameLayout.setLayoutParams(params);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(STEPS_KEY, Parcels.wrap(mSteps));
        outState.putInt(STEP_POSITION_KEY, mStepPosition);

        outState.putBooleanArray(ORIENTATION_STATE_LIST_KEY, mOrientationStateList);
    }}
