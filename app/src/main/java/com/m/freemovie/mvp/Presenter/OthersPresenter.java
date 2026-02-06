package com.m.freemovie.mvp.Presenter;

import androidx.annotation.NonNull;

import com.m.freemovie.Retrofit.Callback;
import com.m.freemovie.mvp.Contract.OthersContract;
import com.m.freemovie.mvp.Model.ClassBean.OtherBean;
import com.m.freemovie.mvp.Model.OthersModel;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class OthersPresenter implements OthersContract.Presenter {

    private final OthersContract.View view;

    public OthersPresenter(OthersContract.View view) {
        this.view = view;
    }
    // ---------- COMMON HANDLER ----------
    private interface ModelCall {
        void call(int page, Callback<OtherBean> callback);
    }

    private void execute(int page, ModelCall modelCall, ResultHandler handler) {
        view.showLoading();

        modelCall.call(page, new Callback<OtherBean>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {}

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {}

            @Override
            public void returnResult(OtherBean apiBean) {
                view.hideLoading();
                handler.onSuccess(apiBean);
            }

            @Override
            public void returnError(String message) {
                view.hideLoading();
                view.showError(message);
            }
        });
    }

    private interface ResultHandler {
        void onSuccess(OtherBean bean);
    }

    // ---------- IMPLEMENTATIONS ----------
    @Override
    public void getSciFiFantasyPage(int page) {
        execute(page, OthersModel::getSciFiFantasy, view::getSciFiFantasy);
    }

    @Override
    public void getThaiDramaPage(int page) {
        execute(page, OthersModel::getThaiDrama, view::getThaiDrama);
    }

    @Override
    public void getCrimePage(int page) {
        execute(page, OthersModel::getCrime, view::getCrime);
    }

    @Override
    public void getRomancePage(int page) {
        execute(page, OthersModel::getRomance, view::getRomance);
    }

    @Override
    public void getHistoryPage(int page) {
        execute(page, OthersModel::getHistory, view::getHistory);
    }

    @Override
    public void getWarPage(int page) {
        execute(page, OthersModel::getWar, view::getWar);
    }

    @Override
    public void getActionPage(int page) {
        execute(page, OthersModel::getAction, view::getAction);
    }

    @Override
    public void getDramaPage(int page) {
        execute(page, OthersModel::getDrama, view::getDrama);
    }

    @Override
    public void getMovieSpeakKhmerPage(int page) {
        execute(page, OthersModel::getMovieSpeakKhmer, view::getMovieSpeakKhmer);
    }

    @Override
    public void getThrillerPage(int page) {
        execute(page, OthersModel::getThriller, view::getThriller);
    }

    @Override
    public void getFantasyPage(int page) {
        execute(page, OthersModel::getFantasy, view::getFantasy);
    }

    @Override
    public void getMusicPage(int page) {
        execute(page, OthersModel::getMusic, view::getMusic);
    }

    @Override
    public void getWarPoliticsPage(int page) {
        execute(page, OthersModel::getWarPolitics, view::getWarPolitics);
    }

    @Override
    public void getVivamaxPage(int page) {
        execute(page, OthersModel::getVivamax, view::getVivamax);
    }

    @Override
    public void getTvMoviePage(int page) {
        execute(page, OthersModel::getTvMovie, view::getTvMovie);
    }

    @Override
    public void getDocumentaryPage(int page) {
        execute(page, OthersModel::getDocumentary, view::getDocumentary);
    }

    @Override
    public void getKoreaDramaPage(int page) {
        execute(page, OthersModel::getKoreaDrama, view::getKoreaDrama);
    }

    @Override
    public void getMysteryPage(int page) {
        execute(page, OthersModel::getMystery, view::getMystery);
    }

    @Override
    public void getAdventurePage(int page) {
        execute(page, OthersModel::getAdventure, view::getAdventure);
    }

    @Override
    public void getComedyPage(int page) {
        execute(page, OthersModel::getComedy, view::getComedy);
    }

    @Override
    public void getChineseDramaPage(int page) {
        execute(page, OthersModel::getChineseDrama, view::getChineseDrama);
    }

    @Override
    public void getScienceFictionPage(int page) {
        execute(page, OthersModel::getScienceFiction, view::getScienceFiction);
    }

    @Override
    public void getFamilyPage(int page) {
        execute(page, OthersModel::getFamily, view::getFamily);
    }

    @Override
    public void getTvShowsPage(int page) {
        execute(page, OthersModel::getTvShows, view::getTvShows);
    }

    @Override
    public void getEroticPage(int page) {
        execute(page, OthersModel::getErotic, view::getErotic);
    }

    @Override
    public void getMoviePage(int page) {
        execute(page, OthersModel::getMovie, view::getMovie);
    }

    @Override
    public void getAnimationPage(int page) {
        execute(page, OthersModel::getAnimation, view::getAnimation);
    }

    @Override
    public void getHorrorPage(int page) {
        execute(page, OthersModel::getHorror, view::getHorror);
    }

    @Override
    public void getAllMoviesPage(int page) {
        execute(page, OthersModel::getAllMovies, view::getAllMovies);
    }
}
