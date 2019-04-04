package com.upkipp.bakingapp;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.upkipp.bakingapp.fragments.DescriptionFragment;
import com.upkipp.bakingapp.fragments.ThumbnailFragment;
import com.upkipp.bakingapp.fragments.VideoPlayerFragment;
import com.upkipp.bakingapp.utils.AppConstants;

import org.parceler.Parcels;

import java.util.List;
import java.util.Map;

public class DetailsActivity extends AppCompatActivity {

    public static final String STEPS_KEY = "steps";
    public static final String POSITION_KEY = "position";

    public static final String DESCRIPTION_FRAGMENT_TAG = "description_fragment";
    public static final String VIDEO_FRAGMENT_TAG = "video_fragment";
    public static final String THUMBNAIL_FRAGMENT_TAG = "thumbnail_fragment";

    private List<Map<String, String>> mSteps;
    private int mPosition;

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
                    && getIntent().hasExtra(POSITION_KEY)) {

                defineStepsAndPosition();
            }
        } else {
            restoreSavedValues(savedInstanceState);
        }

        loadFragments();
    }

    private void setDeviceAndVideoOrientationParameters() {
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
        mPosition = receivedIntent.getIntExtra(POSITION_KEY, 0);
    }

    private void restoreSavedValues(Bundle savedInstanceState) {
        mSteps =
                Parcels.unwrap(savedInstanceState.getParcelable(STEPS_KEY));
        mPosition = savedInstanceState.getInt(POSITION_KEY);
    }

    private void loadFragments() {
        loadDescriptionFragment();
        loadThumbnailFragment();
        loadVideoFragment();
    }

    private void loadDescriptionFragment() {
        Map<String, String> selectedStep = mSteps.get(mPosition);
        String description = selectedStep.get(AppConstants.STEP_DESCRIPTION_KEY);

        if (description == null || description.equals("")) {
            hideContainer(R.id.description_container);
            removeFragment(DESCRIPTION_FRAGMENT_TAG);

        } else {
            DescriptionFragment descriptionFragment = new DescriptionFragment();
            descriptionFragment.setDescription(description);
            showContainer(R.id.description_container);
            replaceFragment(R.id.description_container, descriptionFragment, DESCRIPTION_FRAGMENT_TAG);
        }
    }

    private void loadThumbnailFragment() {
        Map<String, String> selectedStep = mSteps.get(mPosition);
        String thumbnailUrl = selectedStep.get(AppConstants.STEP_THUMBNAIL_URL_KEY);

        if (thumbnailUrl == null || thumbnailUrl.equals("")) {
            hideContainer(R.id.thumbnail_container);
            removeFragment(THUMBNAIL_FRAGMENT_TAG);

        } else {
            ThumbnailFragment thumbnailFragment = new ThumbnailFragment();
            thumbnailFragment.setThumbnailUrl(thumbnailUrl);
            showContainer(R.id.thumbnail_container);
            replaceFragment(R.id.thumbnail_container, thumbnailFragment, THUMBNAIL_FRAGMENT_TAG);
        }
    }

    private void loadVideoFragment() {
        Map<String, String> selectedStep = mSteps.get(mPosition);
        String videoUrl = selectedStep.get(AppConstants.STEP_VIDEO_URL_KEY);

        if (videoUrl == null || videoUrl.equals("")) {
            hideContainer(mVideoContainerId);
            removeFragment(VIDEO_FRAGMENT_TAG);

        } else {
            VideoPlayerFragment videoFragment = new VideoPlayerFragment();
            videoFragment.setVideoUrl(videoUrl);
            showContainer(mVideoContainerId);
            replaceFragment(mVideoContainerId, videoFragment, VIDEO_FRAGMENT_TAG);

            if (mIsPhoneLandscape) {
                hideOtherUiElements();
                makeVideoFullScreen();
            }
        }
    }

    private void hideContainer(int viewId) {
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

    private void showContainer(int viewId) {
        findViewById(viewId).setVisibility(View.VISIBLE);
    }

    private void replaceFragment(int containerId, Fragment fragment, String fragmentTag) {
        getSupportFragmentManager().beginTransaction()
                .replace(containerId, fragment, fragmentTag)
                .commit();
    }

    private void hideOtherUiElements() {
        findViewById(R.id.details_scroll_view).setVisibility(View.GONE);
        findViewById(R.id.previous_step_button).setVisibility(View.GONE);
        findViewById(R.id.next_step_button).setVisibility(View.GONE);

        getSupportActionBar().hide();
    }

    private void makeVideoFullScreen() {
        FrameLayout videoContainer = findViewById(mVideoContainerId);
        ViewGroup.LayoutParams layoutParams = findViewById(mVideoContainerId).getLayoutParams();
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;

        videoContainer.setLayoutParams(layoutParams);
    }

    public void getNextStep(View view) {
        if (mPosition < mSteps.size() - 1) {
            mPosition += 1;
            loadFragments();
        }
    }

    public void getPreviousStep(View view) {
        if (mPosition > 0) {
            mPosition -= 1;
            loadFragments();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(STEPS_KEY, Parcels.wrap(mSteps));
        outState.putInt(POSITION_KEY, mPosition);

    }
}
