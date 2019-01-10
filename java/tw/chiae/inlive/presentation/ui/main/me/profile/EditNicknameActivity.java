package tw.chiae.inlive.presentation.ui.main.me.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
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
import tw.chiae.inlive.presentation.ui.base.BaseActivity;
import tw.chiae.inlive.presentation.ui.main.mergefilm.Log;
import tw.chiae.inlive.util.Const;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import rx.functions.Action1;
import rx.functions.Func1;
import tw.chiae.inlive.util.EmojiFilter;
import tw.chiae.inlive.util.UnicodeUtil;

/**
 * @author Muyangmin
 * @since 1.0.0
 * 修改昵称
 */
public class EditNicknameActivity extends BaseActivity implements ProfileEditInterface {

    private static final String EXTRA_NICKNAME = "nickname";

    private String mNickname;
    private int mLengthLimit;

    private EditText edtContent;
    private ImageButton imgbtnClearInput;
    private TextView tvSave, tvLimitLabel;
    private ProfilePresenter presenter;
    private int mLen;

    public static Intent createIntent(Context context, String nickname){
        Intent intent = new Intent(context, EditNicknameActivity.class);
        intent.putExtra(EXTRA_NICKNAME, nickname);
        return intent;
    }

    @Override
    protected void parseIntentData(Intent intent, boolean isFromNewIntent) {
        super.parseIntentData(intent, isFromNewIntent);
        mNickname = intent.getStringExtra(EXTRA_NICKNAME);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_nickname;
    }

    @Override
    protected void findViews(Bundle savedInstanceState) {
        edtContent = $(R.id.edit_nickname_edt);
        imgbtnClearInput = $(R.id.edit_nickname_imgbtn_clear_input);
        tvSave = $(R.id.tv_toolbar_right);
        tvLimitLabel = $(R.id.edit_nickname_tv_limit_label);
    }


    @Override
    protected void init() {
        presenter = new ProfilePresenter(this);

        if (!TextUtils.isEmpty(mNickname)){
            edtContent.setText(mNickname);
            edtContent.setSelection(mNickname.length());
        }
        mLengthLimit = getResources().getInteger(R.integer.nickname_length_limit) ;
        tvLimitLabel.setText(getString(R.string.edit_nickname_limit, mLengthLimit));
        EmojiFilter emojiFilter = new EmojiFilter();
        emojiFilter.setEmojiCallback(new EmojiFilter.emojiCallback() {
            @Override
            public void FilterEmojiError() {
                toastShort("不支援字符");
            }
        });
        edtContent.setFilters(new InputFilter[]{emojiFilter});
        edtContent.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i("RayTest","length:"+s.length());
                if (!TextUtils.isEmpty(s)) {
                    imgbtnClearInput.setVisibility(View.VISIBLE);
                } else {
                    imgbtnClearInput.setVisibility(View.INVISIBLE);
                }
                mLen = s.length();
                if (s.length()>mLengthLimit){
                    toastShort("輸入的字數已達上限");
                    edtContent.setText(s.subSequence(0,mLengthLimit));
                    edtContent.setSelection(mLengthLimit);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

                /*if (getlength(edtContent.getText().toString())>=mLengthLimit){
                    toastShort("輸入的字數已達上限");
                }*/
            }
        });

        subscribeClick(imgbtnClearInput, new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                edtContent.setText("");
            }
        });

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
                        String nickName=charSequence.toString().trim();
                        if (TextUtils.isEmpty(nickName)){
                            toastShort(getString(R.string.edit_notnull));
                            return false;
                        }
                        Log.i("RayTest","old len :"+getlength(charSequence.toString()) +" new len"+mLen);
                        if (mLen > mLengthLimit){
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
                        String nickname = charSequence.toString().trim();
                        //完全相同则无需提交
                        if (nickname.equals(mNickname)){
                            finish();
                        }
                        //否则提交服务器处理
                        else{
                            presenter.updateNickname(UnicodeUtil.StringUtfEncode(nickname));
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
    public void showProfileUpdated(String nickname, String tips) {
        toastShort(tips);
        Intent intent = new Intent();
        intent.putExtra(EditProfileActivity.RESULT_NICKNAME, nickname);
        setResult(RESULT_OK, intent);
        finish();
    }

    public static boolean isLetter(char c) {
        int k = 0x80;
        return c / k == 0 ? true : false;
    }

    /**
     * 得到一个字符串的长度,显示的长度,一个汉字或日韩文长度为2,英文字符长度为1
     * @param s 需要得到长度的字符串
     * @return int 得到的字符串长度
     */
    public static int getlength(String s) {
        if (s == null)
            return 0;
        char[] c = s.toCharArray();
        int len = 0;
        for (int i = 0; i < c.length; i++) {
            len++;
            if (!isLetter(c[i])) {
                len++;
            }
        }
        return len;
    }

    @Override
    public void setCoinData(int currencyItemSum) {

    }
}
