package tw.chiae.inlive.data.bean.websocket;

/**
 * Created by Administrator on 2016/6/29.
 */

public class UserPrvMsg implements RoomPublicMsg {
//    public static final int  RECEIVE = 0;
//    public static final int SEND = 1;

    private String type;
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

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
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
                "type='" + type + '\'' +
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
