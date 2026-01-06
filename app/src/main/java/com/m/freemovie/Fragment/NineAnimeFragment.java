package com.m.freemovie.Fragment;

import android.content.Intent;
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

import com.m.freemovie.Activity.ViewAllNineAnimeActivity;
import com.m.freemovie.R;
import com.m.freemovie.adapter.NineAnimeAdapter;
import com.m.freemovie.databinding.FragmentNineAnimeBinding;
import com.m.freemovie.mvp.ClassBean.NineAnimeBean;
import com.m.freemovie.mvp.Contract.NineAnimeContract;
import com.m.freemovie.mvp.Presenter.NineAminePresenter;

import java.util.ArrayList;

public class NineAnimeFragment extends Fragment  implements NineAnimeContract.View,View.OnClickListener {
    private FragmentNineAnimeBinding binding;
    private NineAminePresenter presenter;
    private NineAnimeAdapter OngoingAdapter,LatestAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNineAnimeBinding.inflate(inflater);
        presenter = new NineAminePresenter(this);

        OngoingAdapter = new NineAnimeAdapter();
        LatestAdapter = new NineAnimeAdapter();
        binding.rvOngoing.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvLatest.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvOngoing.setAdapter(OngoingAdapter);
        binding.rvLatest.setAdapter(LatestAdapter);
        binding.tvOngoing.setOnClickListener(this);
        binding.tvLatest.setOnClickListener(this);
        initFetch();


        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.rlOngoing.setVisibility(View.GONE);
                binding.rvOngoing.setVisibility(View.GONE);
                binding.rlLatest.setVisibility(View.GONE);
                binding.rvLatest.setVisibility(View.GONE);
                binding.rvLatest.scrollToPosition(0);
                binding.rvOngoing.scrollToPosition(0);
                OngoingAdapter.setNewData(new ArrayList<>());
                LatestAdapter.setNewData(new ArrayList<>());
                initFetch();

            }
        });
        return binding.getRoot();
    }

    private void initFetch() {
        presenter.getPageLatest(1);
        presenter.getPageOngoing(1);
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
    public void getOngoing(NineAnimeBean nineAnimeBean) {
        if(nineAnimeBean!=null && nineAnimeBean.getResults() !=null){
            binding.rlOngoing.setVisibility(View.VISIBLE);
            binding.rvOngoing.setVisibility(View.VISIBLE);
            OngoingAdapter.setNewData(nineAnimeBean.getResults());
        }

    }

    @Override
    public void getLatest(NineAnimeBean nineAnimeBean) {
        if(nineAnimeBean!=null && nineAnimeBean.getResults() !=null){
            binding.rlLatest.setVisibility(View.VISIBLE);
            binding.rvLatest.setVisibility(View.VISIBLE);
            LatestAdapter.setNewData(nineAnimeBean.getResults());
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_ongoing:
                startActivity(new Intent(getContext(), ViewAllNineAnimeActivity.class).putExtra("position",1));
                break;
            case R.id.tv_latest:
                startActivity(new Intent(getContext(), ViewAllNineAnimeActivity.class).putExtra("position",2));
                break;
        }
    }
}