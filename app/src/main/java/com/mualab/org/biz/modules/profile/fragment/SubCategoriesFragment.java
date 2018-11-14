package com.mualab.org.biz.modules.profile.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mualab.org.biz.R;
import com.mualab.org.biz.dialogs.NoConnectionDialog;
import com.mualab.org.biz.modules.profile.BusinessProfileActivity;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.model.Category;
import com.mualab.org.biz.model.Service;
import com.mualab.org.biz.model.SubCategory;
import com.mualab.org.biz.model.serializer.SubCategorySerializer;
import com.mualab.org.biz.session.PreRegistrationSession;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.ConnectionDetector;
import java.util.HashMap;
import java.util.Map;
import views.pickerview.MyTimePickerDialog;
import views.pickerview.timepicker.TimePicker;

public class SubCategoriesFragment extends ProfileCreationBaseFragment {

    //private Category category;
    private SubCategory subCategory;
    private TextView tv_categoryName, tvCompletionTime;
    private EditText edTagName, edDescription, edIncallPrice, edOutcallPrice;
    private LinearLayout ll_incallinput, ll_outCallInput;
    private boolean isUpdateCategory;
    private PreRegistrationSession bpSession;

    public static SubCategoriesFragment newInstance() {
        return new SubCategoriesFragment();
    }

    public void addSubCategory(Category category){
        //this.category = category;
        this.subCategory = new SubCategory();
        this.subCategory.categoryName = category.name;
        this.subCategory.categoryId = category.id;
        this.subCategory.serviceId = category.serviceId;
        this.subCategory.serviceName = category.serviceName;
        isUpdateCategory = false;
        updateView();
    }

