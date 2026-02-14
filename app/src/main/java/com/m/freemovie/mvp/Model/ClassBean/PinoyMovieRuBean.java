package com.m.freemovie.mvp.Model.ClassBean;

import java.util.List;

public class PinoyMovieRuBean {

    /**
     * id : 64014
     * date : 2026-01-30T00:13:09
     * date_gmt : 2026-01-29T16:13:09
     * guid : {"rendered":"https://pinoymoviepedia.ru/?post_type=movies&#038;p=64014"}
     * modified : 2026-01-30T00:24:02
     * modified_gmt : 2026-01-29T16:24:02
     * slug : init-ng-hinagpis-2026-part-2
     * status : publish
     * type : movies
     * link : https://pinoymoviepedia.ru/movies/init-ng-hinagpis-2026-part-2/
     * title : {"rendered":"INIT NG HINAGPIS (2026) PART 2"}
     * content : {"rendered":"<p>INIT NG HINAGPIS 2026 Part 2 Free Download Link Streaming Online HD<br />\nDirected by: Maxee Moe<br />\nStarring: Lyka Ashen, Fayah Levin, Zhander Pilapil, Casper Padilla, Rustom Diodos, Nikko \u201cCoolas\u201d Taller<\/p>\n","protected":false}
     * author : 1
     * featured_media : 64015
     * comment_status : open
     * ping_status : closed
     * template :
     * tags : []
     * genres : [497,7768]
     * dtquality : [113]
     * dtcast : [7937,7934,7929,7930,7935]
     * dtdirector : [7932]
     * dtyear : [7808]
     * class_list : ["post-64014","movies","type-movies","status-publish","has-post-thumbnail","hentry","genres-pinay-sexy","genres-tbonx","dtquality-hd","dtcast-casper-padilla","dtcast-fayah-levin","dtcast-lyka-ashen","dtcast-rustom-diodos","dtcast-zhander-pilapil","dtdirector-maxee-moe","dtyear-7808"]
     * _links : {"self":[{"href":"https://pinoymoviepedia.ru/wp-json/wp/v2/movies/64014","targetHints":{"allow":["GET"]}}],"collection":[{"href":"https://pinoymoviepedia.ru/wp-json/wp/v2/movies"}],"about":[{"href":"https://pinoymoviepedia.ru/wp-json/wp/v2/types/movies"}],"author":[{"embeddable":true,"href":"https://pinoymoviepedia.ru/wp-json/wp/v2/users/1"}],"replies":[{"embeddable":true,"href":"https://pinoymoviepedia.ru/wp-json/wp/v2/comments?post=64014"}],"wp:featuredmedia":[{"embeddable":true,"href":"https://pinoymoviepedia.ru/wp-json/wp/v2/media/64015"}],"wp:attachment":[{"href":"https://pinoymoviepedia.ru/wp-json/wp/v2/media?parent=64014"}],"wp:term":[{"taxonomy":"post_tag","embeddable":true,"href":"https://pinoymoviepedia.ru/wp-json/wp/v2/tags?post=64014"},{"taxonomy":"genres","embeddable":true,"href":"https://pinoymoviepedia.ru/wp-json/wp/v2/genres?post=64014"},{"taxonomy":"dtquality","embeddable":true,"href":"https://pinoymoviepedia.ru/wp-json/wp/v2/dtquality?post=64014"},{"taxonomy":"dtcast","embeddable":true,"href":"https://pinoymoviepedia.ru/wp-json/wp/v2/dtcast?post=64014"},{"taxonomy":"dtdirector","embeddable":true,"href":"https://pinoymoviepedia.ru/wp-json/wp/v2/dtdirector?post=64014"},{"taxonomy":"dtyear","embeddable":true,"href":"https://pinoymoviepedia.ru/wp-json/wp/v2/dtyear?post=64014"}],"curies":[{"name":"wp","href":"https://api.w.org/{rel}","templated":true}]}
     */

    private String id;
    private String date;
    private String date_gmt;
    private GuidBean guid;
    private String modified;
    private String modified_gmt;
    private String slug;
    private String status;
    private String type;
    private String link;
    private TitleBean title;
    private ContentBean content;
    private int author;
    private int featured_media;
    private String comment_status;
    private String ping_status;
    private String template;
    private LinksBean _links;
    private java.util.List<?> tags;
    private java.util.List<Integer> genres;
    private java.util.List<Integer> dtquality;
    private java.util.List<Integer> dtcast;
    private java.util.List<Integer> dtdirector;
    private java.util.List<Integer> dtyear;
    private java.util.List<String> class_list;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public TitleBean getTitle() {
        return title;
    }


