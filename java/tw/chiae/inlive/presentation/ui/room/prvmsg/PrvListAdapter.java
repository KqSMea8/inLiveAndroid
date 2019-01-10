package tw.chiae.inlive.presentation.ui.room.prvmsg;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import tw.chiae.inlive.BeautyLiveApplication;
import tw.chiae.inlive.R;
import tw.chiae.inlive.data.repository.SourceFactory;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.presentation.ui.chatting.CircleImageView;
import tw.chiae.inlive.presentation.ui.chatting.SortConvList;
import tw.chiae.inlive.presentation.ui.chatting.utils.HandleResponseCode;
import tw.chiae.inlive.presentation.ui.chatting.utils.TimeFormat;
import tw.chiae.inlive.presentation.ui.room.PriConversation;
import tw.chiae.inlive.util.Const;
import tw.chiae.inlive.util.FrescoUtil;
import tw.chiae.inlive.util.L;
import tw.chiae.inlive.util.PicUtil;
import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.content.CustomContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;

public class PrvListAdapter extends BaseAdapter {

    private List<tw.chiae.inlive.data.bean.me.UserInfo> UserList;
    List<PriConversation> mDatas;
    private Context mContext;
    private Map<String, String> mDraftMap = new HashMap<String, String>();
    private UIHandler mUIHandler = new UIHandler(this);
    private static final int REFRESH_CONVERSATION_LIST = 0x3001;
    private String name,lev,avt,sex;
    private HashMap<String,String> mFuImgUrl;
    private Gson gson;
    private tw.chiae.inlive.data.bean.me.UserInfo mTestInfo;
    private UserInfo addinfo;
    private List<tw.chiae.inlive.data.bean.me.UserInfo> newDefaultlist;
    private ArrayList<DefaultConversation> DefaultConversionList;
    private resetCallback mCallback;

    public PrvListAdapter(Activity context, List<PriConversation> data, PrvListCallback callback) {
        this.mContext = context;
        this.mDatas = data;
        gson = new Gson();
        mFuImgUrl=new HashMap<>();

    }


    public PrvListAdapter(Context context, List<PriConversation> data, HashMap<String,String> mFuImgUrl, PrvListCallback callback) {
        this.mContext = context;
        this.mDatas = data;
        this.mFuImgUrl=mFuImgUrl;
        gson = new Gson();
    }
    /**
     * 收到消息后将会话置顶
     *
     * @param conv 要置顶的会话
     */
    public void setToTop(Conversation conv) {
        String some= ((UserInfo) conv.getTargetInfo()).getUserName();
        some=some.replace("user","");
        Log.i("RayTest","setToTop: "+some);
        for (PriConversation conversation : mDatas) {

            Log.i("RayTest","[conv.some:"+some +" getConversationID:"+conversation.getUserID()+"]");
            if (some.equals(conversation.getUserID())) {
                PriConversation Conversation = getPriConversation(conv);
                Log.i("RayTest","mDatas id:"+conversation.getUserID());
                mDatas.remove(conversation);
                mDatas.add(0, Conversation);
                mUIHandler.removeMessages(REFRESH_CONVERSATION_LIST);
                mUIHandler.sendEmptyMessageDelayed(REFRESH_CONVERSATION_LIST, 200);
                return;
            }
        }
        //如果是新的会话
        Log.i("RayTest","如果是新的会话");
        PriConversation Conversation = getPriConversation(conv);
        mDatas.add(0, Conversation);
        mUIHandler.removeMessages(REFRESH_CONVERSATION_LIST);
        mUIHandler.sendEmptyMessageDelayed(REFRESH_CONVERSATION_LIST, 200);
    }

    private PriConversation getPriConversation(Conversation conversation) {
        String some= ((UserInfo) conversation.getTargetInfo()).getUserName();
        some=some.replace("user","");
        tw.chiae.inlive.data.bean.me.UserInfo info = LocalDataManager.getInstance().getUserInfo(some);
        if(info==null){
            return null;
        }else{
            PriConversation Conversation = new PriConversation();
            Conversation.setUserID(some);
            Conversation.setLastMsg(conversation.getLatestMessage());
            Conversation.setConversationID(conversation.getId());
            Conversation.setTime(conversation.getLastMsgDate());
            Conversation.setAvt(info.getAvatar());
            Conversation.setApproveid(info.getApproveid());
            Conversation.setNickName(info.getNickname());
            return Conversation;
        }
    }

