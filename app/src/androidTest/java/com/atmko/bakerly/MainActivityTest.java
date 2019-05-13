/*
 * Copyright (C) 2019 Aayat Mimiko
 */

package com.atmko.bakerly;

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

import com.atmko.bakerly.adapters.RecipesAdapter;
import com.atmko.bakerly.models.Recipe;
import com.atmko.bakerly.models.Step;
import com.atmko.bakerly.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

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

        onView(ViewMatchers.withId(R.id.RecipesRecyclerView)).check(viewAssertion());

        for (int index = 0; index < recipeList.size(); index++) {

            Recipe currentRecipe = recipeList.get(index);
            onView(withId(R.id.RecipesRecyclerView)).perform(actionOnItemAtPosition(index, click()));
            List<Step> stepsList = currentRecipe.getAdjustedSteps();

            checkSteps(stepsList);
            pressBack();

        }
    }

    private void checkSteps(List<Step> stepsList) {
        //NOTE: ingredients index is 0, steps begin at index of 1
        for (int index = 1; index < stepsList.size(); index++) {
            Step currentStep = stepsList.get(index);
            String description = currentStep.getDescription();
            String thumbnailUrl = currentStep.getThumbnailUrl();
            String videoUrl = currentStep.getVideoUrl();

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