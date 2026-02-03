package com.m.freemovie.mvp.Model.ClassBean;

public class DownloadNineAnimeBean {

    /**
     * author : yazky
     * results : {"title":"One-Punch Man Season 3","alter":"One-Punch Man Season 3, One Punch Man 3rd Season, OPM 3, ワンパンマン 3","image":"https://i0.wp.com/9anime.me.uk/wp-content/uploads/2025/10/1760294356-8520-148347.jpg?resize=246,350","rating":"7.14","status":"Ongoing","studio":"J.C.Staff","released":"2025","duration":"","season":"Fall 2025","type":"Anime","iframeSrc":"https://my.1anime.site/index.php?action=play&file=One_Punch_Man_3_Episode_10_1765726547.mp4"}
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
         * title : One-Punch Man Season 3
         * alter : One-Punch Man Season 3, One Punch Man 3rd Season, OPM 3, ワンパンマン 3
         * image : https://i0.wp.com/9anime.me.uk/wp-content/uploads/2025/10/1760294356-8520-148347.jpg?resize=246,350
         * rating : 7.14
         * status : Ongoing
         * studio : J.C.Staff
         * released : 2025
         * duration :
         * season : Fall 2025
         * type : Anime
         * iframeSrc : https://my.1anime.site/index.php?action=play&file=One_Punch_Man_3_Episode_10_1765726547.mp4
         */

        private String title;
        private String alter;
        private String image;
        private String rating;
        private String status;
        private String studio;
        private String released;
        private String duration;
        private String season;
        private String type;
        private String iframeSrc;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAlter() {
            return alter;
        }

        public void setAlter(String alter) {
            this.alter = alter;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getRating() {
            return rating;
        }

        public void setRating(String rating) {
            this.rating = rating;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getStudio() {
            return studio;
        }

        public void setStudio(String studio) {
            this.studio = studio;
        }

        public String getReleased() {
            return released;
        }

        public void setReleased(String released) {
            this.released = released;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getSeason() {
            return season;
        }

        public void setSeason(String season) {
            this.season = season;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getIframeSrc() {
            return iframeSrc;
        }

        public void setIframeSrc(String iframeSrc) {
            this.iframeSrc = iframeSrc;
        }
    }
}
