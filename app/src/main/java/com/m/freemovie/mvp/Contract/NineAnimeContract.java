package com.m.freemovie.mvp.Contract;


import com.m.freemovie.mvp.Model.ClassBean.NineAnimeBean;

public interface NineAnimeContract {
    interface View {
        void showLoading();
        void showError(String error);
        void hideLoading();
        void getOngoing(NineAnimeBean nineAnimeBean);
        void getLatest(NineAnimeBean nineAnimeBean);
    }

    interface Presenter {
        void getPageOngoing(int page);
        void getPageLatest(int page);
    }
}
