package com.m.freemovie.mvp.Presenter;

import androidx.annotation.NonNull;

import com.m.freemovie.Retrofit.Callback;
import com.m.freemovie.mvp.Contract.SearchContract;
import com.m.freemovie.mvp.Model.AniKoToSearchModel;
import com.m.freemovie.mvp.Model.AniMoTvSearchModel;
import com.m.freemovie.mvp.Model.AniNekoSearchModel;
import com.m.freemovie.mvp.Model.ClassBean.AniKoToSearchBean;
import com.m.freemovie.mvp.Model.ClassBean.AniMoTvSearchBean;
import com.m.freemovie.mvp.Model.ClassBean.AniNekoSearchBean;
import com.m.freemovie.mvp.Model.ClassBean.AnimePaheSearchBean;
import com.m.freemovie.mvp.Model.ClassBean.MovieBean;
import com.m.freemovie.mvp.Model.ClassBean.NineAnimeSearchBean;
import com.m.freemovie.mvp.Model.ClassBean.SearchRuBean;
import com.m.freemovie.mvp.Model.ClassBean.TagalogSearchBean;
import com.m.freemovie.mvp.Model.ClassBean.ZoRoSearchBean;
import com.m.freemovie.mvp.Model.MovieModel;
import com.m.freemovie.mvp.Model.SearchRuModel;
import com.m.freemovie.mvp.Model.ZoroSearchModel;

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
        MovieModel.getTagalogSearch(query, new Callback<TagalogSearchBean>() {
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
        MovieModel.getNineAnime(query, new Callback<NineAnimeSearchBean>() {
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
        MovieModel.getSearchPahe(search, new Callback<AnimePaheSearchBean>() {
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

    @Override
    public void getZoRoQuery(String search) {
        view.showLoading();
        ZoroSearchModel.getZoRoSearchQuery(search, new Callback<ZoRoSearchBean>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void returnResult(ZoRoSearchBean apiBean) {
                view.hideLoading();
                view.getZoRoSearch(apiBean);
            }

            @Override
            public void returnError(String message) {
                view.hideLoading();
                view.showError(message);
            }
        });

    }

    @Override
    public void getAniKoToQuery(String keyword) {
        view.showLoading();
        AniKoToSearchModel.getAniKoToSearch(keyword, new Callback<AniKoToSearchBean>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void returnResult(AniKoToSearchBean apiBean) {
                view.hideLoading();
                view.getAniKoToSearch(apiBean);
            }

            @Override
            public void returnError(String message) {
                view.hideLoading();
                view.showError(message);
            }
        });
    }

    @Override
    public void getAniNeKoQuery(String q) {
        view.showLoading();
        AniNekoSearchModel.getAniNeKoQuery(q, new Callback<AniNekoSearchBean>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void returnResult(AniNekoSearchBean apiBean) {
                view.hideLoading();
                view.getAniNeKoSearchData(apiBean);
            }

            @Override
            public void returnError(String message) {
                view.hideLoading();
                view.showError(message);
            }
        });
    }

    @Override
    public void getAniMoTvQuery(String search) {
        view.showLoading();
        AniMoTvSearchModel.getAniMoSearch(search, new Callback<AniMoTvSearchBean>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void returnResult(AniMoTvSearchBean apiBean) {
                view.hideLoading();
                view.getAniMoTvSearch(apiBean);
            }

            @Override
            public void returnError(String message) {
                view.hideLoading();
                view.showError(message);
            }
        });
    }


}