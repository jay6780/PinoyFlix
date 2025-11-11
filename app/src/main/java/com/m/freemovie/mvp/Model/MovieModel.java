package com.m.freemovie.mvp.Model;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.m.freemovie.mvp.Api.MovieApi;
import com.m.freemovie.mvp.ClassBean.MovieBean;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieModel {
    private static final String BASE_URL = "https://api.themoviedb.org/3/";

    private MovieApi api;

    public MovieModel() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(MovieApi.class);
    }

    public void getLatestMovie(String apiKey, int page,final VideoMovieListerner listener) {
        String authHeader = "Bearer " + apiKey;
        api.getMovieList("en-US",authHeader,page).enqueue(new Callback<MovieBean>() {
            @Override
            public void onResponse(Call<MovieBean> call, Response<MovieBean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MovieBean movieBean = response.body();
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
//                    String prettyJson = gson.toJson(movieBean);
//                    Log.d("ResponseBody", prettyJson);
                    listener.onSuccess(movieBean);
                } else {
                    listener.onError("Failed to load videos");
                }
            }

            @Override
            public void onFailure(Call<MovieBean> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        });
    }

    public void getSearch(String apiKey, String query, int page, final VideoMovieListerner listener) {
        String authHeader = "Bearer " + apiKey;
        api.getSearchList("en-US", authHeader, query, page, false).enqueue(new Callback<MovieBean>() {
            @Override
            public void onResponse(Call<MovieBean> call, Response<MovieBean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MovieBean movieBean = response.body();
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
//                    String prettyJson = gson.toJson(movieBean);
//                    Log.d("ResponseBody", prettyJson);
                    listener.onSuccess(movieBean);
                } else {
                    listener.onError("Failed to load videos - HTTP " + response.code());
                }
            }
            @Override
            public void onFailure(Call<MovieBean> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        });
    }

    public interface VideoMovieListerner {
        void onSuccess(MovieBean movieBean);

        void onError(String error);
    }
}
