package com.m.freemovie.mvp.Presenter;

import androidx.annotation.NonNull;

import com.m.freemovie.Retrofit.Callback;
import com.m.freemovie.mvp.ClassBean.DetailBean;
import com.m.freemovie.mvp.ClassBean.DetailTvBean;
import com.m.freemovie.mvp.Contract.DetailContract;
import com.m.freemovie.mvp.Model.DetailModel;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class DetailPresenter implements DetailContract.Presenter {
    private DetailContract.View view;

    public DetailPresenter(DetailContract.View view) {
        this.view = view;
    }


    @Override
    public void getDetail(String id, String apiKey) {
        view.showLoading();

        DetailModel.getDetailData(id,apiKey, new Callback<DetailBean>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            }
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void returnResult(DetailBean apiBean) {
                view.hideLoading();
                view.getDetailResponse(apiBean);
            }

            @Override
            public void returnError(String message) {
                view.hideLoading();
                view.showError(message);
            }
        });
    }

    @Override
    public void getTvDetail(String id, String apiKey) {
        view.showLoading();

        DetailModel.getTvDetailData(id,apiKey, new Callback<DetailTvBean>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            }
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void returnResult(DetailTvBean apiBean) {
                view.hideLoading();
                view.getTvDetailResponse(apiBean);
            }

            @Override
            public void returnError(String message) {
                view.hideLoading();
                view.showError(message);
            }
        });
    }
}