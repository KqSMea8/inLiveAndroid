package tw.chiae.inlive.presentation.ui.room;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.LinkedList;

import tw.chiae.inlive.R;
import tw.chiae.inlive.data.bean.LoginInfo;
import tw.chiae.inlive.data.bean.websocket.SystemWelcome;
import tw.chiae.inlive.data.repository.SourceFactory;
import tw.chiae.inlive.presentation.ui.main.mergefilm.Log;
import tw.chiae.inlive.presentation.ui.room.pubmsg.MsgUtils;

/**
 * Created by rayyeh on 2017/8/30.
 */

public class EnterView extends RelativeLayout{
    private Context mContext;
    private int height;
    private int width;
    private boolean isRunning = false;
    private Animation enterInAnim;
    private Animation enterOutAnim;
    //private View vEnterView_effect1,vEnterView_effect2,vEnterView_effect3;
    //private SimpleDraweeView sd_head;
    //private TextView tv_content;
    private LinkedList<QueueItem> queueList;
    private MsgUtils utils;
    private int Textwidth;
    private int Childwidth;
    private int Childheight;


    public EnterView(Context context) {
        super(context);
        this.mContext=context;
        thisAddView();
    }

    public EnterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext=context;
        thisAddView();
    }


    public EnterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext=context;
        thisAddView();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        //this.setMeasuredDimension(parentWidth, parentHeight);
        this.setMeasuredDimension(parentWidth, parentHeight);
        super.onMeasure(parentWidth, heightMeasureSpec);
    }

    private void thisAddView() {
        if(isInEditMode())
            return;
        isRunning = false;
        utils = MsgUtils.getInstance();
        queueList =  new LinkedList<>();
        enterInAnim = AnimationUtils.loadAnimation(mContext, R.anim.enter_in);
        enterOutAnim = AnimationUtils.loadAnimation(mContext, R.anim.enter_out);
    }

    private void RunEnterAnim(final QueueItem q) {
        if(Childwidth<=0 || Childheight<=0)
            return;
        RunAnim(q.getType(), q.getAvatar(),q.getQueueItemContent(),q.getName(),q.getLevel());
    }

    private void RunAnim(final int iType, final String avatar, final String Content, final String name, final int level) {
        final View vEnterView  = getView(iType,avatar,Content,name,level);
        if(vEnterView==null)
            return;
        if(isRunning){
            QueueItem queueItem = new QueueItem();
            queueItem.setAvatar(avatar);
            queueItem.setQueueItemContent(Content);
            queueItem.setName(name);
            queueItem.setType(iType);
            queueItem.setLevel(level);
            queueList.addLast(queueItem);

        }else{
            isRunning = true;
            this.addView(vEnterView);
            vEnterView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    ViewGroup.LayoutParams mainLayoutParams = getLayoutParams();

                    vEnterView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    enterInAnim.setAnimationListener(new FadeInListener(vEnterView));
                    vEnterView.setVisibility(VISIBLE);
                    if(vEnterView.getMeasuredWidth()>Childwidth)
                    {
                        float radio = vEnterView.getMeasuredWidth()/Childwidth;
                        long newDuration = (long) (enterInAnim.getDuration()*radio);
                        Log.i("RayTest","修改秒數: "+newDuration);
                        enterInAnim.setDuration(newDuration);
                        enterOutAnim.setDuration(newDuration);
                    }
                    vEnterView.startAnimation(enterInAnim);
                }
            });
        }


    }

    private View getView(int iType,  String avatar,String Content ,String UserName,int level) {
        View view = null;
        CharSequence content = "";
        String nameContent ="";
        if(iType==1){
            view = LayoutInflater.from(mContext).inflate(R.layout.danmu_room_enter_effect1,null);
            TextView tv_content = (TextView) view.findViewById(R.id.tv_enter_content);
            nameContent = UserName+" 霸氣登場";
            content = utils.buildEnterEffectContent(nameContent,tv_content.getLineHeight(),"",level, Color.BLACK);
            tv_content.setText(content);
            setupTextWidth(tv_content,nameContent);
        }
        if(iType==2){
            view = LayoutInflater.from(mContext).inflate(R.layout.danmu_room_enter_effect2,null);

            TextView tv_content = (TextView) view.findViewById(R.id.tv_enter_content);
            nameContent = UserName+" 拉風登場";
            content = utils.buildEnterEffectContent(nameContent,tv_content.getLineHeight(),"",level,Color.BLACK);

            tv_content.setText(content);
            setupTextWidth(tv_content,nameContent);
        }
        if(iType==3){
            view = LayoutInflater.from(mContext).inflate(R.layout.danmu_room_enter_effect2,null);
            TextView tv_content = (TextView) view.findViewById(R.id.tv_enter_content);
            nameContent = UserName+" 霸氣拉風登場";
            content = utils.buildEnterEffectContent(nameContent,tv_content.getLineHeight(),"",level,Color.BLACK);
            tv_content.setText(content);
            setupTextWidth(tv_content,nameContent);
        }
        if(iType==4){
            view = LayoutInflater.from(mContext).inflate(R.layout.danmu_room_enter_effect3,null);
            TextView tv_content = (TextView) view.findViewById(R.id.tv_enter_content);
            nameContent = UserName+" 霸氣拉風登場霸氣拉風登場霸氣拉風登場霸氣拉風登場霸氣拉風登場霸氣拉風登場霸氣拉風登拉風登場霸氣拉風登場";
            content = utils.buildEnterEffectContent(nameContent,tv_content.getLineHeight(),"",level,Color.BLACK);
            tv_content.setText(content);
            setupTextWidth(tv_content,nameContent);
        }
        if(view!=null){
            SimpleDraweeView sd_head = (SimpleDraweeView) view.findViewById(R.id.sd_enter_head);
            sd_head.setImageURI(SourceFactory.wrapPathToUri(avatar));
            view.setMinimumWidth(Childwidth);
            view.setMinimumHeight(Childheight/4);
        }
        //RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        return view;
    }

    private void setupTextWidth(TextView tv_content, String nameContent) {
        Rect bounds = new Rect();
        Paint textPaint = tv_content.getPaint();
        textPaint.getTextBounds(nameContent, 0, nameContent.length(), bounds);
        Textwidth = bounds.width();
    }

    private QueueItem CheckQueueWait() {

        if(queueList.size()==0)
            return null;
        else
            return queueList.pop();
    }

    public void RunEnterAnim(int iType,LoginInfo userinfo,String content) {
        RunAnim(iType, userinfo.getAvatar(),content,userinfo.getNickname(),Integer.parseInt(userinfo.getLevel()));
    }


    public void RunEnterAnim(final int iType, final SystemWelcome systemMsg, final String content) {
        Log.i("RayTest","add Enter anum iType : "+iType);
        RunAnim(iType,systemMsg.getAvatar(),content,systemMsg.getClient_name(),systemMsg.getLevelid());

    }

    public void setViewParams(int mDanmuAnimViewWidth, int mDanmuAnimViewHeight) {
        this.Childwidth = mDanmuAnimViewWidth;
        this.Childheight = mDanmuAnimViewHeight;

    }

    private class QueueItem {
        String avatar ;
        String QueueItemContent;


        String Name;
        int type ;
        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        int level;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getQueueItemContent() {
            return QueueItemContent;
        }

        public void setQueueItemContent(String queueItemContent) {
            QueueItemContent = queueItemContent;
        }


    }

    private class FadeInListener extends Animation implements Animation.AnimationListener {
        private View vFadeInView;

        public FadeInListener(View vAnimView) {
            this.vFadeInView = vAnimView;
        }

        @Override
        public void onAnimationStart(Animation animation) {
            Log.i("RayTest","start anim.....");
        }

        @Override
        public void onAnimationEnd(Animation animation) {

            enterOutAnim.setAnimationListener(new FadeOutListener(vFadeInView));
            vFadeInView.startAnimation(enterOutAnim);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }
    private class FadeOutListener implements  Animation.AnimationListener{
        private final View vFadeOutView;

        public FadeOutListener(View vView) {
            this.vFadeOutView = vView;
        }

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            isRunning = false;
            if(vFadeOutView!=null)
                removeView(vFadeOutView);
            QueueItem q = CheckQueueWait();
            if(q!=null){

                RunEnterAnim(q);
            }else
                Log.i("RayTest","QueueItem null");

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }
}
