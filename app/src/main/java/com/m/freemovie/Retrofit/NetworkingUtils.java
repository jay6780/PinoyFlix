package com.m.freemovie.Retrofit;


import com.m.freemovie.mvp.Api.MovieApi;

public class NetworkingUtils {

    private static MovieApi apiService;


    public static MovieApi getMovieData() {
        if (apiService == null)
            apiService = FreeMovieRetrofitAdapter.getInstance().create(MovieApi.class);

        return apiService;
    }

}