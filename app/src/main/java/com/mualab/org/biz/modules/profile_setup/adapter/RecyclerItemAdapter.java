package com.mualab.org.biz.modules.profile_setup.adapter;

import android.support.v7.widget.RecyclerView;

import com.mualab.org.biz.modules.profile_setup.db_modle.Services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public abstract class RecyclerItemAdapter extends RecyclerView.Adapter {

    List<Services> items = new ArrayList<>();
    RecyclerItemAdapter(){
        setHasStableIds(true);
    }


    public void add(Services object) {
        items.add(object);
        notifyDataSetChanged();
    }

    public void add(int index, Services object) {
        items.add(index, object);
        notifyDataSetChanged();
    }

    public void addAll(Collection collection) {
        if (collection != null) {
            items.addAll(collection);
            notifyDataSetChanged();
        }
    }

    public void addAll(Services... items) {
        addAll(Arrays.asList(items));
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public void remove(Services object) {
        items.remove(object);
        notifyDataSetChanged();
    }

}
