package com.m.freemovie.mvp.Presenter;

import androidx.annotation.NonNull;

import com.m.freemovie.Retrofit.Callback;
import com.m.freemovie.mvp.Model.ClassBean.MovieBean;
import com.m.freemovie.mvp.Contract.MovieWatchListContract;
import com.m.freemovie.mvp.Model.ViewAllModel;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class MovieWatchListPresenter implements MovieWatchListContract.Presenter {
    private MovieWatchListContract.View view;

    public MovieWatchListPresenter(MovieWatchListContract.View view) {
        this.view = view;
    }

    @Override
    public void getViewAll(String apiKey, int page,int positon) {
        view.showLoading();
        ViewAllModel.getListResponse(apiKey,page,positon,new Callback<MovieBean>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            }
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void returnResult(MovieBean apiBean) {
                view.hideLoading();
                view.getViewAllResponse(apiBean);
            }

            @Override
            public void returnError(String message) {
                view.hideLoading();
                view.showError(message);
            }
        });
    }
}