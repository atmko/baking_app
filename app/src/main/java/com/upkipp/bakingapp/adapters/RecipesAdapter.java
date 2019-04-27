package com.upkipp.bakingapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.upkipp.bakingapp.R;
import com.upkipp.bakingapp.models.Recipe;
import com.upkipp.bakingapp.utils.NetworkUtils;

import java.util.List;

// adapter for recipe recycler view
public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.RecipeViewHolder> {
    private OnRecipeItemClickListener mOnRecipeItemClickListener;
    private List<Recipe> mRecipeList;

    public RecipesAdapter(Context context) {
        mOnRecipeItemClickListener = (OnRecipeItemClickListener) context;
    }

    public interface OnRecipeItemClickListener {
        void onItemClick(int position);
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        ImageView recipeImageView;
        TextView nameTextView;

        private RecipeViewHolder(@NonNull View itemView) {
            super(itemView);

            recipeImageView = itemView.findViewById(R.id.recipe_image_view);
            nameTextView = itemView.findViewById(R.id.name_text_view);

            itemView.setOnClickListener(this);
        }

        //override onclick to implement item click listener
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

        int resourceId = R.layout.layout_recipe;
        View view = layoutInflater.inflate(resourceId, viewGroup, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder recipeViewHolder, int position) {
        Context context = recipeViewHolder.recipeImageView.getContext();
        //get current MovieData
        Recipe currentRecipe = mRecipeList.get(position);

        //load image with glide
        NetworkUtils.loadImage(
                context,
                currentRecipe.getImage(),
                recipeViewHolder.recipeImageView);

        //set recipe name in text view
        recipeViewHolder.nameTextView.setText(currentRecipe.getName());

    }

    @Override
    public int getItemCount() {
        if (mRecipeList != null) return mRecipeList.size();

        return 0;

    }

    public List<Recipe> getRecipeList() {
        return mRecipeList;
    }

    public void setRecipeList(List<Recipe> recipeList) {
        this.mRecipeList = recipeList;
        notifyDataSetChanged();
    }

}
