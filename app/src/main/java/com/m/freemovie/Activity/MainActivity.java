package com.m.freemovie.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.m.freemovie.Fragment.MovieFragment;
import com.m.freemovie.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, new MovieFragment())
                .commit();
    }
}