package com.m.freemovie.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.m.freemovie.R;
import com.m.freemovie.Utils.DbHelper.BookmarkDbHelper;
import com.m.freemovie.Utils.WindowUtils;
import com.m.freemovie.adapter.SeasonsAdapter;
import com.m.freemovie.databinding.ActivityDetailsBinding;
import com.m.freemovie.databinding.ActivityDetailsSeriesBinding;
import com.m.freemovie.mvp.ClassBean.DetailBean;
import com.m.freemovie.mvp.ClassBean.DetailTvBean;
import com.m.freemovie.mvp.Contract.DetailContract;
import com.m.freemovie.mvp.Presenter.DetailPresenter;

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
    private String lastImage;
    private ActivityDetailsBinding binding;
    private ActivityDetailsSeriesBinding seriesBinding;
    private SeasonsAdapter seasonsAdapter;
    private BookmarkDbHelper dbHelper;
    private int position = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        id = getIntent().getStringExtra("id");
        position = getIntent().getIntExtra("position",2);
//        Log.d("IsTv", "value: " + isTv + " id: " + id);

        if (position == 2) {
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
        dbHelper = new BookmarkDbHelper(this);

        if (position == 2) {
            seriesBinding.ivBack.setOnClickListener(view -> finish());
            seriesBinding.ivBook.setOnClickListener(view -> savedBook());
            seriesBinding.tvWatch.setOnClickListener(view -> watchNow());
        } else {
            binding.ivBack.setOnClickListener(view -> finish());
            binding.ivBook.setOnClickListener(view -> savedBook());
            binding.tvWatch.setOnClickListener(view -> watchNow());
        }

        setImageData(id);

        if (position == 2) {
            detailPresenter.getTvDetail(id, getString(R.string.key));
        } else {
            detailPresenter.getDetail(id, getString(R.string.key));
        }
    }

    private void savedBook() {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
        DetailBean details = new DetailBean(id, timestamp, lastImage, title,"false");
        details.setVideoId(id);
        details.setTimeStamp(timestamp);
        dbHelper.toggleBookmark(details, position);
        setImageData(id);
    }

    private void setImageData(String videoId) {
        boolean isBookmarked = dbHelper.isBookmarked(videoId);
        if (position == 2) {
            seriesBinding.ivBook.setImageResource(isBookmarked ? R.drawable.booked : R.drawable.unbooked);
        } else {
            binding.ivBook.setImageResource(isBookmarked ? R.drawable.booked : R.drawable.unbooked);
        }
    }

    private void watchNow() {
        String[] videoPlayer = {"Player 1", "Player 2"};

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
                        intent.putExtra("videoPosition", 2);
                        intent.putExtra("videoId", id);
                        break;
                    case 1:
                        intent = new Intent(Details_activity.this, VideoWebviewActivity.class);
                        intent.putExtra("title", title);
                        intent.putExtra("videoPosition", 4);
                        intent.putExtra("videoId", id);
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
        new WindowUtils(this,false,false);
    }

    @Override
    public void getDetailResponse(DetailBean movieBean) {
        if (movieBean != null && !isFinishing() && !isDestroyed()) {
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
    private String tvSeriesName;
    @Override
    public void getTvDetailResponse(DetailTvBean detailTvBean) {
        if (detailTvBean != null && !isFinishing() && !isDestroyed()) {
            String posterPath = "https://image.tmdb.org/t/p/w500/" + detailTvBean.getPoster_path();
            this.lastImage = posterPath;
            seriesBinding.tvTitle.setText(detailTvBean.getName());
            tvSeriesName = detailTvBean.getName();

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

            if(detailTvBean.getLast_episode_to_air() !=null){
                seriesBinding.tvDate.setText(detailTvBean.getLast_episode_to_air().getAir_date());

                seriesBinding.tvInfo.setText(sb.toString());

                seriesBinding.tvRate.setText(String.format("%.2f", detailTvBean.getLast_episode_to_air().getVote_average()));
                seriesBinding.language.setText(detailTvBean.getOrigin_country().get(0));
                seriesBinding.tvVote.setText(String.valueOf(detailTvBean.getLast_episode_to_air().getVote_count()));
                seriesBinding.tvDescription.setText(detailTvBean.getLast_episode_to_air().getOverview());
                seriesBinding.overView.setVisibility(detailTvBean.getLast_episode_to_air().getOverview().isEmpty() ? View.GONE : View.VISIBLE);
                seriesBinding.tvOriginal.setText(detailTvBean.getName());
                this.title = detailTvBean.getName();

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.BELOW, seriesBinding.card.getId());
                params.setMargins(30, 0, 0, 0);
                seriesBinding.orig.setLayoutParams(params);

                seriesBinding.status.setVisibility(View.GONE);
                seriesBinding.tvStatus.setVisibility(View.GONE);
                seriesBinding.revenue.setVisibility(View.GONE);
                seriesBinding.tvRevenue.setVisibility(View.GONE);
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
    }
    private void seasonRecycler(List<DetailTvBean.SeasonsBean> seasons) {
        seriesBinding.rvSeasons.setVisibility(View.VISIBLE);
        seasonsAdapter = new SeasonsAdapter();
        seriesBinding.rvSeasons.setLayoutManager(new LinearLayoutManager(this));
        seriesBinding.rvSeasons.setAdapter(seasonsAdapter);
        List<DetailTvBean.SeasonsBean> specialsSeasons = new ArrayList<>();
        for (DetailTvBean.SeasonsBean seasonsBean : seasons) {
            if (!"Specials".equals(seasonsBean.getName())) {
                seasonsBean.setTvSeriesName(tvSeriesName);
                seasonsBean.setVideoId(id);
                specialsSeasons.add(seasonsBean);
            }
        }

        seasonsAdapter.setNewData(specialsSeasons);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new WindowUtils(this,false,false);
        if(seasonsAdapter !=null){
            seasonsAdapter.recount();
        }
        setImageData(id);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}