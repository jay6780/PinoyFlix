package com.m.freemovie.mvp.ClassBean;

public class FreeMovieEvent {
    boolean changeSearch;

    public boolean isChangeSearch() {
        return changeSearch;
    }

    public FreeMovieEvent(boolean changeSearch) {
        this.changeSearch = changeSearch;
    }

    public void setLotteryType(boolean changeSearch) {
        this.changeSearch = changeSearch;
    }
}
