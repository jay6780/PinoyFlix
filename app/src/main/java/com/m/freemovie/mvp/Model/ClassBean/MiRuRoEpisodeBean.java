package com.m.freemovie.mvp.Model.ClassBean;

import java.util.List;

public class MiRuRoEpisodeBean {

    /**
     * author : yazky
     * title : Killed Again, Mr. Detective. Episode 11 English subbed
     * seriesUrl : https://miruro.ro/series/killed-again-mr-detective/
     * miruroPlayerUrl : https://miruro.ro/wp-content/themes/dramastream-child/player/?source=blogger&url=TDQ0YVNoQmVKb29tWlBIWTF3SnJUUkhtT0Y2SlZoMTRVejRheUlKbFFFcW5tZ3VRaXJuWnlFVFBqZXZFOTFkN0VOVmdoanlrZlNmSExnNklxeDBIWSt2dW9SajFNNWgyckgwOFZtOHVGSVZtem50K1BJL0toMm1xVVFhdzlkQ3hqNEE5bXJ2YU5FRk01SUh3bitsQmF3PT0%3D
     * megaplayUrl : null
     * video : {"source":"blogger","file":"https://rr5---sn-nx57ynsk.googlevideo.com/videoplayback?expire=1781290526&ei=nuUratfDBYnr-eUPmrmZsAw&ip=196.51.200.124&id=af74c0c5313e3a69&itag=18&source=blogger&requiressl=yes&xpc=Egho7Zf3LnoBAQ==&cps=39&met=1781261726,&mh=iz&mm=31&mn=sn-nx57ynsk&ms=au&mv=m&mvi=5&pl=17&rms=au,au&susc=bl&svpuc=1&eaua=V_JFGAD64UM&mime=video/mp4&vprv=1&rqh=1&dur=1450.132&lmt=1781230559475760&mt=1781261358&txp=1311224&sparams=expire,ei,ip,id,itag,source,requiressl,xpc,susc,svpuc,eaua,mime,vprv,rqh,dur,lmt&sig=AHEqNM4wRQIgT3NUEPPU-0KfRwqK6TF2TZuw8ro1Krwv2YmWEWN16goCIQDqzKJoZ9xPZ_Hn67rxuNDCl7Te9V6NNhPND3xMKGHfSA==&lsparams=cps,met,mh,mm,mn,ms,mv,mvi,pl,rms&lsig=APaTxxMwRQIhAILnMH28F1htZU9aJdn3Vx9UPMYYR0cVjj6ES87kyHrCAiBqNCLE2Slqm2ffST4q-a-s53f1aLc5KiLYzsq5wxv1Eg==","thumbnail":"https://i9.ytimg.com/vi_blogger/r3TAxTE-Omk/1.jpg?sqp=CJ7Lr9EGGJAc-oaymwEGCMACELQB&rs=AMzJL3n2FQ6gzOFPntnnFFGe1hmQdJuY5Q","tracks":[]}
     * totalEpisodes : 11
     * episodes : [{"title":"Killed Again, Mr. Detective. Episode 11 English subbed","url":"https://miruro.ro/mata-korosarete-shimatta-no-desu-ne-tantei-sama-episode-11/"},{"title":"Killed Again, Mr. Detective. Episode 10 English subbed","url":"https://miruro.ro/mata-korosarete-shimatta-no-desu-ne-tantei-sama-episode-10/"},{"title":"Killed Again, Mr. Detective. Episode 9 English subbed","url":"https://miruro.ro/mata-korosarete-shimatta-no-desu-ne-tantei-sama-episode-9/"},{"title":"Killed Again, Mr. Detective. Episode 8 English subbed","url":"https://miruro.ro/mata-korosarete-shimatta-no-desu-ne-tantei-sama-episode-8/"},{"title":"Killed Again, Mr. Detective. Episode 7 English subbed","url":"https://miruro.ro/mata-korosarete-shimatta-no-desu-ne-tantei-sama-episode-7/"},{"title":"Killed Again, Mr. Detective. Episode 6 English subbed","url":"https://miruro.ro/mata-korosarete-shimatta-no-desu-ne-tantei-sama-episode-6/"},{"title":"Killed Again, Mr. Detective. Episode 5 English subbed","url":"https://miruro.ro/mata-korosarete-shimatta-no-desu-ne-tantei-sama-episode-5/"},{"title":"Killed Again, Mr. Detective. Episode 4 English subbed","url":"https://miruro.ro/mata-korosarete-shimatta-no-desu-ne-tantei-sama-episode-4/"},{"title":"Killed Again, Mr. Detective. Episode 3 English subbed","url":"https://miruro.ro/mata-korosarete-shimatta-no-desu-ne-tantei-sama-episode-3/"},{"title":"Killed Again, Mr. Detective. Episode 2 English subbed","url":"https://miruro.ro/mata-korosarete-shimatta-no-desu-ne-tantei-sama-episode-2/"},{"title":"Killed Again, Mr. Detective. Episode 1 English subbed","url":"https://miruro.ro/mata-korosarete-shimatta-no-desu-ne-tantei-sama-episode-1/"}]
     */

