package tw.chiae.inlive.domain;

import tw.chiae.inlive.data.bean.AnchorSummary;
import tw.chiae.inlive.data.bean.BaseResponse;
import tw.chiae.inlive.data.bean.CurrencyRankItem;
import tw.chiae.inlive.data.bean.PageBean;
import tw.chiae.inlive.data.repository.SourceFactory;

import rx.Observable;

/**
 * Created by huanzhang on 2016/5/9.
 */
public class CurrencyManager {
    public Observable<BaseResponse<PageBean<CurrencyRankItem>>> getCurrentByUser(String uid,
                                                                                 int pageNum){
        return SourceFactory.create().getCurrencyRankList(uid, pageNum);
    }
}
