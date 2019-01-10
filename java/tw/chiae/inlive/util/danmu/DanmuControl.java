package tw.chiae.inlive.util.danmu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.text.style.ReplacementSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.w3c.dom.Text;

import master.flame.danmaku.danmaku.loader.ILoader;
import master.flame.danmaku.danmaku.loader.IllegalDataException;
import master.flame.danmaku.danmaku.loader.android.DanmakuLoaderFactory;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.android.AndroidDisplayer;
import master.flame.danmaku.danmaku.model.android.ViewCacheStuffer;
import master.flame.danmaku.danmaku.parser.IDataSource;
import master.flame.danmaku.ui.widget.DanmakuView;
import tw.chiae.inlive.R;
import tw.chiae.inlive.data.bean.Danmu;
import tw.chiae.inlive.data.bean.websocket.UserPublicMsg;
import tw.chiae.inlive.data.repository.SourceFactory;
import tw.chiae.inlive.presentation.ui.room.pubmsg.MsgUtils;
import tw.chiae.inlive.util.Const;
import tw.chiae.inlive.util.DpOrSp2PxUtil;
import tw.chiae.inlive.util.danmu.danmuview.CenteredImageSpan;
import tw.chiae.inlive.util.danmu.danmuview.CircleDrawable;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.BaseCacheStuffer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.model.android.SpannedCacheStuffer;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;

import static tw.chiae.inlive.R.id.view;

/**
 * Created by feiyang on 16/3/2.
 */
public class DanmuControl {

    private static final String TAG = "DanmuControl";

    //弹幕显示的时间(如果是list的话，会 * i)，记得加上mDanmakuView.getCurrentTime()
    private static final long ADD_DANMU_TIME = 2000;

    private static final int PINK_COLOR   = 0xffff5a93;//粉红 楼主
    private static final int ORANGE_COLOR = 0xffff815a;//橙色 我
    private static final int BLACK_COLOR  = 0xb2000000;//黑色 普通

    private int   BITMAP_WIDTH    = 40;//头像的大小
    private int   BITMAP_HEIGHT   = 40;
    private float DANMU_TEXT_SIZE = 11f;//弹幕字体的大小
//    private int   EMOJI_SIZE      = 14;//emoji的大小

    //这两个用来控制两行弹幕之间的间距
    private int DANMU_PADDING       = 8;
    private int DANMU_PADDING_INNER = 6;
    private int DANMU_RADIUS        = 15;//圆角半径

    private final int mGoodUserId = 1;
    private final int mMyUserId   = 2;

    private Context        mContext;
    private IDanmakuView mDanmakuView;
    private DanmakuContext mDanmakuContext;
    private MsgUtils utils;
    private boolean isAddDanMu = false;
    private LinkedList<DanmuQuene> queueList ;
    private int MaxVisibleCount = 3;
    private int runningDanmuNum = 0;
    private DanmuHandler danmuHandler;
    //private WorkerThread workerThread;
    private final int DANMU_ADD_QUEUE = 121;
    private Runnable danmuRunnable;
    private Thread danmuThread;

    public DanmuControl(Context context) {
        this.mContext = context;
        setSize(context);
        initDanmuConfig();
    }

    public void finishDanMuControl(){
        if (danmuHandler!=null){
            danmuHandler = null;
            if(danmuRunnable!=null)
            danmuRunnable = null;
            if(danmuThread!=null)
                danmuThread=null;
          /*  workerThread.exit();
            workerThread = null;*/
        }
    }
    /**
     * 对数值进行转换，适配手机，必须在初始化之前，否则有些数据不会起作用
     */
    private void setSize(Context context) {
        BITMAP_WIDTH = DpOrSp2PxUtil.dp2pxConvertInt(context, BITMAP_HEIGHT);
        BITMAP_HEIGHT = DpOrSp2PxUtil.dp2pxConvertInt(context, BITMAP_HEIGHT);
//        EMOJI_SIZE = DpOrSp2PxUtil.dp2pxConvertInt(context, EMOJI_SIZE);
        DANMU_PADDING = DpOrSp2PxUtil.dp2pxConvertInt(context, DANMU_PADDING);
        DANMU_PADDING_INNER = DpOrSp2PxUtil.dp2pxConvertInt(context, DANMU_PADDING_INNER);
        DANMU_RADIUS = DpOrSp2PxUtil.dp2pxConvertInt(context, DANMU_RADIUS);
        DANMU_TEXT_SIZE = DpOrSp2PxUtil.sp2px(context, DANMU_TEXT_SIZE);
    }

