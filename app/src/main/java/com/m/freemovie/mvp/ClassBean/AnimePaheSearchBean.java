package com.m.freemovie.mvp.ClassBean;

import java.util.List;

public class AnimePaheSearchBean {


    /**
     * author : yazky
     * results : {"total":139,"per_page":8,"current_page":1,"last_page":18,"from":1,"to":8,"data":[{"id":5201,"title":"The Dangers in My Heart","type":"TV","episodes":12,"status":"Finished Airing","season":"Spring","year":2023,"score":8.21,"poster":"https://i.animepahe.si/posters/ccdb7ad1d6ebfbebf55949a2fdf7a0ade8304d6b662c2a80e0b515e1ea511671.jpg","session":"d0784446-5683-4d3c-1415-0ce1c8e7d678"},{"id":5426,"title":"The Dangers in My Heart Season 2","type":"TV","episodes":13,"status":"Finished Airing","season":"Winter","year":2024,"score":8.71,"poster":"https://i.animepahe.si/posters/ec72e6248df26cca27f5b0e22744b9a1cdb62e4ea2045709ec4539335983a8b9.jpg","session":"86a84da0-c042-a393-1f94-bb705b14d55d"},{"id":5474,"title":"The Dangers in My Heart: Twi-Yaba","type":"ONA","episodes":1,"status":"Finished Airing","season":"Winter","year":2024,"score":7.67,"poster":"https://i.animepahe.si/posters/4b8ae868ef3af878509e72e44b7f6089225e96079c03a6d9e1c6b089dfd8d00f.jpg","session":"0ed6edc6-bd0b-6083-8ca4-ee73760703d0"},{"id":4814,"title":"My Isekai Life: I Gained a Second Character Class and Became the Strongest Sage in the World","type":"TV","episodes":12,"status":"Finished Airing","season":"Summer","year":2022,"score":6.32,"poster":"https://i.animepahe.si/posters/f371c6cab345923b694a71cee642931af0ad38a54ae31e2cea49f66ea0967151.jpg","session":"b8a7da74-150f-e76b-287b-6341e2fa50ff"},{"id":770,"title":"Mai-Otome","type":"TV","episodes":26,"status":"Finished Airing","season":"Fall","year":2005,"score":7.27,"poster":"https://i.animepahe.si/posters/fe32f99ccf81e4946469c56c02459f2916e21ab132138c6a82f714085d4246c8.jpg","session":"491ee4bf-2f2a-80eb-c684-41a5a078ed47"},{"id":5212,"title":"My One-Hit Kill Sister","type":"TV","episodes":12,"status":"Finished Airing","season":"Spring","year":2023,"score":6.3,"poster":"https://i.animepahe.si/posters/8fab0a71036c007999cdfd05fee29a9f0c05072c13f8f0fa8ed052abd04c35b4.jpg","session":"2559a932-ee55-426d-50c2-07f513d949f9"},{"id":1418,"title":"Ao Oni The Animation","type":"TV","episodes":13,"status":"Finished Airing","season":"Fall","year":2016,"score":5.11,"poster":"https://i.animepahe.si/posters/ccf13bdb9ff20dbd6c3c626afa3c1003f4692d5517f0f27c6c72b8e3bb47972e.jpg","session":"6649c03c-533a-408f-cc05-f4c10a27e7b5"},{"id":44,"title":"Dragon Ball Super","type":"TV","episodes":131,"status":"Finished Airing","season":"Summer","year":2015,"score":7.47,"poster":"https://i.animepahe.si/posters/ef832b4f00f29f90a50821c3b7743225b2a34b5dd3d4290f31b1bf1c4a5e74de.jpg","session":"a5144c3b-804d-8faf-f2d1-dd318376777b"}]}
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
         * total : 139
         * per_page : 8
         * current_page : 1
         * last_page : 18
         * from : 1
         * to : 8
         * data : [{"id":5201,"title":"The Dangers in My Heart","type":"TV","episodes":12,"status":"Finished Airing","season":"Spring","year":2023,"score":8.21,"poster":"https://i.animepahe.si/posters/ccdb7ad1d6ebfbebf55949a2fdf7a0ade8304d6b662c2a80e0b515e1ea511671.jpg","session":"d0784446-5683-4d3c-1415-0ce1c8e7d678"},{"id":5426,"title":"The Dangers in My Heart Season 2","type":"TV","episodes":13,"status":"Finished Airing","season":"Winter","year":2024,"score":8.71,"poster":"https://i.animepahe.si/posters/ec72e6248df26cca27f5b0e22744b9a1cdb62e4ea2045709ec4539335983a8b9.jpg","session":"86a84da0-c042-a393-1f94-bb705b14d55d"},{"id":5474,"title":"The Dangers in My Heart: Twi-Yaba","type":"ONA","episodes":1,"status":"Finished Airing","season":"Winter","year":2024,"score":7.67,"poster":"https://i.animepahe.si/posters/4b8ae868ef3af878509e72e44b7f6089225e96079c03a6d9e1c6b089dfd8d00f.jpg","session":"0ed6edc6-bd0b-6083-8ca4-ee73760703d0"},{"id":4814,"title":"My Isekai Life: I Gained a Second Character Class and Became the Strongest Sage in the World","type":"TV","episodes":12,"status":"Finished Airing","season":"Summer","year":2022,"score":6.32,"poster":"https://i.animepahe.si/posters/f371c6cab345923b694a71cee642931af0ad38a54ae31e2cea49f66ea0967151.jpg","session":"b8a7da74-150f-e76b-287b-6341e2fa50ff"},{"id":770,"title":"Mai-Otome","type":"TV","episodes":26,"status":"Finished Airing","season":"Fall","year":2005,"score":7.27,"poster":"https://i.animepahe.si/posters/fe32f99ccf81e4946469c56c02459f2916e21ab132138c6a82f714085d4246c8.jpg","session":"491ee4bf-2f2a-80eb-c684-41a5a078ed47"},{"id":5212,"title":"My One-Hit Kill Sister","type":"TV","episodes":12,"status":"Finished Airing","season":"Spring","year":2023,"score":6.3,"poster":"https://i.animepahe.si/posters/8fab0a71036c007999cdfd05fee29a9f0c05072c13f8f0fa8ed052abd04c35b4.jpg","session":"2559a932-ee55-426d-50c2-07f513d949f9"},{"id":1418,"title":"Ao Oni The Animation","type":"TV","episodes":13,"status":"Finished Airing","season":"Fall","year":2016,"score":5.11,"poster":"https://i.animepahe.si/posters/ccf13bdb9ff20dbd6c3c626afa3c1003f4692d5517f0f27c6c72b8e3bb47972e.jpg","session":"6649c03c-533a-408f-cc05-f4c10a27e7b5"},{"id":44,"title":"Dragon Ball Super","type":"TV","episodes":131,"status":"Finished Airing","season":"Summer","year":2015,"score":7.47,"poster":"https://i.animepahe.si/posters/ef832b4f00f29f90a50821c3b7743225b2a34b5dd3d4290f31b1bf1c4a5e74de.jpg","session":"a5144c3b-804d-8faf-f2d1-dd318376777b"}]
         */

