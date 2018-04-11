package com.mualab.org.biz.module.add_staff.cv_popup;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mualab.org.biz.R;
import com.mualab.org.biz.module.add_staff.listner.PoppupWithListListner;

import java.util.ArrayList;

public class CustomPopupWindow {

    private PoppupWithListListner poppupWithListListner;


    public void poppup_window_with_list(Context context,PoppupWithListListner hourListners,View v,ArrayList<String> strings) {
        this.poppupWithListListner = hourListners;
        android.widget.PopupWindow popupWindow = new android.widget.PopupWindow(context);
        ListView listViewSort = new ListView(context);
        listViewSort.setBackgroundColor(Color.parseColor("#ffffff"));
        listViewSort.setScrollBarFadeDuration(100);
        listViewSort.setOnItemClickListener(onItemClickListener(popupWindow, strings));
        popupWindow.setFocusable(true);
        popupWindow.setWidth(560);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(listViewSort);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.popup_text_view,
                strings);
        listViewSort.setAdapter(adapter);
        int x = (int) v.getX();
        int y = (int) v.getY();
       // popupWindow.showAtLocation(v, Gravity.CENTER_HORIZONTAL, 0, 0);
        popupWindow.showAsDropDown(v);

    }

    private AdapterView.OnItemClickListener onItemClickListener(final android.widget.PopupWindow popupWindow, final ArrayList<String> strings) {
        return new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {

                strings.get(position);
                popupWindow.dismiss();
                poppupWithListListner.selectedoption(position);

            }
        };
    }

    public static Rect locateView(View v) {
        int[] loc_int = new int[2];
        if (v == null) return null;
        try {
            v.getLocationOnScreen(loc_int);
        } catch (NullPointerException npe) {
            //Happens when the view doesn't exist on screen anymore.
            return null;
        }
        Rect location = new Rect();
        location.left = loc_int[0];
        location.top = loc_int[1];
        location.right = location.left + v.getWidth();
        location.bottom = location.top + v.getHeight();
        return location;
    }


}