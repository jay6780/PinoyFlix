package com.m.freemovie.mvp.Presenter;

import androidx.annotation.NonNull;

import com.m.freemovie.Retrofit.Callback;
import com.m.freemovie.mvp.ClassBean.RevivalSeriesBean;
import com.m.freemovie.mvp.ClassBean.TagalogInfoBean;
import com.m.freemovie.mvp.Contract.RevivalContractDetail;
import com.m.freemovie.mvp.Contract.RevivalContractSeries;
import com.m.freemovie.mvp.Model.RevivalDetailModel;
import com.m.freemovie.mvp.Model.RevivalSeriesModel;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class RevivalInfoDetailPresenter implements RevivalContractDetail.Presenter {
    private RevivalContractDetail.View view;

    public RevivalInfoDetailPresenter(RevivalContractDetail.View view) {
        this.view = view;
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