package com.mualab.org.biz.modules.business_setup.voucher_code;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.dialogs.NoConnectionDialog;
import com.mualab.org.biz.dialogs.Progress;
import com.mualab.org.biz.helper.InputFilterMinMax;
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.model.VoucherCode;
import com.mualab.org.biz.modules.business_setup.voucher_code.adapter.CustomSpAdapter;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.ConnectionDetector;
import com.mualab.org.biz.util.Helper;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class AddVoucherCodeActivity extends AppCompatActivity implements View.OnClickListener {
    //private  TextView tvDiscountType;
    private EditText edt_discount,edt_voucher_code;
    private  ImageView ivValidityAerow;
    private boolean isExpand;
    private LinearLayout llValidityDate;
    private TextView tvFromdate,tvTodate;
    private String fromDate="",toDate="",sDiscountType;
    private  int mYear,mMonth,mDay;
    private VoucherCode voucherCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_voucher_code);
        initView();
    }

    private void initView(){
        ImageView ivHeaderBack = findViewById(R.id.ivHeaderBack);
        TextView tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        tvHeaderTitle.setText("Add Voucher Code");

        if (getIntent()!=null){
            if (getIntent().hasExtra("voucherCode")){
                voucherCode = (VoucherCode)getIntent().getParcelableExtra("voucherCode");
            }
        }

        // tvDiscountType = findViewById(R.id.tvDiscountType);
        edt_discount = findViewById(R.id.edt_discount);
        edt_voucher_code = findViewById(R.id.edt_voucher_code);
        edt_voucher_code.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        ivValidityAerow = findViewById(R.id.ivValidityAerow);
        llValidityDate = findViewById(R.id.llValidityDate);
        tvFromdate = findViewById(R.id.tvFromdate);
        tvTodate = findViewById(R.id.tvTodate);

        AppCompatButton btnSave = findViewById(R.id.btnSave);

        RelativeLayout rlValidity = findViewById(R.id.rlValidity);

        final ArrayList<String> arrayList = new ArrayList<>();

        if (voucherCode!=null){
            edt_discount.setText(voucherCode.amount);
            edt_voucher_code.setText(voucherCode.voucherCode);
            tvFromdate.setText(voucherCode.startDate);
            tvTodate.setText(voucherCode.endDate);
            switch (voucherCode.discountType) {
                case "1":
                    arrayList.add("Amount(£)");
                    arrayList.add("Percentage(%)");
                    break;
                case "2":
                    arrayList.add("Percentage(%)");
                    arrayList.add("Amount(£)");
                    break;
                default:
                    arrayList.add("Discount Type");
                    arrayList.add("Percentage(%)");
                    arrayList.add("Amount(£)");
                    break;
            }
        }else {
            arrayList.add("Discount Type");
            arrayList.add("Percentage(%)");
            arrayList.add("Amount(£)");
        }

        final Spinner spDiscountType = findViewById(R.id.spDiscountType);

        CustomSpAdapter spAdapter = new CustomSpAdapter(AddVoucherCodeActivity.this,
                arrayList);

        spDiscountType.setAdapter(spAdapter);
        spDiscountType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //  TextView textView =  view.findViewById(R.id.tvSpItem);
                // textView.setText("");
                //  tvDiscountType.setText(arrayList.get(i));

                sDiscountType = arrayList.get(i);
                if (i!=0)
                    edt_discount.setText("");

                if (sDiscountType.equals("Percentage(%)"))
                    edt_discount.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
                else
                    edt_discount.setFilters(new InputFilter[]{new InputFilter.LengthFilter(7)});

                if (sDiscountType.equals("Discount Type"))
                    edt_discount.setVisibility(View.GONE);
                else
                    edt_discount.setVisibility(View.VISIBLE);


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // textv1.setText(getString(R.string.business_type));
                // tvBizType.setVisibility(View.GONE);
            }
        });


        tvFromdate.setOnClickListener(this);
        tvTodate.setOnClickListener(this);
        ivHeaderBack.setOnClickListener(this);
        rlValidity.setOnClickListener(this);
        ivHeaderBack.setOnClickListener(this);
        btnSave.setOnClickListener(this);

        edt_discount.setFilters(new InputFilter[]{new InputFilterMinMax(1, 100)});

        edt_discount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
              /*  if (charSequence.length()!=0 && sDiscountType.equals("Percentage(%)")) {
                    Integer val = Integer.parseInt(charSequence + "");
                    if (val > 100) {
                        MyToast.getInstance(AddVoucherCodeActivity.this).showDasuAlert("Percentage value must be less than 100");
                    }
                }*/
            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    if (sDiscountType.equals("Percentage(%)")) {
                        if (Integer.parseInt(editable.toString()) > 100) {
                            editable.replace(2, editable.length(), "");
                            MyToast.getInstance(AddVoucherCodeActivity.this).showDasuAlert("Percentage value must be less than 100");
                        }
                    }
                }
                catch(NumberFormatException e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void slideToBottom(View v){

        Animation a = AnimationUtils.loadAnimation(AddVoucherCodeActivity.this,
                R.anim.item_anim_fall_down);
        if(a != null){
            a.reset();
            if(v != null){
                v.clearAnimation();
                v.startAnimation(a);
            }
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ivHeaderBack:
                finish();
                break;

            case R.id.tvFromdate:
                datePicker("from");
                break;

            case R.id.tvTodate:
                if (fromDate!=null && !fromDate.isEmpty())
                    datePicker("to");
                else
                    MyToast.getInstance(AddVoucherCodeActivity.this).showDasuAlert("Please select start date");
                break;

            case R.id.rlValidity:
                if (isExpand){
                    isExpand = false;
                    ivValidityAerow.setRotation(360);
                    llValidityDate.setVisibility(View.GONE);
                }else {
                    isExpand = true;
                    ivValidityAerow.setRotation(180);
                    llValidityDate.setVisibility(View.VISIBLE);
                    slideToBottom(llValidityDate);
                }
                break;

            case R.id.btnSave:
                if (checkNotempty(edt_voucher_code.getText().toString().trim())){

                    if (checkNotempty(edt_discount.getText().toString().trim())){

                        if (checkNotempty(tvFromdate.getText().toString().trim())){

                            if (checkNotempty(tvTodate.getText().toString().trim())){
                                apiAddVoucher();
                            }else {
                                MyToast.getInstance(AddVoucherCodeActivity.this).showDasuAlert("Please select start date");
                            }

                        }else {
                            MyToast.getInstance(AddVoucherCodeActivity.this).showDasuAlert("Please select start date");
                        }
                    }else {
                        MyToast.getInstance(AddVoucherCodeActivity.this).showDasuAlert("Please enter discount amount");
                    }
                }else {
                    MyToast.getInstance(AddVoucherCodeActivity.this).showDasuAlert("Please enter voucher code");
                }
                break;
        }
    }

    private void datePicker(final String tag){
        // Get Current Date
        if (tag.equals("from")) {
            final Calendar c = GregorianCalendar.getInstance();
            mYear = c.get(GregorianCalendar.YEAR);
            mMonth = c.get(GregorianCalendar.MONTH);
            mDay = c.get(GregorianCalendar.DAY_OF_MONTH);
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Date date = new Date(year, monthOfYear, dayOfMonth-1);

                        if (tag.equals("from")) {
                            fromDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                            tvFromdate.setText(fromDate);
                            tvTodate.setText("");
                            mYear = year;
                            mMonth = monthOfYear+1;
                            mDay = dayOfMonth;

                        }
                        else {
                            toDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                            tvTodate.setText(toDate);
                        }


                    }
                }, mYear, mMonth, mDay);
        if (tag.equals("from")) {
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        }else {
            Calendar cal = GregorianCalendar.getInstance();
            cal.setTimeInMillis(0);
            cal.set(mYear, mMonth, mDay, 11, 12, 13);
            Date chosenDate = cal.getTime();
            datePickerDialog.getDatePicker().setMinDate(chosenDate.getTime());
        }
        datePickerDialog.show();
    }

    private boolean checkNotempty(String text) {
        return !TextUtils.isEmpty(text);
    }

    private void apiAddVoucher() {
        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(AddVoucherCodeActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if (isConnected) {
                        dialog.dismiss();
                        apiAddVoucher();
                    }
                }
            }).show();
        }

        //  pbLoder.setVisibility(View.VISIBLE);
        Map<String,String> body = new HashMap<>();
        body.put("voucherCode", edt_voucher_code.getText().toString().trim());
        if (sDiscountType.equals("Percentage(%)"))
            body.put("discountType", "2");// (1:Fix;2:percentage)
        else
            body.put("discountType", "1");

        body.put("amount", edt_discount.getText().toString().trim());
        body.put("startDate", tvFromdate.getText().toString().trim());
        body.put("endDate", tvTodate.getText().toString().trim());
        if (voucherCode!=null)
            body.put("id ", voucherCode._id);
        else
            body.put("id ", "");


        HttpTask task = new HttpTask(new HttpTask.Builder(AddVoucherCodeActivity.this,
                "addVoucher", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    Progress.hide(AddVoucherCodeActivity.this);
                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");

                    if (status.equalsIgnoreCase("success")) {
                        setResult(RESULT_OK);
                        finish();

                    }else {
                        MyToast.getInstance(AddVoucherCodeActivity.this).showDasuAlert(message);
                    }
                    //    pbLoder.setVisibility(View.GONE);


                }catch (Exception e) {
                    Progress.hide(AddVoucherCodeActivity.this);
                    e.printStackTrace();
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                try {
                    Progress.hide(AddVoucherCodeActivity.this);
                    Helper helper = new Helper();
                    if (helper.error_Messages(error).contains("Session")) {
                        Mualab.getInstance().getSessionManager().logout();
                    }else
                        MyToast.getInstance(AddVoucherCodeActivity.this).showDasuAlert(helper.error_Messages(error));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        })
                .setAuthToken(Mualab.getInstance().getSessionManager().getUser().authToken)
                .setProgress(true)
                .setParam(body));

        task.execute(AddVoucherCodeActivity.class.getName());
    }

}
