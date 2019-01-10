package tw.chiae.inlive.presentation.ui.chatting;

import android.app.Fragment;
import android.content.Context;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import tw.chiae.inlive.R;

import java.util.List;


public class MainView extends RelativeLayout {

    List<Fragment> fragments;
    private ImageView mMsgUnreadiv;
    private ScrollControllViewPager mViewContainer;

    public MainView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void initModule() {
//        mMsgUnreadiv = (ImageView) findViewById(R.id.msg_unread_iv);
        mViewContainer = (ScrollControllViewPager) findViewById(R.id.viewpager);
    }

    public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        mViewContainer.setOnPageChangeListener(onPageChangeListener);
    }

    public void setViewPagerAdapter(FragmentPagerAdapter adapter) {
        mViewContainer.setAdapter(adapter);
    }

//    public void dismissUnreadFlag() {
//        mMsgUnreadiv.setVisibility(View.INVISIBLE);
//    }
//
//    public void showNewMsgReceivedFlag() {
//        mMsgUnreadiv.setVisibility(View.VISIBLE);
//    }
}
