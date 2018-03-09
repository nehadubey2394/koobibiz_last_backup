package com.mualab.org.biz.activity.profile.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.session.PreRegistrationSession;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class StripAccountFragment extends ProfileCreationBaseFragment implements View.OnClickListener {
        //DatePickerFragment.DatePickerEvent {

    // view
    //private TextView tv_dob;
    private EditText ed_firstName, ed_lastName, ed_accountNumber, ed_sortCode;
    //ed_cnfAccountNumber,  ed_postalCode, ed_ssnLast, ed_routingNumber;
    //private Spinner sp_countryCode;

    // variables
    private String firstName, lastName, accountNo, sortCode;
    //private String country_code, ssnNumber, routingNumber, dateOfBirth, postalCode;
    private PreRegistrationSession bpSession;

    public StripAccountFragment() {
        // Required empty public constructor
    }

    public static StripAccountFragment newInstance() {
        StripAccountFragment fragment = new StripAccountFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bpSession = Mualab.getInstance().getBusinessProfileSession();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_strip_account, container, false);
        ed_firstName = view.findViewById(R.id.ed_firstName);
        ed_lastName = view.findViewById(R.id.ed_lastName);
        ed_sortCode = view.findViewById(R.id.ed_sortCode);
        ed_accountNumber = view.findViewById(R.id.ed_accountNumber);
        /*ed_cnfAccountNumber = view.findViewById(R.id.ed_cnfAccountNumber);
        sp_countryCode = view.findViewById(R.id.sp_countryCode);
        ed_postalCode = view.findViewById(R.id.ed_postalCode);
        tv_dob = view.findViewById(R.id.tv_dob);
        ed_ssnLast = view.findViewById(R.id.ed_ssnLast);
        tv_dob.setOnClickListener(this);*/
        view.findViewById(R.id.btn_addAccount).setOnClickListener(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btn_addAccount:
                if(isValidInputData()){
                    updateDataIntoServer();
                }
                break;

            /*case R.id.tv_dob:
                DatePickerFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "datePicker");
                newFragment.setListner(this);
                break;*/

        }
    }


    /*@Override
    public void onDateSet(int year, int month, int dayOfMonth) {
        Calendar userAge = new GregorianCalendar(year, month, dayOfMonth);
        Calendar minAdultAge = new GregorianCalendar();
        minAdultAge.add(Calendar.YEAR, -14);
        if (minAdultAge.before(userAge)) {
            showToast("You must be 14 years or older");
        } else {

            String formattedDay = (String.valueOf(dayOfMonth));
            String formattedMonth = (String.valueOf(month));

            if (dayOfMonth < 10) {
                formattedDay = "0" + dayOfMonth;
            }

            if (month < 10) {
                formattedMonth = "0" + month;
            }

            tv_dob.setText(String.format(Locale.getDefault(),"%s-%s-%d", formattedDay, formattedMonth, year));
            //  tv_dob.setText(year+"-"+month+"-"+dayOfMonth);
        }
    }*/


    private boolean isValidInputData(){
        firstName = ed_firstName.getText().toString().trim();
        lastName = ed_lastName.getText().toString().trim();
        sortCode = ed_sortCode.getText().toString().trim();
        accountNo = ed_accountNumber.getText().toString().trim();
       /* String cnfAccountNo = ed_cnfAccountNumber.getText().toString().trim();
        dateOfBirth = tv_dob.getText().toString().trim();
        country_code = sp_countryCode.getSelectedItem().toString();
        postalCode = ed_postalCode.getText().toString().trim();
        ssnNumber = ed_ssnLast.getText().toString().trim();
        routingNumber = ed_routingNumber.getText().toString().trim();
*/
        if(TextUtils.isEmpty(firstName)){
            showToast("Please enter first name");    ed_firstName.requestFocus();
        }else if(TextUtils.isEmpty(lastName)){
            showToast("Please enter last name");     ed_lastName.requestFocus();
        }else if(TextUtils.isEmpty(sortCode)){
            showToast("Please enter sort code");    ed_sortCode.requestFocus();
        }else if(TextUtils.isEmpty(accountNo)){
            showToast("Please enter account number");ed_accountNumber.requestFocus();
        }/*else if(TextUtils.isEmpty(cnfAccountNo)){
            showToast("Please enter confirm account number");ed_cnfAccountNumber.requestFocus();
        }else if(dateOfBirth.equals("dd-mm-yyyy")){
            showToast("please select Date of birth");           tv_dob.requestFocus();
        }else if(!accountNo.equals(cnfAccountNo)){
            showToast("Account number and confirm account number should be same");ed_cnfAccountNumber.requestFocus();
        }else if(TextUtils.isEmpty(postalCode)){
            showToast("please enter Postal code");     ed_postalCode.requestFocus();
        }else if(TextUtils.isEmpty(ssnNumber)){
            showToast("Please enter SSN number");     ed_ssnLast.requestFocus();
        }*/else return true; return false;
    }



    private void updateDataIntoServer(){

        Map<String,String> params = new HashMap<>();
        params.put("firstName", firstName);
        params.put("lastName", lastName);
        params.put("accountNo", accountNo);
        params.put("routingNumber", sortCode);
        params.put("currency", "gbp");
        params.put("country", "GB");
        /*params.put("dob", dateOfBirth);

        params.put("postalCode", postalCode);
        params.put("ssnLast", ssnNumber);*/

        new HttpTask(new HttpTask.Builder(mContext, "addBankDetail", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");

                    if (status.equalsIgnoreCase("success")) {
                        bpSession.updateRegStep(7);
                        Mualab.getInstance().getSessionManager().setBusinessProfileComplete(true);
                        listener.onNext();
                    }

                    showToast(message);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
               // listener.onNext();
            }})
                //.setParam(params)
                .setAuthToken(user.authToken)
                .setBody(params, HttpTask.ContentType.APPLICATION_JSON)
                .setMethod(Request.Method.POST)
                .setProgress(true))
                .execute("addBankDetail");
    }
}
