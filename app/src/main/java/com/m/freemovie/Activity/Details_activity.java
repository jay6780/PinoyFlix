package com.m.freemovie.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.m.freemovie.R;
import com.m.freemovie.Utils.SPUtils;
import com.m.freemovie.Utils.WindowUtils;
import com.m.freemovie.adapter.SeasonsAdapter;
import com.m.freemovie.databinding.ActivityDetailsBinding;
import com.m.freemovie.databinding.ActivityDetailsSeriesBinding;
import com.m.freemovie.mvp.ClassBean.DetailBean;
import com.m.freemovie.mvp.ClassBean.DetailTvBean;
import com.m.freemovie.mvp.Contract.DetailContract;
import com.m.freemovie.mvp.Presenter.DetailPresenter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Details_activity extends AppCompatActivity implements DetailContract.View {
    private DetailPresenter detailPresenter;
    private String id;
    private KProgressHUD hud;
    private String title;
    private SPUtils spUtils;
    private String lastImage;
    private ActivityDetailsBinding binding;
    private ActivityDetailsSeriesBinding seriesBinding;
    private SeasonsAdapter seasonsAdapter;
    private boolean isTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isTv = getIntent().getBooleanExtra("isTv", false);
        id = getIntent().getStringExtra("id");
//        Log.d("IsTv", "value: " + isTv + " id: " + id);

        if (isTv) {
            seriesBinding = ActivityDetailsSeriesBinding.inflate(getLayoutInflater());
            setContentView(seriesBinding.getRoot());
        } else {
            binding = ActivityDetailsBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
        }

        getSupportActionBar().hide();
        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait");

        detailPresenter = new DetailPresenter(this);

        if (isTv) {
            seriesBinding.ivBack.setOnClickListener(view -> finish());
            seriesBinding.ivBook.setOnClickListener(view -> savedBook());
            seriesBinding.tvWatch.setOnClickListener(view -> watchNow());
        } else {
            binding.ivBack.setOnClickListener(view -> finish());
            binding.ivBook.setOnClickListener(view -> savedBook());
            binding.tvWatch.setOnClickListener(view -> watchNow());
        }

        spUtils = SPUtils.getInstance("detailPrefs");
        setImageData(id);

        if (isTv) {
            detailPresenter.getTvDetail(id, getString(R.string.key));
        } else {
            detailPresenter.getDetail(id, getString(R.string.key));
        }
    }

    private void savedBook() {
        List<DetailBean> detailBeans = getDetailData();

        boolean alreadyBookmarked = false;
        for (DetailBean bean : detailBeans) {
            if (bean.getVideoId() != null && bean.getVideoId().equals(id)) {
                alreadyBookmarked = true;
                break;
            }
        }

        if (alreadyBookmarked) {
            for (int i = 0; i < detailBeans.size(); i++) {
                if (detailBeans.get(i).getVideoId().equals(id)) {
                    detailBeans.remove(i);
                    break;
                }
            }
            if (isTv) {
                seriesBinding.ivBook.setImageResource(R.drawable.unbooked);
            } else {
                binding.ivBook.setImageResource(R.drawable.unbooked);
            }
        } else {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
            DetailBean details = new DetailBean(id, timestamp, lastImage, title);
            details.setVideoId(id);
            details.setTimeStamp(timestamp);
            detailBeans.add(0, details);
            if (isTv) {
                seriesBinding.ivBook.setImageResource(R.drawable.booked);
            } else {
                binding.ivBook.setImageResource(R.drawable.booked);
            }
        }

        saveDetailData(detailBeans);
    }

    private void saveDetailData(List<DetailBean> detailBeans) {
        try {
            JSONArray jsonArray = new JSONArray();
            for (DetailBean detail : detailBeans) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("videoId", detail.getVideoId());
                jsonObject.put("timeStamp", detail.getTimeStamp());
                jsonObject.put("tempImage", detail.getTempImage());
                jsonObject.put("movieName", detail.getMovieName());
                jsonArray.put(jsonObject);
            }
            spUtils.put("detailPrefs", jsonArray.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setImageData(String videoId) {
        boolean isBookmarked = false;
        List<DetailBean> detailBeans = getDetailData();
        for (DetailBean bean : detailBeans) {
            if (bean.getVideoId() != null && bean.getVideoId().equals(videoId)) {
                isBookmarked = true;
                break;
            }
        }
        if (isTv) {
            seriesBinding.ivBook.setImageResource(isBookmarked ? R.drawable.booked : R.drawable.unbooked);
        } else {
            binding.ivBook.setImageResource(isBookmarked ? R.drawable.booked : R.drawable.unbooked);
        }
    }

    private List<DetailBean> getDetailData() {
        String detailJson = spUtils.getString("detailPrefs", "[]");
        List<DetailBean> detailBeans = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(detailJson);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                DetailBean details = new DetailBean();
                details.setVideoId(jsonObject.getString("videoId"));
                details.setTimeStamp(jsonObject.getString("timeStamp"));
                details.setTempImage(jsonObject.getString("tempImage"));
                details.setMovieName(jsonObject.getString("movieName"));
                detailBeans.add(details);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detailBeans;
    }

    private void watchNow() {
        String[] videoPlayer = {"Player 1", "Player 2 (With download)"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        TextView titleView = new TextView(this);
        titleView.setText("Select player");
        titleView.setTextColor(Color.BLACK);
        titleView.setPadding(40, 40, 40, 20);
        titleView.setTextSize(15);

        builder.setCustomTitle(titleView);

        builder.setItems(videoPlayer, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = null;
                switch (which) {
                    case 0:
                        intent = new Intent(Details_activity.this, VideoWebviewActivity.class);
                        intent.putExtra("title", title);
                        intent.putExtra("videoPosition", 1);
                        intent.putExtra("videoId", id);
                        intent.putExtra("isTv", isTv);
                        break;
                    case 1:
                        intent = new Intent(Details_activity.this, VideoWebviewActivity.class);
                        intent.putExtra("title", title);
                        intent.putExtra("videoPosition", 2);
                        intent.putExtra("videoId", id);
                        intent.putExtra("isTv", isTv);
                        break;
                }
                startActivity(intent);
            }
        });
        builder.show();
    }

    @Override
    public void showLoading() {
        hud.show();
    }

    @Override
    public void showError(String error) {
        Log.d("ErrorData", "val: " + error);
    }

    @Override
    public void hideLoading() {
        if (hud != null && hud.isShowing()) {
            hud.dismiss();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        new WindowUtils(this);
    }

    @Override
    public void getDetailResponse(DetailBean movieBean) {
        if (movieBean != null && !isFinishing() && !isDestroyed() && !isTv) {
            String posterPath = "https://image.tmdb.org/t/p/w500/" + movieBean.getPoster_path();
            this.lastImage = posterPath;
            binding.tvTitle.setText(movieBean.getOriginal_title());

            StringBuilder sb = new StringBuilder();
            List<String> names = new ArrayList<>();

            for (DetailBean.GenresBean moviename : movieBean.getGenres()) {
                names.add(moviename.getName());
            }
            for (int i = 0; i < names.size(); i++) {
                sb.append(names.get(i));
                if (i < movieBean.getGenres().size() - 1) {
                    sb.append(", ");
                }
            }

            binding.tvInfo.setText(sb.toString());
            binding.tvDate.setText(movieBean.getRelease_date());
            binding.tvRate.setText(String.format("%.2f", movieBean.getVote_average()));
            binding.language.setText(movieBean.getOriginal_language());
            binding.tvVote.setText(String.valueOf(movieBean.getVote_count()));
            binding.tvStatus.setText(movieBean.getStatus());
            binding.tvRevenue.setText(String.valueOf(movieBean.getRevenue()));
            binding.tvDescription.setText(movieBean.getOverview());
            binding.tvOriginal.setText(movieBean.getOriginal_title());
            this.title = movieBean.getTitle();

            Glide.with(this)
                    .asBitmap()
                    .load(posterPath)
                    .into(binding.ivSmallimg);
            Glide.with(this)
                    .asBitmap()
                    .load(posterPath)
                    .into(binding.ivBig);

            setImageData(id);
        }
    }

    @Override
    public void getTvDetailResponse(DetailTvBean detailTvBean) {
        if (detailTvBean != null && !isFinishing() && !isDestroyed() && isTv) {
            String posterPath = "https://image.tmdb.org/t/p/w500/" + detailTvBean.getPoster_path();
            this.lastImage = posterPath;
            seriesBinding.tvTitle.setText(detailTvBean.getLast_episode_to_air().getName());

            StringBuilder sb = new StringBuilder();
            List<String> names = new ArrayList<>();

            for (DetailTvBean.GenresBean moviename : detailTvBean.getGenres()) {
                names.add(moviename.getName());
            }
            for (int i = 0; i < names.size(); i++) {
                sb.append(names.get(i));
                if (i < detailTvBean.getGenres().size() - 1) {
                    sb.append(", ");
                }
            }

            seriesBinding.tvInfo.setText(sb.toString());
            seriesBinding.tvDate.setText(detailTvBean.getLast_episode_to_air().getAir_date());
            seriesBinding.tvRate.setText(String.format("%.2f", detailTvBean.getLast_episode_to_air().getVote_average()));
            seriesBinding.language.setText(detailTvBean.getOrigin_country().get(0));
            seriesBinding.tvVote.setText(String.valueOf(detailTvBean.getLast_episode_to_air().getVote_count()));
            seriesBinding.tvDescription.setText(detailTvBean.getLast_episode_to_air().getOverview());
            seriesBinding.overView.setVisibility(detailTvBean.getLast_episode_to_air().getOverview().isEmpty() ? View.GONE : View.VISIBLE);
            seriesBinding.tvOriginal.setText(detailTvBean.getLast_episode_to_air().getName());
            this.title = detailTvBean.getLast_episode_to_air().getName();

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.BELOW, seriesBinding.card.getId());
            params.setMargins(30, 0, 0, 0);
            seriesBinding.orig.setLayoutParams(params);

            seriesBinding.status.setVisibility(View.GONE);
            seriesBinding.tvStatus.setVisibility(View.GONE);
            seriesBinding.revenue.setVisibility(View.GONE);
            seriesBinding.tvRevenue.setVisibility(View.GONE);
            seriesBinding.ivBook.setVisibility(View.GONE);
            seriesBinding.tvWatch.setVisibility(View.GONE);

            seasonRecycler(detailTvBean.getSeasons());

            Glide.with(this)
                    .asBitmap()
                    .load(posterPath)
                    .into(seriesBinding.ivSmallimg);
            Glide.with(this)
                    .asBitmap()
                    .load(posterPath)
                    .into(seriesBinding.ivBig);

            setImageData(id);
        }
    }
    private void seasonRecycler(List<DetailTvBean.SeasonsBean> seasons) {
        seriesBinding.rvSeasons.setVisibility(View.VISIBLE);
        seasonsAdapter = new SeasonsAdapter();
        seriesBinding.rvSeasons.setLayoutManager(new LinearLayoutManager(this));
        seriesBinding.rvSeasons.setAdapter(seasonsAdapter);
        List<DetailTvBean.SeasonsBean> specialsSeasons = new ArrayList<>();
        for (DetailTvBean.SeasonsBean seasonsBean : seasons) {
            if (!"Specials".equals(seasonsBean.getName())) {
                specialsSeasons.add(seasonsBean);
            }
        }

        seasonsAdapter.setNewData(specialsSeasons);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new WindowUtils(this);
        setImageData(id);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}