    /**
     * 初始化配置
     */
    private void initDanmuConfig() {
        // 设置最大显示行数
        utils = MsgUtils.getInstance();
        queueList = new LinkedList<>();
        HashMap<Integer, Integer> maxLinesPair = new HashMap<Integer, Integer>();
        Log.i("RayTest","滚动弹幕最大显示: " +MaxVisibleCount+" 行");
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, MaxVisibleCount); // 滚动弹幕最大显示2行
        // 设置是否禁止重叠
        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<Integer, Boolean>();
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);

        mDanmakuContext = DanmakuContext.create();
        mDanmakuContext.setDanmakuBold(true);
        mDanmakuContext
                //.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_NONE)
                .setDanmakuStyle(IDisplayer.DANMAKU_STYLE_NONE)
                .setDuplicateMergingEnabled(false)
                .setScrollSpeedFactor(ADD_DANMU_TIME/1000)//越大速度越慢 2.0
                .setScaleTextSize(1.2f)
                .preventOverlapping(overlappingEnablePair)
                .setCacheStuffer(new SpannedCacheStuffer(), mCacheStufferAdapter)
                .setMaximumLines(maxLinesPair)
                /*.preventOverlapping(overlappingEnablePair)
                .setCacheStuffer(new ViewCacheStuffer<DanMuViewHolder>() {
                    @Override
                    public DanMuViewHolder onCreateViewHolder(int viewType) {
                        return new DanMuViewHolder(View.inflate(mContext.getApplicationContext(), R.layout.danmu_room_text_view, null));
                    }

                    @Override
                    public void onBindViewHolder(int viewType, DanMuViewHolder viewHolder, BaseDanmaku danmaku, AndroidDisplayer.DisplayerConfig displayerConfig, TextPaint paint) {
                        Log.i("RayTest","onBindViewHolder");
                        if(danmaku.tag instanceof BitmapItem){

                            BitmapItem itemData = (BitmapItem) danmaku.tag;
                            Danmu danmu = itemData.getDanmu();
                            Log.i("RayTest","tag"+viewHolder.getMeasureWidth()+danmu.publicMsg.getFromClientName());
                            CircleDrawable circleDrawable = itemData.getCircleDrawable();
                            viewHolder.tv_name.setText(danmu.publicMsg.getFromClientName());
                            viewHolder.tv_content.setText(danmu.content);
                            viewHolder.sm_head.setImageDrawable(circleDrawable);

                        }

                    }

                    @Override
                    public void prepare(BaseDanmaku danmaku, boolean fromWorkerThread) {
                        Log.i("RayTest","prepare");
                        if (danmaku.isTimeOut()) {
                            return;
                        }

                        Log.i("RayTest","prepare"+danmaku.text.toString());
                    }


                    @Override
                    public void releaseResource(BaseDanmaku danmaku) {
                        if(danmaku.tag instanceof BitmapItem){
                            BitmapItem itemData = (BitmapItem) danmaku.tag;
                            if (itemData != null) {
                                UserPublicMsg msg = itemData.getDanmu().publicMsg;
                               Log.i("RayTest","releaseResource"+msg.getFromClientName()+" "+msg.getContent());
                            }
                            danmaku.setTag(null);

                        }

                    }
                },null)*/
                ;

    }

    /**
     * 绘制背景(自定义弹幕样式)
     */
    private float iconWidth;

    /*private class BackgroundCacheStuffer extends SpannedCacheStuffer {
        // 通过扩展SimpleTextCacheStuffer或SpannedCacheStuffer个性化你的弹幕样式
        final Paint paint = new Paint();

        @Override
        public void measure(BaseDanmaku danmaku, TextPaint paint, boolean fromWorkerThread) {
            Log.i("RayTest","measure");
//            danmaku.padding = 20;  // 在背景绘制模式下增加padding
            if (danmaku.text instanceof Spanned) {
                if (mProxy != null) {
                    mProxy.prepareDrawing(danmaku, fromWorkerThread);
                }

                CharSequence text = danmaku.text;
                if (text != null) {
                    StaticLayout staticLayout = new StaticLayout(text, paint, (int) StaticLayout.getDesiredWidth(danmaku.text, paint), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
                    int iconi=text.toString().indexOf(" ");
//                    头像的为了得到头像的宽度
                    CharSequence textimg=text.subSequence(0,iconi-1);
                    iconWidth=StaticLayout.getDesiredWidth(textimg, paint);
//                    后面的字符串
                    String iconStr=text.toString().substring(iconi+1,text.length());
//                    得到文字中间的:分割出名字和内容
                    int namei=iconStr.indexOf(":");

//                    然后得到名字的:符号
                    CharSequence textname=iconStr.toString().substring(0,namei+1);
//                    然后截取内容的字符串了
                    CharSequence textcontent=iconStr.toString().substring(namei+1,iconStr.length());
                    if (textcontent.length()==0){
//                        为了防止空弹幕
                        textcontent=" ";
                    }

                    StaticLayout staticLayouttop = new StaticLayout(textname,0,namei, paint, (int) StaticLayout.getDesiredWidth(textname, paint), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
                    StaticLayout staticLayoutdown = new StaticLayout(textcontent,namei+1,textcontent.length(), paint, (int) StaticLayout.getDesiredWidth(textcontent, paint), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
//                    对比名字和内容得出谁最长然后加上头像的长度
                    if (staticLayouttop.getWidth()>staticLayoutdown.getWidth()) {
                        danmaku.paintWidth = staticLayouttop.getWidth()+iconWidth+DANMU_PADDING_INNER;
                    }else {
                        danmaku.paintWidth = staticLayoutdown.getWidth()+iconWidth+DANMU_PADDING_INNER;
                    }
                    danmaku.paintHeight = staticLayouttop.getHeight()*2;
                    danmaku.obj = new SoftReference<>(staticLayout);

                    return;
                }
            }
            super.measure(danmaku, paint, fromWorkerThread);
        }

        @Override
        public void drawBackground(BaseDanmaku danmaku, Canvas canvas, float left, float top) {
            *//*paint.setAntiAlias(true);
            if (!danmaku.isGuest && danmaku.userId == mGoodUserId && mGoodUserId != 0) {
                paint.setColor(0x26000000);//粉红 楼主
            } else if (!danmaku.isGuest && danmaku.userId == mMyUserId
                    && danmaku.userId != 0) {
                paint.setColor(0x26000000);//橙色 我
            } else {
                paint.setColor(0x26000000);//黑色 普通
            }
            if (danmaku.isGuest) {//如果是赞 就不要设置背景
                paint.setColor(0x26000000);
            }
            canvas.drawRoundRect(new RectF(left + 40, top + DANMU_PADDING_INNER
                            , left + danmaku.paintWidth - DANMU_PADDING_INNER + 6,
                            top + danmaku.paintHeight - DANMU_PADDING_INNER + 6),//+6 主要是底部被截得太厉害了，+6是增加padding的效果
                    DANMU_RADIUS, DANMU_RADIUS, paint);*//*
        }

        @Override
        public void drawStroke(BaseDanmaku danmaku, String lineText, Canvas canvas, float left, float top, Paint paint) {
            // 禁用描边绘制
        }

        @Override
        public void drawText(BaseDanmaku danmaku, String lineText, Canvas canvas, float left, float top, TextPaint paint, boolean fromWorkerThread) {
            if (danmaku.obj == null) {
                super.drawText(danmaku, lineText, canvas, left, top, paint, fromWorkerThread);
                return;
            }
            SoftReference<StaticLayout> reference = (SoftReference<StaticLayout>) danmaku.obj;
            StaticLayout staticLayout = reference.get();
            StaticLayout staticLayout1=reference.get();;
            boolean requestRemeasure = 0 != (danmaku.requestFlags & BaseDanmaku.FLAG_REQUEST_REMEASURE);
            boolean requestInvalidate = 0 != (danmaku.requestFlags & BaseDanmaku.FLAG_REQUEST_INVALIDATE);

            if (requestInvalidate || staticLayout == null) {
                if (requestInvalidate) {
                    danmaku.requestFlags &= ~BaseDanmaku.FLAG_REQUEST_INVALIDATE;
                } else if (mProxy != null) {
                    mProxy.prepareDrawing(danmaku, fromWorkerThread);
                }
                CharSequence text = danmaku.text;
                if (text != null) {
                    if (requestRemeasure) {
                        danmaku.paintWidth = staticLayout.getWidth();
                        danmaku.paintHeight = staticLayout.getHeight();
                        danmaku.requestFlags &= ~BaseDanmaku.FLAG_REQUEST_REMEASURE;
                    } else {
                        staticLayout = new StaticLayout(text, paint, (int) danmaku.paintWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
                    }
                    danmaku.obj = new SoftReference<>(staticLayout);
                } else {
                    return;
                }
            }
            boolean needRestore = false;
            if (left != 0 && top != 0) {
                canvas.save();
                canvas.translate(left, top + paint.ascent());
                needRestore = true;
            }
//            这里先分出名字和头像
            if (danmaku.text!=null) {
//         这个是得到得到名字和内容中间的那个分割父
                int icon = danmaku.text.toString().indexOf(":");
//            先拆分出内容
                CharSequence str = danmaku.text.toString().substring(icon + 1, danmaku.text.length());
//              这个是上半部分
                paint.setColor(0xffff59a5);
                staticLayout = new StaticLayout(danmaku.text, 0, icon+1, paint, (int) StaticLayout.getDesiredWidth(danmaku.text, paint), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
                staticLayout.draw(canvas);
//            移动画布显示内容
                paint.setColor(0xffffffff);
                canvas.translate(iconWidth, staticLayout.getHeight());
                staticLayout1 = new StaticLayout(str, paint, (int) StaticLayout.getDesiredWidth(str, paint), Layout.Alignment.ALIGN_OPPOSITE, 1.0f, 0.0f, true);
                staticLayout1.draw(canvas);
                if (needRestore) {
                    canvas.restore();
                }
            }
        }
    }*/

    private BaseCacheStuffer.Proxy mCacheStufferAdapter = new BaseCacheStuffer.Proxy() {

        @Override
        public void prepareDrawing(final BaseDanmaku danmaku, boolean fromWorkerThread) {
//            if (danmaku.text instanceof Spanned) { // 根据你的条件检查是否需要需要更新弹幕
//            }
        }

        @Override
        public void releaseResource(BaseDanmaku danmaku) {
            // TODO 重要:清理含有ImageSpan的text中的一些占用内存的资源 例如drawable
            if (danmaku.text instanceof Spanned) {
                danmaku.text = "";
            }
            if(danmaku.tag instanceof BitmapItem){
                Log.i("RayTest","BitmapItem need release : "+danmaku.index);
                ((BitmapItem) danmaku.tag).bitmap.recycle();
                danmaku.tag = null;
                /*if(runningDanmuNum>0)
                    runningDanmuNum --;
                Log.i("RayTest","runningDanmuNum - 1 "+runningDanmuNum);
                if(danmuHandler!=null){
                    Message message = new Message();
                    message.what = DANMU_ADD_QUEUE;
                    danmuHandler.sendMessage(message);
                }*/

            }


        }
    };

    public void setDanmakuView(IDanmakuView danmakuView) {
        this.mDanmakuView = danmakuView;
        initDanmuView();
    }

    private void initDanmuView() {
        danmuHandler = new DanmuHandler();
        //workerThread = new WorkerThread();
        danmuRunnable  = new Runnable(){

            @Override
            public void run() {
                if(danmuHandler!=null){
                    runDanmu();
                    Message message = new Message();
                    message.what = DANMU_ADD_QUEUE;
                    danmuHandler.postDelayed(this, ADD_DANMU_TIME);
                }

            }
        };
        danmuThread = new Thread(danmuRunnable);
        danmuThread.start();
        if (mDanmakuView != null) {
            mDanmakuView.setCallback(new DrawHandler.Callback() {
                @Override
                public void prepared() {
                    mDanmakuView.start();
                    Log.i("RayDanmuView","DanmuView prepared");
                }

                @Override
                public void updateTimer(DanmakuTimer timer) {


                    if(!isAddDanMu)
                        pause();
                    //Log.i("RayDanmuView","DanmuView updateTimer : "+timer.currMillisecond+" "+mDanmakuView.getCurrentVisibleDanmakus());
                }

                @Override
                public void danmakuShown(BaseDanmaku danmaku) {
                    Log.i("RayDanmuView","DanmuView danmakuShown");
                    Log.i("RayDanmuView","DanmuView getCurrentVisibleDanmakus : "+mDanmakuView.getCurrentVisibleDanmakus());
                }

                @Override
                public void drawingFinished() {
                    Log.i("RayDanmuView","DanmuView drawingFinished");
                    pause();

                }
            });
        }
        //BaseDanmakuParser mParser = createParser(mContext.getResources().openRawResource(R.raw.comments));
         //mDanmakuView.prepare(mParser, mDanmakuContext);
        mDanmakuView.prepare(new BaseDanmakuParser() {

            @Override
            protected Danmakus parse() {
                return new Danmakus();
            }
        }, mDanmakuContext);
        mDanmakuView.enableDanmakuDrawingCache(true);


    }
    private BaseDanmakuParser createParser(InputStream stream) {

        if (stream == null) {
            return new BaseDanmakuParser() {

                @Override
                protected Danmakus parse() {
                    return new Danmakus();
                }
            };
        }

        ILoader loader = DanmakuLoaderFactory.create(DanmakuLoaderFactory.TAG_BILI);

        try {
            loader.load(stream);
        } catch (IllegalDataException e) {
            e.printStackTrace();
        }
        BaseDanmakuParser parser = new BiliDanmukuParser();
        IDataSource<?> dataSource = loader.getDataSource();
        parser.load(dataSource);
        return parser;

    }
    public void pause() {
        if (mDanmakuView != null && mDanmakuView.isPrepared()) {
            mDanmakuView.pause();
        }
    }

    public void hide() {
        if (mDanmakuView != null) {
            mDanmakuView.hide();
        }
    }

    public void show() {
        if (mDanmakuView != null) {
            mDanmakuView.show();
        }
    }

    public void resume() {
        if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isPaused()) {
            mDanmakuView.resume();
        }
    }

    public void destroy() {
        if (mDanmakuView != null) {
            mDanmakuView.release();
            mDanmakuView = null;
        }

        finishDanMuControl();
    }

    public void addDanmuList(final List<Danmu> danmuLists) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < danmuLists.size(); i++) {
                    addDanmu(danmuLists.get(i), i);
                }
            }
        }).start();
    }

    public void addDanmu(Danmu danmu, int i) {
        //workerThread.executeTask();
        Log.i("RayTest", "Queue Size: " + queueList.size() + "  runningDanmuNum:" + runningDanmuNum);
        DanmuQuene danmuQueue = new DanmuQuene();
        danmuQueue.setDanMu(danmu);
        danmuQueue.setTimer(i);
        queueList.addLast(danmuQueue);


        //int VisibleDanmakusCount = mDanmakuView.getCurrentVisibleDanmakus().getCollection().size();

/*        Log.i("RayTest","VisibleDanmakusCount:"+VisibleDanmakusCount);
        if(VisibleDanmakusCount<MaxVisibleCount)*/
/*        if (runningDanmuNum < MaxVisibleCount) {
            runningDanmuNum = runningDanmuNum + 1;
            runDanmu();
        }*/
    }

    private void runDanmu( ) {

        if(queueList.size()<=0){
            return;
        }

        DanmuQuene danmuQuene = queueList.pop();
        Danmu danmu = danmuQuene.getDanMu();
        int i = danmuQuene.getTimer();
        isAddDanMu = true;
        Log.i("RayTest","run .... danmu content: "+danmu.content);
        resume();
        BaseDanmaku danmaku = mDanmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);

        int id = Integer.parseInt(danmu.publicMsg.getUserId());
        if (id <= 0)
            return;
        Log.i("RayTest","runDanmu "+danmaku.index);
        danmaku.userId = id;
        danmaku.isGuest = danmu.type.equals("Like");//isGuest此处用来判断是赞还是评论
        CharSequence spannableName, spannableContent;
        Bitmap bitmap = getDefaultBitmap(danmu.avatarUrl);
        CircleDrawable circleDrawable = new CircleDrawable(mContext, bitmap, danmaku.isGuest);
        //circleDrawable.setBounds(0, 0, BITMAP_WIDTH, BITMAP_HEIGHT);
        //spannable = createSpannable(circleDrawable, danmu.content);
