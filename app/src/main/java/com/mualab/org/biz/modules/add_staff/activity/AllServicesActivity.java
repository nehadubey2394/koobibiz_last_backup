package com.mualab.org.biz.modules.add_staff.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.dialogs.NoConnectionDialog;
import com.mualab.org.biz.dialogs.Progress;
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.model.add_staff.SelectedServices;
import com.mualab.org.biz.model.add_staff.StaffDetail;
import com.mualab.org.biz.modules.add_staff.fragments.AllServiesFragment;
import com.mualab.org.biz.session.Session;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.ConnectionDetector;
import com.mualab.org.biz.util.Helper;
import com.mualab.org.biz.util.StatusBarUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.mualab.org.biz.modules.add_staff.fragments.ArtistLastServicesFragment.localMap;

public class AllServicesActivity extends AppCompatActivity {
    public  StaffDetail staffDetail;
    private ImageView ivHeaderBack;
    private TextView tvHeaderTitle;

    public void setBackButtonVisibility(int visibility){
        if(ivHeaderBack!=null)
            ivHeaderBack.setVisibility(visibility);
    }

    public void setTitle(String text){
        if(tvHeaderTitle!=null)
            tvHeaderTitle.setText(text);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_services);
        //    StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary));
        init();
    }

    private void init(){
        Intent intent = getIntent();
        if (intent!=null){
            Bundle args = intent.getBundleExtra("BUNDLE");
            staffDetail = (StaffDetail) args.getSerializable("staffDetail");
        }

        ivHeaderBack = findViewById(R.id.ivHeaderBack);
        tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        tvHeaderTitle.setText(getString(R.string.text_category));
        ivHeaderBack.setVisibility(View.VISIBLE);

        ivHeaderBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        addFragment(AllServiesFragment.newInstance(""), false,R.id.flServiceContainer);
    }

    public StaffDetail getStaffDetail(){
        return staffDetail;
    }

    /* frangment replace code */
    public void addFragment(Fragment fragment, boolean addToBackStack, int containerId) {
        String backStackName = fragment.getClass().getName();
        FragmentManager fragmentManager = getSupportFragmentManager();
        boolean fragmentPopped = fragmentManager.popBackStackImmediate(backStackName, 0);
        if (!fragmentPopped) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_in,0,0);
            transaction.add(containerId, fragment, backStackName);
            if (addToBackStack)
                transaction.addToBackStack(backStackName);
            transaction.commit();
        }

    }

    private void showAlertDailog(final FragmentManager fm,final int fCont){
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(AllServicesActivity.this, R.style.MyDialogTheme);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Alert!");
        alertDialog.setMessage("Are you sure want to discard changes?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                //selected.clear();
                localMap.clear();
                dialog.cancel();
                finish();
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();

    }

    private void showDailogForAddService(){
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(AllServicesActivity.this, R.style.MyDialogTheme);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Alert!");
        alertDialog.setMessage("You have to keep at least one staff service");
        alertDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                if (localMap.size()!=0){
                    String[] sIds = new String[localMap.size()];
                    int i = 0;
                    for(Map.Entry<String, SelectedServices> entry : localMap.entrySet()){
                        SelectedServices  services = entry.getValue();
                        sIds[i] = services.artistServiceId;
                        i++;
                    }
                    Collection<SelectedServices> values = localMap.values();
                    ArrayList<SelectedServices> listOfValues = new ArrayList<>(values);

                    Gson gson = new GsonBuilder().disableHtmlEscaping().create();
                    String jsonString = gson.toJson(listOfValues);

                    if (!jsonString.isEmpty()) {
                        apiForAddServices(dialog,jsonString,sIds);
                    }
                }else {
                    MyToast.getInstance(AllServicesActivity.this).showDasuAlert("Please select staff service");
                }
            }
        });

        alertDialog.show();

    }

    private void apiForAddServices(final DialogInterface dialog,final String jsonArray,final String[] sIds){
        Session session = Mualab.getInstance().getSessionManager();
        User user = session.getUser();
        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(AllServicesActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if(isConnected){
                        dialog.dismiss();
                        apiForAddServices(dialog,jsonArray,sIds);
                    }
                }
            }).show();
        }

        Map<String, String> params = new HashMap<>();
        params.put("artistId", staffDetail.staffId);
        params.put("businessId", String.valueOf(user.id));
        params.put("staffService", jsonArray);

        HttpTask task = new HttpTask(new HttpTask.Builder(AllServicesActivity.this, "addStaffService", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");

                    if (status.equalsIgnoreCase("success")) {

                        Intent intent = new Intent();
                        intent.putExtra("jsonArray", sIds);
                        //  selectedServicesList.clear();
                        dialog.cancel();
                        setResult(RESULT_OK, intent);
                        finish();

                    }else {
                        if (!message.equals("ArtistCategory already added"))
                            MyToast.getInstance(AllServicesActivity.this).showDasuAlert(message);
                    }
                } catch (Exception e) {
                    Progress.hide(AllServicesActivity.this);
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
                .setProgress(true)
                .setBody(params, HttpTask.ContentType.APPLICATION_JSON));
        //.setBody(params, "application/x-www-form-urlencoded"));

        task.execute(this.getClass().getName());
    }

    @Override
    public void onBackPressed() {
        // Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.flStffContainer);

        FragmentManager fm = getSupportFragmentManager();
        int i = fm.getBackStackEntryCount();
        if (staffDetail.staffServices.size()==0 && localMap.size()>0){
            showDailogForAddService();
        }
        else if (i>0 && localMap.size()>0){
            showAlertDailog(fm,i);
        }else if (i==0 && localMap.size()>0){
            showAlertDailog(fm,i);
        }
        else if (i > 0) {
            fm.popBackStack();
        }
        else  {
            finish();
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        //  localMap.clear();
        super.onDestroy();
    }
}
