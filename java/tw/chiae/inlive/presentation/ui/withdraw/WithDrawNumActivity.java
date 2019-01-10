package tw.chiae.inlive.presentation.ui.withdraw;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jakewharton.rxbinding.view.RxView;
import tw.chiae.inlive.R;
import tw.chiae.inlive.data.bean.IncomeBean;
import tw.chiae.inlive.data.bean.transaction.WithDrawRespose;
import tw.chiae.inlive.data.repository.SourceFactory;
import tw.chiae.inlive.presentation.ui.base.BaseActivity;
import tw.chiae.inlive.util.Const;
import tw.chiae.inlive.util.FrescoUtil;

import java.util.concurrent.TimeUnit;

import rx.functions.Action1;
import rx.functions.Func1;

public class WithDrawNumActivity extends BaseActivity implements IwithDrawNum {
    private static final String KEY_AVATAR = "avater";
    private static final String EXTRA_INCOME_BEAN = "bean";

    private WithDrawPresenter mPresenter;

    private SimpleDraweeView mUserPortrait;
    private EditText mWithDrawMoney;
    private Button mCommit;
    private EditText edtAccount;
    private String mAvatar;
    private IncomeBean mIncomeBean;
    private Dialog mProgressDialog;

    public static Intent createIntent(Context context, String avatar,
                                      @NonNull IncomeBean bean) {
        Intent intent = new Intent(context, WithDrawNumActivity.class);
        intent.putExtra(KEY_AVATAR, avatar);
        intent.putExtra(EXTRA_INCOME_BEAN, bean);
        return intent;
    }

    @Override
    protected void parseIntentData(Intent intent, boolean isFromNewIntent) {
        super.parseIntentData(intent, isFromNewIntent);
        mAvatar = intent.getStringExtra(KEY_AVATAR);
        mIncomeBean = intent.getParcelableExtra(EXTRA_INCOME_BEAN);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.account_withdraw_cash_confirm;
    }

    @Override
    protected void findViews(Bundle savedInstanceState) {
        edtAccount = $(R.id.withdraw_edt_account);
        mUserPortrait = $(R.id.user_portrait);
        mWithDrawMoney = $(R.id.et_money);
        mCommit = $(R.id.btn_ok);

        mWithDrawMoney.setInputType(InputType.TYPE_CLASS_NUMBER);
    }

    @Override
    protected void init() {
        mPresenter = new WithDrawPresenter(this);
        if (!TextUtils.isEmpty(mIncomeBean.getAlipayname())) {
            edtAccount.setText(mIncomeBean.getAlipayname());
        }
        if (!TextUtils.isEmpty(mAvatar)) {
            FrescoUtil.frescoResize(SourceFactory.wrapPathToUri(getIntent().getStringExtra
                            (KEY_AVATAR)),
                    (int) getResources().getDimension(R.dimen.withdraw_avater_size),
                    (int) getResources().getDimension(R.dimen.withdraw_avater_size),
                    mUserPortrait
            );
        }
        if (!TextUtils.isEmpty(mIncomeBean.getRmb())) {
            mWithDrawMoney.setHint("最多提现"+mIncomeBean.getRmb());
//            mWithDrawMoney.getText().toString().trim();
        }
        RxView.clicks(mCommit).throttleFirst(Const.VIEW_THROTTLE_TIME,
                TimeUnit.MILLISECONDS)
                .filter(new Func1<Void, Boolean>() {
                    @Override
                    public Boolean call(Void aVoid) {
                        String account = edtAccount.getText().toString();
                        if (TextUtils.isEmpty(account)) {
                            toastShort("你还没有填写支付宝帐号");
                            return Boolean.FALSE;
                        }
                        return Boolean.TRUE;
                    }
                })
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        String num = null;
                        num = mWithDrawMoney.getText().toString();
                        mProgressDialog = showLoadingDialog();
                        String account = edtAccount.getText().toString().trim();
                        mPresenter.withDraw(String.valueOf(num), account);
                    }
                });
    }

//    private ProgressDialog mProgressDialog;
//
//    private void showProgressDialog() {
//        if (mProgressDialog == null) {
//            //创建ProgressDialog对象
//            mProgressDialog = new ProgressDialog(this);
//        }
//        //设置进度条风格，风格为圆形，旋转的
//        mProgressDialog.setProgressStyle(
//                ProgressDialog.STYLE_SPINNER);
//        //设置ProgressDialog 提示信息
//        mProgressDialog.setMessage("提现中...");
//        //设置ProgressDialog 标题图标
//        mProgressDialog.setIcon(android.R.drawable.btn_star);
//        //设置ProgressDialog 的进度条是否不明确
//        mProgressDialog.setIndeterminate(false);
//        //设置ProgressDialog 是否可以按退回按键取消
//        mProgressDialog.setCancelable(true);
//        mProgressDialog.show();
//    }

    @Override
    public void showLoadingComplete() {
        super.showLoadingComplete();
        if (mProgressDialog != null && mProgressDialog.isShowing() && !this.isFinishing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void setCoinData(int currencyItemSum) {

    }

    @Override
    public void commitSuccess(WithDrawRespose respose) {
        String num = null;
        if (respose != null) {
            num = mWithDrawMoney.getText().toString().trim() ;
        }
        if (TextUtils.isEmpty(num)) {
            num = "0";
        }

        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            dismissLoadingDialog();
        }
        startActivity(WithDrawSuccessActivity.createIntent(this, num));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.unsubscribeTasks();
    }
}
