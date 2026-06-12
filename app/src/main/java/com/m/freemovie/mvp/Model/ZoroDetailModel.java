package com.m.freemovie.mvp.Model;

import com.m.freemovie.Retrofit.Callback;
import com.m.freemovie.Retrofit.NetworkingUtils;
import com.m.freemovie.mvp.Model.ClassBean.TagalogEpisodeBean;
import com.m.freemovie.mvp.Model.ClassBean.ZoRoDetailBean;
import com.m.freemovie.mvp.Model.ClassBean.ZoRoVideoUrlBean;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ZoroDetailModel {
    public static void getZoroUrl(String url,final Callback<ZoRoDetailBean> callback) {
        NetworkingUtils.getTagalogDub()
                .getZoroDetail(url)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<ZoRoDetailBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {}

                    @Override
                    public void onNext(ZoRoDetailBean data) {
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


    public static void getZoRoVideoUrl(String url,final Callback<ZoRoVideoUrlBean> callback) {
        NetworkingUtils.getTagalogDub()
                .getVideoZoRo(url)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<ZoRoVideoUrlBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {}

                    @Override
                    public void onNext(ZoRoVideoUrlBean data) {
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
