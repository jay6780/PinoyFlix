package com.m.freemovie.mvp.ClassBean;

public class FreeMovieEvent {
    boolean changeSearch;
    boolean isTagalog;

    public boolean isTagalog() {
        return isTagalog;
    }

    public void setTagalog(boolean tagalog) {
        isTagalog = tagalog;
    }

    public boolean isChangeSearch() {
        return changeSearch;
    }

    public FreeMovieEvent(boolean changeSearch, boolean isTagalog) {
        this.changeSearch = changeSearch;
        this.isTagalog = isTagalog;
    }

    public void setLotteryType(boolean changeSearch) {
        this.changeSearch = changeSearch;
    }
}
