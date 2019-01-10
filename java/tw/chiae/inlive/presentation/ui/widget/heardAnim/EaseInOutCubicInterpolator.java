package tw.chiae.inlive.presentation.ui.widget.heardAnim;

import android.animation.TimeInterpolator;

/**
 * Created by Administrator on 2016/6/7.
 */

public class EaseInOutCubicInterpolator implements TimeInterpolator{
    @Override
    public float getInterpolation(float input) {
        if ((input *= 2) < 1.0f) {
            return 0.5f * input * input * input;
        }
        input -= 2;
        return 0.5f * input * input * input + 1;
    }
}
