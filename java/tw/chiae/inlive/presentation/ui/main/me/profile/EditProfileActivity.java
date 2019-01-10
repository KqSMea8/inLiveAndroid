package tw.chiae.inlive.presentation.ui.main.me.profile;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.jakewharton.rxbinding.view.RxView;

import tw.chiae.inlive.R;
import tw.chiae.inlive.data.bean.me.UserInfo;
import tw.chiae.inlive.data.repository.SourceFactory;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.presentation.ui.base.BaseActivity;
import tw.chiae.inlive.presentation.ui.login.UserInfoWriteInterface;
import tw.chiae.inlive.presentation.ui.login.UserInfoWritePresenter;
import tw.chiae.inlive.presentation.ui.main.me.popup.time.adapter.ArrayWheelAdapter;
import tw.chiae.inlive.presentation.ui.main.me.popup.time.adapter.OnWheelChangedListener;
import tw.chiae.inlive.presentation.ui.main.me.popup.time.adapter.WheelView;
import tw.chiae.inlive.presentation.ui.main.setting.CreateViewDialogFragment;
import tw.chiae.inlive.presentation.ui.main.webview.SimpleWebViewActivity;
import tw.chiae.inlive.presentation.ui.widget.AdaptiveTextView;
import tw.chiae.inlive.util.Const;
import tw.chiae.inlive.util.DateUtilsl;
import tw.chiae.inlive.util.FrescoUtil;
import tw.chiae.inlive.util.L;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import rx.functions.Action1;
import tw.chiae.inlive.util.PermissionsChecker;
import tw.chiae.inlive.util.UnicodeUtil;

/**
 * @author Muyangmin
 * @since 1.0.0
 * 修改资料
 */
