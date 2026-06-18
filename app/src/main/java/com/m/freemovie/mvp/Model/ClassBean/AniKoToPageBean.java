package com.m.freemovie.mvp.Model.ClassBean;

import java.util.List;

public class AniKoToPageBean {


    /**
     * status : true
     * page : 1
     * total : 12
     * results : [{"animeId":"8274","title":"Reborn as a Cat","japaneseTitle":"Wei Miao Rensheng","url":"https://anikoto.cz/watch/reborn-as-a-cat-cnbc3/ep-27","thumbnail":"https://cdn.anipixcdn.co/thumbnail/fce1eab4411d5df240d93ff334f15385.jpg","episode":"34","dubEpisode":null,"totalEpisodes":"36","type":"ONA"},{"animeId":"8687","title":"The Beginning After the End Season 2","japaneseTitle":"Saikyou no Ousama, Nidome no Jinsei wa Nani wo Suru? Season 2","url":"https://anikoto.cz/watch/the-beginning-after-the-end-season-2-af0mf/ep-11","thumbnail":"https://cdn.anipixcdn.co/thumbnail/286b0b3ea509af1aeff6bb47299d96d7.jpg","episode":"11","dubEpisode":"10","totalEpisodes":"12","type":"TV"},{"animeId":"8316","title":"Tamon's B-Side","japaneseTitle":"Tamon-kun Ima Docchi!?","url":"https://anikoto.cz/watch/tamon-s-b-side-fuove/ep-13","thumbnail":"https://cdn.anipixcdn.co/thumbnail/8aeea62fd2f5ce7bd80c28c5531d793f.jpg","episode":"13","dubEpisode":"7","totalEpisodes":"13","type":"TV"},{"animeId":"8686","title":"Go For It, Nakamura!","japaneseTitle":"Ganbare! Nakamura-kun!!","url":"https://anikoto.cz/watch/go-for-it-nakamura-3qydv/ep-13","thumbnail":"https://cdn.anipixcdn.co/thumbnail/8ca22b09aeb179a42c434b12b381c692.jpg","episode":"13","dubEpisode":"13","totalEpisodes":"13","type":"TV"},{"animeId":"8683","title":"Always a Catch!, The Fish I Missed Was Big","japaneseTitle":"Nigashita Sakana wa Ookikatta ga Tsuriageta Sakana ga Ookisugita Ken","url":"https://anikoto.cz/watch/always-a-catch-the-fish-i-missed-was-big-s9jtc/ep-12","thumbnail":"https://cdn.anipixcdn.co/thumbnail/4dbf24aefdccf24bd66404a1e29b870d.jpg","episode":"12","dubEpisode":null,"totalEpisodes":"12","type":"TV"},{"animeId":"8763","title":"Candy Caries","japaneseTitle":"Candy Caries","url":"https://anikoto.cz/watch/candy-caries-vm1jn/ep-10","thumbnail":"https://cdn.anipixcdn.co/thumbnail/92c2425736b1065fa04616737b9e41b5.webp","episode":"10","dubEpisode":null,"totalEpisodes":"24","type":"TV"},{"animeId":"8732","title":"Gals Can't Be Kind to Otaku!?","japaneseTitle":"Otaku ni Yasashii Gal wa Inai!?","url":"https://anikoto.cz/watch/gals-can-t-be-kind-to-otaku-whjvd/ep-11","thumbnail":"https://cdn.anipixcdn.co/thumbnail/b6bcdc5176f139f9c4c0036b123ee12d.jpg","episode":"11","dubEpisode":null,"totalEpisodes":"12","type":"TV"},{"animeId":"8685","title":"Reborn as a Vending Machine, I Now Wander the Dungeon Season 3","japaneseTitle":"Jidou Hanbaiki ni Umarekawatta Ore wa Meikyuu wo Samayou 3rd Season","url":"https://anikoto.cz/watch/reborn-as-a-vending-machine-i-now-wander-the-dungeon-season-3-4iet4/ep-11","thumbnail":"https://cdn.anipixcdn.co/thumbnail/bb6d7d30819268d8c124728d77d544cd.jpg","episode":"11","dubEpisode":"10","totalEpisodes":"12","type":"TV"},{"animeId":"8738","title":"Re:ZERO -Starting Life in Another World- Season 4","japaneseTitle":"Re:Zero kara Hajimeru Isekai Seikatsu 4th Season","url":"https://anikoto.cz/watch/re-zero-starting-life-in-another-world-season-4-4hk9h/ep-11","thumbnail":"https://cdn.anipixcdn.co/thumbnail/72a7dd9f91089b8fabd3edc4c9db7ed1.jpg","episode":"11","dubEpisode":"11","totalEpisodes":"19","type":"TV"},{"animeId":"8734","title":"Rent-a-Girlfriend Season 5","japaneseTitle":"Kanojo, Okarishimasu 5th Season","url":"https://anikoto.cz/watch/rent-a-girlfriend-season-5-kdefb/ep-11","thumbnail":"https://cdn.anipixcdn.co/thumbnail/24f5f1b33c54fc7383dcb331a82c259d.jpg","episode":"11","dubEpisode":"9","totalEpisodes":"12","type":"TV"},{"animeId":"8689","title":"Classroom of the Elite IV","japaneseTitle":"Youkoso Jitsuryoku Shijou Shugi no Kyoushitsu e 4th Season: 2-nensei-hen 1 Gakki","url":"https://anikoto.cz/watch/classroom-of-the-elite-iv-rzzt2/ep-15","thumbnail":"https://cdn.anipixcdn.co/thumbnail/596a5705f4d9c0867ea0aba3be5db567.jpg","episode":"15","dubEpisode":"13","totalEpisodes":"16","type":"TV"},{"animeId":"8237","title":"Ugoku! Neko Mukashibanashi","japaneseTitle":"Ugoku! Neko Mukashibanashi","url":"https://anikoto.cz/watch/ugoku-neko-mukashibanashi-axqrf/ep-36","thumbnail":"https://cdn.anipixcdn.co/thumbnail/898b5da2c62fa1cc9fc1e5c8f204f41f.jpg","episode":"36","dubEpisode":null,"totalEpisodes":"50","type":"ONA"}]
     */

