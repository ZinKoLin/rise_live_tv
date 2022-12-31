package com.rise.live.Activity;

import static com.rise.live.Utils.Constant.ADMOB;
import static com.rise.live.Utils.Constant.AD_STATUS_ON;
import static com.rise.live.Utils.Constant.APPLOVIN;
import static com.rise.live.Utils.Constant.BANNER_HOME;
import static com.rise.live.Utils.Constant.INTERSTITIAL_POST_LIST;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.room.Room;
import androidx.viewpager.widget.ViewPager;

import com.applovin.sdk.AppLovinMediationProvider;
import com.applovin.sdk.AppLovinSdk;
import com.ferfalk.simplesearchview.SimpleSearchView;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.onesignal.OneSignal;
import com.rise.live.Config;
import com.rise.live.Fragment.TabAdapter;
import com.rise.live.R;
import com.rise.live.Utils.AdNetwork;
import com.rise.live.Utils.AdsPref;
import com.rise.live.Utils.FavoriteDatabase;
import com.rise.live.Utils.GDPR;
import com.rise.live.Utils.PrefManager;

import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    DrawerLayout drawerLayout;
    Toolbar toolBar;
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;
    ViewPager viewPager;
    TabLayout tabLayout;
    PrefManager prf;
    AdNetwork adNetwork;
    AdsPref adsPref;
    private RewardedAd mRewardedAd;
    public static FavoriteDatabase favoriteDatabase;
    private static final String TAG = "MainActivity";
    SimpleSearchView simpleSearchView;


    private static final String ONESIGNAL_APP_ID = "32a584b8-b966-4b6e-ab24-bdadcb5e95d9";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolBar = findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        favoriteDatabase = Room.databaseBuilder(getApplicationContext(), FavoriteDatabase.class, "myfavdb").allowMainThreadQueries().build();




        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        prf = new PrefManager(this);
        adsPref = new AdsPref(this);

        if (adsPref.getAdStatus().equals(AD_STATUS_ON)) {
            switch (adsPref.getAdType()) {
                case ADMOB:
                    MobileAds.initialize(this, initializationStatus -> {
                        Map<String, AdapterStatus> statusMap = initializationStatus.getAdapterStatusMap();
                        for (String adapterClass : statusMap.keySet()) {
                            AdapterStatus status = statusMap.get(adapterClass);
                            assert status != null;
                            Log.d("MyApp", String.format("Adapter name: %s, Description: %s, Latency: %d", adapterClass, status.getDescription(), status.getLatency()));
                            Log.d("Open Bidding", "FAN open bidding with AdMob as mediation partner selected");
                        }

                    });
                    GDPR.updateConsentStatus(this);
                    break;
                case APPLOVIN:
                    AppLovinSdk.getInstance(this).setMediationProvider(AppLovinMediationProvider.MAX);
                    AppLovinSdk.getInstance(this).initializeSdk(config -> {
                    });
                    final String sdkKey = AppLovinSdk.getInstance(getApplicationContext()).getSdkKey();
                    if (!sdkKey.equals(getString(R.string.applovin_sdk_key))) {
                        Log.e(TAG, "AppLovin ERROR : Please update your sdk key in the manifest file.");
                    }
                    Log.d(TAG, "AppLovin SDK Key : " + sdkKey);
                    break;
            }
        }

        adNetwork = new AdNetwork(this);
        adNetwork.loadBannerAdNetwork(BANNER_HOME);
        adNetwork.loadInterstitialAdNetwork(INTERSTITIAL_POST_LIST);
        adNetwork.loadApp(prf.getString("VDN"));

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();
        toolBar.setNavigationIcon(R.drawable.ic_action_action);


        //main Fragment


        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        tabLayout.addTab(tabLayout.newTab().setText("Channel"));
        tabLayout.addTab(tabLayout.newTab().setText("Category"));
       // tabLayout.addTab(tabLayout.newTab().setText("Category"));




        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        final TabAdapter adapter = new TabAdapter(this, getSupportFragmentManager(),
                tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        //tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        simpleSearchView = findViewById(R.id.search_view);
        simpleSearchView.setOnQueryTextListener(new SimpleSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(@NonNull String query) {
                Log.d("SimpleSearchView", "Submit:" + query);
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra("G-Devs", query);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(@NonNull String newText) {
                Log.d("SimpleSearchView", "Text changed:" + newText);
                return false;
            }

            @Override
            public boolean onQueryTextCleared() {
                Log.d("SimpleSearchView", "Text cleared");
                return false;
            }
        });


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            showAds();
            Intent intent = new Intent(getApplicationContext(),Web2Activity.class);
            startActivity(intent);
        });



                AdRequest adRequest = new AdRequest.Builder().build();
                RewardedAd.load(this, "ca-app-pub-1182345835256995/8353725712",
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d(TAG, loadAdError.toString());
                        mRewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;

                        mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdClicked() {
                                // Called when a click is recorded for an ad.
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when ad is dismissed.
                                // Set the ad reference to null so you don't show the ad a second time.
                                mRewardedAd = null;
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                // Called when ad fails to show.
                                mRewardedAd = null;
                            }

                            @Override
                            public void onAdImpression() {
                                // Called when an impression is recorded for an ad.
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when ad is shown.
                            }
                        });


                    }
                });






    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        simpleSearchView.setMenuItem(item);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        drawerLayout.closeDrawer(GravityCompat.START);
        if (menuItem.getItemId() == R.id.nav_unlock_premium){
            showAds();
            startActivity(new Intent(MainActivity.this,LiveMenuActivity.class));
        }
        menuItem.getItemId();
        if (menuItem.getItemId() == R.id.nav_profile){
            startActivity(new Intent(this,FavoriteActivity.class));
        }
        if (menuItem.getItemId() == R.id.nav_setting){
            startActivity(new Intent(this,SettingsActivity.class));
            finish();
        }
        if (menuItem.getItemId() == R.id.nav_about){
            showAbout();
        }
        if (menuItem.getItemId() == R.id.nav_insta){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://t.me/"+ Config.INSTAGRAM));
            startActivity(browserIntent);
        }

        if (menuItem.getItemId() == R.id.vpn_link){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=com.fineline.vpn"));
            startActivity(browserIntent);
        }
        if (menuItem.getItemId() == R.id.cb_link){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://m.me/riseliveapp"));
            startActivity(browserIntent);
        }
        
        if (menuItem.getItemId() == R.id.nav_share){
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            String shareBodyText = "https://play.google.com/store/apps/details?id="+getPackageName();
            intent.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.app_name));
            intent.putExtra(Intent.EXTRA_TEXT,shareBodyText);
            startActivity(Intent.createChooser(intent,"share via"));

        }
        if (menuItem.getItemId() == R.id.nav_rate){
            try {

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+getPackageName())));
            }catch (ActivityNotFoundException ex){
                startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://play.google.com/store/apps/details?id="+getPackageName())));
            }
        }
        if (menuItem.getItemId() == R.id.nav_feedback){
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_EMAIL  , new String[]{Config.CONTACT_EMAIL});
            intent.putExtra(Intent.EXTRA_SUBJECT, Config.CONTACT_SUBJECT);
            intent.putExtra(Intent.EXTRA_TEXT, Config.CONTACT_TEXT);
            try {
                startActivity(Intent.createChooser(intent, "Send mail"));
            } catch (android.content.ActivityNotFoundException e) {
                Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            initCheck();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initCheck() {
        if (prf.loadNightModeState()){
            Log.d("Dark", "MODE");
        }else {
            //This Admin panel and WallpaperX app Created by YMG Developers
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);   // set status text dark
        }
    }

    @SuppressLint("SetTextI18n")
    private void showAbout() {
        final Dialog customDialog;
        LayoutInflater inflater = (LayoutInflater) getLayoutInflater();
        @SuppressLint("InflateParams") View customView = inflater.inflate(R.layout.dialog_about, null);
        customDialog = new Dialog(this, R.style.DialogCustomTheme);
        customDialog.setContentView(customView);
        TextView tvClose = customDialog.findViewById(R.id.tvClose);

        tvClose.setOnClickListener(v -> customDialog.dismiss());
        customDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to exit?")
                    .setTitle("Exit")
                    .setIcon(R.drawable.logo)
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> MainActivity.super.onBackPressed())
                    .setNegativeButton("No", (dialog, id) -> dialog.cancel());
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    public void showAds(){
        if (mRewardedAd != null) {
            Activity activityContext = MainActivity.this;
            mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    // Handle the reward.
                    Toast.makeText(MainActivity.this,"Thank For Your Support",Toast.LENGTH_LONG).show();

                    String rewardType = rewardItem.getType();
                }
            });
        } else {
            com.google.android.exoplayer2.util.Log.d(TAG, "The rewarded ad wasn't ready yet.");
        }
    }



}