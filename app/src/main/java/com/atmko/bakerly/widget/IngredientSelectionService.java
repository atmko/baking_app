/*
 * Copyright (C) 2019 Aayat Mimiko
 */

package com.atmko.bakerly.widget;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.atmko.bakerly.StepsAndSharedActivity;
import com.atmko.bakerly.models.Ingredient;
import com.atmko.bakerly.models.Recipe;
import com.atmko.bakerly.models.Step;
import com.atmko.bakerly.utils.AppConstants;
import com.atmko.bakerly.R;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//remote views service for widget
public class IngredientSelectionService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new IngredientSelectionRemoteViewsFactory(this.getApplicationContext());
    }
}

//remote view factory for widget
class IngredientSelectionRemoteViewsFactory extends  BroadcastReceiver
        implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    //field for knowing whether to load ingredients or load recipes
    private int mState;

    //list of data that gets displayed in list view
    private String[] mDisplayList;

    private List<Recipe> mRecipeList;
    private Recipe mRecipe;

    public IngredientSelectionRemoteViewsFactory(Context context) {
        mContext = context;
        mState = RecipeWidgetProvider.STATE_INGREDIENTS;

        //recipe list will be received later
        mRecipeList = null;
        mRecipe = getSavedRecipe(context);
        mDisplayList = mRecipe.getIngredientNames();

        //register local broadcast receiver
        registerBroadcastReceiver();
    }

    private void registerBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WidgetJobService.ACTION_HANDSHAKE_GET_RECIPE_NAMES);
        intentFilter.addAction(WidgetJobService.ACTION_HANDSHAKE_SAVE_AND_OR_LOAD_WIDGET_RECIPE);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(IngredientSelectionRemoteViewsFactory.this,
                intentFilter);
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
        int[] appWidgetIds = RecipeWidgetProvider.getAppWidgetIds(context, appWidgetManager);

        String action = intent.getAction();

        //noinspection ConstantConditions
        if (action.equals(WidgetJobService.ACTION_HANDSHAKE_SAVE_AND_OR_LOAD_WIDGET_RECIPE)) {
            //update relevant set values
            mState = RecipeWidgetProvider.STATE_INGREDIENTS;
            mRecipe = getSavedRecipe(mContext);

        } else if (action.equals(WidgetJobService.ACTION_HANDSHAKE_GET_RECIPE_NAMES)) {
            //update relevant set values
            mState = RecipeWidgetProvider.STATE_RECIPE;
            Parcelable recipeListParcel = intent.getParcelableExtra(WidgetJobService.RECIPE_LIST_KEY);
            mRecipeList = Parcels.unwrap(recipeListParcel);
        }

        //notify data changed
        appWidgetManager.notifyAppWidgetViewDataChanged
                (appWidgetIds, R.id.widget_ingredient_names_list_view);
    }

    @Override
    public void onDataSetChanged() {
        //update widget data according to widget state
        if (mState == RecipeWidgetProvider.STATE_INGREDIENTS) {
            mDisplayList = mRecipe.getIngredientNames();
            RecipeWidgetProvider.updateAppWidgetIngredients(mContext, mRecipe);

        } else {
            mDisplayList = getNameList(mRecipeList);
            RecipeWidgetProvider.updateAppWidgetRecipes(mContext);
        }
    }

    private static String[] getNameList(List<Recipe> recipeList) {
        int listSize = recipeList.size();

        String[] recipeNameList = new String[listSize];
        for (int index = 0; index < listSize; index++) {
            recipeNameList[index] = recipeList.get(index).getName();

        }

        return recipeNameList;
    }

    //gets recipe saved in shared preferences
    public static Recipe getSavedRecipe(Context context) {
        SharedPreferences sharedPreferences = context
                .getSharedPreferences(RecipeWidgetProvider.WIDGET_PREFERENCE_PREFIX, Context.MODE_PRIVATE);

        String id = sharedPreferences.getString(AppConstants.RECIPE_ID_KEY, "");
        String name = sharedPreferences.getString(AppConstants.RECIPE_NAME_KEY, "");
        List<Ingredient> ingredients = getRecipeIngredients(sharedPreferences);
        List<Step> steps = getRecipeSteps(sharedPreferences);
        String servings = sharedPreferences.getString(AppConstants.SERVINGS_KEY, "");
        String image = sharedPreferences.getString(AppConstants.IMAGE_KEY, "");

        return new Recipe(id, name, ingredients, steps, servings, image);
    }

    private static List<Ingredient> getRecipeIngredients(SharedPreferences sharedPreferences) {
        Set<String> quantitySet = sharedPreferences.getStringSet
                (WidgetJobService.SET_INGREDIENT_QUANTITY, new HashSet<String>());

        Set<String> measureSet = sharedPreferences.getStringSet
                (WidgetJobService.SET_INGREDIENT_MEASURE, new HashSet<String>());

        Set<String> nameSet = sharedPreferences.getStringSet
                (WidgetJobService.SET_INGREDIENT_NAME, new HashSet<String>());

        List<String> quantityList = convertSetToList(quantitySet);
        List<String> measureList = convertSetToList(measureSet);
        List<String> nameList = convertSetToList(nameSet);

        Comparator<String> comparator = new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                int[] compareValues = getCompareValues(o1, o2);

                //noinspection UseCompareMethod
                if (compareValues[0] > compareValues[1]) {

                    return 1;
                } else if (compareValues[0] < compareValues[1]) {
                    return -1;
                } else {
                    return 0;
                }
            }
        };

        Collections.sort(quantityList, comparator);
        Collections.sort(measureList, comparator);
        Collections.sort(nameList, comparator);

        List<Ingredient> ingredientList = new ArrayList<>();

        //noinspection ConstantConditions
        for (int index = 0; index < quantitySet.size(); index++) {

            String quantityWithIdPrefix = quantityList.get(index);
            String measureWithIdPrefix = measureList.get(index);
            String nameWithIdPrefix = nameList.get(index);

            String prefix = index + AppConstants.WIDGET_PREF_DELINEATION;

            String quantity = quantityWithIdPrefix.replace(prefix, "");
            String measure = measureWithIdPrefix.replace(prefix, "");
            String name = nameWithIdPrefix.replace(prefix, "");

            Ingredient ingredient = new Ingredient(quantity, measure, name);
            ingredientList.add(ingredient);
        }

        return ingredientList;
    }

    private static List<Step> getRecipeSteps(SharedPreferences sharedPreferences) {
        Set<String> idSet = sharedPreferences.getStringSet
                (WidgetJobService.SET_STEP_ID, new HashSet<String>());

        Set<String> shortDescriptionSet = sharedPreferences.getStringSet
                (WidgetJobService.SET_STEP_SHORT_DESCRIPTION, new HashSet<String>());

        Set<String> descriptionSet = sharedPreferences.getStringSet
                (WidgetJobService.SET_STEP_DESCRIPTION, new HashSet<String>());

        Set<String> videoUrlSet = sharedPreferences.getStringSet
                (WidgetJobService.SET_STEP_VIDEO_URL, new HashSet<String>());

        Set<String> thumbnailUrlSet = sharedPreferences.getStringSet
                (WidgetJobService.SET_STEP_THUMBNAIL_URL, new HashSet<String>());

        List<String> idList = convertSetToList(idSet);
        List<String> shortDescriptionList = convertSetToList(shortDescriptionSet);
        List<String> descriptionList = convertSetToList(descriptionSet);
        List<String> videoUrlList = convertSetToList(videoUrlSet);
        List<String> thumbnailUrlList = convertSetToList(thumbnailUrlSet);

        Comparator<String> comparator = new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                int[] compareValues = getCompareValues(o1, o2);

                //noinspection UseCompareMethod
                if (compareValues[0] > compareValues[1]) {

                    return 1;
                } else if (compareValues[0] < compareValues[1]) {
                    return -1;
                } else {
                    return 0;
                }
            }
        };

        Collections.sort(idList, comparator);
        Collections.sort(shortDescriptionList, comparator);
        Collections.sort(descriptionList, comparator);
        Collections.sort(videoUrlList, comparator);
        Collections.sort(thumbnailUrlList, comparator);

        List<Step> stepList = new ArrayList<>();

        //noinspection ConstantConditions
        for (int index = 0; index < idSet.size(); index++) {

            String idWithIdPrefix = idList.get(index);
            String shortDescriptionWithIdPrefix = shortDescriptionList.get(index);
            String descriptionWithIdPrefix = descriptionList.get(index);
            String videoUrlWithIdPrefix = videoUrlList.get(index);
            String thumbnailUrlWithIdPrefix = thumbnailUrlList.get(index);

            String prefix = index + AppConstants.WIDGET_PREF_DELINEATION;

            String id = idWithIdPrefix.replace(prefix, "");
            String shortDescription = shortDescriptionWithIdPrefix.replace(prefix, "");
            String description = descriptionWithIdPrefix.replace(prefix, "");
            String videoUrl = videoUrlWithIdPrefix.replace(prefix, "");
            String thumbnailUrl = thumbnailUrlWithIdPrefix.replace(prefix, "");

            Step ingredient = new Step(id, shortDescription, description, videoUrl, thumbnailUrl);

            stepList.add(ingredient);
        }

        return stepList;
    }

    private static List<String> convertSetToList(Set<String> set) {
        return new ArrayList<>(set);
    }

    private static int[] getCompareValues(String stringA, String stringB) {
        String delineation = AppConstants.WIDGET_PREF_DELINEATION;

        int endIndexA = stringA.indexOf(delineation);
        String numStringA = stringA.substring(0, endIndexA);

        int endIndexB = stringB.indexOf(delineation);
        String numStringB = stringB.substring(0, endIndexB);

        int compareValA  = Integer.parseInt(numStringA);
        int compareValB  = Integer.parseInt(numStringB);

        int[] compareValues = new int[2];

        compareValues[0] = compareValA;
        compareValues[1] = compareValB;

        return compareValues;
    }

    //unregister local broadcast receiver
    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(IngredientSelectionRemoteViewsFactory.this);
    }

    @Override
    public int getCount() {
        if (mDisplayList == null) return 0;

        return mDisplayList.length;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        //get Remote View according to widget state
        if (mState == RecipeWidgetProvider.STATE_INGREDIENTS) {
            return getIngredientsRemoteViews(position);

        } else {
            return getRecipeNamesRemoteViews(position);
        }
    }

    private RemoteViews getIngredientsRemoteViews(int position) {
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.widget_ingredient_or_recipe_layout);
        remoteViews.setTextViewText(R.id.recipe_name_text_view, mDisplayList[position]);

        //configure fillInIntent that does nothing(in order to overwrite recipe state fill in intent)
        Intent fillInIntent = new Intent();

        remoteViews.setOnClickFillInIntent(R.id.recipe_name_text_view, fillInIntent);

        return remoteViews;
    }

    private RemoteViews getRecipeNamesRemoteViews(int position) {
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.widget_ingredient_or_recipe_layout);
        remoteViews.setTextViewText(R.id.recipe_name_text_view, mDisplayList[position]);

        //configure fillInIntent
        Intent fillInIntent = new Intent();

        //configure extras
        Bundle extras = new Bundle();
        Recipe recipe = mRecipeList.get(position);
        extras.putParcelable(StepsAndSharedActivity.SELECTED_RECIPE_KEY, Parcels.wrap(recipe));

        fillInIntent.putExtras(extras);
        remoteViews.setOnClickFillInIntent(R.id.recipe_name_text_view, fillInIntent);

        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
