package tw.chiae.inlive.presentation.ui.main.me.transaction;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.github.magiepooh.recycleritemdecoration.ItemDecorations;
import com.google.gson.Gson;
import com.jakewharton.rxbinding.view.RxView;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import tw.chiae.inlive.BeautyLiveApplication;
import tw.chiae.inlive.R;
import tw.chiae.inlive.data.WxpayInfo;
import tw.chiae.inlive.data.bean.local.PayChannel;
import tw.chiae.inlive.data.bean.transaction.RechargeMapItem;
import tw.chiae.inlive.data.repository.SourceFactory;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.presentation.ui.base.BaseActivity;
import tw.chiae.inlive.presentation.ui.base.recycler.SimpleRecyclerAdapter;
import tw.chiae.inlive.presentation.ui.base.recycler.SimpleRecyclerHolder;
import tw.chiae.inlive.presentation.ui.main.webview.SimpleWebViewActivity;
import tw.chiae.inlive.util.Const;
import tw.chiae.inlive.util.Event.EventPayment;
import tw.chiae.inlive.util.L;
import tw.chiae.inlive.util.PicUtil;
import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * @author Muyangmin
 * @since 1.0.0
 * 余额充值
 * 我的星钻
 */
public class RechargeActivity extends BaseActivity implements RechargeInterface {

    public static Intent createIntent(Context context){
        return new Intent(context, RechargeActivity.class);
    }

    private RechargePresenter presenter;

