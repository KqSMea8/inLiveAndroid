package tw.chiae.inlive.presentation.ui.login;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import com.jakewharton.rxbinding.view.RxView;
import tw.chiae.inlive.R;
import tw.chiae.inlive.presentation.ui.base.BaseActivity;
import tw.chiae.inlive.util.Const;

import java.util.concurrent.TimeUnit;

import rx.functions.Action1;

public class RegisterActivity extends BaseActivity implements TextWatcher, RegisterUiInterface {

    private RegisterPresenter presenter;

    private EditText mName ,mPass,mCpass;
    private Button mCommit;
    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, RegisterActivity.class);
        return intent;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    protected void findViews(Bundle savedInstanceState) {
        presenter = new RegisterPresenter(this);
        mName = (EditText)findViewById(R.id.register_name);
        mPass = (EditText)findViewById(R.id.register_pass);
        mCpass = (EditText)findViewById(R.id.register_commit_pass);
        mCommit = (Button)findViewById(R.id.register_btn);
        checkEdit();
    }
    @Override
    protected void init() {
        setSwipeBackEnable(false);
    }
    @Override
    protected void setListeners() {
        super.setListeners();
        mName.addTextChangedListener(this);
        mPass.addTextChangedListener(this);
        mCpass.addTextChangedListener(this);
        RxView.clicks(mCommit).throttleFirst(Const.VIEW_THROTTLE_TIME,
                TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                String username = mName.getText().toString().trim();
                String password = mPass.getText().toString().trim();
                presenter.performLogin(username, password);
            }
        });
    }

    @Override
    public void gotoMain() {
        startActivity(UserInfoWriteActivity.createIntent(RegisterActivity.this));
        finish();
    }

    private void checkEdit(){
        if(!TextUtils.isEmpty(mName.getText())&&!TextUtils.isEmpty(mPass.getText())&&!TextUtils.isEmpty(mCpass.getText())){
            if(mPass.getText().toString().equals(mCpass.getText().toString())) {
                mCommit.setEnabled(true);
            }else{
                mCommit.setEnabled(false);
            }
        }else{
            mCommit.setEnabled(false);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        checkEdit();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void setCoinData(int currencyItemSum) {

    }
}
