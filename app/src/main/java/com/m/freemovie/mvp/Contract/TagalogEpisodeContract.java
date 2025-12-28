package com.m.freemovie.mvp.Contract;


import com.m.freemovie.mvp.ClassBean.TagalogEpisodeBean;

public interface TagalogEpisodeContract {
    interface View {
        void showLoading();
        void showError(String error);
        void hideLoading();
        void getTagalogEpisode(TagalogEpisodeBean tagalogEpisodeBean);
    }

    interface Presenter {
        void getUrl(String url);
    }
}
