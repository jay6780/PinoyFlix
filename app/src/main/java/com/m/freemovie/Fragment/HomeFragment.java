package com.m.freemovie.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.app.hubert.guide.NewbieGuide;
import com.app.hubert.guide.core.Controller;
import com.app.hubert.guide.listener.OnGuideChangedListener;
import com.app.hubert.guide.model.GuidePage;
import com.app.hubert.guide.model.HighLight;
import com.m.freemovie.R;
import com.m.freemovie.Retrofit.AppConstant;
import com.m.freemovie.Utils.SPUtils;
import com.m.freemovie.databinding.FragmentHomeBinding;
import com.m.freemovie.mvp.ClassBean.MovieEvent;

import org.greenrobot.eventbus.EventBus;

public class HomeFragment extends Fragment implements View.OnClickListener {
    private FragmentHomeBinding binding;
    private Fragment movieFragment, tvSeriesFragment,animeFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater);
        binding.tvMovies.setOnClickListener(this);
        binding.tvSeries.setOnClickListener(this);
        binding.tvTagalog.setOnClickListener(this);
        movieFragment = new MovieFragment();
        tvSeriesFragment = new TvSeriesFragment();
        animeFragment = new AnimeFragment();
        getChildFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, movieFragment, "movie")
                .add(R.id.fragment_container, tvSeriesFragment, "tvSeries")
                .add(R.id.fragment_container, animeFragment, "animeFragment")
                .hide(tvSeriesFragment)
                .hide(animeFragment)
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
                .show();
    }

    @Override
    public void onClick(View view) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

        switch (view.getId()) {
            case R.id.tv_movies:
                transaction.show(movieFragment);
                transaction.hide(tvSeriesFragment);
                transaction.hide(animeFragment);
                binding.tvMovies.setTextColor(getResources().getColor(R.color.SecondColor));
                binding.tvSeries.setTextColor(getResources().getColor(R.color.white));
                binding.tvTagalog.setTextColor(getResources().getColor(R.color.white));
                EventBus.getDefault().post(new MovieEvent(1));
                break;
            case R.id.tv_series:
                transaction.show(tvSeriesFragment);
                transaction.hide(movieFragment);
                transaction.hide(animeFragment);
                binding.tvSeries.setTextColor(getResources().getColor(R.color.SecondColor));
                binding.tvMovies.setTextColor(getResources().getColor(R.color.white));
                binding.tvTagalog.setTextColor(getResources().getColor(R.color.white));
                EventBus.getDefault().post(new MovieEvent(2));
                break;

            case R.id.tv_tagalog:
                transaction.show(animeFragment);
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