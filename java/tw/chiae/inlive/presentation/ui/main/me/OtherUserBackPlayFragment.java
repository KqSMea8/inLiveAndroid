package tw.chiae.inlive.presentation.ui.main.me;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import tw.chiae.inlive.BeautyLiveApplication;
import tw.chiae.inlive.R;
import tw.chiae.inlive.data.bean.HotAnchorSummary;
import tw.chiae.inlive.data.bean.me.PlayBackInfo;
import tw.chiae.inlive.data.bean.me.UserInfo;
import tw.chiae.inlive.data.bean.room.PrivateLimitBean;
import tw.chiae.inlive.domain.BlackList;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.presentation.ui.base.BaseFragment;
import tw.chiae.inlive.presentation.ui.main.index.HotAnchorFragment;
import tw.chiae.inlive.presentation.ui.main.index.privateutil.GoPrivateRoom;
import tw.chiae.inlive.presentation.ui.main.index.privateutil.GoPrivateRoomInterface;
import tw.chiae.inlive.presentation.ui.room.RoomActivity;
import tw.chiae.inlive.presentation.ui.room.create.PrivateSetStringDialog;
import tw.chiae.inlive.presentation.ui.room.player.PlayerFragment;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class OtherUserBackPlayFragment extends BaseFragment implements IMe, GoPrivateRoomInterface {
    private static final String ARG_UID = "roomid";
    private String roomid;
    private OtherUserPresenter mePresenter;
    private RecyclerView mBackList;
    private SearchTableRecycleAdapter adapter;
    private TextView playbackCont;
    private long endTime;
    //    url的房间号
    private String urlroomid;
    //    url的开始时间
    private String urlstart;
    private String url;
    private UserInfo mUserInfo;
    private List<PlayBackInfo> playBackList;
    private OtherUserActivity otherUserActivity;
    // 私密房间的dialog
    private GoPrivateRoom goPrivateRoom;
    // 当前选中的用户
    private HotAnchorSummary hotAnchorSummary;
    //回播私密需要传入  localtiontime
    private String localtiontime;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_other_user_back_play;
    }

    public static OtherUserBackPlayFragment newInstance(UserInfo mUserInfo) {
        OtherUserBackPlayFragment fragment = new OtherUserBackPlayFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_UID, mUserInfo);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initViews(View view) {
        mePresenter = new OtherUserPresenter(this);
        mUserInfo = getArguments().getParcelable(ARG_UID);
        roomid = mUserInfo.getCurrentRoomNum();
        mBackList = (RecyclerView) view.findViewById(R.id.other_back_paly_list);
        playbackCont = (TextView) view.findViewById(R.id.back_play_cont);
        endTime = System.currentTimeMillis();
        mePresenter.getPlayList(LocalDataManager.getInstance().getLoginInfo().getToken(), roomid);
    }

    public void setOtherUserActivity(OtherUserActivity otherUserActivity) {
        this.otherUserActivity = otherUserActivity;
    }

    @Override
    public void showInfo(UserInfo info) {

    }

    @Override
    public void getPlayLists(final List<PlayBackInfo> playBackList) {
        if (playBackList != null) {
            this.playBackList = playBackList;
            playbackCont.setText(String.valueOf(playBackList.size()));
            adapter = new SearchTableRecycleAdapter(playBackList);
            adapter.setOnItemClickLitener(new OnItemClickLitener() {
                @Override
                public void onItemClick(View view, int position) {
                    localtiontime=playBackList.get(position).getLocaltime();
                    getViewPagerJson(roomid, String.valueOf(playBackList.get(position).getStart()), String.valueOf(playBackList.get(position).getEnd()));
                }

                @Override
                public void onItemLongClick(View view, int position) {
                    mePresenter.getPlayBackUrl(roomid, String.valueOf(playBackList.get(position).getStart()), String.valueOf(playBackList.get(position).getEnd()));
                }
            });
            mBackList.setLayoutManager(new LinearLayoutManager(otherUserActivity));
            mBackList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void getPlayUrl(String url) {

    }

    @Override
    public void getHitCode(int code) {

    }

    @Override
    public void getRemoveHitCode(int code) {

    }

    @Override
    public void getStartCode(int code) {

    }

    @Override
    public void getRemoveStartCode(int code) {

    }

    @Override
    public void showPrivateLimit(PrivateLimitBean bean) {
        dismissLoadingDialog();
        if (bean.getCome() == 0 && bean.getPtid() != 0) {
            if (bean.getPtid() == Integer.valueOf(PrivateSetStringDialog.PRIVTE_ROOM_LEVEL)) {
                //如果等级够了，直接调用验证接口
                if (Integer.valueOf(bean.getPrerequisite()) <= Integer.valueOf(LocalDataManager.getInstance().getLoginInfo().getLevel())) {
                    mePresenter.checkPrivatePass(PrivateSetStringDialog.PRIVTE_ROOM_LEVEL, bean.getId(), "", LocalDataManager.getInstance().getLoginInfo().getUserId(), hotAnchorSummary.getId());
                    return;
                }
            }
            if (goPrivateRoom == null)
                goPrivateRoom = new GoPrivateRoom();
            goPrivateRoom.setGoPrivateRoomInterface(OtherUserBackPlayFragment.this);
            Bundle bundle = new Bundle();
            bundle.putInt(GoPrivateRoom.GO_PLID, bean.getId());
            bundle.putString(GoPrivateRoom.GO_PRIVATE_TYPE, String.valueOf(bean.getPtid()));
            bundle.putString(GoPrivateRoom.GO_PRIVATE_CONTE, bean.getPrerequisite());
            bundle.putString(GoPrivateRoom.GO_NAME, hotAnchorSummary.getNickname());
            bundle.putString(GoPrivateRoom.GO_PHOTO, hotAnchorSummary.getAvatar());
            bundle.putString(GoPrivateRoom.GO_USER_ID, hotAnchorSummary.getId());
            bundle.putString(GoPrivateRoom.GO_LAYOU_BG, hotAnchorSummary.getSnap());
            goPrivateRoom.setArguments(bundle);
            goPrivateRoom.show(getActivity().getFragmentManager(), "dasdas");
        } else
            startPlayFragment();
    }

    @Override
    public void startGoPlayFragment() {
        startPlayFragment();
    }

    @Override
    public void FailDelBlackList(String blackUid) {
        Log.i("RayTest","刪除失敗");
    }

    @Override
    public void CompleteDelBlackList(List<BlackList> blackUid,int code) {
        Log.i("RayTest","刪除成功");
    }

    @Override
    public void CompleteAddBlackList(List<BlackList> blackUserId) {
        Log.i("RayTest","新增成功");
    }

    @Override
    public void FailAddBlackList(String blackUserId) {
        Log.i("RayTest","新增失敗");
    }



    int PAGER_JSON = 1;

    public void getViewPagerJson(String roomid, String start, String end) {
        Request<JSONObject> request = NoHttp.createJsonObjectRequest(Const.MAIN_HOST_URL + "/OpenAPI/V1/Qiniu/getPlayback", RequestMethod.GET);
        urlroomid = roomid;
        urlstart = start;
        request.add("roomID", roomid);
        request.add("startTime", start);
        request.add("endTime", end);
        BeautyLiveApplication.getRequestQueue().add(PAGER_JSON, request, ViewPagerOnResponse);
    }

    private OnResponseListener<JSONObject> ViewPagerOnResponse = new OnResponseListener<JSONObject>() {
        @Override
        public void onStart(int i) {
        }

        @Override
        public void onSucceed(int i, Response<JSONObject> response) {
            if (i == PAGER_JSON) {// 判断what是否是刚才指定的请求
                //                {"code":0,"msg":"ok","data":"320200"}这个是什么
                // 请求成功
                JSONObject result = response.get();// 响应结果
                try {
                    JSONObject data = result.getJSONObject("data");
                    url = data.getString("ORIGIN");
                    handler.sendEmptyMessage(PAGER_JSON);
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
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == PAGER_JSON) {
                HotAnchorSummary hotAnchorSummary = new HotAnchorSummary();
                hotAnchorSummary.setNickname(mUserInfo.getNickname());
                hotAnchorSummary.setAvatar(mUserInfo.getAvatar());
                hotAnchorSummary.setSnap(mUserInfo.getSnap());
                hotAnchorSummary.setCity(mUserInfo.getCity());
                hotAnchorSummary.setCurrentRoomNum(mUserInfo.getCurrentRoomNum());
                hotAnchorSummary.setId(mUserInfo.getId());
                hotAnchorSummary.setSnap(mUserInfo.getSnap());

                OtherUserBackPlayFragment.this.hotAnchorSummary = hotAnchorSummary;
                showLoadingDialog();
                mePresenter.loadBackPrivateLimit(hotAnchorSummary.getId(), localtiontime);
            }
        }
    };

    @Override
    public void questGoPrivateRoom(String type, int msg, String userid, String pwd) {
        mePresenter.checkPrivatePass(type, msg, pwd, LocalDataManager.getInstance().getLoginInfo().getUserId(), userid);
    }

    @Override
    public void setCoinData(int currencyItemSum) {

    }

    @Override
    public void onCancelDialogcheck(Bundle mArgs) {

    }

    //    适配器
    public class SearchTableRecycleAdapter extends RecyclerView.Adapter<SearchTableRecycleAdapter.Holder> {

        private List<PlayBackInfo> diseaselist;
        private LayoutInflater mInflater;

        public SearchTableRecycleAdapter(List<PlayBackInfo> placelist) {
            this.diseaselist = placelist;
            mInflater = LayoutInflater.from(otherUserActivity);
        }

        public Holder viewHolder;

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
//        找到我们的item布局
            View view = mInflater.inflate(R.layout.layout_playback_item, parent, false);
//        实例化一个Myviewholder对象 ,并传入我们的item布局
            viewHolder = new Holder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final Holder holder, final int position) {
            holder.space.setText(getSpace(diseaselist.get(position).getStart()));
            holder.start.setText(refFormatNowDate(diseaselist.get(position).getStart()));
            holder.end.setText(refFormatNowDate(diseaselist.get(position).getEnd()));
            if (diseaselist.get(position).getTitle()==null&&diseaselist.get(position).getTopic()==null) {
                holder.title.setText(getString(R.string.my_backpublish_item_notitle));
            }else {
                StringBuffer stringBuffer=new StringBuffer();
                if (diseaselist.get(position).getTopic()!=null){
                    for (int i=0;i<diseaselist.get(position).getTopic().size();i++){
                        stringBuffer.append(diseaselist.get(position).getTopic().get(i).getTitle()+" ");
                    }
                }
                holder.title.setText(diseaselist.get(position).getTitle()+" "+stringBuffer.toString());
            }
            holder.looknumber.setText("0");
            // 如果设置了回调，则设置点击事件
            if (mOnItemClickLitener != null) {
//          这里item设置我们系统的点击事件
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                    然后这里设置一个int 用来接受当前点击的是 myViewHolder的哪个layout
                        int pos = holder.getLayoutPosition();
//                    然后这里调用接口里的点击抽象方法，并传入 View类型的参数 和当前点击的是哪个
                        mOnItemClickLitener.onItemClick(holder.itemView, pos);
                    }
                });

//            这里item设置我们系统的长点击事件
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
//                    长按依旧是同理
                        int pos = holder.getLayoutPosition();
                        mOnItemClickLitener.onItemLongClick(holder.itemView, pos);
                        return false;
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            if (diseaselist == null) {
                return 0;
            }
            return diseaselist.size();
        }

        public void setPlaceList(List<PlayBackInfo> placelist) {
            this.diseaselist = placelist;
            notifyDataSetChanged();
        }

        //    ========================1=========================点击监听 需要我们自己来写，但是依然需要用到系统的点击回调
//    监听依旧需要一个回调接口

        private OnItemClickLitener mOnItemClickLitener;

        //   提供一个公开的方法用来进行回调，参数就是我们的回调接口，让需要调用的Activity来实现这个接口里的俩个抽象方法
        public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
            this.mOnItemClickLitener = mOnItemClickLitener;
        }


        //    内部类holder
        public class Holder extends RecyclerView.ViewHolder {
            public TextView space, start, end, title, looknumber;

            public Holder(View itemView) {
                super(itemView);
                space = (TextView) itemView.findViewById(R.id.space_item);
                start = (TextView) itemView.findViewById(R.id.start_item);
                end = (TextView) itemView.findViewById(R.id.end_item);
                title = (TextView) itemView.findViewById(R.id.play_back_title);
                looknumber = (TextView) itemView.findViewById(R.id.play_back_looknumber);
            }
        }
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    //  时间转换类哦
    public String refFormatNowDate(long time) {
        Date nowTime = new Date(time * 1000);
        SimpleDateFormat sdFormatter = new SimpleDateFormat("MM-dd HH:mm");
        String retStrFormatNowDate = sdFormatter.format(nowTime);
        return retStrFormatNowDate;
    }

    //    计算当前时间与所给时间间隔几天
    public String getSpace(long times) {
        long space = endTime / 1000 - times;
        int day = (int) (space / 60 / 60 / 24);
        if (day > 0) {
            return day + getString(R.string.my_backpublish_before);
        } else {
            return getString(R.string.my_backpublish_today);
        }
    }

    public void startPlayFragment() {
        startActivity(RoomActivity.createIntent(otherUserActivity,
                RoomActivity.TYPE_VIEW_REPLAY,
                urlroomid + "_" + urlstart + "_" + url,
                mUserInfo.getId(),
                PlayerFragment.createArgs(hotAnchorSummary)));
        otherUserActivity.overridePendingTransition(R.anim.fragment_slide_left_in, R.anim
                .fragment_slide_left_out);
    }
}
