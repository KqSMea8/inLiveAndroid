package tw.chiae.inlive.presentation.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

import tw.chiae.inlive.R;

import java.lang.reflect.Field;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public class StrokeTextView extends TextView {

    private TextPaint m_TextPaint;
    private int mInnerColor;
    private int mOuterColor;

    private boolean m_bDrawSideLine = true; // 默认采用描边

    public StrokeTextView(Context context, int outerColor, int innerColor) {
        super(context);
        m_TextPaint = this.getPaint();
        this.mInnerColor = innerColor;
        this.mOuterColor = outerColor;
    }

    public StrokeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        m_TextPaint = this.getPaint();
        //获取自定义的XML属性名称
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StrokeTextView);
        //获取对应的属性值
        this.mInnerColor = a.getColor(R.styleable.StrokeTextView_innerColor, 0xffffff);
        this.mOuterColor = a.getColor(R.styleable.StrokeTextView_outerColor, 0xffffff);
        a.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (m_bDrawSideLine) {
            // 描外层
            // super.setTextColor(Color.BLUE); // 不能直接这么设，如此会导致递归
            int GradientStart = getResources().getColor(R.color.md_orange_gradient_start);
            int GradientEnd = getResources().getColor(R.color.md_orange_gradient_end);
            int Gradientborder = getResources().getColor(R.color.md_orange_gradient_border);
            LinearGradient shader = new LinearGradient(0, 0, 0, getHeight(), GradientStart, GradientEnd, Shader.TileMode.CLAMP);
            TextPaint mTextPaint = new TextPaint();
            TextPaint mPaint = new TextPaint();
            mTextPaint.setTextSize(100);
            mTextPaint.setColor(Gradientborder);
            mTextPaint.setStyle(Paint.Style.STROKE);
            mTextPaint.setFakeBoldText(true);
            mTextPaint.setStrokeWidth(10);
            int baseX = (int) (canvas.getWidth() / 2 - mTextPaint.measureText(getText().toString()) / 2);
            int baseY = (int) ((canvas.getHeight() / 2) - ((mTextPaint.descent() + mTextPaint.ascent()) / 2));
            canvas.drawText(getText().toString(), baseX, baseY, mTextPaint);
            mPaint.setTextSize(100);
            //mPaint.setColor(Color.RED);

            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setShader(shader);
            mPaint.setFakeBoldText(true);
            mPaint.setStrokeWidth(0);
            canvas.drawText(getText().toString(), baseX, baseY, mPaint);
        }

    }

    /**
     * 使用反射的方法进行字体颜色的设置
     */
    private void setTextColorUseReflection(int color ) {
        Field textColorField;
        try {
            textColorField = TextView.class.getDeclaredField("mCurTextColor");
            textColorField.setAccessible(true);
            textColorField.set(this, color);
            textColorField.setAccessible(false);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        m_TextPaint.setColor(color);
    }

}
