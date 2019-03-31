package com.upkipp.bakingapp.widget;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.upkipp.bakingapp.models.Recipe;
import com.upkipp.bakingapp.utils.NetworkUtils;
import com.upkipp.bakingapp.utils.RecipeParser;

import org.parceler.Parcels;

import java.util.List;

public class WidgetService extends IntentService {
    public static final String ACTION_CHECK_WIDGET_STATE = "com.upkipp.android.bakingapp.action.check_widget_state";
    public static final String ACTION_GET_RECIPE_NAMES = "com.upkipp.android.bakingapp.action.get_recipe_names";
    public static final String ACTION_GET_INGREDIENTS = "com.upkipp.android.bakingapp.action.get_ingredients";
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
//     * @param name Used to name the worker thread, important only for debugging.
     */
    public WidgetService() {
        super("jjk");
    }

    public void getWidgetState(Intent widgetStateIntent) {
        //set intent bundle
        Bundle bundle = new Bundle();
        widgetStateIntent.putExtras(bundle);

        //send broadcast
        LocalBroadcastManager.getInstance(getApplicationContext())
                .sendBroadcast(widgetStateIntent);
    }

    public void getRecipeNames(final Intent recipeNamesIntent) {
        NetworkUtils.getAllRecipes().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String responseString) {
                //parse json string
                List<Recipe> recipeList = RecipeParser.parseRecipes(responseString);
                String[] recipeNames = getNameList(recipeList);

                //set intent bundle
                Bundle bundle = new Bundle();
                bundle.putString("type", "recipes");
                bundle.putStringArray("recipe_names", recipeNames);

                Parcelable parcelable = Parcels.wrap(recipeList);
                bundle.putParcelable("recipes", parcelable);

                recipeNamesIntent.putExtras(bundle);

                //send broadcast
                LocalBroadcastManager.getInstance(getApplicationContext())
                        .sendBroadcast(recipeNamesIntent);

            }

            @Override
            public void onError(ANError anError) {

            }
        });
    }

    public void getIngredientNames(Intent ingredientNameIntent) {
        //set intent bundle
        Bundle bundle = new Bundle();
        ingredientNameIntent.putExtras(bundle);
        ingredientNameIntent.setAction(ACTION_GET_INGREDIENTS);

        //send broadcast
        LocalBroadcastManager.getInstance(getApplicationContext())
                .sendBroadcast(ingredientNameIntent);
    }

    public static void updateAccordingToState(Context context, String receivedState) {
        Intent serviceIntent = new Intent(context, WidgetService.class);
        if (receivedState.equals("recipes")) {
            serviceIntent.setAction(WidgetService.ACTION_GET_RECIPE_NAMES);
            context.startService(serviceIntent);

        } else if (receivedState.equals("ingredients")) {
            serviceIntent.setAction(WidgetService.ACTION_GET_INGREDIENTS);
            context.startService(serviceIntent);        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent serviceIntent) {
        if (serviceIntent != null) {
            String action = serviceIntent.getAction();

            if (action.equals(ACTION_CHECK_WIDGET_STATE)) {
                getWidgetState(serviceIntent);

            } else if (action.equals(ACTION_GET_RECIPE_NAMES)) {
                getRecipeNames(serviceIntent);

            } else if (action.equals(ACTION_GET_INGREDIENTS))  {
                getIngredientNames(serviceIntent);
            }
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
}