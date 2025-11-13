package com.m.freemovie.mvp.Presenter;

import com.m.freemovie.mvp.ClassBean.MovieBean;
import com.m.freemovie.mvp.Contract.MovieAllContract;
import com.m.freemovie.mvp.Model.ViewAllModel;

public class ViewAllPresenter implements MovieAllContract.Presenter {
    private MovieAllContract.View view;
    private ViewAllModel model;

    public ViewAllPresenter(MovieAllContract.View view) {
        this.view = view;
        this.model = new ViewAllModel();
    }

    @Override
    public void getViewAll(String apiKey, int page,int positon) {
        view.showLoading();
        model.getListResponse(apiKey,page,positon, new ViewAllModel.VideoMovieListerner() {
            @Override
            public void onSuccess(MovieBean movieBean) {
                view.hideLoading();
                view.getViewAllResponse(movieBean);
            }

            @Override
            public void onError(String error) {
                view.hideLoading();
                view.showError(error);
            }
        });
    }
}