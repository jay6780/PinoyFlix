package com.m.freemovie.mvp.Contract;


import com.m.freemovie.mvp.Model.ClassBean.PaheLatestBean;
import com.m.freemovie.mvp.Model.ClassBean.RevivalSeriesBean;
import com.m.freemovie.mvp.Model.ClassBean.TagalogBean;

public interface AnimeContract {
    interface View {
        void showLoading();
        void showError(String error);
        void hideLoading();
        void getNewest(PaheLatestBean paheLatestBean);
        void getHot(TagalogBean tagalogBean);
        void getPopular(RevivalSeriesBean revivalSeriesBean);
        void getMovie(RevivalSeriesBean revivalSeriesBean);
    }

    interface Presenter {
        void getNewestPage(int page);
        void getHotPage();
        void getPopular(int page);
        void getMovie(int page);
    }
}
