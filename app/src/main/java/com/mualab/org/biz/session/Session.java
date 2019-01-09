package com.mualab.org.biz.session;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Base64;

import com.google.gson.Gson;
import com.mualab.org.biz.model.add_staff.StaffDetail;
import com.mualab.org.biz.modules.authentication.LoginActivity;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.model.BusinessDay;
import com.mualab.org.biz.model.User;

import java.io.UnsupportedEncodingException;

public class Session {

    private static final String PREF_NAME = "userSession";
    private static final String PREF_NAME2 = "appSession";

    private static final String IS_LOGGEDIN = "isLoggedIn";
    private static final String IS_BisinessProfileComplete = "isBisinessProfileComplete";

    private static final String IS_FIrebaseLogin = "isFirebaseLogin";
    private static final String IS_UPDATE_UID = "isUpdateUid";
    private Context _context;
    private SharedPreferences mypref, mypref2;
    private SharedPreferences.Editor editor, editor2;

    public Session(Context context) {
        this._context = context;
        mypref = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        mypref2 = _context.getSharedPreferences(PREF_NAME2, Context.MODE_PRIVATE);
        editor = mypref.edit();
        editor2 = mypref2.edit();
        editor.apply();
        editor2.apply();
    }


    public boolean isUpdateUid() {
        return mypref.getBoolean(IS_UPDATE_UID, false);
    }

    public void setUpdateUid(boolean isUpdate) {
        editor.putBoolean(IS_UPDATE_UID, isUpdate);
        editor.commit();
    }

   /* public void createSession(FirebaseUser user) {
        Gson gson = new Gson();
        String json = gson.toJson(user); // myObject - instance of MyObject
        editor.putString("userSession", json);
        editor.putBoolean(IS_LOGGEDIN, true);
        editor.putString("authToken", user.authToken);
        editor.commit();
    }*/

    public void createSession(User user) {
        createSession(user, false);
    }

    public void createSession(User user, boolean isFirebaseLogin) {
        Gson gson = new Gson();
        String json = gson.toJson(user); // myObject - instance of MyObject
        editor.putString("user", json);
        editor.putBoolean(IS_LOGGEDIN, true);
        editor.putBoolean(IS_FIrebaseLogin, isFirebaseLogin);
        editor.putString("authToken", user.authToken);
        Mualab.getInstance().getBusinessProfileSession().updateAddress(user.getBusinessAddress());
        editor.apply();
    }

    public void setPassword(String pwd) {
        try {
            byte[] data = pwd.getBytes("UTF-8");
            String base64 = Base64.encodeToString(data, Base64.DEFAULT);
            editor.putString("pwd", base64);
            editor.apply();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public String getPassword(){
        // Receiving side
        try {
            byte[] data = Base64.decode(mypref.getString("pwd", null), Base64.DEFAULT);
            return new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }


    public User getUser() {
        Gson gson = new Gson();
        String string = mypref.getString("user", "");
        if (!string.isEmpty())
            return gson.fromJson(string, User.class);
        else return null;
    }

    public StaffDetail getStaffInfo() {
        Gson gson = new Gson();
        String string = mypref.getString("satffInfo", "");
        if (!string.isEmpty())
            return gson.fromJson(string, StaffDetail.class);
        else return null;
    }

    public BusinessDay getBusinessHours() {
        Gson gson = new Gson();
        String string = mypref.getString("businessHours", "");
        if (!string.isEmpty())
            return gson.fromJson(string, BusinessDay.class);
        else return null;
    }

    public void setBusinessHours(BusinessDay businessHour) {
        Gson gson = new Gson();
        String json = gson.toJson(businessHour); // myObject - instance of MyObject
        editor.putString("businessHours", json);
        editor.apply();
    }


    public String getAuthToken() {
        return mypref.getString("authToken", "");
    }

    public boolean getIsFirebaseLogin() {
        return mypref.getBoolean(IS_FIrebaseLogin, false);
    }


    public void logout() {
        editor.clear();
        editor.apply();
        new ClearTask().execute("");
       /* try {
            FirebaseInstanceId.getInstance().deleteInstanceId();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        Mualab.getInstance().getBusinessProfileSession().clearSession();
        Intent showLogin = new Intent(_context, LoginActivity.class);
        showLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        showLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            Mualab.getInstance().getDB().clearAllTables();
        }catch (Exception e){

        }

        _context.startActivity(showLogin);
    }


    private class ClearTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            Mualab.getInstance().getDB().clearAllTables();
            return null;
        }

        @Override
        protected void onPostExecute(String result) { }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {

        }
    }

    public boolean isLoggedIn() {
        return mypref.getBoolean(IS_LOGGEDIN, false);
    }


    public void setBusinessProfileComplete(boolean isComplete) {
        editor.putBoolean(IS_BisinessProfileComplete, isComplete);
        editor.apply();
    }

    public boolean isBusinessProfileComplete() {
        return mypref.getBoolean(IS_BisinessProfileComplete, false);
    }

}
