package com.mualab.org.biz.model.add_staff;


import java.io.Serializable;

/**
 * Created by Mindiii on 2/6/2018.
 */

public class ArtistServices implements Serializable{
    public String _id;
    public String title;
    public String isInCallEdited,isOutCallEdited;
    public String completionTime,editedCtime;
    public String inCallPrice,editedInCallP;
    public String outCallPrice,editedOutCallP;
    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
    public boolean isOutCall3;
}