package com.m.freemovie.mvp.Presenter;

import androidx.annotation.NonNull;

import com.m.freemovie.Retrofit.Callback;
import com.m.freemovie.mvp.Model.ClassBean.RevivalSeriesBean;
import com.m.freemovie.mvp.Contract.RevivalContractSeries;
import com.m.freemovie.mvp.Model.RevivalSeriesModel;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class RevivalSeriesPresenter implements RevivalContractSeries.Presenter {
    private RevivalContractSeries.View view;

    public RevivalSeriesPresenter(RevivalContractSeries.View view) {
        this.view = view;
    }

    @Override
    public void getListTv(int page) {
        view.showLoading();

        RevivalSeriesModel.getRevivalSeriesData(page, new Callback<RevivalSeriesBean>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            }
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void returnResult(RevivalSeriesBean apiBean) {
                view.hideLoading();
                view.getTvSeries(apiBean);
            }

            @Override
            public void returnError(String message) {
                view.hideLoading();
                view.showError(message);
            }
        });
    }
}