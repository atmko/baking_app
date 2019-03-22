package com.upkipp.bakingapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.upkipp.bakingapp.R;
import com.upkipp.bakingapp.adapters.RecipesAdapter;
import com.upkipp.bakingapp.adapters.StepsAdapter;

import java.util.List;
import java.util.Map;

public class StepsFragment extends Fragment implements StepsAdapter.OnStepItemClickListener{
    Context mContext;
    StepsAdapter mStepsAdapter;
    RecyclerView mStepsRecyclerView;

    private List<Map<String, String>> mIngredientsList;
    private List<Map<String, String>> mStepsList;


    public StepsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_steps, container, false);

        defineViews(rootView);

        setAdapterData();

        return rootView;
    }

    private void defineViews(View rootView) {
        mContext = rootView.getContext();
        mStepsRecyclerView = rootView.findViewById(R.id.steps_recycler_view);
        //configureLayoutManager returns a LayoutManager
        mStepsRecyclerView.setLayoutManager(configureLayoutManager());

        mStepsAdapter = new StepsAdapter(this);
        mStepsRecyclerView.setAdapter(mStepsAdapter);

    }

    private void setAdapterData() {
        mStepsAdapter.setStepList(mStepsList);
    }

    private LinearLayoutManager configureLayoutManager() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        return layoutManager;
    }

    public void setIngredientsList(List<Map<String, String>> ingredientsList) {
        this.mIngredientsList = ingredientsList;
    }

    public void setStepsList(List<Map<String, String>> stepsList) {
        this.mStepsList = stepsList;
    }

    @Override
    public void onItemClick(int position) {

    }
}
