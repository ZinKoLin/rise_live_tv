package com.rise.live.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rise.live.R;
import com.rise.live.Utils.PrefManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PrivacyPolicyActivity extends AppCompatActivity {

    TextView textView;
    RelativeLayout relativeLayoutLoadMore;
    PrefManager prefManager;
    private DatabaseReference dbCategories , quote;
    private final String TAG = PrivacyPolicyActivity.class.getSimpleName();
    //This FireTV app Created by YMG Developers and G-devs

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitle(getResources().getString(R.string.policy_privacy));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        prefManager = new PrefManager(this);

        textView = findViewById(R.id.textView);
        relativeLayoutLoadMore = findViewById(R.id.relativeLayoutLoadMore);

        relativeLayoutLoadMore.setVisibility(View.VISIBLE);
        getPrivacyPolicy();
        initCheck();

    }

    private void getPrivacyPolicy() {

        quote = FirebaseDatabase.getInstance().getReference("policy");
        quote.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    relativeLayoutLoadMore.setVisibility(View.GONE);
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String name = ds.getKey();
                        String policy = ds.child("desc").getValue(String.class);

                        textView.setText(policy);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    private void initCheck() {
        if (prefManager.loadNightModeState()){
            Log.d("Dark", "MODE");
        }else {
            //This Admin panel and WallpaperX app Created by YMG Developers
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);   // set status text dark
        }
    }
}