package com.upkipp.bakingapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.upkipp.bakingapp.R;
import com.upkipp.bakingapp.StepsAndSharedActivity;
import com.upkipp.bakingapp.models.Recipe;

import org.parceler.Parcels;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeWidgetProvider extends AppWidgetProvider {

    private static final String TAG = RecipeWidgetProvider.class.getName();

    public static final int STATE_INGREDIENTS = 1;
    public static final int STATE_RECIPE = 2;

    public static final String SET_INGREDIENT_QUANTITY = "ingredients_quantity_set";
    public static final String SET_INGREDIENT_MEASURE = "ingredients_measure_set";
    public static final String SET_INGREDIENT_NAME = "ingredients_name_set";

    public static final String SET_STEP_ID = "steps_id_set";
    public static final String SET_STEP_SHORT_DESCRIPTION = "steps_shortDescription_set";
    public static final String SET_STEP_DESCRIPTION = "steps_description_set";
    public static final String SET_STEP_VIDEO_URL = "steps_videoURL_set";
    public static final String SET_STEP_THUMBNAIL_URL = "steps_thumbnailURL_set";

    public static final String WIDGET_PREFERENCE_PREFIX = "widget_preferences";

    //loop through and update widgets
    static void updateAppWidgetIngredients(Context context, Recipe recipe) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager
                .getAppWidgetIds(new ComponentName(context, RecipeWidgetProvider.class));

        // Construct the RemoteViews object
        RemoteViews remoteViews = getIngredientListRemoteView(context, recipe);

        //update all widgets
        for (int appWidgetId : appWidgetIds) {
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }

        Log.d(TAG,"widget update complete");
    }

    //loop through and update widgets
    static void updateAppWidgetRecipes(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager
                .getAppWidgetIds(new ComponentName(context, RecipeWidgetProvider.class));

        // Construct the RemoteViews object
        RemoteViews remoteViews = getRecipeListRemoteView(context);

        //update all widgets
        for (int appWidgetId : appWidgetIds) {
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }

        Log.d(TAG,"widget update complete");
    }

    //get remote view for ingredient state
    private static RemoteViews getIngredientListRemoteView(Context context, Recipe recipe) {
        RemoteViews remoteViews = new RemoteViews
                (context.getPackageName(), R.layout.widget_ingredient_names_list);
        remoteViews.setTextViewText(R.id.headingText, recipe.getName());
        remoteViews.setViewVisibility(R.id.selectRecipeButton, View.VISIBLE);
        remoteViews.setViewVisibility(R.id.openRecipeButton, View.VISIBLE);

        //set remote adapter
        Intent ingredientSelectionServiceIntent = new Intent
                (context, IngredientSelectionService.class);
        remoteViews.setRemoteAdapter
                (R.id.widget_ingredient_names_list_view, ingredientSelectionServiceIntent);

        //set recipe button pending intent
        Intent getRecipesIntent =  new Intent(context, WidgetService.class);
        getRecipesIntent.setAction(WidgetService.ACTION_GET_RECIPE_NAMES);

        PendingIntent selectRecipeIntent = PendingIntent
                .getService(context, 0, getRecipesIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setOnClickPendingIntent(R.id.selectRecipeButton, selectRecipeIntent);

        //set pending intent template that does nothing, to nullify previous template
        Intent nullIntent =  new Intent(context, WidgetService.class);
        nullIntent.setAction("");

        PendingIntent nullPendingIntent = PendingIntent
                .getService(context, 0, nullIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setPendingIntentTemplate(R.id.widget_ingredient_names_list_view, nullPendingIntent);

        //set open recipe pending intent
        Intent activityIntent =  new Intent(context, StepsAndSharedActivity.class);
        activityIntent.putExtra(StepsAndSharedActivity.SELECTED_RECIPE_KEY, Parcels.wrap(recipe));

        PendingIntent activityPendingIntent = PendingIntent
                .getActivity(context, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setOnClickPendingIntent(R.id.openRecipeButton, activityPendingIntent);

        //set empty view
        remoteViews.setEmptyView(R.id.widget_ingredient_names_list_view, R.id.emptytext);

        return remoteViews;
    }

    //get remote view for recipe state
    private static RemoteViews getRecipeListRemoteView(Context context) {
        RemoteViews remoteViews = new RemoteViews
                (context.getPackageName(), R.layout.widget_ingredient_names_list);
        remoteViews.setTextViewText(R.id.headingText, context.getString(R.string.widget_select_recipe));
        remoteViews.setViewVisibility(R.id.selectRecipeButton, View.INVISIBLE);
        remoteViews.setViewVisibility(R.id.openRecipeButton, View.INVISIBLE);

        //set pending intent template
        Intent serviceIntent =  new Intent(context, WidgetService.class);
        serviceIntent.setAction(WidgetService.ACTION_SAVE_AND_OR_LOAD_WIDGET_RECIPE);

        PendingIntent servicePendingIntent = PendingIntent
                .getService(context, 0, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setPendingIntentTemplate
                (R.id.widget_ingredient_names_list_view, servicePendingIntent);

        //set empty view
        remoteViews.setEmptyView(R.id.widget_ingredient_names_list_view, R.id.emptytext);

        return remoteViews;
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
         Intent ingredientsServiceIntent = new Intent(context, WidgetService.class);
         ingredientsServiceIntent.setAction(WidgetService.ACTION_SAVE_AND_OR_LOAD_WIDGET_RECIPE);
         context.startService(ingredientsServiceIntent);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

}