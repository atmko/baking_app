package com.upkipp.bakingapp.models;

import java.util.Map;

public class Recipe {
    private String mId;
    private String mName;
    private Map<String, String> mIngredients;
    private Map<String, String> mSteps;
    private String mServings;
    private String mImage;

    public Recipe(String id, String name, Map<String, String> ingredients,
                  Map<String, String> steps, String servings, String image) {

        mId = id;
        mName = name;
        mIngredients = ingredients;
        mSteps = steps;
        mServings = servings;
        mImage = image;

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

    public Map<String, String> getIngredients() {
        return mIngredients;
    }

    public void setIngredients(Map<String, String> mIngredients) {
        this.mIngredients = mIngredients;
    }

    public Map<String, String> getSteps() {
        return mSteps;
    }

    public void setSteps(Map<String, String> mSteps) {
        this.mSteps = mSteps;
    }

    public String getServings() {
        return mServings;
    }

    public void setServings(String mServings) {
        this.mServings = mServings;
    }

    public String getImage() {
        return mImage;
    }

    public void setImage(String mImage) {
        this.mImage = mImage;
    }
}
