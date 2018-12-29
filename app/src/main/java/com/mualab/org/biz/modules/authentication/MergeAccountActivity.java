package com.mualab.org.biz.modules.authentication;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.dialogs.NoConnectionDialog;
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.modules.MainActivity;
import com.mualab.org.biz.modules.NewBaseActivity;
import com.mualab.org.biz.session.Session;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.ConnectionDetector;
import com.mualab.org.biz.util.Helper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MergeAccountActivity extends AppCompatActivity {
    private User user;
    private  String str1="You already have an existing social account using this email and phone number! Would you like to merge your existing social profile_setup "
            ,str2=" with your biz account or create a new social account for your biz profile_setup?";
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merge_account);
        session = new Session(this);

        user = (User) getIntent().getSerializableExtra("userData");

        TextView tvMessege = findViewById(R.id.tvMessege);

        String textToHighlight = "<b>" + "@"+user.userName + "</b> ";

        // Construct the formatted text
        String replacedWith = "<font color='black'>" + textToHighlight + "</font>";

        // Update the TextView text
        tvMessege.setText(Html.fromHtml(str1+replacedWith+str2));

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.btnMerge).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                apiForMergeRecord();
            }
        });

        findViewById(R.id.btnCreateNew).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MergeAccountActivity.this, RegistrationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });


    }

    private void apiForMergeRecord() {

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(MergeAccountActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if (isConnected) {
                        dialog.dismiss();
                        apiForMergeRecord();
                    }
                }
            }).show();
        }

        Map<String, String> body = new HashMap<>();
        body.put("businessType", "independent");
        //   body.put("contactNo", user.contactNo);
        //   body.put("email", user.email);
        body.put("userType", "artist");

        new HttpTask(new HttpTask.Builder(this, "updateRecord", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                System.out.println("response = "+response);
                try {
                    /*{"status":"success","otp":5386,"users":null}*/
                    JSONObject object = new JSONObject(response);
                    String status = object.getString("status");
                    String   message = object.getString("message");

                    if (status.equalsIgnoreCase("success")) {

                        Gson gson = new Gson();
                        JSONObject userObj = object.getJSONObject("user");
                        User user = gson.fromJson(String.valueOf(userObj), User.class);
                        session.createSession(user);
                        session.setBusinessProfileComplete(false);
                        Intent intent = new Intent(MergeAccountActivity.this, NewBaseActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("user", user);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();

                    }else {
                        showToast(message);

                    }

                } catch (JSONException e) {
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
                .setBody(body, HttpTask.ContentType.APPLICATION_JSON)
                .setAuthToken(user.authToken)
                .setProgress(true))
                .execute(this.getClass().getName());
    }

    private void showToast(String msg){
        if (!TextUtils.isEmpty(msg)){
            MyToast.getInstance(this).showDasuAlert(msg);
        }
    }

}
