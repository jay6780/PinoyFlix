package com.m.freemovie.mvp.Contract;


import com.m.freemovie.mvp.Model.ClassBean.MovieBean;
import com.m.freemovie.mvp.Model.ClassBean.TvSeriesBean;

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
