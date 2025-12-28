package com.m.freemovie.mvp.Presenter;

import androidx.annotation.NonNull;

import com.m.freemovie.Retrofit.Callback;
import com.m.freemovie.mvp.ClassBean.TagalogEpisodeBean;
import com.m.freemovie.mvp.Contract.TagalogEpisodeContract;
import com.m.freemovie.mvp.Model.TagalogEpisodeModel;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class TagalogEpisodePresenter implements TagalogEpisodeContract.Presenter {
    private TagalogEpisodeContract.View  view ;

    public TagalogEpisodePresenter(TagalogEpisodeContract.View view) {
        this.view = view;
    }


    @Override
    public void getUrl(String url) {
        view.showLoading();

        TagalogEpisodeModel.getTagalogEpisode(url,new Callback<TagalogEpisodeBean>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            }
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void returnResult(TagalogEpisodeBean apiBean) {
                view.hideLoading();
                view.getTagalogEpisode(apiBean);
            }

            @Override
            public void returnError(String message) {
                view.hideLoading();
                view.showError(message);
            }
        });
    }
}