package com.rise.live.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.util.Base64;
import android.view.View;
import android.view.Window;

import java.io.UnsupportedEncodingException;

public class PrefManager {

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String PREF_NAME = "status_app";
   // public static final String URL = "https://yashchouhan.com/verify/api.php?nid=";
    int PRIVATE_MODE = 0;
    String TAG_NIGHT_MODE = "nightmode";
    Context _context;
    SharedPreferences.Editor editor;
    SharedPreferences pref;



    public PrefManager(Context context) {
        this._context = context;
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, 0);
        this.pref = sharedPreferences;
        this.editor = sharedPreferences.edit();
    }

    public void setBoolean(String str, Boolean bool) {
        this.editor.putBoolean(str, bool.booleanValue());
        this.editor.commit();
    }

    public void setString(String str, String str2) {
        this.editor.putString(str, str2);
        this.editor.commit();
    }

    public static String decodeString(String encoded) {
        byte[] dataDec = Base64.decode(encoded, Base64.DEFAULT);
        String decodedString = "";
        try {

            decodedString = new String(dataDec, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

        } finally {

            return decodedString;
        }
    }

    public void setInt(String str, int i) {
        this.editor.putInt(str, i);
        this.editor.commit();
    }

    public void setStatusColor(Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public boolean getBoolean(String str) {
        return this.pref.getBoolean(str, true);
    }

    public void remove(String str) {
        if (this.pref.contains(str)) {
            this.editor.remove(str);
            this.editor.commit();
        }
    }

    public String getString(String str) {
        return this.pref.contains(str) ? this.pref.getString(str, null) : "";
    }

    public int getInt(String str) {
        return this.pref.getInt(str, 3);
    }

    public void setDarkMode(String str) {
        this.editor.putString(this.TAG_NIGHT_MODE, str);
        this.editor.apply();
    }

    //save
    public void setNightModeState(Boolean state){
        SharedPreferences.Editor editor= pref.edit();
        editor.putBoolean("NightMode",state);
        editor.commit();
    }

    //load
    public Boolean loadNightModeState(){
        Boolean state = pref.getBoolean("NightMode",false);
        return state;
    }

}