public class EditProfileActivity extends BaseActivity implements
        UserInfoWriteInterface, tw.chiae.inlive.presentation.ui.main.me.popup.city.widget.OnWheelChangedListener {

    private UserInfoWritePresenter presenter;
    public static final String RESULT_NICKNAME = "nickname";
    private static final int CODE_EDIT_NICKNAME = 0x1001;

    public static final String RESULT_INTRODUCTION = "intro";
    private static final int CODE_EDIT_INTRODUCTION = 0x1002;

    public static final String RESULT_PROFESSIONAL = "professional";
    private static final int CODE_EDIT_PROFESSIONAL = 0x1004;

    public static final String RESULT_AVATAR = "avatar";
    private static final int CODE_EDIT_AVATAR = 0x1003;

    private static final String EXTRA_USER_INFO = "info";
    private RadioGroup mGroupsex;

    private UserInfo mUserInfo;
    private PopupWindow popAge, popFeel, home, timePickerPopupWindow;
    private RelativeLayout rlInform, hometome;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private RadioButton rbBoy, rbGril;
    private tw.chiae.inlive.presentation.ui.main.me.popup.city.widget.WheelView mViewProvince, mViewCity, mViewDistrict;
    private AdaptiveTextView tvJob;
    private TextView tvExit, tvShow, tvIdLabel, tvIdValue, tvNickName, tvIntroduction, mCommit, constellation, age, tvFeel, tvAge, tvE;
    private TextView tvHome;
    private SimpleDraweeView draweeAvatar;
    private WheelView year, month, day;
    private ArrayList<String> yData = new ArrayList<>();
    private ArrayList<String> mData = new ArrayList<>();
    private ArrayList<String> dData = new ArrayList<>();
    private int year_index, day_index, month_index;
    private String time;
    private int flag = 0;
    private ArrayWheelAdapter<Object> dayAdapter;
    private String isUpdateProfile = "";
    private PermissionsChecker mPermissionsChecker;
    private CreateViewDialogFragment dialogFragment;


    public static Intent createIntent(@NonNull Context context, @NonNull UserInfo info) {
        Intent intent = new Intent(context, EditProfileActivity.class);
        intent.putExtra(EXTRA_USER_INFO, info);
        return intent;
    }

    @Override
    protected void parseIntentData(Intent intent, boolean isFromNewIntent) {
        super.parseIntentData(intent, isFromNewIntent);
        mUserInfo = intent.getParcelableExtra(EXTRA_USER_INFO);
        if (mUserInfo == null) {
            toastShort(getString(R.string.edit_profile_erroruser));
            finish();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_profile;
    }

    @Override
    protected void findViews(Bundle savedInstanceState) {

        presenter = new UserInfoWritePresenter(this);
        tvIdLabel = $(R.id.edit_profile_tv_id_label);
        tvIdValue = $(R.id.edit_profile_tv_id_value);
        tvNickName = $(R.id.edit_profile_tv_nickname);
        tvJob = $(R.id.edit_profile_tv_job);
        rbBoy = $(R.id.userinfo_write_boy);
        rbGril = $(R.id.userinfo_write_gril);
        mCommit = $(R.id.edit_sex_btn);
        mGroupsex = (RadioGroup) findViewById(R.id.edit_profile_gp_sex);
        tvIntroduction = $(R.id.edit_profile_tv_introduction);
        draweeAvatar = $(R.id.edit_profile_drawee_avatar);
        tvAge = $(R.id.edit_profile_tv_age);
        tvFeel = $(R.id.edit_profile_tv_state);
        rlInform = $(R.id.edit_profile_rl_inform);

        hometome = $(R.id.edit_profile_rl_hometown);
        tvHome = $(R.id.edit_profile_tv_hometown);

//        popAge= new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);


//        subscribeFeatureStub(R.id.edit_profile_rl_avatar);

        subscribeClick(R.id.edit_profile_rl_avatar, new Action1<Void>() {
            @Override
            public void call(Void aVoid) {

                mPermissionsChecker = new PermissionsChecker(getApplicationContext());
                boolean camera_permission = mPermissionsChecker.lacksPermission(Manifest.permission.CAMERA);
                boolean camera_photo_read = mPermissionsChecker.lacksPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
                boolean camera_photo_write = mPermissionsChecker.lacksPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if(camera_permission && camera_photo_read && camera_photo_write ){
                    startActivityForResult(EditAvatarActivity.createIntent(EditProfileActivity.this,
                            mUserInfo.getSnap()), CODE_EDIT_AVATAR);
                }else {
                    if (dialogFragment == null) {
                        dialogFragment = CreateViewDialogFragment.newInstance();
                    }
                    dialogFragment.setDialogCallback(new CreateViewDialogFragment.dialogCallback() {
                        @Override
                        public void onOKDialogcheck(Bundle bundle) {
                            dialogFragment.dismiss();

                        }

                        @Override
                        public void onCancelDialogcheck(Bundle mArgs) {

                        }
                    });
                    if (!camera_permission) {
                        dialogFragment.showMsgDialog(getSupportFragmentManager(), "提示", getString(R.string.permissions_error_storage_camera), CreateViewDialogFragment.TYPE_SHOW_ERROR, false);
                        return;
                    }
                    if (!camera_photo_read ||!camera_photo_write ) {
                        dialogFragment.showMsgDialog(getSupportFragmentManager(), "提示", getString(R.string.permissions_error_storage), CreateViewDialogFragment.TYPE_SHOW_ERROR, false);
                        return;
                    }


                }
            }
        });

        subscribeClick(R.id.edit_profile_rl_id, new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                ClipData clipData = ClipData.newPlainText("text", mUserInfo.getId());
                ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                cm.setPrimaryClip(clipData);
                toastShort(R.string.edit_profile_clip_success);
            }
        });

        subscribeClick(R.id.edit_profile_rl_nickname, new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                startActivityForResult(EditNicknameActivity.createIntent(EditProfileActivity.this,
                        mUserInfo.getNickname()), CODE_EDIT_NICKNAME);
            }
        });
        subscribeClick(R.id.edit_profile_rl_introduction, new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                startActivityForResult(EditIntroductionActivity.createIntent(EditProfileActivity
                        .this, mUserInfo.getIntro()), CODE_EDIT_INTRODUCTION);
            }
        });
        subscribeClick(R.id.edit_profile_rl_job, new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                startActivityForResult(EditJobActivity.createIntent(EditProfileActivity
                        .this, mUserInfo.getProfessional()), CODE_EDIT_PROFESSIONAL);
            }
        });

        rlInform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EditProfileActivity.this, EditInformActivity.class);
                startActivity(i);
            }
        });

        subscribeClick(R.id.edit_profile_rl_age, new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                showPopupWindowAge();
            }
        });
        subscribeClick(R.id.edit_profile_rl_state, new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                showPopupWindowFeel();
            }
        });
        subscribeClick(R.id.edit_profile_rl_hometown, new Action1<Void>() {
            @Override
            public void call(Void aVoid) {

                startThemPopupWindow();
//                showPopupWindowHome();
            }
        });


        RxView.clicks(mCommit)
                .throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        performSubmit();
                    }
                });

        subscribeClick(R.id.edit_profile_rl_certification, new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                if (mUserInfo != null) {
                    startActivity(SimpleWebViewActivity.createIntent(EditProfileActivity.this,
                            SourceFactory.wrapPath(getString(R.string.me_authentication_url, mUserInfo.getId())),""));
                }
            }
        });
    }

    private void performSubmit() {
        if (TextUtils.isEmpty(tvNickName.getText())) {
            return;
        }
        int gender;
        if (mGroupsex.getCheckedRadioButtonId() == R.id.userinfo_write_boy) {
            gender = UserInfo.GENDER_MALE;
        } else {
            gender = UserInfo.GENDER_FEMALE;
        }
        String name = String.valueOf(Html.fromHtml(UnicodeUtil.StringUtfEncode(tvNickName.getText().toString())));
        Log.i("RayTest","name save:"+name);
        showLoadingDialog();
        presenter.fixProfile(name, gender,Integer.parseInt(mUserInfo.getId()));

    }

