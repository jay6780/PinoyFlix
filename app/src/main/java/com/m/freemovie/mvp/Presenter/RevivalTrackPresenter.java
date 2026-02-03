package com.m.freemovie.mvp.Presenter;

import androidx.annotation.NonNull;

import com.m.freemovie.Retrofit.Callback;
import com.m.freemovie.mvp.Model.ClassBean.DetailDownloadBean;
import com.m.freemovie.mvp.Contract.RevivalContractTrack;
import com.m.freemovie.mvp.Model.RevivalTrackModel;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class RevivalTrackPresenter implements RevivalContractTrack.Presenter {
    private RevivalContractTrack.View view;

    public RevivalTrackPresenter(RevivalContractTrack.View view) {
        this.view = view;
    }
    @Override
    public void getTrackUrl(String Url) {
        view.showLoading();

        RevivalTrackModel.getTrackInfo(Url, new Callback<DetailDownloadBean>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            }
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void returnResult(DetailDownloadBean apiBean) {
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
}