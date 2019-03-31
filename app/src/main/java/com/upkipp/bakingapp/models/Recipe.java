package com.upkipp.bakingapp.models;

import org.parceler.Parcel;

import java.util.List;
import java.util.Map;

@Parcel
public class Recipe {
    String mId;
    String mName;
    List<Map<String, String>> mIngredients;
    List<Map<String, String>> mSteps;
    String mServings;
    String mImage;

    Recipe() {
    }

    public Recipe(String id, String name, List<Map<String, String>> ingredients,
                  List<Map<String, String>> steps, String servings, String image) {

        mId = id;
        mName = name;
        mIngredients = ingredients;


//        addIngredientsToSteps(ingredients);


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

    public List<Map<String, String>> getIngredients() {
        return mIngredients;
    }

    public void setIngredients(List<Map<String, String>> mIngredients) {
        this.mIngredients = mIngredients;
    }

    public List<Map<String, String>> getSteps() {
        return mSteps;
    }

    public void setSteps(List<Map<String, String>> mSteps) {
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
