package com.m.freemovie.mvp.Contract;


import com.m.freemovie.mvp.Model.ClassBean.AniKoToSearchBean;
import com.m.freemovie.mvp.Model.ClassBean.AniMoTvSearchBean;
import com.m.freemovie.mvp.Model.ClassBean.AniNekoSearchBean;
import com.m.freemovie.mvp.Model.ClassBean.AnimePaheSearchBean;
import com.m.freemovie.mvp.Model.ClassBean.MovieBean;
import com.m.freemovie.mvp.Model.ClassBean.NineAnimeSearchBean;
import com.m.freemovie.mvp.Model.ClassBean.SearchRuBean;
import com.m.freemovie.mvp.Model.ClassBean.TagalogSearchBean;
import com.m.freemovie.mvp.Model.ClassBean.ZoRoSearchBean;

import java.util.List;

public interface SearchContract {
    interface View {
        void showLoading();
        void showError(String error);
        void hideLoading();
        void getSearchResponse(MovieBean movieBean);
        void getSearchSeriesResponse(MovieBean movieBean);
        void getTagalogSearch(TagalogSearchBean tagalogSearchBean);
        void getNineAnime(NineAnimeSearchBean nineAnimeSearchBean);
        void getSearchPahe(AnimePaheSearchBean animePaheSearchBean);
        void getSearchList(List<SearchRuBean> bean);
        void getZoRoSearch(ZoRoSearchBean zoRoSearchBean);
        void getAniKoToSearch(AniKoToSearchBean aniKoToSearchBean);
        void getAniNeKoSearchData(AniNekoSearchBean aniNekoSearchBean);
        void getAniMoTvSearch(AniMoTvSearchBean aniMoTvSearchBean);
    }

    interface Presenter {
        void getSearchQuery(String apiKey,String query,int page);
        void getSearchSeries(String apiKey,String query,int page);
        void getTagalogQuery(String query);
        void getNineAnimeQuery(String query);
        void getAnimePaheQuery(String search);
        void getTagalogMovieQuery(String query);
        void getZoRoQuery(String search);
        void getAniKoToQuery(String keyword);
        void getAniNeKoQuery(String q);
        void getAniMoTvQuery(String search);
    }
}
