package tw.chiae.inlive.data.bean.room;

/**
 * Created by Administrator on 2016/12/15 0015.
 */

public class PrivateLimitBean {
    /**
     * 私密限制id
     */
    private int id;
    /**
     * 回播表id
     */
    private int bsid;
    /**
     * 私密类型id
     */
    private int ptid;
    /**
     * 私密限制条件
     */
    private String prerequisite;
    /**
     * 私密类型名称
     */
    private String ptname;

    /**
     * 1就是该次私密直播 已经购买了
     */
    //获取现在是不是在直播
    private int online;

    public int getOnline() {
        return online;
    }

    public void setOnline(int online) {
        this.online = online;
    }

    private int come;
    public int getId() {
        return id;
    }

    public int getBsid() {
        return bsid;
    }

    public int getPtid() {
        return ptid;
    }

    public String getPrerequisite() {
        return prerequisite;
    }

    public String getPtname() {
        return ptname;
    }

    public int getCome() {
        return come;
    }

    @Override
    public String toString() {
        return "PrivateLimitBean{" +
                "bsid=" + bsid +
                ", id=" + id +
                ", ptid=" + ptid +
                ", prerequisite='" + prerequisite + '\'' +
                ", ptname='" + ptname + '\'' +
                ", online=" + online +
                ", come=" + come +
                '}';
    }
}
