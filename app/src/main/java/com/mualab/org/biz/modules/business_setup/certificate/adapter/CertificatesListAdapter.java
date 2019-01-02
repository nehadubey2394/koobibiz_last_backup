package com.mualab.org.biz.modules.business_setup.certificate.adapter;


import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mualab.org.biz.R;
import com.mualab.org.biz.model.Certificate;
import com.mualab.org.biz.model.company_management.CompanyDetail;
import com.squareup.picasso.Picasso;

import java.util.List;


public class CertificatesListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<Certificate> certificates;
    private onActionListner actionListner;


    public CertificatesListAdapter(Context context, List<Certificate> certificates,
                                   onActionListner actionListner) {
        this.context = context;
        this.certificates = certificates;
        this.actionListner = actionListner;
    }

    public interface onActionListner{

        void onClick(int position, Certificate companyDetail);
        void onEdit(int position, Certificate companyDetail);
        void onDelete(int position, Certificate companyDetail);

    }


    @Override
    public int getItemCount() {
        return certificates.size();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_new_certificates_list, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {

        final ViewHolder holder = ((ViewHolder) viewHolder);
        final Certificate item = certificates.get(position);

        holder.tvExpertize.setText(item.title);
        holder.tvDescription.setText(item.description);

        if (!item.certificateImage.equals("")){
            Picasso.with(context).load(item.certificateImage).placeholder(R.drawable.gallery_placeholder).
                    fit().into(holder.ivCertificate);
        }else {
            holder.ivCertificate.setImageDrawable(context.getResources().getDrawable(R.drawable.gallery_placeholder));
        }

    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView tvExpertize,tvDescription;
        ImageView ivCertificate,ivRemoveCertificate,ivEditCertificate;

        private ViewHolder(View itemView)
        {
            super(itemView);

            tvExpertize = itemView.findViewById(R.id.tvExpertize);
            ivCertificate = itemView.findViewById(R.id.ivCertificate);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivRemoveCertificate = itemView.findViewById(R.id.ivRemoveCertificate);
            ivEditCertificate = itemView.findViewById(R.id.ivEditCertificate);

            ivEditCertificate.setOnClickListener(this);
            ivRemoveCertificate.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.ivEditCertificate:
                    Certificate certificate1 = certificates.get(getAdapterPosition());
                    if (actionListner!=null)
                        actionListner.onEdit(getAdapterPosition(),certificate1);

                    break;

                case R.id.ivRemoveCertificate:
                    Certificate certificate = certificates.get(getAdapterPosition());
                    if (actionListner!=null)
                        actionListner.onDelete(getAdapterPosition(),certificate);

                    break;
            }

        }
    }

}