    public void updateSubCategory(SubCategory subCategory){
        this.subCategory = subCategory;
        isUpdateCategory = true;
        updateView();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bpSession = Mualab.getInstance().getBusinessProfileSession();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_subcategory, container, false);
        tv_categoryName = view.findViewById(R.id.tv_categoryName);
        edTagName = view.findViewById(R.id.edTagName);
        edDescription = view.findViewById(R.id.edDescription);
        edIncallPrice = view.findViewById(R.id.edIncallPrice);
        edOutcallPrice = view.findViewById(R.id.edOutcallPrice);
        tvCompletionTime = view.findViewById(R.id.tvCompletionTime);
        ll_incallinput = view.findViewById(R.id.ll_incallinput);
        ll_outCallInput = view.findViewById(R.id.ll_outCallInput);
        return view;
    }

    private void updateView(){
        tv_categoryName.setText(subCategory.categoryName);
        edTagName.setText(subCategory.name);
        edDescription.setText(subCategory.description);
        edIncallPrice.setText("");
        edOutcallPrice.setText("");
        tvCompletionTime.setText(subCategory.completionTime);
        if(subCategory.inCallPrice>0) edIncallPrice.setText(String.format("%s", subCategory.inCallPrice));
        if(subCategory.outCallPrice>0) edOutcallPrice.setText(String.format("%s", subCategory.outCallPrice));

        if(bpSession.getServiceType()==1){
            ll_incallinput.setVisibility(View.VISIBLE);
            ll_outCallInput.setVisibility(View.GONE);
        }else if(bpSession.getServiceType()==2){
            ll_incallinput.setVisibility(View.GONE);
            ll_outCallInput.setVisibility(View.VISIBLE);
        }else if(bpSession.getServiceType()==2){
            ll_incallinput.setVisibility(View.VISIBLE);
            ll_outCallInput.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.btnAddMore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateInputData() && listener!=null){
                    insertServices(true);
                    //updateDtataIntoServer(true);
                    //listener.onPrev("Services");
                }

            }
        });

        view.findViewById(R.id.btnNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateInputData() && listener!=null){
                    updateDtataIntoServer(false);
                    //listener.onNext();
                }
            }
        });

        final TextView tvCompletionTime = view.findViewById(R.id.tvCompletionTime);
        tvCompletionTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPicker(tvCompletionTime, getString(R.string.time_for_completion));
            }
        });

    }



    public void showPicker(final TextView tvTime, final String title){
        int hours = 01;
        //int minute = 00;
        String tmpTime = tvTime.getText().toString();
        if(!tmpTime.equals("HH:MM")){
            String[] arrayTime = tmpTime.split(":");
            hours = Integer.parseInt(arrayTime[0]);
           // minute = Integer.parseInt(arrayTime[1]);
        }
        MyTimePickerDialog mTimePicker = new MyTimePickerDialog(mContext, new MyTimePickerDialog.OnTimeSetListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onTimeSet(TimePicker view, int hours, int minute) {
                tvTime.setText(String.format("%s:%s", String.format("%02d", hours), String.format("%02d", minute)));
            }
        }, title, hours, 00,10);
        mTimePicker.show();
    }


    private boolean validateInputData(){
        String tagName = edTagName.getText().toString().trim();
        String description = edDescription.getText().toString().trim();
        String inCallPrice = edIncallPrice.getText().toString().trim();
        String outCallPrice = edOutcallPrice.getText().toString().trim();
        String completionTime = tvCompletionTime.getText().toString().trim();

        if(TextUtils.isEmpty(tagName)){
            edTagName.requestFocus();
        }else if(TextUtils.isEmpty(description)){
            edDescription.requestFocus();
        }else if(bpSession.getServiceType()==1 && TextUtils.isEmpty(inCallPrice)){
            edIncallPrice.requestFocus();
        }else if(bpSession.getServiceType()==2 && TextUtils.isEmpty(outCallPrice)){
            edOutcallPrice.requestFocus();
        }else if(bpSession.getServiceType()==3 && TextUtils.isEmpty(inCallPrice)){
            edIncallPrice.requestFocus();
        }else if(completionTime.equals(getString(R.string.hh_mm)) || completionTime.equals("00:00")){
            edOutcallPrice.clearFocus();
        }else {
            subCategory.name = tagName;
            subCategory.description = description;
            subCategory.completionTime = completionTime;

            if(!TextUtils.isEmpty(inCallPrice))
                subCategory.inCallPrice = Double.parseDouble(inCallPrice);

            if(!TextUtils.isEmpty(outCallPrice))
                subCategory.outCallPrice = Double.parseDouble(outCallPrice);

            if(!isUpdateCategory)
                BusinessProfileActivity.tmpSubCategory.add(subCategory);
            //List<SubCategoryJoin> tmpSubCategory =
            //bpSession.addCategory(BusinessProfileActivity.tmpSubCategory);
            return true;
        }
        showToast(R.string.error_required_field);
        return false;
    }

    @SuppressLint("StaticFieldLeak")
    public void insertServices(final boolean addMoreService) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                Service service = Mualab.get().getDB().serviceDao().findById(subCategory.serviceId);
                if(service!=null){

                    Category category = service.getCategory(subCategory.categoryId);

                    if(category!=null){

                        SubCategory tmp = category.getSubCategory(subCategory.id);

                        if(tmp!=null){
                            category.deleteSubCategory(tmp);
                            category.addSubCategory(subCategory);

                        }else {
                            category.addSubCategory(subCategory);
                        }
                    }
                    Mualab.get().getDB().serviceDao().update(service);

                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                if(addMoreService)
                    listener.onChangeByTag("Services");
                else
                    listener.onNext();
            }
        }.execute();
    }


    /*update service data local db to server db*/
    private void updateDtataIntoServer(final boolean addMoreService){

        if(ConnectionDetector.isConnected()){

            Map<String,String> body = new HashMap<>();
            /*body.put("artistServiceId", subCategory.id!=-1?""+subCategory.id:"");
            body.put("serviceId", ""+subCategory.serviceId);
            body.put("subserviceId", ""+subCategory.categoryId);
            body.put("title", subCategory.name);
            body.put("description", subCategory.description);
            body.put("incallPrice", String.valueOf(subCategory.inCallPrice));
            body.put("outCallPrice", String.valueOf(subCategory.outCallPrice));
            body.put("completionTime", subCategory.completionTime);*/

            Gson gson = new GsonBuilder().registerTypeAdapter(SubCategory.class, new SubCategorySerializer()).create();
            body.put("artistService", gson.toJson(BusinessProfileActivity.tmpSubCategory));
            new HttpTask(new HttpTask.Builder(mContext, "addArtistService", new HttpResponceListner.Listener() {
                @Override
                public void onResponse(String response, String apiName) {
                    Log.d("res:", response);
                    bpSession.updateRegStep(5);
                    insertServices(addMoreService);
                }

                @Override
                public void ErrorListener(VolleyError error) {
                }})
                    .setMethod(Request.Method.POST)
                    .setParam(body)
                    .setBodyContentType( HttpTask.ContentType.APPLICATION_JSON)
                    .setBody(body)
                    .setProgress(true)
                    .setAuthToken(user.authToken)).execute("updateRange");
        }else{
            //showToast(R.string.error_msg_network);
            new NoConnectionDialog(mContext, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if(isConnected){
                        dialog.dismiss();
                    }
                }
            }).show();
        }
    }

}
