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
import com.m.freemovie.Utils.GlobalWindowUtils;
import com.m.freemovie.adapter.ViewAllAnimeAdapter;
import com.m.freemovie.databinding.ActivityViewAllAnimeAcitvityBinding;
import com.m.freemovie.mvp.Model.ClassBean.AniKoToPageBean;
import com.m.freemovie.mvp.Model.ClassBean.AnimeItemBean;
import com.m.freemovie.mvp.Model.ClassBean.AnimoPageBean;
import com.m.freemovie.mvp.Model.ClassBean.PaheLatestBean;
import com.m.freemovie.mvp.Model.ClassBean.RevivalSeriesBean;
import com.m.freemovie.mvp.Model.ClassBean.TagalogBean;
import com.m.freemovie.mvp.Contract.AnimeContract;
import com.m.freemovie.mvp.Model.ClassBean.ZoRoPageBean;
import com.m.freemovie.mvp.Presenter.AnimePresenter;

import java.util.ArrayList;
import java.util.List;

public class ViewAllAnimeAcitvity extends AppCompatActivity implements AnimeContract.View {
    private ActivityViewAllAnimeAcitvityBinding binding;
    private AnimePresenter presenter;
    private String title;
    private int position;
    private int page = 1;
    private List<AnimeItemBean> animeItemBeanList = new ArrayList<>();
    private ViewAllAnimeAdapter viewAllAnimeAdapter;
    private boolean isLoading = false;
    private boolean isNomore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        new GlobalWindowUtils(this,false);
        binding = ActivityViewAllAnimeAcitvityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        title = getIntent().getStringExtra("title");
        position = getIntent().getIntExtra("position", 1);
        presenter = new AnimePresenter(this);
        binding.titleName.setText(title);
        binding.btnBack.setOnClickListener(view -> onBackPressed());
        viewAllAnimeAdapter = new ViewAllAnimeAdapter(position);
        binding.rvViewAll.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        binding.rvViewAll.setAdapter(viewAllAnimeAdapter);

        callApi();


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

                    if (lastVisiblePosition >= animeItemBeanList.size() - 1) {
                        if (isNomore) {
                            return;
                        }
                        isLoading = true;
                        page++;
                        callApi();

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
                if (!isNetworkAvailable()) {
                    binding.swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getApplicationContext(), "Please check internet and try again", Toast.LENGTH_SHORT).show();
                    return;
                }
                page = 1;
                animeItemBeanList.clear();
                viewAllAnimeAdapter.setNewData(animeItemBeanList);
                callApi();
            }
        });

    }

    private void initGuide() {
        NewbieGuide.with(this)
                .setLabel("view_all_reset_anime")
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
        binding.rvViewAll.scrollToPosition(0);
        binding.llReset.setVisibility(View.GONE);
    }


    private void callApi() {
        if (!isNetworkAvailable()) {
            Toast.makeText(getApplicationContext(), "Please check internet and try again", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (position) {
            case 1:
                presenter.getAniKoToPage(page);
                break;
            case 2:
                presenter.getHotPage(page);
                break;
            case 3:
                presenter.getPopular(page);
                break;
            case 4:
                presenter.getMovie(page);
                break;
        }
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
    public void getNewest(PaheLatestBean paheLatestBean) {
        if (paheLatestBean != null && paheLatestBean.getResults() != null) {
            isLoading = false;
            if (paheLatestBean.getResults().getData() != null) {
                if (!paheLatestBean.getResults().getData().isEmpty()) {
                    for (PaheLatestBean.ResultsBean.DataBean dataBean : paheLatestBean.getResults().getData()) {
                        animeItemBeanList.add(new AnimeItemBean(dataBean.getAnime_session(), dataBean.getAnime_title(), dataBean.getSnapshot()));
                    }
                    viewAllAnimeAdapter.setNewData(animeItemBeanList);
                } else {
                    isNomore = true;
                }
            }
        } else {
            isNomore = true;
        }
    }

    @Override
    public void getHot(AnimoPageBean animoPageBean) {
        if (animoPageBean != null && animoPageBean.getResults() != null) {
            isLoading = false;
            if (animoPageBean.getResults() != null) {
                if (!animoPageBean.getResults().isEmpty()) {
                    for (AnimoPageBean.ResultsBean dataBean : animoPageBean.getResults()) {
                        animeItemBeanList.add(new AnimeItemBean(dataBean.getLink(), dataBean.getTitle(), dataBean.getImg()));
                    }
                    viewAllAnimeAdapter.setNewData(animeItemBeanList);
                } else {
                    isNomore = true;
                }
            }
        } else {
            isNomore = true;
        }
    }

    @Override
    public void getPopular(RevivalSeriesBean revivalSeriesBean) {
        if (revivalSeriesBean != null && revivalSeriesBean.getResults() != null) {
            isLoading = false;
            if (revivalSeriesBean.getResults() != null) {
                if (!revivalSeriesBean.getResults().isEmpty()) {
                    for (RevivalSeriesBean.ResultsBean dataBean : revivalSeriesBean.getResults()) {
                        animeItemBeanList.add(new AnimeItemBean(dataBean.getLink(), dataBean.getTitle(), dataBean.getPoster()));
                    }
                    viewAllAnimeAdapter.setNewData(animeItemBeanList);
                } else {
                    isNomore = true;
                }
            }
        } else {
            isNomore = true;
        }
    }

    @Override
    public void getMovie(RevivalSeriesBean revivalSeriesBean) {
        if (revivalSeriesBean != null && revivalSeriesBean.getResults() != null) {
            isLoading = false;
            if (revivalSeriesBean.getResults() != null) {
                if (!revivalSeriesBean.getResults().isEmpty()) {
                    for (RevivalSeriesBean.ResultsBean dataBean : revivalSeriesBean.getResults()) {
                        animeItemBeanList.add(new AnimeItemBean(dataBean.getLink(), dataBean.getTitle(), dataBean.getPoster()));
                    }
                    viewAllAnimeAdapter.setNewData(animeItemBeanList);
                } else {
                    isNomore = true;
                }
            }
        } else {
            isNomore = true;
        }
    }

    @Override
    public void getZoRo(List<ZoRoPageBean> zoRoPageBean) {
//        if (zoRoPageBean != null) {
//            isLoading = false;
//            if (!zoRoPageBean.isEmpty()) {
//                for (ZoRoPageBean zoRoPageBean1 : zoRoPageBean) {
//                    animeItemBeanList.add(new AnimeItemBean(zoRoPageBean1.getUrl(), zoRoPageBean1.getTitle(), zoRoPageBean1.getImage()));
//                }
//                viewAllAnimeAdapter.setNewData(animeItemBeanList);
//            } else {
//                isNomore = true;
//            }
//        } else {
//            isNomore = true;
//        }
    }

    @Override
    public void getAniKoTo(AniKoToPageBean aniKoToPageBean) {
        if (aniKoToPageBean != null) {
            isLoading = false;
            if (!aniKoToPageBean.getResults().isEmpty()) {
                for (AniKoToPageBean.ResultsBean resultsBean : aniKoToPageBean.getResults()) {
                    animeItemBeanList.add(new AnimeItemBean(resultsBean.getAnimeId(), resultsBean.getTitle(), resultsBean.getThumbnail()));
                }
                viewAllAnimeAdapter.setNewData(animeItemBeanList);
            } else {
                isNomore = true;
            }
        } else {
            isNomore = true;
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}