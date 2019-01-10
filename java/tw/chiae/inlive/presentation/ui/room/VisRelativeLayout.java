package tw.chiae.inlive.presentation.ui.room;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by rayyeh on 2017/8/28.
 */

public class VisRelativeLayout extends RelativeLayout {
    public VisRelativeLayout(Context context) {
        super(context);
    }

    public VisRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VisRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private VisibilitChangeListener mVisibilityListener;

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
