/*
 * Copyright (C) 2019 Aayat Mimiko
 */

package com.atmko.bakerly.utils;

import android.content.Context;
import android.widget.ImageView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.atmko.bakerly.R;

//class that contains internet calls
public class NetworkUtils {

    //gets and returns recipes request
    public static ANRequest getAllRecipes() {
        return AndroidNetworking.get(AppConstants.RECIPES_URL_STRING).build();

    }

    //loads images into ImageViews using glide
    public static void loadImage(Context context, String urlString, ImageView imageView) {
        //configure glide behaviour
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.loading_placeholder)
                .error(R.drawable.app_icon);

        Glide.with(context)
                .load(urlString)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(requestOptions)
                .into(imageView);
    }
}
