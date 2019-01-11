package com.mualab.org.biz.modules.business_setup.voucher_code.adapter;


import android.content.Context;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mualab.org.biz.R;
import com.mualab.org.biz.model.VoucherCode;

import java.util.List;


public class VoucherListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<VoucherCode> voucherCodeList;
    private onActionListner actionListner;
    private long mLastClickTime = 0;


    public VoucherListAdapter(Context context, List<VoucherCode> voucherCodeList,
                              onActionListner actionListner) {
        this.context = context;
        this.voucherCodeList = voucherCodeList;
        this.actionListner = actionListner;
    }

    public interface onActionListner{

        void onClick(int position, VoucherCode voucherCode);
        void onEdit(int position, VoucherCode voucherCode);
        void onDelete(int position, VoucherCode voucherCode);

    }

    @Override
    public int getItemCount() {
        return voucherCodeList.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_voucher_code_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int position) {

        final ViewHolder holder = ((ViewHolder) viewHolder);
        final VoucherCode item = voucherCodeList.get(position);

        holder.tvVoucherCode.setText(item.voucherCode);
        if (item.discountType.equals("1"))
            holder.tvDiscount.setText("Discount - £"+item.amount);
        else
            holder.tvDiscount.setText("Discount - "+item.amount+"%");

        holder.tvExpiryDate.setText("Expiry Date - "+item.endDate);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView tvVoucherCode,tvDiscount,tvExpiryDate;
        ImageView ivShare,ivRemove,ivEdit;;

        private ViewHolder(View itemView)
        {
            super(itemView);

            tvVoucherCode = itemView.findViewById(R.id.tvVoucherCode);
            tvDiscount = itemView.findViewById(R.id.tvDiscount);
            tvExpiryDate = itemView.findViewById(R.id.tvExpiryDate);
            ivShare = itemView.findViewById(R.id.ivShare);
            ivRemove = itemView.findViewById(R.id.ivRemove);
            ivEdit = itemView.findViewById(R.id.ivEdit);

            ivShare.setOnClickListener(this);
            ivRemove.setOnClickListener(this);
            ivEdit.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 600) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

            switch (view.getId()){
                case R.id.ivShare:

                    break;
                case R.id.ivRemove:
                    VoucherCode voucherCode = voucherCodeList.get(getAdapterPosition());
                    if (actionListner!=null)
                        actionListner.onDelete(getAdapterPosition(),voucherCode);
                    break;
                case R.id.ivEdit:
                    VoucherCode voucherCode2 = voucherCodeList.get(getAdapterPosition());
                    if (actionListner!=null)
                        actionListner.onEdit(getAdapterPosition(),voucherCode2);
                    break;

            }

        }
    }

}