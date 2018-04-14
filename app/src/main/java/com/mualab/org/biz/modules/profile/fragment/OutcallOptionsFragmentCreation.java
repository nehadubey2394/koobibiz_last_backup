package com.mualab.org.biz.modules.profile.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.mualab.org.biz.R;
import com.mualab.org.biz.modules.authentication.AddAddressActivity;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.dialogs.NoConnectionDialog;
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
import views.pickerview.MyTimePickerDialog;
import views.pickerview.timepicker.TimePicker;

public class OutcallOptionsFragmentCreation extends ProfileCreationBaseFragment implements OnMapReadyCallback {

    private TextView tv_placeName, tv_address;
    private TextView tv_mileIndicater1;
    private TextView tvIncallPreprationTime, tvOutcallPreprationTime;
    private LinearLayout ll_outCallInput, ll_radious, ll_incall;
    private DiscreteSeekBar seekBarRadius;
    private RadioGroup radioGroup;
    private RadioButton rb_inCall, rb_outCall, rb_both;

    private int miles = 5;
    private int serviceType = 1;
    private float mapWidth;
    private String incallPreprationTime, outcallPreprationTime;
    private Address address;

    private PreRegistrationSession bpSession;
    private MyMapView mapView;
    private GoogleMap map;

    public OutcallOptionsFragmentCreation() {
        // Required empty public constructor
    }

    public static OutcallOptionsFragmentCreation newInstance() {
        return new OutcallOptionsFragmentCreation();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bpSession = Mualab.getInstance().getBusinessProfileSession();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_outcall_options, container, false);
        tv_placeName = view.findViewById(R.id.tv_placeName);
        tv_address = view.findViewById(R.id.tv_address);
        tvIncallPreprationTime = view.findViewById(R.id.tvIncallPreprationTime);
        tvOutcallPreprationTime = view.findViewById(R.id.tvOutcallPreprationTime);
        tv_mileIndicater1 = view.findViewById(R.id.tv_mileIndicater1);
        seekBarRadius = view.findViewById(R.id.seekBarRadius);
        ll_outCallInput = view.findViewById(R.id.ll_outCallInput);
        ll_radious = view.findViewById(R.id.ll_radious);
        ll_incall = view.findViewById(R.id.ll_incall);
        rb_inCall = view.findViewById(R.id.rb_inCall);
        rb_outCall = view.findViewById(R.id.rb_outCall);
        rb_both = view.findViewById(R.id.rb_both);
        radioGroup = view.findViewById(R.id.rg_serviceType);
        // Gets the MapView from the XML layout and creates it
        mapView = view.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        mapWidth = mapView.getWidth() / metrics.scaledDensity;
        updateView();
        seekBarRadius.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                miles = value;
                String newValue = value > 20 ? "20+" : "" + value;
                tv_mileIndicater1.setText(String.format("%s miles", newValue));
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


