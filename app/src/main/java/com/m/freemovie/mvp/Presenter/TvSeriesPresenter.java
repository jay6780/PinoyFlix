package com.m.freemovie.mvp.Presenter;

import androidx.annotation.NonNull;

import com.m.freemovie.Retrofit.Callback;
import com.m.freemovie.mvp.Model.ClassBean.TvSeriesBean;
import com.m.freemovie.mvp.Contract.TvSeriesContract;
import com.m.freemovie.mvp.Model.TvSeriesModel;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class TvSeriesPresenter implements TvSeriesContract.Presenter {
    private TvSeriesContract.View view;

    public TvSeriesPresenter(TvSeriesContract.View view) {
        this.view = view;
    }

    @Override
    public void getTodayTv(String apiKey, int page) {
        view.showLoading();
        TvSeriesModel.getToday(apiKey,page, new Callback<TvSeriesBean>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            }
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void returnResult(TvSeriesBean apiBean) {
                view.hideLoading();
                view.getTodayTvResponse(apiBean);
            }

            @Override
            public void returnError(String message) {
                view.hideLoading();
                view.showError(message);
            }
        });
    }

    @Override
    public void getOnAiringTv(String apiKey, int page) {
        view.showLoading();
        TvSeriesModel.getAiring(apiKey,page, new Callback<TvSeriesBean>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            }
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void returnResult(TvSeriesBean apiBean) {
                view.hideLoading();
                view.getOnAiringTvResponse(apiBean);
            }

            @Override
            public void returnError(String message) {
                view.hideLoading();
                view.showError(message);
            }
        });
    }

    @Override
    public void getPopularTv(String apiKey, int page) {
        view.showLoading();
        TvSeriesModel.getTvPopular(apiKey,page, new Callback<TvSeriesBean>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            }
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void returnResult(TvSeriesBean apiBean) {
                view.hideLoading();
                view.getPopularTvResponse(apiBean);
            }

            @Override
            public void returnError(String message) {
                view.hideLoading();
                view.showError(message);
            }
        });
    }

    @Override
    public void getTopRatedTv(String apiKey, int page) {
        view.showLoading();
        TvSeriesModel.getTopRatedTv(apiKey,page, new Callback<TvSeriesBean>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            }
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void returnResult(TvSeriesBean apiBean) {
                view.hideLoading();
                view.getTopRatedTvResponse(apiBean);
            }

            @Override
            public void returnError(String message) {
                view.hideLoading();
                view.showError(message);
            }
        });
    }
}