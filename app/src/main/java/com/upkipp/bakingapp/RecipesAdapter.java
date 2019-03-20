package com.upkipp.bakingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.RecipeViewHolder> {
    private Context mContext;
    private OnRecipeItemClickListener mOnRecipeItemClickListener;
    private List<Recipe> mRecipeList;

    public RecipesAdapter(Context context, List<Recipe> recipeList, OnRecipeItemClickListener clickListener) {
        mContext = context;
        mRecipeList = recipeList;
        mOnRecipeItemClickListener = clickListener;
    }

    public interface OnRecipeItemClickListener {
        void onItemClick(int position);
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            View placeholderView = itemView.findViewById(R.id.placeholer);
        }

        @Override
        public void onClick(View v) {
            mOnRecipeItemClickListener.onItemClick(getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        int resourceId = R.layout.recipeLayout;

        View view = layoutInflater.inflate(resourceId, viewGroup, false);

        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder recipeViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        if (mRecipeList != null) return mRecipeList.size();

        return 0;

    }
}
