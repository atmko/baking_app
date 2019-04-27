/*
 * Copyright (C) 2019 Aayat Mimiko
 */

package com.upkipp.bakingapp.utils;

//app/api common values
public class AppConstants {

    static final String RECIPES_URL_STRING = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";

    //recipe parse keys
    public static final String RECIPE_ID_KEY = "id";
    public static final String RECIPE_NAME_KEY = "name";
    public static final String INGREDIENTS_KEY = "ingredients";
    public static final String STEPS_KEY = "steps";
    public static final String SERVINGS_KEY = "servings";
    public static final String IMAGE_KEY = "image";

    //ingredient keys
    public static final String INGREDIENT_QUANTITY_KEY = "quantity";
    public static final String INGREDIENT_MEASURE_KEY = "measure";
    public static final String INGREDIENT_NAME_KEY = "ingredient";

    //steps keys
    public static final String STEP_ID_KEY = "id";
    public static final String STEP_SHORT_DESCRIPTION_KEY = "shortDescription";
    public static final String STEP_DESCRIPTION_KEY = "description";
    public static final String STEP_VIDEO_URL_KEY = "videoURL";
    public static final String STEP_THUMBNAIL_URL_KEY = "thumbnailURL";

    //widget values
    public static final String WIDGET_PREF_DELINEATION = "wid";

}
