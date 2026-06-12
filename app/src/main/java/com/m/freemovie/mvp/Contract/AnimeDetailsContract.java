package com.m.freemovie.mvp.Contract;


import com.m.freemovie.mvp.Model.ClassBean.AnimePaheDetailBean;
import com.m.freemovie.mvp.Model.ClassBean.MiRuRoDetailBean;
import com.m.freemovie.mvp.Model.ClassBean.TagalogEpisodeBean;
import com.m.freemovie.mvp.Model.ClassBean.TagalogInfoBean;

public interface AnimeDetailsContract {
    interface View {
        void showLoading();
        void showError(String error);
        void hideLoading();
        void getDetailData(AnimePaheDetailBean detailBean);
        void getTagalogEpisode(TagalogEpisodeBean tagalogEpisodeBean);
        void getInfoTagalog(TagalogInfoBean tagalogInfoBean);
        void getMiRuRoDetail(MiRuRoDetailBean miRuRoDetailBean);
    }

    interface Presenter {
        void getDetailAnimePaHe(String url);
        void getUrl(String url);
        void getListTv(String Url);
        void getMiRuRoData(String Url);
    }
}
