package com.m.freemovie.Fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.m.freemovie.adapter.AnimePaheSearchAdapter;
import com.m.freemovie.adapter.NineAnimeSearchAdapter;
import com.m.freemovie.adapter.TagalogSearchAdapter;
import com.m.freemovie.adapter.TvRevivialSearchAdapter;
import com.m.freemovie.adapter.ViewAllAdapter;
import com.m.freemovie.mvp.ClassBean.AnimePaheSearchBean;
import com.m.freemovie.mvp.ClassBean.MovieBean;
import com.m.freemovie.mvp.ClassBean.MovieEvent;
import com.m.freemovie.mvp.ClassBean.NineAnimeSearchBean;
import com.m.freemovie.mvp.ClassBean.RevivalSearchBean;
import com.m.freemovie.mvp.ClassBean.TagalogSearchBean;
import com.m.freemovie.mvp.Contract.RevivalSearchContract;
import com.m.freemovie.mvp.Contract.SearchContract;
import com.m.freemovie.mvp.Presenter.RevivalSearchPresenter;
import com.m.freemovie.mvp.Presenter.SearchPresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SearchFragment extends Fragment implements SearchContract.View,View.OnClickListener, RevivalSearchContract.View {
    private EditText et_search;
    private int page = 1;
    private RecyclerView rv_search;
    private SearchPresenter searchPresenter;
    private ViewAllAdapter movieAdapter;
    private TagalogSearchAdapter tagalogSearchAdapter;
    private TvRevivialSearchAdapter tvRevivialSearchAdapter;
    private NineAnimeSearchAdapter nineAnimeSearchAdapter;
    private AnimePaheSearchAdapter animePaheSearchAdapter;
    private boolean isLoading = false;
    private String lastQuery;
    private boolean isNomore = false;
    private ImageView btn_send;
    private List<MovieBean.ResultsBean> movieLists = new ArrayList<>();
    private List<TagalogSearchBean.ResultsBean> tagaloglist = new ArrayList<>();
    private List<RevivalSearchBean.ResultsBean> revivalList = new ArrayList<>();
    private List<NineAnimeSearchBean.ResultsBean> nineList = new ArrayList<>();
    private List<AnimePaheSearchBean.ResultsBean.DataBean> animePaheList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private int position = 1;
    private RevivalSearchPresenter revivalSearchPresenter;
    private LinearLayout ll_reset;
    private Random random;
    private InputMethodManager mInputManager;
    private String toast;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        et_search = view.findViewById(R.id.et_search);
        rv_search = view.findViewById(R.id.rv_search);
        btn_send = view.findViewById(R.id.btn_send);
        ll_reset = view.findViewById(R.id.ll_reset);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        revivalSearchPresenter = new RevivalSearchPresenter(this);
        searchPresenter = new SearchPresenter(this);
        btn_send.setOnClickListener(this);
        movieAdapter = new ViewAllAdapter();
        tagalogSearchAdapter = new TagalogSearchAdapter();
        tvRevivialSearchAdapter = new TvRevivialSearchAdapter();
        nineAnimeSearchAdapter = new NineAnimeSearchAdapter();
        animePaheSearchAdapter = new AnimePaheSearchAdapter();
        random = new Random();
        if(position == 1){
            int roll = random.nextInt(4) + 1;
            movieAdapter.setApiPosition(roll);
        }

        mInputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        ll_reset.setVisibility(View.GONE);
        ll_reset.setOnClickListener(v -> reset());
        rv_search.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
                if (!isLoading && layoutManager != null) {
                    int[] lastVisiblePositions = layoutManager.findLastVisibleItemPositions(null);
                    int lastVisiblePosition = getMaxPosition(lastVisiblePositions);
                    if (lastVisiblePositions[0] > 5) {
                        ll_reset.setVisibility(View.VISIBLE);
                        initGuide();
                    } else if (lastVisiblePositions[0] == 0) {
                        ll_reset.setVisibility(View.GONE);
                    }
                    if (lastVisiblePosition >= movieLists.size() - 1) {
                        if (position > 3) {
                            return;
                        }
                        if (isNomore) {
                            return;
                        }
                        isLoading = true;
                        page++;
                        loadSearch();
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
        et_search.setHint("Enter movie name");

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        return view;
    }
    private void initGuide() {
        NewbieGuide.with(getActivity())
                .setLabel("Search_reset")
                .setOnGuideChangedListener(new OnGuideChangedListener() {
                    @Override
                    public void onShowed(Controller controller) {

                    }

                    @Override
                    public void onRemoved(Controller controller) {
                    }
                })
                .addGuidePage(GuidePage.newInstance()
                        .addHighLight(ll_reset, HighLight.Shape.ROUND_RECTANGLE, 1)
                        .setLayoutRes(R.layout.ll_reset_guide)
                )
                .show();
    }


    private void reset(){
        rv_search.scrollToPosition(0);
        ll_reset.setVisibility(View.GONE);
    }
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void searchPosition(MovieEvent event) {
        this.position = event.getPosition();
//            Log.d("SearchPosition","value: "+position);
        switch (position){
            case 1:
                et_search.setHint("Enter movie name");
                rv_search.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                movieAdapter = new ViewAllAdapter();
                rv_search.setAdapter(movieAdapter);
                int roll = random.nextInt(4) + 1;
                movieAdapter.setApiPosition(roll);
                movieLists.clear();
                if(ll_reset !=null){
                    ll_reset.setVisibility(View.GONE);
                }
                break;

            case 2:
                et_search.setHint("Enter series name");
                rv_search.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                movieAdapter = new ViewAllAdapter();
                rv_search.setAdapter(movieAdapter);
                movieLists.clear();
                if(ll_reset !=null){
                    ll_reset.setVisibility(View.GONE);
                }
                break;

            case 3:
                et_search.setHint("Enter tagalog series");
                rv_search.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                tagalogSearchAdapter = new TagalogSearchAdapter();
                rv_search.setAdapter(tagalogSearchAdapter);
                tagaloglist.clear();
                if(ll_reset !=null){
                    ll_reset.setVisibility(View.GONE);
                }
                break;
            case 4:
            case 5:
                et_search.setHint("Enter tagalog series");
                rv_search.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                tvRevivialSearchAdapter = new TvRevivialSearchAdapter();
                rv_search.setAdapter(tvRevivialSearchAdapter);
                revivalList.clear();
                if(ll_reset !=null){
                    ll_reset.setVisibility(View.GONE);
                }
                break;
            case 6:
                et_search.setHint("Enter anime series");
                rv_search.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                nineAnimeSearchAdapter = new NineAnimeSearchAdapter();
                rv_search.setAdapter(nineAnimeSearchAdapter);
                nineList.clear();
                if(ll_reset !=null){
                    ll_reset.setVisibility(View.GONE);
                }
                break;
            case 7:
                et_search.setHint("Enter anime series");
                rv_search.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                animePaheSearchAdapter = new AnimePaheSearchAdapter();
                rv_search.setAdapter(animePaheSearchAdapter);
                animePaheList.clear();
                if(ll_reset !=null){
                    ll_reset.setVisibility(View.GONE);
                }
                break;

        }
    }




    private void refresh() {
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
        if (!isNetworkAvailable()) {
            Toast.makeText(getContext(), "Please check network and try again", Toast.LENGTH_SHORT).show();
            return;
        }
        isNomore = false;
        et_search.setText("");
        ll_reset.setVisibility(View.GONE);
        movieAdapter.setNewData(new ArrayList<>());
        tagalogSearchAdapter.setNewData(new ArrayList<>());
        tvRevivialSearchAdapter.setNewData(new ArrayList<>());
        nineAnimeSearchAdapter.setNewData(new ArrayList<>());
        animePaheSearchAdapter.setNewData(new ArrayList<>());
        lastQuery = "";
        page = 1;
        String query = et_search.getText().toString().trim();

        if(query.isEmpty()){
            showToast();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void loadSearch() {
        switch (position){
            case 1:
                searchPresenter.getSearchQuery(getString(R.string.key), lastQuery, page);
                break;
            case 2:
                searchPresenter.getSearchSeries(getString(R.string.key), lastQuery, page);
                break;
        }
    }

    @Override
    public void showLoading() {
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void showError(String error) {
        new Handler().postDelayed(() -> {
            Toast.makeText(getContext(), "Error fetching data: " + error, Toast.LENGTH_SHORT).show();
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
    public void getSearchRevival(RevivalSearchBean revivalSearchBean) {
        if (revivalSearchBean != null && revivalSearchBean.getResults() != null) {
            isLoading = false;
            if (!revivalSearchBean.getResults().isEmpty()) {
                revivalList.addAll(revivalSearchBean.getResults());
                tvRevivialSearchAdapter.setNewData(revivalList);
            } else {
                Toast.makeText(getContext(), "No more Tv series", Toast.LENGTH_SHORT).show();
                isLoading = false;
                isNomore = true;
            }
        }
    }

    @Override
    public void getSearchResponse(MovieBean movieBean) {
        if (movieBean != null && movieBean.getResults() != null) {
            isLoading = false;
            if (!movieBean.getResults().isEmpty()) {
                movieLists.addAll(movieBean.getResults());
                movieAdapter.setNewData(movieLists);
            } else {
                Toast.makeText(getContext(), "No more movies", Toast.LENGTH_SHORT).show();
                isLoading = false;
                isNomore = true;
            }
        }
    }

    @Override
    public void getSearchSeriesResponse(MovieBean movieBean) {
        if (movieBean != null && movieBean.getResults() != null) {
            isLoading = false;
            if (!movieBean.getResults().isEmpty()) {
                movieLists.addAll(movieBean.getResults());
                movieAdapter.setNewData(movieLists);
            } else {
                Toast.makeText(getContext(), "No more Tv series", Toast.LENGTH_SHORT).show();
                isLoading = false;
                isNomore = true;
            }
        }
    }

    @Override
    public void getTagalogSearch(TagalogSearchBean tagalogSearchBean) {
        if (tagalogSearchBean != null && tagalogSearchBean.getResults() != null) {
            isLoading = false;
            if (!tagalogSearchBean.getResults().isEmpty()) {
                tagaloglist.addAll(tagalogSearchBean.getResults());
                tagalogSearchAdapter.setNewData(tagaloglist);
            } else {
                Toast.makeText(getContext(), "No more Tv tagalog series", Toast.LENGTH_SHORT).show();
                isLoading = false;
                isNomore = true;
            }
        }
    }

    @Override
    public void getNineAnime(NineAnimeSearchBean nineAnimeSearchBean) {
        if (nineAnimeSearchBean != null && nineAnimeSearchBean.getResults() != null) {
            isLoading = false;
            if (!nineAnimeSearchBean.getResults().isEmpty()) {
                nineList.addAll(nineAnimeSearchBean.getResults());
                nineAnimeSearchAdapter.setNewData(nineList);
            } else {
                Toast.makeText(getContext(), "No more Anime Series", Toast.LENGTH_SHORT).show();
                isLoading = false;
                isNomore = true;
            }
        }
    }

    @Override
    public void getSearchPahe(AnimePaheSearchBean animePaheSearchBean) {
        if (animePaheSearchBean != null && animePaheSearchBean.getResults().getData() != null) {
            isLoading = false;
            if (!animePaheSearchBean.getResults().getData().isEmpty()) {
                animePaheList.addAll(animePaheSearchBean.getResults().getData());
                animePaheSearchAdapter.setNewData(animePaheList);
            } else {
                Toast.makeText(getContext(), "No more Anime Series", Toast.LENGTH_SHORT).show();
                isLoading = false;
                isNomore = true;
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
                searchData();
                break;
        }
    }

    private void searchData() {
        if (!isNetworkAvailable()) {
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
            Toast.makeText(getContext(), "Please check network and try again", Toast.LENGTH_SHORT).show();
            return;
        }

        String query = et_search.getText().toString().trim();

        if (query.isEmpty()) {
            showToast();
            return;
        }

        mInputManager.hideSoftInputFromWindow(et_search.getWindowToken(), 0);
        lastQuery = query;
        isNomore = false;
        page = 1;
        switch (position){
            case 1:
                movieLists.clear();
                movieAdapter.setNewData(new ArrayList<>());
                searchPresenter.getSearchQuery(getString(R.string.key), query, page);
                movieAdapter.isTvSeries(1);
                break;
            case 2:
                movieLists.clear();
                movieAdapter.setNewData(new ArrayList<>());
                searchPresenter.getSearchSeries(getString(R.string.key), query, page);
                movieAdapter.isTvSeries(2);
                break;

            case 3:
                tagaloglist.clear();
                tagalogSearchAdapter.setNewData(new ArrayList<>());
                searchPresenter.getTagalogQuery(query);
                break;
            case 4:
            case 5:
                revivalList.clear();
                tvRevivialSearchAdapter.setNewData(new ArrayList<>());
                revivalSearchPresenter.getSearchRevival(query);
                break;
            case 6:
                nineList.clear();
                tvRevivialSearchAdapter.setNewData(new ArrayList<>());
                searchPresenter.getNineAnimeQuery(query);
                break;

            case 7:
                animePaheList.clear();
                animePaheSearchAdapter.setNewData(new ArrayList<>());
                searchPresenter.getAnimePaheQuery(query);
                break;
        }
    }

    private void showToast() {
        switch (position){
            case 1:
                toast = "Please enter movie name";
                break;
            case 2:
                toast = "Please enter Tv series";
                break;

            case 3:
            case 4:
            case 5:
                toast = "Please enter tagalog series";
                break;
            case 6:
            case 7:
                toast = "Please enter anime series";
                break;
        }
        Toast.makeText(getContext(),toast, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        if(position == 1){
            if(movieLists.isEmpty()){
                EventBus.getDefault().post(new MovieEvent(1));
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}