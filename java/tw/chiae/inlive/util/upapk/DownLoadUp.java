package tw.chiae.inlive.util.upapk;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Administrator on 2016/6/21 0021.
 */
public class DownLoadUp {
//    下载地址
    public String downLoadurl;
//  下载到的路径
    public String appPath;
//   下载以后app的名字
    public String appName;
//    进度
    public long progress;
//    上下文
    public Context context;
//    文件
    public File mFile;

    public DownLoadUp() {
    }

    public DownLoadUp(String downLoadurl, String appPath, String name, Context context) {
        this.downLoadurl = downLoadurl;
        this.appPath=appPath;
        this.appName=name;
        this.context=context;
    }

    public void startDownLoad(){
        new DownLoadThread().start();
    }

    class DownLoadThread extends Thread{
        @Override
        public void run() {
            super.run();
            //如果相等的话表示当前的sdcard挂载在手机上并且是可用的
            if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                HttpURLConnection conn=null;
                InputStream inputStream=null;
                FileOutputStream fos=null;
                BufferedInputStream bis=null;
                try {
                    URL url=new URL(downLoadurl);
                    conn= (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setRequestMethod("GET");
                    long length=-1;
                    if (conn.getResponseCode()==HttpURLConnection.HTTP_OK){
                        length=conn.getContentLength();
                    }else {
                        return;
                    }
                    if (length<=0){
                        return;
                    }
                    File dir=new File(appPath);
                    if (!dir.exists()){
                        dir.mkdirs();
                    }
                    File file=new File(appPath,appName);

//                    广播
                    Intent intent = new Intent(DownLoadService.ACTION_UPDATA);
                    inputStream=conn.getInputStream();
                    intent.setAction(DownLoadService.ACTION_START);
                    byte[] buffer=new byte[1024*4];
                    int len=-1;
//                    时间戳,用来阶段性发送广播
                    long times=System.currentTimeMillis();
                    fos = new FileOutputStream(file);
                    bis = new BufferedInputStream(inputStream);
                    while((len =bis.read(buffer))!=-1){
                        fos.write(buffer, 0, len);
                        progress+= len;
                        if (System.currentTimeMillis()-times>500) {
                            times=System.currentTimeMillis();
                            intent.putExtra(DownLoadService.SEND_PROGRESS, progress * 100 /length);
                            context.sendBroadcast(intent);
                        }
                    }
                    mFile=file;
                    Intent intent1=new Intent(DownLoadService.ACTION_END);
                    intent.putExtra(DownLoadService.ACTION_END,file.getPath());
                    intent.setAction(DownLoadService.ACTION_END);
                    context.sendBroadcast(intent1);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    try {
                        if (fos!=null) {
                            fos.close();
                        }
                        if (bis!=null) {
                            bis.close();
                        }
                        if (inputStream!=null) {
                            inputStream.close();
                        }
                        if (conn!=null){
                            conn.disconnect();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
