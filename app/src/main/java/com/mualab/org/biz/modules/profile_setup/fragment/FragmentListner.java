package com.mualab.org.biz.modules.profile_setup.fragment;

/**
 * Created by dharmraj on 18/1/18.
 */

public interface FragmentListner {
    void onNext();
    void onPrev();
    void onChangeByTag(String Tag);
    void onFinish();
    //void saveTmpData(String key, Object value);
}
