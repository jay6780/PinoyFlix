package com.m.freemovie.mvp.ClassBean;

public class TagalogSearchEvent {
    boolean changeSearch;
    boolean isTagalog;

    public boolean isTagalog() {
        return isTagalog;
    }

    public void setTagalog(boolean tagalog) {
        isTagalog = tagalog;
    }


    public TagalogSearchEvent(boolean isTagalog) {
        this.isTagalog = isTagalog;
    }

}
