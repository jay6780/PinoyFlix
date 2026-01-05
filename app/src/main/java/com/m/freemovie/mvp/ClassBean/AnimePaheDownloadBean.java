package com.m.freemovie.mvp.ClassBean;

import java.util.List;

public class AnimePaheDownloadBean {


    /**
     * author : yazky
     * results : {"streaming":[{"src":"https://kwik.cx/e/JhYUT0V8ozYQ","fansub":"ZeroBuild","resolution":"360"},{"src":"https://kwik.cx/e/9xKt4efj8OqH","fansub":"ZeroBuild","resolution":"720"},{"src":"https://kwik.cx/e/CRWEVWFxG50s","fansub":"ZeroBuild","resolution":"1080"},{"src":"https://kwik.cx/e/Dvv37QXIY96o","fansub":"ZeroBuild","resolution":"360"},{"src":"https://kwik.cx/e/FCKkRDIPWYx8","fansub":"ZeroBuild","resolution":"720"},{"src":"https://kwik.cx/e/K2BdAyS1STWz","fansub":"ZeroBuild","resolution":"1080"}],"download":[{"href":"https://pahe.win/MRTes","text":"ZeroBuild · 360p (33MB) BD"},{"href":"https://pahe.win/unhZB","text":"ZeroBuild · 720p (66MB) BD"},{"href":"https://pahe.win/xttBq","text":"ZeroBuild · 1080p (109MB) BD"},{"href":"https://pahe.win/PBIgb","text":"ZeroBuild · 360p (32MB) BD eng"},{"href":"https://pahe.win/wkIvW","text":"ZeroBuild · 720p (63MB) BD eng"},{"href":"https://pahe.win/BLOKs","text":"ZeroBuild · 1080p (106MB) BD eng"}]}
     */

    private String author;
    private ResultsBean results;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public ResultsBean getResults() {
        return results;
    }

    public void setResults(ResultsBean results) {
        this.results = results;
    }

    public static class ResultsBean {
        private List<StreamingBean> streaming;
        private List<DownloadBean> download;

        public List<StreamingBean> getStreaming() {
            return streaming;
        }

        public void setStreaming(List<StreamingBean> streaming) {
            this.streaming = streaming;
        }

        public List<DownloadBean> getDownload() {
            return download;
        }

        public void setDownload(List<DownloadBean> download) {
            this.download = download;
        }

        public static class StreamingBean {
            /**
             * src : https://kwik.cx/e/JhYUT0V8ozYQ
             * fansub : ZeroBuild
             * resolution : 360
             */

            private String src;
            private String fansub;
            private String resolution;

            public String getSrc() {
                return src;
            }

            public void setSrc(String src) {
                this.src = src;
            }

            public String getFansub() {
                return fansub;
            }

            public void setFansub(String fansub) {
                this.fansub = fansub;
            }

            public String getResolution() {
                return resolution;
            }

            public void setResolution(String resolution) {
                this.resolution = resolution;
            }
        }

        public static class DownloadBean {
            /**
             * href : https://pahe.win/MRTes
             * text : ZeroBuild · 360p (33MB) BD
             */

            private String href;
            private String text;

            public String getHref() {
                return href;
            }

            public void setHref(String href) {
                this.href = href;
            }

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }
        }
    }
}
