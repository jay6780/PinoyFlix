package com.m.freemovie.mvp.Contract;


import com.m.freemovie.mvp.Model.ClassBean.OthersDlBean;

public interface OtherDownloadContract {
    interface View {
        void showLoading();
        void showError(String error);
        void hideLoading();
        void getDownloadSuccess(OthersDlBean othersDlBean);
    }

    interface Presenter {
        void getLink(String url);
    }
}
