package com.upkipp.bakingapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.upkipp.bakingapp.R;

public class DescriptionFragment extends Fragment{
    Context mContext;
    TextView mDescriptionTextView;

    private String mDescription;
    private String mIngredients;

    public DescriptionFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_description, container, false);

        defineViews(rootView);

        setViewValues();

        return rootView;
    }

    public void setIngredients(String ingredients) {
        this.mIngredients = ingredients;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    private void defineViews(View rootView) {
        mContext = rootView.getContext();
        mDescriptionTextView = rootView.findViewById(R.id.description_text_view);
    }

    private void setViewValues() {
        mDescriptionTextView.setText(mDescription);
    }

}
