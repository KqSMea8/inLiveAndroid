package tw.chiae.inlive.presentation.ui.main.index;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.WindowManager;


import tw.chiae.inlive.R;
import tw.chiae.inlive.presentation.ui.base.BaseActivity;
import tw.chiae.inlive.presentation.ui.base.BaseFragment;
import tw.chiae.inlive.presentation.ui.room.RoomActivity;
import tw.chiae.inlive.presentation.ui.room.RoomFragment;
import tw.chiae.inlive.presentation.ui.room.player.PlayerFragment;
import tw.chiae.inlive.presentation.ui.room.publish.PublishFragment;

import tw.chiae.inlive.util.Const;
import tw.chiae.inlive.util.L;
import tw.chiae.inlive.util.TimingLogger;

/**
 * Created by rayyeh on 2017/6/8.
 */

public class RoomActivityTest extends BaseActivity {

    private static final String EXTRA_ROOM_TYPE = "rt";
    private static final String EXTRA_ROOM_ID = "ri";
    private static final String EXTRA_ANCHOR_ID = "ai";
    private static final int FRAG_CONTAINER = R.id.room_container;
    private RoomFragment roomPFragment;

    private static int ID = 0;
    protected final String INSTANCE_TAG = "RoomActivity-" + ++ID;

    /**
     * 观看直播。
     */
    public static final int TYPE_VIEW_LIVE = 1;
    /**
     * 发布直播。
     */
    public static final int TYPE_PUBLISH_LIVE = 2;
    /**
     * 观看回放。
     */
    public static final int TYPE_VIEW_REPLAY = 3;
    private int mRoomType;
    private String mRoomId;
    private String mAnchorId;
    private Bundle mBundleArgs;
    //private RequestQueue requestQueue;


    @Override
    public void setCoinData(int currencyItemSum) {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_room;
    }

    @Override
    protected void findViews(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        TimingLogger logger = new TimingLogger("timing", "RoomActivity");
        getSupportFragmentManager().beginTransaction()
                .add(FRAG_CONTAINER, createFragmentByType())
                .commit();
        logger.addSplit("add fragment");
        logger.dumpToLog();
    }

    @Override
    protected void init() {
        setSwipeBackEnable(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(FRAG_CONTAINER);
        currentFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void parseIntentData(Intent intent, boolean isFromNewIntent) {
        super.parseIntentData(intent, isFromNewIntent);
        L.i(LOG_TAG, INSTANCE_TAG + ", " + isFromNewIntent);
        @RoomActivity.RoomType int roomType = intent.getIntExtra(EXTRA_ROOM_TYPE, -1);
        mRoomType = roomType;
        mRoomId = intent.getStringExtra(EXTRA_ROOM_ID);
        mAnchorId = intent.getStringExtra(EXTRA_ANCHOR_ID);
        mBundleArgs = intent.getExtras();
        if (isFromNewIntent) {
            getSupportFragmentManager().beginTransaction()
                    .replace(FRAG_CONTAINER, createFragmentByType())
                    .commit();
        }
        //requestQueue = webRequestUtil.getVolleyIntence(this);
    }

    public static Intent createIntent(Context context,
                                      @RoomActivity.RoomType int roomType,
                                      @NonNull String roomId,
                                      @NonNull String anchorId,
                                      Bundle args) {
        Intent intent = new Intent(context, RoomActivityTest.class);
        intent.putExtra(EXTRA_ROOM_TYPE, roomType);
        intent.putExtra(EXTRA_ROOM_ID, roomId);
        intent.putExtra(EXTRA_ANCHOR_ID, anchorId);
        Bitmap bitmap;
        if (args != null) {
            intent.putExtras(args);
        }
        return intent;
    }

    private BaseFragment createFragmentByType() {
        BaseFragment fragment;
        switch (mRoomType) {
            case TYPE_VIEW_LIVE:
                fragment = PlayerFragment_forMemoryTest.newInstance(mBundleArgs, mRoomId, false);
                RoomFragment_forMemoryTest roomFragment = (RoomFragment_forMemoryTest) fragment;
                roomFragment.setmAnchorId(mAnchorId);
                break;
//            直播的
            case TYPE_PUBLISH_LIVE:
                //Not implemented yet
                fragment = PublishFragment.newInstance(mBundleArgs);
                roomPFragment = (RoomFragment) fragment;
                roomPFragment.setmAnchorId(mAnchorId);
                break;
            case TYPE_VIEW_REPLAY:
                //Not implemented yet
//                throw new UnsupportedOperationException("unsupported room type: " + mRoomType);
//                这个roomid里隐藏了我们的playurl   roomid_starttime_url
                fragment = PlayerFragment.newInstance(mBundleArgs, mRoomId, true);
                RoomFragment roomFragmentRePlay = (RoomFragment) fragment;
                roomFragmentRePlay.setmAnchorId(mAnchorId);
                break;
            default:
                throw new IllegalArgumentException("Wrong room type: " + mRoomType);
        }
        return fragment;
    }
}
