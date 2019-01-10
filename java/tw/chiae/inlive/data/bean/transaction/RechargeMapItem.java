package tw.chiae.inlive.data.bean.transaction;

import com.google.gson.annotations.SerializedName;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public class RechargeMapItem {

    /**
     * diamond : 20秀币
     * msg : 新人礼包仅一次机会
     * rmb : 1
     */

    @SerializedName("diamond")
    private String currencyAmount;
    private String msg;
    @SerializedName("rmb")
    private String rmbAmount;

    public String getCurrencyAmount() {
        return currencyAmount;
    }

    public void setCurrencyAmount(String currencyAmount) {
        this.currencyAmount = currencyAmount;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getRmbAmount() {
        return rmbAmount;
    }

    public void setRmbAmount(String rmbAmount) {
        this.rmbAmount = rmbAmount;
    }
}
