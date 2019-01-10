package tw.chiae.inlive.presentation.ui.main.mergefilm;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.download.DownloadListener;
import com.yolanda.nohttp.download.DownloadRequest;
import com.yolanda.nohttp.error.NetworkError;
import com.yolanda.nohttp.error.ServerError;
import com.yolanda.nohttp.error.StorageReadWriteError;
import com.yolanda.nohttp.error.StorageSpaceNotEnoughError;
import com.yolanda.nohttp.error.TimeoutError;
import com.yolanda.nohttp.error.URLError;
import com.yolanda.nohttp.error.UnKnownHostError;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import tw.chiae.inlive.R;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.nohttp.CallServer;
import tw.chiae.inlive.presentation.ui.main.mergefilm.model.DeviceUtils;
import tw.chiae.inlive.presentation.ui.main.mergefilm.model.FileUtils;
import tw.chiae.inlive.presentation.ui.main.mergefilm.model.MediaObject;
import tw.chiae.inlive.presentation.ui.main.mergefilm.model.StringUtils;
import tw.chiae.inlive.presentation.ui.widget.CustomToast;
import tw.chiae.inlive.presentation.ui.widget.lrcview.LrcView;


/**
 * 视频录制
 *
 * @author tangjun@yixia.com 感謝原作者的貢獻 在下李龍龍 抱拳了！
 */
