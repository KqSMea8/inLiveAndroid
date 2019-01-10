package tw.chiae.inlive.data.bean;

/**
 * Created by Administrator on 2016/6/17 0017.
 */
public class UpDataBean {
    String apkversion;
    String apkaddress;

    public String getApkaddress() {
        return apkaddress;
    }

    public void setApkaddress(String apkaddress) {
        this.apkaddress = apkaddress;
    }

    public String getApkversion() {
        return apkversion;
    }

    public void setApkversion(String apkversion) {
        this.apkversion = apkversion;
    }

    @Override
    public String toString() {
        return "UpDataBean{" +
                "apkversion='" + apkversion + '\'' +
                ", apkaddress='" + apkaddress + '\'' +
                '}';
    }
}
