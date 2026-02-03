package com.m.freemovie.mvp.Presenter;

import androidx.annotation.NonNull;

import com.m.freemovie.Retrofit.Callback;
import com.m.freemovie.mvp.Model.ClassBean.RevivalSearchBean;
import com.m.freemovie.mvp.Contract.RevivalSearchContract;
import com.m.freemovie.mvp.Model.RevivalSearchModel;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class RevivalSearchPresenter implements RevivalSearchContract.Presenter {
    private RevivalSearchContract.View view;

    public RevivalSearchPresenter(RevivalSearchContract.View view) {
        this.view = view;
    }


    @Override
    public void getSearchRevival(String search) {
        view.showLoading();

        RevivalSearchModel.getSearchRevival(search, new Callback<RevivalSearchBean>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            }
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void returnResult(RevivalSearchBean apiBean) {
                view.hideLoading();
                view.getSearchRevival(apiBean);
            }

            @Override
            public void returnError(String message) {
                view.hideLoading();
                view.showError(message);
            }
        });
    }
}