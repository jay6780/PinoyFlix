package com.m.freemovie.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.hubert.guide.NewbieGuide;
import com.app.hubert.guide.core.Controller;
import com.app.hubert.guide.listener.OnGuideChangedListener;
import com.app.hubert.guide.model.GuidePage;
import com.app.hubert.guide.model.HighLight;
import com.m.freemovie.R;
import com.m.freemovie.Utils.SharedPreferencesHelper;
import com.m.freemovie.Utils.base.BaseQuickAdapter;
import com.m.freemovie.adapter.AnimePaheSearchAdapter;
import com.m.freemovie.adapter.MovieRuAdapter;
import com.m.freemovie.adapter.NineAnimeSearchAdapter;
import com.m.freemovie.adapter.RecentAdapter;
import com.m.freemovie.adapter.TagalogSearchAdapter;
import com.m.freemovie.adapter.TvRevivialSearchAdapter;
import com.m.freemovie.adapter.ViewAllAdapter;
import com.m.freemovie.mvp.Contract.RevivalSearchContract;
import com.m.freemovie.mvp.Contract.SearchContract;
import com.m.freemovie.mvp.Model.ClassBean.AnimePaheSearchBean;
import com.m.freemovie.mvp.Model.ClassBean.MovieBean;
import com.m.freemovie.mvp.Model.ClassBean.NineAnimeSearchBean;
import com.m.freemovie.mvp.Model.ClassBean.PinoyRuBean;
import com.m.freemovie.mvp.Model.ClassBean.RevivalSearchBean;
import com.m.freemovie.mvp.Model.ClassBean.SearchRuBean;
import com.m.freemovie.mvp.Model.ClassBean.TagalogSearchBean;
import com.m.freemovie.mvp.Presenter.RevivalSearchPresenter;
import com.m.freemovie.mvp.Presenter.SearchPresenter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SearchFragment extends Fragment implements SearchContract.View, View.OnClickListener, RevivalSearchContract.View, AdapterView.OnItemSelectedListener {
    private EditText et_search;
    private int page = 1;
    private RecyclerView rv_search,rv_recent;
    private SearchPresenter searchPresenter;
    private ViewAllAdapter movieAdapter;
    private TagalogSearchAdapter tagalogSearchAdapter;
    private TvRevivialSearchAdapter tvRevivialSearchAdapter;
    private NineAnimeSearchAdapter nineAnimeSearchAdapter;
    private AnimePaheSearchAdapter animePaheSearchAdapter;
    private MovieRuAdapter movieRuAdapter;
    private boolean isLoading = false;
    private String lastQuery;
    private boolean isNomore = false;
    private ImageView btn_send;
    private List<MovieBean.ResultsBean> movieLists = new ArrayList<>();
    private List<TagalogSearchBean.ResultsBean> tagaloglist = new ArrayList<>();
    private List<RevivalSearchBean.ResultsBean> revivalList = new ArrayList<>();
    private List<NineAnimeSearchBean.ResultsBean> nineList = new ArrayList<>();
    private List<AnimePaheSearchBean.ResultsBean.DataBean> animePaheList = new ArrayList<>();
    private List<PinoyRuBean> tagalogMovieList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private int position = 1;
    private RevivalSearchPresenter revivalSearchPresenter;
    private LinearLayout ll_reset;
    private Random random;
    private InputMethodManager mInputManager;
    private String toast;
    private Spinner fragmentSpinner;
    private RecentAdapter recentAdapter;
    private TextView tv_recent,tv_clear;
    private boolean isSelect = false;
    private LinearLayout ll_empty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        et_search = view.findViewById(R.id.et_search);
        rv_search = view.findViewById(R.id.rv_search);
        btn_send = view.findViewById(R.id.btn_send);
        ll_reset = view.findViewById(R.id.ll_reset);
        rv_recent = view.findViewById(R.id.rv_recent);
        tv_recent = view.findViewById(R.id.tv_recent);
        ll_empty = view.findViewById(R.id.ll_empty);
        fragmentSpinner = view.findViewById(R.id.fragmentSpinner);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        tv_clear = view.findViewById(R.id.tv_clear);
        revivalSearchPresenter = new RevivalSearchPresenter(this);
        searchPresenter = new SearchPresenter(this);
        btn_send.setOnClickListener(this);
        tv_clear.setOnClickListener(this);
        movieAdapter = new ViewAllAdapter();
        tagalogSearchAdapter = new TagalogSearchAdapter();
        tvRevivialSearchAdapter = new TvRevivialSearchAdapter();
        nineAnimeSearchAdapter = new NineAnimeSearchAdapter();
        animePaheSearchAdapter = new AnimePaheSearchAdapter();
        movieRuAdapter = new MovieRuAdapter();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.search, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        fragmentSpinner.setAdapter(adapter);
        fragmentSpinner.setOnItemSelectedListener(this);
        fragmentSpinner.setSelection(0);

        random = new Random();
        if(position == 1){
            int roll = random.nextInt(4) + 1;
            movieAdapter.setApiPosition(roll);
        }

        initRecent(et_search.getText().toString());

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

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String recentSearch = s.toString();
                tv_recent.setVisibility(recentSearch.isEmpty()? View.VISIBLE : View.GONE);
                tv_clear.setVisibility(recentSearch.isEmpty()? View.VISIBLE : View.GONE);
                fragmentSpinner.setVisibility(!recentSearch.isEmpty()? View.VISIBLE : View.GONE);
                rv_search.setVisibility(!recentSearch.isEmpty()? View.VISIBLE : View.GONE);
                rv_recent.setVisibility(recentSearch.isEmpty()? View.VISIBLE : View.GONE);
                fragmentSpinner.setEnabled(rv_recent.getVisibility() == View.VISIBLE? false : true);

                if(rv_recent.getVisibility() == View.VISIBLE){
                    ll_reset.setVisibility(View.GONE);
                }
                initRecent(recentSearch);
                isSelect = false;
                if(recentSearch.isEmpty()){
                    animePaheList.clear();
                    movieLists.clear();
                    nineList.clear();
                    tagaloglist.clear();
                    revivalList.clear();
                    tagalogMovieList.clear();

                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        return view;
    }

    private void initRecent(String data){
        if(data.isEmpty()){
            ArrayList<String> loadedList = SharedPreferencesHelper.loadStringList(getContext(), "recent_search");
            if (loadedList == null) {
                loadedList = new ArrayList<>();
            }
            ll_empty.setVisibility(loadedList.isEmpty()? View.VISIBLE : View.GONE);
            rv_recent.setVisibility(!loadedList.isEmpty()? View.VISIBLE : View.GONE);
            recentAdapter = new RecentAdapter();
            rv_recent.setLayoutManager(new LinearLayoutManager(getContext()));
            rv_recent.setAdapter(recentAdapter);
            recentAdapter.setNewData(loadedList);
            recentAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
                @Override
                public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                    if(view.getId() == R.id.ll_select){
                        String data = recentAdapter.getData().get(position);
                        et_search.setText(data);
                        searchData();
                    }
                }
            });
        }
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
        movieRuAdapter.setNewData(new ArrayList<>());
        lastQuery = "";
        page = 1;
        String query = et_search.getText().toString().trim();

        if(query.isEmpty()){
            showToast();
        }
    }
    @SuppressWarnings("deprecation")
    @SuppressLint("MissingPermission")
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

    @SuppressWarnings("deprecation")
    @Override
    public void showError(String error) {
        new Handler().postDelayed(() -> {
            Toast.makeText(getContext(), "Error fetching data: " + error, Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
        }, 500);
    }
    @SuppressWarnings("deprecation")
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
    public void getSearchList(List<SearchRuBean> bean) {
        if(bean !=null) {
            isLoading = false;
            for (SearchRuBean data : bean) {
                tagalogMovieList.add(new PinoyRuBean(data.getUrl(), data.getTitle(), data.getId()));
            }
            if (!tagalogMovieList.isEmpty()) {
                movieRuAdapter.setNewData(tagalogMovieList);
            } else {
                Toast.makeText(getContext(), "No data", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @SuppressWarnings("deprecation")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
                searchData();
                break;
            case R.id.tv_clear:
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme)
                        .setTitle("Clear history")
                        .setMessage("Are you sure want to clear all? ")
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            ArrayList<String> loadedList = SharedPreferencesHelper.loadStringList(getContext(), "recent_search");
                            if (loadedList == null) {
                                loadedList = new ArrayList<>();
                            }
                            if(loadedList.isEmpty()){
                                Toast.makeText(getContext(),"No history available to delete",Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Toast.makeText(getContext(),"Delete history success",Toast.LENGTH_SHORT).show();
                            ArrayList<String> emptyList = new ArrayList<>();
                            SharedPreferencesHelper.saveStringList(getContext(), "recent_search", emptyList);
                            initRecent("");
                            dialog.dismiss();

                        })
                        .setNegativeButton(android.R.string.no, (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .create();

                alertDialog.setOnShowListener(dialog -> {
                    alertDialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                });

                alertDialog.show();

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
        ll_empty.setVisibility(View.GONE);
        isSelect = true;
        ArrayList<String> existingList = SharedPreferencesHelper.loadStringList(getContext(), "recent_search");
        if (existingList == null) {
            existingList = new ArrayList<>();
        }

        existingList.remove(query);
        existingList.add(0, query);
        if (existingList.size() > 10) {
            existingList = new ArrayList<>(existingList.subList(0, 10));
        }

        SharedPreferencesHelper.saveStringList(getContext(), "recent_search", existingList);



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
//                searchPresenter.getTagalogQuery(query);
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
            case 8:
                tagalogMovieList.clear();
                movieRuAdapter.clearCache();
                searchPresenter.getTagalogMovieQuery(query);
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
            case 8:
                toast = "Please enter Tagalog movie";
                break;
        }
        Toast.makeText(getContext(),toast, Toast.LENGTH_SHORT).show();
    }



    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int index, long l) {

        switch (index){
            case 0:
                position = 1;
                break;
            case 1:
                position = 2;
                break;
            case 2:
                position = 7;
                break;
            case 3:
                position = 3;
                break;
            case 4:
                position = 4;
                break;
            case 5:
                position = 5;
                break;
            case 6:
                position = 8;
                break;
        }

        searchAll();
        if(et_search.getText().toString().isEmpty()) {
            return;
        }
        if(isSelect){
            searchData();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    private void searchAll() {
        if(!isNetworkAvailable()){
            Toast.makeText(getContext(),"Please check internet and try again",Toast.LENGTH_SHORT).show();
            return;
        }

        if(position == 3 ){
            Toast.makeText(getContext(),"We fix soon please wait for update!",Toast.LENGTH_SHORT).show();
            return;
        }
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
            case 8:
                et_search.setHint("Enter Tagalog Movie");
                rv_search.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                movieRuAdapter = new MovieRuAdapter();
                rv_search.setAdapter(movieRuAdapter);
                tagalogMovieList.clear();
                if(ll_reset !=null){
                    ll_reset.setVisibility(View.GONE);
                }
                break;


        }
    }

}