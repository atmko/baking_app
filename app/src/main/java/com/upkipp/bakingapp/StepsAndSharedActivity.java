package com.upkipp.bakingapp;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

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

public class StepsAndSharedActivity extends AppCompatActivity implements StepsAdapter.OnStepItemClickListener{

    public static final String SELECTED_RECIPE_KEY = "recipe";

    private Recipe mSelectedRecipe;
    private boolean mIsTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps_and_shared);

        if (savedInstanceState == null) {
            defineSelectedRecipe();

        } else {
            restoreSavedValues(savedInstanceState);
        }

        loadStepsFragment();
        defineIsTwoPane();

        if (mIsTwoPane) {
            loadFragments(0);
        }

    }

    private void defineSelectedRecipe() {
        Intent receivedIntent = getIntent();
        mSelectedRecipe = Parcels.unwrap(receivedIntent.getParcelableExtra(SELECTED_RECIPE_KEY));
    }

    private void restoreSavedValues(Bundle savedInstanceState) {
        mSelectedRecipe =
                Parcels.unwrap(savedInstanceState.getParcelable(SELECTED_RECIPE_KEY));
    }

    private void loadStepsFragment() {
        StepsFragment stepsFragment = new StepsFragment();
        stepsFragment.setIngredientsList(mSelectedRecipe.getIngredients());
        stepsFragment.setStepsList(mSelectedRecipe.getSteps());

        getSupportFragmentManager().beginTransaction()
                .add(R.id.steps_container, stepsFragment)
                .commit();
    }

    private void defineIsTwoPane() {
        mIsTwoPane = (findViewById(R.id.master_detail_flow_divider) != null);
    }

    private void loadFragments(int position) {
        Map<String, String> selectedStep = mSelectedRecipe.getSteps().get(position);
        String description = selectedStep.get(AppConstants.STEP_DESCRIPTION_KEY);
        String videoUrl = selectedStep.get(AppConstants.STEP_VIDEO_URL_KEY);
        String thumbnailUrl = selectedStep.get(AppConstants.STEP_THUMBNAIL_URL_KEY);

        boolean noDescription = description == null || description.equals("");
        boolean noThumbnail = thumbnailUrl == null || thumbnailUrl.equals("");
        boolean noVideo = videoUrl == null || videoUrl.equals("");

        if (noDescription) {
            removeView(R.id.description_container);
            removeFragment(DetailsActivity.DESCRIPTION_FRAGMENT_TAG);

        } else {
            updateDescriptionFragment(description);
        }

        if (noVideo) {
            removeView(R.id.video_container);
            removeFragment(DetailsActivity.VIDEO_FRAGMENT_TAG);

        } else {
            updateThumbnailFragment(videoUrl);
        }

        if (noThumbnail) {
            removeView(R.id.thumbnail_container);
            removeFragment(DetailsActivity.THUMBNAIL_FRAGMENT_TAG);

        } else {
            updateVideoFragment(thumbnailUrl);
        }

        if (noThumbnail && noVideo) {
            showView(R.id.placeholder_container);

        } else {
            removeView(R.id.placeholder_container);
        }
    }

    private void updateDescriptionFragment(String description) {
        DescriptionFragment descriptionFragment = new DescriptionFragment();
        descriptionFragment.setDescription(description);
        showView(R.id.description_container);
        replaceFragment(R.id.description_container, descriptionFragment);
    }

    private void updateVideoFragment(String videoUrl) {
        VideoPlayerFragment videoFragment = new VideoPlayerFragment();
        videoFragment.setVideoUrl(videoUrl);
        showView(R.id.video_container);
        replaceFragment(R.id.video_container, videoFragment);
    }

    private void updateThumbnailFragment(String thumbnailUrl) {
        ThumbnailFragment thumbnailFragment = new ThumbnailFragment();
        thumbnailFragment.setThumbnailUrl(thumbnailUrl);
        showView(R.id.thumbnail_container);
        replaceFragment(R.id.thumbnail_container, thumbnailFragment);
    }

    private void removeView(int viewId) {
        findViewById(viewId).setVisibility(View.GONE);
    }

    //TODO consider making fragment utils class to avoid repeat code in DetailsActivity
    private void removeFragment(String fragmentTag) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(fragment)
                    .commit();
        }
    }

    //TODO consider making fragment utils class to avoid repeat code in DetailsActivity
    private void replaceFragment(int containerId, Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(containerId, fragment)
                .commit();
    }

    private void showView(int viewId) {
        findViewById(viewId).setVisibility(View.VISIBLE);
    }

    @Override
    public void onStepClick(int position) {
        if (mIsTwoPane) {
            loadFragments(position);
        } else {
            startDetailsActivity(position);
        }
    }

    private void startDetailsActivity(int position) {
        List<Map<String, String>> steps = mSelectedRecipe.getSteps();
        Parcelable parceledSteps = Parcels.wrap(steps);

        Intent detailsIntent = new Intent(getApplicationContext(), DetailsActivity.class);
        detailsIntent.putExtra(DetailsActivity.STEPS_KEY, parceledSteps);
        detailsIntent.putExtra(DetailsActivity.POSITION_KEY, position);

        startActivity(detailsIntent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(SELECTED_RECIPE_KEY, Parcels.wrap(mSelectedRecipe));
    }
}
