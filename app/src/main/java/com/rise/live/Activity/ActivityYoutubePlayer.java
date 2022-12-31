package com.rise.live.Activity;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.rtsp.RtspMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.util.Util;
import com.rise.live.Config;
import com.rise.live.R;

import java.util.List;

//////////////////////////////////////////////////////////////////////////////////////////



public class ActivityYoutubePlayer extends AppCompatActivity {
//
//    private final String vlcPackageName = "org.videolan.vlc";
//    private final String mxPackageName = "com.mxtech.videoplayer.ad";
//    private final String mxProPackageName = "com.mxtech.videoplayer.pro";
    //////


    private ExoPlayer player;
    private DefaultBandwidthMeter BANDWIDTH_METER;
    private DataSource.Factory dataSourceFactory;
    private ProgressBar progressBar;
    Button btnTryAgain;
    String channelTitle, channelUrl;
    FrameLayout frameLayout;
    PlayerView playerView;
    private View mDecorView;
    ImageView imgExternalPlayer;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_youtube);




        //InterstitialAds.create(this);

        mDecorView = getWindow().getDecorView();
        mDecorView.setOnSystemUiVisibilityChangeListener(i -> {
            if (i == 0) {
                hideSystemUi();
            }
        });

        channelTitle = getIntent().getExtras().getString("name");
        channelUrl = getIntent().getExtras().getString("link");
        imgExternalPlayer = findViewById(R.id.img_external_player);



        LoadControl loadControl = new DefaultLoadControl();

        AdaptiveTrackSelection.Factory trackSelectionFactory = new AdaptiveTrackSelection.Factory();
        DefaultTrackSelector trackSelector = new DefaultTrackSelector(this, trackSelectionFactory);



        progressBar = findViewById(R.id.progressBar);
        btnTryAgain = findViewById(R.id.btn_try_again);
        playerView = findViewById(R.id.exoPlayerView);
        frameLayout = findViewById(R.id.playerSection);

        player = new ExoPlayer.Builder(this)
                .setTrackSelector(trackSelector)
                .setLoadControl(loadControl)
                .build();
        playerView.setPlayer(player);
        playerView.setUseController(true);
        playerView.requestFocus();

        Uri uri = Uri.parse(channelUrl);

        MediaSource mediaSource = buildMediaSource(uri);
        player.setMediaSource(mediaSource);
        player.prepare();
        player.setPlayWhenReady(true);










        BANDWIDTH_METER = new DefaultBandwidthMeter.Builder(this).build();
        dataSourceFactory= buildDataSourceFactory(true);
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);
        playerView.setUseController(true);
        playerView.requestFocus();
        playStreaming();

        player.addListener(new Player.Listener() {
            @Override
            public void onCues(@NonNull List<Cue> cues) {

            }

            @Override
            public void onTimelineChanged(@NonNull Timeline timeline, int reason) {

            }

            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == PlaybackStateCompat.STATE_PLAYING) {
                    progressBar.setVisibility(View.GONE);

                }

                if (Config.ENABLE_LOOPING_MODE) {
                    switch (state) {
                        case Player.STATE_READY:
                            progressBar.setVisibility(View.GONE);
                            player.setPlayWhenReady(true);
                            break;
                        case Player.STATE_ENDED:
                                retryLoad();

                            break;
                        case Player.STATE_BUFFERING:
                            progressBar.setVisibility(View.VISIBLE);
                            break;
                        case Player.STATE_IDLE:
                            break;
                    }
                }
            }

            @Override
            public void onPlayWhenReadyChanged(boolean playWhenReady, int reason) {

            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {

            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {
                player.setPlayWhenReady(true);
                retryLoad();

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
                player.setPlayWhenReady(true);
                retryLoad();

            }

            @Override
            public void onPlayerError(@NonNull PlaybackException error) {

               player.stop();
               //errorDialog();


            }

            @Override
            public void onPlayerErrorChanged(@Nullable PlaybackException error) {
                player.setPlayWhenReady(true);
                retryLoad();
            }

        });

        btnTryAgain.setOnClickListener(v -> {
            btnTryAgain.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            retryLoad();
        });

        imgExternalPlayer.setOnClickListener(v -> {
            if (player != null && player.getPlayWhenReady()) {
                player.setPlayWhenReady(false);
            }
           // showDialogPlayer();
        });



    }
    //////////////////////////

