package com.upkipp.bakingapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class StepsActivity extends AppCompatActivity {
    public static final String SELECTED_RECIPE_KEY = "recipe_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);
    }
}
