/*
 * Copyright (C) 2019 Aayat Mimiko
 */

package com.upkipp.bakingapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.upkipp.bakingapp.R;
import com.upkipp.bakingapp.models.Ingredient;
import com.upkipp.bakingapp.models.Step;

import java.util.List;

// adapter for step recycler view
public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.StepViewHolder> {
    private OnStepItemClickListener mOnStepItemClickListener;
    private List<Step> mStepList;
    private List<Ingredient> mIngredientList;

    //layout ids
    private final int INGREDIENTS_LAYOUT_ID = 1;
    private final int STEPS_LAYOUT_ID = 2;

    public StepsAdapter(Context context) {
        mOnStepItemClickListener = (OnStepItemClickListener) context;
    }

    public interface OnStepItemClickListener {
        void onStepClick(int position);
    }

    public void setStepList(List<Step> stepList, List<Ingredient> ingredientList) {
        mStepList = stepList;
        mIngredientList = ingredientList;

        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mStepList != null) return mStepList.size();

        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        //if this is the first (ingredients) item
        if (position == 0) {
            return INGREDIENTS_LAYOUT_ID;

        } else {
            return STEPS_LAYOUT_ID;
        }
    }

    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        int resourceId;

        //if this is the first (ingredients) item
        if (viewType == INGREDIENTS_LAYOUT_ID) {
            resourceId = R.layout.layout_ingredient_spinner;

        } else {
            resourceId = R.layout.layout_step;
        }

        View view = layoutInflater.inflate(resourceId, viewGroup, false);
        return new StepViewHolder(view, viewType);
    }

    public class StepViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        int mViewType;
        //ingredient values
        Spinner mIngredientSpinner;
        //step values
        TextView mShortDescriptionTextView;

        private StepViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);

            mViewType = viewType;

            //if this is the first (ingredients) item
            if (viewType == INGREDIENTS_LAYOUT_ID) {
                mIngredientSpinner = itemView.findViewById(R.id.ingredient_spinner);

            } else if (viewType == STEPS_LAYOUT_ID) {
                mShortDescriptionTextView = itemView.findViewById(R.id.short_description_text_view);
                itemView.setOnClickListener(this);
            }
        }

        //override onclick to implement item click listener
        @Override
        public void onClick(View v) {
            mOnStepItemClickListener.onStepClick(getAdapterPosition());
        }
    }

    @Override
    public void onBindViewHolder(@NonNull StepViewHolder stepViewHolder, int position) {
        if (stepViewHolder.mViewType == INGREDIENTS_LAYOUT_ID) {
            configureIngredientsLayout(stepViewHolder);

        } else if (stepViewHolder.mViewType == STEPS_LAYOUT_ID) {
            configureStepLayout(stepViewHolder, position);
        }
    }

    private void configureIngredientsLayout(StepViewHolder stepViewHolder) {
        IngredientsAdapter ingredientSpinnerAdapter =
                new IngredientsAdapter(mIngredientList, stepViewHolder.itemView.getContext());

        stepViewHolder.mIngredientSpinner.setAdapter(ingredientSpinnerAdapter);
    }

    private void configureStepLayout(StepViewHolder stepViewHolder, int position){
        Step currentStep = mStepList.get(position);
        String id = currentStep.getId();
        String shortDescription = currentStep.getShortDescription();

        String newShortDescription = id + ". " + shortDescription;

        stepViewHolder.mShortDescriptionTextView.setText(newShortDescription);
    }
}
