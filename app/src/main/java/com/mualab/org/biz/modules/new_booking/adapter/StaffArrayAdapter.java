package com.mualab.org.biz.modules.new_booking.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mualab.org.biz.R;
import com.mualab.org.biz.model.booking.Staff;

import java.util.List;

/**
 * Created by hemant.
 * Date: 7/6/18
 * Time: 4:03 PM
 */

public class StaffArrayAdapter extends android.widget.ArrayAdapter {

    public int selectedPos = 0;
    private Activity activity;
    private List<Staff> list;

    public StaffArrayAdapter(@NonNull Activity activity, List<Staff> list) {
        super(activity, R.layout.spinner);
        this.activity = activity;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup viewGroup) {
        Staff bean = list.get(position);

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(activity);
            view = inflater.inflate(R.layout.spinner, viewGroup, false);
        }

        TextView tvSpinnerName = view.findViewById(R.id.tvName);

        tvSpinnerName.setText(bean.staffName);

        return view;
    }

    @Nullable
    @Override
    public View getDropDownView(int position, @Nullable View view, @NonNull ViewGroup viewGroup) {

        if (selectedPos == position) {
            view = new View(activity);
            view.setTag("Extra");
            return view;
        }

        Staff bean = list.get(position);

        if (view == null || view.getTag().equals("Extra")) {
            LayoutInflater inflater = LayoutInflater.from(activity);
            view = inflater.inflate(R.layout.spinner, viewGroup, false);
            view.setTag("");
        }

        TextView tvSpinnerName = view.findViewById(R.id.tvName);
        tvSpinnerName.setText(bean.staffName);

        tvSpinnerName.setPadding(10, 0, 0, 0);

        return view;
    }


}


