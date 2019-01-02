package com.mualab.org.biz.modules.profile_setup.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mualab.org.biz.R;
import com.mualab.org.biz.modules.profile_setup.modle.AddedCategory;

import java.util.List;


public class CategorySppinnerAdapter extends ArrayAdapter {

    private Context context;
    private List<AddedCategory> addedCategories;

    /*public CategorySppinnerAdapter(Context context, List<AddedCategory> addedCategories) {
        this.context = context;
        this.addedCategories = addedCategories;
    }*/

    public CategorySppinnerAdapter(@NonNull Context activity, List<AddedCategory> addedCategories) {
        super(activity, R.layout.custom_spinner_items);
        this.context = activity;
        this.addedCategories = addedCategories;
    }

    @Override
    public int getCount() {
        return addedCategories.size();
    }

    @Override
    public Object getItem(int position) {
        return addedCategories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {

        View row = convertView;
        ViewHolder holder;


        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.custom_spinner_items, parent, false);

            holder = new ViewHolder();
            /*find layout components id*/
            holder.tvSpItem = row.findViewById(R.id.tvSpItem);

            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }


        final AddedCategory item = addedCategories.get(position);

      //  holder.tvSpItem.setText(item.subServiceName);
        holder.tvSpItem.setText("");
        return row;

    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        // return super.getDropDownView(position, convertView, parent);
        View row = convertView;
        ViewHolder holder;


        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            row = inflater.inflate(R.layout.custom_spinner_items, parent, false);

            holder = new ViewHolder();
            /*find layout components id*/
            holder.tvSpItem = row.findViewById(R.id.tvSpItem);
            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }

        final AddedCategory item = addedCategories.get(position);
        holder.tvSpItem.setText(item.subServiceName);


        return row;
    }

    private class ViewHolder {
        TextView tvSpItem;
    }
}

