package com.mualab.org.biz.modules.business_setup.payment_setup;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
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
import com.mualab.org.biz.dialogs.Progress;
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.session.PreRegistrationSession;
import com.mualab.org.biz.session.Session;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.ConnectionDetector;
import com.mualab.org.biz.util.Helper;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PaymentSetupActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText ed_firstName, ed_lastName, ed_accountNumber, ed_sortCode;
    private String firstName, lastName, accountNo, sortCode;
    private PreRegistrationSession bpSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_setup);
        initView();
    }

    private void initView(){
        ImageView ivHeaderBack = findViewById(R.id.ivHeaderBack);
        TextView tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        tvHeaderTitle.setText(getString(R.string.payment_set_up));

        ed_firstName = findViewById(R.id.ed_firstName);
        ed_lastName = findViewById(R.id.ed_lastName);
        ed_sortCode = findViewById(R.id.ed_sortCode);
        ed_accountNumber = findViewById(R.id.ed_accountNumber);

        AppCompatButton btn_addAccount = findViewById(R.id.btn_addAccount);

        ivHeaderBack.setOnClickListener(this);
        btn_addAccount.setOnClickListener(this);
        apiForGetBankDetail();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ivHeaderBack:
                finish();
                break;

            case R.id.btn_addAccount:
                if(isValidInputData()){
                    updateDataIntoServer();
                }
                break;
        }

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
        }else return true; return false;
    }

    private void updateDataIntoServer(){
        User user = Mualab.getInstance().getSessionManager().getUser();
        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(PaymentSetupActivity.this, new NoConnectionDialog.Listner() {
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

        new HttpTask(new HttpTask.Builder(PaymentSetupActivity.this, "addBankDetail", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");

                    if (status.equalsIgnoreCase("success")) {
                        finish();
                    }else
                        showToast(message);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                try {
                    Helper helper = new Helper();
                    if (helper.error_Messages(error).contains("Session")) {
                        Mualab.getInstance().getSessionManager().logout();
                        //      MyToast.getInstance(BookingActivity.this).showSmallCustomToast(helper.error_Messages(error));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }})
                //.setParam(params)
                .setAuthToken(user.authToken)
                .setBody(params, HttpTask.ContentType.APPLICATION_JSON)
                .setMethod(Request.Method.POST)
                .setProgress(true))
                .execute("addBankDetail");
    }

    protected void showToast(String msg){
        if(!TextUtils.isEmpty(msg))
            MyToast.getInstance(PaymentSetupActivity.this).showDasuAlert(msg);

    }

    private void apiForGetBankDetail(){
        Session session = Mualab.getInstance().getSessionManager();
        User user = session.getUser();

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(PaymentSetupActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if(isConnected){
                        dialog.dismiss();
                        apiForGetBankDetail();
                    }
                }
            }).show();
        }

        //  Map<String, String> params = new HashMap<>();
        //  params.put("artistId", String.valueOf(user.id));

        HttpTask task = new HttpTask(new HttpTask.Builder(PaymentSetupActivity.this,
                "bankAccountDetail", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    if (status.equalsIgnoreCase("sucess")) {
                        JSONObject msgObj = js.getJSONObject("message");
                        JSONObject legal_entity = msgObj.getJSONObject("legal_entity");
                        String first_name = legal_entity.getString("first_name");
                        String last_name = legal_entity.getString("last_name");
                        ed_firstName.setText(first_name);
                        ed_lastName.setText(last_name);

                    }/*else {
                        MyToast.getInstance(PaymentSetupActivity.this).showDasuAlert(message);

                    }*/

                } catch (Exception e) {
                    Progress.hide(PaymentSetupActivity.this);
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
                .setAuthToken(user.authToken)
                .setProgress(true).setRetryPolicy(10000, 0, 1f)
        );
        task.execute(this.getClass().getName());
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
