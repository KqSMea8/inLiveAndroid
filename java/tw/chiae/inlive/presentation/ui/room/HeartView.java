package tw.chiae.inlive.presentation.ui.room;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.RelativeLayout;

import tw.chiae.inlive.presentation.ui.widget.roomanim.GitfSpecialsStop;
import tw.chiae.inlive.presentation.ui.widget.roomanim.MrlLove;
import tw.chiae.inlive.presentation.ui.widget.roomanim.PlaneImagerView;

/**
 * Created by rayyeh on 2017/4/3.
 */

public class HeartView extends RelativeLayout{

    private final Context context;
    private MrlLoveHeart mrlLove;
    private RoomFragment gitfSpecialsStop;
    private DisplayMetrics metrics;

    public HeartView(Context context) {
        super(context);
        this.context=context;
        thisAddView();
    }

    public HeartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        thisAddView();
    }

    private void thisAddView() {
        metrics = new DisplayMetrics();
        ((Activity)getContext()).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mrlLove=new MrlLoveHeart(context);

        LayoutParams layoutParamsLove= new LayoutParams(metrics.widthPixels,metrics.heightPixels);
        layoutParamsLove.addRule(ALIGN_PARENT_RIGHT, TRUE);
        mrlLove.setLayoutParams(layoutParamsLove);
        this.addView(mrlLove);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        598 286
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(metrics.widthPixels,metrics.heightPixels);
    }

/*    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        46 110 40   424,220
        Log.i("RayTest","onlayout");
        mrlLove.layout( 500,0,100,0);
    }*/

    public void setGitfSpecialsStop(RoomFragment gitfSpecialsStop) {
        this.gitfSpecialsStop = gitfSpecialsStop;
    }

    public void initAnim(int width) {
        this.setVisibility(VISIBLE);
        mrlLove.addFavor();
        //movethis(this,width);
    }

    private void movethis(HeartView view, int width) {
        //ObjectAnimator objectAnimator1=ObjectAnimator.ofFloat(view,"translationX",width,width/2-300);
        //ObjectAnimator objectAnimator2=ObjectAnimator.ofFloat(view,"translationY",0f,200f);
        final ObjectAnimator objectAnimator3=ObjectAnimator.ofFloat(view,"translationX",width/2-300,-600).setDuration(1500);
        final ObjectAnimator objectAnimator4=ObjectAnimator.ofFloat(view,"alpha",1f,1f).setDuration(4300);
        AnimatorSet set = new AnimatorSet();
//        注意这里最好别设置多种动画在同一个set了比如下面不注释第一个setplaytogether
//        同时执行
//        set.playTogether(objectAnimator1,objectAnimator2,objectAnimator3);
//        按顺序执行
//        set.playSequentially(objectAnimator1,objectAnimator2,objectAnimator3);
//        有特殊顺序的动画集合
        objectAnimator4.start();
        for (int i=0;i<1;i++){
            mrlLove.addFavor();
        }
        /*set.play(objectAnimator1).with(objectAnimator2);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                objectAnimator4.start();
                for (int i=0;i<40;i++){
                    mrlLove.addFavor();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });*/
        objectAnimator4.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                objectAnimator3.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        objectAnimator3.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                HeartView.this.setVisibility(GONE);
                if (gitfSpecialsStop!=null) {
                    gitfSpecialsStop.animend();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        set.setDuration(2000).start();
    }
}
