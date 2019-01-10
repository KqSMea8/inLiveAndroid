package tw.chiae.inlive.presentation.ui.chatting;

import android.app.Activity;
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
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import tw.chiae.inlive.BeautyLiveApplication;
import tw.chiae.inlive.R;
import tw.chiae.inlive.data.repository.SourceFactory;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.presentation.ui.chatting.utils.HandleResponseCode;
import tw.chiae.inlive.presentation.ui.chatting.utils.TimeFormat;
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

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
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

public class ConversationListAdapter extends BaseAdapter {

    List<Conversation> mDatas;
    private Activity mContext;
    private Map<String, String> mDraftMap = new HashMap<String, String>();
    private UIHandler mUIHandler = new UIHandler(this);
    private static final int REFRESH_CONVERSATION_LIST = 0x3001;
    private String name,lev,avt,sex;
    private HashMap<String,String> mFuImgUrl;
    private Gson gson;
    private tw.chiae.inlive.data.bean.me.UserInfo mTestInfo;
    private UserInfo addinfo;

    public ConversationListAdapter(Activity context, List<Conversation> data) {
        this.mContext = context;
        this.mDatas = data;
        gson = new Gson();
        mFuImgUrl=new HashMap<>();
    }
    public ConversationListAdapter(Activity context, List<Conversation> data,HashMap<String,String> mFuImgUrl) {
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
        for (Conversation conversation : mDatas) {
            if (conv.getId().equals(conversation.getId())) {
                mDatas.remove(conversation);
                mDatas.add(0, conv);
                mUIHandler.removeMessages(REFRESH_CONVERSATION_LIST);
                mUIHandler.sendEmptyMessageDelayed(REFRESH_CONVERSATION_LIST, 200);
                return;
            }
        }
        //如果是新的会话
        mDatas.add(0, conv);
        mUIHandler.removeMessages(REFRESH_CONVERSATION_LIST);
        mUIHandler.sendEmptyMessageDelayed(REFRESH_CONVERSATION_LIST, 200);
    }

    public void sortConvList() {
        SortConvList sortConvList = new SortConvList();
        Collections.sort(mDatas, sortConvList);
        notifyDataSetChanged();
    }

    public void addNewConversation(Conversation conv) {
        mDatas.add(0, conv);
        notifyDataSetChanged();
    }

    public void deleteConversation(long groupId) {
        for (Conversation conv : mDatas) {
            if (conv.getType() == ConversationType.group
                    && Long.parseLong(conv.getTargetId()) == groupId) {
                mDatas.remove(conv);
                return;
            }
        }
    }

    public void resetConversation() {
        for (Conversation conv : mDatas) {
                conv.resetUnreadCount();
            notifyDataSetChanged();
        }
    }

    public void putDraftToMap(String convId, String draft) {
        mDraftMap.put(convId, draft);
    }

    public void delDraftFromMap(String convId) {
        mDraftMap.remove(convId);
        notifyDataSetChanged();
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
    public Conversation getItem(int position) {
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
        final Conversation convItem = mDatas.get(position);
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.conversation_list_item2,
                    null);
            viewHolder = new ViewHolder();
            viewHolder.headIcon = (CircleImageView) convertView
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
            viewHolder.inliveIdLayout = (LinearLayout) convertView
                    .findViewById(R.id.ll_inlive_id_layout);
            viewHolder.inliveId = (TextView) convertView
                    .findViewById(R.id.tv_inlive_text);
            convertView.setTag(viewHolder);
            viewHolder.inliveIdLayout.setVisibility(View.VISIBLE);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();

        }
        String draft = mDraftMap.get(convItem.getId());
        //如果该会话草稿为空，显示最后一条消息
        if (TextUtils.isEmpty(draft)) {
            Message lastMsg = convItem.getLatestMessage();
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
                        viewHolder.content.setText(((TextContent) lastMsg.getContent()).getText());
                }
            }else {
                TimeFormat timeFormat = new TimeFormat(mContext, convItem.getLastMsgDate());
                viewHolder.datetime.setText(timeFormat.getTime());
                viewHolder.content.setText("");
            }
        } else {
            String content = mContext.getString(R.string.draft) + draft;
            SpannableStringBuilder builder = new SpannableStringBuilder(content);
            builder.setSpan(new ForegroundColorSpan(Color.RED), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            viewHolder.content.setText(builder);
        }

        // 如果是单聊
        long a=getItemId(position);
        if (convItem.getType().equals(ConversationType.single)) {
//            if (some.startsWith("meilibo")) {
//            String some= ((UserInfo) convItem.getTargetInfo()).getUserName();
//            some=some.replace("meilibo","");
//            L.e("lw", "谁的id+截取"+some);
//                Request<JSONObject> request = NoHttp.createJsonObjectRequest(Const.WEB_BASE_URL + "user/profile", RequestMethod.GET);
//                request.add("uid", some);
//                request.add("token",LocalDataManager.getInstance().getLoginInfo().getToken());
//                L.e("lw", "谁的id" + some);
//                BeautyLiveApplication.getRequestQueue().add(MES, request, upMyPathResponse);
                viewHolder.convName.setText(convItem.getTitle());

                UserInfo userInfo = (UserInfo) convItem.getTargetInfo();
                if (userInfo != null && !TextUtils.isEmpty(userInfo.getAvatar())) {
                    userInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
                        @Override
                        public void gotResult(int status, String desc, Bitmap bitmap) {
                            if (status == 0) {
                                viewHolder.headIcon.setImageBitmap(bitmap);
                            } else {
                                viewHolder.headIcon.setImageResource(R.drawable.jmui_head_icon);
                                HandleResponseCode.onHandle(mContext, status, false);
                            }
                        }
                    });
                } else {
                    viewHolder.headIcon.setImageResource(R.drawable.jmui_head_icon);
                    for (String key : mFuImgUrl.keySet()) {
                        if (key.equals(userInfo.getUserName().replaceAll("user",""))){
                            Glide.with(mContext).load(SourceFactory.wrapPathToUri(mFuImgUrl.get(key))).into(viewHolder.headIcon);
                            Log.i("RayTest","mFuImgUrl.get(key)"+mFuImgUrl.get(key));
                            Glide.with(mContext).load(SourceFactory.wrapPathToUri(mFuImgUrl.get(key))).asBitmap().into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    viewHolder.headIcon.setImageBitmap(resource);
                                }
                            });
                            break;
                        }
                    }

                }

                if (userInfo != null) {
                    JMessageClient.getUserInfo(((UserInfo) convItem.getTargetInfo()).getUserName(), ((UserInfo) convItem.getTargetInfo()).getAppKey(), new GetUserInfoCallback() {
                        @Override
                        public void gotResult(int i, String s, UserInfo userInfo) {
                            if (i == 0) {
                                //viewHolder.gender.setImageResource(userInfo.getGender().equals(UserInfo.Gender.male) ? R.drawable.ic_male : R.drawable.ic_female);
                                //viewHolder.level.setImageResource(PicUtil.getLevelImageId(mContext, Integer.parseInt(String.valueOf(userInfo.getRegion()))));
                            } else {
                                //viewHolder.gender.setImageResource(R.drawable.ic_female);
                               // viewHolder.level.setImageResource(R.drawable.ic_level_1);
//                            HandleResponseCode.onHandle(mContext, i, false);
                            }

                            String userid = userInfo.getUserName().replace("user","");
                            viewHolder.inliveId.setText(userid+"");
                        }
                    });
                } else {
                    viewHolder.gender.setImageResource(R.drawable.ic_male);
                    viewHolder.level.setImageResource(R.drawable.ic_level_1);
                }
            }
