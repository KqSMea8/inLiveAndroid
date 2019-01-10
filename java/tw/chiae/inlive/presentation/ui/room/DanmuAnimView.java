package tw.chiae.inlive.presentation.ui.room;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.lang.ref.WeakReference;
import java.util.LinkedList;

import tw.chiae.inlive.R;
import tw.chiae.inlive.data.bean.websocket.UserPublicMsg;
import tw.chiae.inlive.data.repository.SourceFactory;
import tw.chiae.inlive.presentation.ui.room.pubmsg.MsgUtils;
import tw.chiae.inlive.util.Const;

/**
 * Created by rayyeh on 2017/8/31.
 */

public class DanmuAnimView extends LinearLayout{
    private static final int ANIM_END = 1;
    private Context mContext;
    //private boolean isRunning = false;
    private MsgUtils utils;
    private LinkedList<QueueItem> queueList;
    //private Animation danmuInAnim;
    //private Animation danmuOutAnim;
    private int parentHeight;
    private int Childwidth;
    private int Childheight;

    public DanmuAnimView(Context context) {
        super(context);
        this.mContext = context;
        initView();
    }

    public DanmuAnimView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    public DanmuAnimView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView();
    }


    private void initView() {
        if(isInEditMode())
            return;
        //isRunning = false;
        utils = MsgUtils.getInstance();
        setOrientation(VERTICAL);
        queueList =  new LinkedList<>();
        //danmuInAnim = AnimationUtils.loadAnimation(mContext, R.anim.danmu_in);
        //danmuOutAnim = AnimationUtils.loadAnimation(mContext, R.anim.danmu_out);
        DanMuHandler handler = new DanMuHandler(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        this.setMeasuredDimension(parentWidth, parentHeight);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    public void RunDanmuAnim(UserPublicMsg msg, String content) {

        RunAnim(msg.getAvatar(),msg.getContent(),msg.getFromClientName(),msg.getLevel());
        /*getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                Log.i("RayTest","parentHeight:"+parentHeight+" height:"+getHeight());
            }
        });*/
        //RunAnim(iType,msg.getAvatar(),content,msg.getFromClientName(),msg.getLevel());
    }

    private void RunAnim( String avatar, String content, String fromClientName, int level) {
        Log.i("RayTest","RunAnim");
        final View vDanMuView  = getView(avatar,content,fromClientName,level);
        if(vDanMuView==null)
            return;
        if(getChildCount()>3){
            QueueItem queueItem = new QueueItem();
            queueItem.setAvatar(avatar);
            queueItem.setQueueItemContent(content);
            queueItem.setName(fromClientName);
            queueItem.setLevel(level);
            queueList.addLast(queueItem);

        }else{

            final int index = getIndexCanAdd();
            Log.i("RayTest","can use "+index);

            if(index>=0){
                this.addView(vDanMuView,index);
                vDanMuView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Animation danmuInAnim = AnimationUtils.loadAnimation(mContext, R.anim.danmu_in);
                        Animation danmuOutAnim = AnimationUtils.loadAnimation(mContext, R.anim.danmu_out);
                        ViewGroup.LayoutParams mainLayoutParams = getLayoutParams();
                        Log.i("RayTest","vDanMuView mainLayoutParams:"+mainLayoutParams.width+" vDanMuView"+vDanMuView.getWidth());
                        int ConentLen = getViewAnimTime(vDanMuView);
                        vDanMuView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        danmuInAnim.setAnimationListener(new DanMuFadeInListener(vDanMuView,index));
                        vDanMuView.setVisibility(VISIBLE);
                        vDanMuView.startAnimation(danmuInAnim);
                    }
                });
            }

        }
    }

    private int getViewAnimTime(View vDanMuView) {

        Log.i("RayTest","vDanMuView width : "+vDanMuView.getWidth()+"");
        return 0;
    }

    private View getView( String avatar, String mContent, String UserName, int level) {
        View view = null;
        CharSequence content = "";
        CharSequence nameContent ="";
            view = LayoutInflater.from(mContext).inflate(R.layout.danmu_room_text_view,null);
            TextView tv_content = (TextView) view.findViewById(R.id.tv_danmu_content);
            TextView tv_name = (TextView) view.findViewById(R.id.tv_danmu_name);
            nameContent = utils.buildEnterEffectContent(UserName,tv_content.getLineHeight(),"",level, Color.WHITE);
            tv_name.setText(content);
            tv_content.setText(mContent);
            setupTextWidth(tv_content,nameContent);
        if(view!=null){
            SimpleDraweeView sd_head = (SimpleDraweeView) view.findViewById(R.id.sd_enter_head);
            sd_head.setImageURI(SourceFactory.wrapPathToUri(avatar));
        }
        //RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //view.setMinimumWidth(Childwidth);
        //view.setMinimumHeight(Childheight/4);
        return view;
    }

    private void setupTextWidth(TextView tv_content, CharSequence nameContent) {

    }

    public void setViewParams(int mDanmuAnimViewWidth, int mDanmuAnimViewHeight) {
        this.Childwidth = mDanmuAnimViewWidth;
        this.Childheight = mDanmuAnimViewHeight;
    }

    public int getIndexCanAdd() {
        for(int i = 0; i<=2 ; i++)
        {
            if(getChildAt(i)==null)
                return i;
        }
        return -1;
    }

    private class QueueItem {
        String avatar ;
        String QueueItemContent;
        String Name;
        int level;

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }

        public int getLevel() {
            if(level<=0)
                level = 1;
            if(level>= Const.MaxLevel)
                level = Const.MaxLevel;
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
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

    private class DanMuFadeInListener extends Animation implements Animation.AnimationListener {
        private int vIndex;
        private View vFadeInView;

        public DanMuFadeInListener(View vAnimView, int index) {
            this.vFadeInView = vAnimView;
            this.vIndex = index;
        }

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            //isRunning = false;

            removeChildIndex(vFadeInView);
          /*  if(vFadeInView!=null)
                removeViewAt(vIndex);*/
           /* QueueItem q = CheckQueueWait();
            if(q!=null){
                RunEnterAnim(q);
            }
            Log.i("RayTest","remove view:"+getChildCount());*/
        }

       /* private void RunEnterAnim(QueueItem q) {            removeView(v);

            Log.i("RayTest","RunEnterAnim1");
            if(Childwidth<=0 || Childheight<=0)
                return;
            RunAnim(q.getAvatar(),q.getQueueItemContent(),q.getName(),q.getLevel());
        }*/

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    private void removeChildIndex(final View vIndex) {

        new Handler().post(new Runnable() {

            @Override

            public void run() {

                if(vIndex!=null)
                    removeView(vIndex);
            }
        });

    }

    private QueueItem CheckQueueWait() {
        if(queueList.size()==0)
            return null;
        else
            return queueList.pop();
    }

    private class DanMuHandler extends Handler {
        private final WeakReference<DanmuAnimView> mDanmuAnimView;

        public DanMuHandler(DanmuAnimView danmuAnimView) {
            mDanmuAnimView = new WeakReference<DanmuAnimView>(danmuAnimView);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            DanmuAnimView danmu =  mDanmuAnimView.get();
            if(mDanmuAnimView!=null){
                switch (msg.what) {
                    case ANIM_END:

                }
            }
        }
    }
}
