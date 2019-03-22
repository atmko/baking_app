package com.upkipp.bakingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.upkipp.bakingapp.models.Recipe;

import org.parceler.Parcels;

public class StepsActivity extends AppCompatActivity {
    public static final String SELECTED_RECIPE_KEY = "recipe_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);

        Intent receivedIntent = getIntent();

        Recipe selectedRecipe = Parcels.unwrap(receivedIntent.getParcelableExtra(SELECTED_RECIPE_KEY));

        Toast.makeText(this, selectedRecipe.getName(), Toast.LENGTH_SHORT).show();
    }
}
