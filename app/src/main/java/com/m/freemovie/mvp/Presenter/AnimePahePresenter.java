package com.m.freemovie.mvp.Presenter;

import androidx.annotation.NonNull;

import com.m.freemovie.Retrofit.Callback;
import com.m.freemovie.mvp.Model.ClassBean.PaheLatestBean;
import com.m.freemovie.mvp.Contract.AnimePaheContract;
import com.m.freemovie.mvp.Model.AnimePaheModel;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class AnimePahePresenter implements AnimePaheContract.Presenter {
    private AnimePaheContract.View view;

    public AnimePahePresenter(AnimePaheContract.View view) {
        this.view = view;
    }


    @Override
    public void getLatest(int page) {
        view.showLoading();

        AnimePaheModel.getLatest(page, new Callback<PaheLatestBean>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            }
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void returnResult(PaheLatestBean apiBean) {
                view.hideLoading();
                view.getLatestData(apiBean);
            }

            @Override
            public void returnError(String message) {
                view.hideLoading();
                view.showError(message);
            }
        });
    }
}