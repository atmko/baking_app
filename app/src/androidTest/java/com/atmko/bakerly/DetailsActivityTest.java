/*
 * Copyright (C) 2019 Aayat Mimiko
 */

package com.atmko.bakerly;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Parcelable;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;

import com.atmko.bakerly.models.Step;
import com.atmko.bakerly.R;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.parceler.Parcels;

import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertTrue;

//NOTE tests are base on data passed into activity through intents. Changing this data may cause tests to fail
//Note: for phone testing
@RunWith(AndroidJUnit4.class)
public class DetailsActivityTest {

    @Rule
    public ActivityTestRule<DetailsActivity> detailsActivityTestRule
            = new ActivityTestRule<>(DetailsActivity.class, true, false);

    @Test
    public void Test_Auto_Fullscreen_Video_On_Orientation_Change_To_Landscape() {
        startActivityWithSteps();

        //orientation change using view action code sourced from: https://gist.github.com/nbarraille/03e8910dc1d415ed9740
        onView(isRoot()).perform(orientationChange(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE));

        onView(ViewMatchers.withId(R.id.right_panel)).check(matches(not(isDisplayed())));
        onView(withId(R.id.divider_line)).check(matches(not(isDisplayed())));
        onView(withId(R.id.description_container)).check(matches(not(isDisplayed())));
        onView(withId(R.id.thumbnail_container)).check(matches(not(isDisplayed())));
        onView(withId(R.id.placeholder_container)).check(matches(not(isDisplayed())));

        //check phone is landscape
        // target context is main (not test) application context
        assertTrue(InstrumentationRegistry.getTargetContext().getResources().getBoolean(R.bool.isPhoneLandscape));
    }

    @Test
    public void isNotPhoneLandscape_Video_Fullscreen_Button_Click() {
        startActivityWithSteps();

        //orientation change using view action code sourced from: https://gist.github.com/nbarraille/03e8910dc1d415ed9740
        onView(isRoot()).perform(orientationChange(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT));

        onView(withId(R.id.exo_fullscreen)).perform(click());

        onView(withId(R.id.right_panel)).check(matches(not(isDisplayed())));
        onView(withId(R.id.divider_line)).check(matches(not(isDisplayed())));
        onView(withId(R.id.description_container)).check(matches(not(isDisplayed())));
        onView(withId(R.id.thumbnail_container)).check(matches(not(isDisplayed())));
        onView(withId(R.id.placeholder_container)).check(matches(not(isDisplayed())));

        //check phone is landscape
        //target context is main (not test) application context
        assertTrue(InstrumentationRegistry.getTargetContext().getResources().getBoolean(R.bool.isPhoneLandscape));
    }

    @Test
    public void isPhoneLandscape_Video_Fullscreen_Button_Click() {
        startActivityWithSteps();

        //make fullscreen and landscape
        onView(withId(R.id.exo_fullscreen)).perform(click());

        //test fullscreen
        onView(withId(R.id.right_panel)).check(matches(not(isDisplayed())));
        onView(withId(R.id.divider_line)).check(matches(not(isDisplayed())));
        onView(withId(R.id.description_container)).check(matches(not(isDisplayed())));
        onView(withId(R.id.thumbnail_container)).check(matches(not(isDisplayed())));
        onView(withId(R.id.placeholder_container)).check(matches(not(isDisplayed())));

        //check phone is landscape
        //target context is main (not test) application context
        assertTrue(InstrumentationRegistry.getTargetContext().getResources().getBoolean(R.bool.isPhoneLandscape));

        //remove fullscreen
        onView(withId(R.id.exo_fullscreen)).perform(click());

        //test remove fullscreen
        if (InstrumentationRegistry.getTargetContext().getResources().getBoolean(R.bool.isPhoneLandscape)) {
            onView(withId(R.id.right_panel)).check(matches(isDisplayed()));
            onView(withId(R.id.divider_line)).check(matches(isDisplayed()));
            onView(withId(R.id.description_container)).check(matches(isDisplayed()));
            onView(withId(R.id.thumbnail_container)).check(matches(not(isDisplayed())));
            onView(withId(R.id.placeholder_container)).check(matches(not(isDisplayed())));

            //check phone is landscape
            //target context is main (not test) application context
            assertTrue(InstrumentationRegistry.getTargetContext().getResources().getBoolean(R.bool.isPhoneLandscape));
        } else {
            throw new Error("1. Ensure screen auto rotate is on to pass test." +"2. Ensure device in landscape to pass test");
        }
    }

    @Test
    public void fragment_Visibility_Test() {
        List<Step> stepList = startActivityWithSteps();

        for (int index = 0; index < stepList.size(); index++) {

            Step currentStep = stepList.get(index);

            String description = currentStep.getDescription();
            String videoUrl = currentStep.getVideoUrl();
            String thumbnailUrl = currentStep.getThumbnailUrl();

            if (description != null && !description.equals("")) {
                onView(withId(R.id.description_container)).check(matches(isDisplayed()));
            } else {
                onView(withId(R.id.description_container)).check(matches(not(isDisplayed())));
            }

            if (videoUrl != null && !videoUrl.equals("")) {
                onView(withId(R.id.video_container)).check(matches(isDisplayed()));
            } else {
                onView(withId(R.id.video_container)).check(matches(not(isDisplayed())));
            }

            if (thumbnailUrl != null && !thumbnailUrl.equals("")) {
                onView(withId(R.id.thumbnail_container)).check(matches(isDisplayed()));
            } else {
                onView(withId(R.id.thumbnail_container)).check(matches(not(isDisplayed())));
            }
            onView(withId(R.id.next_step_button)).perform(click());
        }
    }

    private List<Step> startActivityWithSteps() {
        List<Step> stepsList = TestConstants.getTestSteps();

        Intent testIntent = new Intent();

        Parcelable parceledSteps = Parcels.wrap(stepsList);
        int stepPosition = 0;

        testIntent.putExtra(DetailsActivity.STEPS_KEY, parceledSteps);
        testIntent.putExtra(DetailsActivity.STEP_POSITION_KEY, stepPosition);

        detailsActivityTestRule.launchActivity(testIntent);

        return stepsList;
}

    private ViewAction orientationChange(final int orientation) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "change orientation to " + orientation;
            }

            @Override
            public void perform(UiController uiController, View view) {

                Activity activity = getActivity(view.getContext());
                if (activity == null && view instanceof ViewGroup) {
                    ViewGroup v = (ViewGroup)view;
                    int c = v.getChildCount();
                    for (int i = 0; i < c && activity == null; ++i) {
                        activity = getActivity(v.getChildAt(i).getContext());
                    }
                }

                if (activity != null) {
                    activity.setRequestedOrientation(orientation);
                }
            }
        };
    }

    private static Activity getActivity(Context context) {
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity)context;
            }
            context = ((ContextWrapper)context).getBaseContext();
        }
        return null;
    }

}
