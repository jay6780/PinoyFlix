package com.m.freemovie.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.m.freemovie.R;
import com.m.freemovie.adapter.MovieAdapter;
import com.m.freemovie.mvp.ClassBean.MovieBean;
import com.m.freemovie.mvp.Contract.MovieContract;
import com.m.freemovie.mvp.Presenter.MoviePresenter;

import java.util.ArrayList;
import java.util.List;

public class MovieFragment extends Fragment implements MovieContract.View {
    private RecyclerView rv_movie;
    private MoviePresenter moviePresenter;
    private int page = 1;
    private MovieAdapter movieAdapter;
    private boolean isLoading = false;
    private List<MovieBean.ResultsBean> movieLists = new ArrayList<>();
    private KProgressHUD hud;
    private EditText search;
    private boolean isSearch = false;
    private String lastQuery;
    private boolean isNomore = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie, container, false);
        rv_movie = view.findViewById(R.id.rv_movie);
        search = view.findViewById(R.id.search);
        rv_movie.setLayoutManager(new GridLayoutManager(getContext(), 2));
        movieAdapter = new MovieAdapter(getContext(),movieLists);
        rv_movie.setAdapter(movieAdapter);
        hud = KProgressHUD.create(getContext())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait");
        moviePresenter = new MoviePresenter(this);

        moviePresenter.getLatestMovie(getString(R.string.key),page);

        view.findViewById(R.id.rotate).setOnClickListener(view1 -> refresh());
        view.findViewById(R.id.btn_send).setOnClickListener(view1 -> searchData());

        rv_movie.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                    if(isSearch){
                        loadSearch();
                    }else{
                        loadMore();
                    }

                }
            }
        });

        return view;
    }

    private void searchData() {
        String query = search.getText().toString().trim();
        if(query.isEmpty()){
            Toast.makeText(getContext(),"Please enter movie name",Toast.LENGTH_SHORT).show();
            return;
        }
        isSearch = true;
        lastQuery = query;
        isNomore = false;
        page = 1;
        movieLists.clear();
        movieAdapter.notifyDataSetChanged();
        moviePresenter.getSearchQuery(getString(R.string.key),query,page);
    }

    private void refresh(){
        isSearch = false;
        isNomore = false;
        search.setText("");
        movieLists.clear();
        movieAdapter.notifyDataSetChanged();
        page = 1;
        moviePresenter.getLatestMovie(getString(R.string.key),page);
    }

    private void loadSearch() {
        moviePresenter.getSearchQuery(getString(R.string.key),lastQuery,page);
    }
    private void loadMore() {
        moviePresenter.getLatestMovie(getString(R.string.key),page);
    }

    @Override
    public void showLoading() {
        hud.show();
    }

    @Override
    public void showError(String error) {
        isLoading = false;
        isNomore = true;
        Log.e("MovieFragment", "Error: " + error);
    }

    @Override
    public void hideLoading() {
        if (hud != null && hud.isShowing() && isAdded()) {
            hud.dismiss();
        }
    }

    @Override
    public void getMovieResponse(MovieBean movieBean) {
        if(movieBean!=null && movieBean.getResults() != null){
            isSearch = false;
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
}