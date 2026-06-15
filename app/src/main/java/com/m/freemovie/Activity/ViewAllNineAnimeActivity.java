package com.m.freemovie.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.m.freemovie.Utils.GlobalWindowUtils;
import com.m.freemovie.adapter.NineAllAdapter;
import com.m.freemovie.databinding.ActivityViewAllNineAnimeBinding;
import com.m.freemovie.mvp.Model.ClassBean.NineAnimeBean;
import com.m.freemovie.mvp.Contract.NineAnimeContract;
import com.m.freemovie.mvp.Presenter.NineAminePresenter;

import java.util.ArrayList;
import java.util.List;

public class ViewAllNineAnimeActivity extends AppCompatActivity implements NineAnimeContract.View {
    private ActivityViewAllNineAnimeBinding binding;
    private int position;
    private NineAminePresenter presenter;
    private int page = 1;
    private NineAllAdapter nineAllAdapter;
    private boolean isNomore = false;
    private boolean isLoading = false;
    private List<NineAnimeBean.ResultsBean> beanList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new GlobalWindowUtils(this,false);
        binding = ActivityViewAllNineAnimeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        position = getIntent().getIntExtra("position", 1);
        presenter = new NineAminePresenter(this);
        if (position == 1) {
            presenter.getPageOngoing(page);
        } else if (position == 2) {
            presenter.getPageLatest(page);
        }
        binding.titleName.setText(position == 1 ?"Ongoing":"Latest");
        nineAllAdapter = new NineAllAdapter();
        binding.rvNineViewAll.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        binding.rvNineViewAll.setAdapter(nineAllAdapter);
        binding.btnBack.setOnClickListener(view -> onBackPressed());
        binding.rotate.setOnClickListener(view -> refreshData());


        binding.rvNineViewAll.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
                if (!isLoading && layoutManager != null) {
                    int[] lastVisiblePositions = layoutManager.findLastVisibleItemPositions(null);
                    int lastVisiblePosition = getMaxPosition(lastVisiblePositions);
                        if (lastVisiblePosition >= beanList.size() - 1) {
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


        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });

    }

    private void loadMore() {
        if (position == 1) {
            presenter.getPageOngoing(page);
        } else if (position == 2) {
            presenter.getPageLatest(page);
        }
    }


    private void refreshData(){
        page = 1;
        beanList.clear();
        nineAllAdapter.setNewData(new ArrayList<>());
        if (position == 1) {
            presenter.getPageOngoing(page);
        } else {
            presenter.getPageLatest(page);
        }
    }
    @Override
    public void showLoading() {
        binding.swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void showError(String error) {
        new Handler().postDelayed(() -> {
            Toast.makeText(getApplicationContext(),"Error fetching data: "+error,Toast.LENGTH_SHORT).show();
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
    public void getOngoing(NineAnimeBean nineAnimeBean) {
        if (nineAnimeBean != null && nineAnimeBean.getResults() != null) {
            isLoading = false;
            beanList.addAll(nineAnimeBean.getResults());
            nineAllAdapter.setNewData(beanList);
        }
        if(nineAnimeBean == null || nineAnimeBean.getResults() == null || nineAnimeBean.getResults().isEmpty()){
            isNomore = true;
        }


    }

    @Override
    public void getLatest(NineAnimeBean nineAnimeBean) {
        if (nineAnimeBean != null && nineAnimeBean.getResults() != null) {
            isLoading = false;
            beanList.addAll(nineAnimeBean.getResults());
            nineAllAdapter.setNewData(beanList);
        }
        if(nineAnimeBean == null || nineAnimeBean.getResults() == null || nineAnimeBean.getResults().isEmpty()){
            isNomore = true;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}