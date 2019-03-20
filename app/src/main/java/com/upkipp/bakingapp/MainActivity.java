package com.upkipp.bakingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.upkipp.bakingapp.adapters.RecipesAdapter;
import com.upkipp.bakingapp.models.Recipe;

import java.util.List;

public class MainActivity extends AppCompatActivity implements RecipesAdapter.OnRecipeItemClickListener {
    RecyclerView mRecipesRecyclerView;
    RecipesAdapter mRecipesAdapter;
    List<Recipe>  mRecipeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            defineViews();
            getRecipes();
            setRecipesToAdapter();

        } else {
            restoreSavedValues();
        }
    }

    private void defineViews() {
        mRecipesRecyclerView = findViewById(R.id.RecipesReclclerView);
    }

    private void getRecipes() {

    }

    private void setRecipesToAdapter() {

    }

    private void restoreSavedValues() {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    private void openStepsActivity(int position) {
        Intent stepsIntent = new Intent(getApplicationContext(), StepsActivity.class);
        //TODO: use parcelable/serializable
        stepsIntent.putExtra(StepsActivity.SELECTED_RECIPE_KEY, );
    }

    @Override
    public void onItemClick(int position) {
        openStepsActivity(position);
    }
}
