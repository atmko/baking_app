package com.upkipp.bakingapp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.upkipp.bakingapp.fragments.DescriptionFragment;
import com.upkipp.bakingapp.fragments.ThumbnailFragment;
import com.upkipp.bakingapp.fragments.VideoPlayerFragment;
import com.upkipp.bakingapp.utils.AppConstants;

import org.parceler.Parcels;

import java.util.List;
import java.util.Map;

public class DetailsActivity extends AppCompatActivity {

    public static final String STEPS_KEY = "steps";
    public static final String STEP_POSITION_KEY = "step_position";

    public static final String DESCRIPTION_FRAGMENT_TAG = "description_fragment";
    public static final String VIDEO_FRAGMENT_TAG = "video_fragment";
    public static final String THUMBNAIL_FRAGMENT_TAG = "thumbnail_fragment";

    private List<Map<String, String>> mSteps;
    private int mStepPosition;

    private boolean mIsPhoneLandscape;
    private int mVideoContainerId;

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
            }
        } else {
            restoreSavedValues(savedInstanceState);
        }

        loadFragments();
    }

    private void setDeviceAndVideoOrientationParameters() {
        boolean isPhone = getResources().getBoolean(R.bool.isPhone);

        if (isPhone) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        mIsPhoneLandscape = findViewById(R.id.video_container_phone_landscape) != null;

        if (mIsPhoneLandscape) {
            mVideoContainerId = R.id.video_container_phone_landscape;
        }else {
            mVideoContainerId = R.id.video_container;
        }
    }

    private void defineStepsAndPosition() {
        Intent receivedIntent = getIntent();
        mSteps = Parcels.unwrap(receivedIntent.getParcelableExtra(STEPS_KEY));
        mStepPosition = receivedIntent.getIntExtra(STEP_POSITION_KEY, 0);
    }

    private void restoreSavedValues(Bundle savedInstanceState) {
        mSteps =
                Parcels.unwrap(savedInstanceState.getParcelable(STEPS_KEY));
        mStepPosition = savedInstanceState.getInt(STEP_POSITION_KEY);
    }

    private void loadFragments() {
        Map<String, String> selectedStep = mSteps.get(mStepPosition);
        String description = selectedStep.get(AppConstants.STEP_DESCRIPTION_KEY);
        String thumbnailUrl = selectedStep.get(AppConstants.STEP_THUMBNAIL_URL_KEY);
        String videoUrl = selectedStep.get(AppConstants.STEP_VIDEO_URL_KEY);

        boolean noDescription = description == null || description.equals("");
        boolean noThumbnail = thumbnailUrl == null || thumbnailUrl.equals("");
        boolean noVideo = videoUrl == null || videoUrl.equals("");

        if (noDescription) {
            removeView(R.id.description_container);
            removeFragment(DESCRIPTION_FRAGMENT_TAG);

        } else {
            loadDescriptionFragment(description);
        }

        if (noVideo) {
            removeView(mVideoContainerId);
            removeFragment(VIDEO_FRAGMENT_TAG);

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            if (mIsPhoneLandscape) {
                //hide regular sized video container also
                removeView(R.id.video_container);
            }

        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
            loadVideoFragment(videoUrl);
        }

        if (noThumbnail) {
            removeView(R.id.thumbnail_container);
            removeFragment(THUMBNAIL_FRAGMENT_TAG);

        } else {
            loadThumbnailFragment(thumbnailUrl);
        }

        if (noThumbnail && noVideo) {
            showView(R.id.placeholder_container);

        } else {
            removeView(R.id.placeholder_container);
        }
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
        showView(mVideoContainerId);
        replaceFragment(mVideoContainerId, videoFragment, VIDEO_FRAGMENT_TAG);

        if (mIsPhoneLandscape) {
            hideUiForFullscreen();
        }
    }

    private void removeView(int viewId) {
        findViewById(viewId).setVisibility(View.GONE);
    }

    private void removeFragment(String fragmentTag) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(fragment)
                    .commit();
        }
    }

    private void showView(int viewId) {
        findViewById(viewId).setVisibility(View.VISIBLE);
    }

    private void replaceFragment(int containerId, Fragment fragment, String fragmentTag) {
        getSupportFragmentManager().beginTransaction()
                .replace(containerId, fragment, fragmentTag)
                .commit();
    }

    private void hideUiForFullscreen() {
        findViewById(R.id.details_scroll_view).setVisibility(View.GONE);
        findViewById(R.id.back_step_button).setVisibility(View.GONE);
        findViewById(R.id.next_step_button).setVisibility(View.GONE);

        getSupportActionBar().hide();
    }

    public void getNextStep(View view) {
        if (mStepPosition < mSteps.size() - 1) {
            mStepPosition += 1;
            loadFragments();
        }
    }

    public void getPreviousStep(View view) {
        if (mStepPosition > 0) {
            mStepPosition -= 1;
            loadFragments();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(STEPS_KEY, Parcels.wrap(mSteps));
        outState.putInt(STEP_POSITION_KEY, mStepPosition);

    }
}
