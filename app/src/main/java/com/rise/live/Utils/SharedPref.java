package com.rise.live.Utils;


import android.content.Context;
import android.content.SharedPreferences;


public class SharedPref {

    private Context mContext;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    public SharedPref(Context context) {
        this.mContext = context;
        sharedPreferences = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public Boolean getIsDarkTheme() {
        return sharedPreferences.getBoolean("theme", false);
    }



    public void setIsDarkTheme(Boolean isDarkTheme) {
        editor.putBoolean("theme", isDarkTheme);
        editor.apply();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return sharedPreferences.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void saveConfig(String api_url, String application_id) {
        editor.putString("api_url", api_url);
        editor.putString("application_id", application_id);
        editor.apply();
    }

    public String getApiUrl() {
        return sharedPreferences.getString("api_url", "http://example.com");
    }

    public String getApplicationId() {
        return sharedPreferences.getString("application_id", "");
    }

    //0 for Most popular
    //1 for Date added (oldest)
    //2 for Date added (newest)
    public void setDefaultSortHome() {
        editor.putInt("sort", 2);
        editor.apply();
    }

    public Integer getCurrentSortHome() {
        return sharedPreferences.getInt("sort", 0);
    }

    public void updateSortHome(int position) {
        editor.putInt("sort", position);
        editor.apply();
    }

    //0 for Most popular
    //1 for Date added (oldest)
    //2 for Date added (newest)
    public void setDefaultSortVideos() {
        editor.putInt("sort_act", 2);
        editor.apply();
    }

    public Integer getCurrentSortVideos() {
        return sharedPreferences.getInt("sort_act", 0);
    }

    public void updateSortVideos(int position) {
        editor.putInt("sort_act", position);
        editor.apply();
    }


    public void updateCategoryViewType(int position) {
        editor.putInt("category_list", position);
        editor.apply();
    }

}
