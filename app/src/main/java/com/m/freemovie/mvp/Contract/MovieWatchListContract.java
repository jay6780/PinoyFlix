package com.m.freemovie.mvp.Contract;


import com.m.freemovie.mvp.ClassBean.MovieBean;

public interface MovieWatchListContract {
    interface View {
        void showLoading();
        void showError(String error);
        void hideLoading();
        void getViewAllResponse(MovieBean movieBean);
    }

    interface Presenter {
        void getViewAll(String apiKey,int page,int positon);
    }
}