//        }
//        } else {
//            viewHolder.headIcon.setImageResource(R.drawable.group);
//            viewHolder.convName.setText(convItem.getTitle());
//            Log.d("ConversationListAdapter", "Conversation title: " + convItem.getTitle());
//        }

        // TODO 更新Message的数量,
        if (convItem.getUnReadMsgCnt() > 0) {
            viewHolder.newMsgNumber.setVisibility(View.VISIBLE);
            if (convItem.getUnReadMsgCnt() < 100) {
                viewHolder.newMsgNumber.setText(String.valueOf(convItem.getUnReadMsgCnt()));
            }
            else {
                viewHolder.newMsgNumber.setText(mContext.getString(R.string.hundreds_of_unread_msgs));
            }
        } else {
            viewHolder.newMsgNumber.setVisibility(View.GONE);
        }

        return convertView;
    }

    static class UIHandler extends Handler {

        private final WeakReference<ConversationListAdapter> mAdapter;

        public UIHandler(ConversationListAdapter adapter) {
            mAdapter = new WeakReference<ConversationListAdapter>(adapter);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            ConversationListAdapter adapter = mAdapter.get();
            if (adapter != null) {
                switch (msg.what) {
                    case REFRESH_CONVERSATION_LIST:
                        adapter.notifyDataSetChanged();
                        break;
                }
            }
        }
    }

    private class ViewHolder {
        CircleImageView headIcon;
        TextView convName;
        TextView content;
        TextView datetime;
        TextView newMsgNumber;
        ImageView gender;
        ImageView level;
        TextView inliveId;
        LinearLayout inliveIdLayout;
    }
    int MES=9;

    private OnResponseListener<JSONObject> upMyPathResponse = new OnResponseListener<JSONObject>() {
        @Override
        public void onStart(int i) {
            L.i("lll", "开始了");
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
            L.i("lll", "请求失败了哦" + s);
        }

        @Override
        public void onFinish(int i) {
            L.i("lll", "viewpager结束了");
        }
    };
    final  int ADDDATA_JSON=41;
    public void getAddDatas(String user_id) {
        Request<JSONObject> request = NoHttp.createJsonObjectRequest(Const.WEB_BASE_URL + "user/profile", RequestMethod.GET);
        request.add("uid", user_id);
        request.add("token", LocalDataManager.getInstance().getLoginInfo().getToken());
        BeautyLiveApplication.getRequestQueue().add(ADDDATA_JSON, request, OnResponseAdd);
    }

    private OnResponseListener<JSONObject> OnResponseAdd = new OnResponseListener<JSONObject>() {
        @Override
        public void onStart(int i) {
            Log.i("lll", "开始了");
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
            Log.i("lll", "请求失败了哦" + s);
        }

        @Override
        public void onFinish(int i) {
            Log.i("lll", "viewpager结束了");
        }
    };

    Handler handler=new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            if (msg.what==1){
                ConversationListAdapter.this.notifyDataSetChanged();
            }
        }
    };


    public void removeSl(int position){
        UserInfo userInfo= (UserInfo) mDatas.get(position).getTargetInfo();
        for (String key : mFuImgUrl.keySet()) {
            if (key.equals(userInfo.getUserName().replaceAll("user",""))){
                mFuImgUrl.remove(key);
                break;
            }
        }
        mDatas.remove(position);
        this.notifyDataSetChanged();
    }

    //    假装单聊
    public void addmFuImg(String key){
        mFuImgUrl.put(key,"");
        this.notifyDataSetChanged();
    }
    public List<Conversation> getmDatas() {
        return mDatas;
    }

    public Map<String, String> getmDraftMap() {
        return mDraftMap;
    }
}
