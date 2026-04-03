package com.m.freemovie.mvp.Model.ClassBean;

import java.util.List;

public class AnimoPageBean {
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
         * title : Welcome to Demon School! Iruma-kun Season 3
         * link : https://animotvslash.org/anime/welcome-to-demon-school-iruma-kun-season-3/
         * img : https://i3.wp.com/animotvslash.org/wp-content/uploads/2026/04/bx139092-q521Du1fkosV.jpg
         * type : TV
         * status : Completed
         * subType : Sub
         */

        private String title;
        private String link;
        private String img;
        private String type;
        private String status;
        private String subType;

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

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getSubType() {
            return subType;
        }

        public void setSubType(String subType) {
            this.subType = subType;
        }
    }
}
