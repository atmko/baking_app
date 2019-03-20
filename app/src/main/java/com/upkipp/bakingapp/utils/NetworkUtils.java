package com.upkipp.bakingapp.utils;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.upkipp.bakingapp.models.Recipe;

import java.util.List;

public class NetworkUtils {

    public static ANRequest getAllRecipes() {
        return AndroidNetworking.get(AppConstants.RECIPES_URL_STRING).build();

    }
}
