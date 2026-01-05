package com.m.freemovie.mvp.Contract;


import com.m.freemovie.mvp.ClassBean.AnimePaheDetailBean;
import com.m.freemovie.mvp.ClassBean.AnimePaheDownloadBean;
import com.m.freemovie.mvp.ClassBean.AnimePaheEpisodeBean;

public interface AnimePaheDetailContract {
    interface View {
        void showLoading();
        void showError(String error);
        void hideLoading();
        void getDetailData(AnimePaheDetailBean detailBean);
        void getEpisodes(AnimePaheEpisodeBean episodeBean);
        void getTrack(AnimePaheDownloadBean downloadBean);
    }

    interface Presenter {
        void getDetailQuery(String url);
        void getEpisodeQuery(String id,int page);
        void getTrackQuery(String url);
    }
}
