package tw.chiae.inlive.presentation.ui.login.splash;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.style.ForegroundColorSpan;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

import java.io.IOException;
import java.util.Timer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tw.chiae.inlive.data.repository.SourceFactory;
import tw.chiae.inlive.presentation.ui.main.mergefilm.Log;
import tw.chiae.inlive.presentation.ui.main.setting.CreateViewDialogFragment;
import tw.chiae.inlive.presentation.ui.main.webview.SimpleWebViewActivity;
import tw.chiae.inlive.presentation.ui.room.RoomInfoTmp;
import tw.chiae.inlive.util.Const;
import tw.chiae.inlive.util.Spans;

/**
 * Created by rayyeh on 2017/11/14.
 */

public class VersionChecker implements CreateViewDialogFragment.dialogCallback {
    private final AQuery aq;
    private final CreateViewDialogFragment dialogFragment;
    private final FragmentManager mFragmentManager;
    // private AppUpdaterUtils appUpdaterUtils;
    private Gson gson;
    private PackageInfo info = null;
    private String newVersion;
    private String googleAppUrl = "https://play.google.com/store/apps/details?id=";
    private VersionCheckInterface mCallback;
    private UpdateStatus update_status;
    private Context mContext;
    private String version_local;
    private boolean onPauseGotoStore;
    private String gashUrl = "https://drive.google.com/file/d/1mEG-tmN8P9sOTFiXDaDllZSNtc8O4Kcj/view";
    private String packInfoUrl = "http://api2.inlive.tw/api2/get_app_version";

    public boolean isCheckingVersion() {
        return !dialogFragment.isDetached();
    }

    public enum UpdateStatus{
        TYPE_NONE_UPDATE ,
        TYPE_SHOW_UPDATE ,
        TYPE_NEED_FOUCSE_UPDATE
    }


    public boolean checkFormat(String s) {

        String pattern = "(\\d{1,3}).(\\d{1,3}).(\\d{1,3})";
        Matcher mMatcher = Pattern.compile(pattern).matcher(s);
        if (mMatcher.find() && mMatcher.groupCount()==3) {
            Log.i("RayTest","checkFormat true");
            return true;
        }
        Log.i("RayTest","checkFormat false");
        return false;
    }

    @Override
    public void onOKDialogcheck(Bundle bundle) {
        if(mCallback==null)
            return;
        int itype = bundle.getInt("type");
        switch (itype){
            case CreateViewDialogFragment.TYPE_SHOW_UPDATE:
            case CreateViewDialogFragment.TYPE_NEED_FOUCSE_UPDATE:
                checkMode();
                dialogFragment.dismiss();
                break;


            default:
                mCallback.onVersionSucess(true);
                dialogFragment.dismiss();
                break;


        }
    }

    private void checkMode() {
        if(Const.IsPayMode)
        {
            gotoGashDownload();
        }else
        {
            gotoStoreDownload();
        }
    }

    private void gotoStoreDownload() {
        Log.i("RayTest","gotoStoreDownload");
        final String appPackageName = info.packageName; // getPackageName() from Context or Activity object
        onPauseGotoStore = true;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            Log.i("RayTest","gotoStoreDownload1");
            intent.setData( Uri.parse("market://details?id=" + appPackageName));
            mContext.startActivity(intent);
            //mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            Log.i("RayTest","gotoStoreDownload2");
            intent.setData( Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName));
            mContext.startActivity(intent);
            //mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            //mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    private void gotoGashDownload() {
        Log.i("RayTest","gotoGashDownload");
        Intent intent = SimpleWebViewActivity.createIntent(mContext,gashUrl,"INLIVE Gash APK 下載");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    @Override
    public void onCancelDialogcheck(Bundle bundle) {
        if(mCallback==null)
            return;
        int itype = bundle.getInt("type");
        switch (itype){
            case CreateViewDialogFragment.TYPE_SHOW_UPDATE:
                RoomInfoTmp.SHOW_UPDATE_SW = true;
                mCallback.onVersionSucess(true);
                break;
            default:
                mCallback.onVersionSucess(true);
                break;


        }
    }



