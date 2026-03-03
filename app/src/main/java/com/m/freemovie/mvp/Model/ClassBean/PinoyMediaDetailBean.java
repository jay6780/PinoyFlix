package com.m.freemovie.mvp.Model.ClassBean;

import java.util.List;

public class PinoyMediaDetailBean {
    /**
     * author : yazky
     * results : {"url":"https://pinoymoviepedia.ru/movies/baka-doon-sa-buwan/","title":"Baka Doon sa Buwan (2024) - Watch Full Pinoy Movies Online","originalTitle":"Baka Doon sa Buwan","synopsis":"Watch Baka Doon sa Buwan 2024 Full Movie Tagalog somewhere in space 2024 An eight-year-old half-Filipina and half-African American living with her mentally unstable mother grows up believing that her father has been absent from their lives because he is on the moon.Baka Doon sa Buwan 2024 Free Download Link Streaming Online HD","images":[{"full":"https://image.tmdb.org/t/p/original/g0ump5v70kxABfsS7Kq4bXa8XKa.jpg","thumb":"https://image.tmdb.org/t/p/w300/g0ump5v70kxABfsS7Kq4bXa8XKa.jpg","alt":"Baka Doon sa Buwan (2024)"}],"director":[{"name":"Noah Tonga","profileUrl":"https://pinoymoviepedia.ru/director/noah-tonga/","image":"https://pinoymoviepedia.ru/wp-content/themes/dooplay/assets/img/no/cast.png"}],"cast":[{"name":"Aaliyah Marciano","profileUrl":"https://pinoymoviepedia.ru/cast/aaliyah-marciano/","image":"https://pinoymoviepedia.ru/wp-content/themes/dooplay/assets/img/no/cast.png"},{"name":"Kate Alejandrino","profileUrl":"https://pinoymoviepedia.ru/cast/kate-alejandrino/","image":"https://image.tmdb.org/t/p/w92/eLKEXySsICRWUXD0c3S6gSzRUpq.jpg"},{"name":"Mimi Felicia","profileUrl":"https://pinoymoviepedia.ru/cast/mimi-felicia/","image":"https://pinoymoviepedia.ru/wp-content/themes/dooplay/assets/img/no/cast.png"},{"name":"Jed Cuestas","profileUrl":"https://pinoymoviepedia.ru/cast/jed-cuestas/","image":"https://pinoymoviepedia.ru/wp-content/themes/dooplay/assets/img/no/cast.png"}],"embedUrls":["https://voe.sx/e/onfjftt7r6wb","https://abstream.to/embed/atdlqxhnyq23","https://myvidplay.com/e/dzqgkw26y3qx"]}
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
         * url : https://pinoymoviepedia.ru/movies/baka-doon-sa-buwan/
         * title : Baka Doon sa Buwan (2024) - Watch Full Pinoy Movies Online
         * originalTitle : Baka Doon sa Buwan
         * synopsis : Watch Baka Doon sa Buwan 2024 Full Movie Tagalog somewhere in space 2024 An eight-year-old half-Filipina and half-African American living with her mentally unstable mother grows up believing that her father has been absent from their lives because he is on the moon.Baka Doon sa Buwan 2024 Free Download Link Streaming Online HD
         * images : [{"full":"https://image.tmdb.org/t/p/original/g0ump5v70kxABfsS7Kq4bXa8XKa.jpg","thumb":"https://image.tmdb.org/t/p/w300/g0ump5v70kxABfsS7Kq4bXa8XKa.jpg","alt":"Baka Doon sa Buwan (2024)"}]
         * director : [{"name":"Noah Tonga","profileUrl":"https://pinoymoviepedia.ru/director/noah-tonga/","image":"https://pinoymoviepedia.ru/wp-content/themes/dooplay/assets/img/no/cast.png"}]
         * cast : [{"name":"Aaliyah Marciano","profileUrl":"https://pinoymoviepedia.ru/cast/aaliyah-marciano/","image":"https://pinoymoviepedia.ru/wp-content/themes/dooplay/assets/img/no/cast.png"},{"name":"Kate Alejandrino","profileUrl":"https://pinoymoviepedia.ru/cast/kate-alejandrino/","image":"https://image.tmdb.org/t/p/w92/eLKEXySsICRWUXD0c3S6gSzRUpq.jpg"},{"name":"Mimi Felicia","profileUrl":"https://pinoymoviepedia.ru/cast/mimi-felicia/","image":"https://pinoymoviepedia.ru/wp-content/themes/dooplay/assets/img/no/cast.png"},{"name":"Jed Cuestas","profileUrl":"https://pinoymoviepedia.ru/cast/jed-cuestas/","image":"https://pinoymoviepedia.ru/wp-content/themes/dooplay/assets/img/no/cast.png"}]
         * embedUrls : ["https://voe.sx/e/onfjftt7r6wb","https://abstream.to/embed/atdlqxhnyq23","https://myvidplay.com/e/dzqgkw26y3qx"]
         */

