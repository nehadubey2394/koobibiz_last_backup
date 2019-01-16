package com.mualab.org.biz.modules.business_setup.invitation;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.dialogs.NoConnectionDialog;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.model.company_management.CompanyDetail;
import com.mualab.org.biz.modules.business_setup.OtherBusinessWorkingHours.OperationHoursActivity;
import com.mualab.org.biz.modules.business_setup.company_services.CompanyServicesActivity;
import com.mualab.org.biz.modules.business_setup.invitation.adapter.BusinessTypeAdapter;
import com.mualab.org.biz.session.Session;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.ConnectionDetector;
import com.mualab.org.biz.util.Helper;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class InvitationDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private CompanyDetail companyDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation_detail);
        initView();
    }

    private void initView(){

        String s1 = "We invite you to join";
        String s2 = "as a staff member Accept the invitation and get start login in biz app with the same social credential";

        companyDetail = (CompanyDetail) getIntent().getSerializableExtra("companyDetail");

        //   invitationAdapter = new InvitationAdapter(InvitationDetailActivity.this, invitationList);
        ImageView ivHeaderBack = findViewById(R.id.ivHeaderBack);
        TextView tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        tvHeaderTitle.setText(getString(R.string.invitation));

        TextView tvBusinessName = findViewById(R.id.tvBusinessName);
        TextView tvAddress = findViewById(R.id.tvAddress);
        TextView tvMessage = findViewById(R.id.tvMessage);
        RelativeLayout rlServices = findViewById(R.id.rlServices);
        RelativeLayout rlWorkingHours = findViewById(R.id.rlWorkingHours);

        AppCompatButton btnReject = findViewById(R.id.btnReject);
        AppCompatButton btnAccept = findViewById(R.id.btnAccept);
        ImageView ivProfile = findViewById(R.id.ivProfile);

        RecyclerView rvBizType = findViewById(R.id.rvBizType);
        LinearLayoutManager layoutManager = new LinearLayoutManager(InvitationDetailActivity.this,
                LinearLayoutManager.HORIZONTAL, false);
        rvBizType.setLayoutManager(layoutManager);

        if (companyDetail !=null) {
            BusinessTypeAdapter adapter = new BusinessTypeAdapter(InvitationDetailActivity.this,
                    companyDetail.businessType);
            rvBizType.setAdapter(adapter);

            tvBusinessName.setText(companyDetail.userName);
            tvAddress.setText(companyDetail.address);

            String textToHighlight = "<b>" + ""+companyDetail.businessName + "</b> ";

            // Construct the formatted text
            String replacedWith = "<font color='black'>" + textToHighlight + "</font>";

            // Update the TextView text
            tvMessage.setText(Html.fromHtml(s1+" "+replacedWith+" "+s2));

            if (!companyDetail.profileImage.equals("")){
                Picasso.with(InvitationDetailActivity.this).load(companyDetail.profileImage).placeholder(R.drawable.defoult_user_img).
                        fit().into(ivProfile);
            }
        }


        ivHeaderBack.setOnClickListener(this);
        btnReject.setOnClickListener(this);
        btnAccept.setOnClickListener(this);
        rlServices.setOnClickListener(this);
        rlWorkingHours.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnAccept:
                apiForAcceptReject("accept");
                break;

            case R.id.btnReject:
                apiForAcceptReject("reject");
                break;

            case R.id.ivHeaderBack:
                finish();
                break;

            case R.id.rlServices:
                Intent intent3 = new Intent(InvitationDetailActivity.this, CompanyServicesActivity.class);
                intent3.putExtra("businessId", companyDetail.businessId);
                intent3.putExtra("staffId", companyDetail._id);
                startActivity(intent3);
                break;

            case R.id.rlWorkingHours:
                Intent intent = new Intent(InvitationDetailActivity.this, OperationHoursActivity.class);
                Bundle args = new Bundle();
                args.putSerializable("workingHours",(Serializable)companyDetail.businessDays);
                intent.putExtra("BUNDLE",args);
                startActivity(intent);
                break;
        }
    }

    private void apiForAcceptReject(final String type) {
        Session session = Mualab.getInstance().getSessionManager();
        final User user = session.getUser();

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(InvitationDetailActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if (isConnected) {
                        dialog.dismiss();
                        apiForAcceptReject(type);
                    }
                }
            }).show();
        }

        Map<String, String> body = new HashMap<>();
        body.put("type", type);
        body.put("id", companyDetail._id);

        new HttpTask(new HttpTask.Builder(this, "invitationUpdate", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                System.out.println("response = "+response);
                try {
                    /*{"status":"success","otp":5386,"users":null}*/
                    JSONObject object = new JSONObject(response);
                    String status = object.getString("status");
                    String   message = object.getString("message");

                    if (status.equalsIgnoreCase("success")) {

                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();

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
                .setAuthToken(user.authToken).setMethod(Request.Method.POST)
                .setProgress(true))
                .execute(this.getClass().getName());
    }

}
