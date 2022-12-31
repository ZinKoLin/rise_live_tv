package com.rise.live.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.rise.live.R;
import com.rise.live.Utils.JZVideoPlayerNew;

import java.util.Objects;

import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;

public class PlayerActivity extends AppCompatActivity {

    JZVideoPlayerNew simpleVideoView;
    String channelName;
    String channelImage;
    String channelLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitle("");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        channelName = intent.getStringExtra("name");
        channelImage = intent.getStringExtra("image");
        channelLink = intent.getStringExtra("link");

        simpleVideoView = findViewById(R.id.videoPlayer); //id in your xml file
        simpleVideoView.setUp(channelLink, JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, channelName);
        simpleVideoView.startVideo();
        simpleVideoView.startWindowFullscreen();

        Glide.with(this)
                .load(channelImage)
                .centerCrop()
                .into(simpleVideoView.thumbImageView);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        JZVideoPlayer.releaseAllVideos();

    }
    @Override
    protected void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }



}