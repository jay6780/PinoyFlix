package com.m.freemovie.mvp.Presenter;

import androidx.annotation.NonNull;

import com.m.freemovie.Retrofit.Callback;
import com.m.freemovie.mvp.Model.ClassBean.AnimePaheDetailBean;
import com.m.freemovie.mvp.Model.ClassBean.AnimePaheDownloadBean;
import com.m.freemovie.mvp.Model.ClassBean.AnimePaheEpisodeBean;
import com.m.freemovie.mvp.Contract.AnimePaheDetailContract;
import com.m.freemovie.mvp.Model.AnimePaheDetailModel;
import com.m.freemovie.mvp.Model.ClassBean.MiRuRoEpisodeBean;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class AnimePaheDetailPresenter implements AnimePaheDetailContract.Presenter {
    private AnimePaheDetailContract.View view;

    public AnimePaheDetailPresenter(AnimePaheDetailContract.View view) {
        this.view = view;
    }

    @Override
    public void getDetailQuery(String url) {
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
    public void getEpisodeQuery(String id,int page) {
        view.showLoading();

        AnimePaheDetailModel.getEpisode(id,page, new Callback<AnimePaheEpisodeBean>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            }
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void returnResult(AnimePaheEpisodeBean apiBean) {
                view.hideLoading();
                view.getEpisodes(apiBean);
            }

            @Override
            public void returnError(String message) {
                view.hideLoading();
                view.showError(message);
            }
        });
    }

    @Override
    public void getTrackQuery(String url) {
        view.showLoading();

        AnimePaheDetailModel.getTrack(url, new Callback<AnimePaheDownloadBean>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            }
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void returnResult(AnimePaheDownloadBean apiBean) {
                view.hideLoading();
                view.getTrack(apiBean);
            }

            @Override
            public void returnError(String message) {
                view.hideLoading();
                view.showError(message);
            }
        });
    }

    @Override
    public void getMiRuRoEpisodeQuery(String url) {
        view.showLoading();

        AnimePaheDetailModel.getMiRuRoEpisode(url, new Callback<MiRuRoEpisodeBean>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            }
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void returnResult(MiRuRoEpisodeBean apiBean) {
                view.hideLoading();
                view.getEpisodesMiRuRo(apiBean);
            }

            @Override
            public void returnError(String message) {
                view.hideLoading();
                view.showError(message);
            }
        });
    }
}