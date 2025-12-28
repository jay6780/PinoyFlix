package com.m.freemovie.Retrofit;


import com.m.freemovie.mvp.Api.MovieApi;

public class NetworkingUtils {

    private static MovieApi apiService,apService2;


    public static MovieApi getMovieData() {
        if (apiService == null)
            apiService = FreeMovieRetrofitAdapter.getInstance().create(MovieApi.class);

        return apiService;
    }

    public static MovieApi getTagalogDub() {
        if (apService2 == null)
            apService2 = Tagalogdubretrofitadapter.getInstance().create(MovieApi.class);

        return apService2;
    }

}