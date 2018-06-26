package com.mualab.org.biz.modules.profile.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.image.picker.ImagePicker;
import com.mualab.org.biz.R;
import com.mualab.org.biz.dialogs.NoConnectionDialog;
import com.mualab.org.biz.dialogs.Progress;
import com.mualab.org.biz.modules.profile.adapter.AdapterUploadCertificate;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.helper.Constants;
import com.mualab.org.biz.model.Certificate;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.session.PreRegistrationSession;
import com.mualab.org.biz.session.Session;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.ConnectionDetector;
import com.mualab.org.biz.util.Helper;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import views.dotsview.DotsView;

import static android.app.Activity.RESULT_OK;

public class UploadCertificationFragment extends ProfileCreationBaseFragment implements
        AdapterUploadCertificate.Listner, DiscreteScrollView.OnItemChangedListener<RecyclerView.ViewHolder>,
        View.OnClickListener{


    //private RecyclerView recyclerView;
    private AdapterUploadCertificate mAdapter;
    private List<Certificate> certificates;
    private DotsView dotsView;
    private EditText edBio;
    private AppCompatButton btnAddMore;
    private DiscreteScrollView item_picker;
    private PreRegistrationSession bpSession;

    public UploadCertificationFragment() {
        // Required empty public constructor
    }


    public static UploadCertificationFragment newInstance() {
        UploadCertificationFragment fragment = new UploadCertificationFragment();
        Bundle args = new Bundle();
        // args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bpSession = Mualab.getInstance().getBusinessProfileSession();
        certificates = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upload_certification, container, false);
        //recyclerView = view.findViewById(R.id.rvCertificate);
        dotsView = view.findViewById(R.id.dotsView);
        edBio = view.findViewById(R.id.edBio);
        item_picker = view.findViewById(R.id.item_picker);
        btnAddMore = view.findViewById(R.id.btnAddMore);
        //AppCompatButton btnNext = view.findViewById(R.id.btnNext);
        btnAddMore.setOnClickListener(this);
        view.findViewById(R.id.btnNext).setOnClickListener(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new AdapterUploadCertificate(mContext, certificates);
        mAdapter.setCallBack(this);
        mAdapter.setAuthtoken(user.authToken);
        // RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        //  recyclerView.setLayoutManager(mLayoutManager);
        /*DividerItemDecoration decoration = new DividerItemDecoration(mContext, LinearLayoutManager.HORIZONTAL);
        recyclerView.addItemDecoration(decoration);
        RecyclerView.LayoutManager horizontalLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(mAdapter);*/

        item_picker.setSlideOnFling(true);
        item_picker.setAdapter(mAdapter);
        item_picker.addOnItemChangedListener(this);
        edBio.setText(TextUtils.isEmpty(user.bio)?"":user.bio);
        updateView();
        //  getAllCertificate();
        apiForGetCertificates();
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){

            case R.id.btnAddMore:
                getPermissionAndPicImage();
                break;

            case R.id.btnNext:
                String bio = edBio.getText().toString().trim();
                if(!TextUtils.isEmpty(bio)){
                    user.bio = bio;
                    updateBioIntoServer();

                }else {
                    bpSession.updateRegStep(6);
                    listener.onNext();
                }

                break;
        }
    }

    private void updateView(){

        if(certificates.size()>0){
            Certificate tmp = certificates.get(0);
            if(tmp.id!=-1){
                //btnAddMore.setVisibility(certificates.get(0).id==-1?View.VISIBLE:View.GONE);
                //dotsView.setVisibility(certificates.get(0).id==-1?View.INVISIBLE:View.VISIBLE);
                btnAddMore.setVisibility(View.VISIBLE);
                dotsView.setVisibility(certificates.size()>1?View.VISIBLE:View.INVISIBLE);

            }else {
                btnAddMore.setVisibility(View.GONE);
                dotsView.setVisibility(View.INVISIBLE);
            }
        }else {
            btnAddMore.setVisibility(View.GONE);
            dotsView.setVisibility(View.INVISIBLE);
            certificates.add(new Certificate(-1));
        }

        mAdapter.notifyDataSetChanged();
        item_picker.scrollToPosition(certificates.size()-1);
        dotsView.setProgressViewCount(certificates.size());
        dotsView.setProgressIndex(certificates.size()-1);
    }

    // check permission or Get image from camera or gallery
    public void getPermissionAndPicImage() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (mContext.checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
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
    public void onClickPickImge() {
        getPermissionAndPicImage();
    }

    @Override
    public void onRemoveImage(int index) {
        mAdapter.notifyItemRemoved(index);
        updateView();
    }

    @Override
    public void onUpdateIndex(int index) {
        if(dotsView!=null)
            dotsView.setProgressIndex(index);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == 234) {
                Bitmap bitmap = ImagePicker.getImageFromResult(mContext, requestCode, resultCode, data);
                //Uri imageUri = ImagePicker.getImageURIFromResult(mContext, requestCode, resultCode, data);
                if (bitmap != null) {
                    uploadCertificateIntoServerDb(bitmap);

                } else {
                    showToast(getString(R.string.msg_some_thing_went_wrong));
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
                } else showToast("YOUR  PERMISSION DENIED");
            }
            break;
        }
    }

    private void apiForGetCertificates(){
        Session session = Mualab.getInstance().getSessionManager();
        User user = session.getUser();

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(mContext, new NoConnectionDialog.Listner() {
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

        HttpTask task = new HttpTask(new HttpTask.Builder(mContext, "getAllCertificate", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");
                    if (status.equalsIgnoreCase("success")) {
                        certificates.clear();
                        JSONArray jsonArray = js.getJSONArray("allCertificate");

                        for(int i=0; i<jsonArray.length(); i++){
                            JSONObject cObj = (JSONObject) jsonArray.get(i);
                            Certificate certificate = new Certificate();
                            certificate.id = cObj.getInt("_id");
                            certificate.imageUri = cObj.getString("certificateImage");
                            certificate.status = cObj.getInt("status");
                            certificates.add(certificate);
                        }

                        updateView();
                        mAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    Progress.hide(mContext);
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

/*
    private void getAllCertificate(){
        new HttpTask(new HttpTask.Builder(mContext, "getAllCertificate", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                Log.d("log", response);
                try {
                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");
                    if (status.equalsIgnoreCase("success")) {
                        certificates.clear();
                        JSONArray jsonArray = js.getJSONArray("allCertificate");

                        for(int i=0; i<jsonArray.length(); i++){
                            JSONObject cObj = (JSONObject) jsonArray.get(i);
                            Certificate certificate = new Certificate();
                            certificate.id = cObj.getInt("_id");
                            certificate.imageUri = cObj.getString("certificateImage");
                            certificate.status = cObj.getInt("status");
                            certificates.add(certificate);
                        }

                        updateView();
                        mAdapter.notifyDataSetChanged();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                //Log.d("log", error.getLocalizedMessage());
            }
        }).setAuthToken(user.authToken)).execute("getAllCertificate");
    }
*/

    private void uploadCertificateIntoServerDb(Bitmap bitmap){
        // Map<String, String> body = new HashMap<>();
        HttpTask task = new HttpTask(new HttpTask.Builder(mContext, "addArtistCertificate", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                Log.d("log", response);
                try {
                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");

                    if (status.equalsIgnoreCase("success")) {

                        if(certificates.size()==1 && certificates.get(0).id==-1)
                            certificates.remove(0);

                        JSONObject jsonObject = js.getJSONObject("certificate");
                        Certificate certificate = new Certificate();
                        certificate.id = jsonObject.getInt("_id");
                        certificate.imageUri = jsonObject.getString("certificateImage");
                        certificate.status = jsonObject.getInt("status");
                        certificates.add(certificate);
                        updateView();
                    }else {
                        showToast(message);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                Log.d("log", ""+error.getLocalizedMessage());
            }})
                .setAuthToken(user.authToken)
                .setProgress(true));

        task.postImage("certificateImage", bitmap);
    }

    @Override
    public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int adapterPosition) {
        onUpdateIndex(adapterPosition);
    }


    private void updateBioIntoServer(){
        if(ConnectionDetector.isConnected()){
            Map<String,String> body = new HashMap<>();
            body.put("bio", TextUtils.isEmpty(user.bio)?"":user.bio);

            new HttpTask(new HttpTask.Builder(mContext, "updateRecord", new HttpResponceListner.Listener() {
                @Override
                public void onResponse(String response, String apiName) {
                    Log.d("res:", response);
                    bpSession.updateRegStep(6);
                    User u = Mualab.getInstance().getSessionManager().getUser();
                    u.bio = user.bio;
                    //bpSession.updateAddress(address);
                    Mualab.getInstance().getSessionManager().createSession(user);
                    listener.onNext();

                }

                @Override
                public void ErrorListener(VolleyError error) {
                    // Log.d("res:", error.getLocalizedMessage());
                }})
                    .setMethod(Request.Method.POST)
                    .setBodyContentType( HttpTask.ContentType.APPLICATION_JSON)
                    .setBody(body)
                    .setProgress(true)
                    .setAuthToken(user.authToken)).execute("updateRecord");
        }else showToast(R.string.error_msg_network);
    }
}
