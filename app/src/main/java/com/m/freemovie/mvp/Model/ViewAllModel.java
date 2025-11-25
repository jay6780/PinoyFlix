package com.m.freemovie.mvp.Model;

import android.util.Log;

import com.m.freemovie.Retrofit.NetworkingUtils;
import com.m.freemovie.mvp.ClassBean.MovieBean;
import com.m.freemovie.Retrofit.Callback;
import com.m.freemovie.mvp.ClassBean.TvSeriesBean;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
public class ViewAllModel {

    public static void getListSeries(String apiKey, int page,int position,final Callback<TvSeriesBean> callback) {
        String authHeader = "Bearer " + apiKey;
        switch (position){
            case 1:
                getToday(authHeader,page,callback);
                break;

            case 2:
                getAiring(authHeader,page,callback);
                break;

            case 3:
                getTvPopular(authHeader,page,callback);
                break;

            case 4:
                getTopRatedTv(authHeader,page,callback);
                break;
        }
    }


    public static void getListResponse(String apiKey, int page,int position,final Callback<MovieBean> callback) {
        String authHeader = "Bearer " + apiKey;
        switch (position){
            case 1:
                getPopular(authHeader,page,callback);
                break;

            case 2:
                getopRated(authHeader,page,callback);
                break;

            case 3:
                getNow(authHeader,page,callback);
                break;

            case 4:
                getUpcoming(authHeader,page,callback);
                break;
        }
    }

    private static void getPopular(String authHeader, int page, Callback<MovieBean> callback) {
        NetworkingUtils.getMovieData()
                .getPopularList("en-US",authHeader,page)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<MovieBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {}

                    @Override
                    public void onNext(MovieBean data) {
                        callback.returnResult(data);
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.returnError(e.getMessage());
                    }

                    @Override
                    public void onComplete() {}
                });
    }

    private static void getopRated(String authHeader, int page, Callback<MovieBean> callback) {
        NetworkingUtils.getMovieData()
                .getTopRated("en-US",authHeader,page)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<MovieBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {}

                    @Override
                    public void onNext(MovieBean data) {
                        callback.returnResult(data);
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.returnError(e.getMessage());
                    }

                    @Override
                    public void onComplete() {}
                });
    }

    private static void getNow(String authHeader, int page, Callback<MovieBean> callback) {
        NetworkingUtils.getMovieData()
                .getNow("en-US",authHeader,page)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<MovieBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {}

                    @Override
                    public void onNext(MovieBean data) {
                        callback.returnResult(data);
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.returnError(e.getMessage());
                    }

                    @Override
                    public void onComplete() {}
                });
    }


    private static void getUpcoming(String authHeader, int page, Callback<MovieBean> callback) {
        NetworkingUtils.getMovieData()
                .getUpcoming("en-US",authHeader,page)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<MovieBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {}

                    @Override
                    public void onNext(MovieBean data) {
                        callback.returnResult(data);
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.returnError(e.getMessage());
                    }

                    @Override
                    public void onComplete() {}
                });
    }


    //tvSeries
    public static void getToday(String apiKey, int page,final Callback<TvSeriesBean> callback) {
//        Log.d("KeyApi","value: "+apiKey);
        NetworkingUtils.getMovieData()
                .getTodayTv("en-US",apiKey,page)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<TvSeriesBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {}

                    @Override
                    public void onNext(TvSeriesBean data) {
                        callback.returnResult(data);
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.returnError(e.getMessage());
                    }

                    @Override
                    public void onComplete() {}
                });
    }

    public static void getAiring(String apiKey, int page,final Callback<TvSeriesBean> callback) {
        NetworkingUtils.getMovieData()
                .getOnAiringTv("en-US",apiKey,page)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<TvSeriesBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {}

                    @Override
                    public void onNext(TvSeriesBean data) {
                        callback.returnResult(data);
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.returnError(e.getMessage());
                    }

                    @Override
                    public void onComplete() {}
                });
    }

    public static void getTvPopular(String apiKey, int page,final Callback<TvSeriesBean> callback) {
        NetworkingUtils.getMovieData()
                .getPopularTv("en-US",apiKey,page)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<TvSeriesBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {}

                    @Override
                    public void onNext(TvSeriesBean data) {
                        callback.returnResult(data);
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.returnError(e.getMessage());
                    }

                    @Override
                    public void onComplete() {}
                });
    }

    public static void getTopRatedTv(String apiKey, int page,final Callback<TvSeriesBean> callback) {
        NetworkingUtils.getMovieData()
                .getTopRatedTv("en-US",apiKey,page)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<TvSeriesBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {}

                    @Override
                    public void onNext(TvSeriesBean data) {
                        callback.returnResult(data);
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.returnError(e.getMessage());
                    }

                    @Override
                    public void onComplete() {}
                });
    }
}
