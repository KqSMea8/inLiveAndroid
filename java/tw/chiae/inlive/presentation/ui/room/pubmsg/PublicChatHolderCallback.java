package tw.chiae.inlive.presentation.ui.room.pubmsg;

import java.util.HashMap;

import tw.chiae.inlive.data.bean.websocket.RoomPublicMsg;

/**
 * Created by rayyeh on 2017/3/25.
 */

interface PublicChatHolderCallback {

    void onCompleteData(RoomPublicMsg msgData, HashMap<String, Object> data);

    void onCancel();
}
