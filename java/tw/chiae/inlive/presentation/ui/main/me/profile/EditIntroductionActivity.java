package tw.chiae.inlive.presentation.ui.main.me.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import tw.chiae.inlive.R;
import tw.chiae.inlive.presentation.ui.base.BaseActivity;
import tw.chiae.inlive.presentation.ui.main.mergefilm.Log;
import tw.chiae.inlive.util.Const;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import rx.functions.Action1;
import rx.functions.Func1;

/**
 * @author Muyangmin
 * @since 1.0.0
 * 修改签名
 */
public class EditIntroductionActivity extends BaseActivity implements ProfileEditInterface {

    private static final String EXTRA_INTRO = "intro";

    private String mIntroduction;
    private int mLengthLimit;

    private EditText edtContent;
    //    private ImageButton imgbtnClearInput;
    private TextView tvSave, tvLimitLabel, tvLimitHint;
    private ProfilePresenter presenter;

    public static Intent createIntent(Context context, String intro) {
        Intent intent = new Intent(context, EditIntroductionActivity.class);
        intent.putExtra(EXTRA_INTRO, intro);
        return intent;
    }

    @Override
    protected void parseIntentData(Intent intent, boolean isFromNewIntent) {
        super.parseIntentData(intent, isFromNewIntent);
        mIntroduction = intent.getStringExtra(EXTRA_INTRO);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_introduction;
    }

    @Override
    protected void findViews(Bundle savedInstanceState) {
        edtContent = $(R.id.edit_intro_edt);
//        imgbtnClearInput = $(R.id.edit_intro_imgbtn_clear_input);
        tvSave = $(R.id.tv_toolbar_right);
        tvLimitLabel = $(R.id.edit_intro_tv_limit_label);
        tvLimitHint = $(R.id.edit_intro_tv_length_hint);
    }

    @Override
    protected void init() {
        presenter = new ProfilePresenter(this);

        mLengthLimit = getResources().getInteger(R.integer.introduction_length_limit);
        tvLimitLabel.setText(getString(R.string.edit_intro_limit, mLengthLimit));

        tvLimitHint.setText(String.valueOf(mLengthLimit));
        if (!TextUtils.isEmpty(mIntroduction)) {
            edtContent.setText(mIntroduction);
            edtContent.setSelection(mIntroduction.length());
            tvLimitHint.setText(String.valueOf(mLengthLimit - mIntroduction.length()));
        }

        edtContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = 0;
                if (!TextUtils.isEmpty(s)) {
                    length = s.length();
                }
                tvLimitHint.setText(String.valueOf(mLengthLimit - length));
//                if (!TextUtils.isEmpty(s)) {
//                    imgbtnClearInput.setVisibility(View.VISIBLE);
//                } else {
//                    imgbtnClearInput.setVisibility(View.INVISIBLE);
//                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
//
//        subscribeClick(imgbtnClearInput, new Action1<Void>() {
//            @Override
//            public void call(Void aVoid) {
//                edtContent.setText("");
//            }
//        });

        edtContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
                        return edtContent.getText();
                    }
                })
                .filter(new Func1<CharSequence, Boolean>() {
                    @Override
                    public Boolean call(CharSequence charSequence) {
                        if (TextUtils.isEmpty(charSequence)) {
                            toastShort(getString(R.string.edit_notnull));
                            return false;
                        }
                        if (charSequence.length() > mLengthLimit) {
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
                        String intro = charSequence.toString();
                        //完全相同则无需提交
                        if (intro.equals(mIntroduction)) {
                            finish();
                        }
                        //否则提交服务器处理
                        else {
                            presenter.updateIntroduction(charSequence.toString());
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.unsubscribeTasks();
    }


    @Override
    public void showProfileUpdated(String intro, String tips) {
        toastShort(tips);
        Intent intent = new Intent();
        intent.putExtra(EditProfileActivity.RESULT_INTRODUCTION, intro);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void setCoinData(int currencyItemSum) {

    }
}
