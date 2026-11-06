package com.m.freemovie.mvp.Model.ClassBean;

import java.util.List;

public class PinoyMediaDetailBean {

    /**
     * success : true
     * pageUrl : https://pinoymoviepedia.ru/movies/milf-2026/
     * data : {"title":"MILF (2026)","poster":"https://image.tmdb.org/t/p/w780/nIccp5Nhyg8oXYC9qtiwY4bFx3H.jpg\n","synopsis":"Watch MILF 2026 Full Movie HD The OG VIVA Hot Babes reunite for a wild night out with VMX vixen Ashley Lopez. Their evening takes an unexpected turn as the four longtime friends compete with a younger woman for a handsome bartender\u2019s attention, sparking a playful clash of beauty and generations.MILF 2026 Streaming Online HD Link","downloadLinks":[{"text":"Hollywood Movies","url":"https://bluray7.com/"},{"text":"Hollywood Movies","url":"https://bluray7.com/"},{"text":"External Link","url":"https://image.tmdb.org/t/p/original/nIccp5Nhyg8oXYC9qtiwY4bFx3H.jpg"},{"text":"External Link","url":"https://image.tmdb.org/t/p/original/2RkxDyGHuU9vx0OEqtTozZvu2vZ.jpg"},{"text":"External Link","url":"https://image.tmdb.org/t/p/original/2o0MUN90bk0v9ULatfrmosWFcSK.jpg"}]}
     */

    private boolean success;
    private String pageUrl;
    private DataBean data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * title : MILF (2026)
         * poster : https://image.tmdb.org/t/p/w780/nIccp5Nhyg8oXYC9qtiwY4bFx3H.jpg
         * synopsis : Watch MILF 2026 Full Movie HD The OG VIVA Hot Babes reunite for a wild night out with VMX vixen Ashley Lopez. Their evening takes an unexpected turn as the four longtime friends compete with a younger woman for a handsome bartender’s attention, sparking a playful clash of beauty and generations.MILF 2026 Streaming Online HD Link
         * downloadLinks : [{"text":"Hollywood Movies","url":"https://bluray7.com/"},{"text":"Hollywood Movies","url":"https://bluray7.com/"},{"text":"External Link","url":"https://image.tmdb.org/t/p/original/nIccp5Nhyg8oXYC9qtiwY4bFx3H.jpg"},{"text":"External Link","url":"https://image.tmdb.org/t/p/original/2RkxDyGHuU9vx0OEqtTozZvu2vZ.jpg"},{"text":"External Link","url":"https://image.tmdb.org/t/p/original/2o0MUN90bk0v9ULatfrmosWFcSK.jpg"}]
         */

        private String title;
        private String poster;
        private String synopsis;
        private List<DownloadLinksBean> downloadLinks;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getPoster() {
            return poster;
        }

        public void setPoster(String poster) {
            this.poster = poster;
        }

        public String getSynopsis() {
            return synopsis;
        }

        public void setSynopsis(String synopsis) {
            this.synopsis = synopsis;
        }

        public List<DownloadLinksBean> getDownloadLinks() {
            return downloadLinks;
        }

        public void setDownloadLinks(List<DownloadLinksBean> downloadLinks) {
            this.downloadLinks = downloadLinks;
        }

        public static class DownloadLinksBean {
            /**
             * text : Hollywood Movies
             * url : https://bluray7.com/
             */

            private String text;
            private String url;

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
        }
    }
}
