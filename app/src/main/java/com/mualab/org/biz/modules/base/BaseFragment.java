package com.mualab.org.biz.modules.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

public class BaseFragment extends Fragment {

    private BaseActivity mActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity) {
            mActivity = (BaseActivity) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    protected BaseActivity getBaseActivity() {
        return mActivity;
    }

    /*protected AppDataManager getDataManager() {
        return Mualab.getDataManager();
    }*/

    protected void hideKeyboard() {
        if (mActivity != null) {
            mActivity.hideKeyboard();
        }
    }

    protected void setLoading(Boolean isLoading) {
        if (mActivity != null) {
            mActivity.setLoading(isLoading);
        }
    }

    protected Boolean isNetworkConnected() {
        return mActivity != null && mActivity.isNetworkConnected();
    }

    protected String getAddressFromLatLng(Double latitude, Double longitude) {
        if (mActivity != null) {
            return mActivity.getAddressFromLatLng(latitude, longitude);
        } else return "";
    }

}

