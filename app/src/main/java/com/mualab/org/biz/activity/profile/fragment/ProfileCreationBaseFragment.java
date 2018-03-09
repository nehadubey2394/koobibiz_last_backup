package com.mualab.org.biz.activity.profile.fragment;

import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.text.TextUtils;

import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.session.Session;

/**
 * Created by dharmraj on 22/1/18.
 **/

public class ProfileCreationBaseFragment extends Fragment{

    protected Context mContext;
    protected FragmentListner listener;
    protected User user;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mContext = context;

        if(user==null) user = Mualab.getInstance().getSessionManager().getUser(); // get session object

        if (context instanceof FragmentListner) { //check having instance of listner or not
            listener = (FragmentListner) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement FragmentListner");
        }
    }

    protected void showToast(String msg){
        if(!TextUtils.isEmpty(msg))
            MyToast.getInstance(mContext).showDasuAlert(msg);
        //MyToast.getInstance(mContext).showSmallCustomToast(msg);

    }


    protected void showToast(@StringRes int id){
        MyToast.getInstance(mContext).showDasuAlert(getString(id));
        //MyToast.getInstance(mContext).showSmallCustomToast(msg);
    }

}
