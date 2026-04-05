package com.m.freemovie.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.m.freemovie.Activity.ViewAllAnimeAcitvity;
import com.m.freemovie.R;
import com.m.freemovie.Retrofit.AppConstant;
import com.m.freemovie.adapter.AnimeMovieAdapter;
import com.m.freemovie.adapter.AnimePopularAdapter;
import com.m.freemovie.adapter.HotAdapter;
import com.m.freemovie.adapter.NewestAdapter;
import com.m.freemovie.databinding.FragmentAnimeBinding;
import com.m.freemovie.mvp.Model.ClassBean.AnimoPageBean;
import com.m.freemovie.mvp.Model.ClassBean.PaheLatestBean;
import com.m.freemovie.mvp.Model.ClassBean.RevivalSeriesBean;
import com.m.freemovie.mvp.Model.ClassBean.TagalogBean;
import com.m.freemovie.mvp.Contract.AnimeContract;
import com.m.freemovie.mvp.Presenter.AnimePresenter;

import java.util.ArrayList;
import java.util.List;

public class AnimeFragment extends Fragment implements AnimeContract.View, View.OnClickListener{
    private FragmentAnimeBinding binding;
    private AnimePresenter presenter;
    private int page = 1;
    private NewestAdapter newestAdapter;
    private HotAdapter hotAdapter;
    private AnimePopularAdapter animePopularAdapter;
    private AnimeMovieAdapter movieAdapter;
    private List<PaheLatestBean.ResultsBean.DataBean> newestList = new ArrayList<>();
    private List<AnimoPageBean.ResultsBean> hotList = new ArrayList<>();
    private List<RevivalSeriesBean.ResultsBean> popularList = new ArrayList<>();
    private List<RevivalSeriesBean.ResultsBean> movieList = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAnimeBinding.inflate(inflater);
        presenter = new AnimePresenter(this);
        if(isNetworkAvailable()){
            presenter.getNewestPage(page);
//            presenter.getHotPage(page);
            presenter.getPopular(page);
            presenter.getMovie(page);
        }else{
            Toast.makeText(getContext(),"Please check internet and try again",Toast.LENGTH_SHORT).show();
        }

        List<View> viewsList = new ArrayList<>();
        viewsList.add(binding.tvNew);
        viewsList.add(binding.tvHot);
        viewsList.add(binding.tvPopular);
        viewsList.add(binding.tvMovies);

        for(View view : viewsList){
            view.setOnClickListener(this);
        }

