package tw.chiae.inlive.presentation.ui.login.splash;

import java.util.List;

import rx.Observable;
import tw.chiae.inlive.data.repository.SourceFactory;
import tw.chiae.inlive.domain.BlackList;

/**
 * Created by rayyeh on 2017/8/17.
 */

public class BlackListManager {
    public Observable<List<BlackList>> getblacklist(String uid, int type, String token) {
        return SourceFactory.createApi3Json().getblacklist(uid,type,token);
    }

    public Observable<List<BlackList>> addblacklist(String uid, String roomid, String blackid, int type,String token) {
        return SourceFactory.createApi3Json().addblacklist(uid,roomid,blackid,type, token);
    }

    public Observable<List<BlackList>> delblacklist(String uid, String token, String id) {
        return SourceFactory.createApi3Json().delblacklist(uid,token,id);
    }
}
