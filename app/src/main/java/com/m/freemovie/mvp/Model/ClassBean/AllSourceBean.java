package com.m.freemovie.mvp.Model.ClassBean;

public class AllSourceBean {
    private String qualityName;
    private String url;

    public AllSourceBean(String qualityName, String url) {
        this.qualityName = qualityName;
        this.url = url;
    }

    public String getQualityName() {
        return qualityName;
    }

    public void setQualityName(String qualityName) {
        this.qualityName = qualityName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
