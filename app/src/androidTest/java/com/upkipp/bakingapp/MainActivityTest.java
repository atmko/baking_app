package com.upkipp.bakingapp;

import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.upkipp.bakingapp.adapters.RecipesAdapter;
import com.upkipp.bakingapp.models.Recipe;
import com.upkipp.bakingapp.utils.AppConstants;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Map;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    private List<Recipe> recipeList;

    @Rule
    public ActivityTestRule<MainActivity> mainActivityTestRule
            = new ActivityTestRule<>(MainActivity.class);
    private IdlingResource mIdlingResource;

    @Before
    public void registerIdlingResource() {
        mIdlingResource = mainActivityTestRule.getActivity().getIdlingResource();
        IdlingRegistry.getInstance().register(mIdlingResource);
    }

    @Test
    public void user_Interaction_Test() {

        onView(withId(R.id.RecipesRecyclerView)).check(viewAssertion());

        for (int index = 0; index < recipeList.size(); index++) {

            Recipe currentRecipe = recipeList.get(index);
            onView(withId(R.id.RecipesRecyclerView)).perform(actionOnItemAtPosition(index, click()));
            List<Map<String, String>> stepsList = currentRecipe.getSteps();

            checkSteps(stepsList);
            pressBack();

        }
    }

    private void checkSteps(List<Map<String, String>> stepsList) {
        for (int index = 0; index < stepsList.size(); index++) {
            Map<String, String> currentStep = stepsList.get(index);
            String description = currentStep.get(AppConstants.STEP_DESCRIPTION_KEY);
            String thumbnailUrl = currentStep.get(AppConstants.STEP_THUMBNAIL_URL_KEY);
            String videoUrl = currentStep.get(AppConstants.STEP_VIDEO_URL_KEY);

            onView(withId(R.id.steps_recycler_view)).perform(actionOnItemAtPosition(index, click()));

            if (description != null && !description.equals("")) {
                onView(withId(R.id.description_text_view)).check(matches(withText(description)));
                onView(withId(R.id.description_container)).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

            } else {
                onView(withId(R.id.description_container)).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

            }

            if (thumbnailUrl != null && !thumbnailUrl.equals("")) {
                onView(withId(R.id.thumbnail_container)).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

            } else {
                onView(withId(R.id.thumbnail_container)).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

            }

            if (videoUrl != null && !videoUrl.equals("")) {
                onView(withId(R.id.video_container)).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

            } else {
                onView(withId(R.id.video_container)).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

            }

            if (mainActivityTestRule.getActivity().getResources().getBoolean(R.bool.isPhone)) {
                pressBack();

            }
        }
    }

    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            IdlingRegistry.getInstance().unregister(mIdlingResource);
        }
    }

    private ViewAssertion viewAssertion() {
        return new ViewAssertion() {
            @Override
            public void check(View view, NoMatchingViewException noViewFoundException) {
                if (noViewFoundException != null) throw noViewFoundException;

                RecyclerView recyclerView = (RecyclerView) view;
                RecipesAdapter recipesAdapter = (RecipesAdapter) recyclerView.getAdapter();
                if (recipesAdapter != null) {
                    recipeList = recipesAdapter.getRecipeList();
                }
            }
        };
    }
}