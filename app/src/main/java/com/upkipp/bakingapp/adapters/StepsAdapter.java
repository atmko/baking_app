package com.upkipp.bakingapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.upkipp.bakingapp.R;
import com.upkipp.bakingapp.fragments.StepsFragment;
import com.upkipp.bakingapp.models.Recipe;

import java.util.List;
import java.util.Map;

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.StepViewHolder> {
    private OnStepItemClickListener mOnStepItemClickListener;
    private List<Map<String, String>> mStepList;

    public StepsAdapter(OnStepItemClickListener clickListener) {
        mOnStepItemClickListener = clickListener;
    }

    public interface OnStepItemClickListener {
        void onItemClick(int position);
    }

    public class StepViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private StepViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mOnStepItemClickListener.onItemClick(getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        int resourceId = R.layout.layout_step;
        View view = layoutInflater.inflate(resourceId, viewGroup, false);
        return new StepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StepViewHolder stepViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        if (mStepList != null) return mStepList.size();

        return 0;

    }

    public List<Map<String, String>> getStepList() {
        return mStepList;
    }

    public void setStepList(List<Map<String, String>> stepList) {
        this.mStepList = stepList;
        notifyDataSetChanged();
    }

}
