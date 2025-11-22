package com.m.freemovie.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.m.freemovie.R;
import com.m.freemovie.Utils.SPUtils;
import com.m.freemovie.Utils.WindowUtils;
import com.m.freemovie.databinding.ActivityDetailsBinding;
import com.m.freemovie.mvp.ClassBean.DetailBean;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait");

        detailPresenter = new DetailPresenter(this);
        id = getIntent().getStringExtra("id");
        binding.ivBack.setOnClickListener(view -> finish());
        binding.ivBook.setOnClickListener(view -> savedBook());
        detailPresenter.getDetail(id,getString(R.string.key));
        binding.tvWatch.setOnClickListener(view -> watchNow());
        spUtils = SPUtils.getInstance("detailPrefs");

        setImageData(id);
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
            binding.ivBook.setImageResource(R.drawable.unbooked);
        } else {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
            DetailBean details = new DetailBean(id, timestamp,lastImage,title);
            details.setVideoId(id);
            details.setTimeStamp(timestamp);
            detailBeans.add(0, details);
            binding.ivBook.setImageResource(R.drawable.booked);
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
        binding.ivBook.setImageResource(isBookmarked ? R.drawable.booked : R.drawable.unbooked);
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
        Intent intent = new Intent(this, VideoWebviewActivity.class);
        intent.putExtra("title",title);
        intent.putExtra("videoId",id);
        startActivity(intent);
    }

    @Override
    public void showLoading() {
        hud.show();
    }

    @Override
    public void showError(String error) {

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
        if (movieBean != null && !isFinishing() && !isDestroyed()) {
            String posterPath = "https://image.tmdb.org/t/p/w500/" + movieBean.getPoster_path();
            this.lastImage = posterPath;
            binding.tvTitle.setText(movieBean.getOriginal_title());

            StringBuilder sb = new StringBuilder();
            List<String> names = new ArrayList<>();

            for(DetailBean.GenresBean moviename : movieBean.getGenres()){
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