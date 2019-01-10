package tw.chiae.inlive.data.bean.transaction;

/**
 * Created by huanzhang on 2016/5/13.
 */
public class PresentRecordItem {
    /**
     * id : 19
     * uid : 879
     * cash : 399.95
     * time : 2016-05-13 01:39:44
     * confirmed : 0
     */

    private String id;
    private String uid;
    private String cash;
    private String time;
    private String confirmed="0";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(String confirmed) {
        this.confirmed = confirmed;
    }
}
