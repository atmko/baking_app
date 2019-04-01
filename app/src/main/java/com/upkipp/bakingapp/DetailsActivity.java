package com.upkipp.bakingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.upkipp.bakingapp.fragments.DetailsFragment;
import com.upkipp.bakingapp.utils.AppConstants;

import org.parceler.Parcels;

import java.util.List;
import java.util.Map;

public class DetailsActivity extends AppCompatActivity {

    public static final String STEPS_KEY = "steps";
    public static final String POSITION_KEY = "position";

    private List<Map<String, String>> mSteps;
    private int mPosition;

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

        loadDetailsFragment();
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

    private void loadDetailsFragment() {
        DetailsFragment detailsFragment = new DetailsFragment();
        Map<String, String> selectedStep = mSteps.get(mPosition);
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

    public void getNextStep(View view) {
        if (mPosition < mSteps.size() - 1)
        mPosition += 1;
        loadDetailsFragment();
    }

    public void getPreviousStep(View view) {
        if (mPosition > 0) {
            mPosition -= 1;
            loadDetailsFragment();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(STEPS_KEY, Parcels.wrap(mSteps));
        outState.putInt(POSITION_KEY, mPosition);

    }

}
