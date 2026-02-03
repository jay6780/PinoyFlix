package com.m.freemovie.mvp.Contract;


import com.m.freemovie.mvp.Model.ClassBean.TagalogInfoBean;

public interface RevivalContractDetail {
    interface View {
        void showLoading();
        void showError(String error);
        void hideLoading();
        void getInfoTagalog(TagalogInfoBean tagalogInfoBean);
    }

    interface Presenter {
        void getListTv(String Url);
    }
}
