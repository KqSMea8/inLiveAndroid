package tw.chiae.inlive.data.bean.websocket;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public class LiveAudienceListMsg {

    /**
     * type : onLineClient
     * all_num : 1
     * viewer_num : 0
     * adminer_list : []
     * client_list : [{"client_name":"蝶恋","user_id":"829","room_id":"1350268125","ucuid":"759",
     * "client_id":"8b8113be08ff0033fa2b","vip":0,"levelid":"11","time":0,"msged":false}]
     */

    @SerializedName("all_num")
    private int totalCount;
    @SerializedName("viewer_num")
    private int guestCount;
    @SerializedName("adminer_list")
    private List<AudienceInfo> adminList;
    @SerializedName("client_list")
    private List<AudienceInfo> clientList;
    @SerializedName("anchor_live_status")
    private String liveStatus;
    @SerializedName("anchor_live")
    private String liveMsg;


    public String getLiveStatus() {
        return liveStatus;
    }

    public void setLiveStatus(String liveStatus) {
        this.liveStatus = liveStatus;
    }

    public String getLiveMsg() {
        return liveMsg;
    }

    public void setLiveMsg(String liveMsg) {
        this.liveMsg = liveMsg;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getGuestCount() {
        return guestCount;
    }

    public void setGuestCount(int guestCount) {
        this.guestCount = guestCount;
    }

    public List<AudienceInfo> getAdminList() {
        return adminList;
    }

    public void setAdminList(List<AudienceInfo> adminList) {
        this.adminList = adminList;
    }

    public List<AudienceInfo> getClientList() {
        return clientList;
    }

    public void setClientList(List<AudienceInfo> clientList) {
        this.clientList = clientList;
    }

    @Override
    public String toString() {
        return "LiveAudienceListMsg{" +
                "totalCount=" + totalCount +
                ", guestCount=" + guestCount +
                ", adminList=" + adminList +
                ", clientList=" + clientList +
                '}';
    }
}
