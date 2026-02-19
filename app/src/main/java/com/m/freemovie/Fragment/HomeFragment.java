package com.m.freemovie.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;

import com.app.hubert.guide.NewbieGuide;
import com.app.hubert.guide.core.Controller;
import com.app.hubert.guide.listener.OnGuideChangedListener;
import com.app.hubert.guide.model.GuidePage;
import com.app.hubert.guide.model.HighLight;
import com.m.freemovie.R;
import com.m.freemovie.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment implements View.OnClickListener {
    private FragmentHomeBinding binding;
    private Fragment movieFragment, tvSeriesFragment, animeFragment,tagalogMovieFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater);
        binding.tvMovies.setOnClickListener(this);
        binding.tvSeries.setOnClickListener(this);
        binding.tvTagalog.setOnClickListener(this);
        binding.tvOthers.setOnClickListener(this);

        movieFragment = new MovieFragment();

        getChildFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, movieFragment, "movie")
                .setMaxLifecycle(movieFragment, Lifecycle.State.STARTED)
                .commit();

        binding.tvMovies.setTextColor(getResources().getColor(R.color.SecondColor));
        binding.tvSeries.setTextColor(getResources().getColor(R.color.white));
        binding.tvTagalog.setTextColor(getResources().getColor(R.color.white));
        initGuide();


        return binding.getRoot();
    }


    private void initGuide() {
        NewbieGuide.with(getActivity())
                .setLabel("home_guide")
                .setOnGuideChangedListener(new OnGuideChangedListener() {
                    @Override
                    public void onShowed(Controller controller) {

                    }

                    @Override
                    public void onRemoved(Controller controller) {
                    }
                })
                .addGuidePage(GuidePage.newInstance()
                        .addHighLight(binding.tvMovies, HighLight.Shape.ROUND_RECTANGLE, 1)
                        .setLayoutRes(R.layout.guide_movie)
                )
                .addGuidePage(GuidePage.newInstance()
                        .addHighLight(binding.tvSeries, HighLight.Shape.ROUND_RECTANGLE, 1)
                        .setLayoutRes(R.layout.guide_series)
                )
                .addGuidePage(GuidePage.newInstance()
                        .addHighLight(binding.tvTagalog, HighLight.Shape.ROUND_RECTANGLE, 1)
                        .setLayoutRes(R.layout.guide_tagalog)
                )
                .addGuidePage(GuidePage.newInstance()
                        .addHighLight(binding.tvOthers, HighLight.Shape.ROUND_RECTANGLE, 1)
                        .setLayoutRes(R.layout.guide_other)
                )
                .show();
    }

    @Override
    public void onClick(View view) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

        switch (view.getId()) {
            case R.id.tv_movies:
                if (movieFragment == null) {
                    movieFragment = new MovieFragment();
                    transaction.add(R.id.fragment_container, movieFragment, "movie");
                    transaction.setMaxLifecycle(movieFragment, Lifecycle.State.STARTED);
                } else {
                    transaction.show(movieFragment);
                }
                if (tvSeriesFragment != null) {
                    transaction.hide(tvSeriesFragment);
                }
                if (animeFragment != null) {
                    transaction.hide(animeFragment);
                }

                if (tagalogMovieFragment != null) {
                    transaction.hide(tagalogMovieFragment);
                }
                binding.tvMovies.setTextColor(getResources().getColor(R.color.SecondColor));
                binding.tvSeries.setTextColor(getResources().getColor(R.color.white));
                binding.tvTagalog.setTextColor(getResources().getColor(R.color.white));
                binding.tvOthers.setTextColor(getResources().getColor(R.color.white));
                break;

            case R.id.tv_series:
                if (tvSeriesFragment == null) {
                    tvSeriesFragment = new TvSeriesFragment();
                    transaction.add(R.id.fragment_container, tvSeriesFragment, "tvSeries");
                    transaction.setMaxLifecycle(tvSeriesFragment, Lifecycle.State.STARTED);
                } else {
                    transaction.show(tvSeriesFragment);
                }
                if(movieFragment !=null){
                    transaction.hide(movieFragment);
                }
                if (animeFragment != null) {
                    transaction.hide(animeFragment);
                }
                if (tagalogMovieFragment != null) {
                    transaction.hide(tagalogMovieFragment);
                }
                binding.tvSeries.setTextColor(getResources().getColor(R.color.SecondColor));
                binding.tvMovies.setTextColor(getResources().getColor(R.color.white));
                binding.tvTagalog.setTextColor(getResources().getColor(R.color.white));
                binding.tvOthers.setTextColor(getResources().getColor(R.color.white));
                break;

            case R.id.tv_tagalog:
                if (animeFragment == null) {
                    animeFragment = new AnimeFragment();
                    transaction.add(R.id.fragment_container, animeFragment, "animeFragment");
                    transaction.setMaxLifecycle(animeFragment, Lifecycle.State.STARTED);
                } else {
                    transaction.show(animeFragment);
                }
                if(movieFragment !=null){
                    transaction.hide(movieFragment);
                }
                if (tvSeriesFragment != null) {
                    transaction.hide(tvSeriesFragment);
                }
                if (tagalogMovieFragment != null) {
                    transaction.hide(tagalogMovieFragment);
                }
                binding.tvTagalog.setTextColor(getResources().getColor(R.color.SecondColor));
                binding.tvMovies.setTextColor(getResources().getColor(R.color.white));
                binding.tvSeries.setTextColor(getResources().getColor(R.color.white));
                binding.tvOthers.setTextColor(getResources().getColor(R.color.white));
                break;

            case R.id.tv_others:
                if (tagalogMovieFragment == null) {
                    tagalogMovieFragment = new GenreRuFragment();
                    transaction.add(R.id.fragment_container, tagalogMovieFragment, "tagalogFragment");
                    transaction.setMaxLifecycle(tagalogMovieFragment, Lifecycle.State.STARTED);
                } else {
                    transaction.show(tagalogMovieFragment);
                }

                if(movieFragment !=null){
                    transaction.hide(movieFragment);
                }
                if (animeFragment != null) {
                    transaction.hide(animeFragment);
                }
                if (tvSeriesFragment != null) {
                    transaction.hide(tvSeriesFragment);
                }
                binding.tvOthers.setTextColor(getResources().getColor(R.color.SecondColor));
                binding.tvTagalog.setTextColor(getResources().getColor(R.color.white));
                binding.tvMovies.setTextColor(getResources().getColor(R.color.white));
                binding.tvSeries.setTextColor(getResources().getColor(R.color.white));
                break;
        }

        transaction.commit();
    }

}