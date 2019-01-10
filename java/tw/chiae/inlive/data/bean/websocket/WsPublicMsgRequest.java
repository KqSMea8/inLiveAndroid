package tw.chiae.inlive.data.bean.websocket;

import com.google.gson.annotations.SerializedName;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public class WsPublicMsgRequest implements WsRequest {

    /**
     * _method_ : SendPubMsg
     * client_name : 东方不败哪
     * content : Send
     * checksum :
     * user_id  814/levelid 11 / ucuid 759 /vip ''
     */

    @SerializedName("_method_")
    private String method;
    @SerializedName("client_name")
    private String clientName;
    private String content;
    private String checksum;
    private String approveid;
    public String getApproveid() {
        return approveid;
    }

    public void setApproveid(String approveid) {
        this.approveid = approveid;
    }




    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

}
