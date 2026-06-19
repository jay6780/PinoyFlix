package com.m.freemovie.mvp.Model.ClassBean;

import java.util.List;

public class AniNekoSearchBean {

    /**
     * author : yazky
     * success : true
     * results : [{"title":"Boruto: Naruto Next Generations","url":"https://anineko.to/watch/boruto-naruto-next-generations/","image":"https://cdn.anizara.store/cover/22b1f2e0983160db6f7bb9f62f4dbb39.webp","meta":"TV \u2022 293 Episodes"},{"title":"Boruto: Naruto the Movie","url":"https://anineko.to/watch/boruto-naruto-the-movie/","image":"https://cdn.anizara.store/cover/6b4e2b9376139fa09a68b94ec04dbe94.webp","meta":"Movie \u2022 1 Episodes"},{"title":"Boruto: Naruto the Movie - The Day Naruto Became the Hokage","url":"https://anineko.to/watch/boruto-naruto-the-movie-the-day-naruto-became-the-hokage/","image":"https://cdn.anizara.store/cover/c57daa0bc9c4d8e35a21e9a2801aecb2.webp","meta":"Special \u2022 1 Episodes"},{"title":"Naruto","url":"https://anineko.to/watch/naruto/","image":"https://cdn.anizara.store/cover/a02ffd91ece5e7efeb46db8f10a74059.webp","meta":"TV \u2022 220 Episodes"},{"title":"Naruto Movie 1: Ninja Clash in the Land of Snow","url":"https://anineko.to/watch/naruto-movie-1-ninja-clash-in-the-land-of-snow/","image":"https://cdn.anizara.store/cover/b9a8f4af85454f7c56c06f0a39e7ec23.webp","meta":"Movie \u2022 1 Episodes"},{"title":"Naruto Narutimate Hero 3: Tsuini Gekitotsu! Jounin vs. Genin!! Musabetsu Dairansen Taikai Kaisai!!","url":"https://anineko.to/watch/naruto-narutimate-hero-3-tsuini-gekitotsu-jounin-vs-genin-musabetsu-dairansen-taikai-kaisai/","image":"https://cdn.anizara.store/cover/15f99f2165aa8c86c9dface16fefd281.webp","meta":"OVA \u2022 1 Episodes"},{"title":"Naruto OVA2: The Lost Story - Mission: Protect the Waterfall Village","url":"https://anineko.to/watch/naruto-ova2-the-lost-story-mission-protect-the-waterfall-village/","image":"https://cdn.anizara.store/cover/9c4e6233c6d5ff637e7984152a3531d5.webp","meta":"Special \u2022 1 Episodes"},{"title":"Naruto OVA3: Hidden Leaf Village Grand Sports Festival","url":"https://anineko.to/watch/naruto-ova3-hidden-leaf-village-grand-sports-festival/","image":"https://cdn.anizara.store/cover/33866f3a2397f4b156ed5a31f5ba7964.webp","meta":"Special \u2022 1 Episodes"}]
     */

    private String author;
    private boolean success;
    private List<ResultsBean> results;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<ResultsBean> getResults() {
        return results;
    }

    public void setResults(List<ResultsBean> results) {
        this.results = results;
    }

    public static class ResultsBean {
        /**
         * title : Boruto: Naruto Next Generations
         * url : https://anineko.to/watch/boruto-naruto-next-generations/
         * image : https://cdn.anizara.store/cover/22b1f2e0983160db6f7bb9f62f4dbb39.webp
         * meta : TV • 293 Episodes
         */

        private String title;
        private String url;
        private String image;
        private String meta;

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

        public String getMeta() {
            return meta;
        }

        public void setMeta(String meta) {
            this.meta = meta;
        }
    }
}
