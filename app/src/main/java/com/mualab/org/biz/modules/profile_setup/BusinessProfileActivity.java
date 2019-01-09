package com.mualab.org.biz.modules.profile_setup;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.dialogs.NoConnectionDialog;
import com.mualab.org.biz.dialogs.Progress;
import com.mualab.org.biz.helper.Constants;
import com.mualab.org.biz.model.Address;
import com.mualab.org.biz.model.BusinessDay;
import com.mualab.org.biz.model.BusinessProfile;
import com.mualab.org.biz.model.SubCategory;
import com.mualab.org.biz.model.TimeSlot;
import com.mualab.org.biz.modules.MainActivity;
import com.mualab.org.biz.modules.base.BaseActivity;
import com.mualab.org.biz.modules.profile_setup.fragment.BusinessHoursFragmentCreation;
import com.mualab.org.biz.modules.profile_setup.fragment.FragmentAddStaff;
import com.mualab.org.biz.modules.profile_setup.fragment.FragmentListner;
import com.mualab.org.biz.modules.profile_setup.fragment.NewBusinessCategoryFragments.AddNewBusinessTypeFragment;
import com.mualab.org.biz.modules.profile_setup.fragment.NewBusinessCategoryFragments.AddNewCategoryFragment;
import com.mualab.org.biz.modules.profile_setup.fragment.NewBusinessCategoryFragments.AddedCategoryFragment;
import com.mualab.org.biz.modules.profile_setup.fragment.NewBusinessCategoryFragments.AddedServicesFragment;
import com.mualab.org.biz.modules.profile_setup.fragment.NewBusinessCategoryFragments.MyBusinessTypeFragment;
import com.mualab.org.biz.modules.profile_setup.fragment.OutcallOptionsFragmentCreation;
import com.mualab.org.biz.modules.profile_setup.fragment.StripAccountFragment;
import com.mualab.org.biz.modules.profile_setup.fragment.UploadCertificationFragment;
import com.mualab.org.biz.modules.profile_setup.fragment.ZoomOutPageTransformer;
import com.mualab.org.biz.session.PreRegistrationSession;
import com.mualab.org.biz.session.Session;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.ConnectionDetector;
import com.mualab.org.biz.util.Helper;
import com.mualab.org.biz.util.KeyboardUtil;
import com.mualab.org.biz.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import views.progressview.ProgressView;

public class BusinessProfileActivity extends BaseActivity implements FragmentListner {

    public static List<SubCategory> tmpSubCategory = new ArrayList<>();

    private List<MyViews> views = new ArrayList<>();
    private ProgressView progressView;
    private TextView tvHeaderText,tv_skip,tv_logout;
    private ImageView iv_back;
    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    public ViewPager mPager;
    private long mLastClickTime = 0;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    public PagerAdapter mPagerAdapter;
    SparseArray<Fragment> registeredFragments = new SparseArray<>();


    class MyViews{
        Fragment fragment;
        String title;

        MyViews(String title, Fragment fragment){
            this.title = title;
            this.fragment = fragment;
        }
    }

    private Session session;

    @Override
    public void onNext() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

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
        completProfile();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_profile);
        session = Mualab.getInstance().getSessionManager();
        tvHeaderText = findViewById(R.id.tvHeaderText);
        progressView = findViewById(R.id.progressView);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setCustomView(R.layout.actionbar_fill_business_profile);
            actionBar.setDisplayShowCustomEnabled(true);
        }

        Utils.showDebugDBAddressLogToast();


        iv_back = findViewById(R.id.iv_back);
        tv_skip = findViewById(R.id.tv_skip);
        tv_logout = findViewById(R.id.tv_logout);
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

        findViewById(R.id.tv_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Mualab.getInstance().getSessionManager().logout();

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

        tv_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(views.size()>mPager.getCurrentItem())
                    mPager.setCurrentItem(mPager.getCurrentItem() + 1);
            }
        });

    }

    private void viewDidLoad(){
        views.add(new MyViews("Business Hours", BusinessHoursFragmentCreation.newInstance()));
        views.add(new MyViews("Outcall Options", OutcallOptionsFragmentCreation.newInstance()));
        // views.add(new MyViews("Business Type", ServicesFragmentCreation.newInstance()));
        views.add(new MyViews("Business Type", MyBusinessTypeFragment.newInstance()));
        views.add(new MyViews("Add Business Type", AddNewBusinessTypeFragment.newInstance()));
        views.add(new MyViews("Service Category", AddedCategoryFragment.newInstance()));
        views.add(new MyViews("Add New Service Category", AddNewCategoryFragment.newInstance()));
        views.add(new MyViews("company_services", AddedServicesFragment.newInstance()));

        //  views.add(new MyViews("Business Type2", AddNewBusinessTypeActivity.newInstance()));
        //views.add(new MyViews("Categories", CategoriesFragmentCreation.newInstance()));
        // views.add(new MyViews("Sub Category", SubCategoriesFragment.newInstance()));
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
                tv_skip.setVisibility(View.GONE);
                tv_logout.setVisibility(View.GONE);
                iv_back.setVisibility(position==0?View.GONE:View.VISIBLE);

                progressView.setProgressIndex(position);
                switch (views.get(position).title) {
                    case "Add Business Type":
                        tv_logout.setVisibility(View.VISIBLE);
                        tvHeaderText.setText("Business Type");
                        break;
                    case "Add New Service Category":
                    case "Service Category":
                        tv_logout.setVisibility(View.VISIBLE);
                        tvHeaderText.setText("Service Category");
                        break;
                    default:
                        tvHeaderText.setText(views.get(position).title);
                        break;
                }

                KeyboardUtil.hideKeyboard(mPager,BusinessProfileActivity.this);

                if(views.get(position).title.equals("Banking Details") || views.get(position).title.equals("Upload Certification"))
                    tv_skip.setVisibility(View.VISIBLE);
                else if(views.get(position).title.equals("Add Staff") &&
                        session.getUser().businessType.equals(Constants.INDEPENDENT)){
                    onFinish();
                }
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
            // Back button. This calls finish() on this modules and pops the back stack.
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
                        //      pSession.updateServiceType(bsp.serviceType);
                        pSession.updateBankStatus(bsp.bankStatus);


                        JSONArray businessArray = obj.getJSONArray("businessHour");
                        if (businessArray.length()!=0){
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
                try {
                    Helper helper = new Helper();
                    if (helper.error_Messages(error).contains("Session")) {
                        Mualab.getInstance().getSessionManager().logout();
                        //      MyToast.getInstance(BookingActivity.this).showDasuAlert(helper.error_Messages(error));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
    public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

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

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }
    }

    private void completProfile(){
        Map<String,String> body = new HashMap<>();
        body.put("artistId", ""+Mualab.getInstance().getSessionManager().getUser().id);
        new HttpTask(new HttpTask.Builder(this, "skipPage", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                //   session.setBusinessProfileComplete(true);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }

            @Override
            public void ErrorListener(VolleyError error) {

            }
        }).setMethod(Request.Method.POST)
                .setBodyContentType( HttpTask.ContentType.APPLICATION_JSON)
                .setBody(body)
                .setProgress(true)
                .setAuthToken(session.getUser().authToken)).execute("skip");
    }

    @Override
    protected void onDestroy() {
        PreRegistrationSession preSession = new PreRegistrationSession(this);
        if (mPager!=null && mPager.getCurrentItem() == 0) {
            preSession.updateRegStep(0);
        }
        super.onDestroy();
    }

}
