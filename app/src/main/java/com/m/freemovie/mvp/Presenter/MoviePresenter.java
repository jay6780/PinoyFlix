package com.m.freemovie.mvp.Presenter;

import androidx.annotation.NonNull;

import com.m.freemovie.Retrofit.Callback;
import com.m.freemovie.mvp.Model.ClassBean.MovieBean;
import com.m.freemovie.mvp.Contract.MovieContract;
import com.m.freemovie.mvp.Model.MovieModel;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class MoviePresenter implements MovieContract.Presenter {
    private MovieContract.View view;

    public MoviePresenter(MovieContract.View view) {
        this.view = view;
    }

    @Override
    public void getPopularMovie(String apiKey, int page) {
        view.showLoading();
        MovieModel.getPopular(apiKey,page, new Callback<MovieBean>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            }
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void returnResult(MovieBean apiBean) {
                view.hideLoading();
                view.getPopularResponse(apiBean);
            }

            @Override
            public void returnError(String message) {
                view.hideLoading();
                view.showError(message);
            }
        });
    }

    @Override
    public void getTopRated(String apiKey, int page) {
        view.showLoading();
        MovieModel.getTopRated(apiKey,page, new Callback<MovieBean>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            }
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void returnResult(MovieBean apiBean) {
                view.hideLoading();
                view.getTopRatedResponse(apiBean);
            }

            @Override
            public void returnError(String message) {
                view.hideLoading();
                view.showError(message);
            }
        });
    }

    @Override
    public void getUpcoming(String apiKey, int page) {
        view.showLoading();
        MovieModel.getUpcoming(apiKey,page, new Callback<MovieBean>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void returnResult(MovieBean apiBean) {
                view.hideLoading();
                view.getUpcomingResponse(apiBean);
            }

            @Override
            public void returnError(String message) {
                view.hideLoading();
                view.showError(message);
            }
        });
    }

    @Override
    public void getNow(String apiKey, int page) {
        view.showLoading();
        MovieModel.getNow(apiKey, page, new Callback<MovieBean>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void returnResult(MovieBean apiBean) {
                view.hideLoading();
                view.getNowResponse(apiBean);
            }

            @Override
            public void returnError(String message) {
                view.hideLoading();
                view.showError(message);
            }
        });

    }
}