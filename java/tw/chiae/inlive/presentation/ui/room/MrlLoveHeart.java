package tw.chiae.inlive.presentation.ui.room;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import tw.chiae.inlive.R;

import java.util.Random;

/**
 * Created by Administrator on 2016/8/9 0009.
 */
public class MrlLoveHeart extends RelativeLayout {
    private Context mConext;
    private Random random = new Random();//用于实现随机功能
    private int loveHeight;//爱心的高度
    private int loveWidth;//爱心的宽度
    private int layoutHeight;//FavorLayout的高度
    private int layoutWidth;//FavorLayout的宽度

    //定义一个LayoutParams 用它来控制子view的位置
    private LayoutParams lp;

    //private Drawable img1,img2,img3,img4,img5,img6,img7,img8,img9,img10,img11,img12,img13,img14,img15,img16,img17,img6,img7,img8,img9,img10,img11,img12,img1,img2,img3,img4,img5,img6,img7,img8,img9,img10,img11,img12;
    private Drawable[] drawables ;

    private Interpolator line = new LinearInterpolator();//线性
    private Interpolator acc = new AccelerateInterpolator();//加速
    private Interpolator dce = new DecelerateInterpolator();//减速
    private Interpolator accdec = new AccelerateDecelerateInterpolator();//先加速后减速
    // 在init中初始化
    private Interpolator[] interpolators ;
    private int MaxCount =29;
    private DisplayMetrics metrics;


    public MrlLoveHeart(Context context) {
        super(context);
        this.mConext = context;
        initDrawable();
        init();
    }

    public MrlLoveHeart(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mConext = context;
        //init里做一些初始化变量的操作
        initDrawable();
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //layoutHeight=getMeasuredHeight();
        //layoutWidth =getMeasuredWidth();
    }

    private void init() {
        //获取图的宽高 用于后面的计算
        //注意 我这里3张图片的大小都是一样的,所以我只取了一个

        metrics = new DisplayMetrics();
        ((Activity)getContext()).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        layoutWidth = metrics.widthPixels;
        layoutHeight = metrics.heightPixels;


        loveHeight =70;
        loveWidth = 70;

        // 初始化插补器
        interpolators = new Interpolator[4];
        interpolators[0] = line;
        interpolators[1] = acc;
        interpolators[2] = dce;
        interpolators[3] = accdec;

        //底部 并且 水平居中
        lp = new LayoutParams(loveHeight, loveWidth);
//        横向居中
        lp.addRule(ALIGN_PARENT_RIGHT, TRUE); //这里的TRUE 要注意 不是true
//        底部
        lp.addRule(ALIGN_PARENT_BOTTOM, TRUE);
        //好了,之后只要给子view设置LayoutParams就可以实现了
    }


    private void initDrawable() {
        //接下去我们初始化:
        //初始化显示的图片
        // int resId = context.getResources().getIdentifier("ic_level_" + info.getLevel(), "drawable", context.getPackageName());
        drawables = new Drawable[MaxCount];
        for(int index = 1; index<=MaxCount ; index++){
            Drawable drawable;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                drawables[index-1]  = getResources().getDrawable(getResourceID(index),null);
            }else{
                drawables[index-1]  = getResources().getDrawable(getResourceID(index));
            }

        }
    }

    private int getResourceID(int index) {
        return getContext().getResources().getIdentifier("planelw" + index, "drawable", getContext().getPackageName());
    }

    //我封装了一个方法  利用ObjectAnimator AnimatorSet来实现 alpha以及x,y轴的缩放功能