/*
    private void SubmitName() {
        if (TextUtils.isEmpty(tvNickName.getText())) {
            return;
        }
        int gender;
        if (mGroupsex.getCheckedRadioButtonId() == R.id.userinfo_write_boy) {
            gender = UserInfo.GENDER_MALE;
        } else {
            gender = UserInfo.GENDER_FEMALE;
        }
        String name = String.valueOf(Html.fromHtml(UnicodeUtil.StringUtfEncode(tvNickName.getText().toString())));
        Log.i("RayTest","name save:"+name);
        presenter.saveNickName(name, gender);
    }
*/

    @Override
    public void onProfileWriteSuccess() {

        finish();
    }

    @Override
    public void onProfileChangeSuccess() {
        toastShort(getString(R.string.edit_profile_complete));
    }

    @Override
    public void saveNickNameSuccess() {

    }



    @Override
    protected void init() {
        String[] s = getResources().getStringArray(R.array.emotion);

        tvIdLabel.setText(getString(R.string.edit_profile_id));
        if (!TextUtils.isEmpty(mUserInfo.getId())) {
            tvIdValue.setText(mUserInfo.getId());
        }
        if (!TextUtils.isEmpty(mUserInfo.getNickname())) {

            tvNickName.setText(mUserInfo.getNickname());
        }
        if (!TextUtils.isEmpty(mUserInfo.getIntro())) {
            tvIntroduction.setText(mUserInfo.getIntro());
        }
        if (!TextUtils.isEmpty(mUserInfo.getEmotion())) {

            tvFeel.setText(s[Integer.parseInt(mUserInfo.getEmotion())]);
        }
        if (!TextUtils.isEmpty(mUserInfo.getProfessional())) {
            tvJob.setText(mUserInfo.getProfessional());
        }
        if (!TextUtils.isEmpty(mUserInfo.getProvince() + mUserInfo.getCity())) {
            tvHome.setText(" " + mUserInfo.getCity());
        }

        if (!TextUtils.isEmpty(mUserInfo.getBirthday()) && Integer.parseInt(mUserInfo.getBirthday()) >0) {
            String str = DateUtilsl.getStringToDataMrl(mUserInfo.getBirthday());
            String sr = DateUtilsl.getDateToString(str);
            int year = Integer.parseInt(sr.substring(0, 4));
            int month = Integer.parseInt(sr.substring(4, 6));
            int day = Integer.parseInt(sr.substring(6, 8));
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(System.currentTimeMillis());
            int nowYear = c.get(Calendar.YEAR);
            if (year > nowYear) {
                tvAge.setText(getString(R.string.edit_profile_nocome_world) + getStarSeat(month, day));
            } else {


                tvAge.setText(String.valueOf(nowYear - year) + getString(R.string.unit_age_year) + " " + getStarSeat(month, day));
            }
        }else
            tvAge.setText("18" + getString(R.string.unit_age_year)  );
        if (mUserInfo.getSex() == UserInfo.GENDER_MALE) {
            rbBoy.setChecked(true);
        } else {
            rbGril.setChecked(true);
        }
        FrescoUtil.frescoResize(SourceFactory.wrapPathToUri(mUserInfo.getAvatar()),
                (int) getResources().getDimension(R.dimen.avatar_size_default),
                (int) getResources().getDimension(R.dimen.avatar_size_default),
                draweeAvatar);

    }

    /**
     * 通过日期来确定星座
     *
     * @param mouth
     * @param day
     * @return
     */
    public String getStarSeat(int mouth, int day) {
        String starSeat = null;

        if ((mouth == 3 && day >= 21) || (mouth == 4 && day <= 19)) {
            starSeat = getString(R.string.edit_profile_aries);
        } else if ((mouth == 4 && day >= 20) || (mouth == 5 && day <= 20)) {
            starSeat = getString(R.string.edit_profile_taurus);
        } else if ((mouth == 5 && day >= 21) || (mouth == 6 && day <= 21)) {
            starSeat = getString(R.string.edit_profile_gemini);
        } else if ((mouth == 6 && day >= 22) || (mouth == 7 && day <= 22)) {
            starSeat = getString(R.string.edit_profile_cancer);
        } else if ((mouth == 7 && day >= 23) || (mouth == 8 && day <= 22)) {
            starSeat = getString(R.string.edit_profile_leo);
        } else if ((mouth == 8 && day >= 23) || (mouth == 9 && day <= 22)) {
            starSeat = getString(R.string.edit_profile_virgo);
        } else if ((mouth == 9 && day >= 23) || (mouth == 10 && day <= 23)) {
            starSeat = getString(R.string.edit_profile_libra);
        } else if ((mouth == 10 && day >= 24) || (mouth == 11 && day <= 22)) {
            starSeat = getString(R.string.edit_profile_scorpio);
        } else if ((mouth == 11 && day >= 23) || (mouth == 12 && day <= 21)) {
            starSeat = getString(R.string.edit_profile_sagittarius);
        } else if ((mouth == 12 && day >= 22) || (mouth == 1 && day <= 19)) {
            starSeat = getString(R.string.edit_profile_capricornus);
        } else if ((mouth == 1 && day >= 20) || (mouth == 2 && day <= 18)) {
            starSeat = getString(R.string.edit_profile_aquarius);
        } else {
            starSeat = getString(R.string.edit_profile_pisces);
        }
        return starSeat;
    }

    private void showPopupWindowAge() {
        View timePickerView = this.getLayoutInflater().inflate(R.layout.popup_edit_age, null);
        year = (WheelView) timePickerView.findViewById(R.id.year);
        month = (WheelView) timePickerView.findViewById(R.id.month);
        day = (WheelView) timePickerView.findViewById(R.id.day);
        TextView tvEx = (TextView) timePickerView.findViewById(R.id.tv_age_eixt);
        age = (TextView) timePickerView.findViewById(R.id.edit_profile_tv_age_popup);
        constellation = (TextView) timePickerView.findViewById(R.id.edit_profile_tv_constellation);
        timePickerView.setFocusableInTouchMode(true);

        for (int i = 1950; i <= 2100; i++)
            yData.add(i + "");

        year.setViewAdapter(new ArrayWheelAdapter<>(this, yData.toArray()));
        year.setCurrentItem(60);
        year.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                year_index = newValue;
                initDayData();
                time = yData.get(year_index) + mData.get(month_index) + dData.get(day_index);


                int y = Integer.parseInt(yData.get(year_index));
                int m = Integer.parseInt(mData.get(month_index));
                int d = Integer.parseInt(dData.get(day_index));
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(System.currentTimeMillis());
                final int nowYear = c.get(Calendar.YEAR);
                if (y > nowYear) {
                    age.setText(getString(R.string.edit_profile_nocome_world));
                    constellation.setText(getStarSeat(m, d));
                } else {
                    age.setText(String.valueOf(nowYear - y) + getString(R.string.unit_age_year));
                    constellation.setText(getStarSeat(m, d));

                    tvAge.setText(age.getText().toString() + " " + constellation.getText().toString());
                }
            }
        });
        tvEx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvAge.setText(age.getText().toString() + " " + constellation.getText().toString());
                presenter.updateBirthday(LocalDataManager.getInstance().getLoginInfo().getToken(), time);
                timePickerPopupWindow.dismiss();
            }
        });

        for (int i = 1; i <= 12; i++) {
            mData.add(i < 10 ? "0" + i + "" : i + "");
        }
        month.setViewAdapter(new ArrayWheelAdapter<>(this, mData.toArray()));
        month.setCurrentItem(0);
        month.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                month_index = newValue;
                initDayData();
                time = yData.get(year_index) + mData.get(month_index) + dData.get(day_index);
                int y = Integer.parseInt(yData.get(year_index));
                int m = Integer.parseInt(mData.get(month_index));
                int d = Integer.parseInt(dData.get(day_index));
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(System.currentTimeMillis());
                final int nowYear = c.get(Calendar.YEAR);
                if (y > nowYear) {
                    age.setText(getString(R.string.edit_profile_nocome_world));
                    constellation.setText(getStarSeat(m, d));
                } else {
                    age.setText(String.valueOf(nowYear - y) + getString(R.string.unit_age_year));
                    constellation.setText(getStarSeat(m, d));

                    tvAge.setText(age.getText().toString() + " " + constellation.getText().toString());
                }
            }
        });

        initDayData();
        dayAdapter = new ArrayWheelAdapter<>(this, dData.toArray());
        day.setViewAdapter(dayAdapter);
        day.setCurrentItem(0);
        day.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                day_index = newValue;
                time = yData.get(year_index) + mData.get(month_index) + dData.get(day_index);
                int y = Integer.parseInt(yData.get(year_index));
                int m = Integer.parseInt(mData.get(month_index));
                int d = Integer.parseInt(dData.get(day_index));
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(System.currentTimeMillis());
                final int nowYear = c.get(Calendar.YEAR);
                if (y > nowYear) {
                    age.setText(getString(R.string.edit_profile_nocome_world));
                    constellation.setText(getStarSeat(m, d));
                } else {
                    age.setText(String.valueOf(nowYear - y) + getString(R.string.unit_age_year));
                    constellation.setText(getStarSeat(m, d));

                    tvAge.setText(age.getText().toString() + " " + constellation.getText().toString());
                }
            }
        });

