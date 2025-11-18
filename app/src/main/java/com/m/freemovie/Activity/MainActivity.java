package com.m.freemovie.Activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.m.freemovie.Fragment.BookmarkFragment;
import com.m.freemovie.Fragment.MovieFragment;
import com.m.freemovie.Fragment.SearchFragment;
import com.m.freemovie.R;
import com.m.freemovie.databinding.ActivityMainBinding;

import meow.bottomnavigation.MeowBottomNavigation;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        initializeBottomNavigation();
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
}