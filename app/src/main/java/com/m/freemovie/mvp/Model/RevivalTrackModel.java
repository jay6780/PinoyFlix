package com.m.freemovie.mvp.Model;

import com.m.freemovie.Retrofit.Callback;
import com.m.freemovie.Retrofit.NetworkingUtils;
import com.m.freemovie.mvp.Model.ClassBean.DetailDownloadBean;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RevivalTrackModel {
    public static void getTrackInfo(String url,final Callback<DetailDownloadBean> callback) {
        NetworkingUtils.getTagalogDub()
                .getVideoTrack(url)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<DetailDownloadBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {}

                    @Override
                    public void onNext(DetailDownloadBean data) {
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
