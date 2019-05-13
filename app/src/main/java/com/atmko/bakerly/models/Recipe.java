/*
 * Copyright (C) 2019 Aayat Mimiko
 */

package com.atmko.bakerly.models;

import com.atmko.bakerly.utils.AppConstants;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//recipe object class
@SuppressWarnings("unused")
@Parcel
public class Recipe {
    String mId;
    String mName;
    List<Ingredient> mIngredients;
    List<Step> mSteps;
    String mServings;
    String mImage;
    List<Step> mAdjustedStepList;

    Recipe() {
    }

    public Recipe(String id, String name, List<Ingredient> ingredients,
                  List<Step> steps, String servings, String image) {
        mId = id;
        mName = name;
        mIngredients = ingredients;
        mSteps = steps;
        mServings = servings;
        mImage = image;
        mAdjustedStepList = adjustSteps(mSteps);
    }

    private List<Step> adjustSteps(List<Step> steps){
        List<Step> adjustedSteps = new ArrayList<>();

        //add empty item to stand for ingredients i.e first item for getItemViewType in steps adapter
        //NOTE: ingredients can't be added directly because of wrong type
        adjustedSteps.add(new Step(null,
                null,
                null,
                null,
                null));

        adjustedSteps.addAll(steps);

        return adjustedSteps;
    }

    public String getId() {
        return mId;
    }

    public void setId(String mId) {
        this.mId = mId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public List<Ingredient> getIngredients() {
        return mIngredients;
    }

    public void setIngredients(List<Ingredient> mIngredients) {
        this.mIngredients = mIngredients;
    }

    public String[] getIngredientNames() {
        int listSize = mIngredients.size();

        String[] ingredientNameList = new String[listSize];
        for (int index = 0; index < listSize; index++) {
            ingredientNameList[index] = mIngredients.get(index).getName();

        }
        return ingredientNameList;
    }

    public List<Step> getSteps() {
        return mSteps;
    }

    public void setSteps(List<Step> mSteps) {
        this.mSteps = mSteps;
    }

    public String getServings() {
        return mServings;
    }

    public String getImage() {
        return mImage;
    }

    public List<Step> getAdjustedSteps() {
        return mAdjustedStepList;
    }

    public List<Map<String, String>> getIngredientPrototype(List<Ingredient> ingredientList) {
        List<Map<String, String>> ingredientPrototype = new ArrayList<>();

        for (Ingredient ingredient: ingredientList) {
            Map<String, String> ingredientMap = new HashMap<>();

            ingredientMap.put(AppConstants.INGREDIENT_MEASURE_KEY, ingredient.getMeasure());
            ingredientMap.put(AppConstants.INGREDIENT_NAME_KEY, ingredient.getName());
            ingredientMap.put(AppConstants.INGREDIENT_QUANTITY_KEY, ingredient.getQuantity());

            ingredientPrototype.add(ingredientMap);
        }

        return ingredientPrototype;
    }

    public List<Map<String, String>> getStepPrototype(List<Step> stepList) {
        List<Map<String, String>> stepPrototype = new ArrayList<>();

        for (Step step: stepList) {
            Map<String, String> stepMap = new HashMap<>();

            stepMap.put(AppConstants.STEP_ID_KEY, step.getId());
            stepMap.put(AppConstants.STEP_SHORT_DESCRIPTION_KEY, step.getShortDescription());
            stepMap.put(AppConstants.STEP_DESCRIPTION_KEY, step.getDescription());
            stepMap.put(AppConstants.STEP_THUMBNAIL_URL_KEY, step.getThumbnailUrl());
            stepMap.put(AppConstants.STEP_VIDEO_URL_KEY, step.getVideoUrl());

            stepPrototype.add(stepMap);
        }

        return stepPrototype;
    }
}
