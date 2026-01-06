package com.m.freemovie.Fragment;

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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.m.freemovie.Activity.ViewAllActivity;
import com.m.freemovie.R;
import com.m.freemovie.adapter.MovieAdapter;
import com.m.freemovie.adapter.MovieNowAdapter;
import com.m.freemovie.databinding.FragmentMovieBinding;
import com.m.freemovie.mvp.ClassBean.MovieBean;
import com.m.freemovie.mvp.Contract.MovieContract;
import com.m.freemovie.mvp.Presenter.MoviePresenter;

import java.util.ArrayList;
import java.util.List;

public class MovieFragment extends Fragment implements MovieContract.View,View.OnClickListener {
    private FragmentMovieBinding binding;
    private MoviePresenter moviePresenter;
    private int page = 1;
    private MovieAdapter popularAdapter,topRatedAdapter,upcommingAdapter;
    private MovieNowAdapter movieNowAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMovieBinding.inflate(inflater);

        List<View> viewsList = new ArrayList<>();
        viewsList.add(binding.tvPopular);
        viewsList.add(binding.tvTopRated);
        viewsList.add(binding.tvNowPlaying);
        viewsList.add(binding.tvUpComing);

        for(View v : viewsList){
            v.setOnClickListener(this);
        }

        initRecycler();
        moviePresenter = new MoviePresenter(this);

        moviePresenter.getPopularMovie(getString(R.string.key),page);
        moviePresenter.getTopRated(getString(R.string.key),page);
        moviePresenter.getUpcoming(getString(R.string.key),page);
        moviePresenter.getNow(getString(R.string.key),page);

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

        page = 1;
        clearAllData();
        moviePresenter.getPopularMovie(getString(R.string.key),page);
        moviePresenter.getTopRated(getString(R.string.key),page);
        moviePresenter.getUpcoming(getString(R.string.key),page);
        moviePresenter.getNow(getString(R.string.key),page);
    }


    private void clearAllData() {
        binding.rlUpcoming.setVisibility(View.GONE);
        binding.rlTopRated.setVisibility(View.GONE);
        binding.rlNow.setVisibility(View.GONE);
        binding.rlPopular.setVisibility(View.GONE);
        binding.rvPopular.setVisibility(View.GONE);
        binding.rvTopRated.setVisibility(View.GONE);
        binding.rvUpComing.setVisibility(View.GONE);
        binding.rvNowPlaying.setVisibility(View.GONE);
        List<MovieAdapter> movieAdapters = new ArrayList<>();
        movieAdapters.add(popularAdapter);
        movieAdapters.add(upcommingAdapter);
        movieAdapters.add(topRatedAdapter);
        for(MovieAdapter movieAdapter : movieAdapters){
            movieAdapter.setNewData(new ArrayList<>());
        }
        movieNowAdapter.setNewData(new ArrayList<>());

        List<RecyclerView> recyclerViewList = new ArrayList<>();
        recyclerViewList.add(binding.rvNowPlaying);
        recyclerViewList.add(binding.rvPopular);
        recyclerViewList.add(binding.rvTopRated);
        recyclerViewList.add(binding.rvUpComing);

        for (RecyclerView recyclerView : recyclerViewList){
            recyclerView.scrollToPosition(0);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void initRecycler() {
        binding.rvPopular.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvTopRated.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvNowPlaying.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvUpComing.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        popularAdapter = new MovieAdapter();
        binding.rvPopular.setAdapter(popularAdapter);

        topRatedAdapter = new MovieAdapter();
        binding.rvTopRated.setAdapter(topRatedAdapter);

        movieNowAdapter = new MovieNowAdapter();
        binding.rvNowPlaying.setAdapter(movieNowAdapter);

        upcommingAdapter = new MovieAdapter();
        binding.rvUpComing.setAdapter(upcommingAdapter);
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


    @Override
    public void getPopularResponse(MovieBean movieBean) {
        if(movieBean!=null && movieBean.getResults() != null){
            if(!movieBean.getResults().isEmpty()) {
                binding.rvPopular.setVisibility(View.VISIBLE);
                binding.rlPopular.setVisibility(View.VISIBLE);
                popularAdapter.setNewData(movieBean.getResults());
            }else{
                binding.rvPopular.setVisibility(View.GONE);
                binding.rlPopular.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void getTopRatedResponse(MovieBean movieBean) {
        if(movieBean!=null && movieBean.getResults() != null){
            if(!movieBean.getResults().isEmpty()) {
                binding.rvTopRated.setVisibility(View.VISIBLE);
                binding.rlTopRated.setVisibility(View.VISIBLE);
                topRatedAdapter.setNewData(movieBean.getResults());
            }
        }else{
            binding.rvTopRated.setVisibility(View.GONE);
            binding.rlTopRated.setVisibility(View.GONE);
        }
    }

    @Override
    public void getUpcomingResponse(MovieBean movieBean) {
        if(movieBean!=null && movieBean.getResults() != null){
            if(!movieBean.getResults().isEmpty()) {
                binding.rvUpComing.setVisibility(View.VISIBLE);
                binding.rlUpcoming.setVisibility(View.VISIBLE);
                upcommingAdapter.setNewData(movieBean.getResults());
            }
        }else{
            binding.rvUpComing.setVisibility(View.GONE);
            binding.rlUpcoming.setVisibility(View.GONE);
        }
    }

    @Override
    public void getNowResponse(MovieBean movieBean) {
        if(movieBean!=null && movieBean.getResults() != null){
            if(!movieBean.getResults().isEmpty()) {
                binding.rvNowPlaying.setVisibility(View.VISIBLE);
                binding.rlNow.setVisibility(View.VISIBLE);
                movieNowAdapter.setNewData(movieBean.getResults());
            }
        }else{
            binding.rvNowPlaying.setVisibility(View.GONE);
            binding.rlNow.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.tv_popular:
                intent = new Intent(getContext(), ViewAllActivity.class);
                intent.putExtra("position",1);
                intent.putExtra("isTvSeries",false);
                startActivity(intent);
                break;
            case R.id.tv_topRated:
                intent = new Intent(getContext(), ViewAllActivity.class);
                intent.putExtra("position",2);
                intent.putExtra("isTvSeries",false);
                startActivity(intent);
                break;
            case R.id.tv_nowPlaying:
                intent = new Intent(getContext(), ViewAllActivity.class);
                intent.putExtra("position",3);
                intent.putExtra("isTvSeries",false);
                startActivity(intent);
                break;
            case R.id.tv_upComing:
                intent = new Intent(getContext(), ViewAllActivity.class);
                intent.putExtra("position",4);
                intent.putExtra("isTvSeries",false);
                startActivity(intent);
                break;
        }

    }

}