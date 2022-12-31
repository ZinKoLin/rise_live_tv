package com.rise.live.Activity;

import static com.rise.live.Utils.Constant.BANNER_HOME;
import static com.rise.live.Utils.Constant.INTERSTITIAL_POST_LIST;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.rise.live.Model.Channel;
import com.rise.live.R;
import com.rise.live.Utils.AdNetwork;
import com.rise.live.Utils.AdsPref;
import com.rise.live.Utils.PrefManager;

public class DetailsActivity extends AppCompatActivity {

    TextView tvName, tvDesc, tvCategory;
    String channelName;
    String channelImage;
    String channelType;
    String channelLink;
    String channelDesc;
    String channelCategory;
    String channelLanguage;
    int channelid;
    PrefManager prf;
    private Menu mMenuItem;
    @SuppressLint("StaticFieldLeak")
    public static ProgressBar progressBar;
    ImageView imageView;
    boolean fullscreen = false;
    RelativeLayout channelPlay;
    ScrollView scrollView;
    AdNetwork adNetwork;
    AdsPref adsPref;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        prf = new PrefManager(this);
        adsPref = new AdsPref(this);
        adNetwork = new AdNetwork(this);
        adNetwork.loadBannerAdNetwork(BANNER_HOME);
        adNetwork.loadApp(prf.getString("VDN"));
        adNetwork.loadInterstitialAdNetwork(INTERSTITIAL_POST_LIST);

        Intent intent = getIntent();
        channelName = intent.getStringExtra("name");
        channelImage = intent.getStringExtra("image");
        channelType = intent.getStringExtra("type");
        channelLink = intent.getStringExtra("link");
        channelDesc = intent.getStringExtra("desc");
        channelCategory = intent.getStringExtra("category");
        channelLanguage = intent.getStringExtra("language");
        channelid = intent.getIntExtra("id",0);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitle(channelName);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        tvName = findViewById(R.id.channelName);
        tvDesc = findViewById(R.id.channelDetails);
        tvCategory = findViewById(R.id.channelCategory);
        imageView = findViewById(R.id.channelImage);
        imageView = findViewById(R.id.channelImage);
        channelPlay = findViewById(R.id.channelPlay);

        tvName.setText(channelName);
        tvDesc.setText(channelDesc);
        tvCategory.setText("Category : "+channelCategory);
        Glide.with(this)
                .load(channelLanguage)
                .placeholder(R.drawable.placeholder)
                .into(imageView);

        channelPlay.setOnClickListener(view -> {
            if (channelType.equals("youtube")){
                Intent intents = new Intent(DetailsActivity.this,ActivityYoutubePlayer.class);
                intents.putExtra("name", channelName);
                intents.putExtra("image", channelLanguage);
                intents.putExtra("link", channelLink);
                startActivity(intents);
            }else {
                Intent intents = new Intent(DetailsActivity.this,PlayerActivity.class);
                intents.putExtra("name", channelName);
                intents.putExtra("image", channelLanguage);
                intents.putExtra("link", channelLink);
                startActivity(intents);
            }

        });
    }
    @Override
    public boolean onCreateOptionsMenu (Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.favorite, menu);
        mMenuItem = menu;

        if (MainActivity.favoriteDatabase.favoriteDao().isFavorite(channelid) == 1)
            mMenuItem.getItem(0).setIcon(R.drawable.ic_favorite_fill);
        else
            mMenuItem.getItem(0).setIcon(R.drawable.ic_favorite_border);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        if (item.getItemId() == R.id.action_fav) {
            Channel wallpaper = new Channel();
            int id = channelid;
            String name = channelName;
            String image = channelImage;
            String type = channelType;
            String link = channelLink;
            String lang = channelLanguage;
            String desc = channelDesc;
            String category = channelCategory;
            wallpaper.setId(id);
            wallpaper.setChannelName(name);
            wallpaper.setChannelImage(image);
            wallpaper.setChannelType(type);
            wallpaper.setChannelLink(link);
            wallpaper.setChannelLanguage(lang);
            wallpaper.setChannelDesc(desc);
            wallpaper.setChannelCategory(category);


            if (MainActivity.favoriteDatabase.favoriteDao().isFavorite(id) != 1) {
                //img_favorite.setImageResource(R.drawable.ic_baseline_favorite);
                mMenuItem.getItem(0).setIcon(R.drawable.ic_favorite_fill);
                Toast.makeText(DetailsActivity.this, R.string.add_fav, Toast.LENGTH_SHORT).show();
                MainActivity.favoriteDatabase.favoriteDao().addData(wallpaper);

            } else {
                mMenuItem.getItem(0).setIcon(R.drawable.ic_favorite_border);
                Toast.makeText(DetailsActivity.this, R.string.remove_fav, Toast.LENGTH_SHORT).show();
                MainActivity.favoriteDatabase.favoriteDao().delete(wallpaper);
            }
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);   // set status text dark
            }
        }
    }


}