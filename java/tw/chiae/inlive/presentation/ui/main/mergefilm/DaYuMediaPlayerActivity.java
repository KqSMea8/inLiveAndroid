package tw.chiae.inlive.presentation.ui.main.mergefilm;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xw.repo.BubbleSeekBar;

import java.io.File;

import tw.chiae.inlive.R;
import tw.chiae.inlive.presentation.ui.main.mergefilm.model.DeviceUtils;
import tw.chiae.inlive.presentation.ui.main.mergefilm.model.FileUtils;
import tw.chiae.inlive.presentation.ui.widget.CustomToast;


/**
 * 视频录制
 *
 * @author tangjun@yixia.com 感謝原作者的貢獻 在下李龍龍 抱拳了！
 */
public class DaYuMediaPlayerActivity extends AppCompatActivity implements OnClickListener, MediaPlayerPresenter.MediaPlayerInterface {

    private static String PREFS_NAME = "DelayTime";
    private static final String DELAY_VALUE = "delayValue";
    public int RECORD_TIME_MAX = 1;

    public static final String VEDIOURL = "mvURL";
    public static final String LRCURL = "rlcURL";
    public static final String STAR_ID = "starId";
    private static final String UPLOADURL = "uploadURL";
    private static final String EXTRAINFO = "extrainfo";
    private MvVideoView mVideoView, mMvVideo;
    private ImageView mPlayMergeMv, onpauseimg, iv_back, mRecPauseImg;
    private RelativeLayout mBottomLayout;
    private int mBackgroundColorPress;
    private String recordPath;
    private String mvPath;
    private boolean mvIsReady = false;
    private boolean recordViewIsReady = false;
    private LaodingDilog mLoadingDilog;
    private DaYuMediaPlayerPresenter presenter;
    private ImageView recoding_re;
    private ProgressBar mProgressView;
    private ImageView mFinishResult;
    private TextView mBack_tip;
    private float iDelayValue = 0;
    private int iDelayMax = 2;
    private int iDelayMin = -2;
    private BubbleSeekBar mSBPlayDelayBar;
    private ImageView mSubDelay;
    private ImageView mAddDelay;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i("RayTest","onCreate:DaYuMediaPlayerActivity  "+PREFS_NAME);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player_dayu);
        presenter = new DaYuMediaPlayerPresenter(this);
        presenter.ParseIntent(getIntent());



    }


    public void showYesidoDialog() {
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
                presenter.downLoadMv();
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

    public int getToolBarHeight() {
        int[] attrs = new int[] {R.attr.actionBarSize};
        TypedArray ta = obtainStyledAttributes(attrs);
        int toolBarHeight = ta.getDimensionPixelSize(0, -1);
        ta.recycle();
        return toolBarHeight;
    }

    public void initVideoView() {
        final int w = DeviceUtils.getScreenWidth(this);
        final int h = DeviceUtils.getScreenHeight(this) - getToolBarHeight();
        int height = w * 4 / 3;

/*        RelativeLayout.LayoutParams RecordlayoutParam = (RelativeLayout.LayoutParams) mVideoView.getLayoutParams();

        RecordlayoutParam.leftMargin = w*1/3;
        RecordlayoutParam.rightMargin = -1*w*1/3;
        mVideoView.setLayoutParams(RecordlayoutParam);*/


        RelativeLayout.LayoutParams RecViewlp = (RelativeLayout.LayoutParams) mRecPauseImg.getLayoutParams();
        RecViewlp.width = w;
        RecViewlp.height = height;
        mRecPauseImg.setLayoutParams(RecViewlp);

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mMvVideo.getLayoutParams();
        lp.width = w / 2;
        lp.height = w;
        mMvVideo.setLayoutParams(lp);
        RelativeLayout.LayoutParams surfaceLayoutParam = (RelativeLayout.LayoutParams) mVideoView.getLayoutParams();
        float ratio = (float)w / (float) h ;
        surfaceLayoutParam.height = w;
        float surfceWidth= (float)w *ratio;
        surfaceLayoutParam.width = (int) surfceWidth;
        int left = (int) surfceWidth- (w-(w/2));
        surfaceLayoutParam.leftMargin = left;
        surfaceLayoutParam.rightMargin = -1*left;


        mVideoView.setLayoutParams(surfaceLayoutParam);
        Log.i("RayTest","w:"+surfaceLayoutParam.width+" h:"+surfaceLayoutParam.height+" "+ (int) surfceWidth+" left:"+left);

        RelativeLayout.LayoutParams MvViewlp = (RelativeLayout.LayoutParams) onpauseimg.getLayoutParams();
        MvViewlp.width = w ;
        MvViewlp.height = height;

        onpauseimg.setLayoutParams(MvViewlp);

        //presenter.setThumbnail(onpauseimg, true);
        //presenter.setThumbnail(mRecPauseImg, true);

    }

    public static Intent createIntent(Context context, String videoUrl, String lrcUrl, int starId, String uploadUrl, String extraInfo) {
        Intent it = new Intent(context, DaYuMediaPlayerActivity.class);
        it.putExtra(VEDIOURL, videoUrl);
        it.putExtra(LRCURL, lrcUrl);
        it.putExtra(STAR_ID, starId+"");
        it.putExtra(UPLOADURL, uploadUrl);
        it.putExtra(EXTRAINFO, extraInfo);
        Log.i("RayTest","starId:"+starId);
        return it;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_mergemv:
                playRecordVideo();
                break;
            case R.id.iv_media_recorder_back:
                setResult(RESULT_CANCELED);
                this.finish();
                break;
            case R.id.iv_recoding_re:
                deletRecordingRecording("是否確認重錄作品？重錄作品將會刪掉本次作品");
                break;
            case R.id.finish_result:
                showFinshDialog();
                break;
            case R.id.iv_delay_add:
                if(iDelayValue>=mSBPlayDelayBar.getMax()) {
                    iDelayValue = mSBPlayDelayBar.getMax();
                    mSBPlayDelayBar.setProgress(iDelayValue);
                }else
                    mSBPlayDelayBar.setProgress(mSBPlayDelayBar.getProgressFloat()+0.1f);

                break;
            case R.id.iv_delay_sub:
                if(iDelayValue<=mSBPlayDelayBar.getMin()) {
                    iDelayValue = mSBPlayDelayBar.getMin();
                    mSBPlayDelayBar.setProgress(iDelayValue);
                }
                else
                    mSBPlayDelayBar.setProgress(mSBPlayDelayBar.getProgressFloat()-0.1f);

                break;
        }
    }


    private void InitDelayMV() {
        if(mVideoView.isPlaying()){
            mVideoView.pause();
            mVideoView.seekTo(0);
        }

        if(mMvVideo.isPlaying()){
            mMvVideo.pause();
            mMvVideo.seekTo(0);
        }

        iDelayValue = mSBPlayDelayBar.getProgressFloat();
        mPlayMergeMv.setVisibility(View.VISIBLE);


        editor.putFloat(DELAY_VALUE, iDelayValue);
        editor.commit();

        if (iDelayValue < 0) {
            toastShort("延遲:" + Math.abs(mSBPlayDelayBar.getProgressFloat())  + "秒");
        }else{
            if(iDelayValue==0)
                toastShort("無延遲");
            else
                toastShort("加快:"+Math.abs(mSBPlayDelayBar.getProgressFloat())+"秒");
        }
    }


    private void showFinshDialog() {
        if (presenter.getMvPath() == null) {
            toastShort("暫無可上傳的視頻");
            return;
        }
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setMessage("您確定要提交合唱作品嗎？\n提交後將無法修改喔!");
        builder.setNegativeButton(getString(R.string.cancel), null);
        builder.setPositiveButton(getString(R.string.commit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishThis();
            }
        });
        builder.show();
    }

    private void finishThis() {
        toastShort("正在進行提交");
        //setResult(SET_RESULT, new Intent().putExtra(SET_RESULT_MEGERPATH, playMegerMvPath));
        StartMergeFilm(presenter.getMvPath(), presenter.getStarID());
    }

    private void StartMergeFilm(String mvPath, int starID) {
        /*if (FileUtils.checkFile(presenter.getMvPath())) {
            File f = new File(presenter.getMvPath());
            if (f != null) {
                if (f.exists()) {
                    //已经存在，删除
                    if (f.isDirectory())
                        FileUtils.deleteDir(f);
                    else
                        FileUtils.deleteFile(f);
                }
            }
        }*/
        editor.putFloat(DELAY_VALUE,0f);
        editor.commit();
        Log.i("RayTest","starId"+starID);
        Log.i("RayTest","mvPath"+mvPath);
        Intent intent = new Intent();
        //intent.putExtra("starId", starID+"");
        //intent.putExtra("filePath", mvPath);
        intent.putExtra("uploadPage", getIntent().getStringExtra(UPLOADURL));
        //intent.putExtra("extraInfo",Integer.parseInt(getIntent().getStringExtra(EXTRAINFO)) );
        intent.putExtra("extraInfo",getIntent().getStringExtra(EXTRAINFO) );
        //  intent.putExtra("extraInfo", extraInfo);
         intent.putExtra("filePath", mvPath);

        Log.i("RayTest","UPLOADURL "+ getIntent().getStringExtra(UPLOADURL));
        Log.i("RayTest","EXTRAINFO "+ getIntent().getStringExtra(EXTRAINFO));
        Log.i("RayTest","mvPath "+ mvPath);

        setResult(RESULT_OK, intent);
        finish();
    }

    private void deletRecordingRecording(String msg) {
        new AlertDialog.Builder(this)
                .setTitle(msg)
                .setMessage(R.string.record_camera_exit_dialog_message)
                .setNegativeButton(R.string.record_camera_cancel_dialog_yes,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                reCording();
                            }

                        })
                .setPositiveButton(R.string.record_camera_cancel_dialog_no,
                        null).setCancelable(false).show();
    }

    private void reCording() {

        Log.i("RayTest","reCording: "+presenter.getMvPath());
        if (FileUtils.checkFile(presenter.getMvPath())) {
            File f = new File(presenter.getMvPath());
            if (f != null) {
                if (f.exists()) {
                    //已经存在，删除
                    if (f.isDirectory())
                        FileUtils.deleteDir(f);
                    else
                        FileUtils.deleteFile(f);
                }
            }
        }
        Intent it = DaYu2MediaRecorderActivity.createIntent(this, getIntent().getStringExtra(VEDIOURL), getIntent().getStringExtra(LRCURL), Integer.parseInt(getIntent().getStringExtra(STAR_ID)), getIntent().getStringExtra(UPLOADURL), getIntent().getStringExtra(EXTRAINFO));
        startActivityForResult(it, KaraStar.REQUEST_VIDEO_RECORD);
        finish();
    }

    private void playRecordVideo() {
        Log.i("RayTest","playRecordVideo "+presenter.getStatus());
        if (presenter.getStatus()) {
            mPlayMergeMv.setVisibility(View.GONE);
            onpauseimg.setVisibility(View.INVISIBLE);
            mRecPauseImg.setVisibility(View.INVISIBLE);
            presenter.setPlayStatus(true);
            mVideoView.seekTo(0);
            mMvVideo.seekTo(0);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    //if(DelaySec<0)
                      /*  mMvVideo.start();
                        Thread.sleep(100);
                        mVideoView.start();*/

                    if (iDelayValue >= 0) {

                        try {
                            //toastShort("延遲:" + Math.abs(mSBPlayDelayBar.getProgressFloat())  + "秒");
                            mVideoView.start();
                            long delaytime = (long) (1000*Math.abs(iDelayValue));
                            Log.i("RayTest"," 錄製先  > delaytime1:"+delaytime +" mMvVideo");
                            Thread.sleep(delaytime);
                            mMvVideo.start();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }


                    } else {

                        try {
                            //toastShort("延遲:" + Math.abs(mSBPlayDelayBar.getProgressFloat())  + "秒");
                            mMvVideo.start();
                            long delaytime = (long) (1000*Math.abs(iDelayValue));
                            Log.i("RayTest"," ＭＶ先  > delaytime2:"+delaytime +" mVideoView");
                            Thread.sleep(delaytime);
                            mVideoView.start();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }


                    }


                }
            });
            UpdatemProgressView();
        }
    }
    private void UpdatemProgressView() {
        DelayThread dThread = new DelayThread(100);
        dThread.start();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.CancleRequest();
        if (mMvVideo != null) {
            mMvVideo.stop();
            mMvVideo.release();
            mMvVideo = null;
        }
        if (mVideoView != null) {
            mVideoView.stop();
            mVideoView.release();
            mVideoView = null;
        }
    }

    public void setLoadingProgress(String s) {
        if (mLoadingDilog == null)
            return;
        mLoadingDilog.setLoadingProgress(s);
    }

    public void hideProgress() {
        stopLoadingDialog();
    }

    private void stopLoadingDialog() {
        if (mLoadingDilog == null || !mLoadingDilog.isVisible())
            return;
        mLoadingDilog.stopLoadinganim();
    }

    public void showErrorMvDialog() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setMessage("Mv下載出現異常，請退出界面重試");
        builder.setNegativeButton(getString(R.string.cancel), null);
        builder.setPositiveButton(getString(R.string.commit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DaYuMediaPlayerActivity.this.finish();
            }
        });
        builder.show();
    }


    @Override
    public void initView() {
        Log.i("RayTest","initView");

        PREFS_NAME = "dayu_"+getIntent().getStringExtra(STAR_ID)+"_"+presenter.getRecordFileName();
        settings = getSharedPreferences(PREFS_NAME, 0);
        iDelayValue = settings.getFloat(DELAY_VALUE, 0);
        Log.i("RayTest","get Value"+iDelayValue);
        editor = settings.edit();

        mMvVideo = (MvVideoView) findViewById(R.id.film_view);
        mPlayMergeMv = (ImageView) findViewById(R.id.start_mergemv);
        mVideoView = (MvVideoView) findViewById(R.id.test_merge_video);
        //mBottomLayout_result = (RelativeLayout) findViewById(R.id.bottom_layout_result);
        mBottomLayout = (RelativeLayout) findViewById(R.id.bottom_layout);
        onpauseimg = (ImageView) findViewById(R.id.onpauseimg);
        mRecPauseImg = (ImageView) findViewById(R.id.test_merge_pauseimg);
        iv_back = (ImageView) findViewById(R.id.iv_media_recorder_back);
        recoding_re = (ImageView) findViewById(R.id.iv_recoding_re);
        mBackgroundColorPress = getResources().getColor(R.color.camera_bottom_press_bg);
        mProgressView = (ProgressBar) findViewById(R.id.record_progress2);
        mFinishResult = (ImageView) findViewById(R.id.finish_result);
        mBack_tip = (TextView) findViewById(R.id.tv_back_tip);
        mSBPlayDelayBar = (BubbleSeekBar) findViewById(R.id.rsv_large_bi);
        mSubDelay = (ImageView) findViewById(R.id.iv_delay_sub);
        mAddDelay = (ImageView) findViewById(R.id.iv_delay_add);
        //mBottomLayout.setBackgroundColor(mBackgroundColorPress);
        mVideoView.setRotationY(180);
        mRecPauseImg.setRotationY(180);
        mSubDelay.setOnClickListener(this);
        mAddDelay.setOnClickListener(this);
        presenter.isCompletedInitView();
        RelativeLayout.LayoutParams layoutParams;

        layoutParams = (RelativeLayout.LayoutParams) mBottomLayout.getLayoutParams();
        final int w = DeviceUtils.getScreenWidth(this);
        layoutParams.topMargin = w;
        setAnim();
        setSeekBar();
        initVideoView();
    }

    private void setSeekBar() {
        Log.i("RayTest","setSeekBar");
        iDelayValue = settings.getFloat(DELAY_VALUE, 0);
        Log.i("RayTest","get Value2"+iDelayValue);
        if(mSBPlayDelayBar==null) {
            Log.i("RayTest", "mSBPlayDelayBar null");
            //mSBPlayDelayBar = (BubbleSeekBar) findViewById(R.id.rsv_large_bi);
        }
        else
            Log.i("RayTest","mSBPlayDelayBar not null");
        mSBPlayDelayBar.getConfigBuilder()
                .min(-2f)
                .max(2f)
                .progress(iDelayValue)
                .sectionCount(4)
                .floatType()
                .sectionTextInterval(1)
                .trackColor(ContextCompat.getColor(this, R.color.color_blue_light))
                .secondTrackColor(ContextCompat.getColor(this, R.color.color_blue))
                .seekBySection()
                .showSectionText()
                .showThumbText()
                .sectionTextPosition(BubbleSeekBar.TextPosition.BELOW_SECTION_MARK)
                .build();
        mSBPlayDelayBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnActionUp(int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(int progress, float progressFloat) {
                InitDelayMV();
            }
        });
    }

    private void setAnim() {

        final int left = ((RelativeLayout.LayoutParams) mBack_tip.getLayoutParams()).leftMargin;
        final ValueAnimator animator = ValueAnimator.ofInt(left,left/4);
        animator.setDuration(1000);
        animator.setInterpolator(new BounceInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                int curValue = (int)animation.getAnimatedValue();
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mBack_tip.getLayoutParams();
                lp.leftMargin = curValue;
                mBack_tip.setLayoutParams(lp);
            }
        });

        animator.start();

    }

    @Override
    public void initEventListener() {
        mPlayMergeMv.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        recoding_re.setOnClickListener(this);
        mFinishResult.setOnClickListener(this);
        presenter.setRecordView(mVideoView);
        presenter.setMvView(mMvVideo);
    }

    @Override
    public void setRecordViewSize() {

    }

    @Override
    public void setMvViewSize() {

    }

    @Override
    public void showProgress() {
        showLoadingDialog();
    }

    @Override
    public void setMediaDuration(int duration) {
       // mProgressView.setMaxDuration(RECORD_TIME_MAX);

    }

    @Override
    public void isCompletion() {
        mVideoView.seekTo(0);
        mMvVideo.seekTo(0);
        mPlayMergeMv.setVisibility(View.VISIBLE);
        mProgressView.setProgress(0);
    }

    @Override
    public void UpdateActivateEvent(EventActivity eventActivity) {

    }

    @Override
    public void OnResumeReady() {

    }

    @Override
    public void stopPlayMv() {

    }

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


        Matrix txform = new Matrix();
        mVideoView.getTransform(txform);
        txform.setScale((float) newWidth / viewWidth, (float) newHeight / viewHeight);
        //txform.postRotate(10);          // just for fun
        txform.postTranslate(xoff, yoff);
        mVideoView.setTransform(txform);
    }

    protected void toastShort(@NonNull String msg) {
        CustomToast.makeCustomText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private Handler mHandle = new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(mMvVideo!=null){
                int position = mMvVideo.getCurrentPosition();

                int mMax = mMvVideo.getDuration();
                int sMax = mProgressView.getMax()+1;

                mProgressView.setProgress(position*sMax/mMax);
            }
        }
    };

    public class DelayThread extends Thread {
        int milliseconds;

        public DelayThread(int i){
            milliseconds = i;
        }
        public void run() {
            while(true){
                try {
                    sleep(milliseconds);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                mHandle.sendEmptyMessage(0);
            }
        }
    }
}



