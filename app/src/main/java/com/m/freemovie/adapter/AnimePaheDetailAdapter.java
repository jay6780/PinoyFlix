package com.m.freemovie.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.m.freemovie.R;
import com.m.freemovie.Utils.DbHelper.PinoyWatchHistoryHelper;
import com.m.freemovie.Utils.base.BaseQuickAdapter;
import com.m.freemovie.Utils.base.BaseViewHolder;
import com.m.freemovie.mvp.Model.ClassBean.AnimePaheBeanList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AnimePaheDetailAdapter extends BaseQuickAdapter<AnimePaheBeanList, BaseViewHolder> {
    private static final Pattern RESILIENT_IFRAME_PATTERN = Pattern.compile("<iframe[^>]*src=\"([^\"]*(?:source=|url=|player/)[^\"]*)\"[^>]*>");
    private static final Pattern BROAD_FILE_URL_PATTERN = Pattern.compile("(?:var|let|const)\\s+fileUrl\\s*=\\s*['\"]([^'\"]+)['\"]");
    private static final Pattern FALLBACK_SOURCES_PATTERN = Pattern.compile("['\"]file['\"]\\s*:\\s*['\"]([^'\"]+)(?:googlevideo\\.com)[^'\"]*['\"]");

    private static final String BROWSER_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

    private final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .cookieJar(new CookieJar() {
                private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

                @Override
                public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                    cookieStore.put(url.host(), cookies);
                }

                @Override
                public List<Cookie> loadForRequest(HttpUrl url) {
                    List<Cookie> cookies = cookieStore.get(url.host());
                    return cookies != null ? cookies : new ArrayList<Cookie>();
                }
            })
            .build();

    private PinoyWatchHistoryHelper dbHelper;
    private EpisodeListener videoPlayListerner;
    private int lastPosition = -1;

    public interface EpisodeListener {
        void getVideoUrl(String videoUrl, boolean isDownload, String episodeNum);
    }

    public AnimePaheDetailAdapter(EpisodeListener videoPlayListerner) {
        super(R.layout.episode_item);
        this.videoPlayListerner = videoPlayListerner;
    }

    @Override
    protected void convert(BaseViewHolder helper, AnimePaheBeanList item) {
        if (dbHelper == null) {
            dbHelper = new PinoyWatchHistoryHelper(mContext);
        }
        TextView tv_season = helper.getView(R.id.tv_season);
        ImageView iv_thumb = helper.getView(R.id.iv_thumb);
        ImageView iv_download = helper.getView(R.id.iv_download);
        iv_download.setVisibility(View.GONE);

        RelativeLayout rl_select = helper.getView(R.id.rl_select);
        TextView tv_watched = helper.getView(R.id.tv_watched);

        if (lastPosition == (helper.getAdapterPosition())) {
            rl_select.setBackgroundColor(Color.parseColor("#050E3C"));
        } else {
            rl_select.setBackgroundColor(Color.parseColor("#313647"));
        }
        tv_watched.setVisibility(item.isWatched() ? View.VISIBLE : View.GONE);
        tv_season.setText("Episode: " + item.getEpisode());

        Glide.with(mContext)
                .asBitmap()
                .placeholder(R.drawable.noimage)
                .load(item.getImageUrl())
                .into(iv_thumb);

        helper.convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isNetworkAvailable()) {
                    Toast.makeText(mContext, "Please check internet and try again", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (lastPosition != (helper.getAdapterPosition())) {
                    lastPosition = helper.getAdapterPosition();
//                    Log.d("UrlData", "val: " + item.getEpisodeUrl());
                    dbHelper.markEpisodeAsWatched(item.getVideoId(), item.getEpisode());
                    lastPosition = (helper.getAdapterPosition());

                    Toast.makeText(mContext, "Extracting video stream...", Toast.LENGTH_SHORT).show();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final String extractedFileUrl = extractJwVideoUrl(item.getEpisodeUrl());

                            ((Activity) mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (extractedFileUrl != null) {
                                        videoPlayListerner.getVideoUrl(extractedFileUrl, false, item.getEpisode());
                                        item.setWatched(true);
                                        notifyDataSetChanged();
                                    } else {
                                        Toast.makeText(mContext, "Failed to parse video stream. Check Logcat.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    }).start();
                }
            }
        });
    }

    private String extractJwVideoUrl(String miruroUrl) {
        try {
            String cacheBusterUrl = miruroUrl + (miruroUrl.contains("?") ? "&" : "?") + "t=" + System.currentTimeMillis();
            Request miruroRequest = new Request.Builder()
                    .url(cacheBusterUrl)
                    .header("User-Agent", BROWSER_USER_AGENT)
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                    .header("Accept-Language", "en-US,en;q=0.5")
                    .header("Upgrade-Insecure-Requests", "1")
                    .build();

            String miruroHtml = "";
            try (Response response = OK_HTTP_CLIENT.newCall(miruroRequest).execute()) {
                if (!response.isSuccessful() || response.body() == null) {
                    Log.e("ExtractionError", "Step 1 Failed. HTTP response code: " + response.code());
                    return null;
                }
                miruroHtml = response.body().string();
            }
            if (miruroHtml.contains("cf-challenge") || miruroHtml.contains("javascript_challenge")) {
                Log.e("ExtractionError", "Step 1 Blocked: Cloudflare/Anti-Bot protection triggered.");
                return null;
            }

            Matcher iframeMatcher = RESILIENT_IFRAME_PATTERN.matcher(miruroHtml);
            if (!iframeMatcher.find()) {
                Log.e("ExtractionError", "Step 1 Failed: Could not locate iframe tag layout framework inside document source.");
                return null;
            }

            String embedUrl = iframeMatcher.group(1);
            if (embedUrl.startsWith("/")) {
                java.net.URL uri = new java.net.URL(miruroUrl);
                embedUrl = uri.getProtocol() + "://" + uri.getHost() + embedUrl;
            } else if (embedUrl.startsWith("//")) {
                embedUrl = "https:" + embedUrl;
            }

            Log.d("ExtractionDebug", "Target Frame Discovered: " + embedUrl);

            Request embedRequest = new Request.Builder()
                    .url(embedUrl)
                    .header("User-Agent", BROWSER_USER_AGENT)
                    .header("Referer", miruroUrl)
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8")
                    .header("Accept-Language", "en-US,en;q=0.5")
                    .header("Sec-Fetch-Dest", "iframe")
                    .header("Sec-Fetch-Mode", "navigate")
                    .header("Sec-Fetch-Site", "same-origin")
                    .build();

            String embedHtml = "";
            try (Response response = OK_HTTP_CLIENT.newCall(embedRequest).execute()) {
                if (!response.isSuccessful() || response.body() == null) {
                    Log.e("ExtractionError", "Step 2 Failed. Player iframe returned error code: " + response.code());
                    return null;
                }
                embedHtml = response.body().string();
            }
            Matcher fileUrlMatcher = BROAD_FILE_URL_PATTERN.matcher(embedHtml);
            if (fileUrlMatcher.find()) {
                String cleanStreamUrl = fileUrlMatcher.group(1);
                return sanitizeExtractedUrl(cleanStreamUrl);
            }

            Log.d("ExtractionDebug", "var fileUrl matching returned empty. Attempting backup array parsing...");

            Matcher sourcesMatcher = FALLBACK_SOURCES_PATTERN.matcher(embedHtml);
            if (sourcesMatcher.find()) {
                String fallbackUrl = sourcesMatcher.group(1);
                if (!fallbackUrl.startsWith("http")) {
                    fallbackUrl = "https:" + (fallbackUrl.startsWith("//") ? "" : "//") + fallbackUrl;
                }
                Log.d("ExtractionSuccess", "Target located via backup payload sequence array parser!");
                return sanitizeExtractedUrl(fallbackUrl);
            }
            Log.e("ExtractionError", "Step 3 Mismatch: Script logic changed. Source payload snippet:\n" +
                    embedHtml.substring(0, Math.min(500, embedHtml.length())));

        } catch (Exception e) {
            Log.e("ExtractionError", "Scraping pipeline generated an exception: ", e);
        }
        return null;
    }

    private String sanitizeExtractedUrl(String rawUrl) {
        String cleanUrl = rawUrl.replace("\\/", "/");
        cleanUrl = cleanUrl.replace("&amp;", "&");
        return cleanUrl;
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("MissingPermission")
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}