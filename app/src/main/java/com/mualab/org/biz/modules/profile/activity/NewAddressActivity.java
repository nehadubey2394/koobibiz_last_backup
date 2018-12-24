package com.mualab.org.biz.modules.profile.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.model.Address;
import com.mualab.org.biz.model.BusinessProfile;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.session.PreRegistrationSession;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.Helper;
import com.mualab.org.biz.util.KeyboardUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class NewAddressActivity extends AppCompatActivity {
    private PreRegistrationSession bpSession;
    protected User user;
    private long mLastClickTime = 0;
    private Intent intent;
    private String errorMsg;
    private int PLACE_PICKER_REQUEST = 1;
    private String country,state, city, stAddress1, stAddress2, postalCode, placeName, houseNumber;// fullAddress;
    private String latitude, longitude;
    private TextView tvAddress,tvLocation;
    private EditText edInputPostcode;
    private Address bizAddress;
    private ImageView ivDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_address);

        if(user==null) user = Mualab.getInstance().getSessionManager().getUser(); // get session object
        bpSession = Mualab.getInstance().getBusinessProfileSession();
        ImageView ivKoobiLogo = findViewById(R.id.ivKoobiLogo);
        ivKoobiLogo.setVisibility(View.GONE);
        ImageView iv_back = findViewById(R.id.iv_back);
        TextView tvHeaderText = findViewById(R.id.tvHeaderText);

        tvHeaderText.setVisibility(View.VISIBLE);
        tvHeaderText.setText("Address");

        tvAddress = findViewById(R.id.tvAddress);
        edInputPostcode = findViewById(R.id.edInputPostcode);
        tvLocation = findViewById(R.id.tvLocation);
        ivDone = findViewById(R.id.ivDone);

        updateView();

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            intent = builder.build(NewAddressActivity.this);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
            errorMsg = e.getLocalizedMessage();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
            errorMsg = e.getLocalizedMessage();
        }

        findViewById(R.id.ll_picAddress).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edInputPostcode.setText("");
                if(intent!=null)
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);
                else Toast.makeText(NewAddressActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
            }
        });


        edInputPostcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length()>0)
                    ivDone.setVisibility(View.VISIBLE);
                else
                    ivDone.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edInputPostcode.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    String postcode = edInputPostcode.getText().toString().trim();
                    hideKeyboard(edInputPostcode);
                    if(!TextUtils.isEmpty(postcode))
                        getAddressByPostCode(postcode);
                    return true;
                }
                return false;
            }
        });

        ivDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String postcode = edInputPostcode.getText().toString().trim();
                hideKeyboard(edInputPostcode);
                if(!TextUtils.isEmpty(postcode))
                    getAddressByPostCode(postcode);
            }
        });

        findViewById(R.id.btnContinue).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                postalCode = edInputPostcode.getText().toString().trim();
                stAddress1 = tvAddress.getText().toString().trim();

                if(validateAddress()) {
                    if (!stAddress1.equals("") && latitude!=null && !latitude.isEmpty()){
                        if (TextUtils.isEmpty(placeName) || placeName.contains("S") ||
                                placeName.contains("N") || placeName.contains("E")
                                || placeName.contains("°"))
                            placeName = stAddress1;

                        PreRegistrationSession bpSession  = Mualab.getInstance().getBusinessProfileSession();

                        Address address = new Address();
                        address.city = city;
                        address.country = country;
                        address.state = state;
                        address.postalCode = postalCode;
                        address.stAddress1 = stAddress1;
                        address.stAddress2 = stAddress2;
                        address.placeName = placeName;
                        address.houseNumber = houseNumber;
                        //address.fullAddress = fullAddress;
                        address.latitude = latitude;
                        address.longitude = longitude;
                        bpSession.updateAddress(address);
                        Intent intent2 = new Intent();
                        intent2.putExtra("address", address);
                        setResult(RESULT_OK, intent2);
                        finish();

                    }else
                        MyToast.getInstance(NewAddressActivity.this).showDasuAlert(getString(R.string.error_required_field));
                }else
                    MyToast.getInstance(NewAddressActivity.this).showDasuAlert(getString(R.string.error_required_field));

            }
        });

    }

    private void updateView(){

        if(bpSession.getBusinessProfile()!=null &&
                bpSession.getBusinessProfile().address!=null){
            bizAddress = bpSession.getAddress();
            BusinessProfile bsp = user.getBusinessProfie();
            // bizAddress = bsp.address;
            if (bizAddress!=null){
                tvAddress.setText(String.format("%s",
                        TextUtils.isEmpty(bizAddress.placeName)
                                ?bizAddress.stAddress1:bizAddress.placeName));

                latitude = bizAddress.latitude;
                longitude = bizAddress.longitude;
            }

        }else {
            bizAddress = bpSession.getAddress();
            //  miles = bsp.radius;

            if (bizAddress!=null){
                tvAddress.setText(String.format("%s",
                        TextUtils.isEmpty(bizAddress.placeName)
                                ?bizAddress.stAddress1:bizAddress.placeName));
                latitude = bizAddress.latitude;
                longitude = bizAddress.longitude;
            }

        }
    }

    private void getAddressByPostCode(String postalCode){
        String api = "https://api.postcodes.io/postcodes/";
        new HttpTask(new HttpTask.Builder(this, postalCode, new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                Log.d("responce", response);
                try{

                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getInt("status")==200){
                        JSONObject o = jsonObject.getJSONObject("result");
                        country = o.getString("country");
                        latitude = o.getString("latitude");
                        longitude = o.getString("longitude");
                        Double lat = Double.parseDouble(latitude);
                        Double lng = Double.parseDouble(longitude);
                        new GioAddress(NewAddressActivity.this, lat, lng).execute();

                    }else {
                        edInputPostcode.setText("");
                        MyToast.getInstance(NewAddressActivity.this)
                                .showDasuAlert(getString(R.string.msg_some_thing_went_wrong));
                    }
                }catch (JSONException e) {
                    edInputPostcode.setText("");
                    e.printStackTrace();
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                Helper helper = new Helper();
                tvAddress.setText("");
                String msg = helper.error_Messages(error);
                edInputPostcode.setText("");
                if (msg.equals("Invalid postcode"))
                    MyToast.getInstance(NewAddressActivity.this).showDasuAlert(" Please enter valid Postcode");
                else
                    MyToast.getInstance(NewAddressActivity.this).showDasuAlert(msg);
            }})
                .setBaseURL(api)
                .setProgress(true)
                .setMethod(Request.Method.GET))
                .execute("TAG");
    }

    private boolean validateAddress(){
        /*if (TextUtils.isEmpty(postalCode))
            return false;*/
        if (TextUtils.isEmpty(stAddress1))
            return false;
        else if (TextUtils.isEmpty(latitude))
            return false;
        else   if (TextUtils.isEmpty(longitude))
            return false;
        else
            return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                //String toastMsg = String.format("Place: %s", place.getName());
                getAddressDetails(place);
                // fullAddress = place.getAddress().toString();
                placeName = place.getName().toString();


           /*     tvAddress.setText(stAddress1);

                city = place.getLocale().toString();
                state = place.getName().toString();
               // country = address.getCountryName();
              //  postalCode = address.getPostalCode();
               // country = address.getCountryName();
                stAddress1 = place.getAddress().toString();
                stAddress2 = place.getAddress().toString();
                latitude = String.valueOf(place.getLatLng().latitude);
                longitude = String.valueOf(place.getLatLng().longitude);*/

                // tvLocation.setText(TextUtils.isEmpty(stAddress1)?"":stAddress1);
                if (!TextUtils.isEmpty(placeName))
                    tvAddress.setText(placeName+" "+stAddress1);
                else
                    tvAddress.setText(stAddress1);

               /* new GioAddress(NewAddressActivity.this, place.getLatLng().latitude,
                        place.getLatLng().longitude).execute();*/
            }
        }
    }

    public void hideKeyboard(View view) {
        KeyboardUtil.hideKeyboard(view, this);
    }

    @SuppressLint("StaticFieldLeak")
    class GioAddress extends AsyncTask<Void, Void, Void> {

        Context mContext;
        Double lat, lng;
        android.location.Address address;

        GioAddress(Context mContext,  Double lat, Double lng){
            this.mContext = mContext;
            this.lat = lat;
            this.lng = lng;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
            try {
                List<android.location.Address> addresses = geocoder.getFromLocation(lat, lng, 1);
                addresses.toString();
                if(addresses.size()>0){
                    address = addresses.get(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(address!=null){
                city = address.getLocality();
                state = address.getAdminArea();
                country = address.getCountryName();
                postalCode = address.getPostalCode();
                country = address.getCountryName();
                stAddress1 = address.getAddressLine(0);
                stAddress2 = address.getAddressLine(1);

                // tvLocation.setText(TextUtils.isEmpty(stAddress1)?"":stAddress1);
                tvAddress.setText(TextUtils.isEmpty(stAddress1)?"":stAddress1);
                //   ed_city.setText(city);
                //    ed_state.setText(state);
                //    ed_country.setText(country);
                // edInputPostcode.setText(postalCode);

                //ed_city.setEnabled(true);
            }
        }
    }

    public void getAddressDetails(Place place) {
        city = state = country = postalCode = stAddress1 = stAddress2 = latitude = longitude = "";
        if (place.getAddress() != null) {
            String[] addressSlice = place.getAddress().toString().split(", ");
            country = addressSlice[addressSlice.length - 1];
            if (addressSlice.length > 1) {
                String[] stateAndPostalCode = addressSlice[addressSlice.length - 2].split(" ");
                if (stateAndPostalCode.length > 1) {
                    postalCode = stateAndPostalCode[stateAndPostalCode.length - 1];
                    state = "";
                    for (int i = 0; i < stateAndPostalCode.length - 1; i++) {
                        state += (i == 0 ? "" : " ") + stateAndPostalCode[i];
                    }
                } else {
                    state = stateAndPostalCode[stateAndPostalCode.length - 1];
                }
            }
            if (addressSlice.length > 2)
                city = addressSlice[addressSlice.length - 3];
            if (addressSlice.length == 4)
                stAddress1 = addressSlice[0];
            else if (addressSlice.length > 3) {
                stAddress2 = addressSlice[addressSlice.length - 4];
                stAddress1 = "";
                for (int i = 0; i < addressSlice.length - 4; i++) {
                    stAddress1 += (i == 0 ? "" : ", ") + addressSlice[i];
                }
            }
        }

        if(place.getLatLng()!=null) {
            latitude = "" + place.getLatLng().latitude;
            longitude = "" + place.getLatLng().longitude;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
