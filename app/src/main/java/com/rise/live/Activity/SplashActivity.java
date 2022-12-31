package com.rise.live.Activity;

import static com.rise.live.Config.ADMOB_PUB_ID;
import static com.rise.live.Config.AD_STATUS;
import static com.rise.live.Config.AD_TYPE;
import static com.rise.live.Config.APPLOVIN_BANNER_ID;
import static com.rise.live.Config.APPLOVIN_INTER_ID;
import static com.rise.live.Config.BANNER_ID;
import static com.rise.live.Config.DEVELOPER_NAME;
import static com.rise.live.Config.FACEBOOK_BANNER_ID;
import static com.rise.live.Config.FACEBOOK_INTER_ID;
import static com.rise.live.Config.INTERSTITIAL_ADS_INTERVAL;
import static com.rise.live.Config.INTER_ID;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.rise.live.Config;
import com.rise.live.R;
import com.rise.live.Utils.AdsPref;
import com.rise.live.Utils.Constant;
import com.rise.live.Utils.PrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    PrefManager prefManager;
    AdsPref adsPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        prefManager = new PrefManager(this);
        adsPref = new AdsPref(this);

        getData();
    }



    private void getConfig() {
        adsPref.saveAds(
                AD_STATUS,
                AD_TYPE,
                ADMOB_PUB_ID,
                ADMOB_PUB_ID,
                BANNER_ID,
                INTER_ID,
                DEVELOPER_NAME,
                DEVELOPER_NAME,
                FACEBOOK_BANNER_ID,
                FACEBOOK_INTER_ID,
                DEVELOPER_NAME,
                DEVELOPER_NAME,
                DEVELOPER_NAME,
                DEVELOPER_NAME,
                DEVELOPER_NAME,
                APPLOVIN_BANNER_ID,
                APPLOVIN_INTER_ID,
                INTERSTITIAL_ADS_INTERVAL,
                INTERSTITIAL_ADS_INTERVAL,
                INTERSTITIAL_ADS_INTERVAL,
                prefManager.getString("VDN"),
                ""
        );
    }

    private void getData() {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, Constant.URL_API_DATA, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    final String VDN = response.getString("DN");
                    prefManager.setString("VDN",VDN);
                    getConfig();
                    Timer myTimer = new Timer();
                    myTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            // If you want to modify a view in your Activity
                            SplashActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(SplashActivity.this,MainActivity.class));
                                    finish();

                                }
                            });
                        }
                    }, 1000);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        MyApplication.getInstance().addToRequestQueue(jsonObjReq);
    }


    public void onResume() {
        super.onResume();
        if (prefManager.loadNightModeState()==true){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        if (!Config.DEVELOPER_NAME.equals(Constant.DEVELOPER_NAME)){
            finish();
        }
        initCheck();
    }

    private void initCheck() {
        if (prefManager.loadNightModeState()){
            Log.d("Dark", "MODE");
        }else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);   // set status text dark
        }
    }
}

