package com.mualab.org.biz.modules.profile_setup.adapter;

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
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.modules.profile_setup.modle.AddedCategory;

import java.util.List;


public class AddedCategoryListAdapter extends RecyclerView.Adapter<AddedCategoryListAdapter.ViewHolder> {
    private Context context;
    private List<AddedCategory> addedCategoryList;
    private CategorySelectListener bizTypeSelectListener = null;

    public interface CategorySelectListener {

        void onBizTypeSelect(int position, AddedCategory addedCategory);

        void onCategoryRemove(int position, AddedCategory addedCategory);

    }

    // Constructor of the class
    public AddedCategoryListAdapter(Context context, List<AddedCategory> addedCategoryList,
                                    CategorySelectListener bizTypeSelectListener) {
        this.context = context;
        this.addedCategoryList = addedCategoryList;
        this.bizTypeSelectListener = bizTypeSelectListener;
    }

    // get the size of the list
    @Override
    public int getItemCount() {
        return addedCategoryList.size();
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

        AddedCategory addedCategory =  addedCategoryList.get(listPosition);
        holder.tvBizTypeTitle.setText(addedCategory.subServiceName);
        if (addedCategory.isChecked) {
            holder.ivRightIcon.setVisibility(View.VISIBLE);
            holder.tvBizTypeTitle.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        }
        else {
            holder.tvBizTypeTitle.setTextColor(context.getResources().getColor(R.color.text_color));
            holder.ivRightIcon.setVisibility(View.GONE);
        }

        if (addedCategory.status.equals("0"))
            holder.rlApprovalStatus.setVisibility(View.VISIBLE);
        else
            holder.rlApprovalStatus.setVisibility(View.GONE);

    }

    // Static inner class to initialize the views of rows
    class ViewHolder extends RecyclerView.ViewHolder   {
        private ImageView ivRightIcon,ivRemove;
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
            ivRemove =  itemView.findViewById(R.id.ivRemove);

            ivRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AddedCategory addedCategory = addedCategoryList.get(getAdapterPosition());

                    if (bizTypeSelectListener!=null)
                        bizTypeSelectListener.onCategoryRemove(getAdapterPosition(), addedCategory);
                }
            });

            rlBizType.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AddedCategory addedCategory = addedCategoryList.get(getAdapterPosition());

                    if (addedCategory.status.equals("0"))
                        MyToast.getInstance(context).showDasuAlert("Your category request is pending");
                    else
                    if (bizTypeSelectListener!=null)
                        bizTypeSelectListener.onBizTypeSelect(getAdapterPosition(), addedCategory);

                }
            });
        }

    }

}
