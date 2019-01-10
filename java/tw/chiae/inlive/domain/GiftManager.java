package tw.chiae.inlive.domain;

import tw.chiae.inlive.data.bean.BaseResponse;
import tw.chiae.inlive.data.bean.gift.Gift;
import tw.chiae.inlive.data.repository.SourceFactory;

import java.util.List;

import rx.Observable;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public class GiftManager {

    public Observable<BaseResponse<List<Gift>>> getAvailableGifts() {
        return SourceFactory.create().getAvailableGifts();
    }

    public Observable<BaseResponse<Object>> sendGift(String toUserId, String giftId, int count){
        return SourceFactory.create().sendGift(toUserId, giftId, count);
    }

    public Observable<BaseResponse<Object>> sendHongBaoGift(String token, String roomid, String giftid){
        return SourceFactory.create().sendHongBaoGift(token, roomid, giftid);
    }


}