        private String url;
        private String title;
        private String originalTitle;
        private String synopsis;
        private List<ImagesBean> images;
        private List<DirectorBean> director;
        private List<CastBean> cast;
        private List<String> embedUrls;

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

        public String getOriginalTitle() {
            return originalTitle;
        }

        public void setOriginalTitle(String originalTitle) {
            this.originalTitle = originalTitle;
        }

        public String getSynopsis() {
            return synopsis;
        }

        public void setSynopsis(String synopsis) {
            this.synopsis = synopsis;
        }

        public List<ImagesBean> getImages() {
            return images;
        }

        public void setImages(List<ImagesBean> images) {
            this.images = images;
        }

        public List<DirectorBean> getDirector() {
            return director;
        }

        public void setDirector(List<DirectorBean> director) {
            this.director = director;
        }

        public List<CastBean> getCast() {
            return cast;
        }

        public void setCast(List<CastBean> cast) {
            this.cast = cast;
        }

        public List<String> getEmbedUrls() {
            return embedUrls;
        }

        public void setEmbedUrls(List<String> embedUrls) {
            this.embedUrls = embedUrls;
        }

        public static class ImagesBean {
            /**
             * full : https://image.tmdb.org/t/p/original/g0ump5v70kxABfsS7Kq4bXa8XKa.jpg
             * thumb : https://image.tmdb.org/t/p/w300/g0ump5v70kxABfsS7Kq4bXa8XKa.jpg
             * alt : Baka Doon sa Buwan (2024)
             */

            private String full;
            private String thumb;
            private String alt;

            public String getFull() {
                return full;
            }

            public void setFull(String full) {
                this.full = full;
            }

            public String getThumb() {
                return thumb;
            }

            public void setThumb(String thumb) {
                this.thumb = thumb;
            }

            public String getAlt() {
                return alt;
            }

            public void setAlt(String alt) {
                this.alt = alt;
            }
        }

        public static class DirectorBean {
            /**
             * name : Noah Tonga
             * profileUrl : https://pinoymoviepedia.ru/director/noah-tonga/
             * image : https://pinoymoviepedia.ru/wp-content/themes/dooplay/assets/img/no/cast.png
             */

            private String name;
            private String profileUrl;
            private String image;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getProfileUrl() {
                return profileUrl;
            }

            public void setProfileUrl(String profileUrl) {
                this.profileUrl = profileUrl;
            }

            public String getImage() {
                return image;
            }

            public void setImage(String image) {
                this.image = image;
            }
        }

        public static class CastBean {
            /**
             * name : Aaliyah Marciano
             * profileUrl : https://pinoymoviepedia.ru/cast/aaliyah-marciano/
             * image : https://pinoymoviepedia.ru/wp-content/themes/dooplay/assets/img/no/cast.png
             */

            private String name;
            private String profileUrl;
            private String image;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getProfileUrl() {
                return profileUrl;
            }

            public void setProfileUrl(String profileUrl) {
                this.profileUrl = profileUrl;
            }

            public String getImage() {
                return image;
            }

            public void setImage(String image) {
                this.image = image;
            }
        }
    }
}
