package com.m.freemovie.mvp.Model;

import com.m.freemovie.Retrofit.Callback;
import com.m.freemovie.Retrofit.NetworkingUtils;
import com.m.freemovie.mvp.Model.ClassBean.PaheLatestBean;
import com.m.freemovie.mvp.Model.ClassBean.RevivalSeriesBean;
import com.m.freemovie.mvp.Model.ClassBean.TagalogBean;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AnimeModel {

    public static void getNewest( int page,final Callback<PaheLatestBean> callback) {
        NetworkingUtils.getTagalogDub()
                .getLatestAnimePahe(page)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<PaheLatestBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {}

                    @Override
                    public void onNext(PaheLatestBean data) {
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

    public static void getHot(final Callback<TagalogBean> callback) {
        NetworkingUtils.getTagalogDub()
                .getTagalogSeries()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<TagalogBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(TagalogBean data) {
                        callback.returnResult(data);
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.returnError(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    public static void getPopular(int page,final Callback<RevivalSeriesBean> callback) {
        NetworkingUtils.getTagalogDub()
                .getRevivalSeries(page)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<RevivalSeriesBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {}

                    @Override
                    public void onNext(RevivalSeriesBean data) {
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

    public static void getMovies(int page,final Callback<RevivalSeriesBean> callback) {
        NetworkingUtils.getTagalogDub()
                .getRevivalMovies(page)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<RevivalSeriesBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {}

                    @Override
                    public void onNext(RevivalSeriesBean data) {
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
