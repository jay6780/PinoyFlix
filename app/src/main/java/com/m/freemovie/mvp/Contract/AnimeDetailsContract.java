package com.m.freemovie.mvp.Contract;


import com.m.freemovie.mvp.ClassBean.AnimePaheDetailBean;
import com.m.freemovie.mvp.ClassBean.TagalogEpisodeBean;
import com.m.freemovie.mvp.ClassBean.TagalogInfoBean;

public interface AnimeDetailsContract {
    interface View {
        void showLoading();
        void showError(String error);
        void hideLoading();
        void getDetailData(AnimePaheDetailBean detailBean);
        void getTagalogEpisode(TagalogEpisodeBean tagalogEpisodeBean);
        void getInfoTagalog(TagalogInfoBean tagalogInfoBean);
    }

    interface Presenter {
        void getDetailAnimePaHe(String url);
        void getUrl(String url);
        void getListTv(String Url);
    }
}