    private FrameLayout flAlipay, flWeChat;
    private TextView tvBalance, tvSelectedChannel;
    private RecyclerView recyclerView;
    private String selectedAlipay, selectedWeChat;
    private TextView tvrecharge;
    private IWXAPI msgApi;
    @PayChannel
    private int mSelectedChannel;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_recharge;
    }

    @Override
    protected void findViews(Bundle savedInstanceState) {
        flAlipay = $(R.id.recharge_fl_alipay);
        flWeChat = $(R.id.recharge_fl_wechat);
        tvBalance = $(R.id.recharge_tv_balance);
        tvSelectedChannel = $(R.id.recharge_tv_selected_channel);
        recyclerView = $(R.id.recharge_recycler);
        tvrecharge=$(R.id.recharge_tv_troubleshot);
        tvrecharge.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(ItemDecorations.vertical(this)
                .type(0, R.drawable.divider_decoration_transparent_h1)
                .create());
    }

    @Override
    protected void init() {
        EventBus.getDefault().register(this);
        presenter = new RechargePresenter(this, this);

        msgApi = WXAPIFactory.createWXAPI(this, Const.WX_APPID);
        TextView tvTitle = $(R.id.tv_toolbar_title);
        tvTitle.setText(getString(R.string.title_recharge,getString(R.string.me_title_recharge_zh)));

        double balance = LocalDataManager.getInstance().getLoginInfo().getTotalBalance();
//        tvBalance.setText(String.valueOf(balance));
        showBalance(balance);

        selectedAlipay = getString(R.string
                .recharge_channel_select_result, getString(R.string
                .recharge_channel_alipay));

        selectedWeChat = getString(R.string
                .recharge_channel_select_result, getString(R.string
                .recharge_channel_wechat));

        //获取上次保存的支付方式，如果没有则默认选择支付宝
        @PayChannel int preferredChannel = LocalDataManager.getInstance().getPreferredPayChannel
                (PayChannel.ALIPAY);
        mSelectedChannel = preferredChannel;
        refreshSelectedResult();

        RxView.clicks(flAlipay)
                .throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                .filter(new Func1<Void, Boolean>() {
                    @Override
                    public Boolean call(Void aVoid) {
                        return (!flAlipay.isSelected());
                    }
                })
                .subscribe(new Action1<Void>() {

                    @Override
                    public void call(Void aVoid) {
                        mSelectedChannel = PayChannel.ALIPAY;
                        refreshSelectedResult();
                    }
                });

        RxView.clicks(flWeChat)
                .throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                .filter(new Func1<Void, Boolean>() {
                    @Override
                    public Boolean call(Void aVoid) {
                        return (!flWeChat.isSelected());
                    }
                })
                .subscribe(new Action1<Void>() {

                    @Override
                    public void call(Void aVoid) {
                        mSelectedChannel = PayChannel.WECHAT;
                        refreshSelectedResult();
                    }
                });

        subscribeClick(R.id.recharge_tv_troubleshot, new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                startActivity(SimpleWebViewActivity.createIntent(RechargeActivity.this,
                        SourceFactory.wrapPath(getString(R.string.setting_contact_url)),""));
            }
        });
        presenter.loadRechargeMap();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        presenter.unsubscribeTasks();
    }

    @Override
    public void showBalance(double balance) {
        tvBalance.setText(new java.text.DecimalFormat("#0.00").format(balance) + " ");
        PicUtil.TextViewSpandImg(this, tvBalance, R.drawable.ic_me_myaccount_reddiamond);
    }

    @Override
    public void showRechargeList(List<RechargeMapItem> list) {
        recyclerView.setAdapter(new RechargeMapAdapter(list));
    }

    @Override
    public void showRechargeSuccess() {
        toastShort(getString(R.string.recharge_complete));
    }

    @Override
    public void showRechargeProcessing() {
        toastShort(getString(R.string.recharge_processing));
    }

    @Override
    public void showRechargeFailed(String status, String msg) {
        toastShort(String.format(Locale.US, getString(R.string.recharge_pay_error), status));
    }

    @Override
    public void showPayCancelled() {
        toastShort(getString(R.string.recharge_pay_cancel));
    }

    private void refreshSelectedResult(){
        if (mSelectedChannel == PayChannel.ALIPAY){
            tvSelectedChannel.setText(selectedAlipay);
            flWeChat.setSelected(false);
            flAlipay.setSelected(true);
            LocalDataManager.getInstance().savePreferredPayChannel(mSelectedChannel);
        }
        else if (mSelectedChannel == PayChannel.WECHAT){
            tvSelectedChannel.setText(selectedWeChat);
            flAlipay.setSelected(false);
            flWeChat.setSelected(true);
            LocalDataManager.getInstance().savePreferredPayChannel(mSelectedChannel);
        }
        else{
            L.e(LOG_TAG, "Neither alipay nor wechat is selected, missing selected result?");
        }
    }

    private void performRecharge(String amount){
        if (mSelectedChannel == PayChannel.ALIPAY){
            presenter.performRechargeAlipay(amount);
        }
        else if(mSelectedChannel == PayChannel.WECHAT){
            showLoadingDialog();
            getDatas(amount);
        }
    }

    @Override
    public void setCoinData(int currencyItemSum) {

    }

    private class RechargeMapAdapter extends SimpleRecyclerAdapter<RechargeMapItem,
            RechargeMapHolder>{
        public RechargeMapAdapter(List<RechargeMapItem> list) {
            super(list);
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            return R.layout.item_recharge;
        }

        @NonNull
        @Override
        protected RechargeMapHolder createHolder(View view) {
            return new RechargeMapHolder(view);
        }
    }

    private class RechargeMapHolder extends SimpleRecyclerHolder<RechargeMapItem> {

        private TextView tvCurrency, tvRmb, tvTips;

        public RechargeMapHolder(View itemView) {
            super(itemView);
            tvCurrency = $(itemView, R.id.item_recharge_tv_coin_amount);
            tvRmb = $(itemView, R.id.item_recharge_tv_rmb_amount);
            tvTips = $(itemView, R.id.item_recharge_tv_tips);
        }

        @Override
        public void displayData(final RechargeMapItem data) {
            tvCurrency.setText(data.getCurrencyAmount());
            tvRmb.setText(getString(R.string.recharge_rmb_amount, data.getRmbAmount()));
            if (!TextUtils.isEmpty(data.getMsg())){
                tvTips.setText(data.getMsg());
                tvTips.setVisibility(View.VISIBLE);
            }
            else{
                tvTips.setVisibility(View.GONE);
            }
            subscribeClick(itemView, new Action1<Void>() {
                @Override
                public void call(Void aVoid) {
                    performRecharge(data.getRmbAmount());
                    //Stub!
//                    toastShort("充值成功");
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    public void onEvent(EventPayment event) {
        if (event.getResultStatus() == Const.PAY_RESULT_STATUS_SUCCESS) {
            showRechargeSuccess();
            presenter.loadRechargeMap();
        }
    }
    private int DATA_JSON=1;
    public void getDatas(String amount) {
        Request<JSONObject> request = NoHttp.createJsonObjectRequest(Const.WEB_BASE_URL + "payment/appWeixin", RequestMethod.GET);
        request.add("token", LocalDataManager.getInstance().getLoginInfo().getToken());
        request.add("num", amount);
        BeautyLiveApplication.getRequestQueue().add(DATA_JSON, request, OnResponse);
    }

    private OnResponseListener<JSONObject> OnResponse = new OnResponseListener<JSONObject>() {
        @Override
        public void onStart(int i) {
        }

        @Override
        public void onSucceed(int i, Response<JSONObject> response) {
            if (i == DATA_JSON) {// 判断what是否是刚才指定的请求
                // 请求成功
                JSONObject result = response.get();// 响应结果
                try {
                    JSONObject json=result.getJSONObject("data");
//                    JSONObject code = result.getJSONObject("code");
//                    L.d("lw","weixin"+code.getInt("code"));
//                    if (code.getInt("code") == 0) {
                        WxpayInfo wxpayInfo=new Gson().fromJson(json.toString(), WxpayInfo.class);
                        PayReq req = new PayReq();
                        req.appId= wxpayInfo.getAppid();
                        req.partnerId= wxpayInfo.getPartnerid();
                        req.prepayId= wxpayInfo.getPrepayid();
                        req.nonceStr= wxpayInfo.getNoncestr();
                        req.timeStamp= wxpayInfo.getTimestamp();
                        req.packageValue= wxpayInfo.getPackagee();
                        req.sign= wxpayInfo.getSign();
                        msgApi.registerApp(Const.WX_APPID);
                        msgApi.sendReq(req);
//                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    dismissLoadingDialog();
                }
                // 响应头
                Headers headers = response.getHeaders();
                headers.getResponseCode();// 响应码
                response.getNetworkMillis();// 请求花费的时间
            }
        }

        @Override
        public void onFailed(int i, String s, Object o, Exception e, int i1, long l) {
            dismissLoadingDialog();
        }

        @Override
        public void onFinish(int what) {

        }

    };
}
