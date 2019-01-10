package tw.chiae.inlive.presentation.ui.main.topic;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jakewharton.rxbinding.view.RxView;
import tw.chiae.inlive.R;
import tw.chiae.inlive.data.bean.HotAnchorSummary;
import tw.chiae.inlive.data.bean.room.PrivateLimitBean;
import tw.chiae.inlive.data.repository.SourceFactory;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.presentation.ui.base.BaseActivity;
import tw.chiae.inlive.presentation.ui.base.ptr.BasePtr;
import tw.chiae.inlive.presentation.ui.base.recycler.SimpleRecyclerAdapter;
import tw.chiae.inlive.presentation.ui.base.recycler.SimpleRecyclerHolder;
import tw.chiae.inlive.presentation.ui.main.IndexFragment;
import tw.chiae.inlive.presentation.ui.main.index.FollowedAnchorInterface;
import tw.chiae.inlive.presentation.ui.main.index.FollowedAnchorPresenter;
import tw.chiae.inlive.presentation.ui.main.index.HotAnchorFragment;
import tw.chiae.inlive.presentation.ui.main.index.privateutil.GoPrivateRoom;
import tw.chiae.inlive.presentation.ui.main.index.privateutil.GoPrivateRoomInterface;
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

public class NewsTopicActivity extends BaseActivity implements FollowedAnchorInterface, GoPrivateRoomInterface {
    private PtrFrameLayout ptrFrameLayout;
    private RecyclerView recyclerView;
    private TopicAnchorAdapter adapter;
    private NewsTopicPresenter presenter;
    private RelativeLayout rlEmptyLive;
    private static final String TOPICID = "topicID";
    private static final String TOPICTITLE = "topicTitle";
    private String topic, topictext;
    private TextView topicTitle;
    private ImageButton back;
    // 私密房间的dialog
    private GoPrivateRoom goPrivateRoom;
    // 当前选中的用户
    private HotAnchorSummary hotAnchorSummary;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_news_topic;
    }

    @SuppressWarnings("unused")
    public static Intent createIntent(Context context, String topicid, String topicTitle) {
        Intent intent = new Intent(context, NewsTopicActivity.class);
        intent.putExtra(TOPICID, topicid);
        intent.putExtra(TOPICTITLE, topicTitle);
        return intent;
    }

    @Override
    protected void parseIntentData(Intent intent, boolean isFromNewIntent) {
        super.parseIntentData(intent, isFromNewIntent);
        topic = intent.getStringExtra(TOPICID);
        topictext = intent.getStringExtra(TOPICTITLE);
    }

    @Override
    protected void findViews(Bundle savedInstanceState) {
        presenter = new NewsTopicPresenter(this);
        back = $(R.id.topic_index_imgbtn_back);
        ptrFrameLayout = $(R.id.hot_topic_ptr);
        topicTitle = $(R.id.topic_index_title);
        topicTitle.setText(topictext);
        BasePtr.setRefreshOnlyStyle(ptrFrameLayout);

        recyclerView = $(R.id.hot_topic_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ptrFrameLayout.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return super.checkCanDoRefresh(frame, recyclerView, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                presenter.loadFirstTopic(Integer.parseInt(topic));
            }
        });

        rlEmptyLive = $(R.id.followed_topic_rl_no_live);
        RxView.clicks(rlEmptyLive)
                .throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {

                    }
                });

        ptrFrameLayout.autoRefresh();
    }

    @Override
    protected void init() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
            adapter = new TopicAnchorAdapter(list);
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
            goPrivateRoom.setGoPrivateRoomInterface(this);
            Bundle bundle = new Bundle();
            bundle.putInt(GoPrivateRoom.GO_PLID, bean.getId());
            bundle.putString(GoPrivateRoom.GO_PRIVATE_TYPE, String.valueOf(bean.getPtid()));
            bundle.putString(GoPrivateRoom.GO_PRIVATE_CONTE, bean.getPrerequisite());
            bundle.putString(GoPrivateRoom.GO_NAME, hotAnchorSummary.getNickname());
            bundle.putString(GoPrivateRoom.GO_PHOTO, hotAnchorSummary.getAvatar());
            bundle.putString(GoPrivateRoom.GO_USER_ID, hotAnchorSummary.getId());
            bundle.putString(GoPrivateRoom.GO_LAYOU_BG, hotAnchorSummary.getSnap());
            goPrivateRoom.setArguments(bundle);
            goPrivateRoom.show(getFragmentManager(), "dasdas");
        } else
            startPlayFragment();
    }

    @Override
    public void startGoPlayFragment() {
        startPlayFragment();
    }

    @Override
    public void questGoPrivateRoom(String type, int msg, String userid, String pwd) {
        presenter.checkPrivatePass(type, msg, pwd, LocalDataManager.getInstance().getLoginInfo().getUserId(), userid);
    }

    private class TopicAnchorAdapter extends SimpleRecyclerAdapter<HotAnchorSummary,
            FollowedAnchorHolder> {
        public TopicAnchorAdapter(List<HotAnchorSummary> hotAnchorSummaries) {
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
        private SimpleDraweeView drawAvatar;

        @SuppressWarnings("unused")
        public FollowedAnchorHolder(View itemView) {
            super(itemView);
            drawAvatar = (SimpleDraweeView) itemView.findViewById(R.id.img_user_avatar);
            drawSnap = (SimpleDraweeView) itemView.findViewById(R.id
                    .item_hot_anchor_img_front_cover);
            tvNickname = (TextView) itemView.findViewById(R.id.item_hot_anchor_tv_nickname);
            tvLocation = (TextView) itemView.findViewById(R.id.item_hot_anchor_tv_location);
            tvOnlineCount = (TextView) itemView.findViewById(R.id.item_hot_anchor_tv_online_count);
        }

        @Override
        public void displayData(final HotAnchorSummary data) {
            drawAvatar.setImageURI(SourceFactory.wrapPathToUri(data.getAvatar()));
            drawSnap.setImageURI(SourceFactory.wrapPathToUri(data.getSnap()));
            tvNickname.setText(data.getNickname());
            tvLocation.setText(data.getCity());
            tvOnlineCount.setText(Spans.createSpan("", String.valueOf(data.getOnlineCount()), getString(R.string.unit_watching), new
                            ForegroundColorSpan
                            (ContextCompat.getColor(NewsTopicActivity.this, R.color.yunkacolor)),
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
        startActivity(RoomActivity.createIntent(NewsTopicActivity.this,
                RoomActivity.TYPE_VIEW_LIVE,
                hotAnchorSummary.getCurrentRoomNum(),
                hotAnchorSummary.getId(),
                PlayerFragment.createArgs(hotAnchorSummary)));
        NewsTopicActivity.this.overridePendingTransition(R.anim.fragment_slide_left_in, R.anim
                .fragment_slide_left_out);
    }
}
