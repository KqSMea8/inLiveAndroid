package tw.chiae.inlive.presentation.ui.widget.roomanim;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ImageView;

import tw.chiae.inlive.R;

/**
 * Created by Administrator on 2016/8/8 0008.
 */
public class FireworksImagerView extends ImageView{
    private AnimationDrawable animationDrawable;
    private int animTime=-1;
    private Handler animHandler;
    private GitfSpecialsStop gitfSpecialsStop;
    private int  mWidth,mHight;
    public FireworksImagerView(Context context) {
        super(context);
    }

    public FireworksImagerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setVisibility(GONE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth=getMeasuredWidth();
        mHight=getMeasuredWidth();
        mWidth=mHight/388*413;
        setMeasuredDimension(mWidth,mHight);
    }

    @Override
    protected void onDraw(Canvas canvas) {

    }

    public void initAnim(){
        this.setVisibility(VISIBLE);
        onStartAnim();
        animHandler=new Handler();
        animHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onStopAnim();
            }
        },animTime);
    }

    public void onStartAnim(){
        this.setBackgroundResource(R.drawable.yanhua);
        animationDrawable= (AnimationDrawable) this.getBackground();
        if (animTime==-1){
            animTime=0;
            for (int i=0;i<animationDrawable.getNumberOfFrames();i++){
                animTime+=animationDrawable.getDuration(i);
            }
        }
        ObjectAnimator objectAnimator=ObjectAnimator.ofFloat(this,"alpha",0f,1f).setDuration(0);
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animationDrawable.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        objectAnimator.start();
    }
    public void onStopAnim(){
        ObjectAnimator objectAnimator=ObjectAnimator.ofFloat(this,"alpha",1f,0f).setDuration(500);
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animationDrawable.stop();
                animationDrawable=null;
                FireworksImagerView.this.setVisibility(GONE);
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
        objectAnimator.start();
    }

    public void setGitfSpecialsStop(GitfSpecialsStop gitfSpecialsStop) {
        this.gitfSpecialsStop = gitfSpecialsStop;
    }
}
