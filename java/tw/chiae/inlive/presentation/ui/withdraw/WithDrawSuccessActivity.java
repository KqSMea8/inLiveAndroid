package tw.chiae.inlive.presentation.ui.withdraw;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import tw.chiae.inlive.R;
import tw.chiae.inlive.presentation.ui.base.BaseActivity;
import tw.chiae.inlive.util.Const;

import java.util.concurrent.TimeUnit;

import rx.functions.Action1;

public class WithDrawSuccessActivity extends BaseActivity {
    private static final String KEY_MONEY = "money";
   private TextView mUserNameTv;
    private TextView mMoneyTv;
    private String mUserName,mMoney;
    private Button mBtn;

    public static Intent createIntent(Context context,String money) {
        Intent intent = new Intent(context, WithDrawSuccessActivity.class);
        intent.putExtra(KEY_MONEY,money);
        return intent;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.account_withdraw_cash_success;
    }

    @Override
    protected void findViews(Bundle savedInstanceState) {
        mUserNameTv = $(R.id.txt_widthdraw_account);
        mMoneyTv = $(R.id.txt_widthdraw_money);
        mBtn = $(R.id.btn_ok);
    }

    @Override
    protected void init() {
        mMoney = getIntent().getStringExtra(KEY_MONEY);
        mMoneyTv.setText(getString(R.string.charge_withdraw_cash_money,mMoney));
        RxView.clicks(mBtn).throttleFirst(Const.VIEW_THROTTLE_TIME,
                TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        finish();
                    }
                });
    }

    @Override
    public void setCoinData(int currencyItemSum) {

    }
}
