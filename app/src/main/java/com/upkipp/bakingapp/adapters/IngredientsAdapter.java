/*
 * Copyright (C) 2019 Aayat Mimiko
 */

package com.upkipp.bakingapp.adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.upkipp.bakingapp.R;
import com.upkipp.bakingapp.models.Ingredient;

import java.util.List;

// adapter for ingredient spinner
class IngredientsAdapter implements SpinnerAdapter {

    private Context mContext;
    private List<Ingredient> mIngredientList;

    IngredientsAdapter(List<Ingredient> ingredientList, Context context) {
        mContext = context;
        mIngredientList = ingredientList;
    }

    //configures how the each view item looks
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        int resourceId = R.layout.layout_ingredient_object;
        View view = layoutInflater.inflate(resourceId, viewGroup, false);

        TextView ingredientHeadingTextView = view.findViewById(R.id.ingredient_heading_text_view);

        TextView ingredientNameTextView = view.findViewById(R.id.ingredient_name_text_view);
        TextView quantityTextView = view.findViewById(R.id.quantity_text_view);
        TextView measureTextView = view.findViewById(R.id.measure_text_view);

        Ingredient ingredient = mIngredientList.get(position);

        ingredientHeadingTextView.setVisibility(View.GONE);
        ingredientNameTextView.setVisibility(View.VISIBLE);
        quantityTextView.setVisibility(View.VISIBLE);
        measureTextView.setVisibility(View.VISIBLE);

        ingredientNameTextView.setText(ingredient.getName());
        quantityTextView.setText(ingredient.getQuantity());
        measureTextView.setText(ingredient.getMeasure());

        return view;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        if (mIngredientList != null) return mIngredientList.size();

        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    //configures how the selected view item looks
    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        int resourceId = R.layout.layout_ingredient_object;
        View view = layoutInflater.inflate(resourceId, viewGroup, false);

        TextView ingredientHeadingTextView = view.findViewById(R.id.ingredient_heading_text_view);
        TextView ingredientNameTextView = view.findViewById(R.id.ingredient_name_text_view);
        TextView quantityTextView = view.findViewById(R.id.quantity_text_view);
        TextView measureTextView = view.findViewById(R.id.measure_text_view);

        //hide all text views except the the "Ingredients" heading
        ingredientHeadingTextView.setVisibility(View.VISIBLE);
        ingredientNameTextView.setVisibility(View.GONE);
        quantityTextView.setVisibility(View.GONE);
        measureTextView.setVisibility(View.GONE);

        return view;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return  (mIngredientList == null || mIngredientList.size() == 0);
    }
}
