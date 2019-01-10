package tw.chiae.inlive.presentation.ui.room.publish;

import tw.chiae.inlive.data.repository.ServerEventResponse;
import tw.chiae.inlive.presentation.ui.base.BaseUiInterface;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public interface PublishFragmentUiInterface extends BaseUiInterface {
    /**
     * 刷新餘額
     * @param coinbalance
     */
    void upDataLoginBalance(String coinbalance);

    void upDataRoomStreamID(String response);

    void EndHotPoint(long coin);

    void setBalamceValue(String data);

    void setupStartPublishTime(String starttime);

    void sendLiveMsg(String msg);
}
