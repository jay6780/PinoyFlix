package com.m.freemovie.mvp.Presenter;

import androidx.annotation.NonNull;

import com.m.freemovie.Retrofit.Callback;
import com.m.freemovie.mvp.Model.ClassBean.NineAnimeBean;
import com.m.freemovie.mvp.Contract.NineAnimeContract;
import com.m.freemovie.mvp.Model.NineAnimeModel;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class NineAminePresenter implements NineAnimeContract.Presenter {
    private NineAnimeContract.View view;

    public NineAminePresenter(NineAnimeContract.View view) {
        this.view = view;
    }

    @Override
    public void getPageOngoing(int page) {
        view.showLoading();

        NineAnimeModel.getOngoing(page, new Callback<NineAnimeBean>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            }
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void returnResult(NineAnimeBean apiBean) {
                view.hideLoading();
                view.getOngoing(apiBean);
            }

            @Override
            public void returnError(String message) {
                view.hideLoading();
                view.showError(message);
            }
        });

    }

    @Override
    public void getPageLatest(int page) {
        view.showLoading();

        NineAnimeModel.getLatest(page, new Callback<NineAnimeBean>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            }
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void returnResult(NineAnimeBean apiBean) {
                view.hideLoading();
                view.getLatest(apiBean);
            }

            @Override
            public void returnError(String message) {
                view.hideLoading();
                view.showError(message);
            }
        });
    }
}