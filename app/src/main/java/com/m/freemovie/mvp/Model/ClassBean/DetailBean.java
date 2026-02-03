package com.m.freemovie.mvp.Model.ClassBean;

import java.util.List;

public class DetailBean {

    private String videoId;
    private String timeStamp;
    private String tempImage;
    private String movieName;
    private String isMovie;
    public DetailBean() {
    }

    public DetailBean(String videoId,String timeStamp,String tempImage,String movieName,String isMovie){
        this.videoId = videoId;
        this.timeStamp = timeStamp;
        this.tempImage = tempImage;
        this.movieName = movieName;
        this.isMovie = isMovie;

    }

    public void setMovie(String movie) {
        isMovie = movie;
    }

    public String getIsMovie() {
        return isMovie;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getTempImage() {
        return tempImage;
    }

    public void setTempImage(String tempImage) {
        this.tempImage = tempImage;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    /**
     * adult : false
     * backdrop_path : /9n2tJBplPbgR2ca05hS5CKXwP2c.jpg
     * belongs_to_collection : {"id":1434561,"name":"The Super Mario Collection","poster_path":"/8wVpJ4DCEVP6Ima1MgKgymZbYcz.jpg","backdrop_path":"/hX6DycbhqpkHuYD5ia4YeQQ2RgV.jpg"}
     * budget : 100000000
     * genres : [{"id":10751,"name":"Family"},{"id":35,"name":"Comedy"},{"id":12,"name":"Adventure"},{"id":16,"name":"Animation"},{"id":14,"name":"Fantasy"}]
     * homepage : https://www.uphe.com/movies/the-super-mario-bros-movie
     * id : 502356
     * imdb_id : tt6718170
     * origin_country : ["US"]
     * original_language : en
     * original_title : The Super Mario Bros. Movie
     * overview : While working underground to fix a water main, Brooklyn plumbers—and brothers—Mario and Luigi are transported down a mysterious pipe and wander into a magical new world. But when the brothers are separated, Mario embarks on an epic quest to find Luigi.
     * popularity : 17.4323
     * poster_path : /qNBAXBIQlnOThrVvA6mA2B5ggV6.jpg
     * production_companies : [{"id":33,"logo_path":"/8lvHyhjr8oUKOOy2dKXoALWKdp0.png","name":"Universal Pictures","origin_country":"US"},{"id":6704,"logo_path":"/xte4JnhtBJfHSR5jqtHQwvyLgk0.png","name":"Illumination","origin_country":"US"},{"id":12288,"logo_path":"/e4dQAqZD374H5EuM0W1ljEBWTKy.png","name":"Nintendo","origin_country":"JP"}]
     * production_countries : [{"iso_3166_1":"JP","name":"Japan"},{"iso_3166_1":"US","name":"United States of America"}]
     * release_date : 2023-04-05
     * revenue : 1362000000
     * runtime : 93
     * spoken_languages : [{"english_name":"English","iso_639_1":"en","name":"English"}]
     * status : Released
     * tagline : Not all heroes wear capes. Some wear overalls.
     * title : The Super Mario Bros. Movie
     * video : false
     * vote_average : 7.605
     * vote_count : 10050
     */
    private String name;
    private boolean adult;
    private String backdrop_path;
    private BelongsToCollectionBean belongs_to_collection;
    private int budget;
    private String homepage;
    private int id;
    private String imdb_id;
    private String original_language;
    private String original_title;
    private String overview;
    private double popularity;
    private String poster_path;
    private String release_date;
    private int revenue;
    private int runtime;
    private String status;
    private String tagline;
    private String title;
    private boolean video;
    private double vote_average;
    private int vote_count;
    private List<GenresBean> genres;
    private List<String> origin_country;
    private List<ProductionCompaniesBean> production_companies;
    private List<ProductionCountriesBean> production_countries;
    private List<SpokenLanguagesBean> spoken_languages;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public BelongsToCollectionBean getBelongs_to_collection() {
        return belongs_to_collection;
    }

    public void setBelongs_to_collection(BelongsToCollectionBean belongs_to_collection) {
        this.belongs_to_collection = belongs_to_collection;
    }

    public int getBudget() {
        return budget;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImdb_id() {
        return imdb_id;
    }

    public void setImdb_id(String imdb_id) {
        this.imdb_id = imdb_id;
    }

    public String getOriginal_language() {
        return original_language;
    }

    public void setOriginal_language(String original_language) {
        this.original_language = original_language;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public int getRevenue() {
        return revenue;
    }

    public void setRevenue(int revenue) {
        this.revenue = revenue;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public double getVote_average() {
        return vote_average;
    }

    public void setVote_average(double vote_average) {
        this.vote_average = vote_average;
    }

    public int getVote_count() {
        return vote_count;
    }

    public void setVote_count(int vote_count) {
        this.vote_count = vote_count;
    }

    public List<GenresBean> getGenres() {
        return genres;
    }

    public void setGenres(List<GenresBean> genres) {
        this.genres = genres;
    }

    public List<String> getOrigin_country() {
        return origin_country;
    }

    public void setOrigin_country(List<String> origin_country) {
        this.origin_country = origin_country;
    }

    public List<ProductionCompaniesBean> getProduction_companies() {
        return production_companies;
    }

    public void setProduction_companies(List<ProductionCompaniesBean> production_companies) {
        this.production_companies = production_companies;
    }

    public List<ProductionCountriesBean> getProduction_countries() {
        return production_countries;
    }

    public void setProduction_countries(List<ProductionCountriesBean> production_countries) {
        this.production_countries = production_countries;
    }

    public List<SpokenLanguagesBean> getSpoken_languages() {
        return spoken_languages;
    }

    public void setSpoken_languages(List<SpokenLanguagesBean> spoken_languages) {
        this.spoken_languages = spoken_languages;
    }

    public static class BelongsToCollectionBean {
        /**
         * id : 1434561
         * name : The Super Mario Collection
         * poster_path : /8wVpJ4DCEVP6Ima1MgKgymZbYcz.jpg
         * backdrop_path : /hX6DycbhqpkHuYD5ia4YeQQ2RgV.jpg
         */

        private int id;
        private String name;
        private String poster_path;
        private String backdrop_path;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPoster_path() {
            return poster_path;
        }

        public void setPoster_path(String poster_path) {
            this.poster_path = poster_path;
        }

        public String getBackdrop_path() {
            return backdrop_path;
        }

        public void setBackdrop_path(String backdrop_path) {
            this.backdrop_path = backdrop_path;
        }
    }

    public static class GenresBean {
        /**
         * id : 10751
         * name : Family
         */

        private int id;
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class ProductionCompaniesBean {
        /**
         * id : 33
         * logo_path : /8lvHyhjr8oUKOOy2dKXoALWKdp0.png
         * name : Universal Pictures
         * origin_country : US
         */

        private int id;
        private String logo_path;
        private String name;
        private String origin_country;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getLogo_path() {
            return logo_path;
        }

        public void setLogo_path(String logo_path) {
            this.logo_path = logo_path;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getOrigin_country() {
            return origin_country;
        }

        public void setOrigin_country(String origin_country) {
            this.origin_country = origin_country;
        }
    }

    public static class ProductionCountriesBean {
        /**
         * iso_3166_1 : JP
         * name : Japan
         */

        private String iso_3166_1;
        private String name;

        public String getIso_3166_1() {
            return iso_3166_1;
        }

        public void setIso_3166_1(String iso_3166_1) {
            this.iso_3166_1 = iso_3166_1;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class SpokenLanguagesBean {
        /**
         * english_name : English
         * iso_639_1 : en
         * name : English
         */

        private String english_name;
        private String iso_639_1;
        private String name;

        public String getEnglish_name() {
            return english_name;
        }

        public void setEnglish_name(String english_name) {
            this.english_name = english_name;
        }

        public String getIso_639_1() {
            return iso_639_1;
        }

        public void setIso_639_1(String iso_639_1) {
            this.iso_639_1 = iso_639_1;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
