package com.m.freemovie.mvp.Api;

import com.m.freemovie.mvp.ClassBean.DetailBean;
import com.m.freemovie.mvp.ClassBean.MovieBean;

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
}