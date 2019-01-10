package tw.chiae.inlive.presentation.ui.main.index;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.github.magiepooh.recycleritemdecoration.ItemDecorations;
import com.jakewharton.rxbinding.view.RxView;
import tw.chiae.inlive.R;
import tw.chiae.inlive.data.bean.AnchorSummary;
import tw.chiae.inlive.data.bean.HotAnchorSummary;
import tw.chiae.inlive.data.bean.ThemBean;
import tw.chiae.inlive.data.bean.room.PrivateLimitBean;
import tw.chiae.inlive.data.repository.SourceFactory;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.presentation.ui.base.BaseFragment;
import tw.chiae.inlive.presentation.ui.base.ptr.BasePtr;
import tw.chiae.inlive.presentation.ui.base.recycler.SimpleRecyclerAdapter;
import tw.chiae.inlive.presentation.ui.base.recycler.SimpleRecyclerHolder;
import tw.chiae.inlive.presentation.ui.main.index.privateutil.GoPrivateRoom;
import tw.chiae.inlive.presentation.ui.main.index.privateutil.GoPrivateRoomInterface;
import tw.chiae.inlive.presentation.ui.main.topic.AllTopicActivity;
import tw.chiae.inlive.presentation.ui.main.topic.NewsTopicActivity;
import tw.chiae.inlive.presentation.ui.room.RoomActivity;
import tw.chiae.inlive.presentation.ui.room.create.PrivateSetStringDialog;
import tw.chiae.inlive.presentation.ui.room.player.PlayerFragment;
import tw.chiae.inlive.util.Const;
import tw.chiae.inlive.util.L;
import tw.chiae.inlive.util.PicUtil;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import rx.functions.Action1;

/**
 * 推荐
 *
 * @author Muyangmin
 * @since 1.0.0
 */
public class CommendAnchorFragment extends BaseFragment implements CommendInterface, GoPrivateRoomInterface {

    private CommendPresenter presenter;

    private PtrFrameLayout ptrFrameLayout;
    private RecyclerView recyclerView;
    private View viewEmpty;
    private CommendAdapter adapter;
    private View mThemlayout;
    private TagFlowLayout mFlowLayout;
    //    跳转到热门
    private TextView goHot;
    //    跳转到人呢的回调
    private CommendShowHot commendShowHot;
    // 私密房间的dialog
    private GoPrivateRoom goPrivateRoom;
    // 当前选中的用户
    private AnchorSummary anchorSummary;

    public static CommendAnchorFragment newInstance() {
        return new CommendAnchorFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_commend_anchor;
    }

