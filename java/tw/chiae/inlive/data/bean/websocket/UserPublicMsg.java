package tw.chiae.inlive.data.bean.websocket;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import tw.chiae.inlive.util.Const;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public class UserPublicMsg implements RoomPublicMsg {


    /**
     * type : SendPubMsg
     * from_client_id : 7f00000108ff00000150
     * from_client_name : 小＆魅力播
     * to_client_id : all
     * content : 我是歌手
     * time : 21:15:16
     * vip : false
     * levelid : 11
     *
     * **NOTE**: since 0.3.0, vip changed to integer type instead of boolean, to support different
     * vip level, e.g. vip1, vip2.
     */

    private String type;
    @SerializedName("from_client_id")
    private String fromClientId;
    @SerializedName("from_client_name")
    private String fromClientName;
    @SerializedName("to_client_id")
    private String toClientId;
    private String content;
    private String time;
    @SerializedName("vip")
    private int vipLevel;
    @SerializedName("levelid")
    private int level;
    private String from_user_id;
    private String fly;
    private String avatar;
    private String approveid ;
    public String getApproveid() {
        return approveid;
    }

    public void setApproveid(String approveid) {
        this.approveid = approveid;
    }


    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getFly() {
        return fly;
    }

    public void setFly(String fly) {
        this.fly = fly;
    }
    public String getUserId() {
        return from_user_id;
    }

    public void setUserId(String from_user_id) {
        this.from_user_id = from_user_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFromClientId() {
        return fromClientId;
    }

    public void setFromClientId(String fromClientId) {
        this.fromClientId = fromClientId;
    }

    public String getFromClientName() {
        String Prefix = "fb_";
        if(fromClientName.contains(Prefix)){
            fromClientName = fromClientName.substring(Prefix.length(),fromClientName.length());
        }
        return fromClientName;
    }

    public void setFromClientName(String fromClientName) {
        this.fromClientName = fromClientName;
    }

    public String getToClientId() {
        return toClientId;
    }

    public void setToClientId(String toClientId) {
        this.toClientId = toClientId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getVipLevel() {
        return vipLevel;
    }

    public void setVipLevel(int vipLevel) {
        this.vipLevel = vipLevel;
    }

    public int getLevel() {

        if(level<=0)
            level=1;
        if(level>=Const.MaxLevel)
            level= Const.MaxLevel;
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }


}
