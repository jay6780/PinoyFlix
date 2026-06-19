package com.m.freemovie.mvp.Presenter;

import androidx.annotation.NonNull;

import com.m.freemovie.Retrofit.Callback;
import com.m.freemovie.mvp.Model.ClassBean.AniKoToPageBean;
import com.m.freemovie.mvp.Model.ClassBean.AniNekoBean;
import com.m.freemovie.mvp.Model.ClassBean.AnimoPageBean;
import com.m.freemovie.mvp.Model.ClassBean.PaheLatestBean;
import com.m.freemovie.mvp.Model.ClassBean.RevivalSeriesBean;
import com.m.freemovie.mvp.Model.ClassBean.TagalogBean;
import com.m.freemovie.mvp.Contract.AnimeContract;
import com.m.freemovie.mvp.Model.AnimeModel;
import com.m.freemovie.mvp.Model.ClassBean.ZoRoPageBean;

import java.io.IOException;
import java.util.List;

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
    public void getHotPage(int page) {
        view.showLoading();
        AnimeModel.getHot(page, new Callback<AnimoPageBean>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void returnResult(AnimoPageBean apiBean) {
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

    @Override
    public void getZoRoPage(int page) {

        view.showLoading();
        AnimeModel.getZoRoPage(page, new Callback<List<ZoRoPageBean>>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void returnResult(List<ZoRoPageBean> apiBean) {
                view.hideLoading();
                view.getZoRo(apiBean);
            }

            @Override
            public void returnError(String message) {
                view.hideLoading();
                view.showError(message);
            }
        });

    }

    @Override
    public void getAniKoToPage(int page) {
        view.showLoading();
        AnimeModel.getAniKoToPage(page, new Callback<AniKoToPageBean>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void returnResult(AniKoToPageBean apiBean) {
                view.hideLoading();
                view.getAniKoTo(apiBean);
            }

            @Override
            public void returnError(String message) {
                view.hideLoading();
                view.showError(message);
            }
        });
    }

    @Override
    public void getAniNekoPage(int page) {
        view.showLoading();
        AnimeModel.getAniNekoPage(page, new Callback<AniNekoBean>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void returnResult(AniNekoBean apiBean) {
                view.hideLoading();
                view.getAniNeKo(apiBean);
            }

            @Override
            public void returnError(String message) {
                view.hideLoading();
                view.showError(message);
            }
        });
    }
}