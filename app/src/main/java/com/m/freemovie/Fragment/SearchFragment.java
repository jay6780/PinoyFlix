package com.m.freemovie.Fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.m.freemovie.R;
import com.m.freemovie.adapter.TagalogSearchAdapter;
import com.m.freemovie.adapter.ViewAllAdapter;
import com.m.freemovie.mvp.ClassBean.FreeMovieEvent;
import com.m.freemovie.mvp.ClassBean.MovieBean;
import com.m.freemovie.mvp.ClassBean.TagalogSearchBean;
import com.m.freemovie.mvp.ClassBean.TagalogSearchEvent;
import com.m.freemovie.mvp.Contract.SearchContract;
import com.m.freemovie.mvp.Presenter.SearchPresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment implements SearchContract.View,View.OnClickListener {
    private EditText et_search;
    private int page = 1;
    private RecyclerView rv_search;
    private SearchPresenter searchPresenter;
    private ViewAllAdapter movieAdapter;
    private TagalogSearchAdapter tagalogSearchAdapter;
    private boolean isLoading = false;
    private String lastQuery;
    private boolean isNomore = false;
    private ImageView btn_send;
    private List<MovieBean.ResultsBean> movieLists = new ArrayList<>();
    private List<TagalogSearchBean.ResultsBean> tagaloglist = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isTvSeries = false;
    private boolean isTagalog = false;
    private boolean isDefault = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        et_search = view.findViewById(R.id.et_search);
        rv_search = view.findViewById(R.id.rv_search);
        btn_send = view.findViewById(R.id.btn_send);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        searchPresenter = new SearchPresenter(this);
        btn_send.setOnClickListener(this);


        movieAdapter = new ViewAllAdapter();
        rv_search.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        rv_search.setAdapter(movieAdapter);
        tagalogSearchAdapter = new TagalogSearchAdapter();

        rv_search.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
                if (!isLoading && layoutManager != null) {
                    int[] lastVisiblePositions = layoutManager.findLastVisibleItemPositions(null);
                    int lastVisiblePosition = getMaxPosition(lastVisiblePositions);
                    if (lastVisiblePosition >= movieLists.size() - 1) {
                        if(isTagalog){
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

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void changeSearch(FreeMovieEvent event) {
        this.isTvSeries = event.isChangeSearch();
//            Log.d("isTvSeries","value: "+isTvSeries);
        isTagalog = false;

        rv_search.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        movieAdapter = new ViewAllAdapter();
        rv_search.setAdapter(movieAdapter);
        movieLists.clear();
        if(isTvSeries){
            et_search.setHint("Enter series name");
        }else{
            et_search.setHint( "Enter movie name");
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void isTagalog(TagalogSearchEvent event) {
        this.isTagalog = event.isTagalog();
        Log.d("isTagalog","value: "+isTagalog);
        tagaloglist.clear();
        if(isTagalog){
            et_search.setHint("Enter tagalog series");
            rv_search.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            tagalogSearchAdapter = new TagalogSearchAdapter();
            rv_search.setAdapter(tagalogSearchAdapter);

        }

    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private void refresh(){
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
        if(!isNetworkAvailable()){
            Toast.makeText(getContext(),"Please check network and try again",Toast.LENGTH_SHORT).show();
            return;
        }
        String query = et_search.getText().toString().trim();

        if(isTagalog){
            if(query.isEmpty()){
                Toast.makeText(getContext(),"Please enter tagalog series",Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if(isTvSeries){
            if(query.isEmpty()){
                Toast.makeText(getContext(),"Please enter Tv series"  ,Toast.LENGTH_SHORT).show();
                return;
            }
        }else{
            if(query.isEmpty()){
                Toast.makeText(getContext(),"Please enter movie name"  ,Toast.LENGTH_SHORT).show();
                return;
            }
        }


        isNomore = false;
        et_search.setText("");
        movieAdapter.setNewData(new ArrayList<>());
        tagalogSearchAdapter.setNewData(new ArrayList<>());
        lastQuery ="";
        page = 1;
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void loadSearch() {
        if(isTvSeries){
            searchPresenter.getSearchSeries(getString(R.string.key),lastQuery,page);
        }else{
            searchPresenter.getSearchQuery(getString(R.string.key),lastQuery,page);
        }

    }

    @Override
    public void showLoading() {
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void showError(String error) {
        new Handler().postDelayed(() -> {
            Toast.makeText(getContext(),"Error fetching data: "+error,Toast.LENGTH_SHORT).show();
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
    public void getSearchResponse(MovieBean movieBean) {
        if(movieBean!=null && movieBean.getResults() != null){
            isLoading = false;
            if(!movieBean.getResults().isEmpty()){
                movieLists.addAll(movieBean.getResults());
                movieAdapter.setNewData(movieLists);
            }else{
                Toast.makeText(getContext(),"No more movies",Toast.LENGTH_SHORT).show();
                isLoading = false;
                isNomore = true;
            }
        }
    }

    @Override
    public void getSearchSeriesResponse(MovieBean movieBean) {
        if(movieBean!=null && movieBean.getResults() != null){
            isLoading = false;
            if(!movieBean.getResults().isEmpty()){
                movieLists.addAll(movieBean.getResults());
                movieAdapter.setNewData(movieLists);
            }else{
                Toast.makeText(getContext(),"No more Tv series",Toast.LENGTH_SHORT).show();
                isLoading = false;
                isNomore = true;
            }
        }
    }

    @Override
    public void getTagalogSearch(TagalogSearchBean tagalogSearchBean) {
        if(tagalogSearchBean!=null && tagalogSearchBean.getResults() != null){
            isLoading = false;
            if(!tagalogSearchBean.getResults().isEmpty()){
                tagaloglist.addAll(tagalogSearchBean.getResults());
                tagalogSearchAdapter.setNewData(tagaloglist);
            }else{
                Toast.makeText(getContext(),"No more Tv tagalog series",Toast.LENGTH_SHORT).show();
                isLoading = false;
                isNomore = true;
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_send:
                searchData();
                break;
        }
    }

    private void searchData() {
        if(!isNetworkAvailable()){
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
            Toast.makeText(getContext(),"Please check network and try again",Toast.LENGTH_SHORT).show();
            return;
        }
        String query = et_search.getText().toString().trim();
        if(query.isEmpty()){
            Toast.makeText(getContext(),isTvSeries?"Please enter Tv series" :"Please enter movie name" ,Toast.LENGTH_SHORT).show();
            return;
        }
        lastQuery = query;
        isNomore = false;
        page = 1;
        tagalogSearchAdapter.setNewData(new ArrayList<>());
        movieAdapter.setNewData(new ArrayList<>());
        if(isTvSeries){
            searchPresenter.getSearchSeries(getString(R.string.key),query,page);
            movieAdapter.isTvSeries(true);
        }else{
            searchPresenter.getSearchQuery(getString(R.string.key),query,page);
            movieAdapter.isTvSeries(false);
        }

        if(isTagalog){
            searchPresenter.getTagalogQuery(query);
        }

    }
}