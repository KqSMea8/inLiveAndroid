package tw.chiae.inlive.data.bean;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public class PushStreamInfo {

    /**
     * id : z1.xingketv.Test123456
     * createdAt : 2016-05-28T10:45:32.287Z
     * updatedAt : 2016-05-28T10:45:32.287Z
     * title : Test123456
     * hub : xingketv
     * disabled : false
     * publishKey : 2c505e89-220f-49d0-b82f-c436a008bcd6
     * publishSecurity : static
     * hosts : {"publish":{"rtmp":"pili-publish.xingketv.com"},"live":{"hdl":"pili-live-hdl
     * .xingketv.com","hls":"pili-live-hls.xingketv.com","http":"pili-live-hls.xingketv.com",
     * "rtmp":"pili-live-rtmp.xingketv.com","snapshot":"10000rr.live1-snapshot.z1.pili.qiniucdn
     * .com"},"playback":{"hls":"pili-playback.xingketv.com","http":"pili-playback.xingketv.com"}}
     */

    private String id;
    private String createdAt;
    private String updatedAt;
    private String title;
    private String hub;
    private boolean disabled;
    private String publishKey;
    private String publishSecurity;
    /**
     * publish : {"rtmp":"pili-publish.xingketv.com"}
     * live : {"hdl":"pili-live-hdl.xingketv.com","hls":"pili-live-hls.xingketv.com",
     * "http":"pili-live-hls.xingketv.com","rtmp":"pili-live-rtmp.xingketv.com",
     * "snapshot":"10000rr.live1-snapshot.z1.pili.qiniucdn.com"}
     * playback : {"hls":"pili-playback.xingketv.com","http":"pili-playback.xingketv.com"}
     */

    private HostsBean hosts;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHub() {
        return hub;
    }

    public void setHub(String hub) {
        this.hub = hub;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public String getPublishKey() {
        return publishKey;
    }

    public void setPublishKey(String publishKey) {
        this.publishKey = publishKey;
    }

    public String getPublishSecurity() {
        return publishSecurity;
    }

    public void setPublishSecurity(String publishSecurity) {
        this.publishSecurity = publishSecurity;
    }

    public HostsBean getHosts() {
        return hosts;
    }

    public void setHosts(HostsBean hosts) {
        this.hosts = hosts;
    }

    public static class HostsBean {
        /**
         * rtmp : pili-publish.xingketv.com
         */

        private PublishBean publish;
        /**
         * hdl : pili-live-hdl.xingketv.com
         * hls : pili-live-hls.xingketv.com
         * http : pili-live-hls.xingketv.com
         * rtmp : pili-live-rtmp.xingketv.com
         * snapshot : 10000rr.live1-snapshot.z1.pili.qiniucdn.com
         */

        private LiveBean live;
        /**
         * hls : pili-playback.xingketv.com
         * http : pili-playback.xingketv.com
         */

        private PlaybackBean playback;

        public PublishBean getPublish() {
            return publish;
        }

        public void setPublish(PublishBean publish) {
            this.publish = publish;
        }

        public LiveBean getLive() {
            return live;
        }

        public void setLive(LiveBean live) {
            this.live = live;
        }

        public PlaybackBean getPlayback() {
            return playback;
        }

        public void setPlayback(PlaybackBean playback) {
            this.playback = playback;
        }

        public static class PublishBean {
            private String rtmp;

            public String getRtmp() {
                return rtmp;
            }

            public void setRtmp(String rtmp) {
                this.rtmp = rtmp;
            }
        }

        public static class LiveBean {
            private String hdl;
            private String hls;
            private String http;
            private String rtmp;
            private String snapshot;

            public String getHdl() {
                return hdl;
            }

            public void setHdl(String hdl) {
                this.hdl = hdl;
            }

            public String getHls() {
                return hls;
            }

            public void setHls(String hls) {
                this.hls = hls;
            }

            public String getHttp() {
                return http;
            }

            public void setHttp(String http) {
                this.http = http;
            }

            public String getRtmp() {
                return rtmp;
            }

            public void setRtmp(String rtmp) {
                this.rtmp = rtmp;
            }

            public String getSnapshot() {
                return snapshot;
            }

            public void setSnapshot(String snapshot) {
                this.snapshot = snapshot;
            }
        }

        public static class PlaybackBean {
            private String hls;
            private String http;

            public String getHls() {
                return hls;
            }

            public void setHls(String hls) {
                this.hls = hls;
            }

            public String getHttp() {
                return http;
            }

            public void setHttp(String http) {
                this.http = http;
            }
        }
    }
}