//      创建PopupWindow实例
        View rootview = LayoutInflater.from(this).inflate(R.layout.activity_edit_profile, null);
        timePickerPopupWindow = new PopupWindow(timePickerView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        timePickerPopupWindow.showAtLocation(rootview, Gravity.BOTTOM, 0, 0);

        timePickerView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_BACK:
                        if (timePickerPopupWindow != null) {
                            timePickerPopupWindow.dismiss();
                        }
                        break;
                }
                return false;
            }

        });
        // 设置弹出窗体显示时的动画，从底部向上弹出
        timePickerPopupWindow.setAnimationStyle(R.style.popupAnim);
        //点击空白处时，隐藏掉pop窗口
        timePickerPopupWindow.setFocusable(true);
        timePickerPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        timePickerPopupWindow.update();

    }

    private void initDayData() {
        String m = mData.get(month_index).substring(0, mData.get(month_index).length() - 1);
        String y = yData.get(year_index).substring(0, yData.get(year_index).length() - 1);

        if (m.equals("01") || m.equals("03") || m.equals("05") || m.equals("07") || m.equals("08") ||
                m.equals("10") || m.equals("12")) {
            dData.clear();
            for (int i = 1; i <= 31; i++) {
                dData.add(i < 10 ? "0" + i + "" : i + "");
            }
            day.setViewAdapter(new ArrayWheelAdapter<>(this, dData.toArray()));
            day.setCurrentItem(0);
        } else if (m.equals("02")) {
            dData.clear();
            if (Integer.parseInt(y) % 4 == 0 && Integer.parseInt(y) % 100 != 0 || Integer.parseInt(y) % 400 == 0) {
                for (int i = 1; i <= 29; i++) {
                    dData.add(i < 10 ? "0" + i + "" : i + "");
                }
            } else {
                for (int i = 1; i <= 28; i++) {
                    dData.add(i < 10 ? "0" + i + "" : i + "");
                }
            }
            day.setViewAdapter(new ArrayWheelAdapter<>(this, dData.toArray()));
            day.setCurrentItem(0);
        } else {
            dData.clear();
            for (int i = 1; i <= 30; i++) {
                dData.add(i < 10 ? "0" + i + "" : i + "");
            }
            day.setViewAdapter(new ArrayWheelAdapter<>(this, dData.toArray()));
            day.setCurrentItem(0);
        }

    }

    private void showPopupWindowFeel() {
        View view = LayoutInflater.from(this).inflate(R.layout.popup_edit_feel, null);
        popFeel = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        popFeel.setContentView(view);

        popFeel.setAnimationStyle(R.style.popupAnim);

        popFeel.setOutsideTouchable(true);
        popFeel.setBackgroundDrawable(new BitmapDrawable());

        tvExit = (TextView) view.findViewById(R.id.tv_eixt);
        RelativeLayout rlNo = (RelativeLayout) view.findViewById(R.id.popup_feel_no);
        RelativeLayout rlLoney = (RelativeLayout) view.findViewById(R.id.popup_feel_lonely);
        RelativeLayout rlLove = (RelativeLayout) view.findViewById(R.id.popup_feel_love);
        RelativeLayout rlMarried = (RelativeLayout) view.findViewById(R.id.popup_feel_married);
        RelativeLayout rlGay = (RelativeLayout) view.findViewById(R.id.popup_feel_gay);
        final TextView tvNo = (TextView) view.findViewById(R.id.edit_profile_tv_no_label);
        final TextView tvLoney = (TextView) view.findViewById(R.id.edit_profile_tv_lonely_label);
        final TextView tvLove = (TextView) view.findViewById(R.id.edit_profile_tv_love_label);
        final TextView tvMarried = (TextView) view.findViewById(R.id.edit_profile_tv_married_label);
        final TextView tvGay = (TextView) view.findViewById(R.id.edit_profile_tv_gay_label);
        final ImageView imNo = (ImageView) view.findViewById(R.id.popup_feel_no_img);
        final ImageView imLoney = (ImageView) view.findViewById(R.id.edit_profile_tv_lonely);
        final ImageView imLove = (ImageView) view.findViewById(R.id.edit_profile_tv_love);
        final ImageView imMarried = (ImageView) view.findViewById(R.id.edit_profile_tv_married);
        final ImageView imGay = (ImageView) view.findViewById(R.id.edit_profile_tv_gay);
        if (tvFeel.getText().toString().equals(tvNo.getText().toString())) {
            imNo.setVisibility(View.VISIBLE);
            tvFeel.setTextColor(getResources().getColor(R.color.yunkacolor_60));
        } else if (tvFeel.getText().toString().equals(tvLoney.getText().toString())) {
            imLoney.setVisibility(View.VISIBLE);
            tvFeel.setTextColor(getResources().getColor(R.color.yunkacolor_60));
        } else if (tvFeel.getText().toString().equals(tvLove.getText().toString())) {
            imLove.setVisibility(View.VISIBLE);
            tvFeel.setTextColor(getResources().getColor(R.color.yunkacolor_60));
        } else if (tvFeel.getText().toString().equals(tvMarried.getText().toString())) {
            imMarried.setVisibility(View.VISIBLE);
            tvFeel.setTextColor(getResources().getColor(R.color.yunkacolor_60));
        } else {
            imGay.setVisibility(View.VISIBLE);
            tvFeel.setTextColor(getResources().getColor(R.color.yunkacolor_60));
        }

        rlNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvFeel.setText(tvNo.getText().toString());
                presenter.updateEmotion(LocalDataManager.getInstance().getLoginInfo().getToken(), 0);
                popFeel.dismiss();
            }
        });
        rlLoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvFeel.setText(tvLoney.getText().toString());
                presenter.updateEmotion(LocalDataManager.getInstance().getLoginInfo().getToken(), 1);
                popFeel.dismiss();
            }
        });
        rlLove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvFeel.setText(tvLove.getText().toString());
                presenter.updateEmotion(LocalDataManager.getInstance().getLoginInfo().getToken(), 2);
                popFeel.dismiss();
            }
        });
        rlMarried.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvFeel.setText(tvMarried.getText().toString());
                presenter.updateEmotion(LocalDataManager.getInstance().getLoginInfo().getToken(), 3);
                popFeel.dismiss();
            }
        });
        rlGay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvFeel.setText(tvGay.getText().toString());
                presenter.updateEmotion(LocalDataManager.getInstance().getLoginInfo().getToken(), 4);
                popFeel.dismiss();
            }
        });
        tvExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popFeel.dismiss();
            }
        });

        View rootview = LayoutInflater.from(this).inflate(R.layout.activity_edit_profile, null);
        popFeel.showAtLocation(rootview, Gravity.BOTTOM, 0, 0);
    }

    private void showPopupWindowHome() {
        View view = LayoutInflater.from(this).inflate(R.layout.popup_edit_home, null);
        home = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        home.setContentView(view);

        home.setAnimationStyle(R.style.popupAnim);

        home.setOutsideTouchable(true);
        home.setBackgroundDrawable(new BitmapDrawable());

        tvShow = (TextView) view.findViewById(R.id.tv_show);
        mViewProvince = (tw.chiae.inlive.presentation.ui.main.me.popup.city.widget.WheelView) view.findViewById(R.id.id_province);
        mViewCity = (tw.chiae.inlive.presentation.ui.main.me.popup.city.widget.WheelView) view.findViewById(R.id.id_city);
        mViewDistrict = (tw.chiae.inlive.presentation.ui.main.me.popup.city.widget.WheelView) view.findViewById(R.id.id_district);
        // 添加change事件
        mViewProvince.addChangingListener(this);
        mViewCity.addChangingListener(this);
//        if (tvHome == null) {
//            tvHome.setText(" " + mUserInfo.getCity());
//        }
        mViewProvince.setViewAdapter(new tw.chiae.inlive.presentation.ui.main.me.popup.city.widget.adapters.ArrayWheelAdapter<>(EditProfileActivity.this, mProvinceDatas));
        // 设置可见条目数量
        mViewProvince.setVisibleItems(7);
        mViewCity.setVisibleItems(7);
        mViewDistrict.setVisibleItems(7);
        updateCities();
        updateAreas();

        tvShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvHome.setText(" " + mCurrentCityName);
                presenter.updateProvince(LocalDataManager.getInstance().getLoginInfo().getToken(), mCurrentProviceName, mCurrentCityName);
                home.dismiss();
            }
        });

        View rootview = LayoutInflater.from(this).inflate(R.layout.activity_edit_profile, null);
        home.showAtLocation(rootview, Gravity.BOTTOM, 0, 0);
