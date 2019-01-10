package tw.chiae.inlive.data.bean.room;

import android.os.Parcel;
import android.os.Parcelable;

import tw.chiae.inlive.data.bean.HotAnchorSummary;

/**
 * Created by Administrator on 2016/12/14 0014.
 */

public class CreateRoomBean implements Parcelable {
    private int bsid;
    private int ptid;
    private String prerequisite;
    private int plid;
    private String privatemsg;
    private String callback_data;
    private String createroom;

    public int getBsid() {
        return bsid;
    }

    public void setBsid(int bsid) {
        this.bsid = bsid;
    }

    public int getPtid() {
        return ptid;
    }

    public void setPtid(int ptid) {
        this.ptid = ptid;
    }

    public String getPrerequisite() {
        return prerequisite;
    }

    public void setPrerequisite(String prerequisite) {
        this.prerequisite = prerequisite;
    }

    public int getPlid() {
        return plid;
    }

    public void setPlid(int plid) {
        this.plid = plid;
    }

    public String getPrivatemsg() {
        return privatemsg;
    }

    public void setPrivatemsg(String privatemsg) {
        this.privatemsg = privatemsg;
    }

    public String getCallback_data() {
        return callback_data;
    }

    public void setCallback_data(String callback_data) {
        this.callback_data = callback_data;
    }

    public String getCreateroom() {
        return createroom;
    }

    public void setCreateroom(String createroom) {
        this.createroom = createroom;
    }

    @Override
    public String toString() {
        return "CreateRoomBean{" +
                "bsid=" + bsid +
                ", ptid=" + ptid +
                ", prerequisite=" + prerequisite +
                ", plid=" + plid +
                ", privatemsg='" + privatemsg + '\'' +
                ", callback_data='" + callback_data + '\'' +
                ", createroom='" + createroom + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.bsid);
        dest.writeInt(this.ptid);
        dest.writeString(this.prerequisite);
        dest.writeInt(this.plid);
        dest.writeString(this.privatemsg);
        dest.writeString(this.callback_data);
        dest.writeString(this.createroom);
    }

    protected CreateRoomBean(Parcel in) {
        this.bsid = in.readInt();
        this.ptid = in.readInt();
        this.prerequisite = in.readString();
        this.plid = in.readInt();
        this.privatemsg = in.readString();
        this.callback_data = in.readString();
        this.createroom = in.readString();
    }

    public static final Parcelable.Creator<CreateRoomBean> CREATOR = new Parcelable.Creator<CreateRoomBean>() {
        @Override
        public CreateRoomBean createFromParcel(Parcel source) {
            return new CreateRoomBean(source);
        }

        @Override
        public CreateRoomBean[] newArray(int size) {
            return new CreateRoomBean[size];
        }
    };
}
