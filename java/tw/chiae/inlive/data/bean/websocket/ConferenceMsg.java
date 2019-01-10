package tw.chiae.inlive.data.bean.websocket;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2016/12/3 0003.
 */

public class ConferenceMsg {
    /**
     * {
     * "type": "SendPrvMsg",
     * "from_client_name": "wechat_@浩",
     * "from_user_id": "2500",
     * "vip": 0,
     * "levelid": "15",
     * "avatar": "/style/avatar/f76/2500_small.jpg?t=1480304780",
     * "to_client_name": "你",
     * "to_user_id": "1358",
     * "content": {
     * "conference_invitation": "67081469110754"
     * },
     * "pub": "1",
     * "time": "11:06"
     * }
     **/
    private String from_client_name;
    private String from_user_id;
    private int vip;
    private String levelid;
    private String avatar;
    private String to_client_name;
    private String to_user_id;
    private String pub;
    private String time;

    public class ContentConference {
        private String conference_invitation;

        public String getConference_invitation() {
            return conference_invitation;
        }

        public void setConference_invitation(String conference_invitation) {
            this.conference_invitation = conference_invitation;
        }
    }

    public String getFrom_client_name() {
        return from_client_name;
    }

    public void setFrom_client_name(String from_client_name) {
        this.from_client_name = from_client_name;
    }

    public String getFrom_user_id() {
        return from_user_id;
    }

    public void setFrom_user_id(String from_user_id) {
        this.from_user_id = from_user_id;
    }

    public int getVip() {
        return vip;
    }

    public void setVip(int vip) {
        this.vip = vip;
    }

    public String getLevelid() {
        return levelid;
    }

    public void setLevelid(String levelid) {
        this.levelid = levelid;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getTo_client_name() {
        return to_client_name;
    }

    public void setTo_client_name(String to_client_name) {
        this.to_client_name = to_client_name;
    }

    public String getTo_user_id() {
        return to_user_id;
    }

    public void setTo_user_id(String to_user_id) {
        this.to_user_id = to_user_id;
    }

    public String getPub() {
        return pub;
    }

    public void setPub(String pub) {
        this.pub = pub;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
