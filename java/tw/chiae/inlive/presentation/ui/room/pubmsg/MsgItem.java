package tw.chiae.inlive.presentation.ui.room.pubmsg;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;

import tw.chiae.inlive.R;
import tw.chiae.inlive.data.bean.websocket.LightHeartMsg;
import tw.chiae.inlive.data.bean.websocket.RoomPublicMsg;
import tw.chiae.inlive.data.bean.websocket.SendGiftMsg;
import tw.chiae.inlive.data.bean.websocket.SystemMsg;
import tw.chiae.inlive.data.bean.websocket.SystemWelcome;
import tw.chiae.inlive.data.bean.websocket.UserPublicMsg;

/**
 * Created by rayyeh on 2017/3/25.
 */

public class MsgItem {
    private Context mMsgItemContext;



    private RoomPublicMsg MsgData;
    private PublicChatHolderCallback HolderCallback;
    private String sSystemFlag;
    private String sGiftFlag;
    private String DisplayName;
    private int DisplayLevel;
    private String DisplayContent;
    private String DisplayFlag;
    private int DisplayVip;

    public static final String MsgPublicView = "MsgPublic";
    public static final String MsgContent = "MsgContent";
    public static final String MsgLevel = "MsgLevel";
    public static final String MsgType = "MsgType";
    public static final String MsgName = "MsgName";
    public static final String MsgVip = "MsgVip";
    public static final String MsgApproeid = "MsgApproe";
    public static final String MsgApproeStr = "MsgApproeStr";
    private String[] sArtistType_cn = {"無","星級藝人","普通藝人","金牌藝人","官方","特約藝人"};
    private String[] sArtistType_tw = {"无","星级艺人","普通艺人","金牌艺人","官方","特约艺人"};
    private int[] starticon = new int[]{0, R.drawable.id_star, R.drawable.id_vip, R.drawable.id_gold,R.drawable.id_in,R.drawable.id_specialicon};

    private HashMap<String, Object> DisplayMsgData;
    private PublicChatHolder callback;
    private LinearLayout publicMsgLayout;
    private String viewID;
    private Drawable DisplayApproeid;
    private String DisplayApproeStr;

    public String getDisplayName() {
        return DisplayName;
    }

    public void setDisplayName(String displayName) {
        DisplayName = displayName;
    }

    public String getViewID() {
        return viewID;
    }

    public void setViewID(String viewID) {
        this.viewID = viewID;
    }

    public RoomPublicMsg getMsgData() {
        return MsgData;
    }

    public MsgItem(PublicChatHolderCallback callback, Context context, RoomPublicMsg data) {
        this.HolderCallback = callback;
        this.mMsgItemContext = context;
        this.MsgData = data;
        initView();
    }

    public void initView() {
        sSystemFlag = mMsgItemContext.getResources().getString(R.string.room_live_msg_system);
        sGiftFlag = mMsgItemContext.getResources().getString(R.string.room_live_msg_gift);
        getData();
    }

