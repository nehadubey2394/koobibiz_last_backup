package com.mualab.org.biz.modules.authentication;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.broadcast.OnSmsCatchListener;
import com.mualab.org.biz.broadcast.SmsVerifyCatcher;
import com.mualab.org.biz.dialogs.NoConnectionDialog;
import com.mualab.org.biz.helper.Constants;
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.model.Address;
import com.mualab.org.biz.model.Country;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.session.SharedPreferanceUtils;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.ConnectionDetector;
import com.mualab.org.biz.util.Helper;
import com.mualab.org.biz.util.JsonUtils;
import com.mualab.org.biz.util.KeyboardUtil;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static com.mualab.org.biz.application.Mualab.IS_DEBUG_MODE;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener{

    private ViewSwitcher viewSwitcher;
    private View progressView1, progressView2;
    //Reg_View1
    private TextInputLayout input_layout_email, input_layout_phone;
    private TextView tvCountryCode, tvAddress, tvAddressHint;
    private EditText ed_email, edPhoneNumber;
    private EditText etOtp1, etOtp2,etOtp3,etOtp4;
    //Reg_View2
    private TextView tvOtp[] = new TextView[4];

    private int CURRENT_VIEW_STATE = 1;
    private ArrayList<Integer> inputList = new ArrayList<>();
    private List<Country> countries;
    private String countryCode;
    private String apiOTP="";
    private User user,tempUser;
    private Address address;
    private int otpSize;

    private SmsVerifyCatcher smsVerifyCatcher;
    private boolean isResendOTP;
    //private CountDownTimer countDownTimer;
    //private boolean timerIsRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        //if(ScreenUtils.hasSoftKeys(getWindowManager(), this)) findViewById(R.id.nevSoftBar).setVisibility(View.VISIBLE);
        initViews();
        countries = JsonUtils.loadCountries(this);
        getCountryZipCode();
        user = new User();

        Intent intent = getIntent();
        if(intent.getExtras()!=null){
            user.socialId = intent.getStringExtra(Constants.SOCIAL_ID);
            user.email = intent.getStringExtra(Constants.EMAIL_ID);
            user.businessType = intent.getStringExtra(Constants.registrationType);
        }

        smsVerifyCatcher = new SmsVerifyCatcher(this, new OnSmsCatchListener<String>() {
            @Override
            public void onSmsCatch(String message) {
                String code = parseCode(message);//Parse verification code

                if(CURRENT_VIEW_STATE==2){
                    if(message.contains("Mualab") || message.contains("mualab")){
                        inputList.clear();
                        user.otp = code;
                        for(int i=0; i<code.length(); i++){
                            tvOtp[i].setText(String.format("%s", code.charAt(i)));
                            inputList.add(Character.getNumericValue(code.charAt(i)));
                        }

                        if(apiOTP.equals(user.otp)){
                            user.otp = code;
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    nextScreen();
                                }
                            }, 700);
                        }
                    }
                }
            }
        });

        findViewById(R.id.llBusinessAddr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(RegistrationActivity.this, AddAddressActivity.class);
                if(address!=null)
                    intent.putExtra("address",address);
                startActivityForResult(intent,1001);
            }
        });
    }

    private void showToast(String msg){
        if (!TextUtils.isEmpty(msg)){
            MyToast.getInstance(this).showDasuAlert(msg);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.btnContinue1:

                if(validateEmail() && validatePhone() /*&& validateAddress()*/){

                    String contactNo = edPhoneNumber.getText().toString().trim();
                    String email = ed_email.getText().toString().trim();
                    KeyboardUtil.hideKeyboard(edPhoneNumber, this);

                    if(user.countryCode!=null && user.countryCode.equals(countryCode) &&  user.email.equals(email)
                            && user.contactNo.equals(contactNo)
                        /*&& countDownTimer!=null && timerIsRunning*/){
                        // nextScreen();
                        isResendOTP = false;
                        user.countryCode = countryCode;
                        user.contactNo = contactNo;
                        user.email = email;
                        resetOTP();
                        apiCallForDataVerify();

                    }else {
                        isResendOTP = false;
                        user.countryCode = countryCode;
                        user.contactNo = contactNo;
                        user.email = email;
                        resetOTP();
                        apiCallForDataVerify();
                    }
                }

                break;

            case R.id.btnVerifyOtp:
                String userOtp = "";
                /*for (Integer s : inputList) userOtp += s;
                if(inputList.size()!=4) showToast(getString(R.string.error_otp_reuired));
                else if(apiOTP.equals(userOtp)) {
                    user.otp = userOtp;
                    user.otpVerified = true;
                    nextScreen();
                } else {
                    resetOTP();
                    showToast(getString(R.string.error_otp_invalid));
                }*/

                String otp1 = etOtp1.getText().toString().trim();
                String otp2 = etOtp2.getText().toString().trim();
                String otp3 = etOtp3.getText().toString().trim();
                String otp4 = etOtp4.getText().toString().trim();

                if (!otp1.isEmpty() && !otp2.isEmpty() && !otp3.isEmpty() && !otp4.isEmpty()){
                    userOtp = otp1+""+otp2+""+otp3+""+otp4;
                    if(apiOTP.equals(userOtp)) {
                        user.otp = userOtp;
                        user.otpVerified = true;
                        if (tempUser!=null){
                            Intent intent = new Intent(RegistrationActivity.this,MergeAccountActivity.class);
                            intent.putExtra("userData",tempUser);
                            startActivity(intent);
                        }else {
                            nextScreen();
                        }
                    } else {
                        resetOTP();
                        showToast(getString(R.string.error_otp_invalid));
                    }

                }else
                    showToast(getString(R.string.error_otp_reuired));

                break;

            case R.id.tv_resend_otp:
                isResendOTP = true;
                //timerIsRunning = false;
                resetOTP();
                apiCallForDataVerify();
                break;

            case R.id.tv_0:
                inputOtp(0);
                break;

            case R.id.tv_1:
                inputOtp(1);
                break;

            case R.id.tv_2:
                inputOtp(2);
                break;

            case R.id.tv_3:
                inputOtp(3);
                break;

            case R.id.tv_4:
                inputOtp(4);
                break;

            case R.id.tv_5:
                inputOtp(5);
                break;

            case R.id.tv_6:
                inputOtp(6);
                break;

            case R.id.tv_7:
                inputOtp(7);
                break;

            case R.id.tv_8:
                inputOtp(8);
                break;

            case R.id.tv_9:
                inputOtp(9);
                break;

            case R.id.ivBackKeyboard:
                inputOtp(-1);
                break;

            case R.id.tvCountryCode:
                startActivityForResult(new Intent(RegistrationActivity.this, ChooseCountryActivity.class), 1003);
                //SelectCountry();
                break;
        }
    }

    private boolean validateEmail() {
        String email = ed_email.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            showToast(getString(R.string.error_email_required));
            //input_layout_email.setError(getString(R.string.error_email_required));
            ed_email.requestFocus();
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast(getString(R.string.error_invalid_email));
            //input_layout_email.setError(getString(R.string.error_invalid_email));
            ed_email.requestFocus();
            return false;
        } else {
            input_layout_email.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePhone() {
        String phone = edPhoneNumber.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            showToast(getString(R.string.error_phone_no_reuired));
            //input_layout_phone.setError(getString(R.string.error_phone_no_reuired));
            edPhoneNumber.requestFocus();
            return false;
        } else if (phone.length() < 4 || phone.length()>15) {
            showToast(getString(R.string.error_phone_no_length));
            //input_layout_phone.setError(getString(R.string.error_phone_no_length));
            edPhoneNumber.requestFocus();
            return false;
        } else {
            input_layout_phone.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateAddress() {
        // String phone = tvAddress.getText().toString().trim();
        if (address==null) {
            tvAddress.setText(String.format("%s*", getString(R.string.address_activity)));
            showToast(getString(R.string.error_address_required));
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch(requestCode) {
                case 1003: {
                    Country country = (Country) data.getSerializableExtra("country");
                    tvCountryCode.setText(String.format("+%s", country.phone_code));
                    countryCode = "+"+country.phone_code;
                    break;
                }

                case 1001: {
                    address = (Address) data.getSerializableExtra("address");
                    if(address!=null){
                        tvAddressHint.setVisibility(View.VISIBLE);
                        tvAddress.setText(String.format("%s",
                                TextUtils.isEmpty(address.placeName)?address.stAddress1:address.placeName));
                    }
                    break;
                }
            }
        }

    }

    private void inputOtp(int otpKey){
        int size = inputList.size();

        if(otpKey==-1 && size>0){

            switch (size){
                case 1:
                    tvOtp[0].setText("*");
                    break;
                case 2:
                    tvOtp[1].setText("*");
                    break;
                case 3:
                    tvOtp[2].setText("*");
                    break;
                case 4:
                    tvOtp[3].setText("*");
                    break;
            }inputList.remove(size-1);

        }else if(otpKey!=-1 && !(size>3)){
            inputList.add(otpKey);
            switch (size){
                case 0:
                    tvOtp[0].setText(String.valueOf(otpKey));
                    break;
                case 1:
                    tvOtp[1].setText(String.valueOf(otpKey));
                    break;
                case 2:
                    tvOtp[2].setText(String.valueOf(otpKey));
                    break;
                case 3:
                    tvOtp[3].setText(String.valueOf(otpKey));
                    break;
            }
        }
    }

    private void resetOTP(){
        inputList.clear();
        for (TextView aTvOtp : tvOtp) {
            aTvOtp.setText("*");
        }
    }

    private void initViews(){
        viewSwitcher = findViewById(R.id.viewSwitcher);
        progressView1 = findViewById(R.id.progressView1);
        progressView2 = findViewById(R.id.progressView2);
        progressView1.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimary2));

        /* view 1 */
        input_layout_email = findViewById(R.id.input_layout_email);
        input_layout_phone = findViewById(R.id.input_layout_phone);
        tvAddressHint = findViewById(R.id.tvAddressHint);
        tvAddress = findViewById(R.id.tvAddress);
        tvCountryCode = findViewById(R.id.tvCountryCode);
        ed_email = findViewById(R.id.ed_email);
        edPhoneNumber = findViewById(R.id.edPhoneNumber);
        //Reg_View2
        tvOtp[0] = findViewById(R.id.tvOtp1);
        tvOtp[1] = findViewById(R.id.tvOtp2);
        tvOtp[2] = findViewById(R.id.tvOtp3);
        tvOtp[3] = findViewById(R.id.tvOtp4);


        findViewById(R.id.btnContinue1).setOnClickListener(this);
        findViewById(R.id.tvCountryCode).setOnClickListener(this);
        findViewById(R.id.tv_resend_otp).setOnClickListener(this);
        tvCountryCode.setOnClickListener(this);

        findViewById(R.id.btnVerifyOtp).setOnClickListener(this);
        findViewById(R.id.tv_0).setOnClickListener(this);
        findViewById(R.id.tv_1).setOnClickListener(this);
        findViewById(R.id.tv_2).setOnClickListener(this);
        findViewById(R.id.tv_3).setOnClickListener(this);
        findViewById(R.id.tv_4).setOnClickListener(this);
        findViewById(R.id.tv_5).setOnClickListener(this);
        findViewById(R.id.tv_6).setOnClickListener(this);
        findViewById(R.id.tv_7).setOnClickListener(this);
        findViewById(R.id.tv_8).setOnClickListener(this);
        findViewById(R.id.tv_9).setOnClickListener(this);
        findViewById(R.id.ivBackKeyboard).setOnClickListener(this);

        etOtp1 = findViewById(R.id.etOtp1);
        etOtp2 = findViewById(R.id.etOtp2);
        etOtp3 = findViewById(R.id.etOtp3);
        etOtp4 = findViewById(R.id.etOtp4);


        etOtp1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                otpSize++;
                if (editable.length() ==1) {
                    etOtp2.requestFocus();
                }
            }
        });

        etOtp2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                otpSize ++;
                if (editable.length() ==1) {
                    etOtp3.requestFocus();
                }else {
                    etOtp2.requestFocus();
                }
            }
        });

        etOtp3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                otpSize++;
                if (editable.length() ==1) {
                    etOtp4.requestFocus();
                }else {
                    etOtp2.requestFocus();
                }
            }
        });

        etOtp4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                otpSize++;
                if (editable.length() ==0)
                    etOtp3.requestFocus();
            }
        });

    }

    private void nextScreen(){
        resetProgressView();
        switch (CURRENT_VIEW_STATE){
            case 1:
                CURRENT_VIEW_STATE = 2;
                inputList.clear();
                for (TextView aTvOtp : tvOtp) aTvOtp.setText("*");
                viewSwitcher.showNext();
                progressView2.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimary2));
                break;

            case 2:
                progressView2.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimary2));
                startActivity(new Intent(RegistrationActivity.this, Registration2Activity.class)
                        .putExtra(Constants.USER, user)
                        .putExtra(Constants.ADDRESS, address));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
        }

    }

    private void resetProgressView(){
        progressView1.setBackgroundColor(ContextCompat.getColor(this,R.color.white));
        progressView2.setBackgroundColor(ContextCompat.getColor(this,R.color.white));
    }

    @Override
    public void onBackPressed() {
        resetProgressView();
        switch (CURRENT_VIEW_STATE){
            case 1:
                progressView1.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimary2));
                super.onBackPressed();
                break;

            case 2:
                CURRENT_VIEW_STATE = 1;
                etOtp1.setText("");
                etOtp2.setText("");
                etOtp3.setText("");
                etOtp4.setText("");
                progressView1.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimary2));
                viewSwitcher.showPrevious();
                break;
        }
    }

    /**
     * Parse verification code
     *
     * @param message sms message
     * @return only four numbers from massage string
     */
    private String parseCode(String message) {
        Pattern p = Pattern.compile("\\b\\d{4}\\b");
        Matcher m = p.matcher(message);
        String code = "";
        while (m.find()) {
            code = m.group(0);
        }
        return code;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(smsVerifyCatcher!=null) smsVerifyCatcher.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(smsVerifyCatcher!=null) smsVerifyCatcher.onStop();
    }

    //*******country code alert ***********
    public void getCountryZipCode() {
        String CountryID;
        //String CountryZipCode = "";
        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        assert manager != null;
        CountryID = manager.getSimCountryIso().toUpperCase();
        if (CountryID.equals("")) {
            tvCountryCode.setText("+1");
            countryCode = "+1";
        }else {
            for (int i = 0; i < countries.size(); i++) {
                Country country = countries.get(i);
                if (CountryID.equalsIgnoreCase(country.code)) {
                    // CountryZipCode = countries.get(i).phone_code;
                    countryCode = "+" + country.phone_code;
                    tvCountryCode.setText(String.format("+%s", country.phone_code));
                    break;
                }
            }
        }
        // return CountryZipCode + " " + CountryID;
    }

    private void apiCallForDataVerify() {

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(RegistrationActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if (isConnected) {
                        dialog.dismiss();
                        apiCallForDataVerify();
                    }
                }
            }).show();
        }

        Map<String, String> body = new HashMap<>();
        body.put("countryCode", user.countryCode);
        body.put("contactNo", user.contactNo);
        body.put("email", user.email);
        body.put("userType", "artist");
        body.put("socialId", TextUtils.isEmpty(user.socialId)?"":user.socialId);

        new HttpTask(new HttpTask.Builder(this, "phonVerification", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                System.out.println("response = "+response);
                parseApiResponce(response);
            }

            @Override
            public void ErrorListener(VolleyError error)
            {
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
                .setProgress(true))
                .execute(this.getClass().getName());
    }

    private void parseApiResponce(String response){
        String status = "";
        String message = "";
        String otp = "";
        try {
            /*{"status":"success","otp":5386,"users":null}*/
            JSONObject object = new JSONObject(response);
            status = object.getString("status");

            if (object.has("message"))
                message = object.getString("message");

            if (status.equalsIgnoreCase("success")) {
                otp = object.getString("otp");
                showToast("OTP is - " +otp);
                apiOTP = otp;
                if (object.has("message")){
                    message = object.getString("message");
                    if (message.equals("Email already exist by social app")){
                        Gson gson = new Gson();
                        JSONObject userObj = object.getJSONObject("users");
                        tempUser = gson.fromJson(String.valueOf(userObj), User.class);
                    }
                }

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("email", user.email);
                jsonObject.put("phone", user.contactNo);
                jsonObject.put("code", user.countryCode);
                jsonObject.put("otp", apiOTP);
                SharedPreferanceUtils.setParam(RegistrationActivity.this, "OTP", jsonObject.toString());

                if(!isResendOTP)
                    nextScreen();

            }else if (otp.equals("already exist")) {
                showToast("This number already registered");
            } else if (status.equalsIgnoreCase("fail")) {
                showToast(message);
            }else {
                showToast(message);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

   /*     if (status.equalsIgnoreCase("success")) {
            apiOTP = otp;
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("email", user.email);
                jsonObject.put("phone", user.contactNo);
                jsonObject.put("code", user.countryCode);
                jsonObject.put("otp", apiOTP);
                SharedPreferanceUtils.setParam(RegistrationActivity.this, "OTP", jsonObject.toString());

                if(!isResendOTP)
                    nextScreen();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (otp.equals("already exist")) {
            showToast("This number already registered");
        } else if (status.equalsIgnoreCase("fail")) {
            showToast(message);

        }*/
    }

    /*private void startTimear(){
        final AppCompatButton btnResendOtp = findViewById(R.id.tv_resend_otp);
        btnResendOtp.setEnabled(false);
        if(countDownTimer!=null)
            countDownTimer.cancel();
        timerIsRunning = true;
        countDownTimer = new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                btnResendOtp.setText("00:" + millisUntilFinished / 1000);
            }

            public void onFinish() {
                timerIsRunning = false;
                btnResendOtp.setText(R.string.resent_code);
                btnResendOtp.setEnabled(true);
            }
        }.start();
    }*/
}
