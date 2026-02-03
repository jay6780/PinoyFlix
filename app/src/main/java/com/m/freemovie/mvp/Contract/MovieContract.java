package com.m.freemovie.mvp.Contract;


import com.m.freemovie.mvp.Model.ClassBean.MovieBean;

public interface MovieContract {
    interface View {
        void showLoading();
        void showError(String error);
        void hideLoading();
        void getPopularResponse(MovieBean movieBean);
        void getTopRatedResponse(MovieBean movieBean);
        void getUpcomingResponse(MovieBean movieBean);
        void getNowResponse(MovieBean movieBean);
    }

    interface Presenter {
        void getPopularMovie(String apiKey,int page);
        void getTopRated(String apiKey,int page);
        void getUpcoming(String apiKey,int page);
        void getNow(String apiKey,int page);
    }
}
