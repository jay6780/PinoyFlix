package com.m.freemovie.mvp.Presenter;

import androidx.annotation.NonNull;

import com.m.freemovie.Retrofit.Callback;
import com.m.freemovie.mvp.Model.AniMoDetailModel;
import com.m.freemovie.mvp.Model.ClassBean.AniNeKoDetailModel;
import com.m.freemovie.mvp.Model.ClassBean.AniNeKoInfoBean;
import com.m.freemovie.mvp.Model.ClassBean.AnimePaheDetailBean;
import com.m.freemovie.mvp.Model.ClassBean.AnimoDetailsBean;
import com.m.freemovie.mvp.Model.ClassBean.TagalogEpisodeBean;
import com.m.freemovie.mvp.Model.ClassBean.TagalogInfoBean;
import com.m.freemovie.mvp.Contract.AnimeDetailsContract;
import com.m.freemovie.mvp.Model.AnimePaheDetailModel;
import com.m.freemovie.mvp.Model.ClassBean.ZoRoDetailBean;
import com.m.freemovie.mvp.Model.RevivalDetailModel;
import com.m.freemovie.mvp.Model.TagalogEpisodeModel;
import com.m.freemovie.mvp.Model.ZoroDetailModel;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class AnimeDetailPresenter implements AnimeDetailsContract.Presenter {
    private AnimeDetailsContract.View view;

    public AnimeDetailPresenter(AnimeDetailsContract.View view) {
        this.view = view;
    }

    @Override
    public void getDetailAnimePaHe(String url) {
        view.showLoading();

        AnimePaheDetailModel.getDetailData(url, new Callback<AnimePaheDetailBean>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void returnResult(AnimePaheDetailBean apiBean) {
                view.hideLoading();
                view.getDetailData(apiBean);
            }

            @Override
            public void returnError(String message) {
                view.hideLoading();
                view.showError(message);
            }
        });
    }

    @Override
    public void getUrl(String url) {
        view.showLoading();

        TagalogEpisodeModel.getTagalogEpisode(url, new Callback<TagalogEpisodeBean>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void returnResult(TagalogEpisodeBean apiBean) {
                view.hideLoading();
                view.getTagalogEpisode(apiBean);
            }

            @Override
            public void returnError(String message) {
                view.hideLoading();
                view.showError(message);
            }
        });
    }

    @Override
    public void getListTv(String Url) {
        view.showLoading();

        RevivalDetailModel.getRevivalInfo(Url, new Callback<TagalogInfoBean>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void returnResult(TagalogInfoBean apiBean) {
                view.hideLoading();
                view.getInfoTagalog(apiBean);
            }

            @Override
            public void returnError(String message) {
                view.hideLoading();
                view.showError(message);
            }
        });
    }

    @Override
    public void getZoroUrl(String Url) {
        view.showLoading();

        ZoroDetailModel.getZoroUrl(Url, new Callback<ZoRoDetailBean>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void returnResult(ZoRoDetailBean apiBean) {
                view.hideLoading();
                view.getZoroDetail(apiBean);
            }

            @Override
            public void returnError(String message) {
                view.hideLoading();
                view.showError(message);
            }
        });
    }

    @Override
    public void getAniNekoUrl(String Url) {
        view.showLoading();

        AniNeKoDetailModel.getAniNekoUrl(Url, new Callback<AniNeKoInfoBean>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void returnResult(AniNeKoInfoBean apiBean) {
                view.hideLoading();
                view.getAniNekoDetail(apiBean);
            }

            @Override
            public void returnError(String message) {
                view.hideLoading();
                view.showError(message);
            }
        });
    }

    @Override
    public void getAniMoTvUrl(String Url) {
        view.showLoading();

        AniMoDetailModel.getAniMoDetailData(Url, new Callback<AnimoDetailsBean>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void returnResult(AnimoDetailsBean apiBean) {
                view.hideLoading();
                view.getAniMoTvDetail(apiBean);
            }

            @Override
            public void returnError(String message) {
                view.hideLoading();
                view.showError(message);
            }
        });
    }

}