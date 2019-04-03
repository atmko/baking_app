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
            updateDescriptionFragment(0);
            updateVideoFragment(0);
            updateThumbnailFragment(0);
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

    private void updateDescriptionFragment(int position) {
        DescriptionFragment descriptionFragment = new DescriptionFragment();
        Map<String, String> selectedStep = mSelectedRecipe.getSteps().get(position);
        String description = selectedStep.get(AppConstants.STEP_DESCRIPTION_KEY);

        if (description == null || description.equals("")) {
            hideContainer(R.id.description_container);
            removeFragment(DetailsActivity.DESCRIPTION_FRAGMENT_TAG);

        } else {
            showContainer(R.id.description_container);
            descriptionFragment.setDescription(description);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.description_container, descriptionFragment)
                    .commit();
        }
    }

    private void updateVideoFragment(int position) {
        VideoPlayerFragment videoFragment = new VideoPlayerFragment();
        Map<String, String> selectedStep = mSelectedRecipe.getSteps().get(position);
        String videoUrl = selectedStep.get(AppConstants.STEP_VIDEO_URL_KEY);

        if (videoUrl == null || videoUrl.equals("")) {
            hideContainer(R.id.video_container);
            removeFragment(DetailsActivity.VIDEO_FRAGMENT_TAG);

        } else {
            showContainer(R.id.video_container);
            videoFragment.setVideoUrl(videoUrl);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.video_container, videoFragment)
                    .commit();
        }
    }

    private void updateThumbnailFragment(int position) {
        ThumbnailFragment thumbnailFragment = new ThumbnailFragment();
        Map<String, String> selectedStep = mSelectedRecipe.getSteps().get(position);
        String thumbnailUrl = selectedStep.get(AppConstants.STEP_THUMBNAIL_URL_KEY);

        if (thumbnailUrl == null || thumbnailUrl.equals("")) {
            hideContainer(R.id.thumbnail_container);
            removeFragment(DetailsActivity.THUMBNAIL_FRAGMENT_TAG);

        } else {
            showContainer(R.id.thumbnail_container);
            thumbnailFragment.setThumbnailUrl(thumbnailUrl);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.thumbnail_container, thumbnailFragment)
                    .commit();
        }
    }

    private void hideContainer(int viewId) {
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

    private void showContainer(int viewId) {
        findViewById(viewId).setVisibility(View.VISIBLE);
    }

    @Override
    public void onStepClick(int position) {
        if (mIsTwoPane) {
            updateDescriptionFragment(position);
            updateVideoFragment(position);
            updateThumbnailFragment(position);
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