    private void getData() {
        boolean isData = false;
        if(MsgData==null)
            return;
        if (MsgData instanceof UserPublicMsg) {
            this.DisplayFlag = mMsgItemContext.getResources().getString(R.string.room_live_msg_system);
            this.viewID = ((UserPublicMsg) MsgData).getUserId();
            this.DisplayName = ((UserPublicMsg) MsgData).getFromClientName();
            this.DisplayLevel = ((UserPublicMsg) MsgData).getLevel();
            this.DisplayContent = ((UserPublicMsg) MsgData).getContent();
            this.DisplayVip = ((UserPublicMsg) MsgData).getVipLevel();
            this.DisplayApproeid = getDrawableType(((UserPublicMsg) MsgData).getApproveid());
            this.DisplayApproeStr = ((UserPublicMsg) MsgData).getApproveid();
            isData = true;
        }else if (MsgData instanceof LightHeartMsg) {
            this.DisplayFlag = mMsgItemContext.getResources().getString(R.string.room_live_msg_system);
            this.viewID = ((LightHeartMsg) MsgData).getFromUserId();
            this.DisplayName = ((LightHeartMsg) MsgData).getFromClientName();
            this.DisplayLevel = ((LightHeartMsg) MsgData).getLevel();
            this.DisplayContent =  mMsgItemContext.getResources().getString(R.string.room_live_msg_mylight);
            this.DisplayVip = ((LightHeartMsg) MsgData).getVip();
            this.DisplayApproeid = getDrawableType(((LightHeartMsg) MsgData).getApproveid());
            this.DisplayApproeStr = ((LightHeartMsg) MsgData).getApproveid();
            isData = true;
        } else if (MsgData instanceof SendGiftMsg) {
            this.DisplayFlag = mMsgItemContext.getResources().getString(R.string.room_live_msg_gift);
            this.viewID = ((SendGiftMsg) MsgData).getFromUserId();
            this.DisplayName = ((SendGiftMsg) MsgData).getFromUserName();
            this.DisplayLevel = ((SendGiftMsg) MsgData).getLevel();
            this.DisplayContent =  mMsgItemContext.getResources().getString(R.string.room_live_msg_sendone)+((SendGiftMsg) MsgData).getGiftName();
            this.DisplayVip = -1;
            this.DisplayApproeid = getDrawableType(((SendGiftMsg) MsgData).getApproveid());
            this.DisplayApproeStr = ((SendGiftMsg) MsgData).getApproveid();
            isData = true;
        } else if (MsgData instanceof SystemMsg) {
            String str = ((SystemMsg) MsgData).getContent();
            this.viewID = "";
            this.DisplayVip = -1;
            this.DisplayApproeid = null;
            this.DisplayApproeStr = "";
            if(str.contains("直播消息：")){
                //String parseString = "直播消息：";
                this.DisplayFlag = mMsgItemContext.getResources().getString(R.string.room_live_msg_system);
                //int startIndex = str.indexOf("直播消息：");
                //int endIndex = str.lastIndexOf("將主播加入了最愛");

                //Log.i("RayTest","s:"+startIndex+" e:"+endIndex+" "+str.substring(startIndex,endIndex));
                this.DisplayName = "";
                //str = DisplayName+" "+str.substring(endIndex,str.length());
          /*      this.DisplayName = "test";
                str = DisplayName+" ";*/

            }
            if(str.contains("已被管理员禁言")){
                //str= "已被管理員禁言";
            }
                this.DisplayFlag = mMsgItemContext.getResources().getString(R.string.room_live_msg_system);
                this.DisplayName = " ";
                this.DisplayLevel = 1;
                this.DisplayContent = str;

            isData = true;
        } else if (MsgData instanceof SystemWelcome) {
            this.DisplayFlag = mMsgItemContext.getResources().getString(R.string.room_live_msg_system);
            this.DisplayName = ((SystemWelcome) MsgData).getClient_name();
            this.DisplayLevel = ((SystemWelcome) MsgData).getLevelid();
            this.DisplayContent = mMsgItemContext.getResources().getString(R.string.room_live_msg_myroom);
            this.DisplayApproeStr = ((SystemWelcome) MsgData).getApproveid();
            isData = true;
        }
        if(DisplayLevel==0)
            DisplayLevel = 1;
        if(isData){
           buildContentData();

        }
    }

    private Drawable getDrawableType(String approveid) {
        int approveidflag = 0;
        Drawable drawable = null;
        if(approveid!=null) {
            if (approveid.equals(sArtistType_cn[0]) || approveid.equals(sArtistType_tw[0]))
                approveidflag = 0;
            if (approveid.equals(sArtistType_cn[1]) || approveid.equals(sArtistType_tw[1]))
                approveidflag = 1;
            if (approveid.equals(sArtistType_cn[2]) || approveid.equals(sArtistType_tw[2]))
                approveidflag = 2;
            if (approveid.equals(sArtistType_cn[3]) || approveid.equals(sArtistType_tw[3]))
                approveidflag = 3;
            if (approveid.contains(sArtistType_cn[4]) || approveid.contains(sArtistType_tw[4]))
                approveidflag = 4;

        }
        switch (approveidflag)
        {
            case 0:
                drawable = null;
                break;
            case 1:
                drawable = getDrawableView(starticon[1]);
                break;
            case 2:
                drawable = getDrawableView(starticon[2]);
                break;
            case 3:
                drawable = getDrawableView(starticon[3]);
                break;
            case 4:
                drawable = getDrawableView(starticon[4]);
                break;
            default:
                drawable = null;
                break;

        }
        return drawable;
    }

    private Drawable getDrawableView(int res) {
        Drawable drawable = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable = mMsgItemContext.getResources().getDrawable(res,null);
        }else{
            drawable = mMsgItemContext.getResources().getDrawable(res);
        }

