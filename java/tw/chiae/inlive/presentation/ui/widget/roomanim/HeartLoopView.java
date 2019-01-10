package tw.chiae.inlive.presentation.ui.widget.roomanim;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.lang.ref.WeakReference;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by rayyeh on 2017/5/22.
 */

public class HeartLoopView extends RelativeLayout {
    private Context context;
    //private Drawable[] drawables;
    private int MaxCount = 10;
    //private ImageView[] imgs;
    private Interpolator line = new LinearInterpolator();//线性
    private Interpolator acc = new AccelerateInterpolator();//加速
    private Interpolator dce = new DecelerateInterpolator();//减速
    private Interpolator accdec = new AccelerateDecelerateInterpolator();//先加速后减速
    private Interpolator[] interpolators;
    private int loveHeight;
    private int loveWidth;
    private LayoutParams lp;
    private Random random = new Random();
    private RelativeLayout main_gift;
    private int layoutHeight;
    private int layoutWidth;
    //private TimerTask task;
    private int mDurationTime;
    private boolean ifFirstAnim = false;
    private ValueAnimator animator;
    private Animator set;
    private ValueAnimator bezierValueAnimator;
    private boolean isPlayStatus = false;
    private Timer timer;
    private MyTimerTask task;

    public HeartLoopView(Context context) {
        super(context);
        if (isInEditMode()) {
            return;
        }
        this.context = context;

        initView();
    }

    public HeartLoopView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if (isInEditMode()) {
            return;
        }

        this.context = context;
        initView();
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    public void initView() {
        main_gift = new RelativeLayout(context);

        loveHeight = 100;
        loveWidth = 100;
        main_gift.setBackgroundColor(Color.BLUE);
        // 初始化插补器
        initInterpolators();
        lp = new LayoutParams(loveHeight, loveWidth);


        initDrawable();
    }

    private void initInterpolators(){
        if(interpolators==null || interpolators.length!=4){
            interpolators = new Interpolator[4];
            interpolators[0] = line;
            interpolators[1] = acc;
            interpolators[2] = dce;
            interpolators[3] = accdec;
        }
    }
    private void initDrawable() {
        //imgs = new ImageView[MaxCount];
        //drawables = new Drawable[MaxCount];
        for (int index = 1; index <= MaxCount; index++) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //drawables[index - 1] = getResources().getDrawable(getResourceID(index), null);
            } else {
                //drawables[index - 1] = getResources().getDrawable(getResourceID(index));
            }

        }

    }
    private  void removeAllDrawable(){
        /*if(imgs!=null)
            imgs = null;*/
        /*if(drawables!=null)
            drawables = null;*/
        handler = null;
        interpolators = null;
        this.removeAllViews();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        598 286
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        layoutHeight = getMeasuredHeight();
        layoutWidth = getMeasuredWidth();
    }

    private int getResourceID(int index) {
        return getContext().getResources().getIdentifier("planelw" + index, "drawable", getContext().getPackageName());
    }

    public void playAllAnim() {
        /*if(drawables==null)
            return;*/
        ImageView imageView = new ImageView(context.getApplicationContext());
        imageView.setLayoutParams(lp);
        //imageView.setImageDrawable(drawables[getRandom(0, MaxCount - 1)]);
        imageView.setImageResource(getResourceID(getRandom(0, MaxCount - 1)));
        this.addView(imageView);
        set = getAnimator(imageView);
        set.addListener(new AnimEndListener(imageView));
        set.start();
    }





