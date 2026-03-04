package com.m.freemovie.mvp.Model;

import com.m.freemovie.Retrofit.Callback;
import com.m.freemovie.Retrofit.NetworkingUtils;
import com.m.freemovie.mvp.Model.ClassBean.PinoyMediaDetailBean;
import com.m.freemovie.mvp.Model.ClassBean.PinoyMovieRuBean;
import com.m.freemovie.mvp.Model.ClassBean.SearchRuBean;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PinoyPedialModel {
    public static void getDetailsRu(String url,final Callback<PinoyMediaDetailBean> callback) {
        NetworkingUtils.getTagalogDub()
                .getDetailRu(url)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<PinoyMediaDetailBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {}

                    @Override
                    public void onNext(PinoyMediaDetailBean data) {
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
