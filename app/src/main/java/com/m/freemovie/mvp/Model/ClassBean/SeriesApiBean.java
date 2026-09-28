package com.m.freemovie.mvp.Model.ClassBean;

import java.util.List;

public class SeriesApiBean {

    /**
     * type : series
     * count : 4
     * providers : [{"name":"Videasy","baseUrl":"https://player.videasy.ws/embed/tv","url":"https://player.videasy.ws/embed/tv"},{"name":"Vidrock","baseUrl":"https://vidrock.to/tv","url":"https://vidrock.to/tv"},{"name":"MoviesAPI","baseUrl":"https://moviesapi.to/tv","url":"https://moviesapi.to/tv"},{"name":"Vidfast","baseUrl":"https://vidfast.vc/tv","url":"https://vidfast.vc/tv"}]
     */

    private String type;
    private int count;
    private List<ProvidersBean> providers;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<ProvidersBean> getProviders() {
        return providers;
    }

    public void setProviders(List<ProvidersBean> providers) {
        this.providers = providers;
    }

    public static class ProvidersBean {
        /**
         * name : Videasy
         * baseUrl : https://player.videasy.ws/embed/tv
         * url : https://player.videasy.ws/embed/tv
         */

        private String name;
        private String baseUrl;
        private String url;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
