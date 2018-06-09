package com.mualab.org.biz.modules.profile.fragment;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mualab.org.biz.R;

import com.mualab.org.biz.modules.profile.BusinessProfileActivity;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.model.Category;
import com.mualab.org.biz.model.ParseService;
import com.mualab.org.biz.model.Service;
import com.mualab.org.biz.model.SubCategory;
import com.mualab.org.biz.model.serializer.SubCategorySerializer;
import com.mualab.org.biz.session.PreRegistrationSession;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.ConnectionDetector;

import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dharmraj on 18/1/18.
 **/

public class ServicesFragmentCreation extends ProfileCreationBaseFragment {

    private ProgressBar pbLoder;
    private AppCompatButton btnNext;
    private ServiceAdapter adapter;
    private PreRegistrationSession bpSession;
    private List<Service> serviceList;
    //private List<ArtistCategory> actualServiceList;

    public static ServicesFragmentCreation newInstance() {
        return new ServicesFragmentCreation();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bpSession = Mualab.getInstance().getBusinessProfileSession();
        if(serviceList==null) serviceList = new ArrayList<>();
        //if(actualServiceList==null) actualServiceList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_services, container, false);
        pbLoder = view.findViewById(R.id.pbLoder);
        btnNext = view.findViewById(R.id.btnNext);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ListView listViewServices = view.findViewById(R.id.listViewServices);

        // Create the adapter to convert the array to views
        adapter = new ServiceAdapter(serviceList);
        listViewServices.setAdapter(adapter);

        listViewServices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BusinessProfileActivity activity = ((BusinessProfileActivity) getActivity());
                if(listener!=null && activity!=null){
                    ViewPager viewPager = (getActivity()) != null ? activity.mPager : null;
                    PagerAdapter adapter = (getActivity()) != null ? activity.mPagerAdapter : null;
                    if (adapter != null && viewPager!=null) {
                        CategoriesFragmentCreation fragment = (CategoriesFragmentCreation) adapter.instantiateItem(null, viewPager.getCurrentItem()+1);

                        for(Category category: serviceList.get(position).categorys){
                            category.serviceName = serviceList.get(position).name;
                        }
                        fragment.updateView(serviceList.get(position).categorys);
                        //listener.saveTmpData("tmpService", serviceList.get(position));
                        listener.onNext();
                    }
                }
               /* if((getActivity() != null ? ((BusinessProfileActivity) getActivity()).fragmentChangeListner : null) !=null){
                    BusinessProfileActivity.tmpData.put("tmpService", serviceList.get(position));
                    //((BusinessProfileActivity) getActivity()).fragmentChangeListner.onNext();
                    listener.saveTmpData("tmpService", serviceList.get(position));
                }*/
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateDtataIntoServer();
               /* bpSession.updateRegStep(5);
                listener.onChangeByTag("Upload Certification");*/
            }
        });


        if(serviceList.size()>0){
            getAllSerVice();
        }else {
            retrieveServices();
        }
    }


    private void margeServiceAndSubCategory(List<SubCategory> serverSubCategory){
        BusinessProfileActivity.tmpSubCategory.clear();
        //BusinessProfileActivity.tmpSubCategory = serverSubCategory!=null?serverSubCategory:bpSession.getBusinessProfile().subCategories;

        if(serverSubCategory!=null)
            BusinessProfileActivity.tmpSubCategory.addAll(serverSubCategory);
       // else BusinessProfileActivity.tmpSubCategory = bpSession.getBusinessProfile().subCategories;

        for(SubCategory subCategory : BusinessProfileActivity.tmpSubCategory){

            for(Service service : serviceList){

                if(service.id == subCategory.serviceId ){
                    Category category = service.getCategory(subCategory.categoryId);
                    if(category!=null){
                        category.serviceName = service.name;
                        category.addSubCategory(subCategory);
                    }
                }
            }
        }
    }


    private void retrieveServices(){
        pbLoder.setVisibility(View.VISIBLE);
        Map<String,String> header = new HashMap<>();
        Map<String,String> body = new HashMap<>();
        header.put("authToken", user.authToken);
        //header.put("authToken", user.authToken);
        HttpTask task = new HttpTask(new HttpTask.Builder(mContext, "allService", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    pbLoder.setVisibility(View.GONE);
                    serviceList.clear();

                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");
                    if (status.equalsIgnoreCase("success")) {
                        Gson gson = new Gson();
                        ParseService tmp = gson.fromJson(response, ParseService.class);
                        serviceList.addAll(tmp.serviceList);
                        margeServiceAndSubCategory(tmp.subCategory);
                        insertServices();
                        tmp.serviceList.clear();
                        tmp = null;
                        populateServices();
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                try{
                    pbLoder.setVisibility(View.GONE);
                    //Log.d("res", error.getLocalizedMessage());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }})
                .setMethod(Request.Method.POST)
                .setHeader(header)
                .setBody(body, HttpTask.ContentType.X_WWW_FORM_URLENCODED));
        pbLoder.setVisibility(View.VISIBLE);
        task.execute("allService");
    }

    private void populateServices(){
        adapter.notifyDataSetChanged();
        btnNext.setVisibility(BusinessProfileActivity.tmpSubCategory.size()>0?View.VISIBLE:View.GONE);
    }


    @SuppressLint("StaticFieldLeak")
    public void insertServices() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
               // Mualab.get().getDB().serviceDao().deleteAll();
                Mualab.get().getDB().serviceDao().insertAll(serviceList);
                return null;
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void getAllSerVice() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                serviceList.clear();
                serviceList.addAll(Mualab.get().getDB().serviceDao().getAll());
                //margeServiceAndSubCategory(null);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                populateServices();
            }
        }.execute();
    }



    class ServiceAdapter extends BaseAdapter {

        private List<Service> serviceList;

        ServiceAdapter(List<Service> serviceList){
            this.serviceList = serviceList;
        }

        @Override
        public int getCount() {
            return serviceList.size();
        }

        @Override
        public Service getItem(int position) {
            return serviceList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Service service = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_adapter_servicelist, parent, false);
                holder = new ViewHolder();
                holder.tvName = convertView.findViewById(R.id.tv_ServiceName);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            // Lookup view for data population
            //final TextView tvName = convertView.findViewById(R.id.tv_ServiceName);
            //tvName.setTextColor(ContextCompat.getColor(mContext, R.color.black));
            // Populate the data into the template view using the data object
            if (service != null) {
                holder.tvName.setText(service.name);
            }
            // Return the completed view to render on screen
            return convertView;
        }
    }

    private static class ViewHolder{
        private TextView tvName;
    }


    /*update service data local db to server db*/
    private void updateDtataIntoServer(){

        if(ConnectionDetector.isConnected()){
            Map<String,String> body = new HashMap<>();
            Gson gson = new GsonBuilder().registerTypeAdapter(SubCategory.class, new SubCategorySerializer()).create();
            body.put("artistService", gson.toJson(BusinessProfileActivity.tmpSubCategory));
            new HttpTask(new HttpTask.Builder(mContext, "addArtistService", new HttpResponceListner.Listener() {
                @Override
                public void onResponse(String response, String apiName) {
                    Log.d("res:", response);
                    bpSession.updateRegStep(5);
                    listener.onChangeByTag("Upload Certification");
                    //insertServices(addMoreService);
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
        }else showToast(R.string.error_msg_network);
    }
}
