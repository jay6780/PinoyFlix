package com.m.freemovie.mvp.ClassBean;

import java.util.List;

public class RevivalSearchBean {

    /**
     * author : yazky
     * results : [{"title":"Hunter × Hunter 1999 Tagalog","link":"https://animerevival.xyz/tvshows/hunter-x-hunter-1999-tagalog/","image":"https://image.tmdb.org/t/p/w92/bQ325mb0gvcksV2YbDVIYHi7RNQ.jpg","type":"TV","rating":"IMDb 8.309","year":"1999","description":"Gon Freecss discovers that the father he had always been told was dead was actually alive the whole time. Ging is a famous Hunter: an individual who has proven themself an elite member of ..."},{"title":"Hunter × Hunter: Phantom Rouge Tagalog","link":"https://animerevival.xyz/movies/hunter-x-hunter-phantom-rouge-tagalog/","image":"https://image.tmdb.org/t/p/w92/oaBGVerJHg59xKfltHI2wrNb2CZ.jpg","type":"Movie","rating":"","year":"2013","description":"Kurapika became a Hunter to take vengeance on the Class-A crime group Phantom Troupe who massacred his clan for their unique eyes. The eyes of the Kurta clan turn scarlet in times of anger or ..."},{"title":"Hunter × Hunter: The Last Mission Tagalog","link":"https://animerevival.xyz/movies/hunter-x-hunter-the-last-mission-tagalog/","image":"https://image.tmdb.org/t/p/w92/qfjGtlrMynBlkcLgNeziDTmm1M4.jpg","type":"Movie","rating":"","year":"2013","description":"The strongest Hunters that once existed in the Hunters Association were split into \u201clight\u201d and \u201cdark\u201d, and each walked down their respective paths. The \u201cdark\u201d ..."},{"title":"Hunter X Hunter 1999","link":"https://animerevival.xyz/tvshows/hunter-x-hunter-1999/","image":"https://image.tmdb.org/t/p/w92/hr5W8jpEP2DyOM4DvRwz5hqpl5I.jpg","type":"TV","rating":"IMDb 7.8","year":"1999","description":"Hunter × Hunter is a 1999 television series and is part of the Hunter × Hunter media franchise. The story focuses on a young boy named Gon Freecss, who one day discovers that the father he had ..."},{"title":"Hunter x Hunter 2011","link":"https://animerevival.xyz/tvshows/hunter-x-hunter-2011/","image":"https://image.tmdb.org/t/p/w92/tolQj5yffSxkEGXusPwNcvYrbph.jpg","type":"TV","rating":"IMDb 8.5","year":"2011","description":"Twelve-year-old Gon Freecss one day discovers that the father he had always been told was dead was alive and well. His Father, Ging, is a Hunter\u2014a member of society\u2019s elite with a license ..."},{"title":"Devil May Cry Tagalog","link":"https://animerevival.xyz/tvshows/devil-may-cry-tagalog/","image":"https://image.tmdb.org/t/p/w92/xDGxMQk24gt4proydCyxddPzTIG.jpg","type":"TV","rating":"IMDb 7.559","year":"2007","description":"The adventures of the demon hunter Dante who himself is half demon and half human."},{"title":"Black Cat Tagalog","link":"https://animerevival.xyz/tvshows/black-cat-tagalog/","image":"https://image.tmdb.org/t/p/w92/hMEr2AdizxVMVEhGytbwev3uANu.jpg","type":"TV","rating":"IMDb 8.8","year":"2005","description":"The bounty hunter Sven is barely scraping by when he crosses paths with the Black Cat (a.k.a. Train Heartnet) and the young bio-weapon Eve. The three new companions will need more than luck to ..."},{"title":"Grimgar of Fantasy and Ash","link":"https://animerevival.xyz/tvshows/grimgar-of-fantasy-and-ash-tagalog/","image":"https://image.tmdb.org/t/p/w92/vDs4SVtbEcklxseEoeTV5B5U3n.jpg","type":"TV","rating":"IMDb 7.376","year":"2016","description":"Fear, survival, instinct. Thrown into a foreign land with nothing but hazy memories and the knowledge of their name, they can feel only these three emotions resonating deep within their souls. A ..."},{"title":"Lupin the Third: Dead or Alive Tagalog","link":"https://animerevival.xyz/movies/lupin-the-third-dead-or-alive-tagalog/","image":"https://image.tmdb.org/t/p/w92/qMcrxGNCFR2FxXLYhyEtNJeYfCO.jpg","type":"Movie","rating":"","year":"1996","description":"Lupin has set his sights on a the national treasure of the country of Zufu, placed for safe-keeping on a mysterious floating island by the country\u2019s late king. The island\u2019s ..."}]
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
         * title : Hunter × Hunter 1999 Tagalog
         * link : https://animerevival.xyz/tvshows/hunter-x-hunter-1999-tagalog/
         * image : https://image.tmdb.org/t/p/w92/bQ325mb0gvcksV2YbDVIYHi7RNQ.jpg
         * type : TV
         * rating : IMDb 8.309
         * year : 1999
         * description : Gon Freecss discovers that the father he had always been told was dead was actually alive the whole time. Ging is a famous Hunter: an individual who has proven themself an elite member of ...
         */

        private String title;
        private String link;
        private String image;
        private String type;
        private String rating;
        private String year;
        private String description;

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

        public String getRating() {
            return rating;
        }

        public void setRating(String rating) {
            this.rating = rating;
        }

        public String getYear() {
            return year;
        }

        public void setYear(String year) {
            this.year = year;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
