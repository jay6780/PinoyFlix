package com.m.freemovie.mvp.Contract;


import com.m.freemovie.mvp.Model.ClassBean.DetailDownloadBean;

public interface RevivalContractTrack {
    interface View {
        void showLoading();
        void showError(String error);
        void hideLoading();
        void getTrack(DetailDownloadBean tagalogInfoBean);
    }

    interface Presenter {
        void getTrackUrl(String Url);
    }
}
