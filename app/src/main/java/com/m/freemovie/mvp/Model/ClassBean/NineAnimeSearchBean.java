package com.m.freemovie.mvp.Model.ClassBean;

import java.util.List;

public class NineAnimeSearchBean {

    /**
     * author : yazky
     * results : [{"title":"Soul of the Dragon","link":"https://9anime.me.uk/series/soul-of-the-dragon/","image":"https://i1.wp.com/9anime.me.uk/wp-content/uploads/2025/12/1764848562-3818-153807.jpg?resize=246,350","status":"Ongoing","type":"Anime","audioType":"Sub"},{"title":"Dragon Raja Season 2","link":"https://9anime.me.uk/series/dragon-raja-season-2/","image":"https://i0.wp.com/9anime.me.uk/wp-content/uploads/2025/08/1754636568-5756-147273.jpg?resize=246,350","status":"Ongoing","type":"Anime","audioType":"Sub"},{"title":"Shrouding the Heavens","link":"https://9anime.me.uk/series/shrouding-the-heavens/","image":"https://i1.wp.com/9anime.me.uk/wp-content/uploads/2025/11/1763624872-4467-135727.jpg?resize=246,350","status":"Ongoing","type":"Anime","audioType":"Sub"},{"title":"Campfire Cooking in Another World with My Absurd Skill Season 2 (Dub)","link":"https://9anime.me.uk/series/campfire-cooking-in-another-world-with-my-absurd-skill-season-2-dub/","image":"https://i0.wp.com/9anime.me.uk/wp-content/uploads/2025/11/1763457476-8311-151772.jpg?resize=246,350","status":"Ongoing","type":"Anime","audioType":"Dub"},{"title":"So You\u2019re Raising a Warrior","link":"https://9anime.me.uk/series/so-youre-raising-a-warrior/","image":"https://i1.wp.com/9anime.me.uk/wp-content/uploads/2025/10/1761913742-9633-152354.jpg?resize=246,350","status":"Ongoing","type":"Anime","audioType":"Sub"},{"title":"Secrets of the Silent Witch: Specials","link":"https://9anime.me.uk/series/secrets-of-the-silent-witch-specials/","image":"https://i0.wp.com/9anime.me.uk/wp-content/uploads/2025/09/1757095547-3159-149732.jpg?resize=246,350","status":"Ongoing","type":"Special","audioType":"Sub"}]
     */

    private String author;
    private List<ResultsBean> results;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<ResultsBean> getResults() {
        return results;
    }

    public void setResults(List<ResultsBean> results) {
        this.results = results;
    }

    public static class ResultsBean {
        /**
         * title : Soul of the Dragon
         * link : https://9anime.me.uk/series/soul-of-the-dragon/
         * image : https://i1.wp.com/9anime.me.uk/wp-content/uploads/2025/12/1764848562-3818-153807.jpg?resize=246,350
         * status : Ongoing
         * type : Anime
         * audioType : Sub
         */

        private String title;
        private String link;
        private String image;
        private String status;
        private String type;
        private String audioType;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getAudioType() {
            return audioType;
        }

        public void setAudioType(String audioType) {
            this.audioType = audioType;
        }
    }
}
