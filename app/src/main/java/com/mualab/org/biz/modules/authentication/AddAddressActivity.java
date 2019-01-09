package com.mualab.org.biz.modules.authentication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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

public class AddAddressActivity extends AppCompatActivity {

    private int PLACE_PICKER_REQUEST = 1;
    private EditText ed_city;
    private EditText ed_locality;
    private EditText ed_opetionalAddress;
    private EditText ed_pinCode;
    private EditText ed_state;
    private EditText ed_country;
    private EditText edInputPostcode;
    private EditText edHouseNumber;

    private String country,state, city, stAddress1, stAddress2, postalCode, placeName, houseNumber;// fullAddress;
    private String latitude, longitude;

    private Intent intent;
    private String errorMsg;
    //private FetchAddressIntentService fetchAddressIntentService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);


        ed_city = findViewById(R.id.ed_city);
        ed_state = findViewById(R.id.ed_state);
        ed_country = findViewById(R.id.ed_country);
        ed_locality = findViewById(R.id.ed_locality);
        ed_opetionalAddress = findViewById(R.id.ed_opetionalAddress);
        ed_pinCode = findViewById(R.id.ed_pinCode);
        edInputPostcode = findViewById(R.id.edInputPostcode);
        edHouseNumber = findViewById(R.id.edHouseNumber);

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

      /*  Intent dataIntent = getIntent();
        if(intent!=null){
            Address address = (Address) dataIntent.getSerializableExtra("address");

        }*/


        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            intent = builder.build(AddAddressActivity.this);
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
                if(intent!=null)
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);
                else Toast.makeText(AddAddressActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
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

        TextView tvSave = findViewById(R.id.tvSave);

        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                city = ed_city.getText().toString().trim();
                state = ed_state.getText().toString().trim();
                country = ed_country.getText().toString().trim();
                postalCode = ed_pinCode.getText().toString().trim();
                stAddress1 = ed_locality.getText().toString().trim();
                stAddress2 = ed_opetionalAddress.getText().toString().trim();
                houseNumber = edHouseNumber.getText().toString().trim();
                //fullAddress = ed_locality.getText().toString().trim();
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

                        Intent intent2 = new Intent();
                        intent2.putExtra("address", address);
                        setResult(RESULT_OK, intent2);
                        finish();
                        // bpSession.updateAddress(address);
                        //   bpSession.getBusinessProfile().address = address;
                        //  setResult(address);


                    }else
                        MyToast.getInstance(AddAddressActivity.this).showDasuAlert(getString(R.string.error_required_field));



                }else
                    MyToast.getInstance(AddAddressActivity.this).showDasuAlert(getString(R.string.error_required_field));
            }
        });

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
                        new GioAddress(AddAddressActivity.this, lat, lng).execute();

                    }else {
                        MyToast.getInstance(AddAddressActivity.this)
                                .showDasuAlert(getString(R.string.msg_some_thing_went_wrong));
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                Helper helper = new Helper();
                MyToast.getInstance(AddAddressActivity.this).showDasuAlert(helper.error_Messages(error));
            }})
                .setBaseURL(api)
                .setProgress(true)
                .setMethod(Request.Method.GET))
                .execute("TAG");
    }

    private void setResult(Address address){
        Intent resultIntent = new Intent();
        resultIntent.putExtra("address", address);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save_btn, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /*  @Override
      public boolean onOptionsItemSelected(MenuItem item) {
          // Handle item selection
          switch (item.getItemId()) {
              case R.id.action_save:
                  city = ed_city.getText().toString().trim();
                  state = ed_state.getText().toString().trim();
                  country = ed_country.getText().toString().trim();
                  postalCode = ed_pinCode.getText().toString().trim();
                  stAddress1 = ed_locality.getText().toString().trim();
                  stAddress2 = ed_opetionalAddress.getText().toString().trim();
                  houseNumber = edHouseNumber.getText().toString().trim();
                  //fullAddress = ed_locality.getText().toString().trim();
                  if(validateAddress()){

                      if(TextUtils.isEmpty(placeName) || placeName.contains("S")||
                              placeName.contains("N") || placeName.contains("E")
                              || placeName.contains("°"))
                          placeName = stAddress1;
                      Address address = new Address();
                      address.city = city;
                      address.country =  country;
                      address.state = state;
                      address.postalCode = postalCode;
                      address.stAddress1 = stAddress1;
                      address.stAddress2 = stAddress2;
                      address.placeName = placeName;
                      address.houseNumber = houseNumber;
                      //address.fullAddress = fullAddress;
                      address.latitude = latitude;
                      address.longitude = longitude;
                      setResult(address);
                  }else MyToast.getInstance(this).showDasuAlert(getString(R.string.error_required_field));

                  return true;
              default:
                  return super.onOptionsItemSelected(item);
          }
      }
  */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private boolean validateAddress(){
        return !TextUtils.isEmpty(postalCode) || TextUtils.isEmpty(stAddress1)
                && TextUtils.isEmpty(latitude) && TextUtils.isEmpty(longitude);

       /* return !(TextUtils.isEmpty(city) || TextUtils.isEmpty(houseNumber) //|| TextUtils.isEmpty(state)
                || TextUtils.isEmpty(postalCode) || TextUtils.isEmpty(stAddress1)
                || TextUtils.isEmpty(latitude) || TextUtils.isEmpty(longitude)
                //|| TextUtils.isEmpty(fullAddress)
        );*/
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                //String toastMsg = String.format("Place: %s", place.getName());
                getAddressDetails(place);
                // fullAddress = place.getAddress().toString();
                placeName = place.getName().toString();

                if(TextUtils.isEmpty(city))
                    ed_city.setEnabled(true);
                if(TextUtils.isEmpty(state))
                    ed_state.setEnabled(true);
                if(TextUtils.isEmpty(country))
                    ed_country.setEnabled(true);

                ed_city.setText(city);
                ed_state.setText(state);
                ed_country.setText(country);
                ed_pinCode.setText(postalCode);
                ed_locality.setText(stAddress1);
                ed_opetionalAddress.setText(stAddress2);
                new GioAddress(AddAddressActivity.this, place.getLatLng().latitude,
                        place.getLatLng().longitude).execute();
            }
        }
    }

    public void hideKeyboard(View view) {
        KeyboardUtil.hideKeyboard(view, this);
    }

    @SuppressLint("StaticFieldLeak")
    class GioAddress extends AsyncTask<Void, Void, Void>{

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

                ed_locality.setText(TextUtils.isEmpty(stAddress1)?"":stAddress1);
                ed_opetionalAddress.setText(TextUtils.isEmpty(stAddress2)?"":stAddress2);
                ed_city.setText(city);
                ed_state.setText(state);
                ed_country.setText(country);
                ed_pinCode.setText(postalCode);

                ed_city.setEnabled(true);
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
