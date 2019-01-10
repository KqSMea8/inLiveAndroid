package tw.chiae.inlive.presentation.ui.main.index;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jakewharton.rxbinding.view.RxView;

import tw.chiae.inlive.R;
import tw.chiae.inlive.data.bean.HotAnchorSummary;
import tw.chiae.inlive.data.bean.room.PrivateLimitBean;
import tw.chiae.inlive.data.repository.SourceFactory;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.presentation.ui.base.BaseFragment;
import tw.chiae.inlive.presentation.ui.base.ptr.BasePtr;
import tw.chiae.inlive.presentation.ui.base.recycler.SimpleRecyclerAdapter;
import tw.chiae.inlive.presentation.ui.base.recycler.SimpleRecyclerHolder;
import tw.chiae.inlive.presentation.ui.main.IndexFragment;
import tw.chiae.inlive.presentation.ui.main.index.privateutil.GoPrivateRoom;
import tw.chiae.inlive.presentation.ui.main.index.privateutil.GoPrivateRoomInterface;
import tw.chiae.inlive.presentation.ui.main.me.OtherUserActivity;
import tw.chiae.inlive.presentation.ui.room.RoomActivity;
import tw.chiae.inlive.presentation.ui.room.create.PrivateSetStringDialog;
import tw.chiae.inlive.presentation.ui.room.player.PlayerFragment;
import tw.chiae.inlive.util.Const;
import tw.chiae.inlive.util.L;
import tw.chiae.inlive.util.Spans;


import java.util.List;
import java.util.concurrent.TimeUnit;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import rx.functions.Action1;

/**
 * 关注好友的直播列表页。
 *
 * @author Muyangmin
 * @since 1.0.0
 */
public class FollowedAnchorLiveFragment extends BaseFragment implements FollowedAnchorInterface, GoPrivateRoomInterface {

    private PtrFrameLayout ptrFrameLayout;
    private RelativeLayout rlEmptyLive;
    private RecyclerView recyclerView;
    private FollowedAnchorAdapter adapter;
    private FollowedAnchorPresenter presenter;
    //    跳转到热门
    private TextView goHot;
    //    跳转到热门的回调接口
    private FollwedShowHot follwedShowHot;
    //    private CircleProgress mProgressView;
// 私密房间的dialog
    private GoPrivateRoom goPrivateRoom;
    // 当前选中的用户
    private HotAnchorSummary hotAnchorSummary;


    public static FollowedAnchorLiveFragment newInstance() {
        return new FollowedAnchorLiveFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_followed_anchor_live;
    }

    @Override
    public void onResume() {
//        MobclickAgent.onPageStart("主页-关注");
        presenter.loadFirstPage();
        super.onResume();
    }

    @Override
    public void onPause() {
//        MobclickAgent.onPageStart("主页-最新");
        super.onPause();
    }

