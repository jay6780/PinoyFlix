package com.m.freemovie.Activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.m.freemovie.R;
import com.m.freemovie.adapter.ViewAllAdapter;
import com.m.freemovie.mvp.ClassBean.MovieBean;
import com.m.freemovie.mvp.Contract.MovieAllContract;
import com.m.freemovie.mvp.Presenter.ViewAllPresenter;

import java.util.ArrayList;
import java.util.List;

public class ViewAllActivity extends AppCompatActivity implements MovieAllContract.View {
    private TextView title_name;
    private RecyclerView rv_viewAll;
    private int page = 1;
    private boolean isLoading = false;
    private List<MovieBean.ResultsBean> movieLists = new ArrayList<>();
    private boolean isNomore = false;
    private ViewAllAdapter viewAllAdapter;
    private int position;
    private ViewAllPresenter viewAllPresenter;
    private SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);
        getSupportActionBar().hide();
        title_name = findViewById(R.id.title_name);
        rv_viewAll = findViewById(R.id.rv_viewAll);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        position = getIntent().getIntExtra("position",1);
        findViewById(R.id.btn_back).setOnClickListener(view -> finish());
        viewAllPresenter = new ViewAllPresenter(this);
        rv_viewAll.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        viewAllAdapter = new ViewAllAdapter();
        rv_viewAll.setAdapter(viewAllAdapter);
        rv_viewAll.setHasFixedSize(true);
        rv_viewAll.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                title_name.setText("Popular Movies");
                viewAllPresenter.getViewAll(getString(R.string.key),page,1);
                break;
            case 2:
                title_name.setText("Top Rated Movies");
                viewAllPresenter.getViewAll(getString(R.string.key),page,2);
                break;
            case 3:
                title_name.setText("Now Playing");
                viewAllPresenter.getViewAll(getString(R.string.key),page,3);
                break;
            case 4:
                title_name.setText("Upcoming Movies");
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
        viewAllAdapter.setNewData(movieLists);
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
            }else{
                Toast.makeText(this,"No more movies",Toast.LENGTH_SHORT).show();
                isLoading = false;
                isNomore = true;
            }
        }
    }
}