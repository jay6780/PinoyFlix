package com.m.freemovie.mvp.Contract;


import com.m.freemovie.mvp.Model.ClassBean.RevivalSeriesBean;

public interface RevivalContractMovies {
    interface View {
        void showLoading();
        void showError(String error);
        void hideLoading();
        void getMovies(RevivalSeriesBean revivalSeriesBean);
    }

    interface Presenter {
        void getMovieList(int page);
    }
}
