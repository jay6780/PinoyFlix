package com.m.freemovie.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.app.hubert.guide.NewbieGuide;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.m.freemovie.Fragment.BookmarkFragment;
import com.m.freemovie.Fragment.HomeFragment;
import com.m.freemovie.Fragment.SearchFragment;
import com.m.freemovie.R;
import com.m.freemovie.Retrofit.AppConstant;
import com.m.freemovie.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

import meow.bottomnavigation.MeowBottomNavigation;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityMainBinding binding;
    private LinearLayout ll_file,ll_guide;
    private DrawerLayout drawerLayout;
    private LinearLayout navigationView;
    private ImageView btn_back5;
    private static final int RESET_GUIDE_REQUEST_CODE = 100;
    private long pressedTime;
    private InterstitialAd mInterstitialAd;
    private String TAG ="MainAd";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        initializeBottomNavigation();
        initPermission();
        ll_file = findViewById(R.id.ll_file);
        btn_back5 = findViewById(R.id.btn_back5);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        ll_guide = findViewById(R.id.ll_guide);
        ll_file.setOnClickListener(this);
        btn_back5.setOnClickListener(this);
        ll_guide.setOnClickListener(this);



        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                binding.fragmentContainer.setVisibility(View.GONE);
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                binding.fragmentContainer.setVisibility(View.GONE);
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                binding.fragmentContainer.setVisibility(View.VISIBLE);
            }

            @Override
            public void onDrawerStateChanged(int newState) {}
        });

        if (getIntent().getBooleanExtra("resetGuide", false)) {
            resetGuideLabels();
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
        switch (view.getId()){
            case R.id.btn_back5:
                if (drawerLayout.isDrawerOpen(navigationView)) {
                    binding.fragmentContainer.setVisibility(View.VISIBLE);
                    drawerLayout.closeDrawer(navigationView);
                } else {
                    binding.fragmentContainer.setVisibility(View.GONE);
                    drawerLayout.openDrawer(navigationView);
                }
                break;
            case R.id.ll_file:
                startActivity(new Intent(getApplicationContext(),Download_videoActivity.class));
                break;
            case R.id.ll_guide:

                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
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
        for (String reset : guideString) {
            NewbieGuide.resetLabel(getApplicationContext(), reset);
        }
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            binding.fragmentContainer.setVisibility(View.GONE);
            drawerLayout.requestDisallowInterceptTouchEvent(true);
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