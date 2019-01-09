package com.mualab.org.biz.modules.business_setup.certificate;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.image.cropper.CropImage;
import com.image.picker.ImagePicker;
import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.dialogs.NoConnectionDialog;
import com.mualab.org.biz.helper.Constants;
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.model.Certificate;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.session.Session;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.ConnectionDetector;
import com.mualab.org.biz.util.Helper;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddCertificateActivity extends AppCompatActivity implements View.OnClickListener {
    private Bitmap bitmap;
    private EditText etCertificateTitle,etDescr;
    private ImageView ivCertificate;
    private Certificate certificate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_certificate);
        initView();
    }

    private void initView(){

        if (getIntent()!=null) {
            certificate = (Certificate) getIntent().getSerializableExtra("certificate");
        }

        ImageView ivHeaderBack = findViewById(R.id.ivHeaderBack);
        ivCertificate = findViewById(R.id.ivCertificate);
        TextView tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        AppCompatButton btnContinue = findViewById(R.id.btnContinue);
        etCertificateTitle = findViewById(R.id.etCertificateTitle);
        etDescr = findViewById(R.id.etDescr);
        tvHeaderTitle.setText(getString(R.string.text_certificate));

        if (certificate!=null){
            Picasso.with(AddCertificateActivity.this).load(certificate.certificateImage).
                    placeholder(R.drawable.ic_gallery_placeholder).fit().into(ivCertificate);

            etCertificateTitle.setText(certificate.title);
            etDescr.setText(certificate.description);
        }

        ivCertificate.setOnClickListener(this);
        ivHeaderBack.setOnClickListener(this);
        btnContinue.setOnClickListener(this);

    }

    private  void uploadCertificateIntoServer(final String title,final  String description){

        Session session = Mualab.getInstance().getSessionManager();
        User user = session.getUser();

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(AddCertificateActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if(isConnected){
                        dialog.dismiss();
                        uploadCertificateIntoServer(title,description);
                    }
                }
            }).show();
        }

        Map<String, String> params = new HashMap<>();
        params.put("title", title);
        params.put("description", description);

        HttpTask task = new HttpTask(new HttpTask.Builder(this, "addArtistCertificate", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");

                    if (status.equalsIgnoreCase("success")) {

                        JSONObject jsonObject = js.getJSONObject("certificate");
                        Gson gson = new Gson();
                        Certificate item = gson.fromJson(String.valueOf(jsonObject), Certificate.class);
                        item.status = jsonObject.getInt("status");
                        item._id = jsonObject.getInt("_id");

                        Intent intent = new Intent();
                        intent.putExtra("certificates", item);
                        setResult(RESULT_OK, intent);
                        finish();

                    }else {
                        MyToast.getInstance(AddCertificateActivity.this).showDasuAlert(message);
                    }
                }catch (Exception e){
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
            }
        }).setParam(params).setAuthToken(user.authToken)
                .setProgress(true));
        task.postImage("certificateImage", bitmap);
    }

    private  void apiForEditCertificate(final String title,final  String description){

        Session session = Mualab.getInstance().getSessionManager();
        User user = session.getUser();

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(AddCertificateActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if(isConnected){
                        dialog.dismiss();
                        uploadCertificateIntoServer(title,description);
                    }
                }
            }).show();
        }

        Map<String, String> params = new HashMap<>();
        params.put("title", title);
        params.put("description", description);
        params.put("id", String.valueOf(certificate._id));


        HttpTask task = new HttpTask(new HttpTask.Builder(this, "editArtistCertificate", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");

                    if (status.equalsIgnoreCase("success")) {

                       /* JSONObject jsonObject = js.getJSONObject("certificate");
                        Gson gson = new Gson();
                        Certificate item = gson.fromJson(String.valueOf(jsonObject), Certificate.class);
                        item.status = jsonObject.getInt("status");
                        item._id = jsonObject.getInt("_id");
*/
                        Intent intent = new Intent();
                        // intent.putExtra("certificates", item);
                        setResult(RESULT_OK, intent);
                        finish();

                        MyToast.getInstance(AddCertificateActivity.this).showDasuAlert(message);


                    }else {
                        MyToast.getInstance(AddCertificateActivity.this).showDasuAlert(message);
                    }
                }catch (Exception e){
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
            }
        }).setParam(params).setAuthToken(user.authToken)
                .setProgress(true));
        task.postImage("certificateImage", bitmap);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ivHeaderBack:
                onBackPressed();
                break;

            case R.id.ivCertificate:
                getPermissionAndPicImage();
                break;

            case R.id.btnContinue:
                String title = etCertificateTitle.getText().toString().trim();
                String descr = etDescr.getText().toString().trim();

                if (!title.isEmpty()) {
                    if (!descr.isEmpty()) {
                        if (certificate!=null) {
                            apiForEditCertificate(title,descr);

                        }else if (bitmap!=null){
                            uploadCertificateIntoServer(title,descr);
                        }else
                            MyToast.getInstance(AddCertificateActivity.this).showDasuAlert("Please select certificate");

                    }else
                        MyToast.getInstance(AddCertificateActivity.this).showDasuAlert("Please enter description");

                }else
                    MyToast.getInstance(AddCertificateActivity.this).showDasuAlert("Please enter title");

                break;


        }
    }

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == 234) {
             /*   Uri imageUri = ImagePicker.getImageURIFromResult(this, requestCode, resultCode, data);

                 if (imageUri != null) {
                    CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.RECTANGLE).setAspectRatio(450, 350).start(this);
                } else {
                    MyToast.getInstance(AddCertificateActivity.this).showDasuAlert(getString(R.string.msg_some_thing_went_wrong));

                }
*/
                bitmap = ImagePicker.getImageFromResult(AddCertificateActivity.this, requestCode, resultCode, data);
                //Uri imageUri = ImagePicker.getImageURIFromResult(mContext, requestCode, resultCode, data);
                if (bitmap != null) {
                    ivCertificate.setImageBitmap(bitmap);
                }else
                    MyToast.getInstance(AddCertificateActivity.this).showDasuAlert(getString(R.string.msg_some_thing_went_wrong));

            }else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                try {
                    if (result != null)
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result.getUri());

                    if (bitmap != null) {
                        ivCertificate.setImageBitmap(bitmap);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.MY_PERMISSIONS_REQUEST_CEMERA_OR_GALLERY: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getPermissionAndPicImage();
                } else  MyToast.getInstance(AddCertificateActivity.this).showDasuAlert("YOUR  PERMISSION DENIED");

            }
            break;
        }
    }

}
