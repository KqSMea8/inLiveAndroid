package tw.chiae.inlive.data.bean.me;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import tw.chiae.inlive.data.bean.LoginInfo;
import tw.chiae.inlive.util.UnicodeUtil;

/**
 * 用户个人资料，即Profile。
 * Created by huanzhang on 2016/4/15.
 */
public class UserInfo implements Parcelable {

    public static transient final int GENDER_MALE = 0;
    public static transient final int GENDER_FEMALE = 1;

    public static final int IS_ATTENTION = 1;
    /**
     * id : 805
     * sex : 0
     * intro :
     * nickname : h825923
     * city : 火星
     * snap : null
     * curroomnum : 1866654681
     * vip : 0
     * coinbalance : 0
     * anchorBalance: 0
     * avatar : passport/avatar.php?uid=750&size=middle
     * followers_cnt : 0   //粉丝
     * followees_cnt : 0  //关注
     * total_contribution : 19320434
     * "is_attention": 1,//调用者是否关注了该UID用户。
     * emceelevel : 1
     * contribute：[
     * "/passport/avatar.php?uid=749&size=middle",
     * "/passport/avatar.php?uid=798&size=middle",
     * "/passport/avatar.php?uid=758&size=middle"
     * ]
     *
     * coinbalance 充值的余额
     * beanbalance 未折算收益
     * earnbean 折算后收益 提现那个
     * anchorBalance 是历史遗留问题！！！  他就是beanorignal
     * beanorignal 魅力值
     * 2017-3-9 14:48:53 by.zhangkaixiang
     */
    private String id;
    private int sex;
    private String intro;
    private String nickname;
    private String city;
    private String snap;

    @SerializedName("curroomnum")
    private String currentRoomNum = "0";
    private String vip;
    //  充值的余额
    @SerializedName("coinbalance")
    private double coinBalance;
    //折算后收益 提现那个
    private double earnbean;
    //    未折算收益
    private double beanbalance;
    //      魅力值
    private double beanorignal;
    //    回播总数
    private String playBackCount;
    //    年龄
    private String birthday;
    //    婚姻
    private String emotion;
    //    家乡
    private String province;
    //    职业
    private String professional;
    //  是否被啦黑 1被拉黑
    private int isHit;
    private String avatar;
    @SerializedName("contribute")
    private List<String> topContributeUsers;
    /**
     * 粉丝数
     */
    @SerializedName("followers_cnt")
    private String followersCount = "0";
    /**
     * 关注数
     */
    @SerializedName("followees_cnt")
    private String followeesCount = "0";
    @SerializedName("total_contribution")
    private int totalContribution;
    @SerializedName("is_attention")
    private int isAttention;
    @SerializedName("emceelevel")
    private String level = "1";
    //  是否在直播 n 或者y
    private String broadcasting;
    // 绑定微信了么
    private String wxunionid;
    //    认证
    private String approveid;
    //    年龄
    private String age;
    //推荐人的name
    private String recommendation;
    private int MaxLevel = 128;

