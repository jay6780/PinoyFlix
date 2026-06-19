package com.m.freemovie.mvp.Contract;


import com.m.freemovie.mvp.Model.ClassBean.AniKoToPageBean;
import com.m.freemovie.mvp.Model.ClassBean.AniNekoBean;
import com.m.freemovie.mvp.Model.ClassBean.AnimoPageBean;
import com.m.freemovie.mvp.Model.ClassBean.PaheLatestBean;
import com.m.freemovie.mvp.Model.ClassBean.RevivalSeriesBean;
import com.m.freemovie.mvp.Model.ClassBean.TagalogBean;
import com.m.freemovie.mvp.Model.ClassBean.ZoRoPageBean;

import java.util.List;

public interface AnimeContract {
    interface View {
        void showLoading();
        void showError(String error);
        void hideLoading();
        void getNewest(PaheLatestBean paheLatestBean);
        void getHot(AnimoPageBean animoPageBean);
        void getPopular(RevivalSeriesBean revivalSeriesBean);
        void getMovie(RevivalSeriesBean revivalSeriesBean);
        void getZoRo(List<ZoRoPageBean> zoRoPageBean);
        void getAniKoTo(AniKoToPageBean aniKoToPageBean);
        void getAniNeKo(AniNekoBean aniNekoBean);
    }

    interface Presenter {
        void getNewestPage(int page);
        void getHotPage(int page);
        void getPopular(int page);
        void getMovie(int page);
        void getZoRoPage(int page);
        void getAniKoToPage(int page);
        void getAniNekoPage(int page);
    }
}
