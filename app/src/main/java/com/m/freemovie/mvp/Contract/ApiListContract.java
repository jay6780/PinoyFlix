package com.m.freemovie.mvp.Contract;


import com.m.freemovie.mvp.Model.ClassBean.MovieApiBean;
import com.m.freemovie.mvp.Model.ClassBean.SeriesApiBean;

public interface ApiListContract {
    interface View {
        void showLoading();
        void showError(String error);
        void hideLoading();
        void getMovieApi(MovieApiBean movieApiBean);
        void getSeriesApi(SeriesApiBean seriesApiBean);
    }

    interface Presenter {
        void getMovieApiList();
        void getSeriesApiList();
    }
}
