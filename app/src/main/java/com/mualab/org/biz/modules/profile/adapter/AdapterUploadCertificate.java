package com.mualab.org.biz.modules.profile.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley.VolleyError;
import com.mualab.org.biz.R;
import com.mualab.org.biz.model.Certificate;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by dharmraj on 2/2/18.
 **/

public class AdapterUploadCertificate extends RecyclerView.Adapter<AdapterUploadCertificate.MyViewHolder> {

    private Context context;
    private List<Certificate> certificates;
    private Listner listner;
    private String authToken;

    public AdapterUploadCertificate(Context context , List<Certificate> certificates){
        this.context = context;
        this.certificates = certificates;
    }

    public void setCallBack(Listner listner){
        this.listner = listner;
    }

    public void setAuthtoken(String authToken){
        this.authToken = authToken;
    }

    public interface Listner{
        void onClickPickImge();
        void onRemoveImage(int index);
        void onUpdateIndex(int index);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivCertificate, civ_picImage;
        private CircleImageView civDeleteBtn;
        private ProgressBar progress_bar;

        private MyViewHolder(View view) {
            super(view);
            ivCertificate = view.findViewById(R.id.ivCertificate);
            civ_picImage = view.findViewById(R.id.civ_picImage);
            civDeleteBtn = view.findViewById(R.id.civDeleteBtn);
            progress_bar = view.findViewById(R.id.progress_bar);
        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_certificate_upload, parent, false);
        final MyViewHolder vh =  new MyViewHolder(itemView);
        vh.civ_picImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listner.onClickPickImge();
            }
        });

        vh.civDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //listner.onRemoveImage(vh.getAdapterPosition());
                removeCertificate(vh);
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Certificate cf = certificates.get(position);
        holder.civ_picImage.setVisibility(cf.id==-1?View.VISIBLE:View.GONE);
        holder.ivCertificate.setVisibility(cf.id==-1?View.GONE:View.VISIBLE);
        holder.civDeleteBtn.setVisibility(cf.id==-1?View.GONE:View.VISIBLE);
       // listner.onUpdateIndex(position);
        if(!TextUtils.isEmpty(cf.imageUri)){
            Picasso.with(context).load(cf.imageUri).into(holder.ivCertificate);
        }
    }


    @Override
    public int getItemCount() {
        return certificates.size();
    }



    private synchronized void removeCertificate(final MyViewHolder vh){
        vh.progress_bar.setVisibility(View.VISIBLE);
        vh.civDeleteBtn.setEnabled(false);
        final Certificate certificate = certificates.get(vh.getAdapterPosition());

        final int index = vh.getAdapterPosition();
        Map<String, String> body = new HashMap<>();
        body.put("certificateId",""+certificate.id);

        new HttpTask(new HttpTask.Builder(context, "deleteCertificate", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                vh.progress_bar.setVisibility(View.GONE);
                vh.civDeleteBtn.setEnabled(true);
                certificates.remove(certificate);
                listner.onRemoveImage(index);
                //AdapterUploadCertificate.this.notifyItemRemoved(index);
            }

            @Override
            public void ErrorListener(VolleyError error) {

            }})
                .setParam(body)
                .setAuthToken(authToken))
                .execute("deleteCertificate");

      /*  Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 5000);*/
    }
}