        initRecycler();

        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });


        return binding.getRoot();
    }

    private void refresh() {
        if(!isNetworkAvailable()){
            if (binding.swipeRefreshLayout.isRefreshing()) {
                binding.swipeRefreshLayout.setRefreshing(false);
            }
            Toast.makeText(getContext(),"Please check network and try again",Toast.LENGTH_SHORT).show();
            return;
        }
        clearAllData();
        page = 1;
        presenter.getNewestPage(page);
//        presenter.getHotPage(page);
        presenter.getPopular(page);
        presenter.getMovie(page);
    }

    private void clearAllData() {
        binding.rvNewest.setVisibility(View.GONE);
        binding.rlNew.setVisibility(View.GONE);
        binding.rvHot.setVisibility(View.GONE);
        binding.rlHot.setVisibility(View.GONE);
        binding.rvPopular.setVisibility(View.GONE);
        binding.rlPopular.setVisibility(View.GONE);
        binding.rvMovies.setVisibility(View.GONE);
        binding.rlMovies.setVisibility(View.GONE);

        newestList.clear();
        hotList.clear();
        popularList.clear();
        movieList.clear();

        newestAdapter.setNewData(newestList);
        hotAdapter.setNewData(hotList);
        animePopularAdapter.setNewData(popularList);
        movieAdapter.setNewData(movieList);

        binding.rvNewest.scrollToPosition(0);
        binding.rvHot.scrollToPosition(0);
        binding.rvPopular.scrollToPosition(0);
        binding.rvMovies.scrollToPosition(0);
    }

    private void initRecycler() {
        newestAdapter = new NewestAdapter();
        hotAdapter = new HotAdapter();
        animePopularAdapter = new AnimePopularAdapter();
        movieAdapter = new AnimeMovieAdapter();

        binding.rvNewest.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvNewest.setAdapter(newestAdapter);

        binding.rvHot.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvHot.setAdapter(hotAdapter);

        binding.rvPopular.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvPopular.setAdapter(animePopularAdapter);
        animePopularAdapter.isMovie(false);

        binding.rvMovies.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvMovies.setAdapter(movieAdapter);
        movieAdapter.isMovie(true);

    }
    @SuppressWarnings("deprecation")
    @SuppressLint("MissingPermission")
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void showLoading() {
        binding.swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void showError(String error) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(),"Error fetching data: "+error,Toast.LENGTH_SHORT).show();
                binding.swipeRefreshLayout.setRefreshing(false);
            }
        }, 500);
    }

    @Override
    public void hideLoading() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.swipeRefreshLayout.setRefreshing(false);
            }
        }, 500);
    }

    //limit list to 10 inside loop
    @Override
    public void getNewest(PaheLatestBean paheLatestBean) {
        if(paheLatestBean !=null && paheLatestBean.getResults().getData() !=null){
            if(!paheLatestBean.getResults().getData().isEmpty()){
                for (int i = 0; i < 10 ; i ++){
                    newestList.add(paheLatestBean.getResults().getData().get(i));
                }
                newestAdapter.setNewData(newestList);
                binding.rvNewest.setVisibility(View.VISIBLE);
                binding.rlNew.setVisibility(View.VISIBLE);
            }else{
                binding.rvNewest.setVisibility(View.GONE);
                binding.rlNew.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void getHot(AnimoPageBean animoPageBean) {
        if(animoPageBean !=null && animoPageBean.getResults() !=null){
            if(!animoPageBean.getResults().isEmpty()){
                for (int i = 0; i < 10 ; i ++){
                    hotList.add(animoPageBean.getResults().get(i));
                }
                hotAdapter.setNewData(hotList);
                binding.rvHot.setVisibility(View.VISIBLE);
                binding.rlHot.setVisibility(View.VISIBLE);
            }else{
                binding.rvHot.setVisibility(View.GONE);
                binding.rlHot.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void getPopular(RevivalSeriesBean revivalSeriesBean) {
        if(revivalSeriesBean !=null && revivalSeriesBean.getResults()!=null){
            if(!revivalSeriesBean.getResults().isEmpty()){
                for (int i = 0; i < 10 ; i ++){
                    popularList.add(revivalSeriesBean.getResults().get(i));
                }
                animePopularAdapter.setNewData(popularList);
                binding.rvPopular.setVisibility(View.VISIBLE);
                binding.rlPopular.setVisibility(View.VISIBLE);
            }else{
                binding.rvPopular.setVisibility(View.GONE);
                binding.rlPopular.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void getMovie(RevivalSeriesBean revivalSeriesBean) {
        if(revivalSeriesBean !=null && revivalSeriesBean.getResults()!=null){
            if(!revivalSeriesBean.getResults().isEmpty()){
                for (int i = 0; i < 10 ; i ++){
                    movieList.add(revivalSeriesBean.getResults().get(i));
                }
                movieAdapter.setNewData(movieList);
                binding.rvMovies.setVisibility(View.VISIBLE);
                binding.rlMovies.setVisibility(View.VISIBLE);
            }else{
                binding.rvMovies.setVisibility(View.GONE);
                binding.rlMovies.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.tv_new:
                 intent = new Intent(getActivity(), ViewAllAnimeAcitvity.class);
                 intent.putExtra("title", AppConstant.NEWEST);
                 intent.putExtra("position",1);
                 startActivity(intent);
                break;
            case R.id.tv_hot:
                intent = new Intent(getActivity(), ViewAllAnimeAcitvity.class);
                intent.putExtra("title", AppConstant.HOT);
                intent.putExtra("position",2);
                startActivity(intent);
                break;
            case R.id.tv_popular:
                intent = new Intent(getActivity(), ViewAllAnimeAcitvity.class);
                intent.putExtra("title", AppConstant.POPULAR);
                intent.putExtra("position",3);
                startActivity(intent);
                break;
            case R.id.tv_movies:
                intent = new Intent(getActivity(), ViewAllAnimeAcitvity.class);
                intent.putExtra("title", AppConstant.MOVIES);
                intent.putExtra("position",4);
                startActivity(intent);
                break;


        }
    }
}