package tw.chiae.inlive.data.bean.websocket;

import com.google.gson.annotations.SerializedName;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public class WsPongRequest implements WsRequest {

    /**
     * _method_ : pong
     */

    @SerializedName("_method_")
    private String method;
    private String device;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }
}
