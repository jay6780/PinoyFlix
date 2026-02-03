package com.m.freemovie.mvp.Contract;


import com.m.freemovie.mvp.Model.ClassBean.DownloadNineAnimeBean;
import com.m.freemovie.mvp.Model.ClassBean.NineAnimeEpisodeBean;

public interface NineAnimeDetailContract {
    interface View {
        void showLoading();
        void showError(String error);
        void hideLoading();
        void getDetailData(NineAnimeEpisodeBean episodeBean);
        void getVideo(DownloadNineAnimeBean episodeBean);
    }

    interface Presenter {
        void getDetails(String url);
        void getVideoUrl(String url);
    }
}
