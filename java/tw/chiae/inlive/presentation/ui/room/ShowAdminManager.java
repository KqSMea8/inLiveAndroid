package tw.chiae.inlive.presentation.ui.room;

import android.util.Log;

import tw.chiae.inlive.data.bean.BaseResponse;
import tw.chiae.inlive.data.bean.HotAnchorSummary;
import tw.chiae.inlive.data.bean.room.RoomAdminInfo;
import tw.chiae.inlive.data.repository.SourceFactory;

import java.util.List;

import rx.Observable;

/**
 * Created by Administrator on 2016/7/14 0014.
 */
public class ShowAdminManager {


    public Observable<BaseResponse<List<RoomAdminInfo>>> loadAdminList(String token, String roomuid) {
        return SourceFactory.create().getAdmin(token,roomuid);
    }

    public Observable<BaseResponse<Object>> removeAdmin(String token, String roomuid,String adminid) {
        return SourceFactory.create().removeAdmin(token,roomuid,adminid);
    }
}
