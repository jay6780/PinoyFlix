package com.m.freemovie.mvp.Presenter;

import androidx.annotation.NonNull;

import com.m.freemovie.Retrofit.Callback;
import com.m.freemovie.mvp.Contract.PinoyPediaContract;
import com.m.freemovie.mvp.Contract.PinoyRuMovieContract;
import com.m.freemovie.mvp.Model.ClassBean.PinoyMediaDetailBean;
import com.m.freemovie.mvp.Model.ClassBean.PinoyMovieRuBean;
import com.m.freemovie.mvp.Model.PinoyPedialModel;
import com.m.freemovie.mvp.Model.PinoyRuModel;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class PinoyPediaPresenter implements PinoyPediaContract.Presenter {
    private PinoyPediaContract.View view;

    public PinoyPediaPresenter(PinoyPediaContract.View view) {
        this.view = view;
    }

    @Override
    public void getUrl(String url) {
        view.showLoading();

        PinoyPedialModel.getDetailsRu(url, new Callback<PinoyMediaDetailBean>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            }
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void returnResult(PinoyMediaDetailBean apiBean) {
                view.hideLoading();
                view.getDetailSuccess(apiBean);
            }

            @Override
            public void returnError(String message) {
                view.hideLoading();
                view.showError(message);
            }
        });
    }
}