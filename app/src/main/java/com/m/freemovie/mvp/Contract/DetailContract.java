package com.m.freemovie.mvp.Contract;


import com.m.freemovie.mvp.Model.ClassBean.DetailBean;
import com.m.freemovie.mvp.Model.ClassBean.DetailTvBean;

public interface DetailContract {
    interface View {
        void showLoading();
        void showError(String error);
        void hideLoading();
        void getDetailResponse(DetailBean movieBean);
        void getTvDetailResponse(DetailTvBean detailTvBean);
    }

    interface Presenter {
        void getDetail(String id, String apiKey);
        void getTvDetail(String id, String apiKey);
    }
}