public class DaYu2MediaRecorderActivity extends AppCompatActivity implements MediaRecorderBase.OnErrorListener, OnClickListener, MediaRecorderBase.OnPreparedListener,
        MediaRecorderBase.OnEncodeListener, MediaRecorderBase.OnMergeListener {
    private static int _starID;
    /**
     * 录制最长时间
     */
    public int RECORD_TIME_MAX = 1;
    /**
     * 录制最小时间
     */
    public final static int RECORD_TIME_MIN = 0;
    /**
     * 刷新进度条
     */
    private static final int HANDLE_INVALIDATE_PROGRESS = 0;
    /**
     * 延迟拍摄停止
     */
    private static final int HANDLE_STOP_RECORD = 1;
    /**
     * 对焦
     */
    private static final int HANDLE_HIDE_RECORD_FOCUS = 2;
    /**
     * 下一步
     */
    private ImageView mTitleNext,iv_back;
    /**
     * 对焦图标-带动画效果
     */
    private ImageView mFocusImage;
    /**
     * 前后摄像头切换
     */
    private ImageView mCameraSwitch;
    /**
     * 回删按钮、延时按钮、滤镜按钮
     */
    private CheckedTextView mRecordDelete;
    /**
     * 闪光灯
     */
    private CheckBox mRecordLed;
    /**
     * 拍摄按钮
     */
    private ImageView mRecordController;
    /**
     * 底部条
     */
    private RelativeLayout mBottomLayout;
    /**
     * 摄像头数据显示画布
     */
    private SurfaceView mSurfaceView;
    /**
     * 录制进度
     */
    private ProgressView mProgressView;
    /**
     * 对焦动画
     */
    private Animation mFocusAnimation;
    /**
     * SDK视频录制对象
     */
    private DaYuMediaRecorderSystem mMediaRecorder;
    /**
     * 视频信息
     */
    private MediaObject mMediaObject;
    /**
     * 需要重新编译（拍摄新的或者回删）
     */
    private boolean mRebuild;
    /**
     * on
     */
    private boolean mCreated;
    /**
     * 是否是点击状态
     */
    private volatile boolean mPressedStatus;
    /**
     * 是否已经释放
     */
    private volatile boolean mReleased;
    /**
     * 对焦图片宽度
     */
    private int mFocusWidth;
    /**
     * 底部背景色
     */
    private int mBackgroundColorNormal, mBackgroundColorPress;
    /**
     * 屏幕宽度
     */
    private int mWindowWidth;
    /**
     * mv播放控件
     */
    private MvVideoView mVideoView;
    /**
     * 正在下載視頻源
     */
    private static final int RECORDING_LOADING_STATE = 21225;
    /**
     * 第一次開始 直接清空進度
     */
    private static final int RECORDING_FRIST_STATE = 21229;
    /**
     * 已經準備好了隨時可以錄製
     */
    private static final int RECORDING_READY_STATE = 21226;
    /**
     * 暫停
     */
    private static final int RECORDING_RECORDING_STATE = 21227;
    /**
     * 時間結束了
     */
    private static final int VIDEO_TIME_OVER = 21230;
    /**
     * 進入了觀看合成視頻的的那個狀態
     */
    private static final int VIDEO_MEGER_PLAY = 21488;
    /**
     * 狀態記錄
     */
    private int recording_state = 0;
    /**
     * 因為android特喵這個有毒 這裡做個那個預覽圖
     */
    private ImageView onpauseimg;
    /***
     * 記錄控件的匡高
     */
    private int width, height;
    /**
     * 歌詞
     */
    private LrcView mLrc;
    /**
     * loading
     */
    protected ProgressDialog mProgressDialog;
    /**
     * 計時器的結束
     */
    private boolean isend;
    /**
     * 歌詞下載到的路徑
     */
    private String lrcPath;
    /**
     * 下載到的視頻路徑
     */
    private String downloadPath;
    /**
     * 下载请求.
     */
    private DownloadRequest mDownloadRequest;
    /**
     * 下载请求歌詞
     */
    private DownloadRequest mDownloadRequestLrc;
    /**
     * 開始觀看合併的視頻按鈕
     */
    private ImageView mPlayMergeMv;
    /**
     * 合成出來的視頻地址
     */
    private String playMegerMvPath;
    /**
     * 你猜猜這個噶啥子的
     */
    public static int SET_RESULT = 0x642;
    /**
     * 你猜猜這個呢？
     */
    public static String SET_RESULT_MEGERPATH = "lilonglongtest";
    /**
     * 提交作品
     */
    private ImageView mFinishResult;
    /**
     * 返回提交 停止一些事情
     */
    private boolean isfinish;
    /**
     * 動漫
     */
    private LaodingDilog mLoadingDilog;
    /**
     * 合成的播放控件  這樣的做法絕對不是小咖秀的做法。。。
     */
    private MvVideoView mTestMergeVideo;
    /**
     * 因為播放如果暫停退出後台 會黑鳥
     */
    private ImageView mTestMergePauseimg;
    /**
     * 倒數動畫偷懶做
     */
    private ImageView startAnimImg;
    /**
     * 及時handle
     */
    private final static int TIME_SEND_TWO = 0x8494;
    private final static int TIME_SEND_THREE = 0x8412;
    private final static int TIME_SEND_FOUR = 0x8444;
    /**
     * 節約資源消耗 共用time
     */
    private boolean isStartAnim;
    public static final String VEDIOURL = "mvURL";
    public static final String LRCURL = "rlcURL";
    public static final String STAR_ID = "starId";
    private static final String UPLOADURL = "uploadURL";
    private static final String EXTRAINFO = "extrainfo";
    private boolean isPuase;

    private boolean MediaRecorderDebugMode = true;
    private MediaPlayer _MediaPlayer;
    private RelativeLayout mBottomLayout_result;
    private ImageView recoding_re;
    private boolean isFirstInit = false;
    private int MV_Width;
    private int MV_Height;
    private boolean isRecordComplete = false;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("RayTest","Finish");

        if(requestCode== KaraStar.REQUEST_VIDEO_RECORD ){
            if(resultCode==RESULT_OK){
                setResult(RESULT_OK, data);
                Log.i("RayTest", "Finish2:" + RESULT_OK);
                finish();
            }else {
                setResult(RESULT_CANCELED, data);
                finish();
            }
        }else
            super.onActivityResult(requestCode, resultCode, data);
    }

    public static Intent createIntent(Context context, String videoUrl, String lrcUrl, int userid, String uploadUrl, String extraInfo) {
        //_starID = starID;
        Intent it = new Intent(context, DaYu2MediaRecorderActivity.class);
        it.putExtra(VEDIOURL,videoUrl);
        it.putExtra(LRCURL,lrcUrl);
        it.putExtra(STAR_ID,userid);
        it.putExtra(UPLOADURL,uploadUrl);
        it.putExtra(EXTRAINFO,extraInfo);
        return it;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_recorder_dayu);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 防止锁屏
        loadIntent();
        loadViews();
    }

    /**
     * 加载传入的参数
     *
     *     public static final String MV_BT_PATH = "mvBtPath";
     public static final String LRC_BT_PATH = "lrcBtPath";
     public static final String STAR_ID = "starId";
     */
    private void loadIntent() {
        _starID = getIntent().getIntExtra(STAR_ID,-1);
        mWindowWidth = DeviceUtils.getScreenWidth(this);
        mFocusWidth = ConvertToUtils.dipToPX(this, 64);
        mBackgroundColorNormal = getResources().getColor(R.color.black);// camera_bottom_bg
        mBackgroundColorPress = getResources().getColor(R.color.camera_bottom_press_bg);
    }

    /**
     * 加载视图
     */
    private void loadViews() {
        mSurfaceView = (SurfaceView) findViewById(R.id.record_preview);
        mCameraSwitch = (ImageView) findViewById(R.id.record_camera_switcher);
        mTitleNext = (ImageView) findViewById(R.id.title_next);
        mFocusImage = (ImageView) findViewById(R.id.record_focusing);
        mProgressView = (ProgressView) findViewById(R.id.record_progress);
        mRecordDelete = (CheckedTextView) findViewById(R.id.record_delete);
        mRecordController = (ImageView) findViewById(R.id.record_controller);
        mBottomLayout = (RelativeLayout) findViewById(R.id.bottom_layout);
        mBottomLayout_result = (RelativeLayout) findViewById(R.id.bottom_layout_result);
        mRecordLed = (CheckBox) findViewById(R.id.record_camera_led);
        mVideoView = (MvVideoView) findViewById(R.id.film_view);
        onpauseimg = (ImageView) findViewById(R.id.onpauseimg);
        mLrc = (LrcView) findViewById(R.id.lrc_small);
        mPlayMergeMv = (ImageView) findViewById(R.id.start_mergemv);
        mFinishResult = (ImageView) findViewById(R.id.finish_result);
        mTestMergeVideo = (MvVideoView) findViewById(R.id.test_merge_video);
        mTestMergePauseimg = (ImageView) findViewById(R.id.test_merge_pauseimg);
        startAnimImg = (ImageView) findViewById(R.id.start_anim);
        recoding_re = (ImageView) findViewById(R.id.iv_recoding_re);

        //mBottomLayout_result.setBackgroundColor(mBackgroundColorPress);
        // ~~~ 绑定事件
        if (DeviceUtils.hasICS())
            mSurfaceView.setOnTouchListener(mOnSurfaveViewTouchListener);

        mTitleNext.setOnClickListener(this);
        //findViewById(R.id.imgbtn_toolbar_back).setOnClickListener(this);

        iv_back = (ImageView) findViewById(R.id.iv_media_recorder_back);
        iv_back.setOnClickListener(this);
        mRecordDelete.setOnClickListener(this);
        mRecordController.setOnClickListener(this);
        mPlayMergeMv.setOnClickListener(this);
        mFinishResult.setOnClickListener(this);
        recoding_re.setOnClickListener(this);
        initVideoView();
        // 是否支持前置摄像头
        if (MediaRecorderBase.isSupportFrontCamera()) {
            mCameraSwitch.setOnClickListener(this);
        } else {
            mCameraSwitch.setVisibility(View.GONE);
        }
        // 是否支持闪光灯
        if (DeviceUtils.isSupportCameraLedFlash(getPackageManager())) {
            mRecordLed.setOnClickListener(this);
        } else {
            mRecordLed.setVisibility(View.GONE);
        }

        try {
            mFocusImage.setImageResource(R.drawable.video_focus);
        } catch (OutOfMemoryError e) {
            Log.e("mrl", e.toString());
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                initSurfaceView();
                initVideoViewMerge();

                initplayerNetworkMV();
                if (getNetWorkType(getApplicationContext())) {

                    downLoadMv(getMVPath());
                    downLoadLrc(getLrcPath());
                } else {
                    showYesidoDialog();
                }
            }
        });

    }

    private String getLrcPath() {
        String lrcPath = getIntent().getStringExtra(LRCURL);
        return lrcPath;
    }

    private String getMVPath() {
        String mvPath = getIntent().getStringExtra(VEDIOURL);
        //return "https://dl.dropboxusercontent.com/s/4i3fk6gjs26ftf2/stars-003.mp4";
        return mvPath;
    }

    /**
     * 這裡沒做緩存 所以模板發啊。。。只能搞一個嘗試加載網絡視頻
     */

    private void adjustAspectRatio(int videoWidth, int videoHeight) {
        int viewWidth = mVideoView.getWidth();
        int viewHeight = mVideoView.getHeight();
        double aspectRatio = (double) videoHeight / videoWidth;

        int newWidth, newHeight;
        if (viewHeight > (int) (viewWidth * aspectRatio)) {
            // limited by narrow width; restrict height
            newWidth = viewWidth;
            newHeight = (int) (viewWidth * aspectRatio);
        } else {
            // limited by short height; restrict width
            newWidth = (int) (viewHeight / aspectRatio);
            newHeight = viewHeight;
        }
        int xoff = (viewWidth - newWidth) / 2;
        int yoff = (viewHeight - newHeight) / 2;
        Log.v("RayTest", "video=" + videoWidth + "x" + videoHeight +
                " view=" + viewWidth + "x" + viewHeight +
                " newView=" + newWidth + "x" + newHeight +
                " off=" + xoff + "," + yoff);
        MV_Width = newWidth;
        MV_Height = newHeight;

        Matrix txform = new Matrix();
        mVideoView.getTransform(txform);
        txform.setScale((float) newWidth / viewWidth, (float) newHeight / viewHeight);
        //txform.postRotate(10);          // just for fun
        txform.postTranslate(xoff, yoff);
        mVideoView.setTransform(txform);
    }

    private void initplayerNetworkMV() {
        try {
            mVideoView.setDataSource(getMVPath());
            mVideoView.setVolume(0, 0);
            mVideoView.setLooping(false);
            mVideoView.prepareAsync(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Log.i("RayTest","onPrepared");

                    //mp.start();
                    mp.seekTo(0);
                }
            });

            mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
//                    toastShort("網絡傲嬌了，請等待視頻下載");
                    Log.i("mrl", "網絡傲嬌了，請等待視頻下載");
                    return false;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
