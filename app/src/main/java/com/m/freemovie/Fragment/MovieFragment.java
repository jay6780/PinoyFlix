package com.m.freemovie.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.m.freemovie.Activity.ViewAllActivity;
import com.m.freemovie.R;
import com.m.freemovie.adapter.MovieAdapter;
import com.m.freemovie.adapter.MovieNowAdapter;
import com.m.freemovie.mvp.ClassBean.MovieBean;
import com.m.freemovie.mvp.Contract.MovieContract;
import com.m.freemovie.mvp.Presenter.MoviePresenter;

import java.util.ArrayList;
import java.util.List;

public class MovieFragment extends Fragment implements MovieContract.View,View.OnClickListener {
    private RecyclerView rv_popular,rv_topRated,rv_nowPlaying,rv_upComing;
    private MoviePresenter moviePresenter;
    private int page = 1;
    private MovieAdapter popularAdapter,topRatedAdapter,upcommingAdapter;
    private MovieNowAdapter movieNowAdapter;
    private List<MovieBean.ResultsBean> popularList = new ArrayList<>();
    private List<MovieBean.ResultsBean> topRatingList = new ArrayList<>();
    private List<MovieBean.ResultsBean> nowPlayingList = new ArrayList<>();
    private List<MovieBean.ResultsBean> upCommingList = new ArrayList<>();
    private KProgressHUD hud;
    private TextView tv_popular,tv_topRated,tv_nowPlaying,tv_upComing;
    private RelativeLayout rl_popular,rl_topRated,rl_now,rl_upcoming;
    private SwipeRefreshLayout swipeRefreshLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie, container, false);
        rv_popular = view.findViewById(R.id.rv_popular);
        rv_topRated = view.findViewById(R.id.rv_topRated);
        rv_nowPlaying = view.findViewById(R.id.rv_nowPlaying);
        rv_upComing = view.findViewById(R.id.rv_upComing);
        tv_popular = view.findViewById(R.id.tv_popular);
        tv_topRated = view.findViewById(R.id.tv_topRated);
        tv_nowPlaying = view.findViewById(R.id.tv_nowPlaying);
        tv_upComing = view.findViewById(R.id.tv_upComing);
        rl_popular = view.findViewById(R.id.rl_popular);
        rl_topRated = view.findViewById(R.id.rl_topRated);
        rl_now = view.findViewById(R.id.rl_now);
        rl_upcoming = view.findViewById(R.id.rl_upcoming);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        List<View> viewsList = new ArrayList<>();
        viewsList.add(tv_popular);
        viewsList.add(tv_topRated);
        viewsList.add(tv_nowPlaying);
        viewsList.add(tv_upComing);

        for(View v : viewsList){
            v.setOnClickListener(this);
        }

        initRecycler();
        hud = KProgressHUD.create(getContext())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait");
        moviePresenter = new MoviePresenter(this);

        moviePresenter.getPopularMovie(getString(R.string.key),page);
        moviePresenter.getTopRated(getString(R.string.key),page);
        moviePresenter.getUpcoming(getString(R.string.key),page);
        moviePresenter.getNow(getString(R.string.key),page);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        return view;
    }

    private void refresh() {
        if(!isNetworkAvailable()){
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
            Toast.makeText(getContext(),"Please check network and try again",Toast.LENGTH_SHORT).show();
            return;
        }

        page = 1;
        moviePresenter.getPopularMovie(getString(R.string.key),page);
        moviePresenter.getTopRated(getString(R.string.key),page);
        moviePresenter.getUpcoming(getString(R.string.key),page);
        moviePresenter.getNow(getString(R.string.key),page);
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void initRecycler() {
        rv_popular.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rv_topRated.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rv_nowPlaying.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rv_upComing.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        popularAdapter = new MovieAdapter(getContext(),popularList);
        rv_popular.setAdapter(popularAdapter);

        topRatedAdapter = new MovieAdapter(getContext(),topRatingList);
        rv_topRated.setAdapter(topRatedAdapter);

        movieNowAdapter = new MovieNowAdapter(getContext(),nowPlayingList);
        rv_nowPlaying.setAdapter(movieNowAdapter);

        upcommingAdapter = new MovieAdapter(getContext(),upCommingList);
        rv_upComing.setAdapter(upcommingAdapter);
    }

    @Override
    public void showLoading() {
        hud.show();
    }

    @Override
    public void showError(String error) {
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void hideLoading() {
        if (hud != null && hud.isShowing() && isAdded()) {
            hud.dismiss();
        }
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }


    @Override
    public void getPopularResponse(MovieBean movieBean) {
        if(movieBean!=null && movieBean.getResults() != null){
            popularList.clear();
            if(!movieBean.getResults().isEmpty()) {
                rv_popular.setVisibility(View.VISIBLE);
                rl_popular.setVisibility(View.VISIBLE);
                popularList.addAll(movieBean.getResults());
                popularAdapter.notifyDataSetChanged();
            }else{
                rv_popular.setVisibility(View.GONE);
                rl_popular.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void getTopRatedResponse(MovieBean movieBean) {
        if(movieBean!=null && movieBean.getResults() != null){
            topRatingList.clear();
            if(!movieBean.getResults().isEmpty()) {
                rv_topRated.setVisibility(View.VISIBLE);
                rl_topRated.setVisibility(View.VISIBLE);
                topRatingList.addAll(movieBean.getResults());
                topRatedAdapter.notifyDataSetChanged();
            }
        }else{
            rv_topRated.setVisibility(View.GONE);
            rl_topRated.setVisibility(View.GONE);
        }
    }

    @Override
    public void getUpcomingResponse(MovieBean movieBean) {
        if(movieBean!=null && movieBean.getResults() != null){
            upCommingList.clear();
            if(!movieBean.getResults().isEmpty()) {
                rv_upComing.setVisibility(View.VISIBLE);
                rl_upcoming.setVisibility(View.VISIBLE);
                upCommingList.addAll(movieBean.getResults());
                upcommingAdapter.notifyDataSetChanged();
            }
        }else{
            rv_upComing.setVisibility(View.GONE);
            rl_upcoming.setVisibility(View.GONE);
        }
    }

    @Override
    public void getNowResponse(MovieBean movieBean) {
        if(movieBean!=null && movieBean.getResults() != null){
            nowPlayingList.clear();
            if(!movieBean.getResults().isEmpty()) {
                rv_nowPlaying.setVisibility(View.VISIBLE);
                rl_now.setVisibility(View.VISIBLE);
                nowPlayingList.addAll(movieBean.getResults());
                movieNowAdapter.notifyDataSetChanged();
            }
        }else{
            rv_nowPlaying.setVisibility(View.GONE);
            rl_now.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.tv_popular:
                intent = new Intent(getContext(), ViewAllActivity.class);
                intent.putExtra("position",1);
                startActivity(intent);
                break;
            case R.id.tv_topRated:
                intent = new Intent(getContext(), ViewAllActivity.class);
                intent.putExtra("position",2);
                startActivity(intent);
                break;
            case R.id.tv_nowPlaying:
                intent = new Intent(getContext(), ViewAllActivity.class);
                intent.putExtra("position",3);
                startActivity(intent);
                break;
            case R.id.tv_upComing:
                intent = new Intent(getContext(), ViewAllActivity.class);
                intent.putExtra("position",4);
                startActivity(intent);
                break;
        }

    }
}