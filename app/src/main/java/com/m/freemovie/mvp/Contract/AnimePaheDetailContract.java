package com.m.freemovie.mvp.Contract;


import com.m.freemovie.mvp.Model.ClassBean.AniKoToWatchBean;
import com.m.freemovie.mvp.Model.ClassBean.ZoRoDetailBean;
import com.m.freemovie.mvp.Model.ClassBean.ZoRoVideoUrlBean;

public interface AnimePaheDetailContract {
    interface View {
        void showLoading();
        void showError(String error);
        void hideLoading();
        void getZoroDetail(ZoRoDetailBean zoRoDetailBean);
        void getZoRoVideo(ZoRoVideoUrlBean zoRoVideoUrlBean);
        void getAniKoToEpisode(AniKoToWatchBean aniKoToWatchBean);
    }

    interface Presenter {
        void getZoroUrl(String Url);
        void getZoRoVideoUrl(String Url);
        void getAniKoToID(String id);
    }
}
