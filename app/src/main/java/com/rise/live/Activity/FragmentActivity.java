package com.rise.live.Activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.rewarded.RewardedAd;
import com.rise.live.R;

public class FragmentActivity extends AppCompatActivity {
    private RewardedAd mRewardedAd;
    private static final String TAG = "FragmentActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home);


    }
}


