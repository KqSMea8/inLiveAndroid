package tw.chiae.inlive.data.bean.transaction;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public class RechargeInfo {
    @SerializedName("coinbalance")
    private double coinBalance;
    private List<RechargeMapItem> list;

    public double getCoinBalance() {
        return coinBalance;
    }

    public void setCoinBalance(double coinBalance) {
        this.coinBalance = coinBalance;
    }

    public List<RechargeMapItem> getList() {
        return list;
    }

    public void setList(List<RechargeMapItem> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "RechargeInfo{" +
                "coinBalance=" + coinBalance +
                ", list=" + list +
                '}';
    }
}
