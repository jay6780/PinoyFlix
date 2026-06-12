package com.m.freemovie.mvp.Contract;


import com.m.freemovie.mvp.Model.ClassBean.AnimoPageBean;
import com.m.freemovie.mvp.Model.ClassBean.MiRuRoHomeBean;
import com.m.freemovie.mvp.Model.ClassBean.PaheLatestBean;
import com.m.freemovie.mvp.Model.ClassBean.RevivalSeriesBean;
import com.m.freemovie.mvp.Model.ClassBean.TagalogBean;

public interface AnimeContract {
    interface View {
        void showLoading();
        void showError(String error);
        void hideLoading();
        void getNewest(PaheLatestBean paheLatestBean);
        void getHot(AnimoPageBean animoPageBean);
        void getPopular(RevivalSeriesBean revivalSeriesBean);
        void getMovie(RevivalSeriesBean revivalSeriesBean);
        void getMiRuRoHome(MiRuRoHomeBean miRuRoHomeBean);
    }

    interface Presenter {
        void getNewestPage(int page);
        void getHotPage(int page);
        void getPopular(int page);
        void getMovie(int page);
        void getMiRuRoHomeData();
    }
}
