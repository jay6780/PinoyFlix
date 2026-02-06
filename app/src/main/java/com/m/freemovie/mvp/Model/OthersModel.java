package com.m.freemovie.mvp.Model;

import com.m.freemovie.Retrofit.Callback;
import com.m.freemovie.Retrofit.NetworkingUtils;
import com.m.freemovie.mvp.Model.ClassBean.OtherBean;
import com.m.freemovie.mvp.Model.ClassBean.OthersDlBean;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class OthersModel {
    // ---------- GENRES ----------
    public static void getSciFiFantasy(int page, Callback<OtherBean> callback) {
        subscribe(NetworkingUtils.getTagalogDub().getSciFiFantasy(page), callback);
    }

    public static void getThaiDrama(int page, Callback<OtherBean> callback) {
        subscribe(NetworkingUtils.getTagalogDub().getThaiDrama(page), callback);
    }

    public static void getCrime(int page, Callback<OtherBean> callback) {
        subscribe(NetworkingUtils.getTagalogDub().getCrime(page), callback);
    }

    public static void getRomance(int page, Callback<OtherBean> callback) {
        subscribe(NetworkingUtils.getTagalogDub().getRomance(page), callback);
    }

    public static void getHistory(int page, Callback<OtherBean> callback) {
        subscribe(NetworkingUtils.getTagalogDub().getHistory(page), callback);
    }

    public static void getWar(int page, Callback<OtherBean> callback) {
        subscribe(NetworkingUtils.getTagalogDub().getWar(page), callback);
    }

    public static void getAction(int page, Callback<OtherBean> callback) {
        subscribe(NetworkingUtils.getTagalogDub().getAction(page), callback);
    }

    public static void getDrama(int page, Callback<OtherBean> callback) {
        subscribe(NetworkingUtils.getTagalogDub().getDrama(page), callback);
    }

    public static void getMovieSpeakKhmer(int page, Callback<OtherBean> callback) {
        subscribe(NetworkingUtils.getTagalogDub().getMovieSpeakKhmer(page), callback);
    }

    public static void getThriller(int page, Callback<OtherBean> callback) {
        subscribe(NetworkingUtils.getTagalogDub().getThriller(page), callback);
    }

    public static void getFantasy(int page, Callback<OtherBean> callback) {
        subscribe(NetworkingUtils.getTagalogDub().getFantasy(page), callback);
    }

    public static void getMusic(int page, Callback<OtherBean> callback) {
        subscribe(NetworkingUtils.getTagalogDub().getMusic(page), callback);
    }

    public static void getWarPolitics(int page, Callback<OtherBean> callback) {
        subscribe(NetworkingUtils.getTagalogDub().getWarPolitics(page), callback);
    }

    public static void getVivamax(int page, Callback<OtherBean> callback) {
        subscribe(NetworkingUtils.getTagalogDub().getVivamax(page), callback);
    }

    public static void getTvMovie(int page, Callback<OtherBean> callback) {
        subscribe(NetworkingUtils.getTagalogDub().getTvMovie(page), callback);
    }

    public static void getDocumentary(int page, Callback<OtherBean> callback) {
        subscribe(NetworkingUtils.getTagalogDub().getDocumentary(page), callback);
    }

    public static void getKoreaDrama(int page, Callback<OtherBean> callback) {
        subscribe(NetworkingUtils.getTagalogDub().getKoreaDrama(page), callback);
    }

    public static void getMystery(int page, Callback<OtherBean> callback) {
        subscribe(NetworkingUtils.getTagalogDub().getMystery(page), callback);
    }

    public static void getAdventure(int page, Callback<OtherBean> callback) {
        subscribe(NetworkingUtils.getTagalogDub().getAdventure(page), callback);
    }

    public static void getComedy(int page, Callback<OtherBean> callback) {
        subscribe(NetworkingUtils.getTagalogDub().getComedy(page), callback);
    }

    public static void getChineseDrama(int page, Callback<OtherBean> callback) {
        subscribe(NetworkingUtils.getTagalogDub().getChineseDrama(page), callback);
    }

    public static void getScienceFiction(int page, Callback<OtherBean> callback) {
        subscribe(NetworkingUtils.getTagalogDub().getScienceFiction(page), callback);
    }

    public static void getFamily(int page, Callback<OtherBean> callback) {
        subscribe(NetworkingUtils.getTagalogDub().getFamily(page), callback);
    }

    public static void getTvShows(int page, Callback<OtherBean> callback) {
        subscribe(NetworkingUtils.getTagalogDub().getTvShows(page), callback);
    }

    public static void getErotic(int page, Callback<OtherBean> callback) {
        subscribe(NetworkingUtils.getTagalogDub().getErotic(page), callback);
    }

    public static void getMovie(int page, Callback<OtherBean> callback) {
        subscribe(NetworkingUtils.getTagalogDub().getMovie(page), callback);
    }

    public static void getAnimation(int page, Callback<OtherBean> callback) {
        subscribe(NetworkingUtils.getTagalogDub().getAnimation(page), callback);
    }

    public static void getHorror(int page, Callback<OtherBean> callback) {
        subscribe(NetworkingUtils.getTagalogDub().getHorror(page), callback);
    }

    public static void getAllMovies(int page, Callback<OtherBean> callback) {
        subscribe(NetworkingUtils.getTagalogDub().getAllMovies(page), callback);
    }

    public static void getDownloadOther( String url, Callback<OthersDlBean> callback) {
        download(NetworkingUtils.getTagalogDub().getOtherDownload(url), callback);
    }

    private static void subscribe(
            Observable<OtherBean> observable,
            final Callback<OtherBean> callback
    ) {
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<OtherBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {}

                    @Override
                    public void onNext(OtherBean data) {
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
    private static void download(
            Observable<OthersDlBean> observable,
            final Callback<OthersDlBean> callback
    ) {
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<OthersDlBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {}

                    @Override
                    public void onNext(OthersDlBean data) {
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