        return drawable;
    }

    private String ParseName(String name) {
        String Prefix = "fb_";

        if(name.contains(Prefix)){
            name = name.substring(Prefix.length(),name.length());
        }
        return name;
    }

    private void buildContentData() {
        HashMap<String,Object> data = new HashMap<>();
        MsgUtils utils = MsgUtils.getInstance();
        CharSequence char_name = utils.buildUserName(DisplayName);
        CharSequence char_context = null;
        Drawable drawable = null;
        View PublicView = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            drawable = mMsgItemContext.getResources().getDrawable(getResourceId(DisplayLevel),null);
        }else
            drawable = mMsgItemContext.getResources().getDrawable(getResourceId(DisplayLevel));
        if(MsgData instanceof SystemWelcome ) {
            // 字體 藍色
            char_context = utils.buildPublicSysMsgWelcome(DisplayContent);
        }else if(MsgData instanceof LightHeartMsg  || MsgData instanceof SendGiftMsg) {
            // 字體 黃色
            char_context = utils.buildPublicSysMsgContent(DisplayContent);
        }else if( MsgData instanceof SystemMsg){
            char_context = utils.buildPublicSysMsgTip(DisplayContent);
        }
        else {
            // 字體 白色

            if(MsgData instanceof UserPublicMsg) {
                if(publicMsgLayout == null) {
                    publicMsgLayout = buildPublicFlag(drawable,char_name);
                }
                data.put(MsgPublicView, publicMsgLayout);
            }

            char_context = DisplayContent;

        }
        if(MsgData instanceof SystemMsg || MsgData instanceof UserPublicMsg)
            data.put(MsgContent, TextUtils.concat(" ",char_context));
        else
            data.put(MsgContent,TextUtils.concat(char_name," ",char_context));
        data.put(MsgLevel,drawable);
        data.put(MsgType,DisplayFlag);
        data.put(MsgName,DisplayName);
        data.put(MsgVip,DisplayVip);
        data.put(MsgApproeid,DisplayApproeid);
        data.put(MsgApproeStr,DisplayApproeStr);
        Log.i("RayTest",DisplayName+"新增一筆:"+DisplayApproeStr);
        this.DisplayMsgData =  data;
        HolderCallback.onCompleteData(MsgData,data);
    }

    private LinearLayout buildPublicFlag(Drawable drawable, CharSequence char_name) {

        LinearLayout Layout = new LinearLayout(mMsgItemContext);
        LinearLayout.LayoutParams mainLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mainLayoutParams.height = mMsgItemContext.getResources().getDimensionPixelSize(R.dimen.user_level_height);
        Layout.setLayoutParams(mainLayoutParams);
        Layout.setOrientation(LinearLayout.HORIZONTAL);
        Layout.setBackgroundResource(R.drawable.room_item_chat);

        ImageView img = new ImageView(mMsgItemContext);
        LinearLayout.LayoutParams img_lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        img_lp.width = mMsgItemContext.getResources().getDimensionPixelSize(R.dimen.user_level_width);
        img_lp.height = mMsgItemContext.getResources().getDimensionPixelSize(R.dimen.user_level_height);
        img_lp.gravity = Gravity.CENTER;
        img.setLayoutParams(img_lp);


        img.setImageDrawable(drawable);

        TextView textView = new TextView(mMsgItemContext);
        LinearLayout.LayoutParams tv_lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textView.setTextSize(13);
        //textView.setShadowLayer(3,3,3,R.color.item_public_chat_shader);
        tv_lp.setMargins(8,0,5,0);
        tv_lp.gravity = Gravity.CENTER;
        textView.setLayoutParams(tv_lp);
        textView.setText(char_name);
        Layout.addView(img);
        Layout.addView(textView);
        return Layout;
    }

    private int getResourceId(int level) {
        if(level==0 )
            level=1;
        int resValue =  mMsgItemContext.getResources().
                getIdentifier("ic_level_"+level, "drawable", mMsgItemContext.getPackageName());
        if(resValue==0)
            resValue =  mMsgItemContext.getResources().
                    getIdentifier("ic_level_"+100, "drawable", mMsgItemContext.getPackageName());
        return resValue;
    }

    public HashMap<String, Object> getDisplayMsgData (){
        return DisplayMsgData;
    }

    public void removeItem() {
        HolderCallback.onCancel();

        this.HolderCallback = null;
        this.mMsgItemContext = null;
        this.MsgData = null;
        DisplayMsgData.clear();
    }

}
