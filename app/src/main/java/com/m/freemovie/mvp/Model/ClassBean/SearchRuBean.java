package com.m.freemovie.mvp.Model.ClassBean;

import java.util.List;

public class SearchRuBean {

    /**
     * id : 23559
     * title : Sanggano, Sanggago’t Sanggwapo 2 (2021)
     * url : https://pinoymoviepedia.ru/movies/sanggano-sanggagot-sanggwapo-2-2021/
     * type : post
     * subtype : movies
     * _links : {"self":[{"embeddable":true,"href":"https://pinoymoviepedia.ru/wp-json/wp/v2/movies/23559","targetHints":{"allow":["GET"]}}],"about":[{"href":"https://pinoymoviepedia.ru/wp-json/wp/v2/types/movies"}],"collection":[{"href":"https://pinoymoviepedia.ru/wp-json/wp/v2/search"}]}
     */

    private String id;
    private String title;
    private String url;
    private String type;
    private String subtype;
    private LinksBean _links;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    public LinksBean get_links() {
        return _links;
    }

    public void set_links(LinksBean _links) {
        this._links = _links;
    }

    public static class LinksBean {
        private List<SelfBean> self;
        private List<AboutBean> about;
        private List<CollectionBean> collection;

        public List<SelfBean> getSelf() {
            return self;
        }

        public void setSelf(List<SelfBean> self) {
            this.self = self;
        }

        public List<AboutBean> getAbout() {
            return about;
        }

        public void setAbout(List<AboutBean> about) {
            this.about = about;
        }

        public List<CollectionBean> getCollection() {
            return collection;
        }

        public void setCollection(List<CollectionBean> collection) {
            this.collection = collection;
        }

        public static class SelfBean {
            /**
             * embeddable : true
             * href : https://pinoymoviepedia.ru/wp-json/wp/v2/movies/23559
             * targetHints : {"allow":["GET"]}
             */

            private boolean embeddable;
            private String href;
            private TargetHintsBean targetHints;

            public boolean isEmbeddable() {
                return embeddable;
            }

            public void setEmbeddable(boolean embeddable) {
                this.embeddable = embeddable;
            }

            public String getHref() {
                return href;
            }

            public void setHref(String href) {
                this.href = href;
            }

            public TargetHintsBean getTargetHints() {
                return targetHints;
            }

            public void setTargetHints(TargetHintsBean targetHints) {
                this.targetHints = targetHints;
            }

            public static class TargetHintsBean {
                private List<String> allow;

                public List<String> getAllow() {
                    return allow;
                }

                public void setAllow(List<String> allow) {
                    this.allow = allow;
                }
            }
        }

        public static class AboutBean {
            /**
             * href : https://pinoymoviepedia.ru/wp-json/wp/v2/types/movies
             */

            private String href;

            public String getHref() {
                return href;
            }

            public void setHref(String href) {
                this.href = href;
            }
        }

        public static class CollectionBean {
            /**
             * href : https://pinoymoviepedia.ru/wp-json/wp/v2/search
             */

            private String href;

            public String getHref() {
                return href;
            }

            public void setHref(String href) {
                this.href = href;
            }
        }
    }
}
