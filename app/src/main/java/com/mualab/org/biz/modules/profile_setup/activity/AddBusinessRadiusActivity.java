package com.mualab.org.biz.modules.profile_setup.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.dialogs.NoConnectionDialog;
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.model.Address;
import com.mualab.org.biz.model.BusinessProfile;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.session.PreRegistrationSession;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.ConnectionDetector;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.util.HashMap;
import java.util.Map;

import views.MyMapView;

public class AddBusinessRadiusActivity extends AppCompatActivity implements
        View.OnClickListener, OnMapReadyCallback {
    private  TextView tvAddress,tv_mileIndicater1,tvShowMiles,tvHeader;
    private String bookingType = "";
    private Address bizAddress;
    private DiscreteSeekBar seekBarRadius;
    private int miles = 5;
    private PreRegistrationSession bpSession;
    protected User user;
    private long mLastClickTime = 0;
    private MyMapView mapView;
    private GoogleMap map;
    private float mapWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_business_address);

        mapView = findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        init();
    }

    private void init(){
        if(user==null) user = Mualab.getInstance().getSessionManager().getUser(); // get session object
        bpSession = Mualab.getInstance().getBusinessProfileSession();
        ImageView ivKoobiLogo = findViewById(R.id.ivKoobiLogo);
        ivKoobiLogo.setVisibility(View.GONE);
        ImageView iv_back = findViewById(R.id.iv_back);
        TextView tvHeaderText = findViewById(R.id.tvHeaderText);
        tv_mileIndicater1 = findViewById(R.id.tv_mileIndicater1);
        tvShowMiles = findViewById(R.id.tvShowMiles);
        tvHeader = findViewById(R.id.tvHeader);
        AppCompatButton btnContinue = findViewById(R.id.btnContinue);
        tvHeaderText.setVisibility(View.VISIBLE);
        tvHeaderText.setText("Address");
        tvAddress = findViewById(R.id.tvAddress);
        seekBarRadius = findViewById(R.id.seekBarRadius);

        updateView();

        String commingFrom = getIntent().getStringExtra("commingFrom");
        LinearLayout ll_radious = findViewById(R.id.ll_radious);

        if (commingFrom.equals("areaOfCoverage")){
            ll_radious.setVisibility(View.VISIBLE);
            tvHeaderText.setText("Area Coverage");
            tvHeader.setText("Set Your Outcall Coverage");
        }else {
            ll_radious.setVisibility(View.GONE);
        }


       /* if (bpSession.getServiceType()!=1){
            ll_radious.setVisibility(View.VISIBLE);
        }else {
            ll_radious.setVisibility(View.GONE);
        }*/


        DisplayMetrics metrics = getResources().getDisplayMetrics();
        mapWidth = mapView.getWidth() / metrics.scaledDensity;

        seekBarRadius.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                miles = value;
                // String newValue = value > 20 ? "20+" : "" + value;
                String newValue = "" + value;
                //  tv_mileIndicater1.setText(String.format("%s miles", newValue));
                tvShowMiles.setText(String.format(newValue));
                seekBarRadius.setIndicatorFormatter(newValue);
                updateLocationCircle(miles);
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });

        iv_back.setOnClickListener(this);
        tvAddress.setOnClickListener(this);
        btnContinue.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 800) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;

            case R.id.tvAddress:
                Intent intent = new Intent(AddBusinessRadiusActivity.this,
                        NewAddressActivity.class);
                intent.putExtra("address",bizAddress);
                startActivityForResult(intent,100);

                break;

            case R.id.btnContinue:
                if (bizAddress!=null) {
                    user.setAddress(bizAddress);
                  /*  if (miles==0) {
                        miles = 1;
                        bpSession.updateRadius(miles);
                    }else*/
                    bpSession.updateRadius(miles);

                    bpSession.updateAddress(bizAddress);
                    Intent intent2 = new Intent();
                    //  intent2.putExtra("redius", bizAddress);
                    setResult(RESULT_OK, intent2);
                    finish();
                    // updateDtataIntoServer();
                }else
                    MyToast.getInstance(AddBusinessRadiusActivity.this).showDasuAlert("Please enter address");
                break;
        }
    }

    private void updateDtataIntoServer(){
        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(AddBusinessRadiusActivity.this, new NoConnectionDialog.Listner() {
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
        /*body.put("serviceType", ""+serviceType);
        body.put("inCallpreprationTime", incallPreprationTime);
        body.put("outCallpreprationTime", outcallPreprationTime);*/

        new HttpTask(new HttpTask.Builder(AddBusinessRadiusActivity.this, "updateRange", new HttpResponceListner.Listener() {
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setMyLocationButtonEnabled(false);

        map.getUiSettings().setAllGesturesEnabled(false);

        updateLocationCircle(miles);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data!=null && resultCode != 0){
            switch(requestCode) {
                case 100: {
                    bizAddress = (Address) data.getSerializableExtra("address");
                    if(bizAddress!=null){
                        tvAddress.setText(String.format("%s",
                                TextUtils.isEmpty(bizAddress.placeName)
                                        ?bizAddress.stAddress1:bizAddress.placeName));
                    }
                }
            }
        }

    }

    private void updateView(){

        if(bpSession.getBusinessProfile()!=null &&
                bpSession.getBusinessProfile().address!=null){
            miles = bpSession.getRadius();
            bizAddress = bpSession.getAddress();
            BusinessProfile bsp = user.getBusinessProfie();
            // bizAddress = bsp.address;
            if (bizAddress!=null){
                tvAddress.setText(String.format("%s",
                        TextUtils.isEmpty(bizAddress.placeName)
                                ?bizAddress.stAddress1:bizAddress.placeName));

            }

        }else {
            bizAddress = bpSession.getAddress();
            //  miles = bsp.radius;
            miles = bpSession.getRadius();

            if (bizAddress!=null){
                tvAddress.setText(String.format("%s",
                        TextUtils.isEmpty(bizAddress.placeName)
                                ?bizAddress.stAddress1:bizAddress.placeName));
            }

        }

        // String newValue = miles>20?"20+":""+miles;
        String newValue = ""+miles;
        //  tv_mileIndicater1.setText(String.format("%s miles", newValue));
        tvShowMiles.setText(String.format( newValue));

        seekBarRadius.setProgress(miles);

        updateLocationCircle(miles);
    }

    private void updateLocationCircle(int miles){

        if(bizAddress!=null){
            final LatLng latLng = new LatLng(Double.parseDouble(bizAddress.latitude),
                    Double.parseDouble(bizAddress.longitude));
            // Zoom in, animating the camera.
            final double iMeter = miles * 1609.34;

            //float currentZoomLevel = getZoomLevel(circle);
            //float animateZomm = currentZoomLevel + 1;
            float animateZomm = (float) getZoomForMetersWide(iMeter, latLng.latitude);
            if(map!=null)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, animateZomm-2));
            //map.animateCamera(CameraUpdateFactory.zoomTo(animateZomm-2), 2000, null);
        }
    }

    public  double getZoomForMetersWide (final double desiredMeters, final double latitude ) {

        if(mapWidth<=0.0f || mapWidth==320.0f){
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            mapWidth = mapView.getWidth() / metrics.scaledDensity;
            mapWidth = mapWidth==0.0f?320.0f:mapWidth;
        }
        final double latitudinalAdjustment = Math.cos( Math.PI * latitude / 180.0 );
        final double arg = 40075016 * mapWidth * latitudinalAdjustment / ( desiredMeters * 256.0 );
        return Math.log( arg ) / Math.log( 2.0 );
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
