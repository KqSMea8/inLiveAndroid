package tw.chiae.inlive.presentation.ui.widget;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * Created by rayyeh on 2017/5/23.
 */

public class MD5Tool
{

    private final String mZip;
    private final int mRandval;
    private final long mCurrentTime;
    private final String mDevice;
    String url = "https://member.inlive.tw/income-02.php?";

    public MD5Tool(String zip, String version) {
        this.mZip = zip;
        this.mRandval = new Random().nextInt(10000)+10000;
        this.mCurrentTime = System.currentTimeMillis();
        this.mDevice ="android"+version;
        initData();
    }

    private void initData() {

    }

    public String getMD5Str(String str)
    {
        MessageDigest messageDigest = null;
        try
        {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e)
        {
            System.out.println("NoSuchAlgorithmException caught!");
            System.exit(-1);
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        byte[] byteArray = messageDigest.digest();

        StringBuffer md5StrBuff = new StringBuffer();

        for (int i = 0; i < byteArray.length; i++)
        {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
        }
        return md5StrBuff.toString();
    }

    public String getUrl() {

        try {
            String md5 = "$IZzFpnlrWp"+mCurrentTime+mZip+mRandval;
            String str = getMD5Str(md5);
            String postData = "zid=" + URLEncoder.encode(mZip, "UTF-8") +
                    "&ss_id=" + URLEncoder.encode(str + mRandval, "UTF-8") +
                    "&sys_device=" + URLEncoder.encode(mDevice, "UTF-8") +
                    "&datetime=" + URLEncoder.encode(mCurrentTime + "", "UTF-8");
            return url+postData;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }
}