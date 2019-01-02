package com.mualab.org.biz.modules.profile_setup.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.mualab.org.biz.R;
import com.mualab.org.biz.modules.profile_setup.activity.ServiceDetailActivity;
import com.mualab.org.biz.modules.profile_setup.db_modle.Services;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.List;


public class ServicesListWithHeaderAdapter extends RecyclerItemAdapter implements StickyRecyclerHeadersAdapter {
    private Context context;
    private List<Services> servicesList;
    private int lastPosition = -1,count;
    private onClickListener listener;

    // Constructor of the class
    public ServicesListWithHeaderAdapter(Context context, List<Services> servicesList, onClickListener listener) {
        this.context = context;
        this.servicesList = servicesList;
        this.listener = listener;
    }

    private static class ItemHeaderViewHolder extends RecyclerView.ViewHolder {
        TextView header;
        ItemHeaderViewHolder(View itemView) {
            super(itemView);
            header = itemView.findViewById(R.id.tvCategoryTitle);
        }
    }


    @Override
    public long getHeaderId(int position) {
        Services services =  servicesList.get(position);

        /*  if (services.subserviceId!=null)
            return Math.abs(services.subserviceId.hashCode());
        else
            return position;*/
        if (services.subserviceName!=null)
            return Math.abs(services.subserviceName.hashCode());
        else
            return position;
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_header_category, parent, false);
        return new ItemHeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemHeaderViewHolder) {
            if (getItem(position).subserviceName != null) {
                String header = String.valueOf(getItem(position).subserviceName);
                ((ItemHeaderViewHolder) holder).header.setText(header);
            }else {
                ((ItemHeaderViewHolder) holder).header.setVisibility(View.GONE);
            }
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_child_service_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder h, @SuppressLint("RecyclerView") int position) {

        Services item = servicesList.get(position);
        MyViewHolder holder = ((MyViewHolder)h);

        holder.tvServiceTime.setText(item.completionTime);
        holder.tvCategoryTitle.setText(item.serviceName);
        holder.tvPrice.setText("£"+item.inCallPrice);

      /*  if(position >lastPosition) {

            Animation animation = AnimationUtils.loadAnimation(context,
                    R.anim.item_animation_slide_from_bottom);
            holder.itemView.startAnimation(animation);
            lastPosition = position;
        }*/
    }

    public interface onClickListener{
        void onEditClick(int pos);
        void onDelClick(int pos);
    }

    @Override
    public int getItemCount() {
        return servicesList.size();
    }

    private Services getItem(int position) {
        return servicesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }


    private class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvCategoryTitle,tvServiceTime,tvPrice;
        private SwipeLayout sample1;
        private ImageView ivEdit,ivRemove;
        private RelativeLayout rlItem;
        //private LinearLayout lyRemove,lyEdit;
        private MyViewHolder(View itemView)
        {
            super(itemView);
            itemView.setOnClickListener(this);
            //  lyRemove =  itemView.findViewById(R.id.lyRemove);
            // lyEdit =  itemView.findViewById(R.id.lyEdit);

            tvCategoryTitle =  itemView.findViewById(R.id.tvCategoryTitle);
            tvServiceTime =  itemView.findViewById(R.id.tvServiceTime);
            tvPrice =  itemView.findViewById(R.id.tvPrice);

            rlItem =  itemView.findViewById(R.id.rlItem);

            ivEdit =  itemView.findViewById(R.id.ivEdit);
            ivRemove =  itemView.findViewById(R.id.ivRemove);

            ivEdit.setOnClickListener(this);
            ivRemove.setOnClickListener(this);
            rlItem.setOnClickListener(this);

            sample1 =  itemView.findViewById(R.id.sample1);
            //    sample1.setLeftSwipeEnabled(false);
            /*sample1.setShowMode(SwipeLayout.ShowMode.PullOut);
            sample1.addDrag(SwipeLayout.DragEdge.Left, sample1.findViewById(R.id.right_swipe_wrapper));
            sample1.addDrag(SwipeLayout.DragEdge.Right, sample1.findViewById(R.id.left_swipe_wrapper));

            sample1.addRevealListener(R.id.ivRemove, new SwipeLayout.OnRevealListener() {
                @Override
                public void onReveal(View child, SwipeLayout.DragEdge edge, float fraction, int distance) {

                }
            });*/

        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){

                case R.id.ivEdit:
                    int position=getAdapterPosition();
                    if (position!= -1){
                        listener.onEditClick(position);
                    }
                    break;

                case  R.id.ivRemove :
                    position=getAdapterPosition();
                    if (position!= -1){
                        listener.onDelClick(position);
                    }
                    break;

                case  R.id.rlItem :
                    Services item = servicesList.get(getAdapterPosition());
                    Intent intent = new Intent(context,ServiceDetailActivity.class);
                    intent.putExtra("serviceItem",item);
                    context.startActivity(intent);
                    break;
            }
        }
    }
}