package com.m.freemovie.mvp.Model.ClassBean;

import java.util.List;

public class ZoRoSearchBean {

    private List<ResultsBean> results;

    public List<ResultsBean> getResults() {
        return results;
    }

    public void setResults(List<ResultsBean> results) {
        this.results = results;
    }

    public static class ResultsBean {
        /**
         * title : One Piece
         * link : https://zorotv.com.ro/anime/one-piece/
         * image : https://zorotv.com.ro/wp-content/uploads/2025/11/one-piece.jpg
         * type : TV
         * status : Ongoing
         * sub : Sub
         */

        private String title;
        private String link;
        private String image;
        private String type;
        private String status;
        private String sub;

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

        public String getSub() {
            return sub;
        }

        public void setSub(String sub) {
            this.sub = sub;
        }
    }
}
