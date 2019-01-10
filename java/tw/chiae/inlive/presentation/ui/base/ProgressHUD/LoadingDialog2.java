package tw.chiae.inlive.presentation.ui.base.ProgressHUD;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by rayyeh on 2017/3/23.
 */

class LoadingDialog2 extends Dialog {
    public LoadingDialog2(Context context) {
        super(context);
    }

    public LoadingDialog2(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    protected LoadingDialog2(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


}