    @Override
    protected void initViews(View view) {

//        mProgressView = $(view,R.id.progress);
//        mProgressView.startAnim();

        presenter = new FollowedAnchorPresenter(this);
        ptrFrameLayout = $(view, R.id.followed_anchor_ptr);
        BasePtr.setRefreshOnlyStyle(ptrFrameLayout);
        goHot = $(view, R.id.followed_anchor_tv_view_hot);
        recyclerView = $(view, R.id.followed_anchor_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ptrFrameLayout.setPtrHandler(new PtrDefaultHandler() {

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return super.checkCanDoRefresh(frame, recyclerView, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                presenter.loadFirstPage();
            }
        });

        rlEmptyLive = $(view, R.id.followed_anchor_rl_no_live);
        RxView.clicks(rlEmptyLive)
                .throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        Fragment parentFragment = getParentFragment();
                        if (parentFragment == null || (!(parentFragment instanceof IndexFragment)
                        )) {
                            L.e(LOG_TAG, "Parent fragment unexpected, is %s!", parentFragment);
                            return;
                        }
                        ((IndexFragment) parentFragment).switchToHotList();
                    }
                });
        goHot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                follwedShowHot.showHot();
            }
        });
        //ptrFrameLayout.autoRefresh();
    }

    @Override
    public void questGoPrivateRoom(String type, int msg, String userid, String pwd) {
        presenter.checkPrivatePass(type, msg, pwd, LocalDataManager.getInstance().getLoginInfo().getUserId(), userid);
    }

    @Override
    public void onCancelDialogcheck(Bundle mArgs) {

    }


    public interface FollwedShowHot {
        void showHot();
    }

    public void setFollwedShowHot(FollwedShowHot follwedShowHot) {
        this.follwedShowHot = follwedShowHot;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.unsubscribeTasks();
    }

    @Override
    public void showLoadingComplete() {
//        super.showLoadingComplete();
        ptrFrameLayout.refreshComplete();
    }

    @Override
    public void setCoinData(int currencyItemSum) {

    }

    @Override
    public void showData(List<HotAnchorSummary> list) {
        rlEmptyLive.setVisibility((list == null || list.isEmpty()) ? View.VISIBLE : View.INVISIBLE);
        if (adapter == null) {
            adapter = new FollowedAnchorAdapter(list);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.setDataList(list);
        }
    }

    @Override
    public void appendData(List<HotAnchorSummary> list) {
        adapter.appendData(list);
    }

    @Override
    public void showPrivateLimit(PrivateLimitBean bean) {

        dismissLoadingDialog();
        if (bean.getCome() == 0 && bean.getPtid() != 0) {
            if (bean.getPtid() == Integer.valueOf(PrivateSetStringDialog.PRIVTE_ROOM_LEVEL)) {
                //如果等级够了，直接调用验证接口
                if (Integer.valueOf(bean.getPrerequisite()) <= Integer.valueOf(LocalDataManager.getInstance().getLoginInfo().getLevel())) {
                    presenter.checkPrivatePass(PrivateSetStringDialog.PRIVTE_ROOM_LEVEL, bean.getId(), "", LocalDataManager.getInstance().getLoginInfo().getUserId(), hotAnchorSummary.getId());
                    return;
                }
            }
            if (goPrivateRoom == null)
                goPrivateRoom = new GoPrivateRoom();
            goPrivateRoom.setGoPrivateRoomInterface(FollowedAnchorLiveFragment.this);
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

    private class FollowedAnchorAdapter extends SimpleRecyclerAdapter<HotAnchorSummary,
            FollowedAnchorHolder> {
        public FollowedAnchorAdapter(List<HotAnchorSummary> hotAnchorSummaries) {
            super(hotAnchorSummaries);
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            return R.layout.item_hot_anchor;
        }

        @NonNull
        @Override
        protected FollowedAnchorHolder createHolder(View view) {
            return new FollowedAnchorHolder(view);
        }
    }

    private class FollowedAnchorHolder extends SimpleRecyclerHolder<HotAnchorSummary> {

        private SimpleDraweeView drawSnap;
        private TextView tvNickname;
        private TextView tvLocation;
        private TextView tvOnlineCount;
        private TextView note;
        private SimpleDraweeView drawAvatar;
        private TextView live_type;
        private TextView tvTitle;
        private ImageView hot_point;
        private ImageView approveid_value;
        private TextView hot_point_value;
        @SuppressWarnings("unused")
        public FollowedAnchorHolder(View itemView) {
            super(itemView);
            drawAvatar = (SimpleDraweeView) itemView.findViewById(R.id.img_user_avatar);
            drawSnap = (SimpleDraweeView) itemView.findViewById(R.id
                    .item_hot_anchor_img_front_cover);
            tvNickname = (TextView) itemView.findViewById(R.id.item_hot_anchor_tv_nickname);
            tvLocation = (TextView) itemView.findViewById(R.id.item_hot_anchor_tv_location);
            tvTitle=(TextView)itemView.findViewById(R.id.item_hot_anchor_title);
            tvOnlineCount = (TextView) itemView.findViewById(R.id.item_hot_anchor_tv_online_count);
            live_type = (TextView) itemView.findViewById(R.id.live_type);
            hot_point = (ImageView)itemView.findViewById(R.id.iv_hot_point);
            hot_point_value = (TextView)itemView.findViewById(R.id.tv_hot_point);
            approveid_value = (ImageView)itemView.findViewById(R.id.iv_hot_approveid_type);
        }

        @Override
        public void displayData(final HotAnchorSummary data) {
            tvTitle.setVisibility(View.INVISIBLE);
            hot_point.setVisibility(View.INVISIBLE);
            approveid_value.setVisibility(View.INVISIBLE);
            hot_point_value.setVisibility(View.INVISIBLE);

            if (data.getBroadcasting() != null) {
                if (data.getBroadcasting().equals("y")) {
                    live_type.setBackgroundResource(R.drawable.live_type_on);
                } else {
                    live_type.setBackgroundResource(R.drawable.live_type_off);
                }
            }
            drawAvatar.setImageURI(SourceFactory.wrapPathToUri(data.getAvatar()));
            drawSnap.setImageURI(SourceFactory.wrapPathToUri(data.getSnap()));
            tvNickname.setText(data.getNickname());
            tvLocation.setText(data.getCity());
            tvOnlineCount.setText(Spans.createSpan("", String.valueOf(data.getOnlineCount()),
                    itemView.getContext().getString(R.string.hot_anchor_online_count_suffix), new
                            ForegroundColorSpan(ContextCompat.getColor(itemView.getContext(), R.color.yunkacolor)),
                    new RelativeSizeSpan(1.6F)));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hotAnchorSummary = data;
                    showLoadingDialog();
                    presenter.loadPrivateLimit(data.getId());
                }
            });
            //TODO note
//            if (StreamingProfile.StreamStatus.class.isAnnotation()) {
//                mProgressView.startAnim();
//            }
//            mProgressView.stopAnim();
        }
    }

    public void startPlayFragment() {
        if (hotAnchorSummary.getBroadcasting().equals("y")) {
            /*startActivity(RoomActivity.createIntent(getActivity(),
                    RoomActivity.TYPE_VIEW_LIVE,
                    hotAnchorSummary.getCurrentRoomNum(),
                    hotAnchorSummary.getId(),
                    PlayerFragment.createArgs(hotAnchorSummary)));*/
            startActivity(RoomActivity.createIntent(getActivity(),
                    RoomActivity.TYPE_VIEW_LIVE,
                    hotAnchorSummary.getCurrentRoomNum(),
                    hotAnchorSummary.getId(),
                    PlayerFragment.createArgs(hotAnchorSummary)));
            getActivity().overridePendingTransition(R.anim.fragment_slide_left_in, R.anim
                    .fragment_slide_left_out);

//            Intent myintent = new Intent(getActivity(), OtherUserActivity.class);
//            startActivity(myintent);
        } else {
            startActivity(OtherUserActivity.createIntent(getActivity(),
                    Integer.valueOf(hotAnchorSummary.getId()),
                    false));
            getActivity().overridePendingTransition(R.anim.fragment_slide_left_in, R.anim
                    .fragment_slide_left_out);
        }
    }
}
