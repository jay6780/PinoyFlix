package com.m.freemovie.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.m.freemovie.adapter.AnimePaheAdapter;
import com.m.freemovie.databinding.FragmentAnimePaheBinding;
import com.m.freemovie.mvp.ClassBean.PaheLatestBean;
import com.m.freemovie.mvp.Contract.AnimePaheContract;
import com.m.freemovie.mvp.Presenter.AnimePahePresenter;

import java.util.ArrayList;
import java.util.List;

public class AnimePaheFragment extends Fragment implements AnimePaheContract.View {
    private AnimePahePresenter presenter;
    private FragmentAnimePaheBinding binding;
    int page = 1;
    private boolean isLoading = false;
    private boolean isNomore = false;
    private AnimePaheAdapter animePaheAdapter;
    private List<PaheLatestBean.ResultsBean.DataBean> dataBeanList = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentAnimePaheBinding.inflate(inflater);
        presenter = new AnimePahePresenter(this);
        binding.rvAnimepahe.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        animePaheAdapter = new AnimePaheAdapter();
        binding.rvAnimepahe.setAdapter(animePaheAdapter);
        presenter.getLatest(page);

        binding.rvAnimepahe.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
                if (!isLoading && layoutManager != null) {
                    int[] lastVisiblePositions = layoutManager.findLastVisibleItemPositions(null);
                    int lastVisiblePosition = getMaxPosition(lastVisiblePositions);
                    if (lastVisiblePosition >= dataBeanList.size() - 1) {
                        if (isNomore) {
                            return;
                        }
                        isLoading = true;
                        page++;
                        loadMore();
                    }
                }
            }

            private int getMaxPosition(int[] positions) {
                int max = positions[0];
                for (int position : positions) {
                    if (position > max) {
                        max = position;
                    }
                }
                return max;
            }
        });


        binding.swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();

            }
        });
        return binding.getRoot();
    }

    private void refresh() {
        page = 1;
        dataBeanList.clear();
        animePaheAdapter.setNewData(null);
        presenter.getLatest(page);
        isNomore = false;
    }

    private void loadMore() {
        presenter.getLatest(page);
    }

    @Override
    public void showLoading() {
        binding.swipe.setRefreshing(true);
    }

    @Override
    public void showError(String error) {
        Log.e("AninePahe","val: "+error);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), "Error fetching data: " + error, Toast.LENGTH_SHORT).show();
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
    public void getLatestData(PaheLatestBean paheLatestBean) {
        if(paheLatestBean !=null && paheLatestBean.getResults()!=null){
            isLoading = false;
            if(!paheLatestBean.getResults().getData().isEmpty()){
                dataBeanList.addAll(paheLatestBean.getResults().getData());
                animePaheAdapter.setNewData(dataBeanList);
            }else{
                Toast.makeText(getContext(), "No more in the list", Toast.LENGTH_SHORT).show();
                isNomore = true;
            }
        }
    }
}