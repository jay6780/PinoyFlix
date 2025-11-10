package com.m.freemovie.mvp.Contract;


import com.m.freemovie.mvp.ClassBean.MovieBean;

import java.util.List;

public interface MovieContract {
    interface View {
        void showLoading();
        void showError(String error);
        void hideLoading();
        void getMovieResponse(MovieBean movieBean);
    }

    interface Presenter {
        void getMovieQuery(String page);
    }
}
