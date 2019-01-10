package tw.chiae.inlive.data.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public class IncomeBean implements Parcelable {

    /**
     * earnbean : 0
     * rmb : 0.00
     * alipayname :
     */

    private String earnbean;
    private String rmb;
    private String alipayname;

    public String getEarnbean() {
        return earnbean;
    }

    public void setEarnbean(String earnbean) {
        this.earnbean = earnbean;
    }

    public String getRmb() {
        return rmb;
    }

    public void setRmb(String rmb) {
        this.rmb = rmb;
    }

    public String getAlipayname() {
        return alipayname;
    }

    public void setAlipayname(String alipayname) {
        this.alipayname = alipayname;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.earnbean);
        dest.writeString(this.rmb);
        dest.writeString(this.alipayname);
    }

    public IncomeBean() {
    }

    protected IncomeBean(Parcel in) {
        this.earnbean = in.readString();
        this.rmb = in.readString();
        this.alipayname = in.readString();
    }

    public static final Parcelable.Creator<IncomeBean> CREATOR = new Parcelable
            .Creator<IncomeBean>() {
        @Override
        public IncomeBean createFromParcel(Parcel source) {
            return new IncomeBean(source);
        }

        @Override
        public IncomeBean[] newArray(int size) {
            return new IncomeBean[size];
        }
    };
}
