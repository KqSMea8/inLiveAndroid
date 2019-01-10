package tw.chiae.inlive.data.bean.websocket;

/**
 * Created by Administrator on 2016/6/29.
 */

public class WsPrvMsgRequest implements WsRequest {

   /** "type": "SendPrvMsg",
            "from_client_name": "\u7f8e\u5973\u6765\u4e86",
            "from_user_id": "1270",
            "vip": 0,
            "levelid": "17",
            "avatar": "\/style\/avatar\/c85\/1270_small.jpg?t=1476173527",
            "to_client_name": "\u4f60",
            "to_user_id": "1358",
            "content": {
        "conference_invitation": "67081469110754",
                "conference_type": "conference_invitation"
    },
            "pub": "1",
            "time": "10:52"*/
    private String _method_;
    private String from_client_id;
    private String from_user_id;
    private String from_client_name;
    private int vip;
    private String levelid;
    private String to_client_id;
    private String to_client_name;
    private String to_user_id;
    private String pub;
    private ContentMsg content;
    private String time;
    private String avatar;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String get_method_() {
        return _method_;
    }

    public void set_method_(String _method_) {
        this._method_ = _method_;
    }

    public void setFrom_client_id(String from_client_id) {
        this.from_client_id = from_client_id;
    }

    public String getFrom_client_id() {
        return this.from_client_id;
    }

    public void setFrom_user_id(String from_user_id) {
        this.from_user_id = from_user_id;
    }

    public String getFrom_user_id() {
        return this.from_user_id;
    }

    public void setFrom_client_name(String from_client_name) {
        this.from_client_name = from_client_name;
    }

    public String getFrom_client_name() {
        return this.from_client_name;
    }

    public void setVip(int vip) {
        this.vip = vip;
    }

    public int getVip() {
        return this.vip;
    }

    public void setLevelid(String levelid) {
        this.levelid = levelid;
    }

    public String getLevelid() {
        return this.levelid;
    }

    public void setTo_client_id(String to_client_id) {
        this.to_client_id = to_client_id;
    }

    public String getTo_client_id() {
        return this.to_client_id;
    }

    public void setTo_client_name(String to_client_name) {
        this.to_client_name = to_client_name;
    }

    public String getTo_client_name() {
        return this.to_client_name;
    }

    public void setTo_user_id(String to_user_id) {
        this.to_user_id = to_user_id;
    }

    public String getTo_user_id() {
        return this.to_user_id;
    }

    public void setPub(String pub) {
        this.pub = pub;
    }

    public String getPub() {
        return this.pub;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return this.time;
    }

    public ContentMsg getContent() {
        return content;
    }

    public void setContent(ContentMsg content) {
        this.content = content;
    }

    public class ContentMsg{
        //        conference_invitation邀请
//        conference_invitation_return 返回
        private String conference_invitation;
        private String conference_type;
        private int conference_invitation_return;

        public String getConference_type() {
            return conference_type;
        }

        public void setConference_type(String conference_type) {
            this.conference_type = conference_type;
        }

        public String getConference_invitation() {
            return conference_invitation;
        }

        public void setConference_invitation(String conference_invitation) {
            this.conference_invitation = conference_invitation;
        }

        public int getConference_invitation_return() {
            return conference_invitation_return;
        }

        public void setConference_invitation_return(int conference_invitation_return) {
            this.conference_invitation_return = conference_invitation_return;
        }

        @Override
        public String toString() {
            return "ContentMsg{" +
                    "conference_invitation='" + conference_invitation + '\'' +
                    ", conference_type='" + conference_type + '\'' +
                    ", conference_invitation_return='" + conference_invitation_return + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "UserPrvMsg{" +
                "_method_='" + _method_ + '\'' +
                ", from_client_id='" + from_client_id + '\'' +
                ", from_user_id='" + from_user_id + '\'' +
                ", from_client_name='" + from_client_name + '\'' +
                ", vip=" + vip +
                ", levelid='" + levelid + '\'' +
                ", to_client_id='" + to_client_id + '\'' +
                ", to_client_name='" + to_client_name + '\'' +
                ", to_user_id='" + to_user_id + '\'' +
                ", pub='" + pub + '\'' +
                ", content=" + content +
                ", time='" + time + '\'' +
                '}';
    }
}
