package com.m.freemovie.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.m.freemovie.R;
import com.m.freemovie.Retrofit.AppConstant;
import com.m.freemovie.Utils.SPUtils;
import com.m.freemovie.databinding.FragmentHomeBinding;
import com.m.freemovie.mvp.ClassBean.MovieEvent;

import org.greenrobot.eventbus.EventBus;

public class HomeFragment extends Fragment implements View.OnClickListener {
    private FragmentHomeBinding binding;
    private Fragment movieFragment, tvSeriesFragment,tagalogFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater);
        binding.tvMovies.setOnClickListener(this);
        binding.tvSeries.setOnClickListener(this);
        binding.tvTagalog.setOnClickListener(this);
        movieFragment = new MovieFragment();
        tvSeriesFragment = new TvSeriesFragment();
        tagalogFragment = new ChooseFragment();
        getChildFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, movieFragment, "movie")
                .add(R.id.fragment_container, tvSeriesFragment, "tvSeries")
                .add(R.id.fragment_container, tagalogFragment, "tagalogFragment")
                .hide(tvSeriesFragment)
                .hide(tagalogFragment)
                .commit();
        binding.tvMovies.setTextColor(getResources().getColor(R.color.SecondColor));
        binding.tvSeries.setTextColor(getResources().getColor(R.color.white));
        binding.tvTagalog.setTextColor(getResources().getColor(R.color.white));
        return binding.getRoot();
    }

    @Override
    public void onClick(View view) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

        switch (view.getId()) {
            case R.id.tv_movies:
                transaction.show(movieFragment);
                transaction.hide(tvSeriesFragment);
                transaction.hide(tagalogFragment);
                binding.tvMovies.setTextColor(getResources().getColor(R.color.SecondColor));
                binding.tvSeries.setTextColor(getResources().getColor(R.color.white));
                binding.tvTagalog.setTextColor(getResources().getColor(R.color.white));
                EventBus.getDefault().post(new MovieEvent(1));
                break;
            case R.id.tv_series:
                transaction.show(tvSeriesFragment);
                transaction.hide(movieFragment);
                transaction.hide(tagalogFragment);
                binding.tvSeries.setTextColor(getResources().getColor(R.color.SecondColor));
                binding.tvMovies.setTextColor(getResources().getColor(R.color.white));
                binding.tvTagalog.setTextColor(getResources().getColor(R.color.white));
                EventBus.getDefault().post(new MovieEvent(2));
                break;

            case R.id.tv_tagalog:
                transaction.show(tagalogFragment);
                transaction.hide(movieFragment);
                transaction.hide(tvSeriesFragment);
                binding.tvTagalog.setTextColor(getResources().getColor(R.color.SecondColor));
                binding.tvMovies.setTextColor(getResources().getColor(R.color.white));
                binding.tvSeries.setTextColor(getResources().getColor(R.color.white));
                EventBus.getDefault().post(new MovieEvent(SPUtils.getInstance().getInt(AppConstant.lastposition,3)));
                break;
        }

        transaction.commit();
    }

}