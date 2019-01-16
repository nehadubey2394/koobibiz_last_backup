package com.mualab.org.biz.modules.business_setup.voucher_code;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.SystemClock;
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
import com.mualab.org.biz.helper.Constants;
import com.mualab.org.biz.helper.InputFilterMinMax;
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.model.VoucherCode;
import com.mualab.org.biz.modules.business_setup.voucher_code.adapter.CustomSpAdapter;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.CalanderUtils;
import com.mualab.org.biz.util.ConnectionDetector;
import com.mualab.org.biz.util.Helper;
import com.mualab.org.biz.util.KeyboardUtil;
import com.mualab.org.biz.util.Utils;

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
    private VoucherCode voucherCode;
    private long mLastClickTime = 0;

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
            edt_voucher_code.setEnabled(false);
            fromDate = voucherCode.startDate;
            toDate = voucherCode.endDate;
            tvFromdate.setText(Utils.getDateToShowFormate(voucherCode.startDate));
            tvTodate.setText(Utils.getDateToShowFormate(voucherCode.endDate));

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
            edt_voucher_code.setEnabled(true);
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
                    edt_discount.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3),new InputFilterMinMax(1, 100),new InputFilter.AllCaps()});
                else
                    edt_discount.setFilters(new InputFilter[]{new InputFilter.LengthFilter(7),new InputFilter.AllCaps()});

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


        edt_discount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    if (sDiscountType.equals("Percentage(%)")) {
                        if (Integer.parseInt(editable.toString()) > 100) {
                            // editable.replace(2, editable.length(), "");
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
        if (SystemClock.elapsedRealtime() - mLastClickTime < 600) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        switch (view.getId()){
            case R.id.ivHeaderBack:
                KeyboardUtil.hideKeyboard(edt_discount, AddVoucherCodeActivity.this);
                KeyboardUtil.hideKeyboard(edt_voucher_code, AddVoucherCodeActivity.this);
                finish();
                break;

            case R.id.tvFromdate:
                datePicker("from");
                KeyboardUtil.hideKeyboard(edt_discount, AddVoucherCodeActivity.this);
                KeyboardUtil.hideKeyboard(edt_voucher_code, AddVoucherCodeActivity.this);
                break;

            case R.id.tvTodate:
                KeyboardUtil.hideKeyboard(edt_discount, AddVoucherCodeActivity.this);
                KeyboardUtil.hideKeyboard(edt_voucher_code, AddVoucherCodeActivity.this);
                if (fromDate!=null && !fromDate.isEmpty())
                    datePicker("to");
                else
                    MyToast.getInstance(AddVoucherCodeActivity.this).showDasuAlert("Please select start date");
                break;

            case R.id.rlValidity:
                KeyboardUtil.hideKeyboard(edt_discount, AddVoucherCodeActivity.this);
                KeyboardUtil.hideKeyboard(edt_voucher_code, AddVoucherCodeActivity.this);
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
                KeyboardUtil.hideKeyboard(edt_discount, AddVoucherCodeActivity.this);
                KeyboardUtil.hideKeyboard(edt_voucher_code, AddVoucherCodeActivity.this);

                if (checkNotempty(edt_voucher_code.getText().toString().trim())){

                    if (checkNotempty(edt_discount.getText().toString().trim())){

                        if (checkNotempty(tvFromdate.getText().toString().trim())){

                            if (checkNotempty(tvTodate.getText().toString().trim())){
                                apiAddVoucher();
                            }else {
                                MyToast.getInstance(AddVoucherCodeActivity.this).showDasuAlert("Please select end date");
                            }

                        }else {
                            MyToast.getInstance(AddVoucherCodeActivity.this).showDasuAlert("Please select voucher validity date");
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

    @Override
    public void onBackPressed() {
        KeyboardUtil.hideKeyboard(edt_discount, AddVoucherCodeActivity.this);
        KeyboardUtil.hideKeyboard(edt_voucher_code, AddVoucherCodeActivity.this);
        super.onBackPressed();
        finish();
    }

    private void datePicker(final String tag){
        // Get Current Date
        // if (tag.equals("from")) {
        final Calendar c = GregorianCalendar.getInstance();
        int  mYear = c.get(GregorianCalendar.YEAR);
        int  mMonth = c.get(GregorianCalendar.MONTH);
        int  mDay = c.get(GregorianCalendar.DAY_OF_MONTH);
        //}

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,R.style.DatePickerDialogTheme,
                (view, year, monthOfYear, dayOfMonth) -> {

                    if (tag.equals("from")) {
                        if (monthOfYear<10) {
                            fromDate = year + "-" + "0" +(monthOfYear + 1) + "-" + dayOfMonth;
                            tvFromdate.setText(dayOfMonth + "/" + "0" +(monthOfYear + 1) + "/" + year);
                        }else {
                            fromDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                            tvFromdate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                        }
                        // tvFromdate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                        tvTodate.setText("");

                    }
                    else {
                        if (monthOfYear<10) {
                            toDate = year + "-" + "0" +(monthOfYear + 1) + "-" + dayOfMonth;
                            tvTodate.setText(dayOfMonth + "/" + "0" + (monthOfYear + 1) + "/" + year);

                        } else {
                            toDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                            tvTodate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                        }
                    }


                }, mYear, mMonth, mDay);
        if (tag.equals("from")) {
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        }else {
            String sDate = CalanderUtils.formatDate(fromDate, Constants.SERVER_TIMESTAMP_FORMAT,Constants.SERVER_TIMESTAMP_FORMAT);
            Date cDate = CalanderUtils.getDateFormat(sDate, Constants.SERVER_TIMESTAMP_FORMAT);
            assert cDate != null;
            datePickerDialog.getDatePicker().setMinDate(cDate.getTime());
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
        if (sDiscountType.equals("Percentage(%)"))
            body.put("discountType", "2");// (1:Fix;2:percentage)
        else
            body.put("discountType", "1");

        body.put("amount", edt_discount.getText().toString().trim());
        body.put("startDate", fromDate);
        body.put("endDate", toDate);
        body.put("voucherCode", edt_voucher_code.getText().toString().trim());
        if (voucherCode!=null) {
            body.put("id", voucherCode._id);
            //  body.put("voucherCode", "");
        }
        else {
            //  body.put("voucherCode", edt_voucher_code.getText().toString().trim());
            body.put("id", "");
        }


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
