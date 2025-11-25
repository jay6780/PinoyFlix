package com.m.freemovie.mvp.Contract;


import com.m.freemovie.mvp.ClassBean.MovieBean;
import com.m.freemovie.mvp.ClassBean.TvSeriesBean;

public interface MovieAllContract {
    interface View {
        void showLoading();
        void showError(String error);
        void hideLoading();
        void getViewAllResponse(MovieBean movieBean);
        void getTvSeriesResponse(TvSeriesBean tvSeriesBean);
    }

    interface Presenter {
        void getViewAll(String apiKey,int page,int positon);
        void getSeriesAll(String apiKey,int page,int positon);
    }
}
