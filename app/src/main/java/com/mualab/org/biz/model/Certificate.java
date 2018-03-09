package com.mualab.org.biz.model;

/**
 * Created by dharmraj on 2/2/18.
 */

public class Certificate {

    public int id;
    public int status;
    public boolean isSelected;
    public String imageUri;

    public Certificate(){

    }

    public Certificate(int id){
        this.id = id;
    }

}
