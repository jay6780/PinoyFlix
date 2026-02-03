package com.m.freemovie.mvp.Contract;


import com.m.freemovie.mvp.Model.ClassBean.PaheLatestBean;

public interface AnimePaheContract {
    interface View {
        void showLoading();
        void showError(String error);
        void hideLoading();
        void getLatestData(PaheLatestBean paheLatestBean);
    }

    interface Presenter {
        void getLatest(int page);
    }
}
