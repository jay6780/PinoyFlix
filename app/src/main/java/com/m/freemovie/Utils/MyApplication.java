package com.m.freemovie.Utils;

import android.app.Application;
import android.content.Context;
import android.webkit.CookieManager;
import android.webkit.WebStorage;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

public class MyApplication extends Application {
    private static MyApplication instance;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initGlide();
        clearWebViewData();
    }

    private void clearWebViewData() {
        CookieManager cookieManager = CookieManager.getInstance();
        if (cookieManager != null) {
            cookieManager.removeAllCookies(null);
            cookieManager.flush();
        }
        WebStorage webStorage = WebStorage.getInstance();
        if (webStorage != null) {
            webStorage.deleteAllData();
        }
    }

    private void initGlide() {
        RequestOptions defaultOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .skipMemoryCache(false);

        Glide.init(this, new GlideBuilder()
                .setDefaultRequestOptions(defaultOptions));
        clearAllGlideCache();
    }
    public static void clearGlideMemoryCache() {
        Glide.get(instance).clearMemory();
    }

    public static void clearGlideDiskCache() {
        new Thread(() -> Glide.get(instance).clearDiskCache()).start();
    }

    public static void clearAllGlideCache() {
        clearGlideMemoryCache();
        clearGlideDiskCache();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Glide.get(this).trimMemory(level);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Glide.get(this).clearMemory();
    }
}