package com.m.freemovie.mvp.Contract;


import com.m.freemovie.mvp.Model.ClassBean.PinoyMediaDetailBean;
import com.m.freemovie.mvp.Model.ClassBean.PinoyMovieRuBean;

import java.util.List;

public interface PinoyPediaContract {
    interface View {
        void showLoading();
        void showError(String error);
        void hideLoading();
        void getDetailSuccess(PinoyMediaDetailBean bean);
    }

    interface Presenter {
        void getUrl(String url);
    }
}