/*    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    playAllAnim();
                    break;
            }
            super.handleMessage(msg);
        }
    };*/

    public void LoopAllAnim(final int DurationTime) {
        initializeTimerTask();
        if(timer==null)
            timer = new Timer();

        this.mDurationTime = DurationTime;
        timer.schedule(task, 0,mDurationTime);

    }

    public void stop() {
        isPlayStatus = false;
        if (timer != null) {
            this.timer.cancel();
            this.timer.purge();
            if (task != null) {
                this.task.cancel();  //将原任务从队列中移除
            }
        }
        handler = null;
        if(animator!=null)
            animator.removeAllUpdateListeners();
        if(set!=null){
            set.removeAllListeners();
            set.cancel();
        }

        this.removeAllViews();
        if(bezierValueAnimator!=null)
            bezierValueAnimator = null;
        removeAllDrawable();
    }

    public void Pause() {
        if(timer!=null) {
            this.timer.cancel();
            task.cancel();
        }
    }

    public void initializeTimerTask() {

        if(timer!=null) {
            this.timer.cancel();
            this.timer=null;
            if(task!=null)
                task.cancel();
        }

        task = new MyTimerTask(handler);
    }

    private MsgHandler handler = new MsgHandler(this);

    private static class MsgHandler extends Handler {

        private WeakReference<HeartLoopView> HeartView;

        public MsgHandler(HeartLoopView view) {
            HeartView = new WeakReference<HeartLoopView>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            if(HeartView==null)
                return;
            HeartLoopView view = HeartView.get();
            switch (msg.what) {
                case 1:
                    view.playAllAnim();
                    break;
            }
        }
    }


    private static class MyTimerTask extends TimerTask {
        private sRunnable runnable;
        private MsgHandler mhandler;

        public MyTimerTask(MsgHandler msghandler) {
            this.mhandler = msghandler;
            runnable = new sRunnable(msghandler);
        }

        @Override
        public void run() {
            if(mhandler==null)
                return;

            mhandler.post(runnable);
        }
    }

    private static class sRunnable implements Runnable {

        private MsgHandler msgHandler;
        public sRunnable(MsgHandler msghandler) {
            this.msgHandler = msghandler;
        }

        @Override
        public void run() {
            if(msgHandler==null)
                return;
            Message message = new Message();
            message.what = 1;
            msgHandler.sendMessage(message);
        }
    }

   /* private static final Runnable sRunnable = new Runnable() {

        @Override
        public void run() {
            if(mhandler==null)
            return;
            Message message = new Message();
            message.what = 1;
            mhandler.sendMessage(message);}
    };*/


    public void onDestroy() {
        removeAllDrawable();
    }

    private class AnimEndListener extends AnimatorListenerAdapter {
        private View target;

        public AnimEndListener(View target) {
            this.target = target;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            //因为不停的add 导致子view数量只增不减,所以在view动画结束后remove掉
            removeView((target));
        }
    }

    private AnimatorSet getEnterAnimtor(final View target) {
        ObjectAnimator translation = ObjectAnimator.ofFloat(target, View.TRANSLATION_Y, layoutHeight - 50, layoutHeight - (layoutHeight / 6));
        ObjectAnimator alpha = ObjectAnimator.ofFloat(target, View.ALPHA, 0.2f, 1f);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(target, View.SCALE_X, 0.2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(target, View.SCALE_Y, 0.2f, 1f);
        AnimatorSet enter = new AnimatorSet();
        enter.setDuration(500);
        enter.setInterpolator(new LinearInterpolator());
        enter.playTogether(translation, alpha, scaleX, scaleY);
        enter.setTarget(target);
        return enter;
    }

    private Animator getAnimator(ImageView target) {
        initInterpolators();
        PointF pointf = new PointF((layoutWidth - loveWidth) / 2, layoutHeight - (layoutHeight / 6));
        target.setX(pointf.x);
        target.setY(layoutHeight - 50);
        AnimatorSet set = getEnterAnimtor(target);

        bezierValueAnimator = getBezierValueAnimator(target, pointf);

        AnimatorSet finalSet = new AnimatorSet();
        //finalSet.playSequentially(set);
        finalSet.playTogether(set, bezierValueAnimator);
        finalSet.setInterpolator(interpolators[random.nextInt(4)]);//实现随机变速
        finalSet.setTarget(target);
        return finalSet;
    }

    private ValueAnimator getBezierValueAnimator(ImageView target, PointF pointF) {
        BezierEvaluator evaluator = new BezierEvaluator(getPointF(2), getPointF(1));
        //new PointF((layoutWidth-loveWidth)/2,0)
        //这里最好画个图 理解一下 传入了起点 和 终点
        PointF endPoint = new PointF(random.nextInt(getWidth()), 100);
        animator = ValueAnimator.ofObject(evaluator, pointF, endPoint);//随机
        animator.addUpdateListener(new BezierListenr(target));
        animator.setTarget(target);
        animator.setDuration(3500);
        return animator;
    }

    private PointF getPointF(int scale) {

        PointF pointF = new PointF();
        int px = layoutWidth - 100;
        int py = layoutHeight - 100;
        if(px<1)
            px = 1;
        if(py<1)
            py = 1;
        pointF.x = random.nextInt(px);
        //减去100 是为了控制 x轴活动范围,看效果 随意~~
        //再Y轴上 为了确保第二个点 在第一个点之上,我把Y分成了上下两半 这样动画效果好一些  也可以用其他方法
        int randomY = random.nextInt(py);
        pointF.y = randomY / scale;
        return pointF;
    }

    private int getRandom(int minVal, int maxVal) {
        return (int) (Math.random() * (maxVal - minVal + 1) + minVal);
    }


    public class BezierEvaluator implements TypeEvaluator<PointF> {


        private PointF mControlP1;//途径的两个点
        private PointF mControlP2;

        public BezierEvaluator(PointF pointF1, PointF pointF2) {
            this.mControlP1 = pointF1;
            this.mControlP2 = pointF2;
        }

        @Override
        public PointF evaluate(float time, PointF startValue,
                               PointF endValue) {

            float timeLeft = 1.0f - time;
            PointF point = new PointF();

            point.x = timeLeft * timeLeft * timeLeft * (startValue.x) + 3 * timeLeft * timeLeft * time *
                    (mControlP1.x) + 3 * timeLeft * time *
                    time * (mControlP2.x) + time * time * time * (endValue.x);

            point.y = timeLeft * timeLeft * timeLeft * (startValue.y) + 3 * timeLeft * timeLeft * time *
                    (mControlP1.y) + 3 * timeLeft * time *
                    time * (mControlP2.y) + time * time * time * (endValue.y);
            return point;
        }
    }

    private class BezierListenr implements ValueAnimator.AnimatorUpdateListener {

        private View target;

        public BezierListenr(View target) {
            this.target = target;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            //这里获取到贝塞尔曲线计算出来的的x y值 赋值给view 这样就能让爱心随着曲线走啦
            PointF pointF = (PointF) animation.getAnimatedValue();
            target.setX(pointF.x);
            target.setY(pointF.y);
            // 这里偷个懒,顺便做一个alpha动画,这样alpha渐变也完成啦
            target.setAlpha(1 - animation.getAnimatedFraction());
        }
    }
}