//            toastShort("網絡傲嬌了，請等待視頻下載");
            Log.i("mrl", "網絡傲嬌了，請等待視頻下載");
        }
    }

    /**
     * 初始化視頻 網絡加載的
     */
    private void initplayerMV() {
        if (mVideoView == null||downloadPath==null)
            return;
//        recording_state = RECORDING_LOADING_STATE;
        try {
            mVideoView.setDataSource(downloadPath);
            mVideoView.setVolume(0.1f, 0.1f);
            mVideoView.setLooping(false);
            mVideoView.prepare(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    if (mVideoView!=null&&!isPuase){
                        fristStartMv(mp);
                    }
                }
            });
            mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    //toastShort(" 視頻解析出錯了(1)");
                    Log.i("RayTest"," 視頻解析出錯了(1)");
                    hideProgress();
                    return false;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            toastShort("視頻解析出錯了(2)");
            hideProgress();
        }
    }

    public int getToolBarHeight() {
        int[] attrs = new int[] {R.attr.actionBarSize};
        TypedArray ta = obtainStyledAttributes(attrs);
        int toolBarHeight = ta.getDimensionPixelSize(0, -1);
        ta.recycle();
        return toolBarHeight;
    }

    /**
     * 初始化畫布,用來切換大小
     */
    private void initVideoView() {
        final RelativeLayout top_layout = (RelativeLayout) findViewById(R.id.top_mv_latout);
        final int w = DeviceUtils.getScreenWidth(this);
        final int h = DeviceUtils.getScreenHeight(this)-getToolBarHeight();

        top_layout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                top_layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                RelativeLayout.LayoutParams layout_lp = (RelativeLayout.LayoutParams) top_layout.getLayoutParams();
                //layout_lp.height = w;
                //top_layout.setLayoutParams(layout_lp);
            }
        });



        //RelativeLayout top_layout = (RelativeLayout) findViewById(R.id.top_mv_latout);
        //RelativeLayout.LayoutParams layout_lp = (RelativeLayout.LayoutParams) top_layout.getLayoutParams();

        int width = w/2;
        int height = w ;
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mVideoView
                .getLayoutParams();
        lp.width = width ;
        lp.height = height;
        RelativeLayout.LayoutParams surfaceLayoutParam = (RelativeLayout.LayoutParams) mSurfaceView.getLayoutParams();
        float ratio = (float)height / (float) h ;
        surfaceLayoutParam.height = height;
        float surfceWidth= (float)w *ratio;
        surfaceLayoutParam.width = (int) surfceWidth;
        int left = (int) surfceWidth- (w-(w/2));
        surfaceLayoutParam.leftMargin = left;

