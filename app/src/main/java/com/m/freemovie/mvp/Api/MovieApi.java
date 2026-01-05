package com.m.freemovie.mvp.Api;

import com.m.freemovie.mvp.ClassBean.AnimePaheDetailBean;
import com.m.freemovie.mvp.ClassBean.AnimePaheDownloadBean;
import com.m.freemovie.mvp.ClassBean.AnimePaheEpisodeBean;
import com.m.freemovie.mvp.ClassBean.AnimePaheSearchBean;
import com.m.freemovie.mvp.ClassBean.DetailBean;
import com.m.freemovie.mvp.ClassBean.DetailDownloadBean;
import com.m.freemovie.mvp.ClassBean.DetailTvBean;
import com.m.freemovie.mvp.ClassBean.DownloadNineAnimeBean;
import com.m.freemovie.mvp.ClassBean.MovieBean;
import com.m.freemovie.mvp.ClassBean.NineAnimeBean;
import com.m.freemovie.mvp.ClassBean.NineAnimeEpisodeBean;
import com.m.freemovie.mvp.ClassBean.NineAnimeSearchBean;
import com.m.freemovie.mvp.ClassBean.PaheLatestBean;
import com.m.freemovie.mvp.ClassBean.RevivalSearchBean;
import com.m.freemovie.mvp.ClassBean.RevivalSeriesBean;
import com.m.freemovie.mvp.ClassBean.TagalogBean;
import com.m.freemovie.mvp.ClassBean.TagalogEpisodeBean;
import com.m.freemovie.mvp.ClassBean.TagalogInfoBean;
import com.m.freemovie.mvp.ClassBean.TagalogSearchBean;
import com.m.freemovie.mvp.ClassBean.TvSeriesBean;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieApi {
    @GET("movie/popular")
    Observable<MovieBean> getPopularList(
            @Query("language") String language,
            @Header("Authorization") String authHeader,
            @Query("page") int page);

    @GET("movie/top_rated")
    Observable<MovieBean> getTopRated(
            @Query("language") String language,
            @Header("Authorization") String authHeader,
            @Query("page") int page);


    @GET("movie/upcoming")
    Observable<MovieBean> getUpcoming(
            @Query("language") String language,
            @Header("Authorization") String authHeader,
            @Query("page") int page);



    @GET("movie/now_playing")
    Observable<MovieBean> getNow(
            @Query("language") String language,
            @Header("Authorization") String authHeader,
            @Query("page") int page);


    //tv series
    @GET("tv/airing_today")
    Observable<TvSeriesBean> getTodayTv(
            @Query("language") String language,
            @Header("Authorization") String authHeader,
            @Query("page") int page);

    @GET("tv/on_the_air")
    Observable<TvSeriesBean> getOnAiringTv(
            @Query("language") String language,
            @Header("Authorization") String authHeader,
            @Query("page") int page);


    @GET("tv/popular")
    Observable<TvSeriesBean> getPopularTv(
            @Query("language") String language,
            @Header("Authorization") String authHeader,
            @Query("page") int page);



    @GET("tv/top_rated")
    Observable<TvSeriesBean> getTopRatedTv(
            @Query("language") String language,
            @Header("Authorization") String authHeader,
            @Query("page") int page);




    @GET("tv/{series_id}")
    Observable<DetailTvBean> geTvDetails(
            @Path("series_id") String id,
            @Query("language") String language,
            @Header("Authorization") String authHeader);


    @GET("movie/{id}")
    Observable<DetailBean> getDetails(
            @Path("id") String id,
            @Query("language") String language,
            @Header("Authorization") String authHeader);


    @GET("search/movie")
    Observable<MovieBean> getSearchList(
            @Query("language") String language,
            @Header("Authorization") String authHeader,
            @Query("query") String query,
            @Query("page") int page,
            @Query("include_adult") boolean includeAdult);

    @GET("search/tv")
    Observable<MovieBean> getSearchSeries(
            @Query("language") String language,
            @Header("Authorization") String authHeader,
            @Query("query") String query,
            @Query("page") int page,
            @Query("include_adult") boolean includeAdult);


    // tagalog dub
    @GET("taganime/series")
    Observable<TagalogBean> getTagalogSeries();

    @GET("taganime/video")
    Observable<TagalogEpisodeBean> getTagalogEpisode(
            @Query("url") String url);

    @GET("taganime")
    Observable<TagalogSearchBean> getSearch(
            @Query("search") String search);


    //revival
    @GET("animerevival/tvshows")
    Observable<RevivalSeriesBean> getRevivalSeries(
            @Query("page") int page);

    @GET("animerevival/movies")
    Observable<RevivalSeriesBean> getRevivalMovies(
            @Query("page") int page);

    @GET("animerevival/info")
    Observable<TagalogInfoBean> getRevivalInfo(
            @Query("url") String url);


    @GET("animerevival/download")
    Observable<DetailDownloadBean> getVideoTrack(
            @Query("url") String url);


    @GET("animerevival")
    Observable<RevivalSearchBean> getSearchRevival(
            @Query("search") String search);


    //9anime
    @GET("9anime/series/ongoing")
    Observable<NineAnimeBean> getNineOngoing(
            @Query("page") int page);

    @GET("9anime/series/latest")
    Observable<NineAnimeBean> getNineLatest(
            @Query("page") int page);

    @GET("9anime/info")
    Observable<NineAnimeEpisodeBean> getNineDetails(
            @Query("url") String url);

    @GET("9anime/download")
    Observable<DownloadNineAnimeBean> getNineEpisodeVideo(
            @Query("url") String url);

    @GET("9anime")
    Observable<NineAnimeSearchBean> getSearchNineAnime(
            @Query("search") String search);


    //animepahe
    @GET("animepahe/latest_release")
    Observable<PaheLatestBean> getLatestAnimePahe(
            @Query("page") int page);

    @GET("animepahe/info")
    Observable<AnimePaheDetailBean> getInfoPahe(
            @Query("url") String url);

    @GET("animepahe/episodes")
    Observable<AnimePaheEpisodeBean> getPaheEpisode(
            @Query("id") String id,
            @Query("page") int page);


    @GET("animepahe/download")
    Observable<AnimePaheDownloadBean> getPaheTrack(
            @Query("url") String url);

    @GET("animepahe/api")
    Observable<AnimePaheSearchBean> getPaheSearch(
            @Query("search") String search);



}