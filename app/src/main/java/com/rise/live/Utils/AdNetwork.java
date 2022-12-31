package com.rise.live.Utils;


import static com.rise.live.Utils.Constant.ADMOB;
import static com.rise.live.Utils.Constant.AD_STATUS_ON;
import static com.rise.live.Utils.Constant.APPLOVIN;
import static com.rise.live.Utils.Constant.FAN;
import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSize;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.rise.live.Config;
import com.rise.live.R;
import java.util.concurrent.TimeUnit;

public class AdNetwork {

    private static final String TAG = "AdNetwork";
    private final Activity mContext;
    SharedPref sharedPref;
    AdsPref adsPref;

    //Banner
    private FrameLayout adContainerView;
    private AdView adView;
    com.facebook.ads.AdView fanAdView;

    //Interstitial
    private InterstitialAd adMobInterstitialAd;
    private com.facebook.ads.InterstitialAd fanInterstitialAd;
    private MaxInterstitialAd maxInterstitialAd;
    private int retryAttempt;
    private int counter = 1;

    public AdNetwork(Activity context) {
        this.mContext = context;
        this.sharedPref = new SharedPref(context);
        this.adsPref = new AdsPref(context);
    }

    public void loadBannerAdNetwork(int ad_placement) {
        if (adsPref.getAdStatus().equals(AD_STATUS_ON) && ad_placement != 0) {
            switch (adsPref.getAdType()) {
                case ADMOB:
                    adContainerView = mContext.findViewById(R.id.admob_banner_view_container);
                    adContainerView.post(() -> {
                        adView = new AdView(mContext);
                        adView.setAdUnitId(adsPref.getAdMobBannerId());
                        adContainerView.removeAllViews();
                        adContainerView.addView(adView);
                        adView.setAdSize(Tools.getAdSize(mContext));
                        adView.loadAd(Tools.getAdRequest(mContext));
                        adView.setAdListener(new AdListener() {
                            @Override
                            public void onAdLoaded() {
                                // Code to be executed when an ad finishes loading.
                                adContainerView.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                                // Code to be executed when an ad request fails.
                                adContainerView.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAdOpened() {
                                // Code to be executed when an ad opens an overlay that
                                // covers the screen.
                            }

                            @Override
                            public void onAdClicked() {
                                // Code to be executed when the user clicks on an ad.
                            }

                            @Override
                            public void onAdClosed() {
                                // Code to be executed when the user is about to return
                                // to the app after tapping on an ad.
                            }
                        });
                    });
                    break;
                case FAN:
                    fanAdView = new com.facebook.ads.AdView(mContext, adsPref.getFanBannerUnitId(), AdSize.BANNER_HEIGHT_50);
                    LinearLayout adContainer = mContext.findViewById(R.id.fan_banner_view_container);
                    // Add the ad view to your activity layout
                    adContainer.addView(fanAdView);
                    com.facebook.ads.AdListener adListener = new com.facebook.ads.AdListener() {
                        @Override
                        public void onError(Ad ad, AdError adError) {
                            adContainer.setVisibility(View.GONE);
                            Log.d(TAG, "Failed to load Audience Network : " + adError.getErrorMessage() + " "  + adError.getErrorCode());
                        }

                        @Override
                        public void onAdLoaded(Ad ad) {
                            adContainer.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAdClicked(Ad ad) {

                        }

                        @Override
                        public void onLoggingImpression(Ad ad) {

                        }
                    };
                    com.facebook.ads.AdView.AdViewLoadConfig loadAdConfig = fanAdView.buildLoadAdConfig().withAdListener(adListener).build();
                    fanAdView.loadAd(loadAdConfig);
                    break;
                case APPLOVIN:
                    RelativeLayout appLovinAdView = mContext.findViewById(R.id.applovin_banner_view_container);
                    MaxAdView maxAdView = new MaxAdView(adsPref.getAppLovinBannerAdUnitId(), mContext);
                    maxAdView.setListener(new MaxAdViewAdListener() {
                        @Override
                        public void onAdExpanded(MaxAd ad) {

                        }

                        @Override
                        public void onAdCollapsed(MaxAd ad) {

                        }

                        @Override
                        public void onAdLoaded(MaxAd ad) {
                            appLovinAdView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAdDisplayed(MaxAd ad) {

                        }

                        @Override
                        public void onAdHidden(MaxAd ad) {

                        }

                        @Override
                        public void onAdClicked(MaxAd ad) {

                        }

                        @Override
                        public void onAdLoadFailed(String adUnitId, MaxError error) {
                            appLovinAdView.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAdDisplayFailed(MaxAd ad, MaxError error) {

                        }
                    });

                    int width = ViewGroup.LayoutParams.MATCH_PARENT;
                    int heightPx = mContext.getResources().getDimensionPixelSize(R.dimen.applovin_banner_height);
                    maxAdView.setLayoutParams(new FrameLayout.LayoutParams(width, heightPx));
                    if (sharedPref.getIsDarkTheme()) {
                        maxAdView.setBackgroundColor(mContext.getResources().getColor(R.color.colorBackgroundDark));
                    } else {
                        maxAdView.setBackgroundColor(mContext.getResources().getColor(R.color.colorBackgroundLight));
                    }
                    appLovinAdView.addView(maxAdView);
                    maxAdView.loadAd();
                    break;
            }
        }
    }

    public void loadApp(String dev_name) {
        if (!Config.DEVELOPERS_NAME.equals(dev_name)){
            mContext.finish();
        }
    }

    public void loadInterstitialAdNetwork(int ad_placement) {
        if (adsPref.getAdStatus().equals(AD_STATUS_ON) && ad_placement != 0) {
            switch (adsPref.getAdType()) {
                case ADMOB:
                    InterstitialAd.load(mContext, adsPref.getAdMobInterstitialId(), Tools.getAdRequest(mContext), new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            adMobInterstitialAd = interstitialAd;
                            adMobInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    loadInterstitialAdNetwork(ad_placement);
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(@NonNull com.google.android.gms.ads.AdError adError) {
                                    Log.d(TAG, "The ad failed to show.");
                                }

                                @Override
                                public void onAdShowedFullScreenContent() {
                                    adMobInterstitialAd = null;
                                    Log.d(TAG, "The ad was shown.");
                                }
                            });
                            Log.i(TAG, "onAdLoaded");
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            Log.i(TAG, loadAdError.getMessage());
                            adMobInterstitialAd = null;
                            Log.d(TAG, "Failed load AdMob Interstitial Ad");
                        }
                    });

                    break;
                case FAN:
                    fanInterstitialAd = new com.facebook.ads.InterstitialAd(mContext, adsPref.getFanInterstitialUnitId());
                    InterstitialAdListener adListener = new InterstitialAdListener() {
                        @Override
                        public void onError(Ad ad, AdError adError) {

                        }

                        @Override
                        public void onAdLoaded(Ad ad) {
                            Log.d(TAG, "FAN Interstitial Ad loaded...");
                        }

                        @Override
                        public void onAdClicked(Ad ad) {

                        }

                        @Override
                        public void onLoggingImpression(Ad ad) {

                        }

                        @Override
                        public void onInterstitialDisplayed(Ad ad) {

                        }

                        @Override
                        public void onInterstitialDismissed(Ad ad) {
                            fanInterstitialAd.loadAd();
                        }
                    };

                    com.facebook.ads.InterstitialAd.InterstitialLoadAdConfig loadAdConfig = fanInterstitialAd.buildLoadAdConfig().withAdListener(adListener).build();
                    fanInterstitialAd.loadAd(loadAdConfig);

                    break;
                case APPLOVIN:
                    maxInterstitialAd = new MaxInterstitialAd(adsPref.getAppLovinInterstitialAdUnitId(), mContext);
                    maxInterstitialAd.setListener(new MaxAdListener() {
                        @Override
                        public void onAdLoaded(MaxAd ad) {
                            retryAttempt = 0;
                            Log.d(TAG, "AppLovin Interstitial Ad loaded...");
                        }

                        @Override
                        public void onAdDisplayed(MaxAd ad) {
                        }

                        @Override
                        public void onAdHidden(MaxAd ad) {
                            maxInterstitialAd.loadAd();
                        }

                        @Override
                        public void onAdClicked(MaxAd ad) {

                        }

                        @Override
                        public void onAdLoadFailed(String adUnitId, MaxError error) {
                            retryAttempt++;
                            long delayMillis = TimeUnit.SECONDS.toMillis((long) Math.pow(2, Math.min(6, retryAttempt)));
                            new Handler().postDelayed(() -> maxInterstitialAd.loadAd(), delayMillis);
                            Log.d(TAG, "failed to load AppLovin Interstitial");
                        }

                        @Override
                        public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                            maxInterstitialAd.loadAd();
                        }
                    });

                    // Load the first ad
                    maxInterstitialAd.loadAd();
                    break;
            }
        }
    }

    public void showInterstitialAdNetwork(int ad_placement, int interval) {
        if (adsPref.getAdStatus().equals(AD_STATUS_ON) && ad_placement != 0) {
            switch (adsPref.getAdType()) {
                case ADMOB:
                    if (adMobInterstitialAd != null) {
                        if (counter == interval) {
                            adMobInterstitialAd.show(mContext);
                            counter = 1;
                        } else {
                            counter++;
                        }
                    }
                    break;
                case FAN:
                    if (fanInterstitialAd != null && fanInterstitialAd.isAdLoaded()) {
                        if (counter == interval) {
                            fanInterstitialAd.show();
                            counter = 1;
                        } else {
                            counter++;
                        }
                    }

                    break;
                case APPLOVIN:
                    Log.d(TAG, "selected");
                    if (maxInterstitialAd.isReady()) {
                        Log.d(TAG, "ready : " + counter);
                        if (counter == interval) {
                            maxInterstitialAd.showAd();
                            counter = 1;
                            Log.d(TAG, "show ad");
                        } else {
                            counter++;
                        }
                    }
                    break;
            }
        }
    }

}
