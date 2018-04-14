package com.mualab.org.biz.modules.add_staff.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.mualab.org.biz.R;

/**
 */

public class LoadingViewHolder extends RecyclerView.ViewHolder{
    public ProgressBar progressBar;
    public LoadingViewHolder(View view) {
        super(view);
        progressBar = view.findViewById(R.id.ProgressBar);
    }
}
