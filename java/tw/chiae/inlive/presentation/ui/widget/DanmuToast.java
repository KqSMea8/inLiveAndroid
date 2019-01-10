package tw.chiae.inlive.presentation.ui.widget;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import tw.chiae.inlive.R;

import java.lang.reflect.Field;

/**
 * Created by huanzhang on 4/5/16.
 */
public class DanmuToast extends Toast {

    private static Toast lastInstance;

    /**
     * Construct an empty Toast object.  You must call {@link #setView} before you
     * can call {@link #show}.
     *
     * @param context The context to use.  Usually your {@link Application}
     *                or {@link Activity} object.
     */
    public DanmuToast(Context context) {
        super(context);
    }

    public static DanmuToast makeCustomText(Context context, CharSequence text,
                                            int duration) {
        DanmuToast toast = new DanmuToast(context);
        /*设置Toast的位置*/
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.setDuration(duration);
        /*让Toast显示为我们自定义的样子*/
        toast.setView(getToastView(context, text));

        try {
            Object mTN = getField(toast.getClass().getSuperclass(), toast, "mTN");
            if (mTN != null) {
                Object mParams = getField(mTN.getClass(), mTN, "mParams");
                if (mParams != null
                        && mParams instanceof WindowManager.LayoutParams) {
                    WindowManager.LayoutParams params = (WindowManager.LayoutParams) mParams;
                    params.windowAnimations = R.style.anim_view;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return toast;
    }

    @Override
    public void show() {
        if (lastInstance != null) {
            lastInstance.cancel();
        }
        super.show();
        lastInstance = this;
    }

    public static DanmuToast makeCustomText(Context context, int text,
                                            int duration) {
        return makeCustomText(context, context.getString(text), duration);
    }

    public static View getToastView(Context context, CharSequence msg) {
        LayoutInflater inflate = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflate.inflate(R.layout.custom_toast, null);
        TextView text = (TextView) v.findViewById(R.id.toast_text);
        text.setText(msg);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(getWidth(context), ViewGroup.LayoutParams.MATCH_PARENT);
        text.setLayoutParams(params);
        return v;
    }

    public static int getWidth(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getWidth();
    }

    private static Object getField(Class<?> clz, Object object, String fieldName)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = clz.getDeclaredField(fieldName);
        if (field != null) {
            field.setAccessible(true);
            return field.get(object);
        }
        return null;
    }
}
