package com.upkipp.bakingapp.widget;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.upkipp.bakingapp.StepsAndSharedActivity;
import com.upkipp.bakingapp.models.Recipe;
import com.upkipp.bakingapp.utils.AppConstants;
import com.upkipp.bakingapp.utils.NetworkUtils;
import com.upkipp.bakingapp.utils.RecipeParser;

import org.parceler.Parcels;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WidgetService extends IntentService {
    private static final String TAG = WidgetService.class.getName();
    public static final String ACTION_GET_RECIPE_NAMES = "com.upkipp.android.bakingapp.action.get_recipe_names";
    public static final String ACTION_GET_INGREDIENTS = "com.upkipp.android.bakingapp.action.get_ingredients";
    public static final String ACTION_SAVE_AND_OR_LOAD_WIDGET_RECIPE = "com.upkipp.android.bakingapp.action.save_and_or_load_widget_recipe";

    public static final String RECIPE_LIST_KEY = "recipes_list";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
//     * @param name Used to name the worker thread, important only for debugging.
     */
    public WidgetService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent serviceIntent) {
        if (serviceIntent != null) {
            String action = serviceIntent.getAction();

             if (action.equals(ACTION_SAVE_AND_OR_LOAD_WIDGET_RECIPE)) {
                 Log.d(TAG,"starting service action: " + action);
                 saveAndOrLoadWidgetRecipe(serviceIntent);

            } else if (action.equals(ACTION_GET_RECIPE_NAMES)) {
                 Log.d(TAG,"starting service action: " + action);
                 getRecipeNames(serviceIntent);
            }
        }
    }

    public void saveAndOrLoadWidgetRecipe(Intent serviceIntent) {
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

        Recipe recipe = Parcels.unwrap
                (extras.getParcelable(StepsAndSharedActivity.SELECTED_RECIPE_KEY));

        saveRecipePreference(recipe);

        //send broadcast to update and load ingredients and recipe
        LocalBroadcastManager.getInstance(getApplicationContext())
                .sendBroadcast(serviceIntent);
    }

    //save selected recipe to shared preferences
    private void saveRecipePreference(Recipe recipe) {
        String id = recipe.getId();
        String name = recipe.getName();
        List<Map<String, String>> ingredients = recipe.getIngredients();
        List<Map<String, String>> steps = recipe.getSteps();
        String servings = recipe.getServings();
        String image = recipe.getImage();

        SharedPreferences sharedPreferences = getApplicationContext()
                .getSharedPreferences(RecipeWidgetProvider.WIDGET_PREFERENCE_PREFIX, Context.MODE_PRIVATE);

        sharedPreferences.edit()
                .putString(AppConstants.RECIPE_ID_KEY, id)

                .putString(AppConstants.RECIPE_NAME_KEY, name)

                .putStringSet(RecipeWidgetProvider.SET_INGREDIENT_QUANTITY,
                        getFormattedSet(ingredients, AppConstants.INGREDIENT_QUANTITY_KEY))

                .putStringSet(RecipeWidgetProvider.SET_INGREDIENT_MEASURE,
                        getFormattedSet(ingredients, AppConstants.INGREDIENT_MEASURE_KEY))

                .putStringSet(RecipeWidgetProvider.SET_INGREDIENT_NAME,
                        getFormattedSet(ingredients, AppConstants.INGREDIENT_NAME_KEY))

                .putStringSet(RecipeWidgetProvider.SET_STEP_ID,
                        getFormattedSet(steps, AppConstants.STEP_ID_KEY))

                .putStringSet(RecipeWidgetProvider.SET_STEP_SHORT_DESCRIPTION,
                        getFormattedSet(steps, AppConstants.STEP_SHORT_DESCRIPTION_KEY))

                .putStringSet(RecipeWidgetProvider.SET_STEP_DESCRIPTION,
                        getFormattedSet(steps, AppConstants.STEP_DESCRIPTION_KEY))

                .putStringSet(RecipeWidgetProvider.SET_STEP_VIDEO_URL,
                        getFormattedSet(steps, AppConstants.STEP_VIDEO_URL_KEY))

                .putStringSet(RecipeWidgetProvider.SET_STEP_THUMBNAIL_URL,
                        getFormattedSet(steps, AppConstants.STEP_THUMBNAIL_URL_KEY))

                .putString(AppConstants.SERVINGS_KEY, servings)

                .putString(AppConstants.IMAGE_KEY, image)

                .apply();
    }

    //format list into set format for storage un shared preferences
    private Set<String> getFormattedSet(List<Map<String, String>> list, String key) {

        Set<String> set = new HashSet<>();

        for (int index = 0; index < list.size(); index++) {
            String prefix =  String.valueOf(index) + AppConstants.WIDGET_PREF_DELINEATION;;

            Map<String, String> currentItem = list.get(index);
            String value = currentItem.get(key) ;

            set.add(prefix + value);
        }

        return set;
    }

    // get saved recipe and update widgets to start broadcast receiver
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