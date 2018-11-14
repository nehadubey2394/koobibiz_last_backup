package com.mualab.org.biz.modules.company_management.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.model.company_management.CompanyDetail;
import com.mualab.org.biz.modules.company_management.fragments.ServiesFragment;
import com.mualab.org.biz.session.Session;
import com.mualab.org.biz.util.StatusBarUtil;

public class CompanyServicesActivity extends AppCompatActivity {
    public CompanyDetail companyDetail;
    private TextView tvHeaderTitle;

    public void setTitle(String text){
        if(tvHeaderTitle!=null)
            tvHeaderTitle.setText(text);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_services);
    //    StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary));
        init();
    }

    public CompanyDetail getCompanyDetail(){
        return companyDetail;
    }

    private void init(){
        Intent intent = getIntent();
        if (intent!=null){
            Bundle args = intent.getBundleExtra("BUNDLE");
            companyDetail = (CompanyDetail) args.getSerializable("companyDetail");
        }
        Session session = Mualab.getInstance().getSessionManager();
        User user = session.getUser();

        ImageView ivHeaderBack = findViewById(R.id.ivHeaderBack);
        tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        tvHeaderTitle.setText(user.businessName);
        ivHeaderBack.setVisibility(View.VISIBLE);

        ivHeaderBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        addFragment(ServiesFragment.newInstance(""), false,R.id.flServiceContainer);
    }

    /* frangment replace code */
    public void addFragment(Fragment fragment, boolean addToBackStack, int containerId) {
        String backStackName = fragment.getClass().getName();
        FragmentManager fragmentManager = getSupportFragmentManager();
        boolean fragmentPopped = fragmentManager.popBackStackImmediate(backStackName, 0);
        if (!fragmentPopped) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_in,0,0);
            transaction.add(containerId, fragment, backStackName);
            if (addToBackStack)
                transaction.addToBackStack(backStackName);
            transaction.commit();
        }

    }

    @Override
    public void onBackPressed() {
        // Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.flStffContainer);

        FragmentManager fm = getSupportFragmentManager();
        int i = fm.getBackStackEntryCount();
        if (i > 0) {
            fm.popBackStack();
        }
        else  {
            finish();
            super.onBackPressed();
        }
    }
}
