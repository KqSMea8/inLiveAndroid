package tw.chiae.inlive.presentation.ui.main.index;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by 繁华丶落尽 on 2017/1/13.
 */
public class MyLinearLayout extends LinearLayout {
    private int color;

    public void setColor(int color) {
        this.color = color;
        invalidate();
    }

    public MyLinearLayout(Context context) {
        super(context);
    }

    public MyLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(color, PorterDuff.Mode.DARKEN);
    }
}