        view.findViewById(R.id.btn_countinue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validateInputValue()) {

                    if (!ConnectionDetector.isConnected()) {
                        new NoConnectionDialog(mContext, new NoConnectionDialog.Listner() {
                            @Override
                            public void onNetworkChange(Dialog dialog, boolean isConnected) {
                                if(isConnected){
                                    dialog.dismiss();
                                    updateDtataIntoServer();
                                    bpSession.updateRadius(miles);
                                    bpSession.updateIncallPreprationTime(incallPreprationTime);
                                    bpSession.updateOutcallPreprationTime(outcallPreprationTime);
                                    bpSession.updateServiceType(serviceType);
                                    if (listener != null)
                                        listener.onNext();
                                }
                            }
                        }).show();
                    }else {
                        bpSession.updateRadius(miles);
                        bpSession.updateIncallPreprationTime(incallPreprationTime);
                        bpSession.updateOutcallPreprationTime(outcallPreprationTime);
                        bpSession.updateServiceType(serviceType);
                        if (listener != null)
                            listener.onNext();
                        updateDtataIntoServer();
                    }
                }
            }
        });

        view.findViewById(R.id.iv_editLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AddAddressActivity.class);
                if (address != null)
                    intent.putExtra("address", address);
                startActivityForResult(intent, 1001);
            }
        });


        tvIncallPreprationTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPicker(tvIncallPreprationTime, getString(R.string.incall_prepration_time));
            }
        });


        final TextView tvOutcallPreprationTime = view.findViewById(R.id.tvOutcallPreprationTime);
        tvOutcallPreprationTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPicker(tvOutcallPreprationTime, getString(R.string.outcall_prepration_time));
            }
        });


        // This overrides the radiogroup onCheckListener
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // This will get the radiobutton that has changed in its check state
                //RadioButton checkedRadioButton = group.findViewById(checkedId);
                switch (checkedId) {

                    case R.id.rb_inCall:
                        ll_outCallInput.setVisibility(View.GONE);
                        ll_radious.setVisibility(View.GONE);
                        ll_incall.setVisibility(View.VISIBLE);
                        serviceType = 1;
                        bpSession.updateServiceType(1);
                        break;

                    case R.id.rb_outCall:
                        ll_outCallInput.setVisibility(View.VISIBLE);
                        ll_radious.setVisibility(View.VISIBLE);
                        ll_incall.setVisibility(View.GONE);
                        serviceType = 2;
                        bpSession.updateServiceType(2);
                        break;

                    case R.id.rb_both:
                        ll_outCallInput.setVisibility(View.VISIBLE);
                        ll_radious.setVisibility(View.VISIBLE);
                        ll_incall.setVisibility(View.VISIBLE);
                        serviceType = 3;
                        bpSession.updateServiceType(3);
                        break;
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setMyLocationButtonEnabled(false);
        /*if (ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }*/
       // map.setMyLocationEnabled(true);
        map.getUiSettings().setAllGesturesEnabled(false);

        updateLocationCircle(miles);
       /*
       //in old Api Needs to call MapsInitializer before doing any CameraUpdateFactory call
        try {
            MapsInitializer.initialize(this.getActivity());
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
       */
    }

    private void updateLocationCircle(int miles){

        if(address!=null){
            final LatLng latLng = new LatLng(Double.parseDouble(address.latitude), Double.parseDouble(address.longitude));
            // Zoom in, animating the camera.
            final double iMeter = miles * 1609.34;
           /* map.clear();
            map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView())));*/

            /*if(circle!=null)
                circle.remove();

            circle = map.addCircle(new CircleOptions()
                    .center(latLng)
                    .radius(14) // Converting Miles into Meters...
                    .strokeColor(getResources().getColor(R.color.colorPrimary))
                    .strokeWidth(2)
                    .fillColor(getResources().getColor(R.color.colorPrimary)));
            circle.isVisible();*/
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

    public void showPicker(final TextView tvTime, final String title){
        int hours = 01;
        String tmpTime = tvTime.getText().toString();
        if(!tmpTime.equals("HH:MM")){
            try {
                String[] arrayTime = tmpTime.split(":");
                hours = Integer.parseInt(arrayTime[0]);
                //minute = Integer.parseInt(arrayTime[1]);
            }catch (Exception ex){
                hours = 01;
            }
        }
        MyTimePickerDialog mTimePicker = new MyTimePickerDialog(mContext, new MyTimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hours, int minute) {
                tvTime.setText(String.format("%02d", hours)+ ":" + String.format("%02d", minute));
            }
        }, title, hours, 00, 10, 3);
        mTimePicker.show();
    }

    private void updateView(){

        if(bpSession.getBusinessProfile()!=null && bpSession.getBusinessProfile().address!=null){

            serviceType = bpSession.getServiceType();
            incallPreprationTime = bpSession.getInCallPreprationTime();
            outcallPreprationTime = bpSession.getOutCallPreprationTime();
            //address = bpSession.getBusinessProfile().address;
            miles = bpSession.getRadius();

            BusinessProfile bsp = user.getBusinessProfie();
            address = bsp.address;

        }else {

            BusinessProfile bsp = user.getBusinessProfie();
            serviceType = bsp.serviceType==0?1:bsp.serviceType;
            incallPreprationTime = bsp.inCallpreprationTime;
            outcallPreprationTime = bsp.outCallpreprationTime;
            address = bsp.address;
            miles = bsp.radius;
        }

        if(TextUtils.isEmpty(incallPreprationTime)){
            incallPreprationTime = "HH:MM";
        }

        if(TextUtils.isEmpty(outcallPreprationTime)){
            outcallPreprationTime = "HH:MM";
        }

        /*Update UI*/

        if(serviceType ==1){
            ll_outCallInput.setVisibility(View.GONE);
            ll_radious.setVisibility(View.GONE);
            ll_incall.setVisibility(View.VISIBLE);
            rb_inCall.setChecked(true);
        }else if(serviceType ==2){
            ll_outCallInput.setVisibility(View.VISIBLE);
            ll_radious.setVisibility(View.VISIBLE);
            ll_incall.setVisibility(View.GONE);
            rb_outCall.setChecked(true);
        }else if(serviceType ==3) {
            ll_outCallInput.setVisibility(View.VISIBLE);
            ll_radious.setVisibility(View.VISIBLE);
            ll_incall.setVisibility(View.VISIBLE);
            rb_both.setChecked(true);
        }

        tv_address.setText(address.stAddress1);
        tv_placeName.setText(address.placeName);
        tvIncallPreprationTime.setText(incallPreprationTime);
        tvOutcallPreprationTime.setText(outcallPreprationTime);

        String newValue = miles>20?"20+":""+miles;
        tv_mileIndicater1.setText(String.format("%s miles", newValue));

        if(miles>20){
            seekBarRadius.setProgress(miles);
            seekBarRadius.setIndicatorFormatter(newValue);
        }else seekBarRadius.setProgress(miles);
    }

    private boolean validateInputValue(){

        int checkedId = radioGroup.getCheckedRadioButtonId();
        String inCallTime = tvIncallPreprationTime.getText().toString();
        String outCallTime = tvOutcallPreprationTime.getText().toString();
        switch (checkedId){

            case R.id.rb_inCall:
                if(inCallTime.equals(getString(R.string.hh_mm)) || inCallTime.equals("00:00")){
                    showToast(R.string.select_incall_preparation_time);
                    return false;
                }else{
                    outcallPreprationTime = "";
                    incallPreprationTime = tvIncallPreprationTime.getText().toString();
                }

                break;

            case R.id.rb_outCall:
                if(outCallTime.equals(getString(R.string.hh_mm)) || outCallTime.equals("00:00")){
                    showToast(R.string.select_outcall_preparation_time);
                    return false;
                }else{
                    incallPreprationTime = "";
                    outcallPreprationTime = tvOutcallPreprationTime.getText().toString();
                }

                break;

            case R.id.rb_both:
                if(inCallTime.equals(getString(R.string.hh_mm))
                        || inCallTime.equals("00:00")
                        || outCallTime.equals(getString(R.string.hh_mm))
                        || outCallTime.equals("00:00")){
                showToast(R.string.select_in_and_outcall_preparation_time);
                    return false;
                }else {
                    incallPreprationTime = tvIncallPreprationTime.getText().toString();
                    outcallPreprationTime = tvOutcallPreprationTime.getText().toString();
                }
                break;

            default:
                showToast("please checked one service type");
                return false;
        }

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch(requestCode) {
                case 1001: {
                    address = (Address) data.getSerializableExtra("address");
                    if(address!=null){

                        if(!ConnectionDetector.isConnected()){

                            new NoConnectionDialog(mContext, new NoConnectionDialog.Listner() {
                                @Override
                                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                                    if(isConnected){
                                        dialog.dismiss();
                                        bpSession.updateAddress(address);
                                        tv_address.setText(String.format("%s", address.stAddress1));
                                        tv_placeName.setText(TextUtils.isEmpty(address.placeName)?address.stAddress1:address.placeName);
                                        tv_address.setVisibility(TextUtils.isEmpty(address.placeName)?View.GONE:View.VISIBLE);
                                        address = bpSession.getBusinessProfile().address;

                                        LatLng latLng = new LatLng(Double.parseDouble(address.latitude), Double.parseDouble(address.latitude));
                                        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                        updateAddressIntoServer();
                                    }
                                }
                            }).show();
                        }else {

                            bpSession.updateAddress(address);
                            tv_address.setText(String.format("%s", address.stAddress1));
                            tv_placeName.setText(TextUtils.isEmpty(address.placeName)?address.stAddress1:address.placeName);
                            tv_address.setVisibility(TextUtils.isEmpty(address.placeName)?View.GONE:View.VISIBLE);
                            address = bpSession.getBusinessProfile().address;

                            LatLng latLng = new LatLng(Double.parseDouble(address.latitude), Double.parseDouble(address.latitude));
                            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                            updateAddressIntoServer();
                        }
                    }
                    break;
                }
            }
        }
    }

    private void updateDtataIntoServer(){
        Map<String,String> body = new HashMap<>();
        body.put("radius", ""+miles);
        body.put("serviceType", ""+serviceType);
        body.put("inCallpreprationTime", incallPreprationTime);
        body.put("outCallpreprationTime", outcallPreprationTime);

        new HttpTask(new HttpTask.Builder(mContext, "updateRange", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                Log.d("res:", response);
                bpSession.updateRegStep(2);
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

    private void updateAddressIntoServer(){
        Map<String,String> params = new HashMap<>();
        params.put("address", address.stAddress1);
        params.put("address2", address.stAddress2);
        params.put("city", address.city);
        params.put("state", address.state);
        params.put("country", address.country);
        params.put("businessPostCode", address.postalCode);
        params.put("latitude", address.latitude);
        params.put("longitude", address.longitude);

        new HttpTask(new HttpTask.Builder(mContext, "updateRecord", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                Log.d("res:", response);
                User user = Mualab.getInstance().getSessionManager().getUser();
                user.setAddress(address);
                bpSession.updateAddress(address);
                Mualab.getInstance().getSessionManager().createSession(user);

               // bpSession.updateRegStep(2);
            }

            @Override
            public void ErrorListener(VolleyError error) {
                //Log.d("res:", error.getLocalizedMessage());
            }})
                .setMethod(Request.Method.POST)
                .setBody(params)
                .setBodyContentType( HttpTask.ContentType.APPLICATION_JSON)
                .setAuthToken(user.authToken)).execute("updateRecord");
    }
}
