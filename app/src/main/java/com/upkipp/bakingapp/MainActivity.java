package com.upkipp.bakingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.upkipp.bakingapp.adapters.RecipesAdapter;
import com.upkipp.bakingapp.models.Recipe;
import com.upkipp.bakingapp.utils.AppConstants;
import com.upkipp.bakingapp.utils.NetworkUtils;
import com.upkipp.bakingapp.utils.RecipeParser;

import java.util.List;

public class MainActivity extends AppCompatActivity implements RecipesAdapter.OnRecipeItemClickListener {
    RecyclerView mRecipesRecyclerView;
    RecipesAdapter mRecipesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            defineViews();
            getRecipes();

        } else {
            restoreSavedValues();
        }

    }

    private void defineViews() {
        mRecipesRecyclerView = findViewById(R.id.RecipesRecyclerView);

    }

    private void getRecipes() {
        NetworkUtils.getAllRecipes().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String responseString) {
                //parse json string
                List<Recipe> recipeList = parseRecipes(responseString);
                //add recipes to adapter
                mRecipesAdapter.setRecipeList(recipeList);
            }

            @Override
            public void onError(ANError anError) {

            }

        });

    }

    private List<Recipe> parseRecipes(String responseString) {
        return RecipeParser.parseRecipes(responseString);
    }

    private void restoreSavedValues() {

    }

    private void openStepsActivity(int position) {
        Intent stepsIntent = new Intent(getApplicationContext(), StepsActivity.class);
        //TODO: use parcelable/serializable
        Recipe selectedRecipe =  mRecipesAdapter.getRecipeList().get(position);
        stepsIntent.putExtra(StepsActivity.SELECTED_RECIPE_KEY, selectedRecipe);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onItemClick(int position) {
        openStepsActivity(position);
    }

}
