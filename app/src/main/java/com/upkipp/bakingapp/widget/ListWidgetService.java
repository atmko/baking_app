package com.upkipp.bakingapp.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.Toast;

import com.upkipp.bakingapp.R;
import com.upkipp.bakingapp.models.Recipe;

import org.parceler.Parcels;

import java.util.List;

public class ListWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
//        Log.d("TAGG", intent.getStringArrayExtra("names").toString());
        Toast.makeText(this, "yello", Toast.LENGTH_SHORT).show();
        return new WidgetRemoteViewsFactory(this.getApplicationContext());
    }

}

class WidgetRemoteViewsFactory extends BroadcastReceiver implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private String mState;
    private String[] mDisplayList;
    private Recipe mRecipe;
    private List<Recipe> mRecipeList;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Bundle extras = intent.getExtras();

        if (action.equals(WidgetService.ACTION_CHECK_WIDGET_STATE)) {
            WidgetService.updateAccordingToState(context, mState);

        } else if (action.equals(WidgetService.ACTION_GET_RECIPE_NAMES)) {
            mState = extras.getString("type");
            mRecipeList = Parcels.unwrap(extras.getParcelable("recipes"));
            mDisplayList = extras.getStringArray("recipe_names");

            RecipeWidgetProvider.updateAllAppWidgets(context, mState, null);

        } else if (action.equals(WidgetService.ACTION_GET_INGREDIENTS)){
            mState = extras.getString("type");
            mRecipe = Parcels.unwrap(extras.getParcelable("recipe"));
            mDisplayList = mRecipe.getIngredientNames();

            RecipeWidgetProvider.updateAllAppWidgets(context, mState, mRecipe);

        }
    }

    public WidgetRemoteViewsFactory(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WidgetService.ACTION_CHECK_WIDGET_STATE);
        intentFilter.addAction(WidgetService.ACTION_GET_RECIPE_NAMES);
        intentFilter.addAction(WidgetService.ACTION_GET_INGREDIENTS);
        LocalBroadcastManager.getInstance(context).registerReceiver(WidgetRemoteViewsFactory.this,
                intentFilter);

        mState = "recipes";
        mContext = context;

        Intent recipeNamesIntent = new Intent(context, WidgetService.class);
        recipeNamesIntent.setAction(WidgetService.ACTION_GET_RECIPE_NAMES);
        context.startService(recipeNamesIntent);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(WidgetRemoteViewsFactory.this);
    }

    @Override
    public int getCount() {
        if (mDisplayList == null) return 0;

        return mDisplayList.length;
    }

    @Override
    public RemoteViews getViewAt(int position) {

        Log.d("..........PATH", mState);
        if (mState.equals("recipes")) {
            return getRecipeNamesRemoteViews(position);

        } else if (mState.equals("ingredients")){
            return getIngredientsRemoteViews(position);

        } else {
            return null;
        }

    }

    private RemoteViews getRecipeNamesRemoteViews(int position) {
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.widget_recipe_layout);
        remoteViews.setTextViewText(R.id.recipe_name_text_view, mDisplayList[position]);

        //configure extras
        Bundle extras = new Bundle();
        Recipe recipe = mRecipeList.get(position);
        extras.putString("type", "ingredients");
        extras.putParcelable("recipe", Parcels.wrap(recipe));

        //configure fillInIntent
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        remoteViews.setOnClickFillInIntent(R.id.recipe_name_text_view, fillInIntent);

        //configure empty view
        remoteViews.setEmptyView(R.id.widget_recipe_names_list_view, R.id.emptytext);

        return remoteViews;
    }

    private RemoteViews getIngredientsRemoteViews(int position) {
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.widget_recipe_layout);
        remoteViews.setTextViewText(R.id.recipe_name_text_view, mDisplayList[position]);

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
