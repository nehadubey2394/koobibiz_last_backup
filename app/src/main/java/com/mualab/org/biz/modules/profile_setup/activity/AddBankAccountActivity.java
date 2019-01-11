package com.mualab.org.biz.modules.profile_setup.activity;

import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.dialogs.NoConnectionDialog;
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.modules.MainActivity;
import com.mualab.org.biz.session.PreRegistrationSession;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.ConnectionDetector;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddBankAccountActivity extends AppCompatActivity {
    private EditText ed_firstName, ed_lastName, ed_accountNumber, ed_sortCode;
    private String firstName, lastName, accountNo, sortCode;
    protected User user;
    private PreRegistrationSession bpSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bank_account);
        init();
    }

    private void init(){
        if(user==null) user = Mualab.getInstance().getSessionManager().getUser(); // get session object

        bpSession = Mualab.getInstance().getBusinessProfileSession();

        ImageView iv_back = findViewById(R.id.iv_back);
        ImageView ivKoobiLogo = findViewById(R.id.ivKoobiLogo);
        TextView tvHeaderText = findViewById(R.id.tvHeaderText);
        ivKoobiLogo.setVisibility(View.GONE);
        tvHeaderText.setVisibility(View.VISIBLE);
        tvHeaderText.setText("Banking Details");
        ImageView ivBasicInfo = findViewById(R.id.ivBasicInfo);
        ivBasicInfo.setImageDrawable(getResources().getDrawable(R.drawable.active_circle_img));
        ImageView ivBusinessInfo = findViewById(R.id.ivBusinessInfo);
        ivBusinessInfo.setImageDrawable(getResources().getDrawable(R.drawable.active_circle_img));
        ImageView ivServices = findViewById(R.id.ivServices);
        ivServices.setImageDrawable(getResources().getDrawable(R.drawable.active_circle_img));
        ImageView ivPaymentSetup = findViewById(R.id.ivPaymentSetup);
        ivPaymentSetup.setImageDrawable(getResources().getDrawable(R.drawable.inactive_green_circle_img));
        View line1 = findViewById(R.id.line1);
        line1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        View line2 = findViewById(R.id.line2);
        line2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        View line3 = findViewById(R.id.line3);
        line3.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        TextView tvSkip = findViewById(R.id.tvSkip);
        ed_firstName = findViewById(R.id.ed_firstName);
        ed_lastName = findViewById(R.id.ed_lastName);
        ed_sortCode = findViewById(R.id.ed_sortCode);
        ed_accountNumber = findViewById(R.id.ed_accountNumber);


        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Mualab.getInstance().getSessionManager().setBusinessProfileComplete(true);
                apiForskip();
            }
        });

        findViewById(R.id.btn_addAccount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValidInputData()){
                    updateDataIntoServer();
                }
            }
        });

    }

    private boolean isValidInputData(){
        firstName = ed_firstName.getText().toString().trim();
        lastName = ed_lastName.getText().toString().trim();
        sortCode = ed_sortCode.getText().toString().trim();
        accountNo = ed_accountNumber.getText().toString().trim();

        if(TextUtils.isEmpty(firstName)){
            showToast("Please enter first name");    ed_firstName.requestFocus();
        }else if(TextUtils.isEmpty(lastName)){
            showToast("Please enter last name");     ed_lastName.requestFocus();
        }else if(TextUtils.isEmpty(sortCode)){
            showToast("Please enter sort code");    ed_sortCode.requestFocus();
        }else if(TextUtils.isEmpty(accountNo)){
            showToast("Please enter account number");ed_accountNumber.requestFocus();
        }/*else if(TextUtils.isEmpty(cnfAccountNo)){
            showToast("Please enter confirm account number");ed_cnfAccountNumber.requestFocus();
        }else if(dateOfBirth.equals("dd-mm-yyyy")){
            showToast("please select Date of birth");           tv_dob.requestFocus();
        }else if(!accountNo.equals(cnfAccountNo)){
            showToast("Account number and confirm account number should be same");ed_cnfAccountNumber.requestFocus();
        }else if(TextUtils.isEmpty(postalCode)){
            showToast("please enter Postal code");     ed_postalCode.requestFocus();
        }else if(TextUtils.isEmpty(ssnNumber)){
            showToast("Please enter SSN number");     ed_ssnLast.requestFocus();
        }*/else return true; return false;
    }

    protected void showToast(String msg){
        if(!TextUtils.isEmpty(msg))
            MyToast.getInstance(AddBankAccountActivity.this).showDasuAlert(msg);
        //MyToast.getInstance(mContext).showSmallCustomToast(msg);

    }

    protected void showToast(@StringRes int id){
        MyToast.getInstance(AddBankAccountActivity.this).showDasuAlert(getString(id));
        //MyToast.getInstance(mContext).showSmallCustomToast(msg);
    }

    private void updateDataIntoServer(){

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(AddBankAccountActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if(isConnected){
                        dialog.dismiss();
                        updateDataIntoServer();
                    }
                }
            }).show();
        }

        Map<String,String> params = new HashMap<>();
        params.put("firstName", firstName);
        params.put("lastName", lastName);
        params.put("accountNo", accountNo);
        params.put("routingNumber", sortCode);
        params.put("currency", "gbp");
        params.put("country", "GB");
        /*params.put("dob", dateOfBirth);

        params.put("postalCode", postalCode);
        params.put("ssnLast", ssnNumber);*/

        new HttpTask(new HttpTask.Builder(AddBankAccountActivity.this, "addBankDetail", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");

                    if (status.equalsIgnoreCase("success")) {
                        bpSession.updateRegStep(5);
                        Mualab.getInstance().getSessionManager().setBusinessProfileComplete(true);
                        Intent intent = new Intent(AddBankAccountActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }else
                        showToast(message);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                // listener.onNext();
            }})
                //.setParam(params)
                .setAuthToken(user.authToken)
                .setBody(params, HttpTask.ContentType.APPLICATION_JSON)
                .setMethod(Request.Method.POST)
                .setProgress(true))
                .execute("addBankDetail");
    }

    private void apiForskip(){

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(AddBankAccountActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if(isConnected){
                        dialog.dismiss();
                        apiForskip();
                    }
                }
            }).show();
        }

        Map<String,String> params = new HashMap<>();
        params.put("artistId", String.valueOf(user.id));

        /*params.put("dob", dateOfBirth);

        params.put("postalCode", postalCode);
        params.put("ssnLast", ssnNumber);*/

        new HttpTask(new HttpTask.Builder(AddBankAccountActivity.this, "skipPage", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");

                    if (status.equalsIgnoreCase("success")) {
                        bpSession.updateRegStep(5);
                        Mualab.getInstance().getSessionManager().setBusinessProfileComplete(true);
                        Intent intent = new Intent(AddBankAccountActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }else showToast(message);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                // listener.onNext();
            }})
                //.setParam(params)
                .setAuthToken(user.authToken)
                .setBody(params, HttpTask.ContentType.APPLICATION_JSON)
                .setMethod(Request.Method.POST)
                .setProgress(true))
                .execute("skipPage");
    }

    @Override
    protected void onStop() {
        PreRegistrationSession preSession = new PreRegistrationSession(this);
        preSession.updateRegStep(4);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        PreRegistrationSession preSession = new PreRegistrationSession(this);
        preSession.updateRegStep(4);
        super.onDestroy();
    }
}
