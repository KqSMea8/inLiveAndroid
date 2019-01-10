package tw.chiae.inlive.presentation.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import tw.chiae.inlive.presentation.ui.main.mergefilm.Log;

/**
 * Created by rayyeh on 2017/8/25.
 */
@SuppressLint("AppCompatCustomView")
public class AdaptiveTextView extends TextView{
    private int mMaxWidth;

    public AdaptiveTextView(Context context) {
        super(context);
    }

    public AdaptiveTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AdaptiveTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setText(final CharSequence text, final BufferType type) {
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                adjustTvTextSize(text.toString());
                Log.i("RayTest","addOnGlobalLayoutListener: "+text);
            }
        });
        super.setText(text, type);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        this.setMeasuredDimension(parentWidth, parentHeight);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mMaxWidth = parentWidth;
    }

    private void adjustTvTextSize(String text) {
        int avaiWidth = mMaxWidth - getPaddingLeft() - getPaddingRight() - 10;
        if (avaiWidth <= 0) {
            return;
        }

        TextPaint textPaintClone = new TextPaint(getPaint());
        float trySize = textPaintClone.getTextSize();

        while (textPaintClone.measureText(text) > avaiWidth) {
            trySize--;
            textPaintClone.setTextSize(trySize);
        }

        setTextSize(TypedValue.COMPLEX_UNIT_PX, trySize);
    }

}