    private boolean status;
    private int page;
    private int total;
    private List<ResultsBean> results;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<ResultsBean> getResults() {
        return results;
    }

    public void setResults(List<ResultsBean> results) {
        this.results = results;
    }

    public static class ResultsBean {
        /**
         * animeId : 8274
         * title : Reborn as a Cat
         * japaneseTitle : Wei Miao Rensheng
         * url : https://anikoto.cz/watch/reborn-as-a-cat-cnbc3/ep-27
         * thumbnail : https://cdn.anipixcdn.co/thumbnail/fce1eab4411d5df240d93ff334f15385.jpg
         * episode : 34
         * dubEpisode : null
         * totalEpisodes : 36
         * type : ONA
         */

        private String animeId;
        private String title;
        private String japaneseTitle;
        private String url;
        private String thumbnail;
        private String episode;
        private Object dubEpisode;
        private String totalEpisodes;
        private String type;

        public String getAnimeId() {
            return animeId;
        }

        public void setAnimeId(String animeId) {
            this.animeId = animeId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getJapaneseTitle() {
            return japaneseTitle;
        }

        public void setJapaneseTitle(String japaneseTitle) {
            this.japaneseTitle = japaneseTitle;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }

        public String getEpisode() {
            return episode;
        }

        public void setEpisode(String episode) {
            this.episode = episode;
        }

        public Object getDubEpisode() {
            return dubEpisode;
        }

        public void setDubEpisode(Object dubEpisode) {
            this.dubEpisode = dubEpisode;
        }

        public String getTotalEpisodes() {
            return totalEpisodes;
        }

        public void setTotalEpisodes(String totalEpisodes) {
            this.totalEpisodes = totalEpisodes;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
