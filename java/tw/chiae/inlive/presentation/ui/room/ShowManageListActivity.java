package tw.chiae.inlive.presentation.ui.room;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.github.magiepooh.recycleritemdecoration.ItemDecorations;
import com.jakewharton.rxbinding.view.RxView;
import tw.chiae.inlive.BeautyLiveApplication;
import tw.chiae.inlive.R;
import tw.chiae.inlive.data.bean.AnchorSummary;
import tw.chiae.inlive.data.bean.HotAnchorSummary;
import tw.chiae.inlive.data.bean.me.UserInfo;
import tw.chiae.inlive.data.bean.room.RoomAdminInfo;
import tw.chiae.inlive.data.bean.websocket.WsRoomManageRequest;
import tw.chiae.inlive.data.repository.SourceFactory;
import tw.chiae.inlive.data.websocket.WsObjectPool;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.presentation.ui.base.BaseActivity;
import tw.chiae.inlive.presentation.ui.base.ptr.BasePtr;
import tw.chiae.inlive.presentation.ui.base.recycler.SimpleRecyclerAdapter;
import tw.chiae.inlive.presentation.ui.base.recycler.SimpleRecyclerHolder;
import tw.chiae.inlive.presentation.ui.main.me.OtherUserActivity;
import tw.chiae.inlive.presentation.ui.room.player.PlayerFragment;
import tw.chiae.inlive.util.Const;
import tw.chiae.inlive.util.L;
import tw.chiae.inlive.util.PicUtil;
import tw.chiae.inlive.util.RecycleViewDivider;
import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import rx.functions.Action1;

public class ShowManageListActivity  extends BaseActivity implements ShowManageInterface{

    private TextView empty;
    private ShowManageListPresenter presenter;
    private PtrFrameLayout ptrFrameLayout;
    private RecyclerView admin_recycler;
    private List<RoomAdminInfo> adminlist;
    private RecommendAdapter adapter;
    public final static String KEY_LIST = "admin";
    public final static String KEY_ROOMUID = "roomid";
    private String roomuid;
    private String removeid;
//    头像右下角的小角角
    public static Intent createIntent(Context context, List<RoomAdminInfo> adminInfoList,String roomuid) {
        Intent intent = new Intent(context, ShowManageListActivity.class);
        intent.putParcelableArrayListExtra(KEY_LIST, (ArrayList<? extends Parcelable>) adminInfoList);
        intent.putExtra(KEY_ROOMUID,roomuid);
        return intent;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_show_manage_list;
    }

    @Override
    protected void findViews(Bundle savedInstanceState) {
        //        获取数据
        this.adminlist=getIntent().getParcelableArrayListExtra(KEY_LIST);
        this.roomuid=getIntent().getStringExtra(KEY_ROOMUID);
//        请求
        presenter=new ShowManageListPresenter(this);
        ptrFrameLayout = (PtrFrameLayout) findViewById(R.id.recommend_anchor_ptr);
        admin_recycler= (RecyclerView) findViewById(R.id.admin_recycler);
        empty= (TextView) findViewById(R.id.recommend_tv_empty);
        BasePtr.setRefreshOnlyStyle(ptrFrameLayout);
        if (adminlist==null){
            empty.setVisibility(View.VISIBLE);
        }
//        天假布局
        admin_recycler.setLayoutManager(new LinearLayoutManager(this));
        //        分割线
        admin_recycler.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.VERTICAL, 6, getResources().getColor(R.color.yunkacolor)));
//        admin_recycler.addItemDecoration(ItemDecorations.vertical(this)
//                .type(0, R.drawable.divider_decoration_transparent_h1)
//                .create());

        ptrFrameLayout.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return super.checkCanDoRefresh(frame, admin_recycler, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                presenter.loadAdminList(LocalDataManager.getInstance().getLoginInfo().getToken(),roomuid);
            }
        });

//        ptrFrameLayout.autoRefresh();

        if (adminlist!=null) {
            adapter = new RecommendAdapter(adminlist);
            admin_recycler.setAdapter(adapter);
        }else {
            adminlist=new ArrayList<RoomAdminInfo>();
            adapter = new RecommendAdapter(adminlist);
            admin_recycler.setAdapter(adapter);
        }
    }

    @Override
    protected void init() {

    }


