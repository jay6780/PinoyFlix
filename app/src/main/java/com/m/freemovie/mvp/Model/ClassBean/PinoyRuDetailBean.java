package com.m.freemovie.mvp.Model.ClassBean;

import java.util.List;

public class PinoyRuDetailBean {
    private List<String> videoUrls;
    private String link;

    public PinoyRuDetailBean(List<String> videoUrls, String link) {
        this.videoUrls = videoUrls;
        this.link = link;
    }

    public List<String> getVideoUrls() { return videoUrls; }
    public String getLink() { return link; }
}