//    public void showDialogPlayer() {
//        AlertDialog builder = new AlertDialog.Builder(this).create();
//        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_player, null);
//        builder.setView(customLayout);
//        builder.setCancelable(true);
//
//        TextView mxPlayer = customLayout.findViewById(R.id.mxPlayer);
//        TextView vlcPlayer = customLayout.findViewById(R.id.vlcPlayer);
//
//        mxPlayer.setOnClickListener(v -> {
//            if (isPlayerInstalled(mxPackageName) || isPlayerInstalled(mxProPackageName)) {
//                playMxPlayer();
//            } else {
//                redirectToDownloadIt(mxPackageName);
//            }
//            builder.dismiss();
//        });
//
//        vlcPlayer.setOnClickListener(v -> {
//            if (isPlayerInstalled(vlcPackageName)) {
//                playVlcPlayer();
//            } else {
//                redirectToDownloadIt(vlcPackageName);
//            }
//            builder.dismiss();
//        });
//
//        builder.show();
//    }
//
//    private boolean isPlayerInstalled(final String packageName) {
//        PackageManager pm = getPackageManager();
//        boolean app_installed;
//        try {
//            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
//            app_installed = true;
//        } catch (PackageManager.NameNotFoundException e) {
//            app_installed = false;
//        }
//        return app_installed;
//    }
//
//    private void playVlcPlayer() {
//        Uri uri = Uri.parse(channelUrl);
//        Intent vlcIntent = new Intent(Intent.ACTION_VIEW);
//        vlcIntent.putExtra("title", channelTitle);
//        vlcIntent.setPackage(vlcPackageName);
//        vlcIntent.setDataAndTypeAndNormalize(uri, "video/*");
//        vlcIntent.setComponent(new ComponentName(vlcPackageName, "org.videolan.vlc.gui.video.VideoPlayerActivity"));
//        try {
//            startActivity(vlcIntent);
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(getApplicationContext(), getString(R.string.player_error), Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void playMxPlayer() {
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        Uri videoUri = Uri.parse(channelUrl);
//        intent.putExtra("title", channelTitle);
//        intent.setDataAndTypeAndNormalize(videoUri, "application/x-mpegURL");
//        intent.setPackage(mxPackageName);
//        try {
//            startActivity(intent);
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(getApplicationContext(), getString(R.string.player_error), Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void redirectToDownloadIt(final String packageName) {
//        try {
//            startActivity(new Intent(Intent.ACTION_VIEW,
//                    Uri.parse("market://details?id=" + packageName)));
//        } catch (android.content.ActivityNotFoundException mx) {
//            if (packageName.equals(vlcPackageName)) {
//                startActivity(new Intent(Intent.ACTION_VIEW,
//                        Uri.parse("https://appgallery.huawei.com/#/app/C101924579")));
//            } else {
//                startActivity(new Intent(Intent.ACTION_VIEW,
//                        Uri.parse("https://appgallery.huawei.com/#/app/C100074999")));
//            }
//        }
//        Toast.makeText(this, getString(R.string.download_player_first), Toast.LENGTH_LONG).show();
//    }
    ///


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUi();
        }
    }

    private void hideSystemUi() {
        mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }


    private void playStreaming() {

        Uri uri = Uri.parse(channelUrl);
        MediaSource mediaSource = buildMediaSource(uri);
        player.prepare(mediaSource);
        player.setPlayWhenReady(true);
        player.setRepeatMode(Player.REPEAT_MODE_ONE);
    }


    @SuppressLint("SwitchIntDef")
    private MediaSource buildMediaSource(Uri uri) {
        MediaItem mMediaItem = MediaItem.fromUri(Uri.parse(String.valueOf(uri)));
        int type = TextUtils.isEmpty(null) ? Util.inferContentType(uri) : Util.inferContentType("." + null);
        switch (type) {
            case C.TYPE_DASH:
                return new DashMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(mMediaItem);
            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(dataSourceFactory)
                        .setAllowChunklessPreparation(true)
                        .createMediaSource(mMediaItem);
            case C.TYPE_OTHER:
                return new ProgressiveMediaSource.Factory(dataSourceFactory, new DefaultExtractorsFactory())
                        .createMediaSource(mMediaItem);
            case C.TYPE_SS:
                return new SsMediaSource.Factory(new DefaultSsChunkSource
                        .Factory(dataSourceFactory),buildDataSourceFactory(false))
                        .createMediaSource(MediaItem.fromUri(uri));
            case C.TYPE_RTSP:
                return new RtspMediaSource.Factory()
                        .createMediaSource(MediaItem.fromUri(uri));
            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }





 /*  private MediaSource buildMediaSource(Uri uri) {
        int type = Util.inferContentType(uri);
        switch (type) {
            case C.TYPE_SS:
                return new SsMediaSource.Factory(new DefaultSsChunkSource.Factory(dataSourceFactory), buildDataSourceFactory(false)).createMediaSource(MediaItem.fromUri(uri));
            case C.TYPE_DASH:
                return new DashMediaSource.Factory(new DefaultDashChunkSource.Factory(dataSourceFactory), buildDataSourceFactory(false)).createMediaSource(MediaItem.fromUri(uri));            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(dataSourceFactory) .setAllowChunklessPreparation(false).createMediaSource(MediaItem.fromUri(uri));
            case C.TYPE_RTSP:
                return new RtspMediaSource.Factory().createMediaSource(MediaItem.fromUri(uri));
            case C.TYPE_OTHER:
                return new ProgressiveMediaSource.Factory(dataSourceFactory,new DefaultExtractorsFactory()).createMediaSource(MediaItem.fromUri(uri));

            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }
*/


       private DataSource.Factory buildDataSourceFactory(boolean useBandwidthMeter) {
        return buildDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
    }

    public DataSource.Factory buildDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultDataSourceFactory(ActivityYoutubePlayer.this, bandwidthMeter,
                buildHttpDataSourceFactory());
    }

//    DefaultRenderersFactory renderersFactory =
//            new DefaultRenderersFactory(ActivityYoutubePlayer.this)
//                    .forceEnableMediaCodecAsynchronousQueueing();
//    ExoPlayer exoPlayer = new ExoPlayer.Builder(ActivityYoutubePlayer.this, renderersFactory).build();

    public DefaultHttpDataSource.Factory buildHttpDataSourceFactory() {
        return new DefaultHttpDataSource.Factory();
    }


    @Override
    public void onStop() {
        super.onStop();
        if (player != null && player.getPlayWhenReady()) {
            player.setPlayWhenReady(false);
            player.getPlaybackState();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (player != null && player.getPlayWhenReady()) {
            player.setPlayWhenReady(false);
            player.getPlaybackState();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (player != null) {
            player.setPlayWhenReady(true);
            player.getPlaybackState();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.setPlayWhenReady(false);
            player.stop();
            player.release();
        }
    }
    @Override

    public void onBackPressed() {

        super.onBackPressed();
    }


//    public void errorDialog() {
//        new AlertDialog.Builder(this)
//                .setTitle(getString(R.string.whops))
//                .setCancelable(false)
//                .setMessage(getString(R.string.msg_failed))
//                .setPositiveButton(getString(R.string.option_retry), (dialog, which) -> retryLoad())
//                .setNegativeButton(getString(R.string.option_no), (dialogInterface, i) -> finish())
//                .show();
//    }

    public void retryLoad() {
        Uri uri = Uri.parse(channelUrl);
        MediaSource mediaSource = buildMediaSource(uri);
        player.setMediaSource(mediaSource);
        player.prepare();
        player.setPlayWhenReady(true);

    }
}



