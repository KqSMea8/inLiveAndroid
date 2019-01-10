package tw.chiae.inlive.data.bean.websocket;

import com.google.gson.annotations.SerializedName;

/**
 * 登录到IM房间返回的信息。
 *
 * @author Muyangmin
 * @since 1.0.0
 */
public class WsLoginMsg {

    /**
     * type : login
     * client_id : 8b8113be08fe00000007
     * client_name : ╰♡桃♡╯℃桃子
     * user_id : 807
     * ucuid : 752
     * vip : 1
     * levelid : 18
     * time : 00:05
     * role : adminer
     * daoju :
     * client_list : []
     * adminer_list : []
     */

    @SerializedName("client_id")
    private String clientId;
    @SerializedName("client_name")
    private String clientName;
    @SerializedName("user_id")
    private String userIid;
    private String ucuid;
    private String vip;
    @SerializedName("levelid")
    private String level;
    private String time;
    private String role;
    private String daoju;
    private String approveid;
    private long hotpoint;

    public long getHotpoint() {
        if(hotpoint<=0)
            hotpoint=0;
        return hotpoint;
    }

    public void setHotpoint(long hotpoint) {
        this.hotpoint = hotpoint;
    }


    public String getApproveid() {
        return approveid;
    }

    public void setApproveid(String approveid) {
        this.approveid = approveid;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getUserIid() {
        return userIid;
    }

    public void setUserIid(String userIid) {
        this.userIid = userIid;
    }

    public String getUcuid() {
        return ucuid;
    }

    public void setUcuid(String ucuid) {
        this.ucuid = ucuid;
    }

    public String getVip() {
        return vip;
    }

    public void setVip(String vip) {
        this.vip = vip;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDaoju() {
        return daoju;
    }

    public void setDaoju(String daoju) {
        this.daoju = daoju;
    }
}
