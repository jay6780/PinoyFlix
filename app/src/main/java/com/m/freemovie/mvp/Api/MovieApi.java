package com.m.freemovie.mvp.Api;
import com.m.freemovie.mvp.ClassBean.MovieBean;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;


public interface MovieApi {

    @GET("latest/{page}")
    Call<MovieBean> getMovieData(@Path("page") String page);
}