//        home.showAsDropDown(rlInform);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("RayTest","onResume");
        //SubmitName();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //无论修改哪一项，只要成功了就认为返回时需要刷新
        if (resultCode == RESULT_OK) {
            setResult(RESULT_OK,data);
        }

        //更新具体修改的那一项的值
        switch (requestCode) {
            case CODE_EDIT_NICKNAME:
                if (resultCode == RESULT_OK && data != null) {
                    String nickname = data.getStringExtra(RESULT_NICKNAME);
                    if (!TextUtils.isEmpty(nickname)) {
                        tvNickName.setText(nickname);
                        mUserInfo.setNickname(nickname);
                    }
                }
                break;

            case CODE_EDIT_PROFESSIONAL:
                if (resultCode == RESULT_OK && data != null) {
                    String professional = data.getStringExtra(RESULT_PROFESSIONAL);
                    if (!TextUtils.isEmpty(professional)) {
                        tvJob.setText(professional);
                    }
                }
                break;

            case CODE_EDIT_INTRODUCTION:
                if (resultCode == RESULT_OK && data != null) {
                    String introduction = data.getStringExtra(RESULT_INTRODUCTION);
                    if (!TextUtils.isEmpty(introduction)) {
                        tvIntroduction.setText(introduction);
                    }
                }
                break;

            case CODE_EDIT_AVATAR:
                if (resultCode == RESULT_OK && data != null) {
                    ImagePipeline imagePipeline = Fresco.getImagePipeline();
                    Uri uri = data.getData();

                    imagePipeline.evictFromMemoryCache(uri);
                    //imagePipeline.evictFromDiskCache(uri);
                    draweeAvatar.setImageURI(uri);

                }
                break;

        }
    }

    @Override
    public void onChanged(tw.chiae.inlive.presentation.ui.main.me.popup.city.widget.WheelView wheel, int oldValue, int newValue) {
        if (wheel == mViewProvince) {
            updateCities();
        } else if (wheel == mViewCity) {
            updateAreas();
        } else if (wheel == mViewDistrict) {
            mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[newValue];
            mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);
        }
    }

    /**
     * 根据当前的市，更新区WheelView的信息
     */
    private void updateAreas() {
        int pCurrent = mViewCity.getCurrentItem();
        mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
        String[] areas = mDistrictDatasMap.get(mCurrentCityName);

        if (areas == null) {
            areas = new String[]{""};
        }
        mViewDistrict.setViewAdapter(new tw.chiae.inlive.presentation.ui.main.me.popup.city.widget.adapters.ArrayWheelAdapter<>(this, areas));
        mViewDistrict.setCurrentItem(0);
    }

    /**
     * 根据当前的省，更新市WheelView的信息
     */
    private void updateCities() {
        int pCurrent = mViewProvince.getCurrentItem();
        mCurrentProviceName = mProvinceDatas[pCurrent];
        String[] cities = mCitisDatasMap.get(mCurrentProviceName);
        if (cities == null) {
            cities = new String[]{""};
        }
        mViewCity.setViewAdapter(new tw.chiae.inlive.presentation.ui.main.me.popup.city.widget.adapters.ArrayWheelAdapter<String>(this, cities));
        mViewCity.setCurrentItem(0);
        updateAreas();
    }

    private int stateh, xunih;
    private PopupWindow mPopupWindow;
    private View popuView;
    private EditText homeedt;
    private Button homebut;

    private ImageButton finsh;

    private void startThemPopupWindow() {

        if (mPopupWindow == null) {
            getXuNiDpi();
            LayoutInflater relativeLayout = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            popuView = relativeLayout.inflate(R.layout.setting_home_popup, null);
            homeedt = (EditText) popuView.findViewById(R.id.setting_home_edt);
            finsh = (ImageButton) popuView.findViewById(R.id.finsh);
            homebut = (Button) popuView.findViewById(R.id.setting_home_but);
            homeedt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (EditNicknameActivity.getlength(homeedt.getText().toString()) > 20) {
                        toastShort("輸入的字數超過了20字符");
                    }
                }
            });

            homebut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (EditNicknameActivity.getlength(homeedt.getText().toString()) > 20) {
                        toastShort("輸入的字數超過了20字符");
                        return;
                    }
                    tvHome.setText(" " + homeedt.getText().toString().trim());
                    presenter.updateProvince(LocalDataManager.getInstance().getLoginInfo().getToken(), "", homeedt.getText().toString().trim());
                    mPopupWindow.dismiss();
                }
            });
            finsh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPopupWindow.dismiss();
                }
            });
            mPopupWindow = new PopupWindow(popuView,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            // 使其聚集
            mPopupWindow.setFocusable(true);
            // 设置允许在外点击消失
            mPopupWindow.setOutsideTouchable(true);
            // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
//            mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
//            mPopupWindow.setAnimationStyle(R.style.popwin_anim_style);
            if (xunih == 0) {
                mPopupWindow.showAsDropDown(mCommit, 0, -stateh);
            } else {
                mPopupWindow.showAsDropDown(mCommit, 0, -(xunih - stateh));
            }
//            startpopwindow(popuView);
        } else {
            if (xunih == 0) {
                mPopupWindow.showAsDropDown(mCommit, 0, -stateh);
            } else {
                mPopupWindow.showAsDropDown(mCommit, 0, -(xunih - stateh));
            }
//            startpopwindow(popuView);
        }
    }

    private void getXuNiDpi() {
        int dpi = 0;
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        @SuppressWarnings("rawtypes")
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            dpi = dm.heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        xunih = dpi - getWindowManager().getDefaultDisplay().getHeight();
        Rect rect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        stateh = rect.top;

        Display disp = this.getWindowManager().getDefaultDisplay();
        Point outP = new Point();
        disp.getSize(outP);
    }

    @Override
    public void setCoinData(int currencyItemSum) {

    }
}