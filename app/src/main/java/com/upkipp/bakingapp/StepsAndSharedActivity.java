package com.upkipp.bakingapp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.upkipp.bakingapp.adapters.StepsAdapter;
import com.upkipp.bakingapp.fragments.DescriptionFragment;
import com.upkipp.bakingapp.fragments.ThumbnailFragment;
import com.upkipp.bakingapp.fragments.StepsFragment;
import com.upkipp.bakingapp.fragments.VideoPlayerFragment;
import com.upkipp.bakingapp.models.Recipe;
import com.upkipp.bakingapp.utils.AppConstants;

import org.parceler.Parcels;

import java.util.List;
import java.util.Map;

import static com.upkipp.bakingapp.DetailsActivity.DESCRIPTION_FRAGMENT_TAG;
import static com.upkipp.bakingapp.DetailsActivity.THUMBNAIL_FRAGMENT_TAG;
import static com.upkipp.bakingapp.DetailsActivity.VIDEO_FRAGMENT_TAG;

public class StepsAndSharedActivity extends AppCompatActivity implements StepsAdapter.OnStepItemClickListener{

    public static final String SELECTED_RECIPE_KEY = "recipe";

    private Recipe mSelectedRecipe;
    private boolean mIsTwoPane;

    private int mStepPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps_and_shared);

        setDeviceAndVideoOrientationParameters();

        defineIsTwoPane();

        if (savedInstanceState == null) {
            if (getIntent() != null
                    && getIntent().hasExtra(SELECTED_RECIPE_KEY)) {

                defineSelectedRecipe();

                if (mIsTwoPane) {
                    loadFragments();
                }
            }

        } else {
            restoreSavedValues(savedInstanceState);

            if (mIsTwoPane) {
                configureFragmentVisibility();
            }
        }

        loadStepsFragment();

    }

    private void setDeviceAndVideoOrientationParameters() {
        boolean isPhone = getResources().getBoolean(R.bool.isPhone);

        if (isPhone) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    private void defineIsTwoPane() {
        mIsTwoPane = (findViewById(R.id.master_detail_flow_divider) != null);
    }

    private void defineSelectedRecipe() {
        Intent receivedIntent = getIntent();
        mSelectedRecipe = Parcels.unwrap(receivedIntent.getParcelableExtra(SELECTED_RECIPE_KEY));
        mStepPosition = 0;
    }

    private void loadFragments() {
        Map<String, String> selectedStep = mSelectedRecipe.getSteps().get(mStepPosition);
        String description = selectedStep.get(AppConstants.STEP_DESCRIPTION_KEY);
        String thumbnailUrl = selectedStep.get(AppConstants.STEP_THUMBNAIL_URL_KEY);
        String videoUrl = selectedStep.get(AppConstants.STEP_VIDEO_URL_KEY);

        loadDescriptionFragment(description);
        loadThumbnailFragment(thumbnailUrl);
        loadVideoFragment(videoUrl);

        configureFragmentVisibility();
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

    private void restoreSavedValues(Bundle savedInstanceState) {
        mSelectedRecipe =
                Parcels.unwrap(savedInstanceState.getParcelable(SELECTED_RECIPE_KEY));
        mStepPosition =
                savedInstanceState.getInt(DetailsActivity.STEP_POSITION_KEY, 0);
    }

    private void configureFragmentVisibility() {
        Map<String, String> selectedStep = mSelectedRecipe.getSteps().get(mStepPosition);
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
        }

        if (noThumbnail && noVideo) {
            showView(R.id.placeholder_container);
        } else {
            removeView(R.id.placeholder_container);
        }
    }

    private void loadStepsFragment() {
        StepsFragment stepsFragment = new StepsFragment();
        stepsFragment.setIngredientsList(mSelectedRecipe.getIngredients());
        stepsFragment.setStepsList(mSelectedRecipe.getSteps());

        getSupportFragmentManager().beginTransaction()
                .add(R.id.steps_container, stepsFragment)
                .commit();
    }

    private void removeView(int viewId) {
        findViewById(viewId).setVisibility(View.GONE);
    }

    private void showView(int viewId) {
        findViewById(viewId).setVisibility(View.VISIBLE);
    }

    @Override
    public void onStepClick(int position) {
        mStepPosition = position;

        if (mIsTwoPane) {
            setFragmentValues();
        } else {
            startDetailsActivity(position);
        }
    }

    //TODO consider making fragment utils class to avoid repeat code in DetailsActivity
    private void setFragmentValues() {
        Map<String, String> selectedStep = mSelectedRecipe.getSteps().get(mStepPosition);
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

    private void startDetailsActivity(int position) {
        List<Map<String, String>> steps = mSelectedRecipe.getSteps();
        Parcelable parceledSteps = Parcels.wrap(steps);

        Intent detailsIntent = new Intent(getApplicationContext(), DetailsActivity.class);
        detailsIntent.putExtra(DetailsActivity.STEPS_KEY, parceledSteps);
        detailsIntent.putExtra(DetailsActivity.STEP_POSITION_KEY, position);

        startActivity(detailsIntent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(SELECTED_RECIPE_KEY, Parcels.wrap(mSelectedRecipe));
        outState.putInt(DetailsActivity.STEP_POSITION_KEY, mStepPosition);
    }
}
