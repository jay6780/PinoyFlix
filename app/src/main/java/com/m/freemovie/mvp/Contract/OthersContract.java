package com.m.freemovie.mvp.Contract;

import com.m.freemovie.mvp.Model.ClassBean.OtherBean;

public interface OthersContract {

    interface View {
        void showLoading();
        void hideLoading();
        void showError(String error);

        void getSciFiFantasy(OtherBean otherBean);
        void getThaiDrama(OtherBean otherBean);
        void getCrime(OtherBean otherBean);
        void getRomance(OtherBean otherBean);
        void getHistory(OtherBean otherBean);
        void getWar(OtherBean otherBean);
        void getAction(OtherBean otherBean);
        void getDrama(OtherBean otherBean);
        void getMovieSpeakKhmer(OtherBean otherBean);
        void getThriller(OtherBean otherBean);
        void getFantasy(OtherBean otherBean);
        void getMusic(OtherBean otherBean);
        void getWarPolitics(OtherBean otherBean);
        void getVivamax(OtherBean otherBean);
        void getTvMovie(OtherBean otherBean);
        void getDocumentary(OtherBean otherBean);
        void getKoreaDrama(OtherBean otherBean);
        void getMystery(OtherBean otherBean);
        void getAdventure(OtherBean otherBean);
        void getComedy(OtherBean otherBean);
        void getChineseDrama(OtherBean otherBean);
        void getScienceFiction(OtherBean otherBean);
        void getFamily(OtherBean otherBean);
        void getTvShows(OtherBean otherBean);
        void getErotic(OtherBean otherBean);
        void getMovie(OtherBean otherBean);
        void getAnimation(OtherBean otherBean);
        void getHorror(OtherBean otherBean);
        void getAllMovies(OtherBean otherBean);
    }

    interface Presenter {
        void getSciFiFantasyPage(int page);
        void getThaiDramaPage(int page);
        void getCrimePage(int page);
        void getRomancePage(int page);
        void getHistoryPage(int page);
        void getWarPage(int page);
        void getActionPage(int page);
        void getDramaPage(int page);
        void getMovieSpeakKhmerPage(int page);
        void getThrillerPage(int page);
        void getFantasyPage(int page);
        void getMusicPage(int page);
        void getWarPoliticsPage(int page);
        void getVivamaxPage(int page);
        void getTvMoviePage(int page);
        void getDocumentaryPage(int page);
        void getKoreaDramaPage(int page);
        void getMysteryPage(int page);
        void getAdventurePage(int page);
        void getComedyPage(int page);
        void getChineseDramaPage(int page);
        void getScienceFictionPage(int page);
        void getFamilyPage(int page);
        void getTvShowsPage(int page);
        void getEroticPage(int page);
        void getMoviePage(int page);
        void getAnimationPage(int page);
        void getHorrorPage(int page);
        void getAllMoviesPage(int page);
    }
}
