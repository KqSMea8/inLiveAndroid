package tw.chiae.inlive.presentation.ui.login.splash;

import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jakewharton.rxbinding.view.RxView;
import tw.chiae.inlive.R;
import tw.chiae.inlive.util.Const;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import rx.functions.Action1;

/** */
class SplashPagerAdapter extends PagerAdapter {
    private SplashBtnListener splashBtnListener;
    private ArrayList<Integer> mImageIds = new ArrayList<>();

    public SplashPagerAdapter(ArrayList<Integer> imageIds, SplashBtnListener listener) {
        mImageIds = imageIds;
        splashBtnListener = listener;
    }

    @Override
    public int getCount() {
        return mImageIds.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position,
                            Object object) {
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.item_splash_page,
                container, false);
        SimpleDraweeView imageView = (SimpleDraweeView) view.findViewById(R.id.item_splash_img);
        Button startBtn = (Button) view.findViewById(R.id.item_splash_btn_start);
        container.addView(view, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        imageView.setImageURI(Uri.parse(("res:///" + mImageIds.get(position))));

        if (position == mImageIds.size() - 1) {
            startBtn.setVisibility(View.VISIBLE);
        } else {
            startBtn.setVisibility(View.GONE);
        }
        RxView.clicks(startBtn).throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        splashBtnListener.onStartBtnClicked();
                    }
                });
        return view;
    }

    public interface SplashBtnListener {
        void onStartBtnClicked();
    }
}
