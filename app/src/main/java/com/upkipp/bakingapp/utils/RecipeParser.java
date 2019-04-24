package com.upkipp.bakingapp.utils;

import com.google.gson.Gson;
import com.upkipp.bakingapp.models.Ingredient;
import com.upkipp.bakingapp.models.Recipe;
import com.upkipp.bakingapp.models.Step;

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

            List<Ingredient> ingredientList = new ArrayList<>();

            List ingredientMapList = (List)currentRecipeData.get(AppConstants.INGREDIENTS_KEY);
            for (int ingredientIndex = 0; ingredientIndex < ingredientMapList.size() ; ingredientIndex++) {
                Map currentIngredientMap = (Map) ingredientMapList.get(ingredientIndex);//get current movie


                Ingredient currentIngredient =
                        new Ingredient(
                                String.valueOf(currentIngredientMap.get("quantity")),
                                (String) currentIngredientMap.get(AppConstants.INGREDIENT_MEASURE_KEY),
                                (String) currentIngredientMap.get(AppConstants.INGREDIENT_NAME_KEY));

                ingredientList.add(currentIngredient);
            }

            List<Step> stepList = new ArrayList<>();

            List stepMapList = (List)currentRecipeData.get(AppConstants.STEPS_KEY);
            for (int stepIndex = 0; stepIndex < stepMapList.size() ; stepIndex++) {
                Map currentStepMap = (Map) stepMapList.get(stepIndex);//get current movie

                //gson numbers default to double
                Double idDoubleValue = (double)currentStepMap.get(AppConstants.STEP_ID_KEY);

                Step currentStep =
                        new Step(String.valueOf(idDoubleValue.intValue()),
                                (String) currentStepMap.get(AppConstants.STEP_SHORT_DESCRIPTION_KEY),
                                (String) currentStepMap.get(AppConstants.STEP_DESCRIPTION_KEY),
                                (String) currentStepMap.get(AppConstants.STEP_VIDEO_URL_KEY),
                                (String) currentStepMap.get(AppConstants.STEP_THUMBNAIL_URL_KEY)
                );

                stepList.add(currentStep);
            }

            //create new Recipe from currentRecipeData
            Recipe recipe =
                    new Recipe(
                            //get by keys
                            //id retrieved as double
                            String.valueOf(currentRecipeData.get(AppConstants.RECIPE_ID_KEY)),
                            (String) currentRecipeData.get(AppConstants.RECIPE_NAME_KEY),
                            ingredientList,
                            stepList,
                            String.valueOf(currentRecipeData.get(AppConstants.SERVINGS_KEY)),
                            (String) currentRecipeData.get(AppConstants.IMAGE_KEY)
                    );

            recipeList.add(recipe);

        }

        return recipeList;

    }

}
