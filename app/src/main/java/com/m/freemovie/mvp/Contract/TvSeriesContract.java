package com.m.freemovie.mvp.Contract;


import com.m.freemovie.mvp.ClassBean.MovieBean;
import com.m.freemovie.mvp.ClassBean.TvSeriesBean;

public interface TvSeriesContract {
    interface View {
        void showLoading();
        void showError(String error);
        void hideLoading();
        void getTodayTvResponse(TvSeriesBean tvSeriesBean);
        void getOnAiringTvResponse(TvSeriesBean tvSeriesBean);
        void getPopularTvResponse(TvSeriesBean tvSeriesBean);
        void getTopRatedTvResponse(TvSeriesBean tvSeriesBean);
    }

    interface Presenter {
        void getTodayTv(String apiKey,int page);
        void getOnAiringTv(String apiKey,int page);
        void getPopularTv(String apiKey,int page);
        void getTopRatedTv(String apiKey,int page);
    }
}
