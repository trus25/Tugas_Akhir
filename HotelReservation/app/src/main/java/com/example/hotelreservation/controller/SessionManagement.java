package com.example.hotelreservation.controller;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.hotelreservation.model.User;

public class SessionManagement {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String SHARED_PREF_NAME = "session";
    String SESSION_KEY = "session_userid";
    String SESSION_USERNAME = "session_username";
    String SESSION_USERPASSWORD = "session_userpassword";



    public SessionManagement(Context context){
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveSession(User user){
        int id = user.getId();
        String username = user.getUsername();
        String password = user.getPassword();

        editor.putString(SESSION_USERNAME,username);
        editor.putString(SESSION_USERPASSWORD, password);
        editor.putInt(SESSION_KEY,id);
        editor.apply();
    }

    public int getSession(){
        return sharedPreferences.getInt(SESSION_KEY,-1);
    }

    public String getUsername(){
        return sharedPreferences.getString(SESSION_USERNAME, "-1");
    }

    public String getPassword(){
        return sharedPreferences.getString(SESSION_USERPASSWORD,"-1");
    }

    public void removeSession() {
        editor.putInt(SESSION_KEY, -1);
        editor.putString(SESSION_USERNAME,"-1");
        editor.putString(SESSION_USERPASSWORD, "-1");
        editor.apply();
    }
}
