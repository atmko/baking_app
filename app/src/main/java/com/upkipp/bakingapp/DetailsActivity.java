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

    private String mDescription;
    private String mVideoUrl;
    private String mThumbnailUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

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
        updateValues();

        if (mDescription == null || mDescription.equals("")) {
            hideContainer(R.id.description_container);
            removeFragment(DESCRIPTION_FRAGMENT_TAG);
        } else {
            showContainer(R.id.description_container);
            loadDescriptionFragment();
        }

        if (mThumbnailUrl == null || mThumbnailUrl.equals("")) {
            hideContainer(R.id.thumbnail_container);
            removeFragment(THUMBNAIL_FRAGMENT_TAG);
        } else {
            showContainer(R.id.thumbnail_container);
            loadThumbnailFragment();
        }

        if (mVideoUrl == null || mVideoUrl.equals("")) {
            hideContainer(R.id.video_container);
            removeFragment(VIDEO_FRAGMENT_TAG);
        } else {
            showContainer(R.id.video_container);
            loadVideoFragment();
        }
    }

    private void updateValues() {
        mDescription = mSteps.get(mPosition).get(AppConstants.STEP_DESCRIPTION_KEY);
        mThumbnailUrl = mSteps.get(mPosition).get(AppConstants.STEP_THUMBNAIL_URL_KEY);
        mVideoUrl = mSteps.get(mPosition).get(AppConstants.STEP_VIDEO_URL_KEY);
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

    private void loadDescriptionFragment() {
        DescriptionFragment descriptionFragment = new DescriptionFragment();
        Map<String, String> selectedStep = mSteps.get(mPosition);
        String description = selectedStep.get(AppConstants.STEP_DESCRIPTION_KEY);

        descriptionFragment.setDescription(description);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.description_container, descriptionFragment, DESCRIPTION_FRAGMENT_TAG)
                .commit();
    }

    private void loadThumbnailFragment() {
        ThumbnailFragment thumbnailFragment = new ThumbnailFragment();
        Map<String, String> selectedStep = mSteps.get(mPosition);
        String thumbnailUrl = selectedStep.get(AppConstants.STEP_THUMBNAIL_URL_KEY);

        thumbnailFragment.setThumbnailUrl(thumbnailUrl);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.thumbnail_container, thumbnailFragment, THUMBNAIL_FRAGMENT_TAG)
                .commit();
    }

    private void loadVideoFragment() {
        VideoPlayerFragment videoFragment = new VideoPlayerFragment();
        Map<String, String> selectedStep = mSteps.get(mPosition);
        String videoUrl = selectedStep.get(AppConstants.STEP_VIDEO_URL_KEY);

        videoFragment.setVideoUrl(videoUrl);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.video_container, videoFragment, VIDEO_FRAGMENT_TAG)
                .commit();
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
