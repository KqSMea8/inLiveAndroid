package tw.chiae.inlive.domain;

import tw.chiae.inlive.data.bean.BaseResponse;
import tw.chiae.inlive.data.bean.IncomeBean;
import tw.chiae.inlive.data.bean.transaction.RechargeInfo;
import tw.chiae.inlive.data.repository.SourceFactory;

import rx.Observable;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public class TransactionManager {

    public Observable<BaseResponse<RechargeInfo>> getRechargeMap() {
        return SourceFactory.create().getRechargeMap();
    }

    public Observable<BaseResponse<IncomeBean>> getIncomeBean(){
        return SourceFactory.create().getIncomeBean();
    }

    public Observable<BaseResponse<String>> generateRechargeOrder(String amount){
        return SourceFactory.create().generateRechargeOrder(amount);
    }
    public Observable<BaseResponse<String>> generateRechargeWechat(String amount){
        return SourceFactory.create().generateRechargeWechat(amount);
    }

}
