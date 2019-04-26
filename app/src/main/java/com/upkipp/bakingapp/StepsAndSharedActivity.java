package com.upkipp.bakingapp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.upkipp.bakingapp.adapters.StepsAdapter;
import com.upkipp.bakingapp.fragments.DescriptionFragment;
import com.upkipp.bakingapp.fragments.ThumbnailFragment;
import com.upkipp.bakingapp.fragments.StepsFragment;
import com.upkipp.bakingapp.fragments.VideoPlayerFragment;
import com.upkipp.bakingapp.models.Recipe;
import com.upkipp.bakingapp.models.Step;

import org.parceler.Parcels;

import java.util.List;

import static com.upkipp.bakingapp.DetailsActivity.DESCRIPTION_FRAGMENT_TAG;
import static com.upkipp.bakingapp.DetailsActivity.THUMBNAIL_FRAGMENT_TAG;
import static com.upkipp.bakingapp.DetailsActivity.VIDEO_FRAGMENT_TAG;

public class StepsAndSharedActivity extends AppCompatActivity
        implements StepsAdapter.OnStepItemClickListener, View.OnClickListener {

    public static final String SELECTED_RECIPE_KEY = "recipe";
    public static final String FULLSCREEN_REQUEST_KEY = "fullscreen_request";

    private Recipe mSelectedRecipe;
    private boolean mIsTwoPane;

    private int mStepPosition;

    boolean mIsTabletLandscape;

    //checks if video should be fullscreen after configuration changes(rotations) and other scenarios
    boolean mFullScreenRequest;

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

            loadStepsFragment();

        } else {
            restoreSavedValues(savedInstanceState);

            if (mIsTwoPane) {
                configureFragmentVisibility();
            }
        }

        setActionBarTitle();
    }

    private void setDeviceAndVideoOrientationParameters() {
        mIsTabletLandscape = getResources().getBoolean(R.bool.isTabletLandscape);
    }

    private void defineIsTwoPane() {
        mIsTwoPane = (findViewById(R.id.master_detail_flow_divider) != null);
    }

    private void defineSelectedRecipe() {
        Intent receivedIntent = getIntent();
        mSelectedRecipe = Parcels.unwrap(receivedIntent.getParcelableExtra(SELECTED_RECIPE_KEY));
        //NOTE: ingredients index is 0, steps begin at index of 1
        mStepPosition = 1;
    }

    private void loadFragments() {
        Step selectedStep = mSelectedRecipe.getAdjustedSteps().get(mStepPosition);
        String description = selectedStep.getDescription();
        String thumbnailUrl = selectedStep.getThumbnailUrl();
        String videoUrl = selectedStep.getVideoUrl();

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

        //NOTE: default value, ingredients index is 0, steps begin at index of 1
        mStepPosition =
                savedInstanceState.getInt(DetailsActivity.STEP_POSITION_KEY, 1);

        mFullScreenRequest =
                savedInstanceState.getBoolean(FULLSCREEN_REQUEST_KEY, false);
    }

    private void configureFragmentVisibility() {
        Step selectedStep = mSelectedRecipe.getAdjustedSteps().get(mStepPosition);
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

            //if video is meant tobe fullscreen after configuration change(screen rotation)
            //&& the tablet is in landscape mode
            if (mFullScreenRequest && mIsTabletLandscape) {
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

    //enable android fullscreen mode
    private void hideUiForFullscreen() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        decorView.setSystemUiVisibility(uiOptions);

        getSupportActionBar().hide();

        //hide layout views
        findViewById(R.id.container_panel).setVisibility(View.GONE);
        findViewById(R.id.master_detail_flow_divider).setVisibility(View.GONE);
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

    private void loadStepsFragment() {
        StepsFragment stepsFragment = new StepsFragment();
        stepsFragment.setIngredientsList(mSelectedRecipe.getIngredients());
        stepsFragment.setStepsList(mSelectedRecipe.getAdjustedSteps());

        getSupportFragmentManager().beginTransaction()
                .add(R.id.steps_container, stepsFragment)
                .commit();
    }

    private void setActionBarTitle() {
        if (getSupportActionBar() != null) {
            getSupportActionBar()
                    .setTitle(mSelectedRecipe.getName());
        }
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
        Step selectedStep = mSelectedRecipe.getAdjustedSteps().get(mStepPosition);
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
        List<Step> adjustedSteps = mSelectedRecipe.getAdjustedSteps();
        Parcelable parceledSteps = Parcels.wrap(adjustedSteps);

        Intent detailsIntent = new Intent(getApplicationContext(), DetailsActivity.class);
        detailsIntent.putExtra(DetailsActivity.STEPS_KEY, parceledSteps);
        detailsIntent.putExtra(DetailsActivity.STEP_POSITION_KEY, position);

        startActivity(detailsIntent);
    }

    @Override
    public void onClick(View v) {
        boolean isFullScreen = findViewById(R.id.video_container).getLayoutParams().height == ViewGroup.LayoutParams.MATCH_PARENT;

        if (!mIsTabletLandscape) {
            //make landscape via sensor. (videos locked to sensor landscape when button clicked)
            //ensure video will automatically become fullscreen when setRequestedOrientation executed
            mFullScreenRequest = true;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        } else if (mIsTabletLandscape && !isFullScreen) {
            hideUiForFullscreen();
            makeVideoFullScreen();
            //ensure video will automatically become fullscreen when setRequestedOrientation executed
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        } else if (mIsTabletLandscape && isFullScreen) {
            //remove fullscreen request
            mFullScreenRequest = false;
            showUiForRegularScreen();
            makeVideoRegularSize();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);

        }
    }

    //remove android fullscreen mode
    private void showUiForRegularScreen() {
        //remove full screen.
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
        decorView.setSystemUiVisibility(uiOptions);

        getSupportActionBar().show();

        //show layout views
        findViewById(R.id.container_panel).setVisibility(View.VISIBLE);
        findViewById(R.id.master_detail_flow_divider).setVisibility(View.VISIBLE);
        findViewById(R.id.description_container).setVisibility(View.VISIBLE);
    }

    private void makeVideoRegularSize() {
        FrameLayout frameLayout = findViewById(R.id.video_container);
        ViewGroup.LayoutParams params = frameLayout.getLayoutParams();

        params.height = Math.round(getResources().getDimension(R.dimen.fragment_container_height));
        frameLayout.setLayoutParams(params);

        LinearLayout topLayout = findViewById(R.id.top_layout);
        ViewGroup.MarginLayoutParams margins = (ViewGroup.MarginLayoutParams) topLayout.getLayoutParams();
        margins.setMargins(16, 16, 16, 16);
        topLayout.setLayoutParams(margins);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(SELECTED_RECIPE_KEY, Parcels.wrap(mSelectedRecipe));
        outState.putInt(DetailsActivity.STEP_POSITION_KEY, mStepPosition);

        outState.putBoolean(FULLSCREEN_REQUEST_KEY, mFullScreenRequest);
    }
}
