package com.upkipp.bakingapp.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.upkipp.bakingapp.models.Recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecipeParser {

    public static List<Recipe> parseRecipes(String responseString) {
        List<Recipe> recipeList = new ArrayList<>();

        Gson gson = new Gson();
        List gsonToList = gson.fromJson(responseString, List.class);

        for (int index = 0; index < gsonToList.size() ; index++) {
            Map currentRecipeData = (Map) gsonToList.get(index);//get current movie

            List newIngredientList = new ArrayList();

            List ingredientList = (List)currentRecipeData.get(AppConstants.INGREDIENTS_KEY);
            for (int ingredientIndex = 0; ingredientIndex < ingredientList.size() ; ingredientIndex++) {
                Map currentIngredient = (Map) ingredientList.get(ingredientIndex);//get current movie

                currentIngredient.put("quantity",String.valueOf(currentIngredient.get("quantity")));

                newIngredientList.add(currentIngredient);

//                Log.d("Tagg", Boolean.toString(currentIngredient.get("quantity") instanceof String));

            }

            List newStepsList = new ArrayList();

            List stepList = (List)currentRecipeData.get(AppConstants.INGREDIENTS_KEY);
            for (int stepIndex = 0; stepIndex < stepList.size() ; stepIndex++) {
                Map currentStep = (Map) stepList.get(stepIndex);//get current movie

                currentStep.put("id",String.valueOf(currentStep.get("id")));

                newIngredientList.add(currentStep);

//                Log.d("Tagg", Boolean.toString(currentStep.get("quantity") instanceof String));

            }

            //create new MovieData from currentObject
            Recipe recipe =
                    new Recipe(
                            //get by keys
                            //id retrieved as double
                            String.valueOf(currentRecipeData.get(AppConstants.ID_KEY)),
                            (String) currentRecipeData.get(AppConstants.NAME_KEY),
                            (List<Map<String, String>>) newIngredientList,
                            (List<Map<String, String>>) newStepsList,
                            String.valueOf(currentRecipeData.get(AppConstants.SERVINGS_KEY)),
                            (String) currentRecipeData.get(AppConstants.IMAGE_KEY)
                    );

            recipeList.add(recipe);

        }

        return recipeList;

    }

}
