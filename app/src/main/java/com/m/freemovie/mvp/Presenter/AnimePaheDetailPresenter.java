package com.m.freemovie.mvp.Presenter;

import androidx.annotation.NonNull;

import com.m.freemovie.Retrofit.Callback;
import com.m.freemovie.mvp.Contract.AnimePaheDetailContract;
import com.m.freemovie.mvp.Model.ClassBean.ZoRoDetailBean;
import com.m.freemovie.mvp.Model.ClassBean.ZoRoVideoUrlBean;
import com.m.freemovie.mvp.Model.ZoroDetailModel;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class AnimePaheDetailPresenter implements AnimePaheDetailContract.Presenter {
    private AnimePaheDetailContract.View view;

    public AnimePaheDetailPresenter(AnimePaheDetailContract.View view) {
        this.view = view;
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
    public void getZoRoVideoUrl(String Url) {
        view.showLoading();

        ZoroDetailModel.getZoRoVideoUrl(Url, new Callback<ZoRoVideoUrlBean>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void returnResult(ZoRoVideoUrlBean apiBean) {
                view.hideLoading();
                view.getZoRoVideo(apiBean);
            }

            @Override
            public void returnError(String message) {
                view.hideLoading();
                view.showError(message);
            }
        });
    }
}