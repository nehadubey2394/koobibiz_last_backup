package com.mualab.org.biz.model.add_staff;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Mindiii on 2/6/2018.
 */

public class ArtistServices implements Serializable{
    public String _id;
    public String title;
    public String completionTime;
    public String inCallPrice;
    public String outCallPrice;
    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
    public boolean isOutCall3;
}