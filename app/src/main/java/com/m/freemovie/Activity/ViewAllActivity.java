package com.m.freemovie.Activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.hubert.guide.NewbieGuide;
import com.app.hubert.guide.core.Controller;
import com.app.hubert.guide.listener.OnGuideChangedListener;
import com.app.hubert.guide.model.GuidePage;
import com.app.hubert.guide.model.HighLight;
import com.m.freemovie.R;
import com.m.freemovie.adapter.SeriesAllAdapter;
import com.m.freemovie.adapter.ViewAllAdapter;
import com.m.freemovie.databinding.ActivityViewAllBinding;
import com.m.freemovie.mvp.ClassBean.MovieBean;
import com.m.freemovie.mvp.ClassBean.TvSeriesBean;
import com.m.freemovie.mvp.Contract.MovieAllContract;
import com.m.freemovie.mvp.Presenter.ViewAllPresenter;

import java.util.ArrayList;
import java.util.List;

public class ViewAllActivity extends AppCompatActivity implements MovieAllContract.View {
    private int page = 1;
    private boolean isLoading = false;
    private List<MovieBean.ResultsBean> movieLists = new ArrayList<>();
    private List<TvSeriesBean.ResultsBean> tvSeriesList = new ArrayList<>();
    private boolean isNomore = false;
    private ViewAllAdapter viewAllAdapter;
    private SeriesAllAdapter seriesAllAdapter;
    private int position;
    private ViewAllPresenter viewAllPresenter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isReload = false;
    private ActivityViewAllBinding binding;
    private boolean isTvSeries;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewAllBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        position = getIntent().getIntExtra("position",1);
        isTvSeries = getIntent().getBooleanExtra("isTvSeries",false);
        findViewById(R.id.btn_back).setOnClickListener(view -> finish());
        viewAllPresenter = new ViewAllPresenter(this);
        initStartApi();
        if(isTvSeries){
            binding.rvViewAll.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            seriesAllAdapter = new SeriesAllAdapter();
            binding.rvViewAll.setAdapter(seriesAllAdapter);
            seriesAllAdapter.isTvSeries(2);
        }else{
            binding.rvViewAll.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            viewAllAdapter = new ViewAllAdapter();
            binding.rvViewAll.setAdapter(viewAllAdapter);
            viewAllAdapter.isTvSeries(1);
        }
        binding.llReset.setVisibility(View.GONE);
        binding.llReset.setOnClickListener(view -> reset());
        binding.rvViewAll.setHasFixedSize(true);
        binding.rvViewAll.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