surfaceLayoutParam.rightMargin = -1*left;

        mSurfaceView.setLayoutParams(surfaceLayoutParam);
        Log.i("RayTest","w:"+surfaceLayoutParam.width+" h:"+surfaceLayoutParam.height+" "+ (int) surfceWidth+" left:"+left);
        //this.width = lp.width;
        //this.height = lp.height;
        mVideoView.setLayoutParams(lp);



        RelativeLayout.LayoutParams layout_lp = (RelativeLayout.LayoutParams) top_layout.getLayoutParams();
        //layout_lp.height = w;
        Log.i("RayTest","h:"+height+" w:"+w);
        //top_layout.setLayoutParams(layout_lp);

        //adjustAspectRatio(width,height);

        onpauseimg.setLayoutParams(lp);
        //onpauseimg.setBackgroundColor(Color.BLACK);
    }

    /**
     * 用來演示的
     */
    private void initVideoViewMerge() {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mSurfaceView.getLayoutParams();
        mTestMergeVideo.setLayoutParams(lp);
        mTestMergePauseimg.setLayoutParams(lp);
        mTestMergePauseimg.setVisibility(View.INVISIBLE);
    }

    /**
     * 初始化画布
     */
    private void initSurfaceView() {
        final int w = DeviceUtils.getScreenWidth(this);

        RelativeLayout.LayoutParams layoutParams;
        layoutParams = (RelativeLayout.LayoutParams) mBottomLayout.getLayoutParams();


        layoutParams.topMargin = w;

        Log.i("RayTest","mBottomLayout:"+ layoutParams.topMargin);
        int width = w;
        int height = w * 4 / 3;
        //
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mSurfaceView.getLayoutParams();
        //lp.leftMargin = w*1/3;
        //lp.rightMargin = -1*w*1/3;

        Log.i("RayTest","video height:"+mVideoView.getHeight());
        //lp.width = width;
        //lp.height = height;
        mSurfaceView.setLayoutParams(lp);

    }

    /**
     * 初始化拍摄SDK
     */
    private void initMediaRecorder() {
        String key = "DaYu_rec_"+_starID;
        mMediaRecorder = new DaYuMediaRecorderSystem(this);
        mRebuild = true;

        mMediaRecorder.setOnErrorListener(this);
        mMediaRecorder.setOnEncodeListener(this);
        mMediaRecorder.setOnMergeListener(this);
        String FilePath = VCamera.getVideoCachePath() + key + "/"+getFileName();
        Log.i("RayTest","Path1:"+FilePath+" :"+ FileUtils.checkFile(FilePath)+" ");
        File f = new File(VCamera.getVideoCachePath()+ key );
        if (!FileUtils.checkFile(f)) {
            f.mkdirs();
        }
        String[] _fileTmp = getFileName().split(".mp4");
        String fileName = _fileTmp[_fileTmp.length-1];
        mMediaObject = mMediaRecorder.setOutputDirectory(key, VCamera.getVideoCachePath() + key+"/"+fileName);
        mMediaRecorder.setSurfaceHolder(mSurfaceView.getHolder());
        mMediaRecorder.prepare();



    }

    private String getFileName (){
        String[] fileName = getIntent().getStringExtra(VEDIOURL).split("/");
        return fileName[fileName.length - 1];
    }
    /**
     * 重新初始化錄像文件
     */
    private void repMediaRecorder() {
        File f = new File(VCamera.getVideoCachePath());
        if (!FileUtils.checkFile(f)) {
            f.mkdirs();
        }
        String key = "DaYu_rec_"+_starID;
        /*mMediaObject = mMediaRecorder.setOutputDirectory(key, VCamera.getVideoCachePath() + key);*/
        String[] fileName = getFileName().split(".mp4");
        mMediaObject = mMediaRecorder.setOutputDirectory(key, VCamera.getVideoCachePath() + key+"/"+fileName[fileName.length - 1]);
        mMediaRecorder.setSuccessPart(0);
        mProgressView.setData(mMediaObject);
    }

    /**
     * 視頻加載完成的第一次播放 ，例如小咖秀的那個
     */
    private void fristStartMv(MediaPlayer mp) {
        _MediaPlayer = mp ;
        RECORD_TIME_MAX = mp.getDuration() - 50;
        mProgressView.setMaxDuration(RECORD_TIME_MAX);
        if (recording_state==0) {
            recording_state = RECORDING_FRIST_STATE;
        }
        //mp.start();
        mp.seekTo(0);
        mRecordController.setVisibility(View.VISIBLE);
        mRecordController.setImageResource(R.drawable.recording_ready);
        hideProgress();
        //onpauseimg.setImageBitmap(createVideoThumbnail(downloadPath, mVideoView.getVideoWidth(), mVideoView.getVideoHeight()));
        onpauseimg.setVisibility(View.VISIBLE);
    }

    /**
     * 点击屏幕录制
     */
    private View.OnTouchListener mOnSurfaveViewTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (mMediaRecorder == null || !mCreated) {
                return false;
            }

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // 检测是否手动对焦
                    if (checkCameraFocus(event))
                        return true;
                    break;
            }
            return true;
        }

    };

    @Override
    public void onResume() {
        super.onResume();
        isPuase=false;
        Log.i("mrl", "onResume"+recording_state);
        if (mMediaRecorder == null) {
            initMediaRecorder();
            Log.i("RayTest","Check File");
            String key = "DaYu_rec_"+_starID;
            //return VCamera.getVideoCachePath()+itData.getStringExtra(MV_NAME)+".mp4";
            String[] filePath =  getFileName().split(".mp4");
            /*final String path = VCamera.getVideoCachePath() + key+"/"+filePath[filePath.length-1];*/
            final String path = VCamera.getVideoCachePath() + key+"/"+getFileName();
            if(FileUtils.checkFile(path)){
                Log.i("RayTest","存在 "+path);
               /* mTestMergePauseimg.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mTestMergePauseimg.setImageBitmap(createVideoThumbnail(path, mTestMergeVideo.getVideoWidth(), mTestMergeVideo.getVideoHeight()));
                        mPlayMergeMv.setVisibility(View.VISIBLE);
                        mBottomLayout.setVisibility(View.INVISIBLE);
                        mBottomLayout_result.setVisibility(View.VISIBLE);
                        mSurfaceView.setVisibility(View.INVISIBLE);
                    }
                });*/

            }else{
                Log.i("RayTest","不存在"+path);

            }
        } else {
            mRecordLed.setChecked(false);
            mMediaRecorder.prepare();
            mProgressView.setData(mMediaObject);
            if (recording_state==RECORDING_READY_STATE)
            initplayerMV();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isPuase=true;
        Log.i("mrl","onPause");
        if (isfinish)
            return;
        startAnimImg.setVisibility(View.INVISIBLE);
        isStartAnim = false;
        startTimeAnim = 1000;
        startAnimImg.setImageResource(R.drawable.second003);
//        stopRecord();
        if (recording_state == RECORDING_RECORDING_STATE)
        {
            Log.i("RayTest","onPause");
            if(!isRecordComplete)
                reCording();
        }
        if (!mReleased) {
            if (mMediaRecorder != null)
                mMediaRecorder.release();
        }
        mReleased = false;
        if (recording_state == VIDEO_MEGER_PLAY) {
            if (mTestMergeVideo.isPlaying())
                mTestMergeVideo.pause();
            mPlayMergeMv.setVisibility(View.VISIBLE);
            mPlayMergeMv.setImageResource(R.drawable.recording_playmerge);
            mTestMergePauseimg.setVisibility(View.VISIBLE);
        } else {
            if (mVideoView.isPlaying())
                mVideoView.pause();
            onpauseimg.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("mrl", "onDestroy");
        if (timer != null)
            timer.cancel();
        if (mVideoView != null) {
            mVideoView.stop();
            mVideoView.release();
            mVideoView = null;
        }
        if (mTestMergeVideo != null) {
            mTestMergeVideo.stop();
            mTestMergeVideo.release();
            mTestMergeVideo = null;
        }
        if (mDownloadRequest != null) {
            mDownloadRequest.cancel();
        }
        if (mDownloadRequestLrc != null) {
            mDownloadRequestLrc.cancel();
        }
       /* if (mMediaObject!=null)
        mMediaObject.delete();
        if (mMediaRecorder!=null)
        mMediaRecorder.deletOutputDirectory();
        stopLoadingDialog();*/
    }

    /**
     * 手动对焦
     */
    @SuppressLint("NewApi")
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private boolean checkCameraFocus(MotionEvent event) {
        mFocusImage.setVisibility(View.GONE);
        float x = event.getX();
        float y = event.getY();
        float touchMajor = event.getTouchMajor();
        float touchMinor = event.getTouchMinor();

        Rect touchRect = new Rect((int) (x - touchMajor / 2),
                (int) (y - touchMinor / 2), (int) (x + touchMajor / 2),
                (int) (y + touchMinor / 2));
        // The direction is relative to the sensor orientation, that is, what
        // the sensor sees. The direction is not affected by the rotation or
        // mirroring of setDisplayOrientation(int). Coordinates of the rectangle
        // range from -1000 to 1000. (-1000, -1000) is the upper left point.
        // (1000, 1000) is the lower right point. The width and height of focus
        // areas cannot be 0 or negative.
        // No matter what the zoom level is, (-1000,-1000) represents the top of
        // the currently visible camera frame
        if (touchRect.right > 1000)
            touchRect.right = 1000;
        if (touchRect.bottom > 1000)
            touchRect.bottom = 1000;
        if (touchRect.left < 0)
            touchRect.left = 0;
        if (touchRect.right < 0)
            touchRect.right = 0;

        if (touchRect.left >= touchRect.right
                || touchRect.top >= touchRect.bottom)
            return false;

        ArrayList<Camera.Area> focusAreas = new ArrayList<Camera.Area>();
        focusAreas.add(new Camera.Area(touchRect, 1000));
        if (!mMediaRecorder.manualFocus(new Camera.AutoFocusCallback() {

            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                // if (success) {
                mFocusImage.setVisibility(View.GONE);
                // }
            }
        }, focusAreas)) {
            mFocusImage.setVisibility(View.GONE);
        }

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mFocusImage
                .getLayoutParams();
        int left = touchRect.left - (mFocusWidth / 2);// (int) x -
        // (focusingImage.getWidth()
        // / 2);
        int top = touchRect.top - (mFocusWidth / 2);// (int) y -
        // (focusingImage.getHeight()
        // / 2);
        if (left < 0)
            left = 0;
        else if (left + mFocusWidth > mWindowWidth)
            left = mWindowWidth - mFocusWidth;
        if (top + mFocusWidth > mWindowWidth)
            top = mWindowWidth - mFocusWidth;

        lp.leftMargin = left;
        lp.topMargin = top;
        mFocusImage.setLayoutParams(lp);
        mFocusImage.setVisibility(View.VISIBLE);

        if (mFocusAnimation == null)
            mFocusAnimation = AnimationUtils.loadAnimation(this,
                    R.anim.record_focus);

        mFocusImage.startAnimation(mFocusAnimation);

        mHandler.sendEmptyMessageDelayed(HANDLE_HIDE_RECORD_FOCUS, 3500);// 最多3.5秒也要消失
        return true;
    }

    /**
     * 开始录制
     */
    private void startRecord() {
        //計時跑起來

        isend = false;
        if (mMediaRecorder != null) {
            MediaObject.MediaPart part = mMediaRecorder.startRecord();
            if (part == null) {
                return;
            }
            // 如果使用MediaRecorderSystem，不能在中途切换前后摄像头，否则有问题
            if (mMediaRecorder instanceof DaYuMediaRecorderSystem) {
                mCameraSwitch.setVisibility(View.GONE);
            }
            mProgressView.setData(mMediaObject);
        }

        mRebuild = true;
        mPressedStatus = true;
        //mRecordController.setImageResource(R.drawable.recoding_re);
        mRecordController.setVisibility(View.VISIBLE);
        mRecordController.setImageResource(R.drawable.recording_re_red);
        Log.i("RayTest","startRecord");
        mBottomLayout.setBackgroundColor(mBackgroundColorPress);
        //mBottomLayout_result.setBackgroundColor(mBackgroundColorPress);
        if (mHandler != null) {
            mHandler.removeMessages(HANDLE_INVALIDATE_PROGRESS);
            mHandler.sendEmptyMessage(HANDLE_INVALIDATE_PROGRESS);

            mHandler.removeMessages(HANDLE_STOP_RECORD);
            mHandler.sendEmptyMessageDelayed(HANDLE_STOP_RECORD, RECORD_TIME_MAX - mMediaObject.getDuration());
        }
        mRecordDelete.setVisibility(View.GONE);
        mCameraSwitch.setEnabled(false);
        mRecordLed.setEnabled(false);
        recording_state = RECORDING_RECORDING_STATE;
        Log.i("RayTest","seekto "+ mMediaObject.getDuration());

        mLrc.onDrag(0);
        mVideoView.start();
    }

    /**
     * 重錄
     */
    public void deletRecording(String msg) {
        mSurfaceView.setVisibility(View.VISIBLE);
        if (mRecordDelete != null && mRecordDelete.isChecked()) {
            cancelDelete();
            return;
        }
        if (mMediaObject != null && mMediaObject.getDuration() > 1) {
            // 未转码
            new AlertDialog.Builder(this)
                    .setTitle(msg)
                    .setMessage(R.string.record_camera_exit_dialog_message)
                    .setNegativeButton(R.string.record_camera_cancel_dialog_yes,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    reCording();
                                    mVideoView.seekTo(0);
                                    mLrc.onDrag(0);
                                    repMediaRecorder();
                                    Log.i("RayTest","初始化");
                                }

                            })
                    .setPositiveButton(R.string.record_camera_cancel_dialog_no,
                            null).setCancelable(false).show();
            return;
        }

        if (mMediaObject != null)
            mMediaObject.delete();
        toastShort("刪除成功");
    }

    /**
     * 重錄
     */
    public void deletRecordingRecording(String msg) {
        mSurfaceView.setVisibility(View.VISIBLE);
        if (mRecordDelete != null && mRecordDelete.isChecked()) {
            cancelDelete();
            return;
        }
        if (mMediaObject != null && mMediaObject.getDuration() > 1) {
            // 未转码
            new AlertDialog.Builder(this)
                    .setTitle(msg)
                    .setMessage(R.string.record_camera_exit_dialog_message)
                    .setNegativeButton(R.string.record_camera_cancel_dialog_yes,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    stopRecord();
                                    reCording();
                                }

                            })
                    .setPositiveButton(R.string.record_camera_cancel_dialog_no,
                            null).setCancelable(false).show();
            return;
        }

        if (mMediaObject != null)
            mMediaObject.delete();
        toastShort("刪除成功");
    }

    /**
     * 重新錄製
     */
    private void reCording() {
        isRecordComplete = false;
        mSurfaceView.setVisibility(View.VISIBLE);
        mMediaObject.delete();
        mMediaRecorder.deletOutputDirectory();
        toastShort("成功清理錄製影片");
        //計時器繼續跑
        if (!isPuase)
        initplayerMV();
        mFinishResult.setVisibility(View.INVISIBLE);
        mLrc.setVisibility(View.VISIBLE);
        mLrc.onDrag(0);
        mBottomLayout.setVisibility(View.VISIBLE);
        //mBottomLayout_result.setVisibility(View.INVISIBLE);
        mRecordController.setVisibility(View.VISIBLE);
        mRecordController.setImageResource(R.drawable.recording_ready);
        recording_state = RECORDING_READY_STATE;
        mPlayMergeMv.setVisibility(View.GONE);
        mPlayMergeMv.setImageResource(R.drawable.recording_playmerge);
        mTestMergeVideo.stop();
        mTestMergeVideo.setVisibility(View.GONE);
        mTestMergePauseimg.setVisibility(View.INVISIBLE);
        repMediaRecorder();
        if (playMegerMvPath == null) {
            return;
        }
        File file = new File(playMegerMvPath);
        file.delete();
    }

    @Override
    public void onBackPressed() {
        if(isRecordComplete)
            finish();
        else{

            if (mRecordDelete != null && mRecordDelete.isChecked()) {
                Log.i("mrl", "這尼瑪 " + mRecordDelete + "   " + mRecordDelete.isChecked());
                cancelDelete();
                return;
            }
            if (mMediaObject != null && mMediaObject.getDuration() > 1) {
                // 未转码
                new AlertDialog.Builder(this)
                        .setTitle(R.string.hint)
                        .setMessage("點「確定」後將刪除當前錄製中的影片")
                        .setNegativeButton(R.string.record_camera_cancel_dialog_yes,
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        mMediaObject.delete();
//                                    toastShort("刪除成功");
                                        finish();
                                    }

                                })
                        .setPositiveButton(R.string.record_camera_cancel_dialog_no,
                                null).setCancelable(false).show();
                return;
            }

            if (mMediaObject != null)
                mMediaObject.delete();
            finish();




        }

    }

    /**
     * 停止录制
     */
    private void stopRecord() {
        mPressedStatus = false;
        Log.i("RayTest","stopRecord");
        //mBottomLayout.setBackgroundColor(mBackgroundColorNormal);
        //mBottomLayout_result.setBackgroundColor(mBackgroundColorNormal);
        if (mMediaRecorder != null) {
            mMediaRecorder.stopRecord();
        }
//        mRecordDelete.setVisibility(View.VISIBLE);
        mCameraSwitch.setEnabled(true);
        mRecordLed.setEnabled(true);
        mHandler.removeMessages(HANDLE_STOP_RECORD);
        checkStatus();
        // 检测是否已经完成
        mRecordController.setVisibility(View.VISIBLE);
        Log.i("mrl", mMediaObject.getDuration() + " 結束了stopRecord " + mVideoView.getCurrentPosition() + "  " + RECORD_TIME_MAX);
        if (mMediaObject.getDuration() >= RECORD_TIME_MAX) {
            mRecordController.setImageResource(R.drawable.recoding_re);
            if (recording_state != VIDEO_MEGER_PLAY) {
                mTitleNext.performClick();
                mVideoView.stop();
            }
        } else {

            mRecordController.setImageResource(R.drawable.recording_ready);
            if (recording_state != VIDEO_MEGER_PLAY)
                recording_state = RECORDING_READY_STATE;
//            mVideoView.seekTo(0);
        }
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (mHandler.hasMessages(HANDLE_STOP_RECORD)) {
            mHandler.removeMessages(HANDLE_STOP_RECORD);
        }

        // 处理开启回删后其他点击操作
        if (id != R.id.record_delete) {
            if (mMediaObject != null) {
                MediaObject.MediaPart part = mMediaObject.getCurrentPart();
                if (part != null) {
                    if (part.remove) {
                        part.remove = false;
                        mRecordDelete.setChecked(false);
                        if (mProgressView != null)
                            mProgressView.invalidate();
                    }
                }
            }
        }

        switch (id) {
            case R.id.iv_media_recorder_back:
                Log.i("mrl", "這尼瑪哦");
                onBackPressed();
                break;
            case R.id.record_camera_switcher:// 前后摄像头切换
                if (mRecordLed.isChecked()) {
                    if (mMediaRecorder != null) {
                        mMediaRecorder.toggleFlashMode();
                    }
                    mRecordLed.setChecked(false);
                }

                if (mMediaRecorder != null) {
                    mMediaRecorder.switchCamera();
                }

                if (mMediaRecorder.isFrontCamera()) {
                    mRecordLed.setEnabled(false);
                } else {
                    mRecordLed.setEnabled(true);
                }
                break;
            case R.id.record_camera_led:// 闪光灯
                // 开启前置摄像头以后不支持开启闪光灯
                if (mMediaRecorder != null) {
                    if (mMediaRecorder.isFrontCamera()) {
                        return;
                    }
                }

                if (mMediaRecorder != null) {
                    mMediaRecorder.toggleFlashMode();
                }
                break;
            case R.id.title_next:// 停止录制
                mMediaRecorder.startEncoding();
                toastShort("錄製完了,正在處理");
                break;
            case R.id.record_delete:
                // 取消回删
                if (mMediaObject != null) {
                    MediaObject.MediaPart part = mMediaObject.getCurrentPart();
                    if (part != null) {
                        if (part.remove) {
                            mRebuild = true;
                            part.remove = false;
                            mMediaObject.removePart(part, true);
                            mRecordDelete.setChecked(false);
                        } else {
                            part.remove = true;
                            mRecordDelete.setChecked(true);
                        }
                    }
                    if (mProgressView != null)
                        mProgressView.invalidate();
                    // 检测按钮状态
                    checkStatus();
                }
                break;
            case R.id.record_controller://錄製按鈕
                mSurfaceView.setVisibility(View.VISIBLE);
                mVideoView.setVisibility(View.VISIBLE);

                if (mMediaRecorder == null) {
                    break;
                }

                if (recording_state >= RECORDING_READY_STATE) {
                    if (recording_state == RECORDING_READY_STATE) {
                        // 取消回删
                        if (cancelDelete())
                            break;
                        startAnim();
//                        startRecord();
//                        onpauseimg.setVisibility(View.INVISIBLE);
                    } else if (recording_state == RECORDING_RECORDING_STATE) {
                        // 暂停
//                        if (mPressedStatus) {
//                            stopRecord();
//                        }

                        deletRecordingRecording("確定要刪除影片，重新錄製嗎?");
                    } else if (recording_state == VIDEO_MEGER_PLAY) {
                        deletRecording("確定要刪除影片，重新錄製嗎?");
                    } else if (recording_state == RECORDING_FRIST_STATE) {
                        mVideoView.setVolume(0.1f, 0.1f);
                        mSurfaceView.setVisibility(View.VISIBLE);
                        //_MediaPlayer.start();
                        mVideoView.seekTo(0);
                        mLrc.onDrag(0);
//                        startRecord();
                        startAnim();
                    }
                } else {
                    toastShort("當前狀態無法進行錄製哦");
                }
                break;
            case R.id.start_mergemv:
                if (!mTestMergeVideo.isPlaying()) {
                    mSurfaceView.setVisibility(View.INVISIBLE);
                    mVideoView.setVisibility(View.INVISIBLE);
                    mTestMergeVideo.start();
                    mTestMergePauseimg.setVisibility(View.INVISIBLE);
                    mPlayMergeMv.setVisibility(View.INVISIBLE);
                    mPlayMergeMv.setImageResource(R.drawable.recording_recording);
                } else {
                    mTestMergeVideo.pause();
                    mPlayMergeMv.setVisibility(View.INVISIBLE);
                    mPlayMergeMv.setImageResource(R.drawable.recording_playmerge);
                }
                break;
            case R.id.finish_result:
                showFinshDialog();
                break;
            case R.id.iv_recoding_re:
                mRecordController.performClick();
                break;
        }
    }

    /**
     * 取消回删
     */
    private boolean cancelDelete() {
        if (mMediaObject != null) {
            MediaObject.MediaPart part = mMediaObject.getCurrentPart();
            if (part != null && part.remove) {
                part.remove = false;
                mRecordDelete.setChecked(false);

                if (mProgressView != null)
                    mProgressView.invalidate();

                return true;
            }
        }
        return false;
    }

    /**
     * 检查录制时间，显示/隐藏下一步按钮
     */
    private int checkStatus() {
        int duration = 0;
        if (!isFinishing() && mMediaObject != null) {
            duration = mMediaObject.getDuration();
            if (duration < RECORD_TIME_MIN) {
                if (duration == 0) {
                    mCameraSwitch.setVisibility(View.VISIBLE);
                    mRecordDelete.setVisibility(View.GONE);
                }
                // 视频必须大于3秒
                if (mTitleNext.getVisibility() != View.INVISIBLE)
                    mTitleNext.setVisibility(View.INVISIBLE);
            } else {
                // 下一步
                if (mTitleNext.getVisibility() != View.VISIBLE) {
                    mTitleNext.setVisibility(View.VISIBLE);
                }
            }
        }
        return duration;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLE_INVALIDATE_PROGRESS:
                    if (mMediaRecorder != null && !isFinishing()) {
                        if (mProgressView != null)
                            mProgressView.invalidate();
                        // if (mPressedStatus)
                        // titleText.setText(String.format("%.1f",
                        // mMediaRecorder.getDuration() / 1000F));
                        if (mPressedStatus)
                            sendEmptyMessageDelayed(0, 30);
                    }
                    break;
                case VIDEO_TIME_OVER:
                    stopRecord();
                    break;
                case TIME_SEND_FOUR:
                    stopAnim();
                    break;
                case TIME_SEND_TWO:
                    mVideoView.seekTo(mMediaObject.getDuration());
                    startAnimImg.setImageResource(R.drawable.second002);
                    break;
                case TIME_SEND_THREE:
                    startAnimImg.setImageResource(R.drawable.second001);
                    break;
            }
        }
    };

    @Override
    public void onEncodeStart() {
        //showProgress("", getString(R.string.record_camera_progress_message));
    }

    @Override
    public void onEncodeProgress(String progress) {

    }

    /**
     * 转码完成
     */
    @Override
    public void onEncodeComplete() {
        mRebuild = false;
        //mMediaRecorder.startMerge();
    }

    /**
     * 转码失败 检查sdcard是否可用，检查分块是否存在。這裡最好進行重新處理的操作
     */
    @Override
    public void onEncodeError(String s) {
        hideProgress();
        toastShort(getString(R.string.record_video_transcoding_faild));
        Log.i("mrl","onEncodeError"+s);
        deletRecording("視頻處理出現了錯誤，請重新錄製吧，請勿頻繁暫停");
    }

    @Override
    public void onMergeStart() {
    }

    @Override
    public void onMergeProgress(int progress) {
    }

    @Override
    public void onMergeComplete(String path) {
        //initMergeMvPlayTest(path);
        isRecordComplete  = true;
        Intent it = getIntent();
        if(it!=null){
            Log.i("RayTest","onMergeComplete");
            String VedioURL = getIntent().getStringExtra(VEDIOURL);
            String LrcURL = getIntent().getStringExtra(LRCURL);
            String UploadURL = getIntent().getStringExtra(UPLOADURL);
            Intent it_player = DaYuMediaPlayerActivity.createIntent(this, VedioURL, LrcURL,  _starID ,UploadURL, getIntent().getStringExtra(EXTRAINFO));
            startActivityForResult(it_player, KaraStar.REQUEST_VIDEO_RECORD);
            //finish();
        }


    }

    @Override
    public void onMergeError() {
        toastShort("視頻出現了意外事故。。。");
        hideProgress();
        deletRecording("視頻處理出現了錯誤，請重新錄製吧，請勿頻繁暫停");
    }

    private void initMergeMvPlayTest(String path) {
        toastShort("視頻處理完成了！");
        recording_state = VIDEO_MEGER_PLAY;
        playMegerMvPath = path;
        Log.i("mrl", "視頻處理完成了" + playMegerMvPath);
        mTestMergeVideo.setVisibility(View.VISIBLE);
        try {
            mTestMergeVideo.setDataSource("/storage/emulated/0/Android/data/tw.chiae.inlive/cache/inlive/rec/stars-004.mp4");
            mTestMergeVideo.setVolume(1, 1);
            mTestMergeVideo.setLooping(true);
            mTestMergeVideo.prepareAsync(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Log.i("mrl", "合成出來的視頻");
                    Log.i("RayTest", "合成出來的視頻");
                    hideProgress();
                    mLrc.setVisibility(View.GONE);
                    mLrc.onDrag(0);
                    mBottomLayout.setVisibility(View.INVISIBLE);
                    //mBottomLayout_result.setVisibility(View.VISIBLE);
                    mRecordController.setVisibility(View.GONE);
                    //mTestMergePauseimg.setImageBitmap(createVideoThumbnail(playMegerMvPath, mTestMergeVideo.getVideoWidth(), mTestMergeVideo.getVideoHeight()));
                    mPlayMergeMv.setVisibility(View.VISIBLE);
                    mFinishResult.setVisibility(View.VISIBLE);
   /*                 RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mRecordController.getLayoutParams();
                    params.addRule(RelativeLayout.ABOVE, R.id.finish_result);
                    mRecordController.setLayoutParams(params);*/
                    mTestMergePauseimg.setVisibility(View.VISIBLE);
                }
            });
            mTestMergeVideo.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    toastShort("合成出來的視頻解析出錯了");
                    Log.i("mrl", "合成出來的視頻解析出錯了" + playMegerMvPath);
                    hideProgress();
                    return false;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            toastShort("合成出來的視頻解析出錯了");
            hideProgress();
        }
    }

    private void StartMergeFilm(String path, int starId) {
       /* KaraStar.getInstance().open(this, LocalDataManager.getInstance().getLoginInfo().getUserId(), new KaraStar.ViewHandler() {
            @Override
            public void switchToRecordView(KSWebActivity act, int starId, String starVideoUrl, String name) {
                Log.i("RayTest",starVideoUrl+name+".mp4");
                startActivity(MediaRecorderActivity.createIntent(MediaRecorderActivity.this,starVideoUrl+name+".mp4","http://kay-wedding.pancakeapps.com/stars-001.lrc"));
                //act.finish();
            }

            @Override
            public void switchToUserView(KSWebActivity act, String userId) {

            }

        });*/
        Log.i("RayTest"," StartID to webview"+starId);
        Intent intent = new Intent();
        intent.putExtra("starId", starId);
        intent.putExtra("filePath", path);
        setResult(RESULT_OK, intent);
        //finish();


    }

    @Override
    public void onVideoError(int what, int extra) {

    }

    @Override
    public void onAudioError(int what, String message) {

    }

    @Override
    public void onPrepared() {

    }

    public void showProgress(String title, String message) {
        showLoadingDialog();
    }


    public void hideProgress() {
        stopLoadingDialog();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onStop() {
        hideProgress();
        mProgressDialog = null;
        super.onStop();
    }

    /**
     * 反序列化对象
     */
    protected static MediaObject restoneMediaObject(String obj) {
        try {
            String str = FileUtils.readFile(new File(obj));
            Gson gson = new Gson();
            MediaObject result = gson.fromJson(str.toString(),
                    MediaObject.class);
            result.getCurrentPart();
            preparedMediaObject(result);
            return result;
        } catch (Exception e) {
            if (e != null)
                Log.e("VCamera", "readFile", e);
        }
        return null;
    }

    /**
     * 预处理数据对象
     */
    public static void preparedMediaObject(MediaObject mMediaObject) {
        if (mMediaObject != null && mMediaObject.getMedaParts() != null) {
            int duration = 0;
            for (MediaObject.MediaPart part : mMediaObject.getMedaParts()) {
                part.startTime = duration;
                part.endTime = part.startTime + part.duration;
                duration += part.duration;
            }
        }
    }

    /**
     * 序列号保存视频数据
     */
    public static boolean saveMediaObject(MediaObject mMediaObject) {
        if (mMediaObject != null) {
            try {
                if (StringUtils.isNotEmpty(mMediaObject.getObjectFilePath())) {
                    FileOutputStream out = new FileOutputStream(
                            mMediaObject.getObjectFilePath());
                    Gson gson = new Gson();
                    out.write(gson.toJson(mMediaObject).getBytes());
                    out.flush();
                    out.close();
                    return true;
                }
            } catch (Exception e) {
                Log.i("mrl", e.toString());
            }
        }
        return false;
    }

    Timer timer = new Timer();
    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            if (isStartAnim) {
                startTimeAnim += 10;
                if (startTimeAnim >= 2000)
                    mHandler.sendEmptyMessage(TIME_SEND_TWO);
                if (startTimeAnim >= 3000)
                    mHandler.sendEmptyMessage(TIME_SEND_THREE);
                if (startTimeAnim >= 4000)
                    mHandler.sendEmptyMessage(TIME_SEND_FOUR);
            }
            if (isend || mVideoView.getMediaPlayer() == null || !mVideoView.isPlaying())
                return;

            Log.i("timeer", "跑起來"+isend + "  " + mVideoView.getMediaPlayer() + "   " + mVideoView.isPlaying() + "    " + mVideoView.getCurrentPosition() + "    " + mVideoView.getDuration() + "  " + mMediaObject.getDuration() + " " + RECORD_TIME_MAX);
            /**
             * 刷新歌詞進度
             */
            if (mVideoView.isPlaying()) {
                long time = mVideoView.getCurrentPosition();
                mLrc.updateTime(time);
            }
            /**
             * 判斷是否結束
             */
            if (mMediaObject.getDuration() >= RECORD_TIME_MAX) {
                mHandler.sendEmptyMessage(VIDEO_TIME_OVER);
                isend = true;
            }
        }
    };

    /**
     * 获取视频的缩略图
     * 提供了一个统一的接口用于从一个输入媒体文件中取得帧和元数据。
     *
     * @param path   视频的路径
     * @param width  缩略图的宽
     * @param height 缩略图的高
     * @return 缩略图
     */
    public Bitmap createVideoThumbnail(String path, int width, int height) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        if (TextUtils.isEmpty(path)) {
            return null;
        }

        File file = new File(path);
        if (!file.exists()) {
            return null;
        }

        try {
            retriever.setDataSource(path);
            bitmap = retriever.getFrameAtTime(0,MediaMetadataRetriever.OPTION_CLOSEST_SYNC); //取得指定时间的Bitmap，即可以实现抓图（缩略图）功能
        } catch (IllegalArgumentException ex) {
            // Assume this is a corrupt video file
        } catch (RuntimeException ex) {
            // Assume this is a corrupt video file.
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                // Ignore failures while cleaning up.
            }
        }

        if (bitmap == null) {
            return null;
        }

        bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
        return bitmap;
    }


    protected void toastShort(@NonNull String msg) {
        CustomToast.makeCustomText(this, msg, Toast.LENGTH_SHORT).show();
    }


    /**
     * 這裡我偷懶 直接no的代碼。。。。別罵我
     * 下載地址
     */
    private void downLoadMv(String btPath) {
        showProgress("", "正在加載mv");
        // 开始下载了，但是任务没有完成，代表正在下载，那么暂停下载。
        if (mDownloadRequest != null && mDownloadRequest.isStarted() && !mDownloadRequest.isFinished()) {
            // 暂停下载。
            mDownloadRequest.cancel();
        } else if (mDownloadRequest == null || mDownloadRequest.isFinished()) {// 没有开始或者下载完成了，就重新下载。
            String[] fileName = btPath.split("/");

            mDownloadRequest = NoHttp.createDownloadRequest(btPath, VCamera.getVideoCachePath(), fileName[fileName.length - 1], true, false);
            CallServer.getDownloadInstance().add(0, mDownloadRequest, downloadListener);
            // 添加到队列，在没响应的时候让按钮不可用。這裡取消所有動作除了強制退出
        }
    }

    /**
     * 下载监听
     */
    private DownloadListener downloadListener = new DownloadListener() {

        @Override
        public void onStart(int what, boolean isResume, long beforeLength, Headers headers, long allCount) {
        }

        @Override
        public void onDownloadError(int what, Exception exception) {
            hideProgress();
            Log.i("mrl", "onDownloadError 下載出現異常" + exception);
            showErrorMvDialog();
        }

        @Override
        public void onProgress(int what, int progress, long fileCount) {
            Log.i("mrl", "當前進度" + progress);
            setLoadingProgress(getString(R.string.loading_progress_text, Integer.toString(progress)));
            float progressVal = (float) progress/100;
            Log.i("RayTest", "當前進度" + progressVal);
            mVideoView.setAlpha(progressVal);
            mSurfaceView.setAlpha(progressVal);
        }

        @Override
        public void onFinish(int what, String filePath) {
            //下載結束了
            hideProgress();
            //計時器拋棄來
            timer.schedule(timerTask, 0, 10);
            downloadPath = filePath;
            Log.i("mrl","下載的mv地址"+filePath);
//            downloadPath = getExternalCacheDir().getPath()+"/inlive/rec/test.mp4";
            mMediaRecorder.setDownloadPath(downloadPath);
            initplayerMV();
        }

        @Override
        public void onCancel(int what) {
        }

        private void updateProgress(int progress) {
        }
    };

    /**
     * 這裡我偷懶 直接no的代碼。。。。別罵我
     * 下載地址
     */
    private void downLoadLrc(String btPath) {
        // 开始下载了，但是任务没有完成，代表正在下载，那么暂停下载。
        if (mDownloadRequestLrc != null && mDownloadRequestLrc.isStarted() && !mDownloadRequestLrc.isFinished()) {
            // 暂停下载。
            mDownloadRequestLrc.cancel();
        } else if (mDownloadRequestLrc == null || mDownloadRequestLrc.isFinished()) {// 没有开始或者下载完成了，就重新下载。
            String[] fileName = btPath.split("/");
            mDownloadRequestLrc = NoHttp.createDownloadRequest(btPath, VCamera.getVideoCachePath() + "/lrc/", fileName[fileName.length - 1], true, false);
            CallServer.getDownloadInstance().add(0x15, mDownloadRequestLrc, downloadListenerLrc);
            // 添加到队列，在没响应的时候让按钮不可用。這裡取消所有動作除了強制退出
        }
    }

    /**
     * 下载监听
     */
    private DownloadListener downloadListenerLrc = new DownloadListener() {

        @Override
        public void onStart(int what, boolean isResume, long beforeLength, Headers headers, long allCount) {
        }

        @Override
        public void onDownloadError(int what, Exception exception) {
            toastShort("沒有找到歌詞呢");
            String message = getString(R.string.download_error);
            String messageContent;
            if (exception instanceof ServerError) {
                messageContent = getString(R.string.download_error_server);
            } else if (exception instanceof NetworkError) {
                messageContent = getString(R.string.download_error_network);
            } else if (exception instanceof StorageReadWriteError) {
                messageContent = getString(R.string.download_error_storage);
            } else if (exception instanceof StorageSpaceNotEnoughError) {
                messageContent = getString(R.string.download_error_space);
            } else if (exception instanceof TimeoutError) {
                messageContent = getString(R.string.download_error_timeout);
            } else if (exception instanceof UnKnownHostError) {
                messageContent = getString(R.string.download_error_un_know_host);
            } else if (exception instanceof URLError) {
                messageContent = getString(R.string.download_error_url);
            } else {
                messageContent = getString(R.string.download_error_un);
            }
            message = String.format(Locale.getDefault(), message, messageContent);
//            mTvResult.setText(message);
        }

        @Override
        public void onProgress(int what, int progress, long fileCount) {
        }

        @Override
        public void onFinish(int what, String filePath) {
            Log.i("mrl", "歌詞加載完成" + filePath);
            lrcPath = filePath;
            File file = new File(lrcPath);
            if (file != null)
                mLrc.loadLrc(file);
            mLrc.onDrag(0);
        }

        @Override
        public void onCancel(int what) {
        }

        private void updateProgress(int progress) {
        }
    };

    /**
     * 關閉Activity回傳數據
     */
    public void finishThis() {
        Log.i("mrl", "finish地址" + playMegerMvPath);
        toastShort("正在進行提交");
        isfinish = true;
        //setResult(SET_RESULT, new Intent().putExtra(SET_RESULT_MEGERPATH, playMegerMvPath));
        StartMergeFilm(playMegerMvPath,_starID);
        //finish();
    }

    /**
     * 提交的diatlog
     */
    private void showFinshDialog() {
        if (playMegerMvPath == null) {
            toastShort("暫無可上傳的視頻");
            return;
        }
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setMessage("你確定要提交該合唱視頻嗎？\n提交將無法修改參賽視頻咯");
        builder.setNegativeButton(getString(R.string.cancel), null);
        builder.setPositiveButton(getString(R.string.commit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishThis();
            }
        });
        builder.show();
    }

    private void showErrorDialog(String errormsg) {
        if (playMegerMvPath == null) {
            toastShort("暫無可上傳的視頻");
            return;
        }
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setMessage(errormsg);
        builder.setNegativeButton(getString(R.string.cancel), null);
        builder.setPositiveButton(getString(R.string.commit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DaYu2MediaRecorderActivity.this.finish();
            }
        });
        builder.show();
    }

    /**
     * mv下載失敗提示退出
     */
    private void showErrorMvDialog() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setMessage("Mv下載出現異常，請退出界面重試");
        builder.setNegativeButton(getString(R.string.cancel), null);
        builder.setPositiveButton(getString(R.string.commit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DaYu2MediaRecorderActivity.this.finish();
            }
        });
        builder.show();
    }

    private void showLoadingDialog() {
        if (mLoadingDilog == null)
            mLoadingDilog = new LaodingDilog();
        if (!mLoadingDilog.isAdded()) {
            mLoadingDilog.show(getFragmentManager(), "myloadinganim");
        }
        mLoadingDilog.setCancelable(false);
    }

    private void stopLoadingDialog() {
        if (mLoadingDilog == null || !mLoadingDilog.isVisible())
            return;
        mLoadingDilog.stopLoadinganim();
    }

    private void setLoadingProgress(String s) {
        if (mLoadingDilog == null)
            return;
        mLoadingDilog.setLoadingProgress(s);
    }

    private int startTimeAnim = 1000;

    public void startAnim() {
        isStartAnim = true;
        startAnimImg.setVisibility(View.VISIBLE);
    }

    public void stopAnim() {
        isStartAnim = false;
        startAnimImg.setVisibility(View.INVISIBLE);
        startTimeAnim = 1000;
        startAnimImg.setImageResource(R.drawable.second003);
        startRecord();
        onpauseimg.setVisibility(View.INVISIBLE);
    }

    /**
     * 獲取網絡狀態
     *
     * @param context
     * @return
     */
    private boolean getNetWorkType(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            String type = networkInfo.getTypeName();
            if (type.equalsIgnoreCase("WIFI")) {
                return true;
            } else if (type.equalsIgnoreCase("MOBILE")) {
                return false;
            }
        } else {
            toastShort("當前無網絡，請檢查網絡");
            return false;
        }
        return false;
    }

    /**
     * 提交的diatlog
     */
    private void showYesidoDialog() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setMessage("建議在wifi環境下錄製合唱，因錄製將消耗較多流量，是否繼續?");
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setPositiveButton(getString(R.string.commit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downLoadMv(getMVPath());
                downLoadLrc(getLrcPath());
            }
        });
        builder.show();
    }


}

