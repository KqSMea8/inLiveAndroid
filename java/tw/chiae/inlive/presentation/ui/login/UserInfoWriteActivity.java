package tw.chiae.inlive.presentation.ui.login;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jakewharton.rxbinding.view.RxView;
import tw.chiae.inlive.R;
import tw.chiae.inlive.data.bean.me.UserInfo;
import tw.chiae.inlive.presentation.ui.base.BaseActivity;
import tw.chiae.inlive.presentation.ui.main.MainActivity;
import tw.chiae.inlive.presentation.ui.main.me.profile.EditAvatarActivity;
import tw.chiae.inlive.util.Const;
import tw.chiae.inlive.util.FrescoUtil;

import java.util.concurrent.TimeUnit;

import rx.functions.Action1;

public class UserInfoWriteActivity extends BaseActivity implements UserInfoWriteInterface {

    private UserInfoWritePresenter presenter;

    private SimpleDraweeView mPhoto;
    private EditText mNickName;
    private RadioGroup mGroup;
    private Button mCommit;
    private final static int CODE_EDIT_AVATAR = 110;
    private Uri mAvatarUri;

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, UserInfoWriteActivity.class);
        return intent;
    }

    @Override
    protected void init() {
        setSwipeBackEnable(false);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_user_info_write;
    }

    @Override
    protected void findViews(Bundle savedInstanceState) {
        presenter = new UserInfoWritePresenter(this);
        mPhoto = (SimpleDraweeView) findViewById(R.id.userinfo_write_photo);
        mNickName = (EditText) findViewById(R.id.userinfo_write_nickname);
        mGroup = (RadioGroup) findViewById(R.id.userinfo_write_sex);
        mCommit = (Button) findViewById(R.id.userinfo_write_btn);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        RxView.clicks(mCommit)
                .throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        performSubmit();
                    }
                });
        RxView.clicks(mPhoto)
                .throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        setPhoto();
                    }
                });
    }

    private void setPhoto() {
        startActivityForResult(EditAvatarActivity.createIntent(this, mAvatarUri),
                CODE_EDIT_AVATAR);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.unsubscribeTasks();
    }

    private void performSubmit() {
        if (TextUtils.isEmpty(mNickName.getText())) {
            toastShort(R.string.nickname_edit_tip);
            return;
        }
        int gender;
        if (mGroup.getCheckedRadioButtonId() == R.id.userinfo_write_boy) {
            gender = UserInfo.GENDER_MALE;
        } else {
            gender = UserInfo.GENDER_FEMALE;
        }
        presenter.fixProfile(mNickName.getText().toString(), gender,0);
    }

    @Override
    public void onProfileWriteSuccess() {
        startActivity(MainActivity.createIntent(this));
        sendFinishBroadcast(MainActivity.class.getSimpleName());
    }

    @Override
    public void onProfileChangeSuccess() {

    }

    @Override
    public void saveNickNameSuccess() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null || data.getData() == null) {
            return;
        }
        if (resultCode == RESULT_OK && requestCode == CODE_EDIT_AVATAR) {
            toastShort(R.string.avatar_upload_success);
            mAvatarUri = data.getData();
            FrescoUtil.frescoResize(mAvatarUri,
                    (int) getResources().getDimension(R.dimen.user_photo_size),
                    (int) getResources().getDimension(R.dimen.user_photo_size),
                    mPhoto
            );
        }
    }

    @Override
    public void onBackPressed() {
        //屏蔽后退键
//        super.onBackPressed();
    }

    @Override
    public void setCoinData(int currencyItemSum) {

    }
}
