package com.m.freemovie.mvp.Api;
import com.m.freemovie.mvp.ClassBean.MovieBean;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface MovieApi {
    @GET("trending/movie/day")
    Call<MovieBean> getMovieList(
            @Query("language") String language,
            @Header("Authorization") String authHeader,
            @Query("page") int page);

    @GET("search/movie")
    Call<MovieBean> getSearchList(
            @Query("language") String language,
            @Header("Authorization") String authHeader,
            @Query("query") String query,
            @Query("page") int page,
            @Query("include_adult") boolean includeAdult);
}