    public void sortConvList() {
        try {
            SortConvList sortConvList = new SortConvList();
            Collections.sort(mDatas, sortConvList);
            notifyDataSetChanged();
        }catch (NullPointerException e) {
        }
    }


    public void addNewConversation(PriConversation conv) {
        Log.i("RayTest","Adapter addNewConversation ");
        mDatas.add(0, conv);
        notifyDataSetChanged();
        //addDatas(conv);
    }

 /*   public void deleteConversation(long groupId) {
       *//* for (PriConversation conv : mDatas) {
            if (conv.getType() == ConversationType.group && Long.parseLong(conv.getTargetId()) == groupId) {
                mDatas.remove(conv);
                return;
            }
        }*//*
    }*/

    public void resetConversation() {
        for (PriConversation Pricon : mDatas) {
            String userid = "user"+Pricon.getUserID();
            Log.i("RayTest","resetConversation id :" +userid);
            Conversation con = JMessageClient.getSingleConversation(userid);
            if(con!=null)
                con.resetUnreadCount();

            if(mCallback!=null)
                mCallback.resetConversationComplete();
            notifyDataSetChanged();
        }
    }

    public void setAdapterCallback(resetCallback callback){
        this.mCallback = callback;
    }

    public void putDraftToMap(String convId, String draft) {
        Log.i("RayTest","草稿:"+convId);
        mDraftMap.put(convId, draft);
        if(mCallback!=null)
            mCallback.initPriChatInput();
    }

    public void delDraftFromMap(String convId) {
        mDraftMap.remove(convId);
        Log.i("RayTest","delDraft 草稿:"+convId);
        notifyDataSetChanged();
        if(mCallback!=null)
            mCallback.initPriChatInput();
    }

    public Map<String, String> getDraftMap() {
        return mDraftMap;
    }

    public String getDraft(String convId) {
        return mDraftMap.get(convId);
    }

    @Override
    public int getCount() {
        if (mDatas == null) {
            return 0;
        }
        return mDatas.size();
    }

    @Override
    public PriConversation getItem(int position) {
        if (mDatas == null) {
            return null;
        }
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Log.i("RayTest",getClass().getSimpleName()+" getview");
        final PriConversation convItem = mDatas.get(position);
        final ViewHolder viewHolder;
        if (convertView == null) {
            if(mContext==null){
            }
            LayoutInflater layoutinlater = LayoutInflater.from(mContext);
            if(layoutinlater==null){

            }
            convertView = LayoutInflater.from(mContext).inflate(R.layout.conversation_list_item,
                    null);
            viewHolder = new ViewHolder();
            viewHolder.headIcon = (SimpleDraweeView) convertView
                    .findViewById(R.id.msg_item_head_icon);
            viewHolder.convName = (TextView) convertView
                    .findViewById(R.id.conv_item_name);
            viewHolder.content = (TextView) convertView
                    .findViewById(R.id.msg_item_content);
            viewHolder.datetime = (TextView) convertView
                    .findViewById(R.id.msg_item_date);
            viewHolder.newMsgNumber = (TextView) convertView
                    .findViewById(R.id.new_msg_number);
            viewHolder.gender = (ImageView) convertView
                    .findViewById(R.id.chat_gender);
            viewHolder.level = (ImageView) convertView
                    .findViewById(R.id.chat_level);
            viewHolder.approveid = (ImageView) convertView
                    .findViewById(R.id.iv_approveid);
            viewHolder.llInliveLayout = (LinearLayout) convertView
                    .findViewById(R.id.ll_inlive_id_layout);
            viewHolder.inliveId = (TextView) convertView
                    .findViewById(R.id.tv_inlive_text);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();

        }
        convertView = setUpView(position,convertView,viewHolder,convItem);


       /* if(convItem instanceof DefaultConversation){
            DefaultConversation con = (DefaultConversation) convItem;
            viewHolder.convName.setText(con.getmName());
            Log.i("RayTest","getmAvt:"+Const.MAIN_HOST_URL+con.getmAvt());
            Uri ImagUri = FrescoUtil.getCache(Const.MAIN_HOST_URL+con.getmAvt());
            if(ImagUri!=null)
                viewHolder.headIcon.setImageURI(ImagUri);
            else {
                FrescoUtil.CacheImgToDisk(Const.MAIN_HOST_URL+con.getmAvt());
                Glide.with(mContext).load(SourceFactory.wrapPathToUri(con.getmAvt())).into(viewHolder.headIcon);
            }
            viewHolder.content.setText("點擊可詢問任何問題！");
            viewHolder.newMsgNumber.setVisibility(View.GONE);
            if(con.getmApproveid()==1)
                viewHolder.approveid.setVisibility(View.VISIBLE);
            else
                viewHolder.approveid.setVisibility(View.INVISIBLE);
        }else{
            convertView = setUpView(position,convertView,viewHolder,convItem);
        }
            Log.i("RayTest","Default Cache"+DefaultConversionList.size());*/

        return convertView;
    }

