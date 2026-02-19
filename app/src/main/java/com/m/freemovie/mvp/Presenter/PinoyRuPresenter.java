package com.m.freemovie.mvp.Presenter;

import androidx.annotation.NonNull;

import com.m.freemovie.Retrofit.Callback;
import com.m.freemovie.mvp.Contract.PinoyRuMovieContract;
import com.m.freemovie.mvp.Model.ClassBean.PinoyMovieRuBean;
import com.m.freemovie.mvp.Model.PinoyRuModel;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class PinoyRuPresenter implements PinoyRuMovieContract.Presenter {
    private PinoyRuMovieContract.View view;

    public PinoyRuPresenter(PinoyRuMovieContract.View view) {
        this.view = view;
    }

    public void getPage(int page) {
        view.showLoading();

        PinoyRuModel.getRuMovie(page, new Callback<List<PinoyMovieRuBean>>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            }
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void returnResult(List<PinoyMovieRuBean> apiBean) {
                view.hideLoading();
                view.getMovieList(apiBean);
            }

            @Override
            public void returnError(String message) {
                view.hideLoading();
                view.showError(message);
            }
        });
    }
}