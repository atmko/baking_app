package com.upkipp.bakingapp;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.upkipp.bakingapp.adapters.RecipesAdapter;
import com.upkipp.bakingapp.models.Recipe;
import com.upkipp.bakingapp.utils.NetworkUtils;
import com.upkipp.bakingapp.utils.RecipeParser;

import org.parceler.Parcels;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements RecipesAdapter.OnRecipeItemClickListener {

    RecyclerView mRecipesRecyclerView;
    RecipesAdapter mRecipesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            defineViews();
            getAllRecipes();

        } else {
            restoreSavedValues();
        }

    }

    private void defineViews() {
        mRecipesRecyclerView = findViewById(R.id.RecipesRecyclerView);
        //configureLayoutManager returns a LayoutManager
        mRecipesRecyclerView.setLayoutManager(configureLayoutManager());

        mRecipesAdapter = new RecipesAdapter(this);
        mRecipesRecyclerView.setAdapter(mRecipesAdapter);

    }

    private void getAllRecipes() {
        NetworkUtils.getAllRecipes().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String responseString) {
                //parse json string
                List<Recipe> recipeList = RecipeParser.parseRecipes(responseString);
                //add recipes to adapter
                mRecipesAdapter.setRecipeList(recipeList);
            }

            @Override
            public void onError(ANError anError) {

            }

        });

    }

    private void restoreSavedValues() {

    }

    private void openStepsActivity(int position) {
        //TODO: use parcelable/serializable
        Recipe selectedRecipe =  mRecipesAdapter.getRecipeList().get(position);
        Parcelable parceledRecipe = Parcels.wrap(selectedRecipe);

        Intent stepsIntent = new Intent(getApplicationContext(), InstructionsActivity.class);
        stepsIntent.putExtra(InstructionsActivity.SELECTED_RECIPE_KEY, parceledRecipe);

        startActivity(stepsIntent);
    }

    private LinearLayoutManager configureLayoutManager() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        return layoutManager;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onItemClick(int position) {
        openStepsActivity(position);
        Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
    }

}
