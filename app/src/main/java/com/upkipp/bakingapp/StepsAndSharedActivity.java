package com.upkipp.bakingapp;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.upkipp.bakingapp.adapters.StepsAdapter;
import com.upkipp.bakingapp.fragments.DetailsFragment;
import com.upkipp.bakingapp.fragments.StepsFragment;
import com.upkipp.bakingapp.models.Recipe;
import com.upkipp.bakingapp.utils.AppConstants;

import org.parceler.Parcels;

import java.util.List;
import java.util.Map;

public class StepsAndSharedActivity extends AppCompatActivity implements StepsAdapter.OnStepItemClickListener{
    public static final String STEPS_KEY = "steps";
    public static final String POSITION_KEY = "details";

    private static final String SELECTED_RECIPE_RESTORE_KEY = "selected_recipe";

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
            updateDetailsFragment(0);
        }

    }

    private void defineSelectedRecipe() {
        Intent receivedIntent = getIntent();
        mSelectedRecipe = Parcels.unwrap(receivedIntent.getParcelableExtra(MainActivity.SELECTED_RECIPE_KEY));
    }

    private void restoreSavedValues(Bundle savedInstanceState) {
        mSelectedRecipe =
                Parcels.unwrap(savedInstanceState.getParcelable(SELECTED_RECIPE_RESTORE_KEY));
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
        mIsTwoPane = (findViewById(R.id.details_container) != null);
    }

    @Override
    public void onStepClick(int position) {
        if (mIsTwoPane) {
            updateDetailsFragment(position);
        } else {
            startDetailsActivity(position);
        }
    }

    private void updateDetailsFragment(int position) {
        DetailsFragment detailsFragment = new DetailsFragment();
        Map<String, String> selectedStep = mSelectedRecipe.getSteps().get(position);
        String description = selectedStep.get(AppConstants.STEP_DESCRIPTION_KEY);
        String thumbnailUrl = selectedStep.get(AppConstants.STEP_THUMBNAIL_URL_KEY);
        String videoUrl = selectedStep.get(AppConstants.STEP_VIDEO_URL_KEY);

        detailsFragment.setDescription(description);
        detailsFragment.setThumbnailUrl(thumbnailUrl);
        detailsFragment.setVideoUrl(videoUrl);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.details_container, detailsFragment)
                .commit();
    }

    private void startDetailsActivity(int position) {
        List<Map<String, String>> steps = mSelectedRecipe.getSteps();
        Parcelable parceledSteps = Parcels.wrap(steps);

        Intent detailsIntent = new Intent(getApplicationContext(), DetailsActivity.class);
        detailsIntent.putExtra(STEPS_KEY, parceledSteps);
        detailsIntent.putExtra(POSITION_KEY, position);

        startActivity(detailsIntent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(SELECTED_RECIPE_RESTORE_KEY, Parcels.wrap(mSelectedRecipe));
    }

}
