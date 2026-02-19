package com.m.freemovie.mvp.Presenter;

import androidx.annotation.NonNull;

import com.m.freemovie.Retrofit.Callback;
import com.m.freemovie.mvp.Model.ClassBean.AnimePaheSearchBean;
import com.m.freemovie.mvp.Model.ClassBean.MovieBean;
import com.m.freemovie.mvp.Model.ClassBean.NineAnimeSearchBean;
import com.m.freemovie.mvp.Model.ClassBean.PinoyMovieRuBean;
import com.m.freemovie.mvp.Model.ClassBean.SearchRuBean;
import com.m.freemovie.mvp.Model.ClassBean.TagalogSearchBean;
import com.m.freemovie.mvp.Contract.SearchContract;
import com.m.freemovie.mvp.Model.MovieModel;
import com.m.freemovie.mvp.Model.PinoyRuModel;
import com.m.freemovie.mvp.Model.SearchRuModel;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class SearchPresenter implements SearchContract.Presenter {
    private SearchContract.View view;
    public SearchPresenter(SearchContract.View view) {
        this.view = view;
    }
    @Override
    public void getSearchQuery(String apiKey, String query, int page) {
        view.showLoading();
        MovieModel.getSearch(apiKey, query, page, new Callback<MovieBean>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void returnResult(MovieBean apiBean) {
                view.hideLoading();
                view.getSearchResponse(apiBean);
            }

            @Override
            public void returnError(String message) {
                view.hideLoading();
                view.showError(message);
            }
        });
    }


    @Override
    public void getSearchSeries(String apiKey, String query, int page) {
        view.showLoading();
        MovieModel.getSearchSeries(apiKey, query, page, new Callback<MovieBean>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void returnResult(MovieBean apiBean) {
                view.hideLoading();
                view.getSearchSeriesResponse(apiBean);
            }

            @Override
            public void returnError(String message) {
                view.hideLoading();
                view.showError(message);
            }
        });
    }

    @Override
    public void getTagalogQuery(String query) {
        view.showLoading();
        MovieModel.getTagalogSearch(query ,new Callback<TagalogSearchBean>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void returnResult(TagalogSearchBean apiBean) {
                view.hideLoading();
                view.getTagalogSearch(apiBean);
            }

            @Override
            public void returnError(String message) {
                view.hideLoading();
                view.showError(message);
            }
        });
    }

    @Override
    public void getNineAnimeQuery(String query) {
        view.showLoading();
        MovieModel.getNineAnime(query ,new Callback<NineAnimeSearchBean>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void returnResult(NineAnimeSearchBean apiBean) {
                view.hideLoading();
                view.getNineAnime(apiBean);
            }

            @Override
            public void returnError(String message) {
                view.hideLoading();
                view.showError(message);
            }
        });
    }

    @Override
    public void getAnimePaheQuery(String search) {
        view.showLoading();
        MovieModel.getSearchPahe(search ,new Callback<AnimePaheSearchBean>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void returnResult(AnimePaheSearchBean apiBean) {
                view.hideLoading();
                view.getSearchPahe(apiBean);
            }

            @Override
            public void returnError(String message) {
                view.hideLoading();
                view.showError(message);
            }
        });
    }

    @Override
    public void getTagalogMovieQuery(String query) {
            view.showLoading();
            SearchRuModel.getSearchRu(query, new Callback<List<SearchRuBean>>() {
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                }
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                }

                @Override
                public void returnResult(List<SearchRuBean> apiBean) {
                    view.hideLoading();
                    view.getSearchList(apiBean);
                }

                @Override
                public void returnError(String message) {
                    view.hideLoading();
                    view.showError(message);
                }
            });

    }
}