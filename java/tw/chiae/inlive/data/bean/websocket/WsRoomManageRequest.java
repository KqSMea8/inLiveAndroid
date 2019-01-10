package tw.chiae.inlive.data.bean.websocket;

import com.google.gson.annotations.SerializedName;

/**
 * 房间管理相关操作。
 * @author Muyangmin
 * @since 1.0.0
 */
public class WsRoomManageRequest implements WsRequest {

    //'adminer','removeAdminer','addKicked','removeKicked','disableMsg','enableMsg'

//    操作
    public static final String MANAGE="Manage";
    /**
     * 踢人操作。
     */
    public static final String TYPE_KICK = "addKicked";
//    设置管理员
    public static final  String ADMINER="adminer";
//      溢出管理员
    public static final  String REMOVERADMINER="removeAdminer";
//    恢复踢人
//    public static final String REMOVEKICKED="removeKicked";
//    禁言
    public static final String DISABLEMSG="disableMsg";
//    恢复发言
//    public static final String ENABLEMSG="enableMsg";
    /**
     * _method_ : Manage
     * _type_ : disableMsg
     * managed_user_id : 814
     * managed_user_name : 7恋歌灬l.用心唱歌
     */

    @SerializedName("_method_")
    private String method;
    @SerializedName("_type_")
    private String type;
    @SerializedName("managed_user_id")
    private String targetUserId;
    @SerializedName("managed_user_name")
    private String targetUsername;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(String targetUserId) {
        this.targetUserId = targetUserId;
    }

    public String getTargetUsername() {
        return targetUsername;
    }

    public void setTargetUsername(String targetUsername) {
        this.targetUsername = targetUsername;
    }
}
