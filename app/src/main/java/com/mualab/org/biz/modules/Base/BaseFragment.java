package com.mualab.org.biz.modules.Base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.mualab.org.biz.R;
import com.mualab.org.biz.modules.BaseActivity;
import com.mualab.org.biz.modules.MainActivity;

/**
 * Created by dharmraj on 19/3/18.
 */

public class BaseFragment extends Fragment {

    public static final String ARGS_INSTANCE = "com.mualab.org.biz";

    private BaseActivity mActivity;
    protected Callback mFragmentNavigation;
    protected Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof BaseActivity) {
            BaseActivity activity = (BaseActivity) context;
            this.mActivity = activity;
         //   activity.onFragmentAttached();
        }

        if (context instanceof Callback) {
            mFragmentNavigation = (Callback) context;
        }

    }

    @Override
    public void onDetach() {
        mActivity = null;
        super.onDetach();
    }

    public BaseActivity getBaseActivity() {
        return mActivity;
    }


    public boolean isNetworkConnected() {
        return mActivity != null && mActivity.isNetworkConnected();
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public interface Callback {

        void onFragmentAttached();

        void onFragmentDetached(String tag);

        void pushFragment(Fragment fragment);
    }
}
