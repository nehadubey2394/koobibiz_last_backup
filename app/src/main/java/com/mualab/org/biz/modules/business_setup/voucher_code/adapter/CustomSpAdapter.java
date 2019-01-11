package com.mualab.org.biz.modules.business_setup.voucher_code.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mualab.org.biz.R;

import java.util.List;


public class CustomSpAdapter extends BaseAdapter {

    private Context context;
    private List<String> stringList;

    public CustomSpAdapter(Context context, List<String> stringList) {
        this.context = context;
        this.stringList = stringList;
    }

    @Override
    public int getCount() {
        return stringList.size();
    }

    @Override
    public Object getItem(int position) {
        return stringList.get(position);
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
            row = inflater.inflate(R.layout.layout_voucher_sp_items, parent, false);

            holder = new ViewHolder();
            /*find layout components id*/
            holder.tvSpItem = row.findViewById(R.id.tvSpItem);
            holder.ivDropDown = row.findViewById(R.id.ivDropDown);
            holder.ivDropDown.setVisibility(View.VISIBLE);

            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }

        final String item = stringList.get(position);
        holder.tvSpItem.setText(item);
        holder.tvSpItem.setTextAppearance(context, R.style.TextView_Reguler_Medium);

     /*   if (position==0) {
            final String item = stringList.get(position);
            holder.tvSpItem.setText(item);
        }else holder.tvSpItem.setText("");
*/
        return row;

    }
    private class ViewHolder {
        TextView tvSpItem;
        ImageView ivDropDown;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        // return super.getDropDownView(position, convertView, parent);
        View row = convertView;
        ViewHolder holder;

        if (stringList.get(position).equals("Discount Type")) {
            row = new View(context);
            row.setTag("Extra");
            return row;
        }

        if (row == null || row.getTag().equals("Extra")) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            row = inflater.inflate(R.layout.layout_voucher_sp_items, parent, false);

            holder = new ViewHolder();
            /*find layout components id*/
            holder.tvSpItem = row.findViewById(R.id.tvSpItem);
            holder.ivDropDown = row.findViewById(R.id.ivDropDown);

            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }

        final String item = stringList.get(position);
        holder.tvSpItem.setText(item);

        return row;
    }

}

