package com.m.freemovie.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.m.freemovie.Fragment.BookmarkFragment;
import com.m.freemovie.Fragment.MovieFragment;
import com.m.freemovie.Fragment.SearchFragment;
import com.m.freemovie.R;
import com.m.freemovie.databinding.ActivityMainBinding;

import meow.bottomnavigation.MeowBottomNavigation;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityMainBinding binding;
    private LinearLayout ll_file;
    private DrawerLayout drawerLayout;
    private LinearLayout navigationView;
    private ImageView btn_back5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        initializeBottomNavigation();
        ll_file = findViewById(R.id.ll_file);
        btn_back5 = findViewById(R.id.btn_back5);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        ll_file.setOnClickListener(this);
        btn_back5.setOnClickListener(this);
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
    }

    private void initializeBottomNavigation() {
        binding.nav.add(new MeowBottomNavigation.Model(1, R.drawable.ic_baseline_search_24));
        binding.nav.add(new MeowBottomNavigation.Model(2, R.drawable.ic_baseline_home_24));
        binding.nav.add(new MeowBottomNavigation.Model(3, R.drawable.unbooked));

        Fragment searchFragment = new SearchFragment();
        Fragment movieFragment = new MovieFragment();
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
        }

    }
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            binding.fragmentContainer.setVisibility(View.GONE);
            drawerLayout.requestDisallowInterceptTouchEvent(true);
        } else {
            super.onBackPressed();
            finish();
        }
    }
}