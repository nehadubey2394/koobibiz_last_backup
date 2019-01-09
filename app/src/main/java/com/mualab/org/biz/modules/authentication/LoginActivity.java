package com.mualab.org.biz.modules.authentication;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.chat.model.FirebaseUser;
import com.mualab.org.biz.dialogs.NoConnectionDialog;
import com.mualab.org.biz.dialogs.Progress;
import com.mualab.org.biz.helper.Constants;
import com.mualab.org.biz.helper.MySnackBar;
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.modules.NewBaseActivity;
import com.mualab.org.biz.modules.base.BaseActivity;
import com.mualab.org.biz.session.Session;
import com.mualab.org.biz.session.SharedPreferanceUtils;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.ConnectionDetector;
import com.mualab.org.biz.util.Helper;
import com.mualab.org.biz.util.KeyboardUtil;
import com.mualab.org.biz.util.StatusBarUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends BaseActivity {

    private TextView ed_username, ed_password;
    private TextInputLayout input_layout_UserName, input_layout_password;
    private SharedPreferanceUtils sp;
    private Session session;
    //private FirebaseAuth mAuth;
    //private DatabaseReference mDatabase;

    private boolean isRemind = true;
    private boolean doubleBackToExitPressedOnce;
    private Runnable runnable;

    private void initView(){
        ed_username = findViewById(R.id.ed_username);
        ed_password = findViewById(R.id.ed_password);
        input_layout_UserName = findViewById(R.id.input_layout_UserName);
        input_layout_password = findViewById(R.id.input_layout_password);
        FirebaseApp.initializeApp(this);
        //ed_password.addTextChangedListener(new MyTextWatcher(ed_password));
    }

    protected void setStatusBar() {
        StatusBarUtil.setTranslucent(this, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sp = new SharedPreferanceUtils(this);
        session = Mualab.getInstance().getSessionManager();
        initView();


      /*  if ((Boolean) sp.getParam(Constants.isLoginReminder, Boolean.FALSE)) {
            isRemind = true;
            ed_username.setText(String.valueOf(sp.getParam(Constants.USER_ID, "")));
            ed_password.setText(String.valueOf(sp.getParam(Constants.USER_PASSWORD, "")));
        }*/



        findViewById(R.id.tvCustomerApp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String appPackageName = "com.mualab.org.user";//getPackageName(); // getPackageName() from Context or Activity object
                if(isPackageExisted(appPackageName)){
                  /*  Intent intent = new Intent();
                    intent.setComponent(new ComponentName(appPackageName, appPackageName+".modules.SplashActivity"));
                    startActivity(intent);*/
                }else {

                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        showToast(getString(R.string.under_development));
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                }
            }
        });


        findViewById(R.id.tvForgotPassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(LoginActivity.this,ForgotPasswordActivity.class));
               /* new ForgotPassword(LoginActivity.this, new ForgotPassword.Listner() {
                    @Override
                    public void onSubmitClick(final Dialog dialog, final String string) {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                apiForForgotPass(string);
                            }
                        }, 1000);
                    }

                    @Override
                    public void onDismis(Dialog dialog) {
                        dialog.dismiss();
                    }
                }).show();*/
            }
        });


        final AppCompatButton btn_login =  findViewById(R.id.btn_login);
        //SetFont.setfontRagular(btn_login, this);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardUtil.hideKeyboard(btn_login, LoginActivity.this);
                loginProcess();
            }
        });



        findViewById(R.id.createNewAccount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                geNextActivity(Constants.INDEPENDENT);
                //  startActivity(new Intent(LoginActivity.this,  ChooseUserTypeActivity.class));
                // overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });


        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        switch (resultCode) {
            case 0:
                //mAuth = FirebaseAuth.getInstance();
                //mDatabase = FirebaseDatabase.getInstance().getReference();
                break;
            case 2:
                Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, resultCode, 0);
                if (dialog != null)
                    dialog.show();
        }
    }

    private void geNextActivity(String registrationType){
        Intent intent = new Intent(this,RegistrationActivity.class);
        intent.putExtra(Constants.registrationType, registrationType);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        startActivity(intent);
        finish();
    }

    public boolean isPackageExisted(String targetPackage){
        PackageManager pm = getPackageManager();
        try {
            PackageInfo info=pm.getPackageInfo(targetPackage,PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }

    private void loginProcess(){
        boolean isValidInput = true;
        String username = ed_username.getText().toString().trim();
        String password = ed_password.getText().toString().trim();
        String deviceToken = FirebaseInstanceId.getInstance().getToken();//"androidTest"

        if (!validateName() || !validatePassword()) {
            isValidInput = false;
        }

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(LoginActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if(isConnected){
                        dialog.dismiss();
                        loginProcess();
                    }
                }
            }).show();

            isValidInput = false;
        }

        if (isValidInput) {
            final Map<String, String> params = new HashMap<>();
            params.put("userName", username.toLowerCase());
            params.put("password", password);
            params.put("deviceToken",deviceToken);
            params.put("firebaseToken",deviceToken);
            params.put("userType", "artist");
            params.put("deviceType", "2");
            params.put("appType", "biz");

            new HttpTask(new HttpTask.Builder(this, "userLogin", new HttpResponceListner.Listener() {
                @Override
                public void onResponse(String response, String apiName) {
                    try {
                        JSONObject js = new JSONObject(response);
                        String status = js.getString("status");
                        String message = js.getString("message");
                        if (status.equalsIgnoreCase("success")) {
                            Gson gson = new Gson();
                            JSONObject userObj = js.getJSONObject("users");
                            User user = gson.fromJson(String.valueOf(userObj), User.class);
                            session.createSession(user);
                            session.setPassword(user.password);
                            checkUserRember(user);

                            if(user.isProfileComplete==3)
                                session.setBusinessProfileComplete(true);
                            MyToast.getInstance(LoginActivity.this).showDasuAlert("Success",message);
                            writeNewUser(user);
                            new ClearTask().execute("");

                        }else {
                            showToast(message);
                            //showDialog("Alert", message);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        showToast(getString(R.string.msg_some_thing_went_wrong));
                    }
                }

                @Override
                public void ErrorListener(VolleyError error) {
                    Helper helper = new Helper();
                    if (helper.error_Messages(error).contains("Session")) {
                        Mualab.getInstance().getSessionManager().logout();
                        //      MyToast.getInstance(BookingActivity.this).showDasuAlert(helper.error_Messages(error));
                    }
                }})
                    .setBody(params, HttpTask.ContentType.APPLICATION_JSON)
                    .setMethod(Request.Method.POST)
                    .setProgress(true))
                    .execute(this.getClass().getName());
            //Progress.show(this);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class ClearTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            Mualab.getInstance().getDB().clearAllTables();
            return null;
        }

        @Override
        protected void onPostExecute(String result) { }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) { }
    }

    private void checkUserRember(User user){
        if (isRemind) {
            sp.setParam(Constants.isLoginReminder, true);
            sp.setParam(Constants.USER_ID, user.userName);
            sp.setParam(Constants.USER_PASSWORD, ed_password.getText().toString());
        } else {
            sp.setParam(Constants.isLoginReminder, false);
            sp.setParam(Constants.USER_ID, "");
            sp.setParam( Constants.USER_PASSWORD, "");
        }
    }

    private boolean validateName() {
        if (ed_username.getText().toString().trim().isEmpty()) {
            showToast(getString(R.string.error_email_or_username_required));
            //input_layout_UserName.setError(getString(R.string.error_email_or_username_required));
            ed_username.requestFocus();
            return false;
        } else {
            input_layout_UserName.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        String password = ed_password.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            showToast(getString(R.string.error_password_required));
            // input_layout_password.setError(getString(R.string.error_password_required));
            ed_password.requestFocus();
            return false;
        } else if (password.length() < 6) {
            showToast(getString(R.string.error_invalid_password_length));
            //input_layout_password.setError(getString(R.string.error_invalid_password_length));
            ed_password.requestFocus();
            return false;
        } else {
            input_layout_password.setErrorEnabled(false);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Handler handler = new Handler();
        if (!doubleBackToExitPressedOnce) {
            doubleBackToExitPressedOnce = true;
            MySnackBar.showSnackbar(this, findViewById(R.id.snackBarView), "Click again to exit");
            handler.postDelayed(runnable = new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);

        } else {
            handler.removeCallbacks(runnable);
            super.onBackPressed();
        }
    }

    private void showToast(String msg) {
        if (!TextUtils.isEmpty(msg)){
            MyToast.getInstance(this).showDasuAlert(msg);
        }
    }

    private void apiForForgotPass(final String email){
        Session session = Mualab.getInstance().getSessionManager();
        User user = session.getUser();
        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(LoginActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if(isConnected){
                        dialog.dismiss();
                        apiForForgotPass(email);
                    }
                }
            }).show();
        }

        Map<String, String> params = new HashMap<>();
        params.put("email", email);

        HttpTask task = new HttpTask(new HttpTask.Builder(LoginActivity.this, "forgotPassword", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");

                    MyToast.getInstance(LoginActivity.this).showDasuAlert(message);

                } catch (Exception e) {
                    Progress.hide(LoginActivity.this);
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
                .setProgress(true)
                .setBody(params, HttpTask.ContentType.APPLICATION_JSON));
        //.setBody(params, "application/x-www-form-urlencoded"));

        task.execute(this.getClass().getName());
    }

    private void writeNewUser(User user) {
        DatabaseReference mDatabase  = FirebaseDatabase.getInstance().getReference();
        FirebaseUser firebaseUser = new FirebaseUser();
        firebaseUser.firebaseToken = FirebaseInstanceId.getInstance().getToken();;
        firebaseUser.isOnline = 1;
        firebaseUser.lastActivity = ServerValue.TIMESTAMP;
        firebaseUser.profilePic = user.profileImage;
        firebaseUser.userName = user.userName;
        firebaseUser.uId = Integer.parseInt(user.id);
        mDatabase.child("users").child(String.valueOf(user.id)).setValue(firebaseUser);

        Intent intent = new Intent(getApplicationContext(), NewBaseActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        overridePendingTransition(0,0);
        startActivity(intent);
        //  overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

}
