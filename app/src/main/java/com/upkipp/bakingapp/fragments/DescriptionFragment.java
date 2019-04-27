/*
 * Copyright (C) 2019 Aayat Mimiko
 */

package com.upkipp.bakingapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.upkipp.bakingapp.R;
import com.upkipp.bakingapp.utils.AppConstants;

//fragment class that shows recipe's description
public class DescriptionFragment extends Fragment{
    @SuppressWarnings({"unused"})
    private TextView mDescriptionTextView;

    private String mDescription;
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private String mIngredients;

    public DescriptionFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_description, container, false);
        defineViews(rootView);

        //noinspection StatementWithEmptyBody
        if (savedInstanceState == null) {

        } else {
            restoreSavedValues(savedInstanceState);
        }

        setViewValues();

        return rootView;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public void setIngredients(String ingredients) {
        this.mIngredients = ingredients;
    }

    private void defineViews(View rootView) {
        mDescriptionTextView = rootView.findViewById(R.id.description_text_view);
    }

    private void restoreSavedValues(Bundle savedInstanceState) {
        mDescription = savedInstanceState.getString(AppConstants.STEP_DESCRIPTION_KEY);
    }

    private void setViewValues() {
        mDescriptionTextView.setText(mDescription);
    }

    public void reloadMedia() {
        setViewValues();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(AppConstants.STEP_DESCRIPTION_KEY, mDescription);
    }
}