                    if(isTvSeries){
                        if (lastVisiblePosition >= tvSeriesList.size() - 1) {
                            if (isNomore) {
                                return;
                            }
                            isLoading = true;
                            isReload = false;
                            page++;
                            loadSeries();
                        }
                    }else{
                        if (lastVisiblePosition >= movieLists.size() - 1) {
                            if (isNomore) {
                                return;
                            }
                            isLoading = true;
                            isReload = false;
                            page++;
                            loadMovie();
                        }
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

        findViewById(R.id.rotate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              if(isTvSeries){
                  refreshSeries();
              }else{
                  refreshMovies();
              }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(isTvSeries){
                    refreshSeries();
                }else{
                    refreshMovies();
                }
            }
        });

    }

    private void initGuide() {
        NewbieGuide.with(this)
                .setLabel("view_all_reset")
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
        binding.rvViewAll.scrollToPosition(0);
        binding.llReset.setVisibility(View.GONE);
    }

    private void initStartApi() {
        if(isTvSeries) {
            switch (position) {
                case 1:
                    binding.titleName.setText("Airing today");
                    viewAllPresenter.getSeriesAll(getString(R.string.key), page, 1);
                    break;
                case 2:
                    binding.titleName.setText("On the Air");
                    viewAllPresenter.getSeriesAll(getString(R.string.key), page, 2);
                    break;
                case 3:
                    binding.titleName.setText("Popular");
                    viewAllPresenter.getSeriesAll(getString(R.string.key), page, 3);
                    break;
                case 4:
                    binding.titleName.setText("Top Rated");
                    viewAllPresenter.getSeriesAll(getString(R.string.key), page, 4);
                    break;
            }
        }else{
            switch (position){
                case 1:
                    binding.titleName.setText("Popular Movies");
                    viewAllPresenter.getViewAll(getString(R.string.key),page,1);
                    break;
                case 2:
                    binding.titleName.setText("Top Rated Movies");
                    viewAllPresenter.getViewAll(getString(R.string.key),page,2);
                    break;
                case 3:
                    binding.titleName.setText("Now Playing");
                    viewAllPresenter.getViewAll(getString(R.string.key),page,3);
                    break;
                case 4:
                    binding.titleName.setText("Upcoming Movies");
                    viewAllPresenter.getViewAll(getString(R.string.key),page,4);
                    break;
            }
        }

    }

    private void loadSeries() {
        switch (position){
            case 1:
                viewAllPresenter.getSeriesAll(getString(R.string.key),page,1);
                break;
            case 2:
                viewAllPresenter.getSeriesAll(getString(R.string.key),page,2);
                break;
            case 3:
                viewAllPresenter.getSeriesAll(getString(R.string.key),page,3);
                break;
            case 4:
                viewAllPresenter.getSeriesAll(getString(R.string.key),page,4);
                break;
        }
    }

    private void loadMovie() {
            switch (position){
                case 1:
                    viewAllPresenter.getViewAll(getString(R.string.key),page,1);
                    break;
                case 2:
                    viewAllPresenter.getViewAll(getString(R.string.key),page,2);
                    break;
                case 3:
                    viewAllPresenter.getViewAll(getString(R.string.key),page,3);
                    break;
                case 4:
                    viewAllPresenter.getViewAll(getString(R.string.key),page,4);
                    break;
            }
    }
    private void refreshSeries(){
        if(!isNetworkAvailable()){
            Toast.makeText(getApplicationContext(),"Please check your internet and try again",Toast.LENGTH_SHORT).show();
            return;
        }
        page = 1;
        tvSeriesList.clear();
        isReload = true;
        isNomore = false;
        seriesAllAdapter.setNewData(new ArrayList<>());
            switch (position){
                case 1:
                    viewAllPresenter.getSeriesAll(getString(R.string.key),page,1);
                    break;
                case 2:
                    viewAllPresenter.getSeriesAll(getString(R.string.key),page,2);
                    break;
                case 3:
                    viewAllPresenter.getSeriesAll(getString(R.string.key),page,3);
                    break;
                case 4:
                    viewAllPresenter.getSeriesAll(getString(R.string.key),page,4);
                    break;
            }

    }


    private void refreshMovies(){
        if(!isNetworkAvailable()){
            Toast.makeText(getApplicationContext(),"Please check your internet and try again",Toast.LENGTH_SHORT).show();
            return;
        }
        page = 1;
        movieLists.clear();
        isReload = true;
        isNomore = false;
        viewAllAdapter.setNewData(new ArrayList<>());
            switch (position){
                case 1:
                    viewAllPresenter.getViewAll(getString(R.string.key),page,1);
                    break;
                case 2:
                    viewAllPresenter.getViewAll(getString(R.string.key),page,2);
                    break;
                case 3:
                    viewAllPresenter.getViewAll(getString(R.string.key),page,3);
                    break;
                case 4:
                    viewAllPresenter.getViewAll(getString(R.string.key),page,4);
                    break;
            }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void showLoading() {
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void showError(String error) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),"Error fetching data: "+error,Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 500);
        isReload = false;
}

    @Override
    public void hideLoading() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 500);
    }


    @Override
    public void getViewAllResponse(MovieBean movieBean) {
        if(movieBean!=null && movieBean.getResults() != null){
            isLoading = false;
            if(!movieBean.getResults().isEmpty()){
                movieLists.addAll(movieBean.getResults());
                viewAllAdapter.setNewData(movieLists);
                if(isReload){
                    binding.rvViewAll.scrollToPosition(0);
                }
            }else{
                Toast.makeText(this,"No more movies",Toast.LENGTH_SHORT).show();
                isNomore = true;
                isLoading = false;
            }
        }
    }

    @Override
    public void getTvSeriesResponse(TvSeriesBean tvSeriesBean) {
        if(tvSeriesBean!=null && tvSeriesBean.getResults() != null){
            isLoading = false;
            if(!tvSeriesBean.getResults().isEmpty()){
                tvSeriesList.addAll(tvSeriesBean.getResults());
                seriesAllAdapter.setNewData(tvSeriesList);
                if(isReload){
                    binding.rvViewAll.scrollToPosition(0);
                }
            }else{
                Toast.makeText(this,"No more tv Series",Toast.LENGTH_SHORT).show();
                isNomore = true;
                isLoading = false;
            }
        }
    }
}