package tw.chiae.inlive.presentation.ui.room;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;

/**
 * Created by rayyeh on 2017/8/4.
 */

public class PriConversation {

    private String conversationID;
    private long time;
    private String avt;
    private String approveid;
    private String nickName;
    private String userID;
    private Message lastMsg;
    private String token;


    public void setConversationID(String conversationID) {
        this.conversationID = conversationID;
    }

    public String getConversationID() {
        return conversationID;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public void setAvt(String avt) {
        this.avt = avt;
    }

    public String getAvt() {
        return avt;
    }

    public void setApproveid(String approveid) {
        this.approveid = approveid;
    }

    public String getApproveid() {
        return approveid;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserID() {
        return userID;
    }

    public void setLastMsg(Message lastMsg) {
        this.lastMsg = lastMsg;
    }

    public Message getLastMsg() {
        return lastMsg;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PriConversation) {
            PriConversation con = (PriConversation) obj;
            return this.userID.equals(con.getUserID());
        }
        return super.equals(obj);
    }

    public Conversation getJConversion() {
        return JMessageClient.getSingleConversation("user"+getUserID());
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
