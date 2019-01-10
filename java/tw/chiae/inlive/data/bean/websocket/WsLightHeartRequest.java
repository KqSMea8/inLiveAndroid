package tw.chiae.inlive.data.bean.websocket;

import com.google.gson.annotations.SerializedName;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public class WsLightHeartRequest implements WsRequest {

    @SerializedName("_method_")
    private String method;

    @SerializedName("color")
    private int colorIndex;

    public String getApproveid() {
        return approveid;
    }

    public void setApproveid(String approveid) {
        this.approveid = approveid;
    }

    private String approveid ;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public int getColorIndex() {
        return colorIndex;
    }

    public void setColorIndex(int colorIndex) {
        this.colorIndex = colorIndex;
    }

    @Override
    public String toString() {
        return "WsLightHeartRequest{" +
                "method='" + method + '\'' +
                ", colorIndex=" + colorIndex +
                '}';
    }
}
