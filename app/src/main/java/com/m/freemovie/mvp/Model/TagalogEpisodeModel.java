package com.m.freemovie.mvp.Model;

import com.m.freemovie.Retrofit.Callback;
import com.m.freemovie.Retrofit.NetworkingUtils;
import com.m.freemovie.mvp.Model.ClassBean.TagalogEpisodeBean;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class TagalogEpisodeModel {
    public static void getTagalogEpisode(String url,final Callback<TagalogEpisodeBean> callback) {
        NetworkingUtils.getTagalogDub()
                .getTagalogEpisode(url)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<TagalogEpisodeBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {}

                    @Override
                    public void onNext(TagalogEpisodeBean data) {
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
