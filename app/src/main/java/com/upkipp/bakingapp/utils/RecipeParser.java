package com.upkipp.bakingapp.utils;

import com.google.gson.Gson;
import com.upkipp.bakingapp.models.Ingredient;
import com.upkipp.bakingapp.models.Recipe;
import com.upkipp.bakingapp.models.Step;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//parses recipe json strings into recipe objects
public class RecipeParser {

    public static List<Recipe> parseRecipes(String responseString) {
        List<Recipe> recipeList = new ArrayList<>();

        //recipe response string using gson
        Gson gson = new Gson();
        List gsonToList = gson.fromJson(responseString, List.class);

        //loop through recipe data and create recipe objects
        for (int index = 0; index < gsonToList.size() ; index++) {
            Map currentRecipeData = (Map) gsonToList.get(index);

            List<Ingredient> ingredientList = new ArrayList<>();

            List ingredientMapList = (List)currentRecipeData.get(AppConstants.INGREDIENTS_KEY);

            //loop through ingredient map and add to ingredient list
            for (int ingredientIndex = 0;
                 ingredientIndex < ingredientMapList.size();
                 ingredientIndex++) {

                //get current recipe
                Map currentIngredientMap = (Map) ingredientMapList.get(ingredientIndex);

                //create ingredient object
                Ingredient currentIngredient = createIngredientFromData(currentIngredientMap);

                //add to ingredient list
                ingredientList.add(currentIngredient);
            }

            List<Step> stepList = new ArrayList<>();

            List stepMapList = (List)currentRecipeData.get(AppConstants.STEPS_KEY);

            //loop through step map and add to step list
            for (int stepIndex = 0; stepIndex < stepMapList.size() ; stepIndex++) {
                //get current step data
                Map currentStepMap = (Map) stepMapList.get(stepIndex);

                //create step object
                Step currentStep = createStepFromData(currentStepMap);

                //add to step list
                stepList.add(currentStep);
            }

            //create new Recipe from currentRecipeData, ingredients and steps
            Recipe recipe = createRecipeObject(currentRecipeData, ingredientList, stepList);

            //add to recipe list
            recipeList.add(recipe);

        }

        return recipeList;

    }

    private static Recipe createRecipeObject(Map currentRecipeData, List<Ingredient> ingredientList, List<Step> stepList) {
        return new Recipe(
                //get by keys
                //id retrieved as double
                String.valueOf(currentRecipeData.get(AppConstants.RECIPE_ID_KEY)),
                (String) currentRecipeData.get(AppConstants.RECIPE_NAME_KEY),
                ingredientList,
                stepList,
                String.valueOf(currentRecipeData.get(AppConstants.SERVINGS_KEY)),
                (String) currentRecipeData.get(AppConstants.IMAGE_KEY)
        );
    }

    private static Step createStepFromData(Map currentStepMap) {
        //gson numbers default to double
        Double idDoubleValue = (double)currentStepMap.get(AppConstants.STEP_ID_KEY);

        return new Step(String.valueOf(idDoubleValue.intValue()),
                (String) currentStepMap.get(AppConstants.STEP_SHORT_DESCRIPTION_KEY),
                (String) currentStepMap.get(AppConstants.STEP_DESCRIPTION_KEY),
                (String) currentStepMap.get(AppConstants.STEP_VIDEO_URL_KEY),
                (String) currentStepMap.get(AppConstants.STEP_THUMBNAIL_URL_KEY)
);
    }

    private static Ingredient createIngredientFromData(Map currentIngredientMap) {
        return new Ingredient(
                String.valueOf(currentIngredientMap.
                        get(AppConstants.INGREDIENT_QUANTITY_KEY)),
                (String) currentIngredientMap.get(AppConstants
                        .INGREDIENT_MEASURE_KEY),
                (String) currentIngredientMap.get(AppConstants
                        .INGREDIENT_NAME_KEY));
    }

}
