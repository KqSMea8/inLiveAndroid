package tw.chiae.inlive.presentation.ui.room.player;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.facebook.drawee.view.SimpleDraweeView;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import tw.chiae.inlive.R;
import tw.chiae.inlive.data.bean.LiveSummary;
import tw.chiae.inlive.presentation.ui.base.BaseActivity;

import static com.tencent.bugly.crashreport.inner.InnerAPI.context;

/**
 * Created by rayyeh on 2017/4/28.
 */

public class PopPlayerRoom extends SwipeBackActivity {
    private WindowManager.LayoutParams windowLayoutParams;
    private WindowManager windowManager;
    private View FloatView;
    private SimpleDraweeView draweeAnchor;
    private static final String ARG_ANCHOR_SUMMARY = "anchor";
    private LiveSummary mSummary;



    protected void findViews(Bundle savedInstanceState) {
       Intent it =  getIntent();
        Bundle bundle = it.getExtras();
        mSummary = bundle.getParcelable(ARG_ANCHOR_SUMMARY);
        Log.i("RayTest","mSummary"+mSummary.getAvatar());
    }


    protected void init() {
        if(windowManager==null)
            windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        windowLayoutParams = new WindowManager.LayoutParams();
        windowLayoutParams.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
        windowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_FULLSCREEN;
        windowLayoutParams.gravity = Gravity.CENTER;
        windowLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        windowLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        windowLayoutParams.format =  PixelFormat.RGBA_8888;

        FloatView = LayoutInflater.from(this).inflate(R.layout.player_service_floatview,null);

        draweeAnchor = (SimpleDraweeView) FloatView.findViewById(R.id.dialog_user_info_player);
        FloatView.setOnTouchListener(floatListener);
        FloatView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("RayTest","on click");
            }
        });
        windowManager.addView(FloatView,windowLayoutParams);
        draweeAnchor.setVisibility(View.GONE);
    }

    private View.OnTouchListener floatListener = new View.OnTouchListener() {
        int lastX, lastY;
        int paramX, paramY;

        @Override
        public boolean onTouch(View view, MotionEvent event) {

            switch (event.getAction()) {          //判斷觸控的動作

                case MotionEvent.ACTION_DOWN:// 按下圖片時
                    Log.i("RayTest", "ACTION_DOWN");
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                    paramX = windowLayoutParams.x;
                    paramY = windowLayoutParams.y;                 //觸控的Y軸位置

                case MotionEvent.ACTION_MOVE:// 移動圖片時
                    Log.i("RayTest", "ACTION_MOVE");
                    int dx = (int) event.getRawX() - lastX;
                    int dy = (int) event.getRawY() - lastY;
                    windowLayoutParams.x = paramX + dx;
                    windowLayoutParams.y = paramY + dy;
// 更新悬浮窗位置
                    windowManager.updateViewLayout(FloatView, windowLayoutParams);

                    break;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViews(savedInstanceState);
        init();
    }
}
