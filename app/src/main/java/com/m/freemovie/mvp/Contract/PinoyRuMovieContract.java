package com.m.freemovie.mvp.Contract;


import com.m.freemovie.mvp.Model.ClassBean.PinoyMovieRuBean;

import java.util.List;

public interface PinoyRuMovieContract {
    interface View {
        void showLoading();
        void showError(String error);
        void hideLoading();
        void getMovieList(List<PinoyMovieRuBean> bean);
    }

    interface Presenter {
        void getPage(int page);
    }
}
