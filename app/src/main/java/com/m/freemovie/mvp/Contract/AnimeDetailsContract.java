package com.m.freemovie.mvp.Contract;


import com.m.freemovie.mvp.Model.ClassBean.AniNeKoInfoBean;
import com.m.freemovie.mvp.Model.ClassBean.AnimePaheDetailBean;
import com.m.freemovie.mvp.Model.ClassBean.TagalogEpisodeBean;
import com.m.freemovie.mvp.Model.ClassBean.TagalogInfoBean;
import com.m.freemovie.mvp.Model.ClassBean.ZoRoDetailBean;

public interface AnimeDetailsContract {
    interface View {
        void showLoading();
        void showError(String error);
        void hideLoading();
        void getDetailData(AnimePaheDetailBean detailBean);
        void getTagalogEpisode(TagalogEpisodeBean tagalogEpisodeBean);
        void getInfoTagalog(TagalogInfoBean tagalogInfoBean);
        void getZoroDetail(ZoRoDetailBean zoRoDetailBean);
        void getAniNekoDetail(AniNeKoInfoBean aniNeKoInfoBean);
    }

    interface Presenter {
        void getDetailAnimePaHe(String url);
        void getUrl(String url);
        void getListTv(String Url);
        void getZoroUrl(String Url);
        void getAniNekoUrl(String Url);
    }
}
