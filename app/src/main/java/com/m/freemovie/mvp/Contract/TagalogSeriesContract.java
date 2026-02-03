package com.m.freemovie.mvp.Contract;


import com.m.freemovie.mvp.Model.ClassBean.TagalogBean;

public interface TagalogSeriesContract {
    interface View {
        void showLoading();
        void showError(String error);
        void hideLoading();
        void getTagalogSeries(TagalogBean tagalogBean);
    }

    interface Presenter {
        void startSeries();
    }
}
