package com.m.freemovie.mvp.Model.ClassBean;

import java.util.List;

public class NineAnimeBean {

    /**
     * author : yazky
     * results : [{"title":"Throne of Seal 2nd Season","link":"https://9anime.me.uk/series/throne-of-seal-2nd-season/","image":"https://i2.wp.com/9anime.me.uk/wp-content/uploads/2025/08/1754556649-9091-150028.jpg?resize=246,350","status":"Ongoing","type":"Anime"},{"title":"One Hundred Thousand Years of Qi Refining","link":"https://9anime.me.uk/series/one-hundred-thousand-years-of-qi-refining/","image":"https://i0.wp.com/9anime.me.uk/wp-content/uploads/2025/08/1754556763-3460-133308.jpg?resize=246,350","status":"Ongoing","type":"Anime"},{"title":"The Swords 2","link":"https://9anime.me.uk/series/the-swords-2/","image":"https://i3.wp.com/9anime.me.uk/wp-content/uploads/2025/12/1766754967-9775-154631.jpg?resize=246,350","status":"Ongoing","type":"Anime"},{"title":"Sentenced to Be a Hero","link":"https://9anime.me.uk/series/sentenced-to-be-a-hero/","image":"https://i3.wp.com/9anime.me.uk/wp-content/uploads/2026/01/1767372298-6437-151911.jpg?resize=246,350","status":"Ongoing","type":"Anime"},{"title":"Saioshi no Gikei wo Mederu Tame, Nagaiki shimasu!","link":"https://9anime.me.uk/series/saioshi-no-gikei-wo-mederu-tame-nagaiki-shimasu/","image":"https://i0.wp.com/9anime.me.uk/wp-content/uploads/2026/01/1767371840-4060-152470.jpg?resize=246,350","status":"Ongoing","type":"Anime"},{"title":"Case Closed","link":"https://9anime.me.uk/series/case-closed/","image":"https://i1.wp.com/9anime.me.uk/wp-content/uploads/2025/08/1754141018-8148-75199.jpg?resize=246,350","status":"Ongoing","type":"Anime"},{"title":"Himitsu no AiPri 2nd Season","link":"https://9anime.me.uk/series/himitsu-no-aipri-2nd-season/","image":"https://i1.wp.com/9anime.me.uk/wp-content/uploads/2025/08/1754138539-3901-148164.jpg?resize=246,350","status":"Ongoing","type":"Anime"},{"title":"Cats Eye","link":"https://9anime.me.uk/series/cats-eye/","image":"https://i0.wp.com/9anime.me.uk/wp-content/uploads/2025/09/1758910820-7098-151708.jpg?resize=246,350","status":"Ongoing","type":"Anime"},{"title":"Cats Eye (Dub)","link":"https://9anime.me.uk/series/cats-eye-dub/","image":"https://i3.wp.com/9anime.me.uk/wp-content/uploads/2025/09/1758910819-1048-151708.jpg?resize=246,350","status":"Ongoing","type":"Anime"},{"title":"So You\u2019re Raising a Warrior","link":"https://9anime.me.uk/series/so-youre-raising-a-warrior/","image":"https://i1.wp.com/9anime.me.uk/wp-content/uploads/2025/10/1761913742-9633-152354.jpg?resize=246,350","status":"Ongoing","type":"Anime"},{"title":"Yao-Chinese Folktales 2","link":"https://9anime.me.uk/series/yao-chinese-folktales-2/","image":"https://i3.wp.com/9anime.me.uk/wp-content/uploads/2026/01/1767346372-4612-147048.jpg?resize=246,350","status":"Ongoing","type":"Anime"},{"title":"The Outcast 6","link":"https://9anime.me.uk/series/the-outcast-6/","image":"https://i2.wp.com/9anime.me.uk/wp-content/uploads/2026/01/1767346124-5238-151431.jpg?resize=246,350","status":"Ongoing","type":"Anime"},{"title":"There\u2019s No Freaking Way I\u2019ll be Your Lover! Unless\u2026 (Sequel)","link":"https://9anime.me.uk/series/theres-no-freaking-way-ill-be-your-lover-unless-sequel/","image":"https://i0.wp.com/9anime.me.uk/wp-content/uploads/2025/12/1767114378-2258-152182.jpg?resize=246,350","status":"Ongoing","type":"Anime"},{"title":"Any and All Living Things","link":"https://9anime.me.uk/series/any-and-all-living-things/","image":"https://i1.wp.com/9anime.me.uk/wp-content/uploads/2025/08/1754139796-6559-150017.jpg?resize=246,350","status":"Ongoing","type":"Anime"},{"title":"Monster Strike: Deadverse Reloaded","link":"https://9anime.me.uk/series/monster-strike-deadverse-reloaded/","image":"https://i2.wp.com/9anime.me.uk/wp-content/uploads/2025/12/1764926840-5406-151875.jpg?resize=246,350","status":"Ongoing","type":"Anime"},{"title":"MF Ghost Season 3","link":"https://9anime.me.uk/series/mf-ghost-season-3/","image":"https://i3.wp.com/9anime.me.uk/wp-content/uploads/2025/12/1767204805-1099-154458.jpg?resize=246,350","status":"Ongoing","type":"Anime"},{"title":"Secrets of the Rivers","link":"https://9anime.me.uk/series/secrets-of-the-rivers/","image":"https://i1.wp.com/9anime.me.uk/wp-content/uploads/2025/12/1765186032-1739-153762.jpg?resize=246,350","status":"Ongoing","type":"Anime"},{"title":"Tamon\u2019s B-Side","link":"https://9anime.me.uk/series/tamons-b-side/","image":"https://i3.wp.com/9anime.me.uk/wp-content/uploads/2025/12/1767116494-6943-153320.jpg?resize=246,350","status":"Ongoing","type":"Anime"},{"title":"Jack-of-All-Trades, Party of None","link":"https://9anime.me.uk/series/jack-of-all-trades-party-of-none/","image":"https://i2.wp.com/9anime.me.uk/wp-content/uploads/2025/12/1767117035-2703-153135.jpg?resize=246,350","status":"Ongoing","type":"Anime"},{"title":"The God of War Dominates","link":"https://9anime.me.uk/series/the-god-of-war-dominates/","image":"https://i1.wp.com/9anime.me.uk/wp-content/uploads/2025/08/1754556410-4353-107609.jpg?resize=246,350","status":"Ongoing","type":"Anime"}]
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
         * title : Throne of Seal 2nd Season
         * link : https://9anime.me.uk/series/throne-of-seal-2nd-season/
         * image : https://i2.wp.com/9anime.me.uk/wp-content/uploads/2025/08/1754556649-9091-150028.jpg?resize=246,350
         * status : Ongoing
         * type : Anime
         */

        private String title;
        private String link;
        private String image;
        private String status;
        private String type;

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

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