    public VersionChecker(Context mContext, FragmentManager supportFragmentManager) {
        aq = new AQuery(mContext);
        this.mContext = mContext;
        this.mFragmentManager = supportFragmentManager;
        dialogFragment = CreateViewDialogFragment.newInstance();
        dialogFragment.setDialogCallback(this);

        try {
            PackageManager manager = mContext.getPackageManager();
            String packageName = mContext.getPackageName();
            info = manager.getPackageInfo(
                    packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if(info==null)
            return;
        gson = new Gson();
        
       // appUpdaterUtils = new AppUpdaterUtils(mContext);

    }
    

    public void getVersionName(final VersionCheckInterface callback) {
        this.mCallback = callback;
        aq.ajax(packInfoUrl, String.class,new AjaxCallback<String>(){
            @Override
            public void callback(String url, String json, AjaxStatus status) {

                if(json != null){

/*
                    Pattern version_pattern = Pattern.compile("\"softwareVersion\"\\W*([^<]*)");
                    //Pattern version_pattern = Pattern.compile("\"softwareVersion\"\\W*(\\d{1,3}).(\\d{1,3}).(\\d{1,3})");
                    Pattern content_pattern = Pattern.compile("\"recent-change\"\\W*>([^<]*)");
                    Matcher version_matcher = version_pattern.matcher(json);
                    Matcher content_matcher = content_pattern.matcher(json);
                    Log.i("RayTest","getVersionName :"+url);
                    if (!version_matcher.find()) {
                        //Log.i("RayTest","version_matcher fail "+json);
                        //mCallback.onVersionFail();
                        //return;
                    }
                    if (!content_matcher.find()) {
                        //Log.i("RayTest","content_matcher fail "+json);
                       // mCallback.onVersionFail();
                       // return;
                    }*/
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        String storeVer = jsonObject.getString("version");
                        boolean onSucess = showDialogFragment(storeVer);
                        mCallback.onVersionSucess(onSucess);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.i("RayTest","VersionResponse:"+json);
                    
                    //callback.VersionResponse(version_matcher.group(1).trim(),content_matcher.group(1).trim());
                   // boolean onSucess = showDialogFragment(version_matcher.group(1).trim(), content_matcher.group(1).trim());
                    //mCallback.onVersionSucess(true);
                   /* PackageVersionInfo infoItem = gson.fromJson(String.valueOf(json), PackageVersionInfo.class);
                    Log.i("RayAquery", "getApp:" + infoItem.getDialog().getWbody());
                    String wbody = infoItem.getDialog().getWbody();
                    String version = infoItem.getVersion();
                    if(infoItem!=null && !wbody.isEmpty()){
                        int size = Jsoup.parse(wbody).select("br").size();
                        String UpdateContent = wbody.split("<br/>")[2];
                        Log.i("RayAquery", "str:" + UpdateContent);
                        if(callback!=null){
                            callback.VersionResponse(version,UpdateContent);
                        }
                    }*/


                }else{
                    Log.i("RayTest","VersionResponse fail");
                }
            }
        });
    }
    
   
    private boolean showDialogFragment(String version_store) {
        version_local = info.versionName;
        VersionChecker.UpdateStatus updateType = ComparisonVersion(version_local, version_store);
        CharSequence newVersion = Spans.createSpan("", version_store, new ForegroundColorSpan(Color.BLUE));
        CharSequence tip = "inLive有新版本啦！為了讓您有最好的體驗並確保APP正常運行，請至商店更新至最新版("+newVersion+")，謝謝。\n\n"+"";

        if(updateType ==VersionChecker.UpdateStatus.TYPE_NONE_UPDATE){
            android.util.Log.i("RayTest","show TYPE_NONE_UPDATE");
            //LoginPrepare();

                    /*if(Const.TEST_ENVIROMENT_SW && !isEnableDebugMode){
                        showDebugMode(MaxNumber);
                    }else{
                        LoginPrepare();
                    }*/
                    return true;

        }

        if(updateType ==VersionChecker.UpdateStatus.TYPE_SHOW_UPDATE){
            if(RoomInfoTmp.SHOW_UPDATE_SW)
                return true;
            android.util.Log.i("RayTest","show TYPE_SHOW_UPDATE");
            dialogFragment.showMsgDialog(mFragmentManager,"提示",tip, CreateViewDialogFragment.TYPE_SHOW_UPDATE,true);
            return false;
        }

        if(updateType ==VersionChecker.UpdateStatus.TYPE_NEED_FOUCSE_UPDATE){
            android.util.Log.i("RayTest","show TYPE_NEED_FOUCSE_UPDATE");
            dialogFragment.showMsgDialog(mFragmentManager,"提示",tip,CreateViewDialogFragment.TYPE_NEED_FOUCSE_UPDATE,false);
            return false;

        }
        return true;

    }

    public UpdateStatus ComparisonVersion(String version_local, String version_store) {
        Log.i("RayTest","version_local:"+version_local+" version_store:"+version_store);
        int local1 = 0,local2 = 0,local3 = 0,store1 = 0,store2 = 0,store3 = 0;
        String pattern = "(\\d{1,3}).(\\d{1,3}).(\\d{1,3})";
        Matcher localMatcher = Pattern.compile(pattern).matcher(version_local);
        Matcher storeMatcher = Pattern.compile(pattern).matcher(version_store);



       if (localMatcher.find()) {

            local1 = Integer.parseInt(localMatcher.group(1));
            local2 = Integer.parseInt(localMatcher.group(2));
            local3 = Integer.parseInt(localMatcher.group(3));
          /* Log.i("RayTest","groupCount  "+localMatcher.groupCount());
           for(int index =1 ;index<=localMatcher.groupCount() ; index++ ){
               Log.i("RayTest","index  "+index+" "+localMatcher.group(index));
           }*/
        }

         if (storeMatcher.find()) {

            store1 = Integer.parseInt(storeMatcher.group(1));
            store2 = Integer.parseInt(storeMatcher.group(2));
            store3 = Integer.parseInt(storeMatcher.group(3));
        }
        Log.i("RayTest","local1:"+local1+" store1:"+store1);
        Log.i("RayTest","local2:"+local2+" store2:"+store2);
        if ((local1 < store1)||(local2 < store2))
        {
            update_status = UpdateStatus.TYPE_NEED_FOUCSE_UPDATE;
            return update_status;
        }
        if ((local3 < store3))
        {
            update_status = UpdateStatus.TYPE_SHOW_UPDATE;
            return update_status;
        }

        return UpdateStatus.TYPE_NONE_UPDATE;
    }

    public interface VersionCheckInterface{


        void onVersionSucess(boolean onSucess);
    
        void onVersionFail();
    }
}
