package tw.chiae.inlive.presentation.ui.base.ProgressHUD;

import android.content.Context;

/**
 * Created by rayyeh on 2017/3/22.
 */

public class DpUtilsHelper {
        private static float scale;

        public static int dpToPixel(float dp, Context context) {
            if (scale == 0) {
                scale = context.getResources().getDisplayMetrics().density;
            }
            return (int) (dp * scale);
        }
}
