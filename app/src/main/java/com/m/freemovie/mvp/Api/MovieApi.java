package com.m.freemovie.mvp.Api;

import com.m.freemovie.mvp.ClassBean.DetailBean;
import com.m.freemovie.mvp.ClassBean.DetailTvBean;
import com.m.freemovie.mvp.ClassBean.MovieBean;
import com.m.freemovie.mvp.ClassBean.TagalogBean;
import com.m.freemovie.mvp.ClassBean.TagalogEpisodeBean;
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

}