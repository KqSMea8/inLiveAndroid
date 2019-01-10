package tw.chiae.inlive.presentation.ui.room.create;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.yolanda.nohttp.rest.Response;

import java.util.ArrayList;

import tw.chiae.inlive.data.bean.room.CreateRoomBean;
import tw.chiae.inlive.presentation.ui.room.RoomFragment;

/**
 * Created by rayyeh on 2017/4/25.
 */

public class PublishFragmentTest extends RoomFragment {

    private static final String ARG_PUSH_ADDRESS = "addr";
    private static final String ARG_PUSH_AHCHOR = "anchor";
    private static final String ARG_ROOM_ID = "roomidcon";
    private static final String CREATE_ROOM_BEAN = "createRoomBean";
    private static final String CAMERA_WIDTH = "camera_width";
    private static final String CAMERA_HEIGHT = "camera_height";

    @Override
    public void setCoinData(int currencyItemSum) {

    }

    @Override
    protected void setupLiveContent(String liveStatus, String liveMsg) {

    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    public void finishRoom(int roomType) {

    }

    @Override
    protected int getRoomType() {
        return 0;
    }

    @Override
    protected void parseArguments(Bundle bundle) {

    }

    @Override
    protected String getWsRoomId() {
        return null;
    }

    @Override
    protected String getWsUserId() {
        return null;
    }

    @Override
    protected boolean shouldSendHeartRequest() {
        return false;
    }

    @Override
    protected void updateBalance(double coinbalance) {

    }

    @Override
    protected void setMute(boolean closeMute) {

    }

    @Override
    protected void sendDanmu(String roomid, String content) {

    }

    @Override
    protected void stopPublishLive() {

    }

    public static Bundle createArgs(String pushAddress, int publishType, String roomid, CreateRoomBean createRoomBean, ArrayList<Integer> arrayListForSize) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PUSH_ADDRESS, pushAddress);
        bundle.putInt(ARG_PUSH_AHCHOR, publishType);
        bundle.putString(ARG_ROOM_ID, roomid);
        bundle.putParcelable(CREATE_ROOM_BEAN, createRoomBean);
        bundle.putInt(CAMERA_WIDTH, arrayListForSize.get(0));
        bundle.putInt(CAMERA_HEIGHT, arrayListForSize.get(1));
        return bundle;
    }

    @Override
    public void onSucceed(int what, Response<Bitmap> response) {

    }

}
