package com.m.freemovie.mvp.Model;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.m.freemovie.mvp.Api.MovieApi;
import com.m.freemovie.mvp.ClassBean.DetailBean;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailModel {
    private static final String BASE_URL = "https://api.themoviedb.org/3/";

    private MovieApi api;

    public DetailModel() {
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
    public void getDetailData(String id,String apiKey,final DetailListener listener) {
        String authHeader = "Bearer " + apiKey;
        api.getDetails(id,"en-US",authHeader).enqueue(new Callback<DetailBean>() {
            @Override
            public void onResponse(Call<DetailBean> call, Response<DetailBean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DetailBean movieBean = response.body();
//                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
//                    String prettyJson = gson.toJson(movieBean);
//                    Log.d("DetailResponse", prettyJson);
                    listener.onSuccess(movieBean);
                } else {
                    listener.onError("Failed to load videos");
                }
            }

            @Override
            public void onFailure(Call<DetailBean> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        });
    }

    public interface DetailListener {
        void onSuccess(DetailBean movieBean);

        void onError(String error);
    }
}
