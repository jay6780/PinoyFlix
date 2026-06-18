package com.m.freemovie.mvp.Model.ClassBean;

import java.util.List;

public class AniKoToSearchBean {

    /**
     * status : true
     * keyword : Dragon ball
     * total : 5
     * linkMore : https://anikoto.cz/filter?keyword=dragon ball
     * count : 5
     * results : [{"url":"https://anikoto.cz/watch/dragon-ball-super-broly-rwlxv","title":"Dragon Ball Super: Broly","japaneseTitle":"Dragon Ball Super Movie: Broly","thumbnail":"https://cdn.anipixcdn.co/thumbnail/8efb100a295c0c690931222ff4467bb8.jpg","rating":"8.12","type":"TV","year":"2018"},{"url":"https://anikoto.cz/watch/dragon-ball-z-special-2-the-history-of-trunks-ngp76","title":"Dragon Ball Z Special 2: The History of Trunks","japaneseTitle":"Dragon Ball Z Special 2: Zetsubou e no Hankou!! Nokosareta Chousenshi - Gohan to Trunks","thumbnail":"https://cdn.anipixcdn.co/thumbnail/9c01802ddb981e6bcfbec0f0516b8e35.jpg","rating":"7.72","type":"TV","year":"1993"},{"url":"https://anikoto.cz/watch/dragon-ball-super-saiya-jin-zetsumetsu-keikaku-jnygm","title":"Dragon Ball: Super Saiya-jin Zetsumetsu Keikaku","japaneseTitle":"Dragon Ball: Super Saiya-jin Zetsumetsu Keikaku","thumbnail":"https://cdn.anipixcdn.co/thumbnail/0f304eddb4ad6007a3093fd6d963a1d2.jpg","rating":"6.72","type":"TV","year":"?"},{"url":"https://anikoto.cz/watch/dragon-ball-z-dead-zone-4d3kg","title":"Dragon Ball Z: Dead Zone","japaneseTitle":"Dragon Ball Z Movie 01: Ora no Gohan wo Kaese!!","thumbnail":"https://cdn.anipixcdn.co/thumbnail/f3507289cfdc8c9ae93f4098111a13f9.jpg","rating":"6.72","type":"TV","year":"1989"},{"url":"https://anikoto.cz/watch/dragon-ball-z-super-android-13-i4fc4","title":"Dragon Ball Z: Super Android 13!","japaneseTitle":"Dragon Ball Z Movie 07: Kyokugen Battle!! Sandai Super Saiyajin","thumbnail":"https://cdn.anipixcdn.co/thumbnail/4175f2ebb265d58c6d8877841d016d08.jpg","rating":"6.91","type":"TV","year":"1992"}]
     */

    private boolean status;
    private String keyword;
    private int total;
    private String linkMore;
    private String count;
    private List<ResultsBean> results;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getLinkMore() {
        return linkMore;
    }

    public void setLinkMore(String linkMore) {
        this.linkMore = linkMore;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
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
         * url : https://anikoto.cz/watch/dragon-ball-super-broly-rwlxv
         * title : Dragon Ball Super: Broly
         * japaneseTitle : Dragon Ball Super Movie: Broly
         * thumbnail : https://cdn.anipixcdn.co/thumbnail/8efb100a295c0c690931222ff4467bb8.jpg
         * rating : 8.12
         * type : TV
         * year : 2018
         */

        private String url;
        private String title;
        private String japaneseTitle;
        private String thumbnail;
        private String rating;
        private String type;
        private String year;
        private String animeId;

        public String getAnimeId() {
            return animeId;
        }

        public void setAnimeId(String animeId) {
            this.animeId = animeId;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getJapaneseTitle() {
            return japaneseTitle;
        }

        public void setJapaneseTitle(String japaneseTitle) {
            this.japaneseTitle = japaneseTitle;
        }

        public String getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }

        public String getRating() {
            return rating;
        }

        public void setRating(String rating) {
            this.rating = rating;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getYear() {
            return year;
        }

        public void setYear(String year) {
            this.year = year;
        }
    }
}
