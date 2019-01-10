package tw.chiae.inlive.data.bean.websocket;

import com.google.gson.annotations.SerializedName;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public class WsLogoutRequest implements WsRequest {

    /**
     * _method_ : logout
     * user_name : 东方不败哪
     * user_id : 804
     * levelid : 16
     * levelname : 十六级
     * daoju :
     * token : 2d0f8f7d21163cf1cda5aaca594aaf88
     * ucuid : 749
     * room_id : 1181801694
     */

    @SerializedName("_method_")
    private String method;
    @SerializedName("user_name")
    private String userName;
    @SerializedName("user_id")
    private String userId;
    @SerializedName("levelid")
    private String levelId;
    @SerializedName("levelname")
    private String levelName;
    private String daoju;
    private String token;
    private String ucuid;
    @SerializedName("room_id")
    private String roomId;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLevelId() {
        return levelId;
    }

    public void setLevelId(String levelId) {
        this.levelId = levelId;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public String getDaoju() {
        return daoju;
    }

    public void setDaoju(String daoju) {
        this.daoju = daoju;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUcuid() {
        return ucuid;
    }

    public void setUcuid(String ucuid) {
        this.ucuid = ucuid;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}
