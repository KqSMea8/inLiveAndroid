package tw.chiae.inlive.presentation.ui.room.pubmsg;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

import tw.chiae.inlive.presentation.ui.main.mergefilm.Log;

/**
 * Created by rayyeh on 2017/9/27.
 */

public class NameImageSpan extends ImageSpan {
    public NameImageSpan(Context mContext, int vipresId) {
        super(mContext,vipresId);
    }

    public NameImageSpan(Drawable d, int alignBaseline) {
        super(d,alignBaseline);
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        Drawable d = getDrawable();
        Rect rect = d.getBounds();
        if (fm != null) {
            Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
            int fontHeight = fmPaint.bottom - fmPaint.top;
            int drHeight = rect.bottom - rect.top;

            int top = drHeight / 2 - fontHeight / 4;
            int bottom = drHeight / 2 + fontHeight / 4;

            fm.ascent = -bottom;
            fm.top = -bottom;
            fm.bottom = top;
            fm.descent = top;
        }
        return rect.right;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        if(getDrawable() instanceof BitmapDrawable){
            if (((BitmapDrawable)getDrawable()).getBitmap().isRecycled()) {
                return;
            }
        }

        Drawable b = getDrawable();
        BitmapDrawable bd = (BitmapDrawable) getDrawable();
        if(b!=null|| !bd.getBitmap().isRecycled()){
            try {
                canvas.save();
                int transY = 0;
                transY = ((bottom - top) - b.getBounds().bottom) / 2 + top;
                canvas.translate(x, transY);
                b.draw(canvas);
            }catch (Exception e){
                Log.i("RayBitmap","bitmap is isRecycled 1!");
            }



        }else{
            Log.i("RayBitmap","bitmap is isRecycled! 2");
        }
        canvas.restore();
    }
}
