package tw.chiae.inlive.data.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2016/6/13.
 */

public class AnchoBean {

    private String token;

    private String user_id;

    @SerializedName("beanbalance")
    private String beanbalance="0";

    public String getBeanbalance() {
        return beanbalance;
    }

    public void setBeanbalance(String beanbalance) {
        this.beanbalance = beanbalance;
    }
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
