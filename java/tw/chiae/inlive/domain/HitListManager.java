package tw.chiae.inlive.domain;

import tw.chiae.inlive.data.bean.BaseResponse;
import tw.chiae.inlive.data.bean.CurrencyRankItem;
import tw.chiae.inlive.data.bean.PageBean;
import tw.chiae.inlive.data.bean.room.HitList;
import tw.chiae.inlive.data.repository.SourceFactory;

import java.util.List;

import rx.Observable;

/**
 * Created by lw on 2016/7/22.
 */
public class HitListManager {
    public Observable<BaseResponse<List<HitList>>> hitList(String token){
        return SourceFactory.create().getHitList(token);
    }
}
