package com.m.freemovie.mvp.Contract;


import com.m.freemovie.mvp.ClassBean.MovieBean;
import com.m.freemovie.mvp.ClassBean.NineAnimeSearchBean;
import com.m.freemovie.mvp.ClassBean.TagalogSearchBean;

public interface SearchContract {
    interface View {
        void showLoading();
        void showError(String error);
        void hideLoading();
        void getSearchResponse(MovieBean movieBean);
        void getSearchSeriesResponse(MovieBean movieBean);
        void getTagalogSearch(TagalogSearchBean tagalogSearchBean);
        void getNineAnime(NineAnimeSearchBean nineAnimeSearchBean);
    }

    interface Presenter {
        void getSearchQuery(String apiKey,String query,int page);
        void getSearchSeries(String apiKey,String query,int page);
        void getTagalogQuery(String query);
        void getNineAnimeQuery(String query);
    }
}
