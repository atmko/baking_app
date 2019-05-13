/*
 * Copyright (C) 2019 Aayat Mimiko
 */

package com.atmko.bakerly.widget;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.JobIntentService;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.atmko.bakerly.utils.RecipeParser;
import com.atmko.bakerly.StepsAndSharedActivity;
import com.atmko.bakerly.models.Recipe;
import com.atmko.bakerly.utils.AppConstants;
import com.atmko.bakerly.utils.NetworkUtils;

import org.parceler.Parcels;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WidgetJobService extends JobIntentService {
    private static final String TAG = WidgetJobService.class.getName();
    public static final String ACTION_GET_RECIPE_NAMES = "com.upkipp.android.bakerly.action.get_recipe_names";
    public static final String ACTION_HANDSHAKE_GET_RECIPE_NAMES = "com.upkipp.android.bakerly.action.handshake_get_recipe_names";

    public static final String ACTION_SAVE_AND_OR_LOAD_WIDGET_RECIPE = "com.upkipp.android.bakerly.action.save_and_or_load_widget_recipe";
    public static final String ACTION_HANDSHAKE_SAVE_AND_OR_LOAD_WIDGET_RECIPE = "com.upkipp.android.bakerly.action.handshake_save_and_or_load_widget_recipe";

    public static final String SET_INGREDIENT_QUANTITY = "ingredients_quantity_set";
    public static final String SET_INGREDIENT_MEASURE = "ingredients_measure_set";
    public static final String SET_INGREDIENT_NAME = "ingredients_name_set";

    public static final String SET_STEP_ID = "steps_id_set";
    public static final String SET_STEP_SHORT_DESCRIPTION = "steps_shortDescription_set";
    public static final String SET_STEP_DESCRIPTION = "steps_description_set";
    public static final String SET_STEP_VIDEO_URL = "steps_videoURL_set";
    public static final String SET_STEP_THUMBNAIL_URL = "steps_thumbnailURL_set";

    public static final String RECIPE_LIST_KEY = "recipes_list";
    public static final int JOBS_ID = 22;

    @SuppressWarnings("FieldCanBeLocal")
    private final String logStartWidgetAction = "starting service action: ";

    public static void queueWork(Context context, Intent intent, int id) {
        enqueueWork(context,
                WidgetJobService.class,
                id,
                intent);
    }

    @Override
    protected void onHandleWork(@Nullable Intent serviceIntent) {
        if (serviceIntent != null) {
            String action = serviceIntent.getAction();
            //action to take as a result of received action
            String handshakeAction;

            //noinspection ConstantConditions
            if (action.equals(ACTION_SAVE_AND_OR_LOAD_WIDGET_RECIPE)) {
                handshakeAction = ACTION_HANDSHAKE_SAVE_AND_OR_LOAD_WIDGET_RECIPE;

                serviceIntent.setAction(handshakeAction);

                Log.d(TAG, logStartWidgetAction + handshakeAction);
                saveAndOrLoadWidgetRecipe(serviceIntent);

            } else if (action.equals(ACTION_GET_RECIPE_NAMES)) {
                handshakeAction = ACTION_HANDSHAKE_GET_RECIPE_NAMES;

                serviceIntent.setAction(handshakeAction);

                Log.d(TAG, logStartWidgetAction + handshakeAction);
                getRecipeNames(serviceIntent);
            }
        }
    }

    private void saveAndOrLoadWidgetRecipe(Intent serviceIntent) {
        //check if this is first run (recipe sent through intent extra)
        boolean hasRecipeExtra = serviceIntent.hasExtra(StepsAndSharedActivity.SELECTED_RECIPE_KEY);

        if (hasRecipeExtra) {
            saveAndLoadRecipe(serviceIntent);

        } else {//if first run
            loadRecipe();
        }
    }

    //save selected recipe and load into widget
    private void saveAndLoadRecipe(Intent serviceIntent) {
        Bundle extras = serviceIntent.getExtras();

        //noinspection ConstantConditions
        Recipe recipe = Parcels.unwrap
                (extras.getParcelable(StepsAndSharedActivity.SELECTED_RECIPE_KEY));

        //noinspection ConstantConditions
        saveRecipePreference(recipe);

        //send broadcast to update and load ingredients and recipe
        LocalBroadcastManager.getInstance(getApplicationContext())
                .sendBroadcast(serviceIntent);
    }

    //save selected recipe to shared preferences
    private void saveRecipePreference(Recipe recipe) {
        String id = recipe.getId();
        String name = recipe.getName();
        List<Map<String, String>> ingredients = recipe.getIngredientPrototype(recipe.getIngredients());
        List<Map<String, String>> steps = recipe.getStepPrototype(recipe.getSteps());
        String servings = recipe.getServings();
        String image = recipe.getImage();

        SharedPreferences sharedPreferences = getApplicationContext()
                .getSharedPreferences(RecipeWidgetProvider.WIDGET_PREFERENCE_PREFIX, Context.MODE_PRIVATE);

        sharedPreferences.edit()
                .putString(AppConstants.RECIPE_ID_KEY, id)

                .putString(AppConstants.RECIPE_NAME_KEY, name)

                .putStringSet(SET_INGREDIENT_QUANTITY,
                        getFormattedSet(ingredients, AppConstants.INGREDIENT_QUANTITY_KEY))

                .putStringSet(SET_INGREDIENT_MEASURE,
                        getFormattedSet(ingredients, AppConstants.INGREDIENT_MEASURE_KEY))

                .putStringSet(SET_INGREDIENT_NAME,
                        getFormattedSet(ingredients, AppConstants.INGREDIENT_NAME_KEY))

                .putStringSet(SET_STEP_ID,
                        getFormattedSet(steps, AppConstants.STEP_ID_KEY))

                .putStringSet(SET_STEP_SHORT_DESCRIPTION,
                        getFormattedSet(steps, AppConstants.STEP_SHORT_DESCRIPTION_KEY))

                .putStringSet(SET_STEP_DESCRIPTION,
                        getFormattedSet(steps, AppConstants.STEP_DESCRIPTION_KEY))

                .putStringSet(SET_STEP_VIDEO_URL,
                        getFormattedSet(steps, AppConstants.STEP_VIDEO_URL_KEY))

                .putStringSet(SET_STEP_THUMBNAIL_URL,
                        getFormattedSet(steps, AppConstants.STEP_THUMBNAIL_URL_KEY))

                .putString(AppConstants.SERVINGS_KEY, servings)

                .putString(AppConstants.IMAGE_KEY, image)

                .apply();
    }

    //format list into set format for storage in shared preferences
    private Set<String> getFormattedSet(List<Map<String, String>> list, String key) {

        Set<String> set = new HashSet<>();

        for (int index = 0; index < list.size(); index++) {
            //String.valueOf(index) makes each item unique
            String prefix = index + AppConstants.WIDGET_PREF_DELINEATION;

            Map<String, String> currentItem = list.get(index);
            String value = currentItem.get(key) ;

            set.add(prefix + value);
        }

        return set;
    }

    //get saved recipe and update widgets to start broadcast receiver
    private void loadRecipe() {
        Recipe recipe = IngredientSelectionRemoteViewsFactory.getSavedRecipe(getApplicationContext());

        RecipeWidgetProvider.updateAppWidgetIngredients(getApplicationContext(), recipe);
    }

    private void getRecipeNames(final Intent serviceIntent) {
        NetworkUtils.getAllRecipes().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String responseString) {
                List<Recipe> recipeList = RecipeParser.parseRecipes(responseString);

                Bundle extras = new Bundle();
                extras.putParcelable(RECIPE_LIST_KEY, Parcels.wrap(recipeList));

                serviceIntent.putExtras(extras);

                //send broadcast of recipe names
                LocalBroadcastManager.getInstance(getApplicationContext())
                        .sendBroadcast(serviceIntent);
            }

            @Override
            public void onError(ANError anError) {

            }
        });
    }
}