package com.mualab.org.biz.activity.profile;

import android.app.Dialog;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.mualab.org.biz.R;
import com.mualab.org.biz.activity.BaseActivity;
import com.mualab.org.biz.activity.MainActivity;
import com.mualab.org.biz.activity.authentication.LoginActivity;
import com.mualab.org.biz.activity.profile.fragment.BusinessHoursFragmentCreation;
import com.mualab.org.biz.activity.profile.fragment.CategoriesFragmentCreation;
import com.mualab.org.biz.activity.profile.fragment.FragmentAddStaff;
import com.mualab.org.biz.activity.profile.fragment.FragmentListner;
import com.mualab.org.biz.activity.profile.fragment.OutcallOptionsFragmentCreation;
import com.mualab.org.biz.activity.profile.fragment.ServicesFragmentCreation;
import com.mualab.org.biz.activity.profile.fragment.StripAccountFragment;
import com.mualab.org.biz.activity.profile.fragment.SubCategoriesFragment;
import com.mualab.org.biz.activity.profile.fragment.UploadCertificationFragment;
import com.mualab.org.biz.activity.profile.fragment.ZoomOutPageTransformer;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.dialogs.NoConnectionDialog;
import com.mualab.org.biz.dialogs.Progress;
import com.mualab.org.biz.model.Address;
import com.mualab.org.biz.model.BusinessDay;
import com.mualab.org.biz.model.BusinessProfile;
import com.mualab.org.biz.model.SubCategory;
import com.mualab.org.biz.model.TimeSlot;
import com.mualab.org.biz.session.PreRegistrationSession;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.ConnectionDetector;
import com.mualab.org.biz.util.KeyboardUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import views.progressview.ProgressView;

public class BusinessProfileActivity extends BaseActivity implements FragmentListner {

    public static List<SubCategory> tmpSubCategory = new ArrayList<>();

    private List<MyViews> views = new ArrayList<>();
    private ProgressView progressView;

    private TextView tvHeaderText;
    private ImageView iv_back;
    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    public ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    public PagerAdapter mPagerAdapter;

    class MyViews{
        Fragment fragment;
        String title;

        MyViews(String title, Fragment fragment){
            this.title = title;
            this.fragment = fragment;
        }
    }


    @Override
    public void onNext() {
        if(views.size()>mPager.getCurrentItem())
            mPager.setCurrentItem(mPager.getCurrentItem() + 1);
    }

    @Override
    public void onPrev() {
        if(mPager.getCurrentItem() != 0)
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
    }

    @Override
    public void onChangeByTag(String tag) {
        if(mPager.getCurrentItem() != 0 && !TextUtils.isEmpty(tag)){
            for(int i=0; i<views.size(); i++){
                MyViews tmpView = views.get(i);
                if(tmpView.title.equals(tag)){
                    mPager.setCurrentItem(i);
                    break;
                }
            }
        }
    }

