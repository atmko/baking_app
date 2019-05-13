/*
 * Copyright (C) 2019 Aayat Mimiko
 */

package com.atmko.bakerly;

import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.atmko.bakerly.R;
import com.atmko.bakerly.adapters.RecipesAdapter;
import com.atmko.bakerly.models.Recipe;
import com.atmko.bakerly.utils.NetworkUtils;
import com.atmko.bakerly.utils.RecipeParser;
import com.atmko.bakerly.utils.SimpleIdlingResource;

import org.parceler.Parcels;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements RecipesAdapter.OnRecipeItemClickListener {

    @SuppressWarnings("FieldCanBeLocal")
    private RecyclerView mRecipesRecyclerView;
    private RecipesAdapter mRecipesAdapter;

    private List<Recipe> mRecipeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getIdlingResource();

        defineViews();

        if (savedInstanceState != null) {
            restoreSavedValues(savedInstanceState);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mSimpleIdlingResource != null) {
            mSimpleIdlingResource.setIdleState(false);
        }
        getAllRecipes();
    }

    private void defineViews() {
        mRecipesRecyclerView = findViewById(R.id.RecipesRecyclerView);
        //configureLayoutManager returns a LayoutManager
        mRecipesRecyclerView.setLayoutManager(configureLayoutManager());

        mRecipesAdapter = new RecipesAdapter(this);
        mRecipesRecyclerView.setAdapter(mRecipesAdapter);

    }

    private GridLayoutManager configureLayoutManager() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, getResources().getInteger(R.integer.recipe_column_span));
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        return layoutManager;
    }

    private void restoreSavedValues(Bundle savedInstanceState) {
        mRecipeList = Parcels.unwrap(savedInstanceState.getParcelable(StepsAndSharedActivity.SELECTED_RECIPE_KEY));
        mRecipesAdapter.setRecipeList(mRecipeList);
    }

    private void getAllRecipes() {
        NetworkUtils.getAllRecipes().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String responseString) {
                if (mSimpleIdlingResource != null) {
                    mSimpleIdlingResource.setIdleState(true);
                }
                //parse json string
                mRecipeList = RecipeParser.parseRecipes(responseString);
                //add recipes to adapter
                mRecipesAdapter.setRecipeList(mRecipeList);
            }

            @Override
            public void onError(ANError anError) {
                //notify error
                Snackbar.make(findViewById(R.id.top_layout),
                        anError.getErrorDetail(), Snackbar.LENGTH_LONG).show();

            }

        });

    }

    @Override
    public void onItemClick(int position) {
        startStepsActivity(position);
    }

    private void startStepsActivity(int position) {
        Recipe selectedRecipe =  mRecipesAdapter.getRecipeList().get(position);
        Parcelable parceledRecipe = Parcels.wrap(selectedRecipe);

        Intent stepsAndSharedIntent = new Intent(getApplicationContext(), StepsAndSharedActivity.class);
        stepsAndSharedIntent.putExtra(StepsAndSharedActivity.SELECTED_RECIPE_KEY, parceledRecipe);

        startActivity(stepsAndSharedIntent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mRecipeList != null) {
            outState.putParcelable(
                    StepsAndSharedActivity.SELECTED_RECIPE_KEY, Parcels.wrap(mRecipeList));
        }

    }

    @Nullable
    private SimpleIdlingResource mSimpleIdlingResource;

    public SimpleIdlingResource getIdlingResource() {
        if (mSimpleIdlingResource == null) {
            mSimpleIdlingResource = new SimpleIdlingResource();
        }
        return mSimpleIdlingResource;
    }

}