    public UserInfo() {}

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UserInfo) {
            UserInfo info = (UserInfo) obj;
            return this.id.equals(info.getId());
        }
        return super.equals(obj);
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getProfessional() {
        return professional;
    }

    public void setProfessional(String professional) {
        this.professional = professional;
    }

    public String getBroadcasting() {
        return broadcasting;
    }

    public void setBroadcasting(String broadcasting) {
        this.broadcasting = broadcasting;
    }

    public int getIsAttention() {
        return isAttention;
    }

    public void setIsAttention(int isAttention) {
        this.isAttention = isAttention;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getNickname() {

        String Prefix = "fb_";
        if(nickname.contains(Prefix)){
            nickname = nickname.substring(Prefix.length(),nickname.length());
        }

        nickname = UnicodeUtil.StringUtfDecode(nickname);
        return nickname;
    }

    public void setNickname(String nickname) {
        nickname = UnicodeUtil.StringUtfDecode(nickname);
        this.nickname = nickname;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getSnap() {
        return snap;
    }

    public void setSnap(String snap) {
        this.snap = snap;
    }

    public String getCurrentRoomNum() {
        return currentRoomNum;
    }

    public void setCurrentRoomNum(String currentRoomNum) {
        this.currentRoomNum = currentRoomNum;
    }

    public String getVip() {
        return vip;
    }

    public void setVip(String vip) {
        this.vip = vip;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(String followersCount) {
        this.followersCount = followersCount;
    }

    public String getFolloweesCount() {
        return followeesCount;
    }

    public void setFolloweesCount(String followeesCount) {
        this.followeesCount = followeesCount;
    }

    public int getTotalContribution() {
        return totalContribution;
    }

    public void setTotalContribution(int totalContribution) {
        this.totalContribution = totalContribution;
    }

    public List<String> getTopContributeUsers() {
        return topContributeUsers;
    }

    public void setTopContributeUsers(List<String> topContributeUsers) {
        this.topContributeUsers = topContributeUsers;
    }

    public String getLevel() {
        if(Integer.parseInt(level)==0 ){
            level = "1";
        }
        if( Integer.parseInt(level)>MaxLevel){
            level = MaxLevel+"";
        }
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getLove() {
        return emotion;
    }

    public void setLove(String love) {
        this.emotion = love;
    }

    public String getHome() {
        return province;
    }

    public void setHome(String home) {
        this.province = home;
    }

    public String getMajor() {
        return professional;
    }

    public void setMajor(String major) {
        this.professional = major;
    }

    public int getIsHit() {
        return isHit;
    }

    public void setIsHit(int isHit) {
        this.isHit = isHit;
    }

    public String getPlayBackCount() {
        return playBackCount;
    }

    public void setPlayBackCount(String playBackCount) {
        this.playBackCount = playBackCount;
    }

    public String getWxunionid() {
        return wxunionid;
    }

    public void setWxunionid(String wxunionid) {
        this.wxunionid = wxunionid;
    }

    public String getApproveid() {
        return approveid;
    }

    public void setApproveid(String approveid) {
        this.approveid = approveid;
    }

    public double getBeanorignal() {
        return beanorignal;
    }

    public void setBeanorignal(double beanorignal) {
        this.beanorignal = beanorignal;
    }

    public double getCoinBalance() {
        return coinBalance;
    }

    public void setCoinBalance(double coinBalance) {
        this.coinBalance = coinBalance;
    }

    public double getEarnbean() {
        return earnbean;
    }

    public void setEarnbean(double earnbean) {
        this.earnbean = earnbean;
    }

    public double getBeanbalance() {
        return beanbalance;
    }

    public void setBeanbalance(double beanbalance) {
        this.beanbalance = beanbalance;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeInt(this.sex);
        dest.writeString(this.intro);
        dest.writeString(this.nickname);
        dest.writeString(this.city);
        dest.writeString(this.snap);
        dest.writeString(this.currentRoomNum);
        dest.writeString(this.vip);
        dest.writeString(this.avatar);
        dest.writeStringList(this.topContributeUsers);
        dest.writeString(this.followersCount);
        dest.writeString(this.followeesCount);
        dest.writeInt(this.totalContribution);
        dest.writeInt(this.isAttention);
        dest.writeString(this.level);
        dest.writeString(this.broadcasting);
        dest.writeString(this.birthday);
        dest.writeString(this.province);
        dest.writeString(this.emotion);
        dest.writeString(this.professional);
        dest.writeInt(this.isHit);
        dest.writeString(this.playBackCount);
        dest.writeString(this.wxunionid);
        dest.writeString(this.approveid);
        dest.writeString(this.age);
        dest.writeString(this.recommendation);
        dest.writeDouble(this.beanorignal);
        dest.writeDouble(this.coinBalance);
        dest.writeDouble(this.beanbalance);
        dest.writeDouble(this.earnbean);
    }

    protected UserInfo(Parcel in) {
        this.id = in.readString();
        this.sex = in.readInt();
        this.intro = in.readString();
        this.nickname = in.readString();
        this.city = in.readString();
        this.snap = in.readString();
        this.currentRoomNum = in.readString();
        this.vip = in.readString();
        this.avatar = in.readString();
        this.topContributeUsers = in.createStringArrayList();
        this.followersCount = in.readString();
        this.followeesCount = in.readString();
        this.totalContribution = in.readInt();
        this.isAttention = in.readInt();
        this.level = in.readString();
        this.broadcasting = in.readString();
        this.birthday = in.readString();
        this.province = in.readString();
        this.emotion = in.readString();
        this.professional = in.readString();
        this.isHit = in.readInt();
        this.playBackCount = in.readString();
        this.wxunionid = in.readString();
        this.approveid = in.readString();
        this.age = in.readString();
        this.recommendation=in.readString();
        this.beanorignal=in.readDouble();
        this.earnbean=in.readDouble();
        this.beanbalance = in.readDouble();
        this.coinBalance = in.readDouble();
    }


    @Override
    public String toString() {
        return "UserInfo{" +
                "id='" + id + '\'' +
                ", sex=" + sex +
                ", intro='" + intro + '\'' +
                ", nickname='" + nickname + '\'' +
                ", city='" + city + '\'' +
                ", snap='" + snap + '\'' +
                ", currentRoomNum='" + currentRoomNum + '\'' +
                ", vip='" + vip + '\'' +
                ", coinBalance='" + coinBalance + '\'' +
                ", playBackCount='" + playBackCount + '\'' +
                ", birthday='" + birthday + '\'' +
                ", emotion='" + emotion + '\'' +
                ", province='" + province + '\'' +
                ", professional='" + professional + '\'' +
                ", isHit=" + isHit +
                ", avatar='" + avatar + '\'' +
                ", topContributeUsers=" + topContributeUsers +
                ", earnbean='" + earnbean + '\'' +
                ", followersCount='" + followersCount + '\'' +
                ", followeesCount='" + followeesCount + '\'' +
                ", totalContribution=" + totalContribution +
                ", isAttention=" + isAttention +
                ", level='" + level + '\'' +
                ", broadcasting='" + broadcasting + '\'' +
                ", beanbalance='" + beanbalance + '\'' +
                ", wxunionid='" + wxunionid + '\'' +
                ", approveid='" + approveid + '\'' +
                ", age='" + age + '\'' +
                ", recommendation='" + recommendation + '\'' +
                '}';
    }

    public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel source) {
            return new UserInfo(source);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };
}
