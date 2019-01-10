package tw.chiae.inlive.data.bean.room;

/**
 * Created by lww on 2016/7/22.
 */

public class PrvMsg {

    private String token;
    private String to_uid;
    private String content;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTo_uid() {
        return to_uid;
    }

    public void setTo_uid(String to_uid) {
        this.to_uid = to_uid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
