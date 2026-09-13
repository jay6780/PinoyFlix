package com.m.freemovie.mvp.Model.ClassBean;

import java.util.List;

public class AniMoTvEpisodeBean {

    /**
     * success : true
     * count : 10
     * results : [{"index":1,"name":"Sub -HD0","url":"https://animotvslash.ru/watch/166522/12?lang=sub&embed=1"},{"index":2,"name":"Sub -HD1","url":"https://tryembed.us.cc/embed/anime/166522/12/sub"},{"index":3,"name":"SoftSub -HD4","url":"https://megaplay.buzz/stream/ani/166522/12/sub"},{"index":4,"name":"SoftSub -HD2","url":"https://vidnest.fun/anime/166522/12/sub"},{"index":5,"name":"Sub -HD3","url":"https://vidnest.fun/animepahe/166522/12/sub"},{"index":6,"name":"Dub -HD0","url":"https://animotvslash.ru/watch/166522/12?lang=dub&embed=1"},{"index":7,"name":"Dub -HD1","url":"https://tryembed.us.cc/embed/anime/166522/12/dub"},{"index":8,"name":"Dub -HD4","url":"https://megaplay.buzz/stream/ani/166522/12/dub"},{"index":9,"name":"Dub -HD2","url":"https://vidnest.fun/anime/166522/12/dub"},{"index":10,"name":"Dub -HD3","url":"https://vidnest.fun/animepahe/166522/12/dub"}]
     */

    private boolean success;
    private int count;
    private List<ResultsBean> results;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
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
         * index : 1
         * name : Sub -HD0
         * url : https://animotvslash.ru/watch/166522/12?lang=sub&embed=1
         */

        private String index;
        private String name;
        private String url;

        public String getIndex() {
            return index;
        }

        public void setIndex(String index) {
            this.index = index;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
