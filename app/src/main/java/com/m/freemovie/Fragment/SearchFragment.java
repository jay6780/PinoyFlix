package com.m.freemovie.Fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.m.freemovie.R;
import com.m.freemovie.adapter.MovieAdapter;
import com.m.freemovie.adapter.ViewAllAdapter;
import com.m.freemovie.mvp.ClassBean.MovieBean;
import com.m.freemovie.mvp.Contract.SearchContract;
import com.m.freemovie.mvp.Presenter.SearchPresenter;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment implements SearchContract.View,View.OnClickListener {
    private EditText et_search;
    private int page = 1;
    private RecyclerView rv_search;
    private SearchPresenter searchPresenter;
    private ViewAllAdapter movieAdapter;
    private boolean isLoading = false;
    private List<MovieBean.ResultsBean> movieLists = new ArrayList<>();
    private KProgressHUD hud;
    private String lastQuery;
    private boolean isNomore = false;
    private ImageView btn_send;
    private SwipeRefreshLayout swipeRefreshLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        et_search = view.findViewById(R.id.et_search);
        rv_search =view.findViewById(R.id.rv_search);
        btn_send= view.findViewById(R.id.btn_send);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        rv_search.setLayoutManager(new GridLayoutManager(getContext(), 2));
        movieAdapter = new ViewAllAdapter(getContext(),movieLists);
        rv_search.setAdapter(movieAdapter);
        searchPresenter = new SearchPresenter(this);
        btn_send.setOnClickListener(this);
        hud = KProgressHUD.create(getContext())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait");

        rv_search.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                if (!isLoading && layoutManager != null &&
                        layoutManager.findLastVisibleItemPosition() >= movieLists.size() - 1) {
                    if(isNomore){
                        return;
                    }
                    isLoading = true;
                    page++;
                    loadSearch();
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        return view;
    }

    private void refresh(){
        if(!isNetworkAvailable()){
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
            Toast.makeText(getContext(),"Please check network and try again",Toast.LENGTH_SHORT).show();
            return;
        }
        String query = et_search.getText().toString().trim();
        if(query.isEmpty()){
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
            Toast.makeText(getContext(),"Please enter movie name",Toast.LENGTH_SHORT).show();
            return;
        }
        isNomore = false;
        et_search.setText("");
        movieLists.clear();
        movieAdapter.notifyDataSetChanged();
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
        searchPresenter.getSearchQuery(getString(R.string.key),lastQuery,page);
    }

    @Override
    public void showLoading() {
        hud.show();
    }

    @Override
    public void showError(String error) {
        isLoading = false;
        isNomore = true;
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void hideLoading() {
        if (hud != null && hud.isShowing() && isAdded()) {
            hud.dismiss();
        }
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }


    @Override
    public void getSearchResponse(MovieBean movieBean) {
        if(movieBean!=null && movieBean.getResults() != null){
            isLoading = false;
            if(!movieBean.getResults().isEmpty()){
                movieLists.addAll(movieBean.getResults());
                movieAdapter.notifyDataSetChanged();
            }else{
                Toast.makeText(getContext(),"No more movies",Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getContext(),"Please enter movie name",Toast.LENGTH_SHORT).show();
            return;
        }
        lastQuery = query;
        isNomore = false;
        page = 1;
        movieLists.clear();
        movieAdapter.notifyDataSetChanged();
        searchPresenter.getSearchQuery(getString(R.string.key),query,page);
    }
}