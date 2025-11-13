package com.m.freemovie.mvp.Contract;


import com.m.freemovie.mvp.ClassBean.MovieBean;

public interface SearchContract {
    interface View {
        void showLoading();
        void showError(String error);
        void hideLoading();
        void getSearchResponse(MovieBean movieBean);
    }

    interface Presenter {
        void getSearchQuery(String apiKey,String query,int page);
    }
}
