package com.m.freemovie.mvp.Model.ClassBean;

import java.util.List;

public class AnimePaheDownloadBean {

    /**
     * author : yazky
     * status : true
     * results : {"video":{"url":"https://www.blogger.com/video.g?token=AD6v5dwJgDo1LTxixB-bfo5N_9I8t7Szx0F1emw6ST7JOeznQyb-6XtOhph8p9EXQKOst6WCd0SpQzAE1xscO3sZVDC5rAK0bNuQFemCOzxGLB0Em4gPvru_tG8qjRRylw7FECtZ--Xv&origin=op.blogspot.com"},"streaming":[{"quality":"HD 1","url":"https://www.blogger.com/video.g?token=AD6v5dwJgDo1LTxixB-bfo5N_9I8t7Szx0F1emw6ST7JOeznQyb-6XtOhph8p9EXQKOst6WCd0SpQzAE1xscO3sZVDC5rAK0bNuQFemCOzxGLB0Em4gPvru_tG8qjRRylw7FECtZ--Xv&origin=op.blogspot.com"},{"quality":"HD 2","url":"https://flixcloud.cc/e/vaecplc532lq?v=2&skI=false&skO=false&project_r_ts=1789245568608"},{"quality":"HD 3","url":"https://megaplay.buzz/stream/mal/62535/10/sub"}],"download":[{"url":"https://gofile.io/d/Isa95RRm","quality":"Download"}]}
     */

    private String author;
    private boolean status;
    private ResultsBean results;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public ResultsBean getResults() {
        return results;
    }

    public void setResults(ResultsBean results) {
        this.results = results;
    }

    public static class ResultsBean {
        /**
         * video : {"url":"https://www.blogger.com/video.g?token=AD6v5dwJgDo1LTxixB-bfo5N_9I8t7Szx0F1emw6ST7JOeznQyb-6XtOhph8p9EXQKOst6WCd0SpQzAE1xscO3sZVDC5rAK0bNuQFemCOzxGLB0Em4gPvru_tG8qjRRylw7FECtZ--Xv&origin=op.blogspot.com"}
         * streaming : [{"quality":"HD 1","url":"https://www.blogger.com/video.g?token=AD6v5dwJgDo1LTxixB-bfo5N_9I8t7Szx0F1emw6ST7JOeznQyb-6XtOhph8p9EXQKOst6WCd0SpQzAE1xscO3sZVDC5rAK0bNuQFemCOzxGLB0Em4gPvru_tG8qjRRylw7FECtZ--Xv&origin=op.blogspot.com"},{"quality":"HD 2","url":"https://flixcloud.cc/e/vaecplc532lq?v=2&skI=false&skO=false&project_r_ts=1789245568608"},{"quality":"HD 3","url":"https://megaplay.buzz/stream/mal/62535/10/sub"}]
         * download : [{"url":"https://gofile.io/d/Isa95RRm","quality":"Download"}]
         */

        private VideoBean video;
        private List<StreamingBean> streaming;
        private List<DownloadBean> download;

        public VideoBean getVideo() {
            return video;
        }

        public void setVideo(VideoBean video) {
            this.video = video;
        }

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

        public static class VideoBean {
            /**
             * url : https://www.blogger.com/video.g?token=AD6v5dwJgDo1LTxixB-bfo5N_9I8t7Szx0F1emw6ST7JOeznQyb-6XtOhph8p9EXQKOst6WCd0SpQzAE1xscO3sZVDC5rAK0bNuQFemCOzxGLB0Em4gPvru_tG8qjRRylw7FECtZ--Xv&origin=op.blogspot.com
             */

            private String url;

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
        }

        public static class StreamingBean {
            /**
             * quality : HD 1
             * url : https://www.blogger.com/video.g?token=AD6v5dwJgDo1LTxixB-bfo5N_9I8t7Szx0F1emw6ST7JOeznQyb-6XtOhph8p9EXQKOst6WCd0SpQzAE1xscO3sZVDC5rAK0bNuQFemCOzxGLB0Em4gPvru_tG8qjRRylw7FECtZ--Xv&origin=op.blogspot.com
             */

            private String quality;
            private String url;

            public String getQuality() {
                return quality;
            }

            public void setQuality(String quality) {
                this.quality = quality;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
        }

        public static class DownloadBean {
            /**
             * url : https://gofile.io/d/Isa95RRm
             * quality : Download
             */

            private String url;
            private String quality;

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getQuality() {
                return quality;
            }

            public void setQuality(String quality) {
                this.quality = quality;
            }
        }
    }
}
