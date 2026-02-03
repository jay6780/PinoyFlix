package com.m.freemovie.mvp.Contract;


import com.m.freemovie.mvp.Model.ClassBean.RevivalSearchBean;

public interface RevivalSearchContract {
    interface View {
        void showLoading();
        void showError(String error);
        void hideLoading();
        void getSearchRevival(RevivalSearchBean revivalSearchBean);
    }

    interface Presenter {
        void getSearchRevival(String search);
    }
}
