package tw.chiae.inlive.presentation.ui.room;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by rayyeh on 2017/8/25.
 */

public class VisLinearLayout extends LinearLayout {
    private VisibilitChangeListener mVisibilityListener;

    public VisLinearLayout(Context context) {
        super(context);
    }

    public VisLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public VisLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if(mVisibilityListener!=null){
            if(getVisibility()==visibility)
                mVisibilityListener.isVisibilityChange(visibility);
        }
    }


    public void setVisibilityListener(VisibilitChangeListener listener) {
        this.mVisibilityListener = listener;
    }


}
