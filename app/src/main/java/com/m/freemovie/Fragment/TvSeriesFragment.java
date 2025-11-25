package com.m.freemovie.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.m.freemovie.Activity.ViewAllActivity;
import com.m.freemovie.R;
import com.m.freemovie.adapter.TvSeriesAdapter;
import com.m.freemovie.databinding.FragmentTvseriesBinding;
import com.m.freemovie.mvp.ClassBean.MovieBean;
import com.m.freemovie.mvp.ClassBean.TvSeriesBean;
import com.m.freemovie.mvp.Contract.TvSeriesContract;
import com.m.freemovie.mvp.Presenter.TvSeriesPresenter;

import java.util.ArrayList;
import java.util.List;

public class TvSeriesFragment extends Fragment implements TvSeriesContract.View,View.OnClickListener {
    private FragmentTvseriesBinding binding;
    private TvSeriesPresenter tvSeriesPresenter;
    private int page = 1;
    private TvSeriesAdapter todayTvAdapter,airingTvAdapter,popularTvAdapter,topRatedTvAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTvseriesBinding.inflate(inflater);

        List<View> viewsList = new ArrayList<>();
        viewsList.add(binding.tvToday);
        viewsList.add(binding.tvAir);
        viewsList.add(binding.tvPopular);
        viewsList.add(binding.tvTopRated);

        for(View v : viewsList){
            v.setOnClickListener(this);
        }

        initRecycler();
        tvSeriesPresenter = new TvSeriesPresenter(this);

        tvSeriesPresenter.getTodayTv(getString(R.string.key),page);
        tvSeriesPresenter.getOnAiringTv(getString(R.string.key),page);
        tvSeriesPresenter.getPopularTv(getString(R.string.key),page);
        tvSeriesPresenter.getTopRatedTv(getString(R.string.key),page);

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
        tvSeriesPresenter.getTodayTv(getString(R.string.key),page);
        tvSeriesPresenter.getOnAiringTv(getString(R.string.key),page);
        tvSeriesPresenter.getPopularTv(getString(R.string.key),page);
        tvSeriesPresenter.getTopRatedTv(getString(R.string.key),page);
    }


    private void clearAllData() {
        binding.rlToday.setVisibility(View.GONE);
        binding.rlAir.setVisibility(View.GONE);
        binding.rlPopular.setVisibility(View.GONE);
        binding.rlPopular.setVisibility(View.GONE);
        binding.rlTopRated.setVisibility(View.GONE);

        binding.rvToday.setVisibility(View.GONE);
        binding.rvAir.setVisibility(View.GONE);
        binding.rvPopular.setVisibility(View.GONE);
        List<TvSeriesAdapter> tvSeriesAdapterList = new ArrayList<>();
        tvSeriesAdapterList.add(todayTvAdapter);
        tvSeriesAdapterList.add(airingTvAdapter);
        tvSeriesAdapterList.add(popularTvAdapter);
        tvSeriesAdapterList.add(topRatedTvAdapter);
        for(TvSeriesAdapter tvSeriesAdapter : tvSeriesAdapterList){
            tvSeriesAdapter.setNewData(new ArrayList<>());
        }

        List<RecyclerView> recyclerViewList = new ArrayList<>();
        recyclerViewList.add(binding.rvToday);
        recyclerViewList.add(binding.rvAir);
        recyclerViewList.add(binding.rvPopular);
        recyclerViewList.add(binding.rvTopRated);

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
        binding.rvToday.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvAir.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvPopular.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvTopRated.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        todayTvAdapter = new TvSeriesAdapter();
        binding.rvToday.setAdapter(todayTvAdapter);

        airingTvAdapter = new TvSeriesAdapter();
        binding.rvAir.setAdapter(airingTvAdapter);

        popularTvAdapter = new TvSeriesAdapter();
        binding.rvPopular.setAdapter(popularTvAdapter);

        topRatedTvAdapter = new TvSeriesAdapter();
        binding.rvTopRated.setAdapter(topRatedTvAdapter);
    }

    @Override
    public void showLoading() {
        binding.swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void showError(String error) {
        new Handler().postDelayed(() -> {
            Toast.makeText(getContext(),"Error fetching data: "+error,Toast.LENGTH_SHORT).show();
            binding.swipeRefreshLayout.setRefreshing(false);
        }, 500);
    }

    @Override
    public void hideLoading() {
        new Handler().postDelayed(() -> {
            binding.swipeRefreshLayout.setRefreshing(false);
        }, 500);
    }

    @Override
    public void getTodayTvResponse(TvSeriesBean tvSeriesBean) {
        if(tvSeriesBean!=null && tvSeriesBean.getResults() != null){
            if(!tvSeriesBean.getResults().isEmpty()) {
                binding.rvToday.setVisibility(View.VISIBLE);
                binding.rlToday.setVisibility(View.VISIBLE);
                todayTvAdapter.setNewData(tvSeriesBean.getResults());
            }else{
                binding.rvToday.setVisibility(View.GONE);
                binding.rlToday.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void getOnAiringTvResponse(TvSeriesBean tvSeriesBean) {
        if(tvSeriesBean!=null && tvSeriesBean.getResults() != null){
            if(!tvSeriesBean.getResults().isEmpty()) {
                binding.rvAir.setVisibility(View.VISIBLE);
                binding.rlAir.setVisibility(View.VISIBLE);
                airingTvAdapter.setNewData(tvSeriesBean.getResults());
            }
        }else{
            binding.rvAir.setVisibility(View.GONE);
            binding.rlAir.setVisibility(View.GONE);
        }
    }

    @Override
    public void getPopularTvResponse(TvSeriesBean tvSeriesBean) {
        if(tvSeriesBean!=null && tvSeriesBean.getResults() != null){
            if(!tvSeriesBean.getResults().isEmpty()) {
                binding.rvPopular.setVisibility(View.VISIBLE);
                binding.rlPopular.setVisibility(View.VISIBLE);
                popularTvAdapter.setNewData(tvSeriesBean.getResults());
            }
        }else{
            binding.rvPopular.setVisibility(View.GONE);
            binding.rlPopular.setVisibility(View.GONE);
        }
    }

    @Override
    public void getTopRatedTvResponse(TvSeriesBean tvSeriesBean) {
        if(tvSeriesBean!=null && tvSeriesBean.getResults() != null){
            if(!tvSeriesBean.getResults().isEmpty()) {
                binding.rvTopRated.setVisibility(View.VISIBLE);
                binding.rlTopRated.setVisibility(View.VISIBLE);
                topRatedTvAdapter.setNewData(tvSeriesBean.getResults());
            }
        }else{
            binding.rvTopRated.setVisibility(View.GONE);
            binding.rlTopRated.setVisibility(View.GONE);
        }
    }


    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.tv_today:
                intent = new Intent(getContext(), ViewAllActivity.class);
                intent.putExtra("position",1);
                startActivity(intent);
                break;
            case R.id.tv_air:
                intent = new Intent(getContext(), ViewAllActivity.class);
                intent.putExtra("position",2);
                startActivity(intent);
                break;
            case R.id.tv_popular:
                intent = new Intent(getContext(), ViewAllActivity.class);
                intent.putExtra("position",3);
                startActivity(intent);
                break;
            case R.id.tv_topRated:
                intent = new Intent(getContext(), ViewAllActivity.class);
                intent.putExtra("position",4);
                startActivity(intent);
                break;
        }

    }
}