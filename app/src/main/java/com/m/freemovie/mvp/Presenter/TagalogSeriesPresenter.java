package com.m.freemovie.mvp.Presenter;

import androidx.annotation.NonNull;

import com.m.freemovie.Retrofit.Callback;
import com.m.freemovie.mvp.ClassBean.TagalogBean;
import com.m.freemovie.mvp.Contract.TagalogSeriesContract;
import com.m.freemovie.mvp.Model.TagalogSeriesModel;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class TagalogSeriesPresenter implements TagalogSeriesContract.Presenter {
    private TagalogSeriesContract.View view;

    public TagalogSeriesPresenter(TagalogSeriesContract.View view) {
        this.view = view;
    }

    @Override
    public void startSeries() {
        view.showLoading();

        TagalogSeriesModel.getTagalogSeries(new Callback<TagalogBean>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            }
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void returnResult(TagalogBean apiBean) {
                view.hideLoading();
                view.getTagalogSeries(apiBean);
            }

            @Override
            public void returnError(String message) {
                view.hideLoading();
                view.showError(message);
            }
        });
    }
}