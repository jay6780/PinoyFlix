package com.m.freemovie.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.m.freemovie.R;
import com.m.freemovie.databinding.FragmentHomeBinding;
import com.m.freemovie.mvp.ClassBean.FreeMovieEvent;

import org.greenrobot.eventbus.EventBus;

public class HomeFragment extends Fragment implements View.OnClickListener {
    private FragmentHomeBinding binding;
    private Fragment movieFragment, TvSeriesFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater);
        binding.tvMovies.setOnClickListener(this);
        binding.tvSeries.setOnClickListener(this);
        movieFragment = new MovieFragment();
        TvSeriesFragment = new TvSeriesFragment();

        getChildFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, movieFragment, "movie")
                .add(R.id.fragment_container, TvSeriesFragment, "tvSeries")
                .hide(TvSeriesFragment)
                .commit();
        binding.tvMovies.setTextColor(getResources().getColor(R.color.SecondColor));
        binding.tvSeries.setTextColor(getResources().getColor(R.color.white));

        return binding.getRoot();
    }

    @Override
    public void onClick(View view) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

        switch (view.getId()) {
            case R.id.tv_movies:
                transaction.show(movieFragment);
                transaction.hide(TvSeriesFragment);
                binding.tvMovies.setTextColor(getResources().getColor(R.color.SecondColor));
                binding.tvSeries.setTextColor(getResources().getColor(R.color.white));
                EventBus.getDefault().post(new FreeMovieEvent(false));
                break;
            case R.id.tv_series:
                transaction.show(TvSeriesFragment);
                transaction.hide(movieFragment);
                binding.tvSeries.setTextColor(getResources().getColor(R.color.SecondColor));
                binding.tvMovies.setTextColor(getResources().getColor(R.color.white));
                EventBus.getDefault().post(new FreeMovieEvent(true));
                break;
        }

        transaction.commit();
    }
}