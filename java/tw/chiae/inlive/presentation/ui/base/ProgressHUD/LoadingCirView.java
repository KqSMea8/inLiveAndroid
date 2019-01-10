package tw.chiae.inlive.presentation.ui.base.ProgressHUD;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import tw.chiae.inlive.R;

/**
 * Created by rayyeh on 2017/3/22.
 */

@SuppressLint("AppCompatCustomView")
public class LoadingCirView extends ImageView {
    private float mRotateDegrees;
    private int mFrameTime;
    private boolean mNeedToUpdateView;
    private Runnable mUpdateViewRunnable;
    private final int MaxSize = 50;

    public LoadingCirView(Context context) {
        super(context);
        init();
    }

    

    public LoadingCirView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setPadding(5,5,5,5);
        setMeasuredDimension(getMeasuredWidth()-MaxSize,getMeasuredHeight()-MaxSize);
    }

    private void init() {
        setImageResource(R.drawable.loading_spinner);
        mFrameTime = 1000 / 12;
//        转圈速度
        mUpdateViewRunnable = new Runnable() {
            @Override
            public void run() {
                mRotateDegrees += 30;
                mRotateDegrees = mRotateDegrees < 360 ? mRotateDegrees : mRotateDegrees - 360;
                invalidate();
                if (mNeedToUpdateView) {
                    postDelayed(this, mFrameTime);
                }
            }
        };
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.rotate(mRotateDegrees, getWidth() / 2 , getHeight() / 2 );
        super.onDraw(canvas);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mNeedToUpdateView = true;
        post(mUpdateViewRunnable);
    }

    @Override
    protected void onDetachedFromWindow() {
        mNeedToUpdateView = false;
        super.onDetachedFromWindow();
    }


}
