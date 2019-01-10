package tw.chiae.inlive.presentation.ui.base.ProgressHUD;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import tw.chiae.inlive.R;

/**
 * Created by rayyeh on 2017/3/22.
 */

public class LoadingDialog extends LinearLayout {

    private float mCornerRadius;
    private int mBackgroundColor;
    private View mView;
    private LinearLayout mainlayout;

    public LoadingDialog(Context context) {
        super(context);
       // init();
    }

    public LoadingDialog(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
       // init();
    }

    public LoadingDialog(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //init();
    }



    private void init() {
        int color = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            color = getContext().getResources().getColor(R.color.gray,null);
        }else
            color = getContext().getResources().getColor(R.color.gray);
        mainlayout  = (LinearLayout) mView.findViewById(R.id.background);
        initBackground(color, mCornerRadius);
    }

    private void initBackground(int color, float cornerRadius) {
        if(mView==null)
            return;
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(color);
        drawable.setAlpha(90);
        drawable.setCornerRadius(cornerRadius);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mainlayout.setBackground(drawable);
        } else {
            mainlayout.setBackgroundDrawable(drawable);
        }
    }

    public void setCornerRadius(float radius) {
        mCornerRadius = DpUtilsHelper.dpToPixel(radius, getContext());
        initBackground(mBackgroundColor, mCornerRadius);
    }

    public void setBaseColor(int color) {
        mBackgroundColor = color;
        initBackground(mBackgroundColor, mCornerRadius);
    }


    public void setContentView(View mainView) {
        this.mView = mainView;
        init();
    }

    public FrameLayout getContainer() {
        return (FrameLayout) mView.findViewById(R.id.container);
    }

    public FrameLayout getContainerText() {
        return (FrameLayout) mView.findViewById(R.id.container_text);
    }

    public LinearLayout getMainLayout() {
        return mainlayout;
    }
}
