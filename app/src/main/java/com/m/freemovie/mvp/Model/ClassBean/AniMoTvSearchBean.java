package com.m.freemovie.mvp.Model.ClassBean;

import java.util.List;

public class AniMoTvSearchBean {

    /**
     * success : true
     * query : One piece
     * page : 1
     * count : 10
     * results : [{"title":"ONE PIECE FAN LETTER","url":"https://animotvslash.org/anime/one-piece-fan-letter/","image":"https://i2.wp.com/animotvslash.org/wp-content/uploads/2026/06/bx182469-JQ808NBPxmgn.jpg","type":"Special","status":"Completed","sub":"Sub"},{"title":"ONE PIECE HEROINES","url":"https://animotvslash.org/anime/one-piece-heroines-episode-nami/","image":"https://i3.wp.com/animotvslash.org/wp-content/uploads/2026/06/627516382_18535217143071632_4100816962873863825_n.jpg","type":"Special","status":"Completed","sub":"Sub"},{"title":"One Piece","url":"https://animotvslash.org/anime/one-piece/","image":"https://i3.wp.com/animotvslash.org/wp-content/uploads/2025/10/large-27.webp","type":"TV","status":"Ongoing","sub":"Sub"},{"title":"One Piece Film: Red","url":"https://animotvslash.org/anime/one-piece-film-red/","image":"https://i2.wp.com/animotvslash.org/wp-content/uploads/2026/05/bx141902-fTyoTk8F8qOl.jpg","type":"Movie","status":"Completed","sub":null},{"title":"One Piece: Stampede","url":"https://animotvslash.org/anime/one-piece-stampede/","image":"https://i1.wp.com/animotvslash.org/wp-content/uploads/2026/05/bx105143-5uBDmhvMr6At.png","type":"Movie","status":"Completed","sub":"Sub"},{"title":"One Piece Film: Gold","url":"https://animotvslash.org/anime/one-piece-film-gold/","image":"https://i0.wp.com/animotvslash.org/wp-content/uploads/2026/05/nx21335-XsXdE0AeOkkZ.jpg","type":"Movie","status":"Completed","sub":"Sub"},{"title":"One Piece Film: Z","url":"https://animotvslash.org/anime/one-piece-film-z/","image":"https://i0.wp.com/animotvslash.org/wp-content/uploads/2026/05/bx12859-uQFENDPzMWz6.jpg","type":"Movie","status":"Completed","sub":null},{"title":"One Piece Film: Strong World","url":"https://animotvslash.org/anime/one-piece-film-strong-world/","image":"https://i1.wp.com/animotvslash.org/wp-content/uploads/2026/05/bx4155-P5TDf6t6qFwX.png","type":"Movie","status":"Completed","sub":"Sub"},{"title":"One Piece: Episode Of Chopper +: The Miracle Winter Cherry Blossom","url":"https://animotvslash.org/anime/one-piece-episode-of-chopper-the-miracle-winter-cherry-blossom/","image":"https://i2.wp.com/animotvslash.org/wp-content/uploads/2026/05/bx3848-SCnYGTn34Llt.jpg","type":"Movie","status":"Completed","sub":"Sub"},{"title":"One Piece: Mega Mecha Soldier of Karakuri Castle","url":"https://animotvslash.org/anime/one-piece-mega-mecha-soldier-of-karakuri-castle/","image":"https://i3.wp.com/animotvslash.org/wp-content/uploads/2026/05/bx465-qSRr0MKYhS0I.jpg","type":"Movie","status":"Completed","sub":"Sub"}]
     */

    private boolean success;
    private String query;
    private int page;
    private int count;
    private List<ResultsBean> results;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
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
         * title : ONE PIECE FAN LETTER
         * url : https://animotvslash.org/anime/one-piece-fan-letter/
         * image : https://i2.wp.com/animotvslash.org/wp-content/uploads/2026/06/bx182469-JQ808NBPxmgn.jpg
         * type : Special
         * status : Completed
         * sub : Sub
         */

        private String title;
        private String url;
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

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
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
