package com.mualab.org.biz.chat.model;

import java.io.Serializable;

public class FirebaseUser implements Serializable {
    public String firebaseToken,profilePic,userName;
    public int uId,isOnline;
    public Object lastActivity;
}