        private int total;
        private int per_page;
        private int current_page;
        private int last_page;
        private int from;
        private int to;
        private List<DataBean> data;

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
             * id : 5201
             * title : The Dangers in My Heart
             * type : TV
             * episodes : 12
             * status : Finished Airing
             * season : Spring
             * year : 2023
             * score : 8.21
             * poster : https://i.animepahe.si/posters/ccdb7ad1d6ebfbebf55949a2fdf7a0ade8304d6b662c2a80e0b515e1ea511671.jpg
             * session : d0784446-5683-4d3c-1415-0ce1c8e7d678
             */

            private int id;
            private String title;
            private String type;
            private int episodes;
            private String status;
            private String season;
            private int year;
            private double score;
            private String poster;
            private String session;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public int getEpisodes() {
                return episodes;
            }

            public void setEpisodes(int episodes) {
                this.episodes = episodes;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getSeason() {
                return season;
            }

            public void setSeason(String season) {
                this.season = season;
            }

            public int getYear() {
                return year;
            }

            public void setYear(int year) {
                this.year = year;
            }

            public double getScore() {
                return score;
            }

            public void setScore(double score) {
                this.score = score;
            }

            public String getPoster() {
                return poster;
            }

            public void setPoster(String poster) {
                this.poster = poster;
            }

            public String getSession() {
                return session;
            }

            public void setSession(String session) {
                this.session = session;
            }
        }
    }
}
