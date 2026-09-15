package com.m.freemovie.mvp.Model.ClassBean;

import java.util.List;

public class AnimePaheSearchBean {


    /**
     * author : yazky
     * query : dragon ball
     * total : 2
     * per_page : 8
     * current_page : 1
     * last_page : 2
     * next_page_url : null
     * prev_page_url : null
     * from : 1
     * to : 2
     * data : [{"anime_title":"Dragon Ball Daima (Dub)","anime_url":"https://animepahe.ch/series/dragon-ball-daima-dub/","anime_image":"https://animepahe.ch/wp-content/uploads/2025/01/1736575420-5093-145231-214x300.jpg","status":"Ongoing","type":"Anime","language":"Dub"},{"anime_title":"Dragon Ball Daima","anime_url":"https://animepahe.ch/series/dragon-ball-daima/","anime_image":"https://animepahe.ch/wp-content/uploads/2024/10/1728671370-7541-145231-214x300.jpg","status":"Ongoing","type":"Anime","language":"Sub"}]
     */

    private String author;
    private String query;
    private int total;
    private int per_page;
    private int current_page;
    private int last_page;
    private Object next_page_url;
    private Object prev_page_url;
    private int from;
    private int to;
    private List<DataBean> data;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPer_page() {
        return per_page;
    }

    public void setPer_page(int per_page) {
        this.per_page = per_page;
    }

    public int getCurrent_page() {
        return current_page;
    }

    public void setCurrent_page(int current_page) {
        this.current_page = current_page;
    }

    public int getLast_page() {
        return last_page;
    }

    public void setLast_page(int last_page) {
        this.last_page = last_page;
    }

    public Object getNext_page_url() {
        return next_page_url;
    }

    public void setNext_page_url(Object next_page_url) {
        this.next_page_url = next_page_url;
    }

    public Object getPrev_page_url() {
        return prev_page_url;
    }

    public void setPrev_page_url(Object prev_page_url) {
        this.prev_page_url = prev_page_url;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * anime_title : Dragon Ball Daima (Dub)
         * anime_url : https://animepahe.ch/series/dragon-ball-daima-dub/
         * anime_image : https://animepahe.ch/wp-content/uploads/2025/01/1736575420-5093-145231-214x300.jpg
         * status : Ongoing
         * type : Anime
         * language : Dub
         */

        private String anime_title;
        private String anime_url;
        private String anime_image;
        private String status;
        private String type;
        private String language;

        public String getAnime_title() {
            return anime_title;
        }

        public void setAnime_title(String anime_title) {
            this.anime_title = anime_title;
        }

        public String getAnime_url() {
            return anime_url;
        }

        public void setAnime_url(String anime_url) {
            this.anime_url = anime_url;
        }

        public String getAnime_image() {
            return anime_image;
        }

        public void setAnime_image(String anime_image) {
            this.anime_image = anime_image;
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

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }
    }
}