    @Override
    public void onFinish() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_profile);
        tvHeaderText = findViewById(R.id.tvHeaderText);
        progressView = findViewById(R.id.progressView);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setCustomView(R.layout.actionbar_fill_business_profile);
            actionBar.setDisplayShowCustomEnabled(true);
        }



        iv_back = findViewById(R.id.iv_back);
        iv_back.setVisibility(View.GONE);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        findViewById(R.id.iv_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(views.size()>mPager.getCurrentItem())
                    mPager.setCurrentItem(mPager.getCurrentItem() + 1);
            }
        });


        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if(isConnected){
                        dialog.dismiss();
                        getBusinessProfile();
                    }
                }
            }).show();
        }else {
            getBusinessProfile();
        }

    }


    private void viewDidLoad(){
        views.add(new MyViews("Business Hours", BusinessHoursFragmentCreation.newInstance()));
        views.add(new MyViews("Outcall Options", OutcallOptionsFragmentCreation.newInstance()));
        views.add(new MyViews("Services", ServicesFragmentCreation.newInstance()));
        views.add(new MyViews("Categories", CategoriesFragmentCreation.newInstance()));
        views.add(new MyViews("Sub Categories", SubCategoriesFragment.newInstance()));
        views.add(new MyViews("Upload Certification", UploadCertificationFragment.newInstance()));

        if(Mualab.getInstance().getBusinessProfileSession().getBankStatus()!=1)
            views.add(new MyViews("Banking Details", StripAccountFragment.newInstance()));
        views.add(new MyViews("Add Staff", FragmentAddStaff.newInstance()));

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setPageTransformer(true, new ZoomOutPageTransformer());
        mPager.setAdapter(mPagerAdapter);

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                iv_back.setVisibility(position==0?View.GONE:View.VISIBLE);
                progressView.setProgressIndex(position);
                tvHeaderText.setText(views.get(position).title);
                KeyboardUtil.hideKeyboard(mPager,BusinessProfileActivity.this);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mPager.canScrollHorizontally(0);
        progressView.setProgressViewCount(views.size());
        int index = Mualab.getInstance().getBusinessProfileSession().getStepIndex();
        mPager.setCurrentItem(index);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //MenuInflater inflater = getMenuInflater();
       // inflater.inflate(R.menu.logout_menu, menu);
       // MenuItem searchViewItem = menu.findItem(R.id.action_search);
        //inflater.inflate(R.menu.business_profile_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.iv_back:
                Mualab.getInstance().getSessionManager().logout();
                finish();
                return true;
            case R.id.action_logout:
                    Mualab.getInstance().getSessionManager().logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
           // mPager.setCurrentItem(mPager.getCurrentItem() - 1);
            int position = mPager.getCurrentItem();
            if(position != 0){
                if(position==4 || position==5){
                    mPager.setCurrentItem(2);
                }else mPager.setCurrentItem(mPager.getCurrentItem() - 1);
            }
        }
    }


    private void getBusinessProfile(){

        new HttpTask(new HttpTask.Builder(this, "getbusinessProfile", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("artistRecord");

                    if(jsonArray.length()>0){
                        PreRegistrationSession pSession = Mualab.getInstance().getBusinessProfileSession();
                        JSONObject obj = jsonArray.getJSONObject(0);
                        BusinessProfile bsp = new BusinessProfile();

                        bsp.address = new Address();
                        bsp.address.postalCode = obj.getString("businesspostalCode");
                        bsp.address.stAddress1 = obj.getString("address");
                        bsp.address.stAddress2 = obj.getString("address2");
                        bsp.address.city = obj.getString("city");
                        bsp.address.state = obj.getString("state");
                        bsp.address.country = obj.getString("country");
                        bsp.address.latitude = obj.getString("latitude");
                        bsp.address.longitude = obj.getString("longitude");

                        bsp.bio = obj.getString("bio");
                        bsp.bankStatus = obj.getInt("bankStatus");

                        if(obj.has("radius")){
                            String r = obj.getString("radius");
                            if(!TextUtils.isEmpty(r))
                                bsp.radius = obj.getInt("radius");
                        }

                        if(obj.has("serviceType"))
                            bsp.serviceType = obj.getInt("serviceType");
                        if(obj.has("inCallpreprationTime"))
                            bsp.inCallpreprationTime = obj.getString("inCallpreprationTime");
                        if(obj.has("outCallpreprationTime"))
                            bsp.outCallpreprationTime = obj.getString("outCallpreprationTime");

                        pSession.updateRadius(bsp.radius);
                        pSession.updateAddress(bsp.address);
                        pSession.updateOutcallPreprationTime(bsp.outCallpreprationTime);
                        pSession.updateIncallPreprationTime(bsp.inCallpreprationTime);
                        pSession.updateServiceType(bsp.serviceType);
                        pSession.updateBankStatus(bsp.bankStatus);


                        JSONArray businessArray = obj.getJSONArray("businessHour");
                        bsp.businessDays = getBusinessDay();
                        for(int i =0; i<businessArray.length();  i++){
                            JSONObject objSlots = businessArray.getJSONObject(i);
                            int dayId = objSlots.getInt("day");

                            TimeSlot slot = new TimeSlot(dayId);
                            slot.id = objSlots.getInt("_id");
                            slot.startTime = objSlots.getString("startTime");
                            slot.endTime = objSlots.getString("endTime");
                            slot.status = objSlots.getInt("status");

                            for(BusinessDay tmpDay : bsp.businessDays){
                                if(tmpDay.dayId == dayId){
                                    tmpDay.isOpen = true;
                                    tmpDay.addTimeSlot(slot);
                                    break;
                                }
                            }
                        }

                        if(businessArray.length()>0)
                            pSession.createBusinessProfile(bsp);

                        viewDidLoad();
                    }
                    Progress.hide(BusinessProfileActivity.this);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Progress.hide(BusinessProfileActivity.this);
                }

            }

            @Override
            public void ErrorListener(VolleyError error) {
                // Log.d("", error.getLocalizedMessage());
                Progress.hide(BusinessProfileActivity.this);
            }})
                .setMethod(Request.Method.GET)
                .setAuthToken(Mualab.getInstance().getSessionManager().getAuthToken()))
                .execute("getbusinessProfile");
        Progress.show(BusinessProfileActivity.this);
    }

    private List<BusinessDay> getBusinessDay(){

        List<BusinessDay>businessDays = new ArrayList<>();

        BusinessDay day1 = new BusinessDay();
        day1.dayName = getString(R.string.monday);
        day1.dayId = 0;
        //day1.addTimeSlot(new TimeSlot(1));

        BusinessDay day2 = new BusinessDay();
        day2.dayName = getString(R.string.tuesday);
        day2.dayId = 1;

        BusinessDay day3 = new BusinessDay();
        day3.dayName = getString(R.string.wednesday);
        day3.dayId = 2;

        BusinessDay day4 = new BusinessDay();
        day4.dayName = getString(R.string.thursday);
        day4.dayId = 3;

        BusinessDay day5 = new BusinessDay();
        day5.dayName = getString(R.string.frieday);
        day5.dayId = 4;

        BusinessDay day6 = new BusinessDay();
        day6.dayName = getString(R.string.saturday);
        day6.dayId = 5;

        BusinessDay day7 = new BusinessDay();
        day7.dayName = getString(R.string.sunday);
        day7.dayId = 6;

        businessDays.add(day1);
        businessDays.add(day2);
        businessDays.add(day3);
        businessDays.add(day4);
        businessDays.add(day5);
        businessDays.add(day6);
        businessDays.add(day7 );
        return businessDays;
    }


    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        private ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return views.get(position).fragment;
        }

        @Override
        public int getCount() {
            return views.size();
        }
    }
}
