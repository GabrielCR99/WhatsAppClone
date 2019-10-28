package com.cursoandroid.whatsappclone.helper;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class Preferences {

    private SharedPreferences.Editor editor;
    private final String FILE_NAME = "whatsAppPreferences";
    private final int MODE = 0;
    private SharedPreferences sharedPreferences;

    private final String ID_KEY = "signedInUserIdentifier";
    private final String NAME_KEY = "userNameIdentifier";

    public Preferences(Context contextParameter) {


        sharedPreferences = contextParameter.getSharedPreferences(FILE_NAME, MODE);
        editor = sharedPreferences.edit();
    }

    public void saveData(String userIdentifier, String userName) {
        editor.putString(ID_KEY, userIdentifier);
        editor.putString(NAME_KEY, userName);

        editor.commit();

    }

    public String getIdentifier(){
        return sharedPreferences.getString(ID_KEY, null);
    }

    public String getNome(){
        return sharedPreferences.getString(NAME_KEY, null);
    }


}