//        spannableName = createNameSpannable( danmu,BITMAP_WIDTH, BITMAP_HEIGHT);
        BitmapItem BitmapItem =  createNewDrawable(danmu,circleDrawable);
        // BitmapItem BitmapItem = new BitmapItem();

        //BitmapItem.setCircleDrawable(circleDrawable);
        //BitmapItem.setDanmu(danmu);
        spannableContent = createContentSpannable(BitmapItem.getDrawable(), BitmapItem.getWidth(), BitmapItem.getHeight());

        danmaku.text = TextUtils.concat("",spannableContent);
        //danmaku.text = danmu.publicMsg.getContent();
        danmaku.padding = DANMU_PADDING;
        danmaku.priority = 0;  // 1:一定会显示, 一般用于本机发送的弹幕,但会导致行数的限制失效
        danmaku.isLive = false;
        //danmaku.time = mDanmakuView.getCurrentTime() + (i * ADD_DANMU_TIME);
        danmaku.textSize = DANMU_TEXT_SIZE * (mDanmakuContext.getDisplayer().getDensity() - 0.6f);
        danmaku.textColor = Color.WHITE;
        danmaku.textShadowColor = 0; // 重要：如果有图文混排，最好不要设置描边(设textShadowColor=0)，否则会进行两次复杂的绘制导致运行效率降低
        danmaku.tag = BitmapItem;
        danmaku.setTime(mDanmakuView.getCurrentTime() + (i * ADD_DANMU_TIME));
        mDanmakuView.addDanmaku(danmaku);

    }
   private void runDanmu2(Danmu danmu, int i ) {


       isAddDanMu = true;
       Log.i("RayTest","danmu content: "+danmu.content);
       resume();
       BaseDanmaku danmaku = mDanmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
       int id = Integer.parseInt(danmu.publicMsg.getUserId());
       if (id <= 0)
           return;
       Log.i("RayTest","runDanmu ");
       danmaku.userId = id;
       danmaku.isGuest = danmu.type.equals("Like");//isGuest此处用来判断是赞还是评论
       CharSequence spannableName, spannableContent;
       Bitmap bitmap = getDefaultBitmap(danmu.avatarUrl);
       CircleDrawable circleDrawable = new CircleDrawable(mContext, bitmap, danmaku.isGuest);
       //circleDrawable.setBounds(0, 0, BITMAP_WIDTH, BITMAP_HEIGHT);
       //spannable = createSpannable(circleDrawable, danmu.content);
//        spannableName = createNameSpannable( danmu,BITMAP_WIDTH, BITMAP_HEIGHT);
       BitmapItem BitmapItem =  createNewDrawable(danmu,circleDrawable);
       // BitmapItem BitmapItem = new BitmapItem();

       //BitmapItem.setCircleDrawable(circleDrawable);
       //BitmapItem.setDanmu(danmu);
       spannableContent = createContentSpannable(BitmapItem.getDrawable(), BitmapItem.getWidth(), BitmapItem.getHeight());

       danmaku.text = TextUtils.concat("",spannableContent);
       //danmaku.text = danmu.publicMsg.getContent();
       danmaku.padding = DANMU_PADDING;
       danmaku.priority = 0;  // 1:一定会显示, 一般用于本机发送的弹幕,但会导致行数的限制失效
       danmaku.isLive = false;
       //danmaku.time = mDanmakuView.getCurrentTime() + (i * ADD_DANMU_TIME);
       danmaku.textSize = DANMU_TEXT_SIZE/* * (mDanmakuContext.getDisplayer().getDensity() - 0.6f)*/;
       danmaku.textColor = Color.WHITE;
       danmaku.textShadowColor = 0; // 重要：如果有图文混排，最好不要设置描边(设textShadowColor=0)，否则会进行两次复杂的绘制导致运行效率降低
       danmaku.tag = BitmapItem;
       danmaku.setTime(mDanmakuView.getCurrentTime() + (i * ADD_DANMU_TIME));
       mDanmakuView.addDanmaku(danmaku);

       Log.i("RayTest","runningDanmuNum + 1 "+runningDanmuNum);
   }

    private CharSequence createContentSpannable(Drawable danmu, int bitmap_width, int bitmap_height) {
        SpannableStringBuilder spannable=new SpannableStringBuilder("DrawableLayout");
        danmu.setBounds(0, 0, bitmap_width, bitmap_height);
        ImageSpan span = new ImageSpan(danmu, ImageSpan.ALIGN_BASELINE);
        spannable.setSpan(span, 0, "DrawableLayout".length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannable;
    }




    private BitmapItem createNewDrawable(Danmu danmu, CircleDrawable circleDrawable) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.danmu_room_text_view, null);
        TextView tv_name = (TextView) view.findViewById(R.id.tv_danmu_name);
        TextView tv_content = (TextView) view.findViewById(R.id.tv_danmu_content);
        ImageView sm_head = (ImageView) view.findViewById(R.id.sd_enter_head);
        tv_name.setText(utils.buildEnterEffectContent(danmu.publicMsg.getFromClientName(),tv_name.getLineHeight(),"",danmu.publicMsg.getLevel(),Color.WHITE));
        tv_content.setText(danmu.content);
        sm_head.setImageDrawable(circleDrawable);
        //sm_head.setImageURI(SourceFactory.wrapPathToUri(danmu.publicMsg.getAvatar()));
        BitmapItem mDrawable = ViewToDrawable(view);
        return mDrawable;
    }


    private BitmapItem ViewToDrawable(View view) {
        BitmapItem snapshot = cViewToBitmap(view);
        Drawable drawable = (Drawable) new BitmapDrawable(snapshot.getBitmap());
        snapshot.setDrawable(drawable);
        return snapshot;
    }

    private BitmapItem cViewToBitmap(View view) {
        view.setDrawingCacheEnabled(true);
        view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(),
                view.getMeasuredHeight());

        view.buildDrawingCache();
        BitmapItem bitmapItem = new BitmapItem();

        Bitmap bitmap = view.getDrawingCache();
        bitmapItem.setBitmap(bitmap);
        bitmapItem.setWidth(view.getMeasuredWidth());
        bitmapItem.setHeight(view.getMeasuredHeight());
        return bitmapItem;
    }

    private Bitmap getDefaultBitmap(Bitmap bitmap) {
        Bitmap mDefauleBitmap = null;

//        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), drawableId);
        if (bitmap != null) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            Log.d(TAG, "width = " + width);
            Log.d(TAG, "height = " + height);
            Matrix matrix = new Matrix();
            matrix.postScale(((float) BITMAP_WIDTH) / width, ((float) BITMAP_HEIGHT) / height);
            mDefauleBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
            Log.d(TAG, "mDefauleBitmap getWidth = " + mDefauleBitmap.getWidth());
            Log.d(TAG, "mDefauleBitmap getHeight = " + mDefauleBitmap.getHeight());
        }
        return mDefauleBitmap;
    }

    private SpannableStringBuilder createSpannable(Drawable drawable, String content) {
        String text = "bitmap";
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
        CenteredImageSpan span = new CenteredImageSpan(drawable);
        spannableStringBuilder.setSpan(span, 0, text.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        if (!TextUtils.isEmpty(content)) {
            spannableStringBuilder.append(" ");
            spannableStringBuilder.append(content.trim());
        }
        return spannableStringBuilder;
    }

    public void setDanmuViewHeigh(int mDanmuAnimViewHeight) {
        if(mDanmakuView==null || mDanmakuView.getConfig()==null)
            return;
        Log.i("RayTest","mDanmuAnimViewHeight: "+mDanmuAnimViewHeight + " BITMAP_HEIGHT:"+ BITMAP_HEIGHT);
        MaxVisibleCount = (int) mDanmuAnimViewHeight / (BITMAP_HEIGHT+11);
        HashMap<Integer, Integer> maxLinesPair = new HashMap<Integer, Integer>();
        Log.i("RayTest","滚动弹幕最大显示: " +MaxVisibleCount+" 行");
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, MaxVisibleCount);
        mDanmakuView.getConfig().setMaximumLines(maxLinesPair);

    }


    private class RoundedBackgroundSpan extends ReplacementSpan {
        @Override
        public int getSize(@NonNull Paint paint, CharSequence text, @IntRange(from = 0) int start, @IntRange(from = 0) int end, @Nullable Paint.FontMetricsInt fm) {
            return Math.round(measureText(paint, text, start, end));
        }

        @Override
        public void draw(@NonNull Canvas canvas, CharSequence text, @IntRange(from = 0) int start, @IntRange(from = 0) int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
            RectF rect = new RectF(x, top, x + measureText(paint, text, start, end), bottom);
            paint.setColor(Color.BLUE);
            canvas.drawRoundRect(rect, 100f, 30f, paint);
            paint.setColor(Color.WHITE);
            canvas.drawText(text, start, end, x, y, paint);
        }

        private float measureText(Paint paint, CharSequence text, int start, int end)
        {
            return paint.measureText(text, start, end);
        }
    }

    public class DanMuViewHolder extends ViewCacheStuffer.ViewHolder{

        private final TextView tv_name;
        private final TextView tv_content;
        private final ImageView sm_head;

        public DanMuViewHolder(View itemView) {
            super(itemView);
             tv_name = (TextView) itemView.findViewById(R.id.tv_danmu_name);
             tv_content = (TextView) itemView.findViewById(R.id.tv_danmu_content);
             sm_head = (ImageView) itemView.findViewById(R.id.sd_enter_head);
        }
    }

    private class BitmapItem {
        private Bitmap bitmap;
        private int width;
        private int height;
        private Drawable drawable;
        private CircleDrawable circleDrawable;
        private Danmu danmu;

        public void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getWidth() {
            return width;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getHeight() {
            return height;
        }

        public void setDrawable(Drawable drawable) {
            this.drawable = drawable;
        }

        public Drawable getDrawable() {
            return drawable;
        }

        public void RecycleItem (){
            bitmap.recycle();
        }

        public void setCircleDrawable(CircleDrawable circleDrawable) {
            this.circleDrawable = circleDrawable;
        }

        public CircleDrawable getCircleDrawable() {
            return circleDrawable;
        }

        public void setDanmu(Danmu danmu) {
            this.danmu = danmu;
        }

        public Danmu getDanmu() {
            return danmu;
        }
    }

    private class DanmuQuene {
        private int timer;
        private Danmu danMu;

        public void setTimer(int timer) {
            this.timer = timer;
        }

        public int getTimer() {
            return timer;
        }

        public void setDanMu(Danmu danMu) {
            this.danMu = danMu;
        }

        public Danmu getDanMu() {
            return danMu;
        }
    }


    private class DanmuHandler extends Handler{

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==DANMU_ADD_QUEUE){
                runDanmu();
            }
        }
    }

    /*private class WorkerThread extends Thread {
        protected static final String TAG = "WorkerThread";
        private Handler mHandler;
        private Looper mLooper;

        public WorkerThread() {
            start();
        }

        public void run() {
            Log.i("RayTest","WorkerThread run");
            if (Looper.myLooper() == null)
            {
                Looper.prepare();
            }
            mLooper = Looper.myLooper();
            Log.i("RayTest","WorkerThread get Looper");
            mHandler = new Handler(mLooper) {
                @Override
                public void handleMessage(Message msg) {
                    Log.i("RayTest","handleMessage");
                    Message message = Message.obtain();
                    message.what = DANMU_ADD_QUEUE;
                    danmuHandler.sendMessage(message);
                    SystemClock.sleep(1000);

                }
            };
            Looper.loop();
        }

        public void exit() {
            if (mLooper != null) {
                mLooper.quit();
                mLooper = null;
            }
        }

        // This method returns immediately, it just push an Message into Thread's MessageQueue.
        // You can also call this method continuously, the task will be executed one by one in the
        // order of which they are pushed into MessageQueue(they are called).
        public void executeTask() {
            Log.i("RayTest","executeTask !");
            if (mLooper == null || mHandler == null) {
                return;
            }
            Message msg = Message.obtain();
            msg.what = DANMU_ADD_QUEUE;
            Log.i("RayTest","executeTask sendMessage!");
            mHandler.sendMessage(msg);
        }
    }
*/
}
