package com.mualab.org.biz.model;

import java.io.Serializable;

/**
 * Created by dharmraj on 2/2/18.
 */

public class Certificate implements Serializable {
/*"title":"first one",
"certificateImage":"http://koobi.co.uk:8042/uploads/certificateImage/1545975395061.jpg",
"description":"new one tixitziggi og ohoh o h igg iig ig i g igoh ig y",
"status":1,
"crd":"2018-12-27T11:24:01.192Z",
"upd":"2018-12-27T11:24:01.192Z",
"_id":3,
"artistId":1,
"__v":0*/
    public int _id;
    public int status;
    public boolean isSelected;
    public String title,description,crd,upd,artistId,__v,imageUri,certificateImage;

    public Certificate(){

    }

    public Certificate(int id){
        this._id = id;
    }

}
