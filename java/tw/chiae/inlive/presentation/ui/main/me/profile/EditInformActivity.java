package tw.chiae.inlive.presentation.ui.main.me.profile;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import tw.chiae.inlive.R;
import tw.chiae.inlive.data.repository.SourceFactory;
import tw.chiae.inlive.presentation.ui.base.BaseActivity;
import tw.chiae.inlive.presentation.ui.main.webview.SimpleWebViewActivity;

import rx.functions.Action1;

/**
 * @author lww
 * @since 1.0.0
 * 修改昵称
 */
public class EditInformActivity extends BaseActivity{

    private TextView tvInform,tvSoft;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_inform;
    }

    @Override
    protected void findViews(Bundle savedInstanceState) {
        tvInform = $(R.id.tv_inform);
        tvSoft = $(R.id.tv_soft);
    }

    @Override
    protected void init() {
        SpannableString sp1=new SpannableString(getString(R.string.edit_inform_capital_security));
        SpannableString sp2=new SpannableString(getString(R.string.edit_inform_identity_authen));
        sp1.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.yellow)),3,7, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        sp2.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.yellow)),6,10, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        tvSoft.setText(sp1);
        tvInform.setText(sp2);

        subscribeClick(R.id.inform_to, new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                startActivity(SimpleWebViewActivity.createIntent(EditInformActivity.this,
                        SourceFactory.wrapPath(getString(R.string.setting_contact_url)),""));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void setCoinData(int currencyItemSum) {

    }
}
