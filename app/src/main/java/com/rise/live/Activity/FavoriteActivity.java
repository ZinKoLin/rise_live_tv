package com.rise.live.Activity;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.rise.live.Utils.Constant.BANNER_HOME;
import static com.rise.live.Utils.Constant.INTERSTITIAL_POST_LIST;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.rise.live.Adapter.ChannelAdapter;
import com.rise.live.Model.Channel;
import com.rise.live.R;
import com.rise.live.Utils.AdNetwork;
import com.rise.live.Utils.AdsPref;
import com.rise.live.Utils.PrefManager;

import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChannelAdapter favoriteAdapter;
    RelativeLayout NoQuotes;
    private ArrayList<Channel> imageArry = new ArrayList<Channel>();
    List<Channel> favoriteLists= MainActivity.favoriteDatabase.favoriteDao().getFavoriteData();
    PrefManager prf;
    Toolbar toolbar;
    private final String TAG = FavoriteActivity.class.getSimpleName();
    AdNetwork adNetwork;
    AdsPref adsPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);


        toolbar = findViewById(R.id.toolbar);
        setTitle("Favorite");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        prf = new PrefManager(this);
        adsPref = new AdsPref(this);
        adNetwork = new AdNetwork(this);
        adNetwork.loadBannerAdNetwork(BANNER_HOME);
        adNetwork.loadInterstitialAdNetwork(INTERSTITIAL_POST_LIST);

        NoQuotes = findViewById(R.id.noFavorite);

        recyclerView=(RecyclerView)findViewById(R.id.rec);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, prf.getInt("wallpaperColumns")));
        getFavData();

        favoriteAdapter.setOnItemClickListener(new ChannelAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Channel obj, int position) {
                Intent intent = new Intent(FavoriteActivity.this, DetailsActivity.class);
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

    private void getFavData() {


        for (Channel cn : favoriteLists) {

            imageArry.add(cn);
        }

        if (imageArry.isEmpty()){

            NoQuotes.setVisibility(View.VISIBLE);

        }

        favoriteAdapter = new ChannelAdapter(getApplicationContext(),favoriteLists);
        recyclerView.setAdapter(favoriteAdapter);
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
            //This Admin panel and WallpaperX app Created by YMG Developers
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);   // set status text dark
        }
    }
}