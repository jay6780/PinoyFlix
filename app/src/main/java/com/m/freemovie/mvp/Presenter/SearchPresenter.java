package com.m.freemovie.mvp.Presenter;

import androidx.annotation.NonNull;

import com.m.freemovie.Retrofit.Callback;
import com.m.freemovie.mvp.ClassBean.MovieBean;
import com.m.freemovie.mvp.Contract.SearchContract;
import com.m.freemovie.mvp.Model.MovieModel;

import java.io.IOException;

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
}