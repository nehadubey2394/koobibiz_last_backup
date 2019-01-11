package com.mualab.org.biz.modules.business_setup.voucher_code;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.dialogs.NoConnectionDialog;
import com.mualab.org.biz.dialogs.Progress;
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.model.VoucherCode;
import com.mualab.org.biz.modules.business_setup.voucher_code.adapter.VoucherListAdapter;
import com.mualab.org.biz.session.Session;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.ConnectionDetector;
import com.mualab.org.biz.util.Helper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoucherCodeActivity extends AppCompatActivity {
    private RecyclerView rvVouchers;
    private TextView tvNoRecord;
    private VoucherListAdapter voucherListAdapter;
    private List<VoucherCode> voucherCodeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher_code);
        initView();
    }

    private void initView(){
        ImageView ivHeaderBack = findViewById(R.id.ivHeaderBack);
        ImageView ivAddVoucher = findViewById(R.id.ivAddVoucher);
        TextView tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        tvHeaderTitle.setText(getString(R.string.voucher_code));
        tvNoRecord = findViewById(R.id.tvNoRecord);
        rvVouchers = findViewById(R.id.rvVouchers);
        voucherCodeList = new ArrayList<>();

        voucherListAdapter = new VoucherListAdapter(VoucherCodeActivity.this, voucherCodeList,
                new VoucherListAdapter.onActionListner() {

                    @Override
                    public void onClick(int position, VoucherCode voucherCode) {

                    }

                    @Override
                    public void onEdit(int position, VoucherCode voucherCode) {
                        Intent intent =  new Intent(VoucherCodeActivity.this,AddVoucherCodeActivity.class);
                        intent.putExtra("voucherCode",voucherCode);
                        startActivityForResult(intent,20);
                    }

                    @Override
                    public void onDelete(int position, VoucherCode voucherCode) {
                        showAlertDailog(position,voucherCode);
                    }
                });

        LinearLayoutManager layoutManager = new LinearLayoutManager(VoucherCodeActivity.this, LinearLayoutManager.VERTICAL, false);
        rvVouchers.setLayoutManager(layoutManager);
        rvVouchers.setAdapter(voucherListAdapter);

        ivHeaderBack.setOnClickListener(view -> finish());


        ivAddVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(VoucherCodeActivity.this,AddVoucherCodeActivity.class);
                startActivityForResult(intent,20);
            }
        });

        apiForGetVouchers();
    }

    private void apiForGetVouchers(){
        Session session = Mualab.getInstance().getSessionManager();
        User user = session.getUser();

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(VoucherCodeActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if(isConnected){
                        dialog.dismiss();
                        apiForGetVouchers();
                    }
                }
            }).show();
        }

        //  Map<String, String> params = new HashMap<>();
        //  params.put("artistId", String.valueOf(user.id));

        HttpTask task = new HttpTask(new HttpTask.Builder(VoucherCodeActivity.this,
                "allvoucherList", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");

                    if (status.equalsIgnoreCase("success")) {

                        voucherCodeList.clear();

                        tvNoRecord.setVisibility(View.GONE);
                        rvVouchers.setVisibility(View.VISIBLE);

                        JSONArray data = js.getJSONArray("data");
                        if (data!=null) {
                            for(int i=0; i<data.length(); i++){
                                Gson gson = new Gson();
                                JSONObject cObj = (JSONObject) data.get(i);
                                VoucherCode item = gson.fromJson(String.valueOf(cObj), VoucherCode.class);
                                voucherCodeList.add(item);
                            }

                        }else {
                            tvNoRecord.setVisibility(View.VISIBLE);
                            rvVouchers.setVisibility(View.GONE);
                        }
                        voucherListAdapter.notifyDataSetChanged();
                    }else {
                        tvNoRecord.setVisibility(View.VISIBLE);
                        rvVouchers.setVisibility(View.GONE);
                    }

                } catch (Exception e) {
                    Progress.hide(VoucherCodeActivity.this);
                    e.printStackTrace();
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                try{
                    Helper helper = new Helper();
                    if (helper.error_Messages(error).contains("Session")){
                        Mualab.getInstance().getSessionManager().logout();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }


            }})
                .setAuthToken(user.authToken).setMethod(Request.Method.GET)
                .setProgress(true).setRetryPolicy(10000, 0, 1f)
        );
        task.execute(this.getClass().getName());
    }

    private void apiDeleteVocher(final VoucherCode voucherCode, final int pos) {
        User user = Mualab.getInstance().getSessionManager().getUser();

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(VoucherCodeActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if (isConnected) {
                        dialog.dismiss();
                        apiDeleteVocher(voucherCode,pos);
                    }
                }
            }).show();
        }

        //  pbLoder.setVisibility(View.VISIBLE);
        Map<String,String> body = new HashMap<>();
        body.put("id", String.valueOf(voucherCode._id));


        HttpTask task = new HttpTask(new HttpTask.Builder(VoucherCodeActivity.this,
                "deleteVoucher", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    Progress.hide(VoucherCodeActivity.this);
                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");

                    if (status.equalsIgnoreCase("success")) {
                        voucherCodeList.remove(pos);
                        voucherListAdapter.notifyItemRemoved(pos);
                        MyToast.getInstance(VoucherCodeActivity.this).showDasuAlert(message);
                    }else {
                        MyToast.getInstance(VoucherCodeActivity.this).showDasuAlert(message);
                    }
                    //    pbLoder.setVisibility(View.GONE);
                    if (voucherCodeList.size()==0) {
                        tvNoRecord.setVisibility(View.VISIBLE);
                        rvVouchers.setVisibility(View.GONE);
                    }else {
                        tvNoRecord.setVisibility(View.GONE);
                        rvVouchers.setVisibility(View.VISIBLE);
                    }

                }catch (Exception e) {
                    Progress.hide(VoucherCodeActivity.this);
                    e.printStackTrace();
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                try {
                    Progress.hide(VoucherCodeActivity.this);
                    Helper helper = new Helper();
                    if (helper.error_Messages(error).contains("Session")) {
                        Mualab.getInstance().getSessionManager().logout();
                    }else
                        MyToast.getInstance(VoucherCodeActivity.this).showDasuAlert(helper.error_Messages(error));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        })
                .setAuthToken(user.authToken)
                .setProgress(true)
                .setParam(body));

        task.execute(VoucherCodeActivity.class.getName());
    }

    private void showAlertDailog(final int position, final VoucherCode voucherCode){
        final android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(VoucherCodeActivity.this, R.style.MyDialogTheme);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Alert!");
        alertDialog.setMessage("Are you sure want to remove this voucher?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                dialog.cancel();
                apiDeleteVocher(voucherCode,position);

            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            apiForGetVouchers();
        }

    }
}