    @Override
    protected void initViews(View view) {

        presenter = new CommendPresenter(this);
        ptrFrameLayout = $(view, R.id.commend_anchor_ptr);
        viewEmpty = $(view, R.id.commend_tv_empty);
        BasePtr.setRefreshOnlyStyle(ptrFrameLayout);
        goHot = $(view, R.id.followed_anchor_tv_view_hot);
        goHot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commendShowHot.showHot();
            }
        });
        recyclerView = $(view, R.id.commend_anchor_recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 3));
        recyclerView.addItemDecoration(ItemDecorations.vertical(view.getContext())
                .type(0, R.drawable.divider_decoration_transparent_h1)
                .create());
        ptrFrameLayout.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return super.checkCanDoRefresh(frame, recyclerView, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                presenter.loadCommendAnchors(LocalDataManager.getInstance().getLoginInfo().getToken(), LocalDataManager.getInstance().getLoginInfo().getCity());
            }
        });
        ptrFrameLayout.autoRefresh();

    }

    @Override
    public void questGoPrivateRoom(String type, int msg, String userid, String pwd) {
        presenter.checkPrivatePass(type, msg, pwd, LocalDataManager.getInstance().getLoginInfo().getUserId(), userid);
    }

    @Override
    public void onCancelDialogcheck(Bundle mArgs) {

    }

    public interface CommendShowHot {
        void showHot();
    }

    public void setCommendShowHot(CommendShowHot recommendShowHot) {
        this.commendShowHot = commendShowHot;
    }

    @Override
    public void showData(List<AnchorSummary> list) {
        viewEmpty.setVisibility(View.INVISIBLE);
        if (adapter == null) {
            adapter = new CommendAdapter(list);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.setDataList(list);
        }
    }

    @Override
    public void onResume() {
//        MobclickAgent.onPageStart("主页-最新");
        super.onResume();
    }

    @Override
    public void onPause() {
//        MobclickAgent.onPageStart("主页-最新");
        super.onPause();
    }

    @Override
    public void appendData(List<AnchorSummary> list) {
        adapter.appendData(list);

    }

    @Override
    public void showEmptyResult() {
        //Clear data
        if (adapter != null) {
            adapter.setDataList(new ArrayList<AnchorSummary>());
        }
        viewEmpty.setVisibility(View.VISIBLE);
    }

    @Override
    public void showPrivateLimit(PrivateLimitBean bean) {
        dismissLoadingDialog();
        if (bean.getCome() == 0 && bean.getPtid() != 0) {
            if (bean.getPtid() == Integer.valueOf(PrivateSetStringDialog.PRIVTE_ROOM_LEVEL)) {
                //如果等级够了，直接调用验证接口
                if (Integer.valueOf(bean.getPrerequisite()) <= Integer.valueOf(LocalDataManager.getInstance().getLoginInfo().getLevel())) {
                    presenter.checkPrivatePass(PrivateSetStringDialog.PRIVTE_ROOM_LEVEL, bean.getId(), "", LocalDataManager.getInstance().getLoginInfo().getUserId(), anchorSummary.getId());
                    return;
                }
            }
            if (goPrivateRoom == null)
                goPrivateRoom = new GoPrivateRoom();
            goPrivateRoom.setGoPrivateRoomInterface(CommendAnchorFragment.this);
            Bundle bundle = new Bundle();
            bundle.putInt(GoPrivateRoom.GO_PLID, bean.getId());
            bundle.putString(GoPrivateRoom.GO_PRIVATE_TYPE, String.valueOf(bean.getPtid()));
            bundle.putString(GoPrivateRoom.GO_PRIVATE_CONTE, bean.getPrerequisite());
            bundle.putString(GoPrivateRoom.GO_NAME, anchorSummary.getNickname());
            bundle.putString(GoPrivateRoom.GO_PHOTO, anchorSummary.getAvatar());
            bundle.putString(GoPrivateRoom.GO_USER_ID, anchorSummary.getId());
            bundle.putString(GoPrivateRoom.GO_LAYOU_BG, anchorSummary.getSnap());
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
    public void showLoadingComplete() {
        super.showLoadingComplete();
        ptrFrameLayout.refreshComplete();
    }

    @Override
    public void setCoinData(int currencyItemSum) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.unsubscribeTasks();
    }

    private class CommendAdapter extends SimpleRecyclerAdapter<AnchorSummary,
            CommendHolder> {
        public CommendAdapter(List<AnchorSummary> anchorSummaries) {
            super(anchorSummaries);
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            return R.layout.item_recommend_user_result;
        }

        @NonNull
        @Override
        protected CommendHolder createHolder(View view) {
            return new CommendHolder(view);
        }
    }

    private class CommendHolder extends SimpleRecyclerHolder<AnchorSummary> {

        private TextView tvName;
        private SimpleDraweeView draweeAvatar;
        private ImageView imgLevel, imgStar;


        public CommendHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.item_search_anchor_tv_name);
//            tvIntro = (TextView) itemView.findViewById(R.id.item_search_anchor_tv_intro);
            draweeAvatar = (SimpleDraweeView) itemView.findViewById(R.id.img_user_avatar);
//            imgGender = (ImageView) itemView.findViewById(R.id.item_search_anchor_img_gender);
            imgLevel = (ImageView) itemView.findViewById(R.id.item_search_anchor_img_level);
            imgStar = (ImageView) itemView.findViewById(R.id.img_user_star_type);
//            imgbtnFollow = (ImageButton) itemView.findViewById(R.id.item_search_anchor_imgbtn_follow);
        }

        @Override
        public void displayData(final AnchorSummary data) {
//            imgbtnFollow.setVisibility(View.GONE);

            tvName.setText(data.getNickname());
//            tvIntro.setText(data.getIntro());
            if (!TextUtils.isEmpty(data.getAvatar())) {
                draweeAvatar.setImageURI(SourceFactory.wrapPathToUri(data.getAvatar()));
            }
//            imgGender.setImageResource(SourceFactory.isMale(data.getSex()) ?
//                    R.drawable.ic_global_male : R.drawable.ic_global_female);
            imgLevel.setImageResource(PicUtil.getLevelImageId(itemView.getContext(), data.getEmceeLevel()));
//            RxView.clicks(imgbtnFollow)
//                    .throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
//                    .subscribe(
//                            new Action1<Void>() {
//                        @Override
//                        public void call(Void aVoid) {
//                            if (data.isFollowing()) {
//                                data.setFollowing(false);
//                                imgbtnFollow.setImageResource(R.drawable.ic_follow);
//                                presenter.unfollowAnchor(data.getId());
//                            } else {
//                                data.setFollowing(true);
//                                imgbtnFollow.setImageResource(R.drawable.ic_followed);
//                                presenter.followAnchor(data.getId());
//                            }
//                        }
//                    });
            RxView.clicks(itemView)
                    .throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                    .subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
//                            startActivity(OtherUserActivity.createIntent(getActivity(),
//                                    Integer.parseInt(data.getId()),true));

//                            HotAnchorSummary hotAnchorSummary=new HotAnchorSummary();
//                            hotAnchorSummary.setSnap(data.getSnap());
//                            hotAnchorSummary.setOnlineCount(0);
//                            hotAnchorSummary.setAvatar(data.getAvatar());
//                            hotAnchorSummary.setCity(data.getCity());
//                            hotAnchorSummary.setCurrentRoomNum(data.getCurrentRoomNum());
//                            hotAnchorSummary.setId(data.getId());
//                            hotAnchorSummary.setNickname(data.getNickname());
                            anchorSummary = data;
                            showLoadingDialog();
                            presenter.loadPrivateLimit(data.getId());
                        }
                    });

//            imgStar.setImageResource(R.drawable.global_star_1);
        }
    }

    public void startPlayFragment() {
        startActivity(RoomActivity.createIntent(getActivity(),
                RoomActivity.TYPE_VIEW_LIVE,
                anchorSummary.getCurrentRoomNum(),
                anchorSummary.getId(),
                PlayerFragment.createArgs(anchorSummary)));
        getActivity().overridePendingTransition(R.anim.fragment_slide_left_in, R.anim
                .fragment_slide_left_out);
    }
}
