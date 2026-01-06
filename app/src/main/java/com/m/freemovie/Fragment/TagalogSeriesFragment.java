package com.m.freemovie.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.m.freemovie.adapter.TagalogSeriesAdapter;
import com.m.freemovie.databinding.FragmentTagalogSeriesBinding;
import com.m.freemovie.mvp.ClassBean.TagalogBean;
import com.m.freemovie.mvp.Contract.TagalogSeriesContract;
import com.m.freemovie.mvp.Presenter.TagalogSeriesPresenter;

public class TagalogSeriesFragment extends Fragment implements TagalogSeriesContract.View {
    private FragmentTagalogSeriesBinding binding;
    private TagalogSeriesAdapter tagalogSeriesAdapter;
    private TagalogSeriesPresenter tagalogSeriesPresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTagalogSeriesBinding.inflate(inflater);
        binding.rvTagalog.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        tagalogSeriesAdapter = new TagalogSeriesAdapter();
        binding.rvTagalog.setAdapter(tagalogSeriesAdapter);
        tagalogSeriesPresenter = new TagalogSeriesPresenter(this);
        tagalogSeriesPresenter.startSeries();

        binding.swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();

            }
        });


        return binding.getRoot();
    }

    private void refresh() {
        tagalogSeriesAdapter.setNewData(null);
        tagalogSeriesPresenter.startSeries();
    }

    @Override
    public void showLoading() {
        binding.swipe.setRefreshing(true);
    }

    @Override
    public void showError(String error) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(),"Error fetching data: "+error,Toast.LENGTH_SHORT).show();
                binding.swipe.setRefreshing(false);
            }
        }, 500);
    }

    @Override
    public void hideLoading() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.swipe.setRefreshing(false);
            }
        }, 500);
    }

    @Override
    public void getTagalogSeries(TagalogBean tagalogBean) {
        if(tagalogBean!=null && tagalogBean.getResults()!=null){
            tagalogSeriesAdapter.setNewData(tagalogBean.getResults());
        }

    }
}