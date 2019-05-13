/*
 * Copyright (C) 2019 Aayat Mimiko
 */

package com.atmko.bakerly.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.atmko.bakerly.models.Step;
import com.atmko.bakerly.R;
import com.atmko.bakerly.adapters.StepsAdapter;
import com.atmko.bakerly.models.Ingredient;
import com.atmko.bakerly.utils.AppConstants;

import org.parceler.Parcels;

import java.util.List;

//fragment class for steps
public class StepsFragment extends Fragment{
    @SuppressWarnings("FieldCanBeLocal")
    private final String errorOnClickListenerImplementation = " must implement OnStepItemClickListener";
    private Context mContext;
    private StepsAdapter mStepsAdapter;
    @SuppressWarnings("FieldCanBeLocal")
    private RecyclerView mStepsRecyclerView;

    private List<Ingredient> mIngredientsList;
    private List<Step> mStepsList;

    public StepsFragment() {
    }

    //check that OnStepItemClickListener is implemented
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof StepsAdapter.OnStepItemClickListener)) {
            throw new ClassCastException(context.toString()
                    + errorOnClickListenerImplementation);

        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_steps, container, false);
        mContext = rootView.getContext();
        defineViews(rootView);

        //noinspection StatementWithEmptyBody
        if (savedInstanceState == null) {

        } else {
            restoreSavedValues(savedInstanceState);
        }

        setAdapterData();

        return rootView;
    }

    public void setIngredientsList(List<Ingredient> ingredientsList) {
        this.mIngredientsList = ingredientsList;
    }

    public void setStepsList(List<Step> stepsList) {
        this.mStepsList = stepsList;
    }

    private void defineViews(View rootView) {
        mStepsRecyclerView = rootView.findViewById(R.id.steps_recycler_view);
        //configureLayoutManager returns a LayoutManager
        mStepsRecyclerView.setLayoutManager(configureLayoutManager());

        mStepsAdapter = new StepsAdapter(mContext);
        mStepsRecyclerView.setAdapter(mStepsAdapter);
    }

    private void restoreSavedValues(Bundle savedInstanceState) {
        mStepsList = Parcels.unwrap(savedInstanceState.getParcelable(AppConstants.STEPS_KEY));
        mIngredientsList = Parcels.unwrap(savedInstanceState.getParcelable(AppConstants.INGREDIENTS_KEY));
    }

    private void setAdapterData() {
        mStepsAdapter.setStepList(mStepsList, mIngredientsList);
    }

    //configures recycler view layout
    private LinearLayoutManager configureLayoutManager() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        return layoutManager;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(AppConstants.STEPS_KEY, Parcels.wrap(mStepsList));
        outState.putParcelable(AppConstants.INGREDIENTS_KEY, Parcels.wrap(mIngredientsList));
    }
}
