package com.mualab.org.biz.modules.profile_setup.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.session.PreRegistrationSession;

public class BookingTypeActivity extends AppCompatActivity implements View.OnClickListener {
    private  TextView tvInCall,tvOutCall,tvBoth;
    private String bookingType = "";
    private PreRegistrationSession bpSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_type);
        init();
    }

    private void init(){
        bpSession  = Mualab.getInstance().getBusinessProfileSession();
        ImageView ivKoobiLogo = findViewById(R.id.ivKoobiLogo);
        ivKoobiLogo.setVisibility(View.GONE);
        ImageView iv_back = findViewById(R.id.iv_back);
        TextView tvHeaderText = findViewById(R.id.tvHeaderText);
        AppCompatButton btnContinue = findViewById(R.id.btnContinue);
        tvInCall = findViewById(R.id.tvInCall);
        tvOutCall = findViewById(R.id.tvOutCall);
        tvBoth = findViewById(R.id.tvBoth);
        tvHeaderText.setVisibility(View.VISIBLE);
        tvHeaderText.setText(getString(R.string.booking_type));

        int serviceType = bpSession.getServiceType();
        if (serviceType==1){
            bookingType = "1";
            tvInCall.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            tvInCall.setTextColor(getResources().getColor(R.color.white));

            tvBoth.setBackgroundColor(getResources().getColor(R.color.white));
            tvBoth.setTextColor(getResources().getColor(R.color.text_color));
            tvOutCall.setBackgroundColor(getResources().getColor(R.color.white));
            tvOutCall.setTextColor(getResources().getColor(R.color.text_color));
        }else if (serviceType==2){
            bookingType = "2";

            tvOutCall.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            tvOutCall.setTextColor(getResources().getColor(R.color.white));

            tvBoth.setBackgroundColor(getResources().getColor(R.color.white));
            tvBoth.setTextColor(getResources().getColor(R.color.text_color));
            tvInCall.setBackgroundColor(getResources().getColor(R.color.white));
            tvInCall.setTextColor(getResources().getColor(R.color.text_color));
        }else if (serviceType==3){
            bookingType = "3";
            tvBoth.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            tvBoth.setTextColor(getResources().getColor(R.color.white));

            tvInCall.setBackgroundColor(getResources().getColor(R.color.white));
            tvInCall.setTextColor(getResources().getColor(R.color.text_color));
            tvOutCall.setBackgroundColor(getResources().getColor(R.color.white));
            tvOutCall.setTextColor(getResources().getColor(R.color.text_color));

        }

        iv_back.setOnClickListener(this);
        tvInCall.setOnClickListener(this);
        tvOutCall.setOnClickListener(this);
        tvBoth.setOnClickListener(this);
        btnContinue.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;

            case R.id.tvInCall:
                bookingType = "1";

                tvInCall.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                tvInCall.setTextColor(getResources().getColor(R.color.white));

                tvBoth.setBackgroundColor(getResources().getColor(R.color.white));
                tvBoth.setTextColor(getResources().getColor(R.color.text_color));
                tvOutCall.setBackgroundColor(getResources().getColor(R.color.white));
                tvOutCall.setTextColor(getResources().getColor(R.color.text_color));

                break;

            case R.id.tvOutCall:
                bookingType = "2";

                tvOutCall.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                tvOutCall.setTextColor(getResources().getColor(R.color.white));

                tvBoth.setBackgroundColor(getResources().getColor(R.color.white));
                tvBoth.setTextColor(getResources().getColor(R.color.text_color));
                tvInCall.setBackgroundColor(getResources().getColor(R.color.white));
                tvInCall.setTextColor(getResources().getColor(R.color.text_color));

                break;

            case R.id.tvBoth:
                bookingType = "3";
                tvBoth.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                tvBoth.setTextColor(getResources().getColor(R.color.white));

                tvInCall.setBackgroundColor(getResources().getColor(R.color.white));
                tvInCall.setTextColor(getResources().getColor(R.color.text_color));
                tvOutCall.setBackgroundColor(getResources().getColor(R.color.white));
                tvOutCall.setTextColor(getResources().getColor(R.color.text_color));

                break;

            case R.id.btnContinue:
                if (!bookingType.equals("")) {
                    bpSession.updateServiceType(Integer.parseInt(bookingType));
                    bpSession.updateIncallPreprationTime("HH:MM");
                    bpSession.updateOutcallPreprationTime("HH:MM");

                    Intent intent = new Intent();
                    intent.putExtra("bookingType", bookingType);
                    setResult(RESULT_OK, intent);
                    finish();
                }else
                    MyToast.getInstance(BookingTypeActivity.this).showDasuAlert("Please select booking type");
                break;
        }
    }

/*
    private void updateDtataIntoServer(){
        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(BookingTypeActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if(isConnected){
                        dialog.dismiss();
                        updateDtataIntoServer();
                    }
                }
            }).show();
        }

        Map<String,String> body = new HashMap<>();
        body.put("radius", ""+miles);
        */
/*body.put("serviceType", ""+serviceType);
        body.put("inCallpreprationTime", incallPreprationTime);
        body.put("outCallpreprationTime", outcallPreprationTime);*//*


        new HttpTask(new HttpTask.Builder(BookingTypeActivity.this, "updateRange", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                Log.d("res:", response);
                Intent intent2 = new Intent();
                intent2.putExtra("address", bizAddress);
                setResult(RESULT_OK, intent2);
                finish();
            }

            @Override
            public void ErrorListener(VolleyError error) {
                // Log.d("res:", error.getLocalizedMessage());
            }})
                .setMethod(Request.Method.POST)
                .setParam(body)
                .setBodyContentType( HttpTask.ContentType.APPLICATION_JSON)
                .setBody(body)
                .setAuthToken(user.authToken)).execute("updateRange");
    }
*/


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
