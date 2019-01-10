package tw.chiae.inlive.data.bean;

import tw.chiae.inlive.util.Const;

/**
 * Created by huanzhang on 2016/5/8.
 */
public class CurrencyRankItem {
    public static transient final int GENDER_MALE = 0;
    public static transient final int GENDER_FEMALE = 1;
    /**
     * username : notvip
     * uid : 814
     * sex : 女
     * coin : 3614153330
     * avatar : /passport/avatar.php?uid=759&size=middle
     * levelid : 25
     */

    private String username;
    private String uid;
    private int sex;
    private String coin;
    private String avatar;
    private String levelid;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getLevelid() {
        int level = Integer.parseInt(levelid);
        if(level<=0)
            level=1;
        if(level> Const.MaxLevel)
            level=Const.MaxLevel;
        return level+"";
    }

    public void setLevelid(String levelid) {
        this.levelid = levelid;
    }
}
