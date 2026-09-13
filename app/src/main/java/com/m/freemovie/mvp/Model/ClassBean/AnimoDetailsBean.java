package com.m.freemovie.mvp.Model.ClassBean;

import java.util.List;

public class AnimoDetailsBean {

    /**
     * success : true
     * info : {"title":"Though I Am an Inept Villainess","cover":"https://i3.wp.com/animotvslash.org/wp-content/uploads/2026/06/GT00371881-backdrop_wide.jpg","rating":null,"description":"As maidens from the Five Clans battle for the Crown Prince\u2019s heart, jealous Keigetsu uses forbidden magic to swap bodies with the beloved Reirin. Switching places does not guarantee happiness for Keigetsu with her new body's ill health leaving her hanging on the verge of death. Meanwhile, Reirin, now wearing the face of a scorned outcast, must answer for Keigetsu\u2019s... Read more"}
     * episodes : {"count":10,"results":[{"episode":10,"title":"Episode 10","release_date":"September 13, 2026","url":"https://animotvslash.org/though-i-am-an-inept-villainess-episode-10/"},{"episode":9,"title":"Episode 9","release_date":"September 6, 2026","url":"https://animotvslash.org/though-i-am-an-inept-villainess-episode-9/"},{"episode":8,"title":"Episode 8","release_date":"August 30, 2026","url":"https://animotvslash.org/though-i-am-an-inept-villainess-episode-8/"},{"episode":7,"title":"Episode 7","release_date":"August 23, 2026","url":"https://animotvslash.org/though-i-am-an-inept-villainess-episode-7/"},{"episode":6,"title":"Episode 6","release_date":"August 16, 2026","url":"https://animotvslash.org/though-i-am-an-inept-villainess-episode-6/"},{"episode":5,"title":"Episode 5","release_date":"August 9, 2026","url":"https://animotvslash.org/though-i-am-an-inept-villainess-episode-5/"},{"episode":4,"title":"Episode 4","release_date":"August 2, 2026","url":"https://animotvslash.org/though-i-am-an-inept-villainess-episode-4/"},{"episode":3,"title":"Episode 3","release_date":"July 26, 2026","url":"https://animotvslash.org/though-i-am-an-inept-villainess-episode-3/"},{"episode":2,"title":"Episode 2","release_date":"July 19, 2026","url":"https://animotvslash.org/though-i-am-an-inept-villainess-episode-2/"},{"episode":1,"title":"Episode 1","release_date":"July 12, 2026","url":"https://animotvslash.org/though-i-am-an-inept-villainess-episode-1/"}]}
     */

    private boolean success;
    private InfoBean info;
    private EpisodesBean episodes;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public InfoBean getInfo() {
        return info;
    }

    public void setInfo(InfoBean info) {
        this.info = info;
    }

    public EpisodesBean getEpisodes() {
        return episodes;
    }

    public void setEpisodes(EpisodesBean episodes) {
        this.episodes = episodes;
    }

    public static class InfoBean {
        /**
         * title : Though I Am an Inept Villainess
         * cover : https://i3.wp.com/animotvslash.org/wp-content/uploads/2026/06/GT00371881-backdrop_wide.jpg
         * rating : null
         * description : As maidens from the Five Clans battle for the Crown Prince’s heart, jealous Keigetsu uses forbidden magic to swap bodies with the beloved Reirin. Switching places does not guarantee happiness for Keigetsu with her new body's ill health leaving her hanging on the verge of death. Meanwhile, Reirin, now wearing the face of a scorned outcast, must answer for Keigetsu’s... Read more
         */

        private String title;
        private String cover;
        private Object rating;
        private String description;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }

        public Object getRating() {
            return rating;
        }

        public void setRating(Object rating) {
            this.rating = rating;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public static class EpisodesBean {
        /**
         * count : 10
         * results : [{"episode":10,"title":"Episode 10","release_date":"September 13, 2026","url":"https://animotvslash.org/though-i-am-an-inept-villainess-episode-10/"},{"episode":9,"title":"Episode 9","release_date":"September 6, 2026","url":"https://animotvslash.org/though-i-am-an-inept-villainess-episode-9/"},{"episode":8,"title":"Episode 8","release_date":"August 30, 2026","url":"https://animotvslash.org/though-i-am-an-inept-villainess-episode-8/"},{"episode":7,"title":"Episode 7","release_date":"August 23, 2026","url":"https://animotvslash.org/though-i-am-an-inept-villainess-episode-7/"},{"episode":6,"title":"Episode 6","release_date":"August 16, 2026","url":"https://animotvslash.org/though-i-am-an-inept-villainess-episode-6/"},{"episode":5,"title":"Episode 5","release_date":"August 9, 2026","url":"https://animotvslash.org/though-i-am-an-inept-villainess-episode-5/"},{"episode":4,"title":"Episode 4","release_date":"August 2, 2026","url":"https://animotvslash.org/though-i-am-an-inept-villainess-episode-4/"},{"episode":3,"title":"Episode 3","release_date":"July 26, 2026","url":"https://animotvslash.org/though-i-am-an-inept-villainess-episode-3/"},{"episode":2,"title":"Episode 2","release_date":"July 19, 2026","url":"https://animotvslash.org/though-i-am-an-inept-villainess-episode-2/"},{"episode":1,"title":"Episode 1","release_date":"July 12, 2026","url":"https://animotvslash.org/though-i-am-an-inept-villainess-episode-1/"}]
         */

        private int count;
        private List<ResultsBean> results;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public List<ResultsBean> getResults() {
            return results;
        }

        public void setResults(List<ResultsBean> results) {
            this.results = results;
        }

        public static class ResultsBean {
            /**
             * episode : 10
             * title : Episode 10
             * release_date : September 13, 2026
             * url : https://animotvslash.org/though-i-am-an-inept-villainess-episode-10/
             */

            private String episode;
            private String title;
            private String release_date;
            private String url;

            public String getEpisode() {
                return episode;
            }

            public void setEpisode(String episode) {
                this.episode = episode;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getRelease_date() {
                return release_date;
            }

            public void setRelease_date(String release_date) {
                this.release_date = release_date;
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
