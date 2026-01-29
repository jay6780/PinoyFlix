package com.m.freemovie.mvp.Presenter;

import androidx.annotation.NonNull;

import com.m.freemovie.Retrofit.Callback;
import com.m.freemovie.mvp.ClassBean.AnimePaheDetailBean;
import com.m.freemovie.mvp.ClassBean.TagalogEpisodeBean;
import com.m.freemovie.mvp.ClassBean.TagalogInfoBean;
import com.m.freemovie.mvp.Contract.AnimeDetailsContract;
import com.m.freemovie.mvp.Model.AnimePaheDetailModel;
import com.m.freemovie.mvp.Model.RevivalDetailModel;
import com.m.freemovie.mvp.Model.TagalogEpisodeModel;

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

        TagalogEpisodeModel.getTagalogEpisode(url,new Callback<TagalogEpisodeBean>() {
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

}