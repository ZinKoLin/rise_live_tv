package com.rise.live.Activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;

public class FullScreenClient extends WebChromeClient {

    public static final int FULL_SCREEN_SETTING =
            View.SYSTEM_UI_FLAG_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                    View.STATUS_BAR_HIDDEN |
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

    private View mCustomView;
    private CustomViewCallback mCustomViewCallback;
    private int mOriginalOrientation;
    private int mOriginalSystemUiVisibility;
    private Activity context;

    public FullScreenClient(Activity activity) {
        this.context = activity;
    }

    public void hideFullScreen(WebView webView) {
        ((FrameLayout) context.getWindow().getDecorView()).removeView(this.mCustomView);
        this.mCustomView = null;
        context.getWindow().getDecorView().setSystemUiVisibility(mOriginalSystemUiVisibility);
        context.getWindow().clearFlags(FULL_SCREEN_SETTING);
        context.setRequestedOrientation(this.mOriginalOrientation);
        this.mCustomViewCallback.onCustomViewHidden();
        this.mCustomViewCallback = null;
        context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
        webView.clearFocus();
    }

    public void showFullScreen(View view, CustomViewCallback callback) {
        if (this.mCustomView != null) {
            onHideCustomView();
            return;
        }
        this.mCustomView = view;
        this.mOriginalSystemUiVisibility = context.getWindow().getDecorView().getSystemUiVisibility();
        this.mOriginalOrientation = context.getRequestedOrientation();
        this.mCustomViewCallback = callback;
        ((FrameLayout) context.getWindow()
                .getDecorView())
                .addView(this.mCustomView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        context.getWindow().getDecorView().setSystemUiVisibility(FULL_SCREEN_SETTING);
        context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.mCustomView.setOnSystemUiVisibilityChangeListener(visibility -> UpdateView());
    }

    private void UpdateView() {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) this.mCustomView.getLayoutParams();
        params.bottomMargin = 0;
        params.topMargin = 0;
        params.leftMargin = 0;
        params.rightMargin = 0;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        this.mCustomView.setLayoutParams(params);
        context.getWindow().getDecorView().setSystemUiVisibility(FULL_SCREEN_SETTING);
    }

    public Bitmap videoBitmap(){
        return Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
    }

}
