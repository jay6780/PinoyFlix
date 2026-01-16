package com.m.freemovie.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.hubert.guide.NewbieGuide;
import com.app.hubert.guide.core.Controller;
import com.app.hubert.guide.listener.OnGuideChangedListener;
import com.app.hubert.guide.model.GuidePage;
import com.app.hubert.guide.model.HighLight;
import com.m.freemovie.R;
import com.m.freemovie.adapter.TvRevivalAdapter;
import com.m.freemovie.databinding.FragmentTagalogServer2Binding;
import com.m.freemovie.mvp.ClassBean.RevivalSeriesBean;
import com.m.freemovie.mvp.Contract.RevivalContractSeries;
import com.m.freemovie.mvp.Presenter.RevivalSeriesPresenter;

import java.util.ArrayList;
import java.util.List;

public class TagalogServer2Fragment extends Fragment implements RevivalContractSeries.View {
    private FragmentTagalogServer2Binding binding;
    private TvRevivalAdapter tvRevivalAdapter;
    private RevivalSeriesPresenter revivalSeriesPresenter;
    private List<RevivalSeriesBean.ResultsBean> resultsBeanList = new ArrayList<>();
    int page = 1;
    private boolean isLoading = false;
    private boolean isNomore = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTagalogServer2Binding.inflate(inflater);
        binding.rvTagalog.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        tvRevivalAdapter = new TvRevivalAdapter();
        tvRevivalAdapter.isMovie(false);
        binding.rvTagalog.setAdapter(tvRevivalAdapter);
        revivalSeriesPresenter = new RevivalSeriesPresenter(this);
        revivalSeriesPresenter.getListTv(page);

        binding.llReset.setVisibility(View.GONE);
        binding.llReset.setOnClickListener(view -> reset());

        binding.rvTagalog.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
                if (!isLoading && layoutManager != null) {
                    int[] lastVisiblePositions = layoutManager.findLastVisibleItemPositions(null);
                    if (lastVisiblePositions[0] > 5) {
                        binding.llReset.setVisibility(View.VISIBLE);
                        initGuide();
                    } else if (lastVisiblePositions[0] == 0) {
                        binding.llReset.setVisibility(View.GONE);
                    }
                    int lastVisiblePosition = getMaxPosition(lastVisiblePositions);
                    if (lastVisiblePosition >= resultsBeanList.size() - 1) {
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

    private void initGuide() {
        NewbieGuide.with(getActivity())
                .setLabel("tagalog2_reset")
                .setOnGuideChangedListener(new OnGuideChangedListener() {
                    @Override
                    public void onShowed(Controller controller) {

                    }

                    @Override
                    public void onRemoved(Controller controller) {
                    }
                })
                .addGuidePage(GuidePage.newInstance()
                        .addHighLight(binding.llReset, HighLight.Shape.ROUND_RECTANGLE, 1)
                        .setLayoutRes(R.layout.ll_reset_guide)
                )
                .show();
    }

    private void reset(){
        binding.rvTagalog.scrollToPosition(0);
        binding.llReset.setVisibility(View.GONE);
    }
    private void loadMore() {
        revivalSeriesPresenter.getListTv(page);
    }

    private void refresh() {
        page = 1;
        resultsBeanList.clear();
        tvRevivalAdapter.setNewData(null);
        revivalSeriesPresenter.getListTv(page);
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
    public void getTvSeries(RevivalSeriesBean revivalSeriesBean) {
        if(revivalSeriesBean!=null && revivalSeriesBean.getResults() !=null){
            isLoading = false;
            resultsBeanList.addAll(revivalSeriesBean.getResults());
            tvRevivalAdapter.setNewData(resultsBeanList);
        }

    }
}