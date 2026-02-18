package com.m.freemovie.mvp.Contract;


import com.m.freemovie.mvp.Model.ClassBean.PinoyMovieRuBean;

import java.util.List;

public interface PinoyRuMovieAllContract {
    interface View {
        void showLoading();
        void showError(String error);
        void hideLoading();
        void getMovieList(List<PinoyMovieRuBean> bean);
        void getActionList(List<PinoyMovieRuBean> bean);
        void getRomanceList(List<PinoyMovieRuBean> bean);
        void getComedyList(List<PinoyMovieRuBean> bean);
    }

    interface Presenter {
        void getPage(int page);
        void getActionPageQuery(String type,int per_page,int page);
        void getRomanceQuery(String type,int per_page,int page);
        void getComedyQuery(String type,int per_page,int page);
    }
}
