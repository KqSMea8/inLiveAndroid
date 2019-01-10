package tw.chiae.inlive.util;

import android.app.ProgressDialog;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2016/6/17 0017.
 */
public class DownLoadUtil {
    public static File getFileFromServer(String path, ProgressDialog pd) throws Exception{
        //如果相等的话表示当前的sdcard挂载在手机上并且是可用的
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            URL url = new URL(path);
            HttpURLConnection conn =  (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            //获取到文件的大小
            pd.setMax(conn.getContentLength());
            InputStream is = conn.getInputStream();
            File file = new File(Environment.getExternalStorageDirectory(), "updata.apk");
            FileOutputStream fos = new FileOutputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            byte[] buffer = new byte[1024];
            int len ;
            int total=0;
            while((len =bis.read(buffer))!=-1){
                fos.write(buffer, 0, len);
                total+= len;
                //获取当前下载量
                pd.setProgress(total);
            }
            fos.close();
            bis.close();
            is.close();
            return file;
        }
        else{
            return null;
        }
    }
    public static String convertFileSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;

        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else
            return String.format("%d B", size);
    }
//    单位换算
    public static String conversionNumber(int number){
        if(number/100000000>0){
            return number/100000000+"億";
        }else if(number/10000>0){
            return number/10000+"萬";
        }else{
            return Integer.toString(number);
        }
    }
    //    单位换算
    public static String conversionNumber(Long number){
        if(number/100000000>0){
            return number/100000000+"亿";
        }else if(number/10000>0){
            return number/10000+"万";
        }else{
            return String.valueOf(number);
        }
    }

    //    单位换算
    public static String conversionNumberDoubel(double number) {
        if (number / 100000000.0d >=1.0d) {
            return new java.text.DecimalFormat("#").format(number / 100000000.0d)+"億";
        } else if (number / 10000.0d >= 1.0d) {
            return new java.text.DecimalFormat("#").format(number / 10000.0d)+"萬";
        } else {
            return new java.text.DecimalFormat("#").format(number);
        }
    }
}
