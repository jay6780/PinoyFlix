package com.m.freemovie.mvp.Presenter;

import androidx.annotation.NonNull;

import com.m.freemovie.Retrofit.Callback;
import com.m.freemovie.mvp.ClassBean.DownloadNineAnimeBean;
import com.m.freemovie.mvp.ClassBean.NineAnimeEpisodeBean;
import com.m.freemovie.mvp.Contract.NineAnimeDetailContract;
import com.m.freemovie.mvp.Model.NineAnimeDetailModel;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class NineAnimeDetailPresenter implements NineAnimeDetailContract.Presenter {
    private NineAnimeDetailContract.View view;

    public NineAnimeDetailPresenter(NineAnimeDetailContract.View view) {
        this.view = view;
    }

    @Override
    public void getDetails(String url) {
        view.showLoading();

        NineAnimeDetailModel.getDetailData(url, new Callback<NineAnimeEpisodeBean>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            }
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void returnResult(NineAnimeEpisodeBean apiBean) {
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
    public void getVideoUrl(String url) {
        view.showLoading();

        NineAnimeDetailModel.getVideoTrack(url, new Callback<DownloadNineAnimeBean>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            }
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void returnResult(DownloadNineAnimeBean apiBean) {
                view.hideLoading();
                view.getVideo(apiBean);
            }

            @Override
            public void returnError(String message) {
                view.hideLoading();
                view.showError(message);
            }
        });
    }
}