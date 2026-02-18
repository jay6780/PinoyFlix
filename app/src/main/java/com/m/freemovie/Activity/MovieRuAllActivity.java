package com.m.freemovie.Activity;

import android.annotation.SuppressLint;
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
import com.m.freemovie.adapter.MovieRuAdapter;
import com.m.freemovie.databinding.ActivityMovieRuAllBinding;
import com.m.freemovie.mvp.Contract.PinoyRuMovieAllContract;
import com.m.freemovie.mvp.Model.ClassBean.PinoyMovieRuBean;
import com.m.freemovie.mvp.Model.ClassBean.PinoyRuBean;
import com.m.freemovie.mvp.Presenter.PinoyRuAllPresenter;

import java.util.ArrayList;
import java.util.List;

public class MovieRuAllActivity extends AppCompatActivity implements PinoyRuMovieAllContract.View {
    private ActivityMovieRuAllBinding binding;
    private PinoyRuAllPresenter presenter;
    private int page = 1;
    private int perPage = 10;
    private List<PinoyRuBean> movieRuBeanList = new ArrayList<>();
    private boolean isNomore = false;
    private boolean isLoading = false;
    private MovieRuAdapter movieRuAdapter;
    private String type;
    private int typeData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = ActivityMovieRuAllBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        presenter = new PinoyRuAllPresenter(this);
        type = getIntent().getStringExtra("type");
        typeData = getIntent().getIntExtra("typeData", 1);
        binding.btnBack.setOnClickListener(view -> finish());
        initApi();
        initRecycler();
        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isNetworkAvailable()) {
                    Toast.makeText(getApplicationContext(), "Please check your internet and try again", Toast.LENGTH_SHORT).show();
                    binding.swipeRefreshLayout.setRefreshing(false);
                    return;
                }
                isNomore = false;
                page = 1;
                perPage = 10;
                if (binding.llReset.getVisibility() == View.VISIBLE) {
                    binding.llReset.setVisibility(View.GONE);
                }
                movieRuAdapter.clearCache();
                movieRuBeanList.clear();
                movieRuAdapter.setNewData(movieRuBeanList);
                initApi();
            }
        });
    }

    private void initApi() {
        switch (type) {
            case "Action":
                binding.titleName.setText("Action");
                presenter.getActionPageQuery("26", perPage, page);
                break;
            case "Romance":
                binding.titleName.setText("Romance");
                presenter.getRomanceQuery("52", perPage, page);
                break;
            case "Comedy":
                binding.titleName.setText("Comedy");
                presenter.getComedyQuery("15", perPage, page);
                break;
            case "VivaMax":
                binding.titleName.setText("All");
                presenter.getPage(page);
                break;
        }

    }

    private void initRecycler() {
        binding.rvRu.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        movieRuAdapter = new MovieRuAdapter();
        movieRuAdapter.type(typeData);
        binding.rvRu.setAdapter(movieRuAdapter);
        binding.llReset.setVisibility(View.GONE);
        binding.llReset.setOnClickListener(view -> reset());
        binding.rvRu.setHasFixedSize(true);
        binding.rvRu.addOnScrollListener(new RecyclerView.OnScrollListener() {
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


                    if (lastVisiblePosition >= movieRuBeanList.size() - 1) {
                        if (isNomore) {
                            return;
                        }
                        isLoading = true;
                        page++;
                        initApi();
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

    }

    private void initGuide() {
        NewbieGuide.with(this)
                .setLabel("other_reset")
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

    private void reset() {
        binding.rvRu.scrollToPosition(0);
        binding.llReset.setVisibility(View.GONE);
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("MissingPermission")
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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
                Toast.makeText(getApplicationContext(), "Error fetching data: " + error, Toast.LENGTH_SHORT).show();
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
    public void getMovieList(List<PinoyMovieRuBean> bean) {
        if (bean != null) {
            isLoading = false;
            for (PinoyMovieRuBean data : bean) {
                movieRuBeanList.add(new PinoyRuBean(data.getLink(), data.getTitle().getRendered(), data.getId()));
            }
            if (!movieRuBeanList.isEmpty()) {
                movieRuAdapter.setNewData(movieRuBeanList);
            } else {
                Toast.makeText(getApplicationContext(), "No data", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void getActionList(List<PinoyMovieRuBean> bean) {
        if (bean != null) {
            isLoading = false;
            for (PinoyMovieRuBean data : bean) {
                movieRuBeanList.add(new PinoyRuBean(data.getLink(), data.getTitle().getRendered(), data.getId()));
            }
            if (!movieRuBeanList.isEmpty()) {
                movieRuAdapter.setNewData(movieRuBeanList);
            } else {
                Toast.makeText(getApplicationContext(), "No data", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void getRomanceList(List<PinoyMovieRuBean> bean) {
        if (bean != null) {
            isLoading = false;
            for (PinoyMovieRuBean data : bean) {
                movieRuBeanList.add(new PinoyRuBean(data.getLink(), data.getTitle().getRendered(), data.getId()));
            }
            if (!movieRuBeanList.isEmpty()) {
                movieRuAdapter.setNewData(movieRuBeanList);
            } else {
                Toast.makeText(getApplicationContext(), "No data", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void getComedyList(List<PinoyMovieRuBean> bean) {
        if (bean != null) {
            isLoading = false;
            for (PinoyMovieRuBean data : bean) {
                movieRuBeanList.add(new PinoyRuBean(data.getLink(), data.getTitle().getRendered(), data.getId()));
            }
            if (!movieRuBeanList.isEmpty()) {
                movieRuAdapter.setNewData(movieRuBeanList);
            } else {
                Toast.makeText(getApplicationContext(), "No data", Toast.LENGTH_SHORT).show();
            }
        }
    }
}