    private String author;
    private String title;
    private String seriesUrl;
    private String miruroPlayerUrl;
    private Object megaplayUrl;
    private VideoBean video;
    private int totalEpisodes;
    private List<EpisodesBean> episodes;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSeriesUrl() {
        return seriesUrl;
    }

    public void setSeriesUrl(String seriesUrl) {
        this.seriesUrl = seriesUrl;
    }

    public String getMiruroPlayerUrl() {
        return miruroPlayerUrl;
    }

    public void setMiruroPlayerUrl(String miruroPlayerUrl) {
        this.miruroPlayerUrl = miruroPlayerUrl;
    }

    public Object getMegaplayUrl() {
        return megaplayUrl;
    }

    public void setMegaplayUrl(Object megaplayUrl) {
        this.megaplayUrl = megaplayUrl;
    }

    public VideoBean getVideo() {
        return video;
    }

    public void setVideo(VideoBean video) {
        this.video = video;
    }

    public int getTotalEpisodes() {
        return totalEpisodes;
    }

    public void setTotalEpisodes(int totalEpisodes) {
        this.totalEpisodes = totalEpisodes;
    }

    public List<EpisodesBean> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<EpisodesBean> episodes) {
        this.episodes = episodes;
    }

    public static class VideoBean {
        /**
         * source : blogger
         * file : https://rr5---sn-nx57ynsk.googlevideo.com/videoplayback?expire=1781290526&ei=nuUratfDBYnr-eUPmrmZsAw&ip=196.51.200.124&id=af74c0c5313e3a69&itag=18&source=blogger&requiressl=yes&xpc=Egho7Zf3LnoBAQ==&cps=39&met=1781261726,&mh=iz&mm=31&mn=sn-nx57ynsk&ms=au&mv=m&mvi=5&pl=17&rms=au,au&susc=bl&svpuc=1&eaua=V_JFGAD64UM&mime=video/mp4&vprv=1&rqh=1&dur=1450.132&lmt=1781230559475760&mt=1781261358&txp=1311224&sparams=expire,ei,ip,id,itag,source,requiressl,xpc,susc,svpuc,eaua,mime,vprv,rqh,dur,lmt&sig=AHEqNM4wRQIgT3NUEPPU-0KfRwqK6TF2TZuw8ro1Krwv2YmWEWN16goCIQDqzKJoZ9xPZ_Hn67rxuNDCl7Te9V6NNhPND3xMKGHfSA==&lsparams=cps,met,mh,mm,mn,ms,mv,mvi,pl,rms&lsig=APaTxxMwRQIhAILnMH28F1htZU9aJdn3Vx9UPMYYR0cVjj6ES87kyHrCAiBqNCLE2Slqm2ffST4q-a-s53f1aLc5KiLYzsq5wxv1Eg==
         * thumbnail : https://i9.ytimg.com/vi_blogger/r3TAxTE-Omk/1.jpg?sqp=CJ7Lr9EGGJAc-oaymwEGCMACELQB&rs=AMzJL3n2FQ6gzOFPntnnFFGe1hmQdJuY5Q
         * tracks : []
         */

        private String source;
        private String file;
        private String thumbnail;
        private List<?> tracks;

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }

        public String getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }

        public List<?> getTracks() {
            return tracks;
        }

        public void setTracks(List<?> tracks) {
            this.tracks = tracks;
        }
    }

    public static class EpisodesBean {
        /**
         * title : Killed Again, Mr. Detective. Episode 11 English subbed
         * url : https://miruro.ro/mata-korosarete-shimatta-no-desu-ne-tantei-sama-episode-11/
         */

        private String title;
        private String url;

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
    }
}