//target就是爱心
    private AnimatorSet getEnterAnimtor(final View target) {

        ObjectAnimator alpha = ObjectAnimator.ofFloat(target,View.ALPHA, 0.2f, 1f);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(target,View.SCALE_X, 0.2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(target,View.SCALE_Y, 0.2f, 1f);
        ObjectAnimator translationY = ObjectAnimator.ofFloat(target,View.TRANSLATION_Y, 0, -200f);
        AnimatorSet enter = new AnimatorSet();
        enter.setDuration(500);
        enter.setInterpolator(new LinearInterpolator());
        enter.playTogether(alpha,scaleX, scaleY,translationY);
        enter.setTarget(target);
        return enter;
    }

    public void addFavor() {
        ImageView imageView = new ImageView(getContext());
        //随机选一个
        imageView.setImageDrawable(drawables[random.nextInt(MaxCount)]);
        lp.setMargins(0,0,(int) layoutWidth-loveWidth,240);
        imageView.setLayoutParams(lp);
        addView(imageView);

        Animator set = getAnimator(imageView);
        set.addListener(new AnimEndListener(imageView));
        set.start();
    }

    private ValueAnimator getBezierValueAnimator(View target) {

        //初始化一个BezierEvaluator
        BezierEvaluator evaluator = new BezierEvaluator(getPointF(2),getPointF(1));

        //这里最好画个图 理解一下 传入了起点 和 终点
        int Xstart = (int) (layoutWidth*0.75);
        int Xend =  random.nextInt((int) (layoutWidth*0.75));
        PointF PointStart = new PointF(Xstart, layoutHeight-240);
        PointF PointEnd = new PointF(layoutWidth,(layoutHeight-500)/2);
        ValueAnimator animator = ValueAnimator.ofObject(evaluator,PointStart,PointEnd);//随机
        animator.addUpdateListener(new BezierListenr(target));
        animator.setTarget(target);
        animator.setDuration(8000);
        return animator;
    }

    private Animator getAnimator(View target){
        AnimatorSet set = getEnterAnimtor(target);

        ValueAnimator bezierValueAnimator = getBezierValueAnimator(target);

        AnimatorSet finalSet = new AnimatorSet();
        /*finalSet.playSequentially(set);
        finalSet.playSequentially(set, bezierValueAnimator);*/
        finalSet.play(bezierValueAnimator);
        finalSet.setInterpolator(interpolators[random.nextInt(4)]);//实现随机变速
        finalSet.setTarget(target);
        return finalSet;
    }

//这里涉及到另外一个方法:getPointF(),这个是我用来获取途径的两个点
// 这里的取值可以随意调整,调整到你希望的样子就好
    /**
     * 获取中间的两个 点
     * @param scale
     */
    private PointF getPointF(int scale) {

        PointF pointF = new PointF();
        pointF.x = random.nextInt((int) (layoutWidth - (layoutWidth-50)));//减去100 是为了控制 x轴活动范围,看效果 随意~~
        //再Y轴上 为了确保第二个点 在第一个点之上,我把Y分成了上下两半 这样动画效果好一些  也可以用其他方法
        pointF.y = random.nextInt((int) (layoutHeight - ((layoutHeight-loveHeight)*0.75)))/scale;

        return pointF;
    }

    //我们自定义一个BezierEvaluator 实现 TypeEvaluator
//由于我们view的移动需要控制x y 所以就传入PointF 作为参数,是不是感觉完全契合??
    public class BezierEvaluator implements TypeEvaluator<PointF> {


        private PointF pointF1;//途径的个点
        private PointF pointF2;
        public BezierEvaluator(PointF pointF1,PointF pointF2){
            this.pointF1 = pointF1;
            this.pointF2 = pointF2;
        }
        @Override
        public PointF evaluate(float time, PointF startValue,
                               PointF endValue) {

            float timeLeft = 1.0f - time;
            PointF point = new PointF();//结果

            PointF point0 = (PointF)startValue;//起点

            PointF point3 = (PointF)endValue;//终点
            //代入公式
            point.x = timeLeft * timeLeft * timeLeft * (point0.x)
                    + 3 * timeLeft * timeLeft * time * (pointF1.x)
                    + 3 * timeLeft * time * time * (pointF2.x)
                    + time * time * time * (point3.x);

            point.y = timeLeft * timeLeft * timeLeft * (point0.y)
                    + 3 * timeLeft * timeLeft * time * (pointF1.y)
                    + 3 * timeLeft * time * time * (pointF2.y)
                    + time * time * time * (point3.y);
            point.y = (point.y)-150;
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
            target.setAlpha(1-animation.getAnimatedFraction());
        }
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

}