    public void setTitle(TitleBean title) {
        this.title = title;
    }

    public static class GuidBean {
        /**
         * rendered : https://pinoymoviepedia.ru/?post_type=movies&#038;p=64014
         */

        private String rendered;

        public String getRendered() {
            return rendered;
        }

        public void setRendered(String rendered) {
            this.rendered = rendered;
        }
    }

    public static class TitleBean {
        /**
         * rendered : INIT NG HINAGPIS (2026) PART 2
         */

        private String rendered;

        public String getRendered() {
            return rendered;
        }

        public void setRendered(String rendered) {
            this.rendered = rendered;
        }
    }

    public static class ContentBean {
        /**
         * rendered : <p>INIT NG HINAGPIS 2026 Part 2 Free Download Link Streaming Online HD<br />
         Directed by: Maxee Moe<br />
         Starring: Lyka Ashen, Fayah Levin, Zhander Pilapil, Casper Padilla, Rustom Diodos, Nikko “Coolas” Taller</p>
         * protected : false
         */

        private String rendered;
        @com.google.gson.annotations.SerializedName("protected")
        private boolean protectedX;

        public String getRendered() {
            return rendered;
        }

        public void setRendered(String rendered) {
            this.rendered = rendered;
        }

        public boolean isProtectedX() {
            return protectedX;
        }

        public void setProtectedX(boolean protectedX) {
            this.protectedX = protectedX;
        }
    }

    // FIXME generate failure  field _$WpTerm185
// FIXME generate failure  field _$WpAttachment260
// FIXME generate failure  field _$WpFeaturedmedia2
    public static class LinksBean {
        private java.util.List<SelfBean> self;
        private java.util.List<CollectionBean> collection;
        private java.util.List<AboutBean> about;
        private java.util.List<AuthorBean> author;
        private java.util.List<RepliesBean> replies;

        public List<SelfBean> getSelf() {
            return self;
        }

        public void setSelf(List<SelfBean> self) {
            this.self = self;
        }

        public List<CollectionBean> getCollection() {
            return collection;
        }

        public void setCollection(List<CollectionBean> collection) {
            this.collection = collection;
        }

        public List<AboutBean> getAbout() {
            return about;
        }

        public void setAbout(List<AboutBean> about) {
            this.about = about;
        }

        public List<AuthorBean> getAuthor() {
            return author;
        }

        public void setAuthor(List<AuthorBean> author) {
            this.author = author;
        }

        public List<RepliesBean> getReplies() {
            return replies;
        }

        public void setReplies(List<RepliesBean> replies) {
            this.replies = replies;
        }

        public static class SelfBean {
            /**
             * href : https://pinoymoviepedia.ru/wp-json/wp/v2/movies/64014
             * targetHints : {"allow":["GET"]}
             */

            private String href;
            private TargetHintsBean targetHints;

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
                private java.util.List<String> allow;

                public List<String> getAllow() {
                    return allow;
                }

                public void setAllow(List<String> allow) {
                    this.allow = allow;
                }
            }
        }

        public static class CollectionBean {
            /**
             * href : https://pinoymoviepedia.ru/wp-json/wp/v2/movies
             */

            private String href;

            public String getHref() {
                return href;
            }

            public void setHref(String href) {
                this.href = href;
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

        public static class AuthorBean {
            /**
             * embeddable : true
             * href : https://pinoymoviepedia.ru/wp-json/wp/v2/users/1
             */

            private boolean embeddable;
            private String href;

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
        }

        public static class RepliesBean {
            /**
             * embeddable : true
             * href : https://pinoymoviepedia.ru/wp-json/wp/v2/comments?post=64014
             */

            private boolean embeddable;
            private String href;

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
        }
            public static class CuriesBean {
                /**
                 * name : wp
                 * href : https://api.w.org/{rel}
                 * templated : true
                 */

                private String name;
                private String href;
                private boolean templated;

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getHref() {
                    return href;
                }

                public void setHref(String href) {
                    this.href = href;
                }

                public boolean isTemplated() {
                    return templated;
                }

                public void setTemplated(boolean templated) {
                    this.templated = templated;
                }
            }
        }
}
