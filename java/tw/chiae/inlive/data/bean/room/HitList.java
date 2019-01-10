package tw.chiae.inlive.data.bean.room;

/**
 * Created by Administrator on 2016/7/22.
 */

public class HitList {

    private String id;
    private String username;
    private String nickname;
    private String curroomnum;
    private int sex;
    private String emceelecel;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getCurroomnum() {
        return curroomnum;
    }

    public void setCurroomnum(String curroomnum) {
        this.curroomnum = curroomnum;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getEmceelecel() {
        return emceelecel;
    }

    public void setEmceelecel(String emceelecel) {
        this.emceelecel = emceelecel;
    }
}
