package com.mualab.org.biz.activity.authentication;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ViewSwitcher;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.image.picker.ImagePicker;
import com.image.cropper.CropImage;
import com.image.cropper.CropImageView;
import com.mualab.org.biz.R;
import com.mualab.org.biz.activity.MainActivity;
import com.mualab.org.biz.dialogs.Progress;
import com.mualab.org.biz.helper.Constants;
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.model.Address;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.session.Session;
import com.mualab.org.biz.session.SharedPreferanceUtils;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.task.WebServiceAPI;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class Registration2Activity extends AppCompatActivity implements View.OnClickListener{

    public static String TAG = Registration2Activity.class.getName();
    private ViewSwitcher viewSwitcher;
    private View progressView3, progressView4;
    //Reg_View1
    private CircleImageView profile_image;
    private TextInputLayout input_layout_firstName, input_layout_lastName, input_layout_businessName, input_layout_userName;
    private EditText ed_firstName, ed_lastName, ed_businessName, ed_userName;

    //Reg_View2
    private TextInputLayout input_layout_pwd, input_layout_cnfPwd;
    private EditText edPwd, edConfirmPwd;

    private int CURRENT_VIEW_STATE = 3;
    private User user;
    private Address address;
    private Bitmap profileImageBitmap;

    private Session session;
    private boolean isRemind = true;
    //private WebServiceAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration2);
        session = new Session(this);
        initViews();

        Intent intent = getIntent();
        if(intent.getExtras()!=null){
            user = (User) intent.getSerializableExtra(Constants.USER);
            address = (Address) intent.getSerializableExtra(Constants.ADDRESS);
        }

        /*api = new WebServiceAPI(this, TAG, new HttpResponceListner.LoginRegistrationListener() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");
                    if (status.equalsIgnoreCase("success")) {
                        Progress.hide(Registration2Activity.this);
                        Gson gson = new Gson();
                        JSONObject userObj = js.getJSONObject("users");
                        User user = gson.fromJson(String.valueOf(userObj), User.class);
                        session.createSession(user);
                        session.setPassword(user.password);

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("user", user);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                    }else {
                        showToast(message);
                        findViewById(R.id.btnContinue2).setEnabled(true);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    findViewById(R.id.btnContinue2).setEnabled(true);
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                Log.d("ERROR:", "");
                findViewById(R.id.btnContinue2).setEnabled(true);
            }
        });*/
    }

    private void initViews(){
        viewSwitcher = findViewById(R.id.viewSwitcher);
        progressView3 = findViewById(R.id.progressView3);
        progressView4 = findViewById(R.id.progressView4);
        progressView3.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimary));

        /* view 1 */
        profile_image = findViewById(R.id.profile_image);
        profile_image.setOnClickListener(this);
        input_layout_firstName = findViewById(R.id.input_layout_firstName);
        input_layout_lastName = findViewById(R.id.input_layout_lastName);
        input_layout_businessName = findViewById(R.id.input_layout_businessName);
        input_layout_userName = findViewById(R.id.input_layout_userName);
        ed_firstName = findViewById(R.id.ed_firstName);
        ed_lastName = findViewById(R.id.ed_lastName);
        ed_businessName = findViewById(R.id.ed_businessName);
        ed_userName = findViewById(R.id.ed_userName);
        findViewById(R.id.btnContinue1).setOnClickListener(this);

        /* view 1 */
        input_layout_pwd = findViewById(R.id.input_layout_pwd);
        input_layout_cnfPwd = findViewById(R.id.input_layout_cnfPwd);
        edPwd = findViewById(R.id.edPwd);
        edConfirmPwd = findViewById(R.id.edConfirmPwd);
        findViewById(R.id.btnContinue2).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btnContinue1:
              /*  if(profileImageBitmap==null){
                    showToast(getString(R.string.error_profile_image));
                }else */
              if(checkNotempty(ed_firstName, input_layout_firstName)
                        && checkNotempty(ed_lastName, input_layout_lastName)
                        && validInputField(ed_businessName , input_layout_businessName, R.string.error_businessname_length)
                        && validUserName(ed_userName, input_layout_userName)){

                    user.firstName = ed_firstName.getText().toString().trim();
                    user.lastName = ed_lastName.getText().toString().trim();
                    user.fullName = user.firstName.concat(" ").concat(user.lastName);
                    user.userName = ed_userName.getText().toString().trim();
                    user.businessName = ed_businessName.getText().toString().trim();

                    final Map<String, String> body = new HashMap<>();
                    body.put("userName", user.userName);
                    new HttpTask(new HttpTask.Builder(this, "checkUser", new HttpResponceListner.Listener() {
                        @Override
                        public void onResponse(String response, String apiName) {
                            try {
                                JSONObject js = new JSONObject(response);
                                String status = js.getString("status");
                                String message = js.getString("message");
                                if (status.equalsIgnoreCase("success")) {
                                    nextScreen();
                                }else {
                                    showToast(getString(R.string.error_user_name_exist));
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void ErrorListener(VolleyError error) {

                        }}) .setBody(body, HttpTask.ContentType.APPLICATION_JSON)
                            .setMethod(Request.Method.POST)
                            .setProgress(true))
                            .execute(Registration2Activity.this.getClass().getName());

                }
                break;


            case R.id.btnContinue2:


                if(isValidPassword(edPwd, input_layout_pwd)){

                    if(isValidPassword(edConfirmPwd, input_layout_cnfPwd)){


                        if(matchPassword()){

                            findViewById(R.id.btnContinue2).setEnabled(false);
                            user.password = edPwd.getText().toString().trim();
                            nextScreen();
                        }
                    }
                }

                /*boolean password1 = isValidPassword(edPwd, input_layout_pwd);
                boolean password2 = isValidPassword(edConfirmPwd, input_layout_cnfPwd);
                boolean match = matchPassword();
                if(password1 && password2 && match){
                    findViewById(R.id.btnContinue2).setEnabled(false);
                    user.password = edPwd.getText().toString().trim();
                   // nextScreen();
                }*/
                break;

            case R.id.profile_image:
                getPermissionAndPicImage();
                break;
        }
    }


    private void nextScreen(){
        progressView3.setBackgroundColor(ContextCompat.getColor(this,R.color.white));
        progressView4.setBackgroundColor(ContextCompat.getColor(this,R.color.white));

        switch (CURRENT_VIEW_STATE){
            case 3:
                CURRENT_VIEW_STATE = 4;
                viewSwitcher.showNext();
                progressView4.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimary));
                break;

            case 4:
                String deviceToken = "android test token";//FirebaseInstanceId.getInstance().getToken();
                Map<String, String> params = new HashMap<>();
                params.put("userName", user.userName);
                params.put("firstName", user.firstName);
                params.put("lastName", user.lastName);
                params.put("email", user.email);
                params.put("password", user.password);
                params.put("countryCode", user.countryCode);
                params.put("contactNo", user.contactNo);
                params.put("businessName", user.businessName);

                params.put("gender", user.gender);
                params.put("dob", user.dob);
                params.put("address", address.stAddress1);
                params.put("address2", address.stAddress2);
                params.put("city", address.city);
                params.put("state", address.state);
                params.put("country", address.country);
                params.put("buildingNo", address.houseNumber);
                params.put("businessPostCode", address.postalCode);
                params.put("latitude", address.latitude);
                params.put("longitude", address.longitude);
                params.put("userType", "artist");
                params.put("businessType", user.businessType);
                params.put("deviceType", "2");
                params.put("deviceToken", deviceToken);
                params.put("firebaseToken", deviceToken);
              //  params.put("socialId", user.socialId);
               // Progress.show(Registration2Activity.this);
               // api.signUpTask(params, profileImageBitmap);

                HttpTask task = new HttpTask(new HttpTask.Builder(this, "artistRegistration", new HttpResponceListner.Listener() {
                    @Override
                    public void onResponse(String response, String apiName) {
                        try {
                            JSONObject js = new JSONObject(response);
                            String status = js.getString("status");
                            String message = js.getString("message");
                            if (status.equalsIgnoreCase("success")) {
                                Progress.hide(Registration2Activity.this);
                                Gson gson = new Gson();
                                JSONObject userObj = js.getJSONObject("users");
                                User user = gson.fromJson(String.valueOf(userObj), User.class);
                                session.createSession(user);
                                session.setPassword(user.password);
                                checkUserRember(user);
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra("user", user);
                                startActivity(intent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                finish();
                            }else {
                                showToast(message);
                                findViewById(R.id.btnContinue2).setEnabled(true);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            findViewById(R.id.btnContinue2).setEnabled(true);
                        }
                    }

                    @Override
                    public void ErrorListener(VolleyError error) {
                        findViewById(R.id.btnContinue2).setEnabled(true);
                        Log.d("log", error.getLocalizedMessage());
                    }
                }).setParam(params)
                .setProgress(true));
                task.postImage("profileImage", profileImageBitmap);
                break;
        }

    }


    // check permission or Get image from camera or gallery
    public void getPermissionAndPicImage() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        Constants.MY_PERMISSIONS_REQUEST_CEMERA_OR_GALLERY);
            } else {
                ImagePicker.pickImage(this);
            }
        } else {
            ImagePicker.pickImage(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == 234) {
                //Bitmap bitmap = ImagePicker.getImageFromResult(this, requestCode, resultCode, data);
                Uri imageUri = ImagePicker.getImageURIFromResult(this, requestCode, resultCode, data);
                if (imageUri != null) {
                    CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.RECTANGLE).setAspectRatio(400, 400).start(this);
                } else {
                    showToast(getString(R.string.msg_some_thing_went_wrong));
                }

            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                try {
                    if (result != null)
                        profileImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result.getUri());

                    if (profileImageBitmap != null) {
                        profile_image.setImageBitmap(profileImageBitmap);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case Constants.MY_PERMISSIONS_REQUEST_CEMERA_OR_GALLERY: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ImagePicker.pickImage(Registration2Activity.this);
                } else showToast("YOUR  PERMISSION DENIED");
            }
            break;
        }
    }


   /* private boolean isValidPassword(EditText edPwd, TextInputLayout inputLayout) {
        //Pattern regex = Pattern.compile("[$&+,:;=\\\\?@#|/'<>.^*()%!-]");
        String password = edPwd.getText().toString().trim();

        if (TextUtils.isEmpty(password)) {
            inputLayout.setError(getString(R.string.error_password_required));
            edPwd.requestFocus();
            return false;
        } else if (password.length() < 8) {
            inputLayout.setError(getString(R.string.error_invalid_password_length));
            edPwd.requestFocus();
            return false;
        }else if (password.toLowerCase().equals(password)) {
            inputLayout.setError(getString(R.string.error_invalid_password_capital));
            edPwd.requestFocus();
            return false;
        }if(!password.matches(".*\\d+.*")){
            inputLayout.setError(getString(R.string.error_invalid_password));
            edPwd.requestFocus();
        } *//*else if (!regex.matcher(password).find()) {
            inputLayout.setError(getString(R.string.error_invalid_password_special_character));
            edPwd.requestFocus();
            return false;
        }*//*else {
            inputLayout.setErrorEnabled(false);
        }
        return true;
    }*/

    public boolean isValidPassword(EditText edPwd, TextInputLayout inputLayout) {
        Pattern PASSWORD_PATTERN_UPERCASE = Pattern.compile(".*[A-Z].*");
        Pattern PASSWORD_PATTERN_NUBER = Pattern.compile(".*\\d.*");

        String password = edPwd.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            inputLayout.setError(getString(R.string.error_password_required));
            edPwd.requestFocus();
            return false;
        }else if(!PASSWORD_PATTERN_UPERCASE.matcher(password).matches()){
            inputLayout.setError(getString(R.string.error_invalid_password));
            edPwd.requestFocus();
            return false;
        }else if(password.length()<8){
            inputLayout.setError(getString(R.string.error_invalid_password));
            edPwd.requestFocus();
            return false;
        }else {
            inputLayout.setErrorEnabled(false);
        }
        return true;
    }

    private boolean matchPassword(){
        if(!edPwd.getText().toString().equals(edConfirmPwd.getText().toString())){
            input_layout_cnfPwd.setError(getString(R.string.error_confirm_password_not_match));
            return false;
        }
        return true;
    }

    private boolean validUserName(EditText editText, TextInputLayout inputLayout) {
        String text = editText.getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            inputLayout.setError(getString(R.string.error_required_field));
            editText.requestFocus();
            return false;
        } else if (text.contains(" ")) {
            inputLayout.setError(getString(R.string.error_username_contain_space));
            editText.requestFocus();
            return false;
        } else if (text.length() < 4) {
            inputLayout.setError(getString(R.string.error_username_length));
            editText.requestFocus();
            return false;
        } else {
            inputLayout.setErrorEnabled(false);
        }

        return true;
    }


    private boolean validInputField(EditText editText, TextInputLayout inputLayout, int id) {
        String text = editText.getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            inputLayout.setError(getString(R.string.error_required_field));
            editText.requestFocus();
            return false;
        } else if (text.length() < 4) {
            inputLayout.setError(getString(id));
            editText.requestFocus();
            return false;
        } else {
            inputLayout.setErrorEnabled(false);
        }
        return true;
    }

    private boolean checkNotempty(EditText editText, TextInputLayout inputLayout) {
        String text = editText.getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            inputLayout.setError(getString(R.string.error_required_field));
            editText.requestFocus();
            return false;
        }else {
            inputLayout.setErrorEnabled(false);
        }
        return true;
    }

    private void showToast(String msg){
        if (!TextUtils.isEmpty(msg)){
            MyToast.getInstance(this).showSmallCustomToast(msg);
        }
    }

    @Override
    public void onBackPressed() {
        if (CURRENT_VIEW_STATE == 4) {
            CURRENT_VIEW_STATE = 3;
            viewSwitcher.showPrevious();
            edPwd.setText("");
            edConfirmPwd.setText("");
            input_layout_pwd.setError(null);
            input_layout_cnfPwd.setError(null);
            progressView4.setBackgroundColor(ContextCompat.getColor(this,R.color.white));
            progressView3.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimary));

        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            /*Handler handler = new Handler();
            if (!doubleBackToExitPressedOnce) {
                doubleBackToExitPressedOnce = true;
                MySnackBar.showSnackbar(this, findViewById(R.id.snackBarView), getString(R.string.click_again_to_exit));
                handler.postDelayed(runnable = new Runnable() {
                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);

            } else {
                handler.removeCallbacks(runnable);
                startActivity(new Intent(Registration2Activity.this, LoginActivity.class));
                //overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
                super.onBackPressed();
            }*/
        }
        //overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }


    private void checkUserRember(User user){
        SharedPreferanceUtils sp = new SharedPreferanceUtils(this);
        if (isRemind) {
            sp.setParam(Constants.isLoginReminder, true);
            sp.setParam(Constants.USER_ID, user.userName);
        }
    }
}
