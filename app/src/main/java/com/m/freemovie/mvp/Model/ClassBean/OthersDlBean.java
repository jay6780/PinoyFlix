package com.m.freemovie.mvp.Model.ClassBean;

import java.util.List;

public class OthersDlBean {


    /**
     * author : yazky
     * results : {"player":{"title":"Death Name \u2013 Khflix","videoUrl":"https://hls.khflix.top/Death.Name/index.m3u8","image":"https://khflix.com/wp-content/uploads/2026/01/uhyhWRzgCSiVfY0Ighgz9ujP85T.jpg"},"subtitles":[{"label":"Khmer","file":"https://subtitle.khflix.top/Death.Name.Khmer.vtt"},{"label":"English","file":"https://subtitle.khflix.top/Death.Name.vtt"},{"label":"Arabic","file":"https://subtitle.khflix.top/Death.Name.Arabic.vtt"},{"label":"Philippines","file":"https://subtitle.khflix.top/Death.Name.Filipino.vtt"},{"label":"Hindi","file":"https://subtitle.khflix.top/Death.Name.Hindi.vtt"},{"label":"Indonesian","file":"https://subtitle.khflix.top/Death.Name.Indonesian.vtt"},{"label":"Thailand","file":"https://subtitle.khflix.top/Death.Name.Thai.vtt"},{"label":"Vietnamese","file":"https://subtitle.khflix.top/Death.Name.Vietnamese.vtt"},{"label":"Malay","file":"https://subtitle.khflix.top/Death.Name.Malay.vtt"}]}
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
        /**
         * player : {"title":"Death Name \u2013 Khflix","videoUrl":"https://hls.khflix.top/Death.Name/index.m3u8","image":"https://khflix.com/wp-content/uploads/2026/01/uhyhWRzgCSiVfY0Ighgz9ujP85T.jpg"}
         * subtitles : [{"label":"Khmer","file":"https://subtitle.khflix.top/Death.Name.Khmer.vtt"},{"label":"English","file":"https://subtitle.khflix.top/Death.Name.vtt"},{"label":"Arabic","file":"https://subtitle.khflix.top/Death.Name.Arabic.vtt"},{"label":"Philippines","file":"https://subtitle.khflix.top/Death.Name.Filipino.vtt"},{"label":"Hindi","file":"https://subtitle.khflix.top/Death.Name.Hindi.vtt"},{"label":"Indonesian","file":"https://subtitle.khflix.top/Death.Name.Indonesian.vtt"},{"label":"Thailand","file":"https://subtitle.khflix.top/Death.Name.Thai.vtt"},{"label":"Vietnamese","file":"https://subtitle.khflix.top/Death.Name.Vietnamese.vtt"},{"label":"Malay","file":"https://subtitle.khflix.top/Death.Name.Malay.vtt"}]
         */

        private PlayerBean player;
        private List<SubtitlesBean> subtitles;

        public PlayerBean getPlayer() {
            return player;
        }

        public void setPlayer(PlayerBean player) {
            this.player = player;
        }

        public List<SubtitlesBean> getSubtitles() {
            return subtitles;
        }

        public void setSubtitles(List<SubtitlesBean> subtitles) {
            this.subtitles = subtitles;
        }

        public static class PlayerBean {
            /**
             * title : Death Name – Khflix
             * videoUrl : https://hls.khflix.top/Death.Name/index.m3u8
             * image : https://khflix.com/wp-content/uploads/2026/01/uhyhWRzgCSiVfY0Ighgz9ujP85T.jpg
             */

            private String title;
            private String videoUrl;
            private String image;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getVideoUrl() {
                return videoUrl;
            }

            public void setVideoUrl(String videoUrl) {
                this.videoUrl = videoUrl;
            }

            public String getImage() {
                return image;
            }

            public void setImage(String image) {
                this.image = image;
            }
        }

        public static class SubtitlesBean {
            /**
             * label : Khmer
             * file : https://subtitle.khflix.top/Death.Name.Khmer.vtt
             */

            private String label;
            private String file;

            public String getLabel() {
                return label;
            }

            public void setLabel(String label) {
                this.label = label;
            }

            public String getFile() {
                return file;
            }

            public void setFile(String file) {
                this.file = file;
            }
        }
    }
}
