package com.mualab.org.biz.modules.business_setup.new_add_staff.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mualab.org.biz.R;
import com.mualab.org.biz.model.company_management.CompanyDetail;

import java.util.List;


public class CompanyListSppinnerAdapter extends BaseAdapter {

    private Context context;
    private List<CompanyDetail> businessTypes;

    public CompanyListSppinnerAdapter(Context context, List<CompanyDetail> businessTypes) {
        this.context = context;
        this.businessTypes = businessTypes;
    }

    @Override
    public int getCount() {
        return businessTypes.size();
    }

    @Override
    public Object getItem(int position) {
        return businessTypes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;
        ViewHolder holder;


        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            row = inflater.inflate(R.layout.company_list_spinner_items, parent, false);

            holder = new ViewHolder();
            /*find layout components id*/
            holder.tvSpItem = row.findViewById(R.id.tvSpItem);
            holder.view = row.findViewById(R.id.view);
            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }


        final CompanyDetail item = businessTypes.get(position);

        //  holder.tvSpItem.setText(item.serviceName);

        holder.tvSpItem.setText("");
        holder.view.setVisibility(View.GONE);

        return row;

    }
    private class ViewHolder {
        TextView tvSpItem;
        View view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        // return super.getDropDownView(position, convertView, parent);
        View row = convertView;
        ViewHolder holder;


        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            row = inflater.inflate(R.layout.company_list_spinner_items, parent, false);

            holder = new ViewHolder();
            /*find layout components id*/
            holder.tvSpItem = row.findViewById(R.id.tvSpItem);
            holder.view = row.findViewById(R.id.view);


            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }

        final CompanyDetail item = businessTypes.get(position);
        holder.tvSpItem.setText(item.businessName);
        holder.view.setVisibility(View.VISIBLE);

        return row;
    }

}

