package com.m.freemovie.mvp.Presenter;

import com.m.freemovie.mvp.ClassBean.MovieBean;
import com.m.freemovie.mvp.Contract.MovieContract;
import com.m.freemovie.mvp.Model.MovieModel;

public class MoviePresenter implements MovieContract.Presenter {
    private MovieContract.View view;
    private MovieModel model;

    public MoviePresenter(MovieContract.View view) {
        this.view = view;
        this.model = new MovieModel();
    }

    @Override
    public void getPopularMovie(String apiKey, int page) {
        view.showLoading();
        model.getPopular(apiKey,page, new MovieModel.VideoMovieListerner() {
            @Override
            public void onSuccess(MovieBean movieBean) {
                view.hideLoading();
                view.getPopularResponse(movieBean);
            }

            @Override
            public void onError(String error) {
                view.hideLoading();
                view.showError(error);
            }
        });
    }

    @Override
    public void getTopRated(String apiKey, int page) {
        view.showLoading();
        model.getTopRated(apiKey,page, new MovieModel.VideoMovieListerner() {
            @Override
            public void onSuccess(MovieBean movieBean) {
                view.hideLoading();
                view.getTopRatedResponse(movieBean);
            }

            @Override
            public void onError(String error) {
                view.hideLoading();
                view.showError(error);
            }
        });
    }

    @Override
    public void getUpcoming(String apiKey, int page) {
        view.showLoading();
        model.getUpcoming(apiKey,page, new MovieModel.VideoMovieListerner() {
            @Override
            public void onSuccess(MovieBean movieBean) {
                view.hideLoading();
                view.getUpcomingResponse(movieBean);
            }

            @Override
            public void onError(String error) {
                view.hideLoading();
                view.showError(error);
            }
        });

    }

    @Override
    public void getNow(String apiKey, int page) {
        view.showLoading();
        model.getNow(apiKey,page, new MovieModel.VideoMovieListerner() {
            @Override
            public void onSuccess(MovieBean movieBean) {
                view.hideLoading();
                view.getNowResponse(movieBean);
            }

            @Override
            public void onError(String error) {
                view.hideLoading();
                view.showError(error);
            }
        });
    }
}