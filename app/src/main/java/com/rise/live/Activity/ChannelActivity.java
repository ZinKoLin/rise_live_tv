package com.rise.live.Activity;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.rise.live.Utils.Constant.BANNER_HOME;
import static com.rise.live.Utils.Constant.INTERSTITIAL_POST_LIST;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rise.live.Adapter.ChannelAdapter;
import com.rise.live.Model.Channel;
import com.rise.live.R;
import com.rise.live.Utils.AdNetwork;
import com.rise.live.Utils.AdsPref;
import com.rise.live.Utils.PrefManager;

import java.util.ArrayList;
import java.util.List;

public class ChannelActivity extends AppCompatActivity {

    PrefManager prf;
    Toolbar toolbar;
    RecyclerView rvWallpaper;
    DatabaseReference wallpaperReference;
    List<Channel> wallpaperList, favList;
    ChannelAdapter channelAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    String categoryName;
    private final String TAG = ChannelActivity.class.getSimpleName();
    AdNetwork adNetwork;
    AdsPref adsPref;
    View lyt_no_item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channels);
        prf = new PrefManager(this);

        Intent intent = getIntent();
        categoryName = intent.getStringExtra("G-Devs");

        toolbar = findViewById(R.id.toolbar);
        setTitle(categoryName);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        adsPref = new AdsPref(this);
        adNetwork = new AdNetwork(this);
        adNetwork.loadBannerAdNetwork(BANNER_HOME);
        adNetwork.loadApp(prf.getString("VDN"));
        adNetwork.loadInterstitialAdNetwork(INTERSTITIAL_POST_LIST);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        lyt_no_item = findViewById(R.id.lyt_no_item);
        showRefresh(true);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });

        loadPictures();

        channelAdapter.setOnItemClickListener(new ChannelAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Channel obj, int position) {
                Intent intent = new Intent(ChannelActivity.this, DetailsActivity.class);
                intent.putExtra("name", obj.getChannelName());
                intent.putExtra("image", obj.getChannelImage());
                intent.putExtra("type", obj.getChannelType());
                intent.putExtra("link", obj.getChannelLink());
                intent.putExtra("desc", obj.getChannelDesc());
                intent.putExtra("category", obj.getChannelCategory());
                intent.putExtra("language", obj.getChannelLanguage());
                intent.putExtra("id", obj.getId());
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                showInterstitialAd();
            }
        });
    }

    public void showInterstitialAd() {
        adNetwork.showInterstitialAdNetwork(INTERSTITIAL_POST_LIST, adsPref.getInterstitialAdInterval());
    }

    private void loadPictures() {

        favList = new ArrayList<>();
        wallpaperList = new ArrayList<>();
        rvWallpaper = findViewById(R.id.recyclerView);
        rvWallpaper.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,prf.getInt("wallpaperColumns"));
        rvWallpaper.setLayoutManager(gridLayoutManager);
        channelAdapter = new ChannelAdapter(this,wallpaperList);
        rvWallpaper.setAdapter(channelAdapter);

        wallpaperReference = FirebaseDatabase.getInstance().getReference("Channels");

        fetchWallpapers(categoryName);
    }

    private void fetchWallpapers(final String categoryName) {
        wallpaperReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                showRefresh(false);
                if (dataSnapshot.exists()) {
                    for (DataSnapshot wallpaperSnapshot : dataSnapshot.getChildren()) {
                        lyt_no_item.setVisibility(View.GONE);
                        int id  = wallpaperSnapshot.child("id").getValue(int.class);
                        String wallpaper = wallpaperSnapshot.child("channelImage").getValue(String.class);
                        String name = wallpaperSnapshot.child("channelName").getValue(String.class);
                        String category = wallpaperSnapshot.child("channelCategory").getValue(String.class);
                        String type = wallpaperSnapshot.child("channelType").getValue(String.class);
                        String link = wallpaperSnapshot.child("channelLink").getValue(String.class);
                        String desc = wallpaperSnapshot.child("channelDesc").getValue(String.class);
                        String language = wallpaperSnapshot.child("channelLanguage").getValue(String.class);

                        Channel wallpaper1 = new Channel(id, wallpaper, name, category, type,link, desc,language);

                        favList.add(0,wallpaper1);
                    }
                    for (int i = 0; i < favList.size(); i++) {
                        if (favList.get(i).getChannelCategory().equals(categoryName)) {
                            wallpaperList.add(favList.get(i));
                            if (wallpaperList.size() == 0){
                                Toast.makeText(ChannelActivity.this, "NO DATA", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                    if (wallpaperList.size() == 0){
                        lyt_no_item.setVisibility(View.VISIBLE);
                    }
                    channelAdapter.notifyDataSetChanged();
                }else {
                    lyt_no_item.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ChannelActivity.this, "Empty", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void refreshData() {

        wallpaperList.clear();

        channelAdapter.notifyDataSetChanged();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadPictures();
            }
        }, 2000);
    }

    private void showRefresh(boolean show) {
        if (show) {
            swipeRefreshLayout.setRefreshing(true);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }, 500);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initCheck();
    }

    private void initCheck() {
        if (prf.loadNightModeState()){
            Log.d("Dark", "MODE");
        }else {
            //This Application is Created by YMG Developers and G-Devs
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);   // set status text dark
        }
    }
}