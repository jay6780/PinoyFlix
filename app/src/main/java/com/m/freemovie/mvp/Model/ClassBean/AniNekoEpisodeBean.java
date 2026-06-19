package com.m.freemovie.mvp.Model.ClassBean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AniNekoEpisodeBean {

    /**
     * author : yazky
     * success : true
     * episode : {"breadcrumb":[{"text":"Home","url":"https://anineko.tohttps://anineko.to/home"},{"text":"Grand Blue Dreaming","url":"https://anineko.to/watch/grand-blue-dreaming"},{"text":"Episode 1","url":null}],"title":"Grand Blue Dreaming","meta":["Episode 1","\u2022","TV","\u2022","Finished Airing","\u2022","SUB"],"downloadUrl":"https://anineko.to/download/grand-blue-dreaming/ep-1","player":{"backgroundImage":"'https://cdn.anizara.store/cover/a0a080f42e6f13b3a2df133f073095dd.webp'","badges":[{"text":"AniNeko Player","class":"purple"},{"text":"HD","class":"yellow"},{"text":"SUB","class":"green"}],"servers":[{"serverTabs":[{"id":"sub","text":"Sort Sub","subtext":"Subtitle version"}],"serverGroups":[{"groupId":"sub","groupTitle":"Sort Sub","servers":[{"text":"HD-1 Sort Sub","videoUrl":"https://vibeplayer.site/113e24f271d3aeb6?sub=https://cdn.anizara.store/subtitles/b9/37/b937176da86d4bb5f0ac63aaecf540ea_eng-2.vtt","tab":"tab_0","isDefault":true},{"text":"HD-2 Sort Sub","videoUrl":"https://bibiemb.xyz/ag5754b70145f81fdff4d1424a914ccc724h?sub=https://cdn.anizara.store/subtitles/b9/37/b937176da86d4bb5f0ac63aaecf540ea_eng-2.vtt","tab":"tab_0","isDefault":false},{"text":"StreamHG Sort Sub","videoUrl":"https://otakuhg.site/e/5m6v7xuweetm?caption_1=https://cdn.anizara.store/subtitles/b9/37/b937176da86d4bb5f0ac63aaecf540ea_eng-2.vtt&sub_1=English","tab":"tab_0","isDefault":false},{"text":"Earnvids Sort Sub","videoUrl":"https://otakuvid.online/embed/3x090t61qmfs?caption_1=https://cdn.anizara.store/subtitles/b9/37/b937176da86d4bb5f0ac63aaecf540ea_eng-2.vtt&sub_1=English","tab":"tab_0","isDefault":false}]}]}]},"description":"Iori Kitahara moves to the coastal town of Izu for his freshman year at its university, taking residence above Grand Blue, his uncle's scuba diving shop. Iori has high hopes and dreams about having the ideal college experience, but when he enters the shop he is sucked into the alcoholic activities of the carefree members of the Diving Club who frequent the place. Persuaded by upperclassmen Shinji Tokita and Ryuujirou Kotobuki, Iori reluctantly joins their bizarre party. His cousin Chisa Kotegawa later walks in and catches him in the act, earning Iori her utter disdain.\n\nBased on Kenji Inoue and Kimitake Yoshioka's popular comedy manga, Grand Blue follows Iori's misadventures with his eccentric new friends as he strives to realize his ideal college dream, while also learning how to scuba dive.\n\n[Written by MAL Rewrite]","actions":[{"text":"+ Watchlist Save anime","url":null},{"text":"Download Get links","url":"https://anineko.to/download/grand-blue-dreaming/ep-1"},{"text":"Report Video issue","url":null},{"text":"Share Copy link","url":null}]}
     */

    private String author;
    private boolean success;
    private EpisodeBean episode;

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

    public EpisodeBean getEpisode() {
        return episode;
    }

    public void setEpisode(EpisodeBean episode) {
        this.episode = episode;
    }

    public static class EpisodeBean {
        /**
         * breadcrumb : [{"text":"Home","url":"https://anineko.tohttps://anineko.to/home"},{"text":"Grand Blue Dreaming","url":"https://anineko.to/watch/grand-blue-dreaming"},{"text":"Episode 1","url":null}]
         * title : Grand Blue Dreaming
         * meta : ["Episode 1","\u2022","TV","\u2022","Finished Airing","\u2022","SUB"]
         * downloadUrl : https://anineko.to/download/grand-blue-dreaming/ep-1
         * player : {"backgroundImage":"'https://cdn.anizara.store/cover/a0a080f42e6f13b3a2df133f073095dd.webp'","badges":[{"text":"AniNeko Player","class":"purple"},{"text":"HD","class":"yellow"},{"text":"SUB","class":"green"}],"servers":[{"serverTabs":[{"id":"sub","text":"Sort Sub","subtext":"Subtitle version"}],"serverGroups":[{"groupId":"sub","groupTitle":"Sort Sub","servers":[{"text":"HD-1 Sort Sub","videoUrl":"https://vibeplayer.site/113e24f271d3aeb6?sub=https://cdn.anizara.store/subtitles/b9/37/b937176da86d4bb5f0ac63aaecf540ea_eng-2.vtt","tab":"tab_0","isDefault":true},{"text":"HD-2 Sort Sub","videoUrl":"https://bibiemb.xyz/ag5754b70145f81fdff4d1424a914ccc724h?sub=https://cdn.anizara.store/subtitles/b9/37/b937176da86d4bb5f0ac63aaecf540ea_eng-2.vtt","tab":"tab_0","isDefault":false},{"text":"StreamHG Sort Sub","videoUrl":"https://otakuhg.site/e/5m6v7xuweetm?caption_1=https://cdn.anizara.store/subtitles/b9/37/b937176da86d4bb5f0ac63aaecf540ea_eng-2.vtt&sub_1=English","tab":"tab_0","isDefault":false},{"text":"Earnvids Sort Sub","videoUrl":"https://otakuvid.online/embed/3x090t61qmfs?caption_1=https://cdn.anizara.store/subtitles/b9/37/b937176da86d4bb5f0ac63aaecf540ea_eng-2.vtt&sub_1=English","tab":"tab_0","isDefault":false}]}]}]}
         * description : Iori Kitahara moves to the coastal town of Izu for his freshman year at its university, taking residence above Grand Blue, his uncle's scuba diving shop. Iori has high hopes and dreams about having the ideal college experience, but when he enters the shop he is sucked into the alcoholic activities of the carefree members of the Diving Club who frequent the place. Persuaded by upperclassmen Shinji Tokita and Ryuujirou Kotobuki, Iori reluctantly joins their bizarre party. His cousin Chisa Kotegawa later walks in and catches him in the act, earning Iori her utter disdain.

         Based on Kenji Inoue and Kimitake Yoshioka's popular comedy manga, Grand Blue follows Iori's misadventures with his eccentric new friends as he strives to realize his ideal college dream, while also learning how to scuba dive.

         [Written by MAL Rewrite]
         * actions : [{"text":"+ Watchlist Save anime","url":null},{"text":"Download Get links","url":"https://anineko.to/download/grand-blue-dreaming/ep-1"},{"text":"Report Video issue","url":null},{"text":"Share Copy link","url":null}]
         */

        private String title;
        private String downloadUrl;
        private PlayerBean player;
        private String description;
        private List<BreadcrumbBean> breadcrumb;
        private List<String> meta;
        private List<ActionsBean> actions;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDownloadUrl() {
            return downloadUrl;
        }

        public void setDownloadUrl(String downloadUrl) {
            this.downloadUrl = downloadUrl;
        }

        public PlayerBean getPlayer() {
            return player;
        }

        public void setPlayer(PlayerBean player) {
            this.player = player;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<BreadcrumbBean> getBreadcrumb() {
            return breadcrumb;
        }

        public void setBreadcrumb(List<BreadcrumbBean> breadcrumb) {
            this.breadcrumb = breadcrumb;
        }

        public List<String> getMeta() {
            return meta;
        }

        public void setMeta(List<String> meta) {
            this.meta = meta;
        }

        public List<ActionsBean> getActions() {
            return actions;
        }

        public void setActions(List<ActionsBean> actions) {
            this.actions = actions;
        }

        public static class PlayerBean {
            /**
             * backgroundImage : 'https://cdn.anizara.store/cover/a0a080f42e6f13b3a2df133f073095dd.webp'
             * badges : [{"text":"AniNeko Player","class":"purple"},{"text":"HD","class":"yellow"},{"text":"SUB","class":"green"}]
             * servers : [{"serverTabs":[{"id":"sub","text":"Sort Sub","subtext":"Subtitle version"}],"serverGroups":[{"groupId":"sub","groupTitle":"Sort Sub","servers":[{"text":"HD-1 Sort Sub","videoUrl":"https://vibeplayer.site/113e24f271d3aeb6?sub=https://cdn.anizara.store/subtitles/b9/37/b937176da86d4bb5f0ac63aaecf540ea_eng-2.vtt","tab":"tab_0","isDefault":true},{"text":"HD-2 Sort Sub","videoUrl":"https://bibiemb.xyz/ag5754b70145f81fdff4d1424a914ccc724h?sub=https://cdn.anizara.store/subtitles/b9/37/b937176da86d4bb5f0ac63aaecf540ea_eng-2.vtt","tab":"tab_0","isDefault":false},{"text":"StreamHG Sort Sub","videoUrl":"https://otakuhg.site/e/5m6v7xuweetm?caption_1=https://cdn.anizara.store/subtitles/b9/37/b937176da86d4bb5f0ac63aaecf540ea_eng-2.vtt&sub_1=English","tab":"tab_0","isDefault":false},{"text":"Earnvids Sort Sub","videoUrl":"https://otakuvid.online/embed/3x090t61qmfs?caption_1=https://cdn.anizara.store/subtitles/b9/37/b937176da86d4bb5f0ac63aaecf540ea_eng-2.vtt&sub_1=English","tab":"tab_0","isDefault":false}]}]}]
             */

            private String backgroundImage;
            private List<BadgesBean> badges;
            private List<ServersBeanX> servers;

            public String getBackgroundImage() {
                return backgroundImage;
            }

            public void setBackgroundImage(String backgroundImage) {
                this.backgroundImage = backgroundImage;
            }

            public List<BadgesBean> getBadges() {
                return badges;
            }

            public void setBadges(List<BadgesBean> badges) {
                this.badges = badges;
            }

            public List<ServersBeanX> getServers() {
                return servers;
            }

            public void setServers(List<ServersBeanX> servers) {
                this.servers = servers;
            }

            public static class BadgesBean {
                /**
                 * text : AniNeko Player
                 * class : purple
                 */

                private String text;
                @SerializedName("class")
                private String classX;

                public String getText() {
                    return text;
                }

                public void setText(String text) {
                    this.text = text;
                }

                public String getClassX() {
                    return classX;
                }

                public void setClassX(String classX) {
                    this.classX = classX;
                }
            }

            public static class ServersBeanX {
                private List<ServerTabsBean> serverTabs;
                private List<ServerGroupsBean> serverGroups;

                public List<ServerTabsBean> getServerTabs() {
                    return serverTabs;
                }

                public void setServerTabs(List<ServerTabsBean> serverTabs) {
                    this.serverTabs = serverTabs;
                }

                public List<ServerGroupsBean> getServerGroups() {
                    return serverGroups;
                }

                public void setServerGroups(List<ServerGroupsBean> serverGroups) {
                    this.serverGroups = serverGroups;
                }

                public static class ServerTabsBean {
                    /**
                     * id : sub
                     * text : Sort Sub
                     * subtext : Subtitle version
                     */

                    private String id;
                    private String text;
                    private String subtext;

                    public String getId() {
                        return id;
                    }

                    public void setId(String id) {
                        this.id = id;
                    }

                    public String getText() {
                        return text;
                    }

                    public void setText(String text) {
                        this.text = text;
                    }

                    public String getSubtext() {
                        return subtext;
                    }

                    public void setSubtext(String subtext) {
                        this.subtext = subtext;
                    }
                }

                public static class ServerGroupsBean {
                    /**
                     * groupId : sub
                     * groupTitle : Sort Sub
                     * servers : [{"text":"HD-1 Sort Sub","videoUrl":"https://vibeplayer.site/113e24f271d3aeb6?sub=https://cdn.anizara.store/subtitles/b9/37/b937176da86d4bb5f0ac63aaecf540ea_eng-2.vtt","tab":"tab_0","isDefault":true},{"text":"HD-2 Sort Sub","videoUrl":"https://bibiemb.xyz/ag5754b70145f81fdff4d1424a914ccc724h?sub=https://cdn.anizara.store/subtitles/b9/37/b937176da86d4bb5f0ac63aaecf540ea_eng-2.vtt","tab":"tab_0","isDefault":false},{"text":"StreamHG Sort Sub","videoUrl":"https://otakuhg.site/e/5m6v7xuweetm?caption_1=https://cdn.anizara.store/subtitles/b9/37/b937176da86d4bb5f0ac63aaecf540ea_eng-2.vtt&sub_1=English","tab":"tab_0","isDefault":false},{"text":"Earnvids Sort Sub","videoUrl":"https://otakuvid.online/embed/3x090t61qmfs?caption_1=https://cdn.anizara.store/subtitles/b9/37/b937176da86d4bb5f0ac63aaecf540ea_eng-2.vtt&sub_1=English","tab":"tab_0","isDefault":false}]
                     */

                    private String groupId;
                    private String groupTitle;
                    private List<ServersBean> servers;

                    public String getGroupId() {
                        return groupId;
                    }

                    public void setGroupId(String groupId) {
                        this.groupId = groupId;
                    }

                    public String getGroupTitle() {
                        return groupTitle;
                    }

                    public void setGroupTitle(String groupTitle) {
                        this.groupTitle = groupTitle;
                    }

                    public List<ServersBean> getServers() {
                        return servers;
                    }

                    public void setServers(List<ServersBean> servers) {
                        this.servers = servers;
                    }

                    public static class ServersBean {
                        /**
                         * text : HD-1 Sort Sub
                         * videoUrl : https://vibeplayer.site/113e24f271d3aeb6?sub=https://cdn.anizara.store/subtitles/b9/37/b937176da86d4bb5f0ac63aaecf540ea_eng-2.vtt
                         * tab : tab_0
                         * isDefault : true
                         */

                        private String text;
                        private String videoUrl;
                        private String tab;
                        private boolean isDefault;

                        public String getText() {
                            return text;
                        }

                        public void setText(String text) {
                            this.text = text;
                        }

                        public String getVideoUrl() {
                            return videoUrl;
                        }

                        public void setVideoUrl(String videoUrl) {
                            this.videoUrl = videoUrl;
                        }

                        public String getTab() {
                            return tab;
                        }

                        public void setTab(String tab) {
                            this.tab = tab;
                        }

                        public boolean isIsDefault() {
                            return isDefault;
                        }

                        public void setIsDefault(boolean isDefault) {
                            this.isDefault = isDefault;
                        }
                    }
                }
            }
        }

        public static class BreadcrumbBean {
            /**
             * text : Home
             * url : https://anineko.tohttps://anineko.to/home
             */

            private String text;
            private String url;

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
        }

        public static class ActionsBean {
            /**
             * text : + Watchlist Save anime
             * url : null
             */

            private String text;
            private Object url;

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }

            public Object getUrl() {
                return url;
            }

            public void setUrl(Object url) {
                this.url = url;
            }
        }
    }
}
