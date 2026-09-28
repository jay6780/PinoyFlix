package com.m.freemovie.mvp.Presenter;

import androidx.annotation.NonNull;

import com.m.freemovie.Retrofit.Callback;
import com.m.freemovie.mvp.Contract.ApiListContract;
import com.m.freemovie.mvp.Model.ApiListModel;
import com.m.freemovie.mvp.Model.ClassBean.MovieApiBean;
import com.m.freemovie.mvp.Model.ClassBean.SeriesApiBean;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class ApiListPresenter implements ApiListContract.Presenter {
    private ApiListContract.View view;

    public ApiListPresenter(ApiListContract.View view) {
        this.view = view;
    }


    @Override
    public void getMovieApiList() {
        view.showLoading();

        ApiListModel.getMovieApi( new Callback<MovieApiBean>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void returnResult(MovieApiBean apiBean) {
                view.hideLoading();
                view.getMovieApi(apiBean);
            }

            @Override
            public void returnError(String message) {
                view.hideLoading();
                view.showError(message);
            }
        });
    }

    @Override
    public void getSeriesApiList() {
        view.showLoading();

        ApiListModel.getSeriesApi( new Callback<SeriesApiBean>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void returnResult(SeriesApiBean apiBean) {
                view.hideLoading();
                view.getSeriesApi(apiBean);
            }

            @Override
            public void returnError(String message) {
                view.hideLoading();
                view.showError(message);
            }
        });
    }
}