package tw.chiae.inlive.domain;

/**
 * Created by rayyeh on 2017/8/17.
 */

//[{"id":"86","uid":"1000787","room_id":"0","black_id":"1001222","nickname":"\u2744\ufe0f\u9b5a\u611b\u8389\u00ae\u2744\ufe0f","addtime":"1502967823","type":"1"}]
public class BlackList {

    private String id;
    private String uid;
    private String room_id;
    private String black_id;
    private String addtime;
    private String type;
    private String nickname;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public String getBlack_id() {
        return black_id;
    }

    public void setBlack_id(String black_id) {
        this.black_id = black_id;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


}
