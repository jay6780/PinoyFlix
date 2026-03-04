package com.m.freemovie.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.hubert.guide.NewbieGuide;
import com.m.freemovie.Fragment.BookmarkFragment;
import com.m.freemovie.Fragment.HomeFragment;
import com.m.freemovie.Fragment.SearchFragment;
import com.m.freemovie.R;
import com.m.freemovie.Retrofit.AppConstant;
import com.m.freemovie.Utils.SPUtils;
import com.m.freemovie.Utils.base.BaseQuickAdapter;
import com.m.freemovie.adapter.OptionAdapter;
import com.m.freemovie.databinding.ActivityMainBinding;
import com.m.freemovie.mvp.Model.ClassBean.OptionBean;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import meow.bottomnavigation.MeowBottomNavigation;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityMainBinding binding;

    private ImageView btn_back5;
    private static final int RESET_GUIDE_REQUEST_CODE = 100;
    private long pressedTime;
    private String TAG = "MainAd";
    private RecyclerView rv_option;
    private OptionAdapter optionAdapter;
    private List<OptionBean> optionBeanList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        btn_back5 = findViewById(R.id.btn_back5);
        rv_option = findViewById(R.id.rv_option);
        btn_back5.setOnClickListener(this);
        startHourCount();
        optionAdapter = new OptionAdapter();
        binding.rvOption.setLayoutManager(new LinearLayoutManager(this));
        binding.rvOption.setAdapter(optionAdapter);
        optionBeanList.add(new OptionBean("file"));
        optionBeanList.add(new OptionBean("guide"));
        optionAdapter.setNewData(optionBeanList);


        optionAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.ll_file) {
                    String name = optionAdapter.getData().get(position).getName();
                    switch (name) {
                        case "file":
                            startActivity(new Intent(MainActivity.this, Download_videoActivity.class));
                            break;
                        case "guide":
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogTheme);
                            builder.setTitle("Are you sure want to reset?");
                            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                    intent.putExtra("resetGuide", true);
                                    startActivityForResult(intent, RESET_GUIDE_REQUEST_CODE);
                                    dialog.dismiss();
                                }
                            });
                            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            AlertDialog alert = builder.create();
                            alert.setOnShowListener(dialog -> {
                                alert.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                                alert.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                            });
                            alert.show();
                            break;
                    }
                }
            }
        });


        if (getIntent().getBooleanExtra("resetGuide", false)) {
            resetGuideLabels();
        }

        new CountDownTimer(1500, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                initializeBottomNavigation();
                initPermission();
            }

        }.start();

    }

    private void startHourCount() {
        try {
            long lastResetTime = SPUtils.getInstance().getLong("last_reset_time", 0);
            long currentTime = System.currentTimeMillis();
            long twoHours = 2 * 60 * 60 * 1000;
            long twentyFiveMins = 25 * 60 * 1000;

            long nextResetTime = lastResetTime + twentyFiveMins;

            if (currentTime >= nextResetTime) {
                resetAds();
                SPUtils.getInstance().put("last_reset_time", currentTime);
                nextResetTime = currentTime + twentyFiveMins;
            }

            long remainingTime = nextResetTime - currentTime;
            start(remainingTime, 1000);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resetAds() {
        SPUtils.getInstance().put(AppConstant.adSeries, false);
        SPUtils.getInstance().put(AppConstant.adMovies, false);
        SPUtils.getInstance().put(AppConstant.adAnime, false);
        SPUtils.getInstance().put(AppConstant.adOpen, false);
    }


    private void start(final long miliSecond, final int interval) {
        try {
            new CountDownTimer(miliSecond, interval) {
                @Override
                public void onTick(long millisUntilFinished) {
                    long day = TimeUnit.MILLISECONDS.toDays(millisUntilFinished);
                    millisUntilFinished -= TimeUnit.DAYS.toMillis(day);

                    long hour = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                    millisUntilFinished -= TimeUnit.HOURS.toMillis(hour);

                    long minute = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                    millisUntilFinished -= TimeUnit.MINUTES.toMillis(minute);

                    long second = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);
                    long totalSeconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);

//                    if (totalSeconds % 10 == 0) {
//                        binding.time.setText("Welcome to PinoyFlix");
//                    } else {
//                        binding.time.setText("Ads reset in : " + String.format("%02d:%02d:%02d:%02d", day, hour, minute, second));
//                    }
                }

                @Override
                public void onFinish() {
                    resetAds();
                    startHourCount();
                }
            }.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void initPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
    }
    private void initializeBottomNavigation() {
        binding.nav.add(new MeowBottomNavigation.Model(1, R.drawable.ic_baseline_search_24));
        binding.nav.add(new MeowBottomNavigation.Model(2, R.drawable.ic_baseline_home_24));
        binding.nav.add(new MeowBottomNavigation.Model(3, R.drawable.unbooked));

        Fragment searchFragment = new SearchFragment();
        Fragment movieFragment = new HomeFragment();
        Fragment bookmarkFragment = new BookmarkFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, movieFragment, "movie")
                .add(R.id.fragment_container, searchFragment, "search")
                .add(R.id.fragment_container, bookmarkFragment, "bookmark")
                .hide(searchFragment)
                .hide(bookmarkFragment)
                .commit();

        binding.nav.setOnClickMenuListener(model -> {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.hide(searchFragment)
                    .hide(movieFragment)
                    .hide(bookmarkFragment);
            switch (model.getId()) {
                case 1:
                    transaction.show(searchFragment);
                    break;
                case 2:
                    transaction.show(movieFragment);
                    break;
                case 3:
                    transaction.show(bookmarkFragment);
                    break;
            }

            transaction.commit();
            return null;
        });

        binding.nav.show(2, true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back5:
                if (binding.drawerLayout.isDrawerOpen(binding.navView)) {
                    binding.drawerLayout.closeDrawer(binding.navView);
                } else {
                    binding.drawerLayout.openDrawer(binding.navView);
                }
                break;

        }

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESET_GUIDE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.getBooleanExtra("resetGuide", false)) {
                resetGuideLabels();
            }
        }
    }


    private void resetGuideLabels() {
        List<String> guideString = new ArrayList<>();
        guideString.add("animepahe_bookmark");
        guideString.add("Tv_series");
        guideString.add("indie_movies");
        guideString.add("tagalog_bookmark");
        guideString.add("tagalog_movie");
        guideString.add("tagalog_series");
        guideString.add("long_press");
        guideString.add("home_guide");
        guideString.add("choose_guide");
        guideString.add("view_all_reset");
        guideString.add("book_reset");
        guideString.add("tagalog_movie_reset");
        guideString.add("tagalog1_reset");
        guideString.add("tagalog2_reset");
        guideString.add("pahe_reset");
        guideString.add("Search_reset");
        guideString.add("MovieListReset");
        guideString.add("view_all_reset_anime");
        guideString.add("other_reset");
        guideString.add("genre_filter");
        guideString.add("OtherVideo");
        for (String reset : guideString) {
            NewbieGuide.resetLabel(getApplicationContext(), reset);
        }
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (pressedTime + 2000 > System.currentTimeMillis()) {
                super.onBackPressed();
                finish();
            } else {
                Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
            }
            pressedTime = System.currentTimeMillis();
        }
    }

}