    private DefaultConversation getInfo(String userid) {
        Log.i("RayTest","DefaultConversionList:"+DefaultConversionList.size());
        for(DefaultConversation con : DefaultConversionList){
            if(con.getDefaultAccount().equals(userid))
                return con;
        }
        return null;

    }

    private View setUpView(int position, View convertView, final ViewHolder viewHolder, PriConversation convItem) {

        String draft = mDraftMap.get(convItem.getUserID());
        Log.i("RayTest","getConversationID: "+""+convItem.getUserID()+ " :"+draft);
        //如果该会话草稿为空，显示最后一条消息
        if (TextUtils.isEmpty(draft)) {
            Conversation conversation = JMessageClient.getSingleConversation("user"+convItem.getUserID());
            if(conversation!=null){
                Message lastMsg = conversation.getLatestMessage();
                if (lastMsg != null) {
                    TimeFormat timeFormat = new TimeFormat(mContext, lastMsg.getCreateTime());
                    viewHolder.datetime.setText(timeFormat.getTime());
                    // 按照最后一条消息的消息类型进行处理
                    switch (lastMsg.getContentType()) {
                        case image:
                            viewHolder.content.setText(mContext.getString(R.string.type_picture));
                            break;
                        case voice:
                            viewHolder.content.setText(mContext.getString(R.string.type_voice));
                            break;
                        case location:
                            viewHolder.content.setText(mContext.getString(R.string.type_location));
                            break;
                        case eventNotification:
                            viewHolder.content.setText(mContext.getString(R.string.group_notification));
                            break;
                        case custom:
                            CustomContent content = (CustomContent) lastMsg.getContent();
                            Boolean isBlackListHint = content.getBooleanValue("blackList");
                            if (isBlackListHint != null && isBlackListHint) {
                                viewHolder.content.setText(mContext.getString(R.string.jmui_server_803008));
                            } else {
                                viewHolder.content.setText(mContext.getString(R.string.type_custom));
                            }
                            break;
                        default:
                            String lastText = ((TextContent) lastMsg.getContent()).getText();
                            if(TextUtils.isEmpty(lastText))
                                viewHolder.content.setText("點擊可以詢問任何問題！");
                            else
                                viewHolder.content.setText(((TextContent) lastMsg.getContent()).getText());
                    }
                }else {
                    Log.i("RayTest","setUpView "+"user"+convItem.getUserID()+":"+" null");
                    TimeFormat timeFormat = new TimeFormat(mContext, convItem.getTime());
                    viewHolder.datetime.setText(timeFormat.getTime());

                    viewHolder.content.setText("點擊可以詢問任何問題！");
                }
            }else{
                viewHolder.content.setText("點擊可以詢問任何問題！");
            }


        } else {
            Log.i("RayTest","setUpView "+"草稿user"+convItem.getUserID()+" "+draft);
            String content = mContext.getString(R.string.draft) + draft;
            SpannableStringBuilder builder = new SpannableStringBuilder(content);
            builder.setSpan(new ForegroundColorSpan(Color.RED), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            viewHolder.content.setText(builder);
        }

        // 如果是单聊
        long a=getItemId(position);
        String some = convItem.getUserID();

            /*Request<JSONObject> request = NoHttp.createJsonObjectRequest(Const.WEB_BASE_URL + "user/profile", RequestMethod.GET);
            request.add("uid", some);
            request.add("token", LocalDataManager.getInstance().getLoginInfo().getToken());

            viewHolder.convName.setText(convItem.getTitle());

            BeautyLiveApplication.getRequestQueue().add(MES, request, new UserInfoResponser(viewHolder.headIcon));*/
            viewHolder.headIcon.setImageURI(SourceFactory.wrapPathToUri(convItem.getAvt()));
            viewHolder.convName.setText(convItem.getNickName());


        // TODO 更新Message的数量,
        Conversation conversation = convItem.getJConversion();
        if(conversation!=null){
            Log.i("RayTest","id:"+convItem.getUserID()+" unread:"+conversation.getUnReadMsgCnt());
            if (conversation.getUnReadMsgCnt() > 0) {
                viewHolder.newMsgNumber.setVisibility(View.VISIBLE);
                if (conversation.getUnReadMsgCnt() < 100) {
                    viewHolder.newMsgNumber.setText(String.valueOf(conversation.getUnReadMsgCnt()));
                }
                else {
                    viewHolder.newMsgNumber.setText(mContext.getString(R.string.hundreds_of_unread_msgs));
                }
            } else {
                viewHolder.newMsgNumber.setVisibility(View.GONE);
            }
        }else{
            viewHolder.newMsgNumber.setVisibility(View.GONE);
        }
        viewHolder.inliveId.setText(convItem.getUserID());
        viewHolder.llInliveLayout.setVisibility(View.VISIBLE);
        return convertView;
    }

    public void addPriConversation(PriConversation conversation) {
        mDatas.add(conversation);
        notifyDataSetChanged();
    }

    public void removeItem(int position, PriConversation conversation) {
        mDatas.remove(position);
        delDraftFromMap(conversation.getUserID());
        if(LocalDataManager.getInstance().isOfficialAccount(conversation.getUserID())) {
            mDatas.add(conversation);
            notifyDataSetChanged();
        }
        else
            notifyDataSetChanged();
    }


    static class UIHandler extends Handler {

        private final WeakReference<PrvListAdapter> mAdapter;

        public UIHandler(PrvListAdapter adapter) {
            mAdapter = new WeakReference<PrvListAdapter>(adapter);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            PrvListAdapter adapter = mAdapter.get();
            if (adapter != null) {
                switch (msg.what) {
                    case REFRESH_CONVERSATION_LIST:
                        adapter.notifyDataSetChanged();
                        break;
                }
            }
        }
    }

    public class ViewHolder {
        //CircleImageView headIcon;
        SimpleDraweeView headIcon;
        TextView convName;
        TextView content;
        TextView datetime;
        TextView newMsgNumber;
        ImageView gender;
        ImageView level;
        ImageView approveid;
        TextView inliveId;
        LinearLayout llInliveLayout;
    }

    int MES=9;


    /*private OnResponseListener<JSONObject> upMyPathResponse = new OnResponseListener<JSONObject>() {
        @Override
        public void onStart(int i) {
        }

        @Override
        public void onSucceed(int i, Response<JSONObject> response) {
            if (i == MES) {// 判断what是否是刚才指定的请求
                //                {"code":0,"msg":"ok","data":"320200"}这个是什么
                // 请求成功
                JSONObject result = response.get();// 响应结果
                try {
                    JSONObject data = result.getJSONObject("data");
                    name=data.getString("nickname");
                    lev=data.getString("emceelevel");
                    sex=data.getString("sex");
                    avt=data.getString("avatar");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // 响应头
                Headers headers = response.getHeaders();
                headers.getResponseCode();// 响应码
                response.getNetworkMillis();// 请求花费的时间
            }
        }

        @Override
        public void onFailed(int i, String s, Object o, Exception e, int i1, long l) {
        }

        @Override
        public void onFinish(int i) {
        }
    };*/

    //    拉取单个
/*    public void addDatas(PriConversation mConv){
        cn.jpush.im.android.api.model.UserInfo info= (cn.jpush.im.android.api.model.UserInfo) mConv.getTargetInfo();
        addinfo= info;
        getAddDatas(info.getUserName().replaceAll("user",""));
    }

    final  int ADDDATA_JSON=41;
    public void getAddDatas(String user_id) {
        Request<JSONObject> request = NoHttp.createJsonObjectRequest(Const.WEB_BASE_URL + "user/profile", RequestMethod.GET);
        request.add("uid", user_id);
        request.add("token", LocalDataManager.getInstance().getLoginInfo().getToken());
        BeautyLiveApplication.getRequestQueue().add(ADDDATA_JSON, request, OnResponseAdd);
    }*/

   /* private OnResponseListener<JSONObject> OnResponseAdd = new OnResponseListener<JSONObject>() {
        @Override
        public void onStart(int i) {
        }

        @Override
        public void onSucceed(int i, Response<JSONObject> response) {
            if (i == ADDDATA_JSON) {// 判断what是否是刚才指定的请求
                //                {"code":0,"msg":"ok","data":"320200"}这个是什么
                // 请求成功
                JSONObject result = response.get();// 响应结果
                try {
                    JSONObject json=result.getJSONObject("data");
                    mTestInfo=gson.fromJson(json.toString(), tw.chiae.inlive.data.bean.me.UserInfo.class);
                    mFuImgUrl.put(addinfo.getUserName().replaceAll("user",""),mTestInfo.getAvatar());
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    handler.sendEmptyMessage(1);
                }
                // 响应头
                Headers headers = response.getHeaders();
                headers.getResponseCode();// 响应码
                response.getNetworkMillis();// 请求花费的时间
            }
        }

        @Override
        public void onFailed(int i, String s, Object o, Exception e, int i1, long l) {
        }

        @Override
        public void onFinish(int i) {
        }
    };*/

/*    Handler handler=new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            if (msg.what==1){
                PrvListAdapter.this.notifyDataSetChanged();
            }
        }
    };*/

    //private mHandler handler = new mHandler(this);

    /*private static class mHandler extends Handler {

        private PrvListAdapter prvListAdapter;

        public mHandler(PrvListAdapter adapter) {
            this.prvListAdapter = adapter;
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            if(prvListAdapter==null)
                return;
            if (msg.what==1){
                prvListAdapter.notifyDataSetChanged();
            }
        }
    }*/



/*    public void UpdateSl(){
        mDatas =getOfficialList(JMessageClient.getConversationList());

        for(Conversation con : mDatas){
            if(con instanceof DefaultConversation){
                Log.i("RayTest","getDefaultAccount"+ ( (DefaultConversation)con ).getDefaultAccount());
            }
        }
        this.notifyDataSetChanged();
    }*/

//    假装单聊
    public void addmFuImg(String key){
        mFuImgUrl.put(key,"");
        this.notifyDataSetChanged();
    }

    public List<PriConversation> getmDatas() {
        return mDatas;
    }

    public  interface PrvListCallback{
        void upDatePriList(List<Conversation> newDataList);
        void isUpdate();
        void needDownloadInfo(String some);
    }

   /* private class UserInfoResponser implements OnResponseListener<JSONObject> {
        private final CircleImageView icon;

        public UserInfoResponser(CircleImageView headIcon) {
            this.icon = headIcon;
        }

        @Override
        public void onStart(int i) {
        }

        @Override
        public void onSucceed(int i, Response<JSONObject> response) {
            if (i == MES) {// 判断what是否是刚才指定的请求
                //                {"code":0,"msg":"ok","data":"320200"}这个是什么
                // 请求成功
                JSONObject result = response.get();// 响应结果
                try {
                    JSONObject data = result.getJSONObject("data");
                    name=data.getString("nickname");
                    lev=data.getString("emceelevel");
                    sex=data.getString("sex");
                    avt=data.getString("avatar");
                    String Url = Const.MAIN_HOST_URL+avt;
                    Uri imgUri = FrescoUtil.getCache(Url);
                    if(imgUri==null){
                        FrescoUtil.CacheImgToDisk(Url);
                        Glide.with(mContext).load(SourceFactory.wrapPathToUri(avt)).into(icon);
                    }else{
                        icon.setImageURI(imgUri);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // 响应头
                Headers headers = response.getHeaders();
                headers.getResponseCode();// 响应码
                response.getNetworkMillis();// 请求花费的时间
            }
        }

        @Override
        public void onFailed(int i, String s, Object o, Exception e, int i1, long l) {
        }

        @Override
        public void onFinish(int i) {
        }
    }*/
   public interface resetCallback{
       void resetConversationComplete();

       void initPriChatInput();
   }
}
