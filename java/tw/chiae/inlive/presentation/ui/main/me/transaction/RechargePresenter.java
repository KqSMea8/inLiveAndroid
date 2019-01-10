package tw.chiae.inlive.presentation.ui.main.me.transaction;

import android.app.Activity;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import tw.chiae.inlive.data.bean.BaseResponse;
import tw.chiae.inlive.data.bean.LoginInfo;
import tw.chiae.inlive.data.bean.transaction.PayResult;
import tw.chiae.inlive.data.bean.transaction.RechargeInfo;
import tw.chiae.inlive.data.WxpayInfo;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.domain.TransactionManager;
import tw.chiae.inlive.presentation.ui.base.BaseObserver;
import tw.chiae.inlive.presentation.ui.base.BasePresenter;
import tw.chiae.inlive.util.Const;
import tw.chiae.inlive.util.L;

import java.util.Map;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public class RechargePresenter extends BasePresenter<RechargeInterface> {

    private TransactionManager tm;
    private Activity activity;
    private IWXAPI msgApi;

    protected final String LOG_TAG = getClass().getSimpleName();

    /**
     *
     * @param activity to create third pay task.
     */
    public RechargePresenter(RechargeInterface uiInterface, Activity activity) {
        super(uiInterface);
        tm = new TransactionManager();
        this.activity = activity;
        msgApi = WXAPIFactory.createWXAPI(activity, Const.WX_APPID);
    }

    public void loadRechargeMap(){
        getUiInterface().showLoadingDialog();
        Subscription subscription = tm.getRechargeMap()
                .compose(this.<BaseResponse<RechargeInfo>>applyAsySchedulers())
                .subscribe(new BaseObserver<BaseResponse<RechargeInfo>>(getUiInterface()) {

                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                        getUiInterface().dismissLoadingDialog();
                    }

                    @Override
                    public void onSuccess(BaseResponse<RechargeInfo> response) {
                        getUiInterface().dismissLoadingDialog();
                        double balance = response.getData().getCoinBalance();
                        getUiInterface().showBalance(balance);
                        getUiInterface().showRechargeList(response.getData().getList());
                        //刷新余额
                        LoginInfo info = LocalDataManager.getInstance().getLoginInfo();
                        info.setTotalBalance(balance);
                        LocalDataManager.getInstance().saveLoginInfo(info);
                    }
                });
        addSubscription(subscription);
    }

    //    public void performRechargeWechat(String amount){
//        getUiInterface().showLoadingDialog();
//        Subscription subscription = tm.generateRechargeWechat(amount)
//                .compose(this.<BaseResponse<WxpayInfo>>applyAsySchedulers())
//                .subscribe(new BaseObserver<BaseResponse<WxpayInfo>>(getUiInterface()) {
//
//                    @Override
//                    public void onError(Throwable throwable) {
//                        super.onError(throwable);
//                        getUiInterface().dismissLoadingDialog();
//                        BaseObserver.handleError(throwable, getUiInterface(), LOG_TAG);
//                    }
//
//                    @Override
//                    public void onSuccess(BaseResponse<WxpayInfo> response) {
//                        getUiInterface().dismissLoadingDialog();
//                        if(response.getCode()==0){
//                            PayReq req = new PayReq();
//                            req.appId= response.getData().getAppid();
//                            req.partnerId= response.getData().getPartnerid();
//                            req.prepayId= response.getData().getPrepayid();
//                            req.nonceStr= response.getData().getNoncestr();
//                            req.timeStamp= response.getData().getTimestamp();
//                            req.packageValue= response.getData().getPackagee();
//                            req.sign= response.getData().getSign();
//                            msgApi.registerApp(Const.WX_APPID);
//                            msgApi.sendReq(req);
//                        }
//                    }
//                });
//        addSubscription(subscription);
//    }
    public void performRechargeAlipay(String amount){
        getUiInterface().showLoadingDialog();
        Subscription subscription = tm.generateRechargeOrder(amount)
//                .compose(this.<BaseResponse<String>>applyAsySchedulers())
                //Map to pay result!
                .map(new Func1<BaseResponse<String>, PayResult>() {
                    @Override
                    public PayResult call(BaseResponse<String> response) {
                        String order = response.getData();
                        L.d(LOG_TAG, "original data %s", order);
//                        String decoded = Uri.decode(order);
//                        L.d(LOG_TAG, "Decoded string is %s", decoded);
                        PayTask payTask = new PayTask(activity);
                        L.i(LOG_TAG, "Alipay Sdk version: %s", payTask.getVersion());
                        Map<String, String> result = payTask.payV2(order, true);
                        if (result!=null){
                            L.e(LOG_TAG, "Pay sdk returns empty string: %s!", result);
                        }
                        else {
                            L.i(LOG_TAG, "Pay result:%s", result);
                        }
                        return new PayResult((Map<String, String>)result);
                    }
                })
                .compose(this.<PayResult>applyAsySchedulers())
                .subscribe(new Observer<PayResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getUiInterface().dismissLoadingDialog();
                        L.e(LOG_TAG, "Error performing recharge!", e);
                        BaseObserver.handleError(e, getUiInterface(), LOG_TAG);
                    }

                    @Override
                    public void onNext(PayResult result) {
                        getUiInterface().dismissLoadingDialog();
                        /**
                         * See https://doc.open.alipay.com/doc2/detail.htm?spm=a219a.7386797.0.0.Va2hGu&treeId=59&articleId=103671&docType=1
                         */
                        switch (result.getResultStatus()){
                            // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                            case "9000":
                                getUiInterface().showRechargeSuccess();
                                //刷新列表和余额
                                loadRechargeMap();
                                break;

                            // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                            case "8000":
                                getUiInterface().showRechargeProcessing();
                                //刷新列表和余额
                                loadRechargeMap();
                                break;

                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            case "6001":
                                getUiInterface().showPayCancelled();
                                break;

                            case "6002":
                                getUiInterface().showNetworkException();
                                break;
                            default:
                                getUiInterface().showRechargeFailed(result.getResultStatus(),
                                        result.getMemo());
                                break;
                        }
                    }
                });
        addSubscription(subscription);
    }

}
