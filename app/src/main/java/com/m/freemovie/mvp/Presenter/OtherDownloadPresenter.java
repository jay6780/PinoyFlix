package com.m.freemovie.mvp.Presenter;

import androidx.annotation.NonNull;

import com.m.freemovie.Retrofit.Callback;
import com.m.freemovie.mvp.Contract.OtherDownloadContract;
import com.m.freemovie.mvp.Model.ClassBean.OthersDlBean;
import com.m.freemovie.mvp.Model.OthersModel;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class OtherDownloadPresenter implements OtherDownloadContract.Presenter {
    private OtherDownloadContract.View view;

    public OtherDownloadPresenter(OtherDownloadContract.View view) {
        this.view = view;
    }



    @Override
    public void getLink(String url) {
        view.showLoading();

        OthersModel.getDownloadOther(url, new Callback<OthersDlBean>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            }
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void returnResult(OthersDlBean apiBean) {
                view.hideLoading();
                view.getDownloadSuccess(apiBean);
            }

            @Override
            public void returnError(String message) {
                view.hideLoading();
                view.showError(message);
            }
        });
    }
}