//    得到的刷新数据
    @Override
    public void showEmptyResult(List<RoomAdminInfo> list) {
        if (list!=null) {
            if (adapter == null) {
                adapter = new RecommendAdapter(list);
                admin_recycler.setAdapter(adapter);
            } else {
                adapter.setDataList(list);
            }
        }else {
            empty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void successAdmin() {
        ptrFrameLayout.refreshComplete();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void requestOver() {
        ptrFrameLayout.refreshComplete();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void setCoinData(int currencyItemSum) {

    }

    private class RecommendAdapter extends SimpleRecyclerAdapter<RoomAdminInfo, RecommendHolder> {
        public RecommendAdapter(List<RoomAdminInfo> anchorSummaries) {
            super(anchorSummaries);
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            return R.layout.item_search_result;
        }

        @NonNull
        @Override
        protected RecommendHolder createHolder(View view) {
            return new RecommendHolder(view);
        }
    }


    private class RecommendHolder extends SimpleRecyclerHolder<RoomAdminInfo> {

        private TextView tvNickname, tvIntro;
        private SimpleDraweeView draweeAvatar;
        private ImageView imgGender, imgLevel, imgStar;
        private ImageButton imgbtnFollow;

        public RecommendHolder(View itemView) {
            super(itemView);
            tvNickname = (TextView) itemView.findViewById(R.id.item_search_anchor_tv_nickname);
            tvIntro = (TextView) itemView.findViewById(R.id.item_search_anchor_tv_intro);
            draweeAvatar = (SimpleDraweeView) itemView.findViewById(R.id.img_user_avatar);
            imgGender = (ImageView) itemView.findViewById(R.id.item_search_anchor_img_gender);
            imgLevel = (ImageView) itemView.findViewById(R.id.item_search_anchor_img_level);
            imgStar = (ImageView) itemView.findViewById(R.id.img_user_star_type);
            imgbtnFollow = (ImageButton) itemView.findViewById(R.id
                    .item_search_anchor_imgbtn_follow);
        }

        @Override
        public void displayData(final RoomAdminInfo data) {
            imgbtnFollow.setVisibility(View.GONE);
            tvNickname.setText(data.getNickname());
            tvIntro.setText(data.getIntro());
            if (!TextUtils.isEmpty(data.getAvatar())) {
                draweeAvatar.setImageURI(SourceFactory.wrapPathToUri(data.getAvatar()));
            }
            imgGender.setImageResource(SourceFactory.isMale(Integer.parseInt(data.getSex())) ? R.drawable.ic_global_male : R.drawable.ic_global_female);
            imgLevel.setImageResource(PicUtil.getLevelImageId(itemView.getContext(), Integer.parseInt(data.getEmceelevel())));
            Log.i("RayTest",data.getNickname()+ ": getEmceelevel: "+data.getEmceelevel());
            RxView.clicks(imgbtnFollow)
                    .throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                    .subscribe(
                            new Action1<Void>() {
                                @Override
                                public void call(Void aVoid) {
//                                    这里是关注和取消关注
//                                    if (data.isFollowing()) {
//                                        data.setFollowing(false);
//                                        imgbtnFollow.setImageResource(R.drawable.ic_follow);
//                                        presenter.unfollowAnchor(data.getId());
//                                    } else {
//                                        data.setFollowing(true);
//                                        imgbtnFollow.setImageResource(R.drawable.ic_followed);
//                                        presenter.followAnchor(data.getId());
//                                    }
                                }
                            });
            RxView.clicks(itemView)
                    .throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                    .subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
                            startActivity(OtherUserActivity.createIntent(ShowManageListActivity.this,
                                    Integer.parseInt(data.getId()),true));
                        }
                    });
            RxView.longClicks(itemView)
                    .throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                    .subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
                            getViewPagerJson(LocalDataManager.getInstance().getLoginInfo().getToken(),roomuid,data.getId());
                            presenter.removeAdmin(LocalDataManager.getInstance().getLoginInfo().getToken(),roomuid,data.getId());
                            if(RoomInfoTmp.webService!=null){
                                RoomInfoTmp.webService.sendRequest(WsObjectPool.newRoomManageRequest(WsRoomManageRequest.MANAGE,
                                        data.getId(),
                                        data.getNickname(),
                                        WsRoomManageRequest.REMOVERADMINER));
                            }
                        }
                    });
//            imgStar.setImageResource(R.drawable.global_star_1);
        }
    }

    int code;
    int PAGER_JSON=1;
    public void getViewPagerJson(String token,String roomuid,String adminuid) {
        Request<JSONObject> request = NoHttp.createJsonObjectRequest(Const.MAIN_HOST_URL+"/OpenAPI/V1/room/delAdmin", RequestMethod.GET);
        request.add("token",token);
        request.add("uid",roomuid);
        request.add("adminuid",adminuid);
        removeid=adminuid;
        BeautyLiveApplication.getRequestQueue().add(PAGER_JSON, request, ViewPagerOnResponse);
    }

    private OnResponseListener<JSONObject> ViewPagerOnResponse = new OnResponseListener<JSONObject>() {
        @Override
        public void onStart(int i) {
            L.i("lll", "开始了");
        }

        @Override
        public void onSucceed(int i, Response<JSONObject> response) {
            Log.i("RayTest","delAdmin onSucceed");
            if (i == PAGER_JSON) {// 判断what是否是刚才指定的请求
                //                {"code":0,"msg":"ok","data":"320200"}这个是什么
                // 请求成功
                JSONObject result = response.get();// 响应结果
                try {
                    code=result.getInt("code");
                    Log.i("RayTest","delAdmin code "+code);
                    ShowManageListActivity.this.runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            if (code==0){
                                for (int i=0;i<adminlist.size();i++){
                                    if (removeid.equals(adminlist.get(i).getId())) {
                                        adminlist.remove(i);
                                         break;
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            }else {
                                toastShort(getString(R.string.room_admin_errorremove));
                            }
                        }
                    });
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

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

}
