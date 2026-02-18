package com.m.freemovie.adapter;

import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.m.freemovie.Activity.OthersDetailsActivity;
import com.m.freemovie.R;
import com.m.freemovie.Utils.base.BaseQuickAdapter;
import com.m.freemovie.Utils.base.BaseViewHolder;
import com.m.freemovie.mvp.Model.ClassBean.PinoyAllBean;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MovieRuAllAdapter extends BaseQuickAdapter<PinoyAllBean, BaseViewHolder> {
    private static final ExecutorService THREAD_POOL = Executors.newFixedThreadPool(5);
    private static final ConcurrentHashMap<String, String> THUMBNAIL_CACHE = new ConcurrentHashMap<>();
    private static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build();
    private static final Pattern THUMB_PATTERN = Pattern.compile("<img itemprop=\"image\" src=\"([^\"]+)\"");
    private static final Pattern VIDEO_PATTERN = Pattern.compile("https://voe\\.sx/e/([a-zA-Z0-9]+)");
    private static final Pattern VIDEO_PATTERN2 = Pattern.compile("https://myvidplay\\.com/e/([a-zA-Z0-9]+)");
    private static final Pattern DOWNLOAD_PATTERN = Pattern.compile("https://pinoymoviepedia\\.ru/links/([a-zA-Z0-9]+)/");
    private static final Pattern DOWNLOAD_TABLE_PATTERN = Pattern.compile("<a href='https://pinoymoviepedia\\.ru/links/([a-zA-Z0-9]+)/' target='_blank'>Download</a>");


    private static final RequestOptions GLIDE_OPTIONS = new RequestOptions()
            .placeholder(R.drawable.noimage)
            .error(R.drawable.noimage)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .timeout(10000);

    public MovieRuAllAdapter() {
        super(R.layout.all_ru_item);
        setHasStableIds(true);
    }

    @Override
    protected void convert(BaseViewHolder helper, PinoyAllBean item) {
        TextView tv_title = helper.getView(R.id.tv_title);
        ImageView iv_thumb = helper.getView(R.id.iv_thumb);
        tv_title.setText(item.getTitle());

        iv_thumb.setImageResource(R.drawable.noimage);
        ThumbnailFetchTask oldTask = (ThumbnailFetchTask) iv_thumb.getTag(R.id.iv_thumb);
        if (oldTask != null) {
            oldTask.cancel(true);
        }

        String cachedUrl = getThumbnailUrl(item);
        if (cachedUrl != null) {
            loadThumbnail(iv_thumb, cachedUrl);
        } else {
            ThumbnailFetchTask task = new ThumbnailFetchTask(iv_thumb, item);
            iv_thumb.setTag(R.id.iv_thumb, task);
            task.executeOnExecutor(THREAD_POOL);
        }

        helper.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (item.getVideoId() == null && item.getVideoIdSecond() == null) {
                    return;
                }
                mContext.startActivity(new Intent(mContext, OthersDetailsActivity.class)
                        .putExtra("videoId", item.getVideoId())
                        .putExtra("title", item.getTitle())
                        .putExtra("image", item.getThumbnailUrl())
                        .putExtra("videoId2", item.getVideoIdSecond())
                        .putExtra("type", item.getType())
                        .putExtra("downloadId",item.getDownloadId()));
            }
        });
    }

    @Override
    public long getItemId(int position) {
        PinoyAllBean item = getItem(position);
        return item != null ? item.getLink().hashCode() : super.getItemId(position);
    }

    private String getThumbnailUrl(PinoyAllBean item) {
        if (item.getThumbnailUrl() != null) {
            return item.getThumbnailUrl();
        }
        String cached = THUMBNAIL_CACHE.get(item.getLink());
        if (cached != null) {
            item.setThumbnailUrl(cached);
            return cached;
        }

        return null;
    }

    private void loadThumbnail(ImageView imageView, String url) {
        if (imageView == null || url == null) return;

        Glide.with(mContext)
                .asBitmap()
                .load(url)
                .apply(GLIDE_OPTIONS)
                .thumbnail(0.25f)
                .into(imageView);
    }

    private static String fetchDownloadId(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .build();

        try (Response response = OK_HTTP_CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) return null;

            String html = response.body().string();

            Matcher downloadMatcher = DOWNLOAD_TABLE_PATTERN.matcher(html);
            if (downloadMatcher.find()) {
                return downloadMatcher.group(1);
            }

            Matcher linkMatcher = DOWNLOAD_PATTERN.matcher(html);
            if (linkMatcher.find()) {
                return linkMatcher.group(1);
            }
        }
        return null;
    }


    private static String fetchThumbnailFromHtml(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .build();

        try (Response response = OK_HTTP_CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) return null;

            String html = response.body().string();
            Matcher thumbMatcher = THUMB_PATTERN.matcher(html);

            if (thumbMatcher.find()) {
                return thumbMatcher.group(1);
            }
        }
        return null;
    }

    private static String FetchVideoId2(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .build();

        try (Response response = OK_HTTP_CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) return null;

            String html = response.body().string();
            Matcher thumbMatcher2 = VIDEO_PATTERN2.matcher(html);

            if (thumbMatcher2.find()) {
                return thumbMatcher2.group(1);
            }

        }
        return null;
    }


    private static String FetchVideoId(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .build();

        try (Response response = OK_HTTP_CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) return null;

            String html = response.body().string();
            Matcher thumbMatcher = VIDEO_PATTERN.matcher(html);
            if (thumbMatcher.find()) {
                return thumbMatcher.group(1);
            }
        }
        return null;
    }

    private static class ThumbnailFetchTask extends AsyncTask<Void, Void, String> {
        private final WeakReference<ImageView> imageViewRef;
        private final PinoyAllBean item;
        private String thumbnailUrl;
        private String videoId, videoId2,downloadId;

        ThumbnailFetchTask(ImageView imageView, PinoyAllBean item) {
            this.imageViewRef = new WeakReference<>(imageView);
            this.item = item;
            this.thumbnailUrl = null;
            this.videoId = null;
            this.videoId2 = null;
        }

        @SuppressWarnings("deprecation")
        @Override
        protected String doInBackground(Void... voids) {
            if (isCancelled()) return null;

            try {
                if (item.getThumbnailUrl() != null) {
                    thumbnailUrl = item.getThumbnailUrl();
                    return thumbnailUrl;
                }

                String cached = THUMBNAIL_CACHE.get(item.getLink());
                if (cached != null) {
                    thumbnailUrl = cached;
                    item.setThumbnailUrl(cached);
                    return cached;
                }
                thumbnailUrl = fetchThumbnailFromHtml(item.getLink());
                videoId = FetchVideoId(item.getLink());
                videoId2 = FetchVideoId2(item.getLink());
                downloadId = fetchDownloadId(item.getLink());
                if (thumbnailUrl != null) {
                    item.setThumbnailUrl(thumbnailUrl);
                    THUMBNAIL_CACHE.put(item.getLink(), thumbnailUrl);
                }

                if (videoId2 != null) {
                    item.setVideoIdSecond(videoId2);
                }
                if (videoId != null) {
                    item.setVideoId(videoId);
                }
                if (downloadId != null) {
                    item.setDownloadId(downloadId);
//                    Log.d("MovieRuAdapter", "Download ID for " + item.getTitle() + ": " + downloadId);
                }

                return thumbnailUrl;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @SuppressWarnings("deprecation")
        @Override
        protected void onPostExecute(String result) {
            try {
                if (isCancelled() || result == null) return;

                ImageView imageView = imageViewRef.get();
                if (imageView != null) {
                    Object tag = imageView.getTag(R.id.iv_thumb);
                    if (tag == this) {
                        Glide.with(imageView.getContext())
                                .asBitmap()
                                .load(result)
                                .apply(GLIDE_OPTIONS)
                                .thumbnail(0.25f)
                                .into(imageView);
                        imageView.setTag(R.id.iv_thumb, null);
                        if (item.getVideoId() != null) {
//                            android.util.Log.d("MovieRuAdapter", "Video ID for " + item.getTitle() + ": " + item.getVideoId());
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void clearCache() {
        THUMBNAIL_CACHE.clear();
        if (getData() != null) {
            for (PinoyAllBean item : getData()) {
                item.setThumbnailUrl(null);
                item.setVideoId(null);
                item.setVideoIdSecond(null);
            }
        }
        notifyDataSetChanged();
    }
}