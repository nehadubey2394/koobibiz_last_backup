package com.mualab.org.biz.modules.profile.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.mualab.org.biz.R;
import com.mualab.org.biz.modules.profile.modle.AllCategory;

import java.util.List;


public class AllCategoryListAdapter extends RecyclerView.Adapter<AllCategoryListAdapter.ViewHolder> {
    private Context context;
    private List<AllCategory> businessTypeList;
    private CategorySelectListener bizTypeSelectListener = null;

    public interface CategorySelectListener {

        void onCategorySelect(int position, AllCategory businessType);

    }

    // Constructor of the class
    public AllCategoryListAdapter(Context context, List<AllCategory> businessTypeList,
                                  CategorySelectListener bizTypeSelectListener) {
        this.context = context;
        this.businessTypeList = businessTypeList;
        this.bizTypeSelectListener = bizTypeSelectListener;
    }

    // get the size of the list
    @Override
    public int getItemCount() {
        return businessTypeList.size();
    }


    // specify the row layout file and click for each row
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_all_businesstype, parent, false);
        return new ViewHolder(view);
    }

    // load data in each row element
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int listPosition) {

        AllCategory businessType =  businessTypeList.get(listPosition);
        holder.tvBizTypeTitle.setText(businessType.title);
        if (businessType.isChecked){
            holder.ivRightIcon.setVisibility(View.VISIBLE);
            holder.tvBizTypeTitle.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        }
        else {
            holder.tvBizTypeTitle.setTextColor(context.getResources().getColor(R.color.text_color));
            holder.ivRightIcon.setVisibility(View.GONE);
        }

        holder.sample1.setRightSwipeEnabled(false);
    }

    // Static inner class to initialize the views of rows
    class ViewHolder extends RecyclerView.ViewHolder   {
        private ImageView ivRightIcon;
        private TextView tvBizTypeTitle;
        private RelativeLayout rlApprovalStatus,rlBizType;
        private SwipeLayout sample1;
        private ViewHolder(View itemView)
        {
            super(itemView);

            tvBizTypeTitle =  itemView.findViewById(R.id.tvBizTypeTitle);
            ivRightIcon =  itemView.findViewById(R.id.ivRightIcon);
            rlApprovalStatus =  itemView.findViewById(R.id.rlApprovalStatus);
            rlBizType =  itemView.findViewById(R.id.rlBizType);
            sample1 =  itemView.findViewById(R.id.sample1);

            rlBizType.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AllCategory businessType = businessTypeList.get(getAdapterPosition());

                    if (bizTypeSelectListener!=null)
                        bizTypeSelectListener.onCategorySelect(getAdapterPosition(), businessType);

                }
            });
        }

    }

}
