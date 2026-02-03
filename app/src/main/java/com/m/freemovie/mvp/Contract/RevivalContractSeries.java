package com.m.freemovie.mvp.Contract;


import com.m.freemovie.mvp.Model.ClassBean.RevivalSeriesBean;

public interface RevivalContractSeries {
    interface View {
        void showLoading();
        void showError(String error);
        void hideLoading();
        void getTvSeries(RevivalSeriesBean revivalSeriesBean);
    }

    interface Presenter {
        void getListTv(int page);
    }
}
