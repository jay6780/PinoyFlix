package com.m.freemovie.mvp.Presenter;

import androidx.annotation.NonNull;

import com.m.freemovie.Retrofit.Callback;
import com.m.freemovie.mvp.ClassBean.PaheLatestBean;
import com.m.freemovie.mvp.ClassBean.RevivalSeriesBean;
import com.m.freemovie.mvp.ClassBean.TagalogBean;
import com.m.freemovie.mvp.Contract.AnimeContract;
import com.m.freemovie.mvp.Model.AnimeModel;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class AnimePresenter implements AnimeContract.Presenter {
    private AnimeContract.View view;
    public AnimePresenter(AnimeContract.View view) {
        this.view = view;
    }
    @Override
    public void getNewestPage(int page) {
        view.showLoading();
            AnimeModel.getNewest(page, new Callback<PaheLatestBean>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            }
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void returnResult(PaheLatestBean apiBean) {
                view.hideLoading();
                view.getNewest(apiBean);
            }

            @Override
            public void returnError(String message) {
                view.hideLoading();
                view.showError(message);
            }
        });
    }

    @Override
    public void getHotPage() {
        view.showLoading();
        AnimeModel.getHot(new Callback<TagalogBean>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            }
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void returnResult(TagalogBean apiBean) {
                view.hideLoading();
                view.getHot(apiBean);
            }

            @Override
            public void returnError(String message) {
                view.hideLoading();
                view.showError(message);
            }
        });
    }

    @Override
    public void getPopular(int page) {
        view.showLoading();
        AnimeModel.getPopular(page, new Callback<RevivalSeriesBean>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            }
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void returnResult(RevivalSeriesBean apiBean) {
                view.hideLoading();
                view.getPopular(apiBean);
            }

            @Override
            public void returnError(String message) {
                view.hideLoading();
                view.showError(message);
            }
        });
    }

    @Override
    public void getMovie(int page) {
        view.showLoading();
        AnimeModel.getMovies(page, new Callback<RevivalSeriesBean>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            }
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void returnResult(RevivalSeriesBean apiBean) {
                view.hideLoading();
                view.getMovie(apiBean);
            }

            @Override
            public void returnError(String message) {
                view.hideLoading();
                view.showError(message);
            }
        });
    }
}