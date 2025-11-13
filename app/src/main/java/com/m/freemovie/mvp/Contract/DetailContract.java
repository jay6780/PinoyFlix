package com.m.freemovie.mvp.Contract;


import com.m.freemovie.mvp.ClassBean.DetailBean;

public interface DetailContract {
    interface View {
        void showLoading();
        void showError(String error);
        void hideLoading();
        void getDetailResponse(DetailBean movieBean);
    }

    interface Presenter {
        void getDetail(String id, String apiKey);
    }
}
