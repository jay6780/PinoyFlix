package com.m.freemovie.Activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.m.freemovie.R;
import com.m.freemovie.adapter.ViewAllAdapter;
import com.m.freemovie.databinding.ActivityViewAllBinding;
import com.m.freemovie.mvp.ClassBean.MovieBean;
import com.m.freemovie.mvp.Contract.MovieAllContract;
import com.m.freemovie.mvp.Presenter.ViewAllPresenter;

import java.util.ArrayList;
import java.util.List;

public class ViewAllActivity extends AppCompatActivity implements MovieAllContract.View {
    private int page = 1;
    private boolean isLoading = false;
    private List<MovieBean.ResultsBean> movieLists = new ArrayList<>();
    private boolean isNomore = false;
    private ViewAllAdapter viewAllAdapter;
    private int position;
    private ViewAllPresenter viewAllPresenter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isReload = false;
    private ActivityViewAllBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewAllBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        position = getIntent().getIntExtra("position",1);
        findViewById(R.id.btn_back).setOnClickListener(view -> finish());
        viewAllPresenter = new ViewAllPresenter(this);
        binding.rvViewAll.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        viewAllAdapter = new ViewAllAdapter();
        binding.rvViewAll.setAdapter(viewAllAdapter);
        binding.rvViewAll.setHasFixedSize(true);
        binding.rvViewAll.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
                if (!isLoading && layoutManager != null) {
                    int[] lastVisiblePositions = layoutManager.findLastVisibleItemPositions(null);
                    int lastVisiblePosition = getMaxPosition(lastVisiblePositions);
                    if (lastVisiblePosition >= movieLists.size() - 1) {

                        if (isNomore) {
                            return;
                        }
                        isLoading = true;
                        isReload = false;
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

        findViewById(R.id.rotate).setOnClickListener(view -> refresh());

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });


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

    private void loadMore() {
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

    private void refresh(){
        if(!isNetworkAvailable()){
            Toast.makeText(getApplicationContext(),"Please check your internet and try again",Toast.LENGTH_SHORT).show();
            return;
        }
        page = 1;
        movieLists.clear();
        isReload = true;
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
        new Handler().postDelayed(() -> {
            Toast.makeText(getApplicationContext(),"Error fetching data: "+error,Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
        }, 500);
        isReload = false;
    }

    @Override
    public void hideLoading() {
        new Handler().postDelayed(() -> {
            swipeRefreshLayout.setRefreshing(false);
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
                isLoading = false;
                isNomore = true;
            }
        }
    }
}