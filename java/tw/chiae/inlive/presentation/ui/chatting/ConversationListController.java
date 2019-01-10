package tw.chiae.inlive.presentation.ui.chatting;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.google.gson.Gson;
import tw.chiae.inlive.BeautyLiveApplication;
import tw.chiae.inlive.R;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.presentation.ui.chatting.utils.DialogCreator;
import tw.chiae.inlive.util.Const;
import tw.chiae.inlive.util.L;
import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.UserInfo;

public class ConversationListController implements
        OnItemClickListener, OnItemLongClickListener {

    private ConversationListView mConvListView;
    private ConversationListFragment mContext;
    private List<Conversation> mDatas = new ArrayList<Conversation>();
    private ConversationListAdapter mListAdapter;
    private int mWidth;
    private Dialog mDialog;

    public ConversationListController(ConversationListView listView, ConversationListFragment context,
                                      int width) {
        this.mConvListView = listView;
        this.mContext = context;
        this.mWidth = width;
        initConvListAdapter();
    }

    // 得到会话列表
    private void initConvListAdapter() {
        try{
            mDatas = JMessageClient.getConversationList();
            Log.i("RayTest","-----nDatas size:"+mDatas.size());
            initDatas(mDatas);
            //对会话列表进行时间排序
            if (mDatas.size() > 1) {
                SortConvList sortList = new SortConvList();
                Collections.sort(mDatas, sortList);
            }
        }catch (NullPointerException e){
        }

        mListAdapter = new ConversationListAdapter(mContext.getActivity(), mDatas);
        mConvListView.setConvListAdapter(mListAdapter);
    }

    // 点击会话列表
    @Override
    public void onItemClick(AdapterView<?> viewAdapter, View view, int position, long id) {
        // TODO Auto-generated method stub
        final Intent intent = new Intent();
        if (position > 0) {
            Conversation conv = mDatas.get(position - 1);
            if (null != conv) {
                String targetId = ((UserInfo) conv.getTargetInfo()).getUserName();
                intent.putExtra(BeautyLiveApplication.TARGET_ID, targetId);
                intent.putExtra(BeautyLiveApplication.TARGET_APP_KEY, conv.getTargetAppKey());
                intent.putExtra("name",((UserInfo) conv.getTargetInfo()).getNickname());
                BitmapDrawable bit= (BitmapDrawable) ((CircleImageView)view.findViewById(R.id.msg_item_head_icon)).getDrawable();
                intent.putExtra("fromimg",bit.getBitmap());
                intent.putExtra("t","1");
                if (((UserInfo) conv.getTargetInfo()).getAvatar()==null) {
                    intent.putExtra("address", "suipianchuande");
                }else{
                    intent.putExtra("address", ((UserInfo) conv.getTargetInfo()).getAvatar());
                }
                Log.d("ConversationList", "Target app key from conversation: " + conv.getTargetAppKey());
                intent.putExtra(BeautyLiveApplication.DRAFT, getAdapter().getDraft(conv.getId()));

            intent.setClass(mContext.getActivity(), ChatActivity.class);
            mContext.getActivity().startActivity(intent);
            }
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> viewAdapter, View view, final int position, long id) {
        if (position > 0) {
            final Conversation conv = mDatas.get(position - 1);
            if (conv != null) {
                OnClickListener listener = new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (conv.getType() == ConversationType.group) {
                            JMessageClient.deleteGroupConversation(((GroupInfo) conv.getTargetInfo())
                                    .getGroupID());
                        } else {
                            //使用带AppKey的接口,可以删除跨/非跨应用的会话(如果不是跨应用,conv拿到的AppKey则是默认的)
                            JMessageClient.deleteSingleConversation(((UserInfo) conv.getTargetInfo())
                                    .getUserName(), conv.getTargetAppKey());
                        }
                        conv.resetUnreadCount();
                        mDatas.remove(position - 1);
                        mListAdapter.notifyDataSetChanged();
                        mDialog.dismiss();
                    }
                };
                mDialog = DialogCreator.createDelConversationDialog(mContext.getActivity(), conv.getTitle(),
                        listener);
                mDialog.show();
                mDialog.getWindow().setLayout((int) (0.8 * mWidth), WindowManager.LayoutParams.WRAP_CONTENT);
            }
        }
        return true;
    }
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what==21){
                for (String value : mFuImgUrl.values()) {
                }
                mListAdapter = new ConversationListAdapter(mContext.getActivity(), mDatas,mFuImgUrl);
                mConvListView.setConvListAdapter(mListAdapter);
            }
            super.handleMessage(msg);
        }

        ;
    };

    public ConversationListAdapter getAdapter() {
        return mListAdapter;
    }

    final  int DATA_JSON=11;
    public void getDatas(String user_id) {
        Request<JSONObject> request = NoHttp.createJsonObjectRequest(Const.WEB_BASE_URL + "user/profile", RequestMethod.GET);
        request.add("uid", user_id);
        request.add("token", LocalDataManager.getInstance().getLoginInfo().getToken());
        BeautyLiveApplication.getRequestQueue().add(DATA_JSON, request, OnResponse);
    }

    private OnResponseListener<JSONObject> OnResponse = new OnResponseListener<JSONObject>() {
        @Override
        public void onStart(int i) {
            Log.i("lll", "开始了");
        }

        @Override
        public void onSucceed(int i, Response<JSONObject> response) {
            if (i == DATA_JSON) {// 判断what是否是刚才指定的请求
                //                {"code":0,"msg":"ok","data":"320200"}这个是什么
                // 请求成功
                JSONObject result = response.get();// 响应结果
                try {
                    JSONObject json=result.getJSONObject("data");
                    mTestInfo=gson.fromJson(json.toString(), tw.chiae.inlive.data.bean.me.UserInfo.class);
                    cn.jpush.im.android.api.model.UserInfo userInfo;
                    for (int j=0;j<mDatas.size();j++){
                        userInfo =((cn.jpush.im.android.api.model.UserInfo) mDatas.get(j).getTargetInfo());
                        if (userInfo.getUserName().replaceAll("user","").equals(mTestInfo.getId())){
                            mFuImgUrl.put(userInfo.getUserName().replaceAll("user",""),mTestInfo.getAvatar());
                            break;
                        }
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
            Log.i("lll", "请求失败了哦" + s);
        }

        @Override
        public void onFinish(int i) {
            Log.i("lll", "viewpager结束了");
            count++;
            if (count==mDatas.size()) {
                handler.sendEmptyMessage(21);
            }
        }
    };


    private List<tw.chiae.inlive.data.bean.me.UserInfo> mFuDatas;
    private Gson gson;
    private tw.chiae.inlive.data.bean.me.UserInfo mTestInfo;
    private HashMap<String,String> mFuImgUrl;
    private int count;

    //    拉去服务器得
    public void  initDatas(List<Conversation> data){
        mFuDatas=new ArrayList<>();
        mFuImgUrl= new HashMap<>();
        cn.jpush.im.android.api.model.UserInfo info;
        gson = new Gson();
        for (int i=0;i<data.size();i++){
            info= (cn.jpush.im.android.api.model.UserInfo) data.get(i).getTargetInfo();
            getDatas(info.getUserName().replaceAll("user",""));
        }
    }
}
