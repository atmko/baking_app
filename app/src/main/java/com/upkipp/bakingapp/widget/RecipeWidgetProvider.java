package com.upkipp.bakingapp.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;

import com.upkipp.bakingapp.MainActivity;
import com.upkipp.bakingapp.R;
import com.upkipp.bakingapp.StepsAndSharedActivity;
import com.upkipp.bakingapp.models.Recipe;

import org.parceler.Parcels;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeWidgetProvider extends AppWidgetProvider {

    public static void updateAllAppWidgets(Context context, String state, Recipe recipe) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, RecipeWidgetProvider.class));

        for (int appWidgetId : appWidgetIds) {
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_recipe_names_list_view);
            if (state.equals("recipes")) {
                updateAppWidgetRecipes(context, appWidgetId);

            } else if (state.equals("ingredients")) {
                updateAppWidgetIngredients(context, appWidgetId, recipe);
            }
        }

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    static void updateAppWidgetRecipes(Context context,
                                       int appWidgetId) {

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        int width = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);

        // Construct the RemoteViews object
        RemoteViews remoteViews = getRecipeListRemoteView(context);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    static void updateAppWidgetIngredients(Context context,
                                           int appWidgetId, Recipe recipe) {

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        int width = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);

        // Construct the RemoteViews object
        RemoteViews remoteViews = getIngredientListRemoteView(context, recipe);


//         Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);

    }

    private static RemoteViews getRecipeListRemoteView(Context context) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_recipe_names_list);
        remoteViews.setTextViewText(R.id.headingText, "Recipes");
        remoteViews.setViewVisibility(R.id.backToRecipes, View.INVISIBLE);
        remoteViews.setViewVisibility(R.id.openRecipeButton, View.INVISIBLE);

        Intent serviceIntent =  new Intent(context, WidgetService.class);
        serviceIntent.setAction(WidgetService.ACTION_GET_INGREDIENTS);

        PendingIntent servicePendingIntent = PendingIntent
                .getService(context, 0, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setPendingIntentTemplate(R.id.widget_recipe_names_list_view, servicePendingIntent);

        remoteViews.setEmptyView(R.id.widget_recipe_names_list_view, R.id.emptytext);

        return remoteViews;
    }

    private static RemoteViews getIngredientListRemoteView(Context context, Recipe recipe) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_recipe_names_list);
        remoteViews.setTextViewText(R.id.headingText, "Ingredients");
        remoteViews.setViewVisibility(R.id.backToRecipes, View.VISIBLE);
        remoteViews.setViewVisibility(R.id.openRecipeButton, View.VISIBLE);

        //set back button pending intent
        Intent backToRecipesIntent =  new Intent(context, WidgetService.class);
        backToRecipesIntent.setAction(WidgetService.ACTION_GET_RECIPE_NAMES);

        PendingIntent backButtonPendingIntent = PendingIntent
                .getService(context, 0, backToRecipesIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setOnClickPendingIntent(R.id.backToRecipes, backButtonPendingIntent);

        //set open recipe pending intent
        Intent activityIntent =  new Intent(context, StepsAndSharedActivity.class);
        activityIntent.putExtra(MainActivity.SELECTED_RECIPE_KEY, Parcels.wrap(recipe));

        PendingIntent activityPendingIntent = PendingIntent
                .getActivity(context, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setOnClickPendingIntent(R.id.openRecipeButton, activityPendingIntent);

        remoteViews.setEmptyView(R.id.widget_recipe_names_list_view, R.id.emptytext);

        return remoteViews;
    }

    public static void createEmptyRemoteView(Context context, int appWidgetId) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        // Construct the RemoteViews object
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_recipe_names_list);
        Intent listWidgetServiceIntent = new Intent(context, ListWidgetService.class);
        Bundle bundle = new Bundle();

        listWidgetServiceIntent.putExtras(bundle);
        remoteViews.setRemoteAdapter(R.id.widget_recipe_names_list_view, listWidgetServiceIntent);

        remoteViews.setEmptyView(R.id.widget_recipe_names_list_view, R.id.emptytext);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            createEmptyRemoteView(context,appWidgetId);

            Intent widgetStateIntent = new Intent(context, WidgetService.class);
            widgetStateIntent.setAction(WidgetService.ACTION_CHECK_WIDGET_STATE);
            context.startService(widgetStateIntent);
        }

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {
//        WidgetService.getRecipeNames(context);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }
}