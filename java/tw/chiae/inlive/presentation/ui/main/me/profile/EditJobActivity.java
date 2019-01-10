package tw.chiae.inlive.presentation.ui.main.me.profile;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import tw.chiae.inlive.R;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.presentation.ui.base.BaseActivity;
import tw.chiae.inlive.util.Const;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import rx.functions.Action1;
import rx.functions.Func1;

public class EditJobActivity extends BaseActivity implements ProfileEditInterface {

    private static final String EXTRA_PROFESSIONAL = "professional";
    private String mProfessional;

    private EditText etText;
    private ImageButton imClear;
    private TextView tvLimitLabel,tvSave;
    private int mLengthLimit;

    private ProfilePresenter presenter;

    public static Intent createIntent(Context context, String professional) {
        Intent intent = new Intent(context, EditJobActivity.class);
        intent.putExtra(EXTRA_PROFESSIONAL, professional);
        return intent;
    }

    @Override
    protected void parseIntentData(Intent intent, boolean isFromNewIntent) {
        super.parseIntentData(intent, isFromNewIntent);
        mProfessional= intent.getStringExtra(EXTRA_PROFESSIONAL);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_job;
    }

    @Override
    protected void findViews(Bundle savedInstanceState) {
        etText = $(R.id.edit_job_edt);
        imClear = $(R.id.edit_job_imgbtn_clear_input);
        tvLimitLabel = $(R.id.edit_job_tv_limit_label);
        tvSave = $(R.id.tv_toolbar_right);
    }

    @Override
    protected void init() {
        presenter = new ProfilePresenter(this);

        if (!TextUtils.isEmpty(mProfessional)){
            etText.setText(mProfessional);
            etText.setSelection(mProfessional.length());
        }
        mLengthLimit = getResources().getInteger(R.integer.professional_length_limit);
        tvLimitLabel.setText(getString(R.string.edit_nickname_limit, mLengthLimit));

        etText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    imClear.setVisibility(View.VISIBLE);
                } else {
                    imClear.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etText.getText().toString().length() >= 20) {
                    toastShort("輸入的字數達到最大");
                }
            }
        });
        subscribeClick(imClear, new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                etText.setText("");
            }
        });

        etText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    tvSave.performClick();
                    return true;
                }
                return false;
            }
        });
        RxView.clicks(tvSave)
                .map(new Func1<Void, CharSequence>() {
                    @Override
                    public CharSequence call(Void aVoid) {
                        return etText.getText();
                    }
                })
                .filter(new Func1<CharSequence, Boolean>() {
                    @Override
                    public Boolean call(CharSequence charSequence) {
                        if (TextUtils.isEmpty(charSequence)){
                            toastShort(getString(R.string.edit_notnull));
                            return false;
                        }
                        if (charSequence.length() > mLengthLimit){
                            toastShort(String.format(Locale.CHINA, getString(R.string.edit_max), mLengthLimit));
                            return false;
                        }
                        return true;
                    }
                })
                .throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MICROSECONDS)
                .subscribe(new Action1<CharSequence>() {
                    @Override
                    public void call(CharSequence charSequence) {
                        String professional = charSequence.toString();
                        //完全相同则无需提交
                        if (professional.equals(mProfessional)){
                            finish();
                        }
                        //否则提交服务器处理
                        else{
                            presenter.updateJob(LocalDataManager.getInstance().getLoginInfo().getToken(),charSequence.toString());
                        }
                    }
                });
    }

    @Override
    public void showProfileUpdated(String professional, String tips) {
        toastShort(tips);
        Intent intent = new Intent();
        intent.putExtra(EditProfileActivity.RESULT_PROFESSIONAL, professional);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.unsubscribeTasks();
    }

    @Override
    public void setCoinData(int currencyItemSum) {

    }
}
