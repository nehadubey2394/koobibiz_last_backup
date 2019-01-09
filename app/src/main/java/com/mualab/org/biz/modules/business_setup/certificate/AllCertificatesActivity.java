package com.mualab.org.biz.modules.business_setup.certificate;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.dialogs.NoConnectionDialog;
import com.mualab.org.biz.dialogs.Progress;
import com.mualab.org.biz.model.Certificate;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.modules.business_setup.certificate.adapter.CertificatesListAdapter;
import com.mualab.org.biz.session.Session;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.ConnectionDetector;
import com.mualab.org.biz.util.Helper;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllCertificatesActivity extends AppCompatActivity {
    private TextView tvNoDataFound;
    private CertificatesListAdapter certificatesListAdapter;
    private RecyclerView rvCertificates;
    private List<Certificate> certificates;
    private AppCompatButton btnAddCertificate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_certificates);
        initView();
    }

    private void initView(){
        certificates = new ArrayList<>();
        ImageView ivHeaderBack = findViewById(R.id.ivHeaderBack);
        TextView tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        rvCertificates = findViewById(R.id.rvCertificates);
        tvNoDataFound = findViewById(R.id.tvNoDataFound);
        tvHeaderTitle.setText("Add Certificate");

        certificatesListAdapter = new CertificatesListAdapter(AllCertificatesActivity.this, certificates,
                new CertificatesListAdapter.onActionListner() {
                    @Override
                    public void onClick(int position, Certificate companyDetail) {
                        Certificate certificate = certificates.get(position);
                        Intent intent = new Intent(AllCertificatesActivity.this,ZoomCertificateActivity.class);
                        intent.putExtra("certificateImage",certificate.certificateImage);
                        startActivity(intent);
                        //  showLargeImage(certificate);
                    }

                    @Override
                    public void onEdit(int position, Certificate certificate) {
                        Intent intent = new Intent(AllCertificatesActivity.this,AddCertificateActivity.class);
                        intent.putExtra("certificate",certificate);
                        startActivityForResult(intent,52);
                    }

                    @Override
                    public void onDelete(int position, Certificate companyDetail) {
                        showAlertDailog(String.valueOf(companyDetail._id),position);
                    }
                });

        LinearLayoutManager layoutManager = new LinearLayoutManager(AllCertificatesActivity.this);
        rvCertificates.setLayoutManager(layoutManager);
        rvCertificates.setAdapter(certificatesListAdapter);

        ivHeaderBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnAddCertificate = findViewById(R.id.btnAddCertificate);
        btnAddCertificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AllCertificatesActivity.this,AddCertificateActivity.class);
                startActivityForResult(intent,51);
            }
        });;

        apiForGetCertificates();
    }

    private void showAlertDailog(final String certificateId, final int position){
        final android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(AllCertificatesActivity.this, R.style.MyDialogTheme);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Alert!");
        alertDialog.setMessage("Are you sure want to delete certificate?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                apiForDeleteCertificates(certificateId,position);

            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();

    }

    private void showLargeImage(Certificate certificate){
        View dialogView = View.inflate(AllCertificatesActivity.this, R.layout.dialog_large_image_view, null);
        final Dialog dialog = new Dialog(AllCertificatesActivity.this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.InOutAnimation;
        dialog.setContentView(dialogView);

        ImageView ivCertificate = dialogView.findViewById(R.id.ivCertificate);
        ImageView btnBack = dialogView.findViewById(R.id.btnBack);
        Picasso.with(AllCertificatesActivity.this).load(certificate.certificateImage).
                priority(Picasso.Priority.HIGH).noPlaceholder().into(ivCertificate);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            apiForGetCertificates();
           /* if (requestCode == 51) {
                Certificate certificate = (Certificate) data.getSerializableExtra("certificates");
                certificates.add(certificate);
                certificatesListAdapter.notifyDataSetChanged();
            }else {
                apiForGetCertificates();
            }*/
        }
    }

    private void apiForGetCertificates(){
        Session session = Mualab.getInstance().getSessionManager();
        User user = session.getUser();

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(AllCertificatesActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if(isConnected){
                        dialog.dismiss();
                        apiForGetCertificates();
                    }
                }
            }).show();
        }

        Map<String, String> params = new HashMap<>();
        params.put("artistId", String.valueOf(user.id));
        params.put("type", "artist");

        HttpTask task = new HttpTask(new HttpTask.Builder(AllCertificatesActivity.this, "getAllCertificate",
                new HttpResponceListner.Listener() {
                    @Override
                    public void onResponse(String response, String apiName) {
                        try {
                            JSONObject js = new JSONObject(response);
                            String status = js.getString("status");
                            String message = js.getString("message");
                            if (status.equalsIgnoreCase("success")) {
                                rvCertificates.setVisibility(View.VISIBLE);
                                tvNoDataFound.setVisibility(View.GONE);
                                certificates.clear();
                                JSONArray jsonArray = js.getJSONArray("allCertificate");

                                for(int i=0; i<jsonArray.length(); i++){
                                    Gson gson = new Gson();
                                    JSONObject cObj = (JSONObject) jsonArray.get(i);
                                    Certificate item = gson.fromJson(String.valueOf(cObj), Certificate.class);
                                    item.status = cObj.getInt("status");
                                    item._id = cObj.getInt("_id");
                                    certificates.add(item);
                                }
                                certificatesListAdapter.notifyDataSetChanged();
                            }else {
                                rvCertificates.setVisibility(View.GONE);
                                tvNoDataFound.setVisibility(View.VISIBLE);
                            }

                            if (certificates.size()==0)
                                btnAddCertificate.setText("Add");
                            else
                                btnAddCertificate.setText("Add More");

                        } catch (Exception e) {
                            Progress.hide(AllCertificatesActivity.this);
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void ErrorListener(VolleyError error) {
                        try{
                            Helper helper = new Helper();
                            if (helper.error_Messages(error).contains("Session")){
                                Mualab.getInstance().getSessionManager().logout();
                                // MyToast.getInstance(BookingActivity.this).showDasuAlert(helper.error_Messages(error));
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }})
                .setAuthToken(user.authToken)
                .setProgress(true)
                .setBody(params, HttpTask.ContentType.APPLICATION_JSON));
        //.setBody(params, "application/x-www-form-urlencoded"));

        task.execute(this.getClass().getName());
    }

    private void apiForDeleteCertificates(final String certificateId, final int position){
        Session session = Mualab.getInstance().getSessionManager();
        User user = session.getUser();

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(AllCertificatesActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if(isConnected){
                        dialog.dismiss();
                        apiForDeleteCertificates(certificateId,position);
                    }
                }
            }).show();
        }

        Map<String, String> params = new HashMap<>();
        params.put("artistId", String.valueOf(user.id));
        params.put("certificateId", certificateId);

        HttpTask task = new HttpTask(new HttpTask.Builder(AllCertificatesActivity.this, "deleteCertificate",
                new HttpResponceListner.Listener() {
                    @Override
                    public void onResponse(String response, String apiName) {
                        try {
                            JSONObject js = new JSONObject(response);
                            String status = js.getString("status");
                            String message = js.getString("message");
                            if (status.equalsIgnoreCase("success")) {
                                certificates.remove(position);
                                certificatesListAdapter.notifyItemRemoved(position);
                            }

                            if (certificates.size()==0)
                            {
                                btnAddCertificate.setText("Add");
                                rvCertificates.setVisibility(View.GONE);
                                tvNoDataFound.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) {
                            Progress.hide(AllCertificatesActivity.this);
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void ErrorListener(VolleyError error) {
                        try{
                            Helper helper = new Helper();
                            if (helper.error_Messages(error).contains("Session")){
                                Mualab.getInstance().getSessionManager().logout();
                                // MyToast.getInstance(BookingActivity.this).showDasuAlert(helper.error_Messages(error));
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }})
                .setAuthToken(user.authToken)
                .setProgress(true)
                .setBody(params, HttpTask.ContentType.APPLICATION_JSON));
        //.setBody(params, "application/x-www-form-urlencoded"));

        task.execute(this.getClass().getName());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
