package com.m.freemovie.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    private List<MovieBean.MovieList> movieLists = new ArrayList<>();
    private KProgressHUD hud;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie, container, false);
        rv_movie = view.findViewById(R.id.rv_movie);
        rv_movie.setLayoutManager(new LinearLayoutManager(getContext()));
        movieAdapter = new MovieAdapter(getContext(),movieLists);
        rv_movie.setAdapter(movieAdapter);
        hud = KProgressHUD.create(getContext())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait");
        moviePresenter = new MoviePresenter(this);
        String query = "page-"+page+".json";
        moviePresenter.getMovieQuery(query);

        view.findViewById(R.id.rotate).setOnClickListener(view1 -> refresh());


        rv_movie.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (!isLoading && layoutManager != null &&
                        layoutManager.findLastVisibleItemPosition() >= movieLists.size() - 1) {
                    isLoading = true;
                    page++;
                    loadMore();
                }
            }
        });

        return view;
    }

    private void refresh(){
        movieLists.clear();
        movieAdapter.notifyDataSetChanged();
        page = 1;
        String query = "page-"+page+".json";
        moviePresenter.getMovieQuery(query);
    }

    private void loadMore() {
        String query = "page-"+page+".json";
        moviePresenter.getMovieQuery(query);
    }

    @Override
    public void showLoading() {
        hud.show();
    }

    @Override
    public void showError(String error) {

    }

    @Override
    public void hideLoading() {
        if (hud != null && hud.isShowing() && isAdded()) {
            hud.dismiss();
        }
    }

    @Override
    public void getMovieResponse(MovieBean movieBean) {
        if(movieBean!=null && movieBean.getResult() != null){
            isLoading = false;
            for(MovieBean.MovieList movieBean1 : movieBean.getResult()){
                movieLists.add(movieBean1);
                movieAdapter.notifyDataSetChanged();
            }
        }

    }
}