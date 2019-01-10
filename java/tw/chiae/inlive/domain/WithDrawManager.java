package tw.chiae.inlive.domain;

import tw.chiae.inlive.data.bean.BaseResponse;
import tw.chiae.inlive.data.bean.PageBean;
import tw.chiae.inlive.data.bean.transaction.PresentRecordItem;
import tw.chiae.inlive.data.bean.transaction.WithDrawRespose;
import tw.chiae.inlive.data.repository.SourceFactory;

import java.util.List;

import rx.Observable;

/**
 * Created by huanzhang on 2016/5/13.
 */
public class WithDrawManager {
    public Observable<BaseResponse<WithDrawRespose>> withDraw(String num, String account){
        return SourceFactory.create().withDraw(num, account);
    }
    public Observable<BaseResponse<List<PresentRecordItem>>> getPresentRecord(){
        return SourceFactory.create().getPresentRecord();
    }
}
