package com.m.freemovie.mvp.ClassBean;

public class FreeMovieEvent {
    boolean changeSearch;

    public boolean isChangeSearch() {
        return changeSearch;
    }

    public void setChangeSearch(boolean changeSearch) {
        this.changeSearch = changeSearch;
    }

    public FreeMovieEvent(boolean changeSearch) {
        this.changeSearch = changeSearch;
    }

}
