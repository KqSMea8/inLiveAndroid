package tw.chiae.inlive.data.bean.transaction;

/**
 * Created by huanzhang on 2016/5/13.
 */
public class WithDrawRespose {

    /**
     * uid : 825
     * cash : 100.00
     * time : 1463071552
     */

    private String uid;
    private String cash;
    private int time;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCash() {
        return cash;
    }

    public void setCash(String cash) {
        this.cash = cash;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
