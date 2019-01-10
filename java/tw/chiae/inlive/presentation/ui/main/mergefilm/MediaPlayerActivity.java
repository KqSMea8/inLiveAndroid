package tw.chiae.inlive.presentation.ui.main.mergefilm;

import android.Manifest;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.xw.repo.BubbleSeekBar;

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
import tw.chiae.inlive.presentation.ui.login.splash.PermissionsActivity;
import tw.chiae.inlive.presentation.ui.main.mergefilm.model.DeviceUtils;
import tw.chiae.inlive.presentation.ui.main.mergefilm.model.FileUtils;
import tw.chiae.inlive.presentation.ui.main.mergefilm.model.MediaObject;
import tw.chiae.inlive.presentation.ui.main.mergefilm.model.StringUtils;
import tw.chiae.inlive.presentation.ui.main.setting.CreateViewDialogFragment;
import tw.chiae.inlive.presentation.ui.widget.CustomToast;
import tw.chiae.inlive.presentation.ui.widget.lrcview.LrcView;
import tw.chiae.inlive.util.PermissionsChecker;


/**
 * 视频录制
 *
 * @author tangjun@yixia.com 感謝原作者的貢獻 在下李龍龍 抱拳了！
 */
public class MediaPlayerActivity extends AppCompatActivity implements OnClickListener, MediaPlayerPresenter.MediaPlayerInterface, CreateViewDialogFragment.dialogCallback {

    private static String PREFS_NAME = "DelayTime";
    private static final String DELAY_VALUE = "delayValue";
    private static final String SOUND_VALUE = "soundValue";
    public int RECORD_TIME_MAX = 1;

    public static final String FILE_PATH = "mvBtPath";
    public static final String STAR_ID = "starId";
    public static final String MV_NAME = "mvName";
    public static final String API_URL = "apiUrl";
    private MvVideoView mVideoView, mMvVideo;
    private ImageView mPlayMergeMv, onpauseimg, iv_back, mRecPauseImg;
    private RelativeLayout mBottomLayout;
    private int mBackgroundColorPress;
    private String recordPath;
    private String mvPath;
    private boolean mvIsReady = false;
    private boolean recordViewIsReady = false;
    private LaodingDilog mLoadingDilog;
    private MediaPlayerPresenter presenter;
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
    private CreateViewDialogFragment dialogFragment;
    private BubbleSeekBar mSBPlaySoundBar;
    private ImageView mAddSound;
    private ImageView mSubSound;
    private float iSoundValue = 0;
    private RelativeLayout rlStartMvView;
    private Toolbar rlToolbarView;
    private ImageView mHelper;
    private final int HELP_ID = 30;
    private boolean isMVPause = false;
    private boolean isVideoPause = false;
    private int MvTimeTmp = 0;
    private int VideoTimeTmp = 0 ;
    private RelativeLayout mTotalViedoHeight;
    private final float min_delay_value =-2.0f;
    private final float max_delay_value = 2.0f;

    private final float min_sound_value = 0.1f ;
    private final float max_sound_value = 1.0f;
    private boolean isfirstPlay = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);
        presenter = new MediaPlayerPresenter(this);
        PREFS_NAME = getIntent().getStringExtra(MV_NAME);
        presenter.ParseIntent(getIntent());

        dialogFragment = CreateViewDialogFragment.newInstance();
        dialogFragment.setDialogCallback(this);
        //setSeekBar();
        //checkSpace();

    }

    private void checkSpace() {
        boolean SpaceEnough = CheckSpaceAvailable();
        if(!SpaceEnough){
            Log.i("RayTest","空間不足");
            dialogFragment.showMsgDialog(getSupportFragmentManager(),"提示","您的儲存空間不足，請確保儲存空間大於0.5GB，才能順利錄製作品。",CreateViewDialogFragment.TYPE_SPACE_NOT_ENOUGH,false);
        }
    }



    public void showYesidoDialog() {
       /* android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
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
        builder.show();*/

        dialogFragment.showMsgDialog(getSupportFragmentManager(),"提示","建議在wifi環境下錄製合唱，因錄製將消耗較多流量，是否繼續?",CreateViewDialogFragment.TYPE_CHECK_WIFI,false);

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
        //int height = w * 4 / 3;
        int height = w-100;
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
        lp.height = w-100;
        /*lp.height = w;*/
        mMvVideo.setLayoutParams(lp);

        RelativeLayout.LayoutParams rl_start_mergemv_layout_params = (RelativeLayout.LayoutParams) rlStartMvView.getLayoutParams();
        rl_start_mergemv_layout_params.height = w-100;
        rlStartMvView.setLayoutParams(rl_start_mergemv_layout_params);

        RelativeLayout.LayoutParams surfaceLayoutParam = (RelativeLayout.LayoutParams) mVideoView.getLayoutParams();
        float ratio = (float)w / (float) h ;
        /*surfaceLayoutParam.height = w;*/
        surfaceLayoutParam.height = w-100;
        float surfceWidth= (float)w *ratio;
        surfaceLayoutParam.width = (int) surfceWidth;
        int left = (int) surfceWidth- (w-(w/2));
        surfaceLayoutParam.leftMargin = left;
        surfaceLayoutParam.rightMargin = -1*left;


        mVideoView.setLayoutParams(surfaceLayoutParam);
        Log.i("RayTest","w:"+surfaceLayoutParam.width+" h:"+surfaceLayoutParam.height+" "+ (int) surfceWidth+" left:"+left);

        RelativeLayout.LayoutParams MvViewlp = (RelativeLayout.LayoutParams) onpauseimg.getLayoutParams();
        MvViewlp.width = w ;
        /*MvViewlp.height = height;*/
        onpauseimg.setLayoutParams(MvViewlp);

        //presenter.setThumbnail(onpauseimg, true);
        //presenter.setThumbnail(mRecPauseImg, true);
        mMvVideo.setAlpha(0.8f);
        final RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mBottomLayout.getLayoutParams();
        final int screenHeight = DeviceUtils.getScreenHeight(this);

       /* RelativeLayout.LayoutParams toolbarparams = (RelativeLayout.LayoutParams) rlToolbarView.getLayoutParams();
        int toolbar_height = toolbarparams.height;
        Log.i("RayTest","getScreenWidth:"+screenHeight+" toolbar h:"+toolbar_height);
        int bottomTopMargin = screenHeight - (w-100)+toolbar_height ;
        layoutParams.topMargin = bottomTopMargin;
        Log.i("RayTest","topMargin:"+bottomTopMargin);*/
        rlToolbarView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rlToolbarView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                Log.i("RayTest","rlToolbarView :"+rlToolbarView.getMeasuredHeight());
            }
        });

        mMvVideo.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mMvVideo.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                Log.i("RayTest","mMvVideo :"+mMvVideo.getMeasuredHeight());
            }
        });


        RelativeLayout.LayoutParams totalParams = (RelativeLayout.LayoutParams) mTotalViedoHeight.getLayoutParams();
        mTotalViedoHeight.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mTotalViedoHeight.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mTotalViedoHeight.getMeasuredHeight();
                Log.i("RayTest","getMeasuredHeight:"+mTotalViedoHeight.getMeasuredHeight());
      /*          RelativeLayout.LayoutParams toolbarparams = (RelativeLayout.LayoutParams) rlToolbarView.getLayoutParams();
                int toolbar_height = toolbarparams.height;
                Log.i("RayTest","getScreenWidth:"+screenHeight+" toolbar h:"+toolbar_height);
                int bottomTopMargin = screenHeight - (w-100)+toolbar_height ;*/
                layoutParams.topMargin = mTotalViedoHeight.getMeasuredHeight();
                Log.i("RayTest","topMargin:"+ layoutParams.topMargin);
            }
        });
        Log.i("RayTest"," mTotalViedoHeight h:"+totalParams.height);

    }

    private boolean CheckSpaceAvailable() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        stat.restat(Environment.getDataDirectory().getPath());
        long bytesAvailable ;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            bytesAvailable = stat.getBlockSize()*stat.getAvailableBlocksLong();
        }else
            bytesAvailable = (long)stat.getBlockSize()*(long)stat.getAvailableBlocks();
        long megAvailable = bytesAvailable / 1048576;
        tw.chiae.inlive.presentation.ui.main.mergefilm.Log.i("RayTest"," 空間剩餘約："+megAvailable);
        if(megAvailable<=getResources().getInteger(R.integer.max_ks_space)){
            return false;
        }
        return true;
    }

    public static Intent createIntent(Context context, String filePath, String mvName, String starVideoUrl, int starId) {
        Intent it = new Intent(context, MediaPlayerActivity.class);
        it.putExtra(MV_NAME, mvName);
        it.putExtra(FILE_PATH, filePath);
        it.putExtra(STAR_ID, starId);
        it.putExtra(API_URL, starVideoUrl);
        return it;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_mergemv:
                Log.i("RayTest","start_mergemv");
                if(isMVPause && isVideoPause)
                    resumVideo();
                else
                    playRecordVideo();
                break;
            case R.id.iv_media_recorder_back:
                setResult(RESULT_CANCELED);
                startFinishCheck();
                //this.finish();
                break;
            case R.id.iv_recoding_re:
                deletRecordingRecording("是否確認重錄作品？重錄作品將會刪掉本次作品");
                break;
            case R.id.finish_result:
                showFinshDialog();
                break;
            case R.id.iv_delay_add:

                if(iDelayValue>mSBPlayDelayBar.getMax()) {
                    Log.i("RayTest","iv_delay_add "+iDelayValue);
                    iDelayValue = mSBPlayDelayBar.getMax();
                    mSBPlayDelayBar.setProgress(iDelayValue);
                }else {
                    if(mSBPlayDelayBar.getProgressFloat()<max_delay_value)
                        mSBPlayDelayBar.setProgress(mSBPlayDelayBar.getProgressFloat() + 0.1f);
                }

                break;
            case R.id.iv_delay_sub:
                Log.i("RayTest","iv_delay_sub "+iDelayValue);
                if(iDelayValue<mSBPlayDelayBar.getMin()) {
                    iDelayValue = mSBPlayDelayBar.getMin();
                    mSBPlayDelayBar.setProgress(iDelayValue);
                }
                else {
                    if(mSBPlayDelayBar.getProgressFloat()>min_delay_value)
                        mSBPlayDelayBar.setProgress(mSBPlayDelayBar.getProgressFloat() - 0.1f);
                }
                break;
            case R.id.iv_sound_add:
                Log.i("RayTest","iv_sound_add");
                if(iSoundValue>mSBPlaySoundBar.getMax()) {
                    iSoundValue = mSBPlaySoundBar.getMax();
                    mSBPlaySoundBar.setProgress(iSoundValue);
                }else
                if(mSBPlaySoundBar.getProgressFloat()<max_sound_value){
                    mSBPlaySoundBar.setProgress(mSBPlaySoundBar.getProgressFloat()+0.1f);
                }

                break;
            case R.id.iv_sound_sub:
                Log.i("RayTest","iv_sound_sub");
                if(iSoundValue<mSBPlaySoundBar.getMin()) {
                    iSoundValue = mSBPlaySoundBar.getMin();
                    mSBPlaySoundBar.setProgress(iSoundValue);
                }
                else
                if(mSBPlaySoundBar.getProgressFloat()>min_sound_value) {
                    mSBPlaySoundBar.setProgress(mSBPlaySoundBar.getProgressFloat() - 0.1f);
                }
                break;
            case R.id.rl_total_height:
                if(mVideoView.isPlaying()||mMvVideo.isPlaying()){
                    mVideoView.pause();
                    mMvVideo.pause();
                    mMvVideo.seekTo(0);
                    mVideoView.seekTo(0);
                    isVideoPause = true;
                    isMVPause = true;
                    mPlayMergeMv.setVisibility(View.VISIBLE);

                }

                break;
            case R.id.iv_helper:
                Log.i("RayTest","show helper");
                showHelpView();
                break;
        }
    }

    private void resumVideo() {
        Log.i("RayTest","resumVideo");
        showSoundMsg();
    }

    private void showHelpView() {
        presenter.checkEventUrl();


    }

    private void startFinishCheck() {
        dialogFragment.showMsgDialog(getSupportFragmentManager(),"提示","您確認關閉頁面嗎？仍會保存完整錄製作品。",CreateViewDialogFragment.TYPE_FINISH_MEDIA_PALYER,true);
    }


    private void InitDelayMV(int viewId) {




        switch (viewId){

            case R.id.rsv_large_bi:
                if(mVideoView.isPlaying()){
                    mVideoView.pause();
                    mVideoView.seekTo(0);
                }

                if(mMvVideo.isPlaying()){
                    mMvVideo.pause();
                    mMvVideo.seekTo(0);
                }

                saveParams();
                mPlayMergeMv.setVisibility(View.VISIBLE);
                showDelayMsg();

                break;

            case R.id.rsv_sound_bar:
                saveParams();
                //showSoundMsg();
                break;

        }

    }

    private void saveParams() {
        iDelayValue = mSBPlayDelayBar.getProgressFloat();
        iSoundValue = mSBPlaySoundBar.getProgressFloat();
        editor.putFloat(DELAY_VALUE, iDelayValue);
        editor.putFloat(SOUND_VALUE, iSoundValue);
        editor.commit();
        presenter.setVol(mVideoView, iSoundValue);
    }

    private void showSoundMsg() {
        Log.i("RayTest","setRecordView1");
        presenter.setRecordView(mVideoView,mSBPlaySoundBar.getProgressFloat());
    }

    private void showDelayMsg() {
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
        Log.i("RayTest","CheckNetWork1");
        if(!CheckNetWork()){
            Log.i("RayTest","CheckNetWork2");
            dialogFragment.showMsgDialog(getSupportFragmentManager(),"提示","您的網路斷線，請確保網路暢通，才能順利上傳作品。",CreateViewDialogFragment.TYPE_ERROR_NETWORK,true);
            return;

        }

     /*   android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setMessage("您確定要提交合唱作品嗎？\n提交後將會自動對外，無法修改，\n請確認節拍跟音量正確喔！");
        builder.setNegativeButton(getString(R.string.cancel), null);
        builder.setPositiveButton(getString(R.string.commit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishThis();
            }
        });
        builder.show();*/
        dialogFragment.showMsgDialog(getSupportFragmentManager(),"提示","您確定要提交合唱作品嗎？\n提交後將會自動對外，無法修改，\n請確認節拍跟音量正確喔！",CreateViewDialogFragment.TYPE_CHECK_SEND,true);

    }

    private Boolean CheckNetWork() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        //Boolean networkAvailableCheck = CheckNetWork();
        return activeNetworkInfo !=null &&  activeNetworkInfo.isConnected();
    }

    private void finishThis() {
        toastShort("正在進行提交");
        //setResult(SET_RESULT, new Intent().putExtra(SET_RESULT_MEGERPATH, playMegerMvPath));
        Log.i("RayTest","getProgressFloat : "+mSBPlayDelayBar.getProgressFloat());
        StartMergeFilm(presenter.getMvPath(), presenter.getStarID(),mSBPlayDelayBar.getProgressFloat(),mSBPlaySoundBar.getProgressFloat());
    }

    private void StartMergeFilm(String mvPath, int starID ,float offsetTime,float audioAdjust) {
        /*if (FileUtils.checkFile(presenter.getMvPath())) {
            File f = new File(presenter.getMvPath());
            if (f != null) {
                if (f.exists()) {
                    //已经存在，删除```````````
                    if (f.isDirectory())
                        FileUtils.deleteDir(f);
                    else
                        FileUtils.deleteFile(f);
                }
            }
        }*/
        editor.putFloat(DELAY_VALUE,0f);
        editor.putFloat(SOUND_VALUE,0.5f);
        editor.commit();
        Intent intent = new Intent();
        intent.putExtra("starId", starID);
        intent.putExtra("filePath", mvPath);
        intent.putExtra("timeOffset", offsetTime);
        intent.putExtra("audioAdjust", audioAdjust);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void deletRecordingRecording(String msg) {
       /* new AlertDialog.Builder(this)
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
                        null).setCancelable(false).show();*/
        dialogFragment.showMsgDialog(getSupportFragmentManager(),"提示",msg,CreateViewDialogFragment.TYPE_RECORD_MEDIA,true);
    }

    private void reCording() {
        /*if(!CheckPermissions())
            return;*/
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
        Intent it = MediaRecorderActivity.createIntent(this, presenter.getName(), presenter.getApiUrl(), presenter.getStarID(),true);
        startActivityForResult(it, KaraStar.REQUEST_VIDEO_RECORD);
        finish();

    }

/*    private static final int REQUEST_CODE = 0; // 请求码
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE

    };


    private boolean CheckPermissions() {
        PermissionsChecker mPermissionsChecker = new PermissionsChecker(getApplicationContext());
        if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
            startPermissionsActivity();
            dialogFragment.showMsgDialog(getSupportFragmentManager(),"提示",getString(R.string.permissions_error),CreateViewDialogFragment.TYPE_MV_DOWNLOAD_ERROR,false);
            return false;
        }else{
            return true;
        }
    }
    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
    }*/

    private void playRecordVideo() {
        Log.i("RayTest","getStatus: "+presenter.getStatus());
        if (presenter.getStatus()) {
            mPlayMergeMv.setVisibility(View.INVISIBLE);
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

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("RayTest",getClass().getSimpleName()+" onPause");
        if(mVideoView!=null && mVideoView.isPlaying()) {
            mVideoView.pause();
            isVideoPause = true;

        }
        if(mMvVideo!=null && mMvVideo.isPlaying()) {
            mMvVideo.pause();
            isMVPause = true;
        }
        //mPlayMergeMv.setVisibility(View.VISIBLE);
        MvTimeTmp = mMvVideo.getMediaPlayer().getCurrentPosition();
        VideoTimeTmp = mVideoView.getMediaPlayer().getCurrentPosition();
        stopPlayMv();
        Log.i("RayTest","mMvVideo getDuration:"+mMvVideo.getMediaPlayer().getDuration()+" mMvVideo getCurrentPosition:"+mMvVideo.getMediaPlayer().getCurrentPosition() );
        Log.i("RayTest","mVideoView getDuration:"+mVideoView.getMediaPlayer().getDuration()+" mVideoView getCurrentPosition:"+mVideoView.getMediaPlayer().getCurrentPosition() );
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("RayTest",getClass().getSimpleName()+" onResume");
        if(isVideoPause && isMVPause){
            /*mMvVideo.seekTo(MvTimeTmp);
            mVideoView.seekTo(VideoTimeTmp);
            Log.i("RayTest","mMvVideo seedto :"+MvTimeTmp );
            Log.i("RayTest","mVideoView seedto:"+VideoTimeTmp);*/
            //resumVideo();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("RayTest","Runnable Pause1");
                    mMvVideo.pause();
                    mVideoView.pause();

                    try {
                        mVideoView.seekTo(0);
                        mMvVideo.seekTo(0);
                        Log.i("RayTest","Runnable start");
                        mVideoView.start();
                        mMvVideo.start();

                        Thread.sleep(1000);
                        Log.i("RayTest","Runnable pause2");
                        mMvVideo.pause();
                        mVideoView.pause();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            });

            //mVideoView.seekTo(VideoTimeTmp);
            //mMvVideo.seekTo(MvTimeTmp);
            //mPlayMergeMv.performClick();

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
        /*android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setMessage("Mv下載出現異常，請退出界面重試");
        builder.setNegativeButton(getString(R.string.cancel), null);
        builder.setPositiveButton(getString(R.string.commit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MediaPlayerActivity.this.finish();
            }
        });
        builder.show();*/
        dialogFragment.showMsgDialog(getSupportFragmentManager(),"提示","建議在wifi環境下錄製合唱，因錄製將消耗較多流量，是否繼續?",CreateViewDialogFragment.TYPE_MV_DOWNLOAD_ERROR,false);
    }


    @Override
    public void initView() {
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

        mHelper = (ImageView)findViewById(R.id.iv_helper);
        rlStartMvView = (RelativeLayout) findViewById(R.id.rl_start_mergemv_layout);
        rlToolbarView = (Toolbar)findViewById(R.id.title_layout);
        mSBPlaySoundBar = (BubbleSeekBar) findViewById(R.id.rsv_sound_bar);
        mSubSound = (ImageView) findViewById(R.id.iv_sound_sub);
        mAddSound = (ImageView) findViewById(R.id.iv_sound_add);
        mTotalViedoHeight = (RelativeLayout)findViewById(R.id.rl_total_height);
        //mBottomLayout.setBackgroundColor(mBackgroundColorPress);
        mVideoView.setRotationY(180);
        mRecPauseImg.setRotationY(180);
        mSubDelay.setOnClickListener(this);
        mAddDelay.setOnClickListener(this);

        mSubSound.setOnClickListener(this);
        mAddSound.setOnClickListener(this);
        mHelper.setOnClickListener(this);
        mTotalViedoHeight.setOnClickListener(this);
        presenter.isCompletedInitView();
        setAnim();
    }


    private void setSeekBar() {
        settings = getSharedPreferences(PREFS_NAME, 0);
        editor = settings.edit();

        iDelayValue = settings.getFloat(DELAY_VALUE, 0);
        iSoundValue = settings.getFloat(SOUND_VALUE, 0.5f);
        Log.i("RayTest","get Value2 :"+iDelayValue);
        Log.i("RayTest","get sound2 :"+iSoundValue);
        mSBPlayDelayBar.getConfigBuilder()
                .min(min_delay_value)
                .max(max_delay_value)
                .progress(iDelayValue)
                .sectionCount(4)
                .floatType()
                .sectionTextInterval(1)
                .trackColor(ContextCompat.getColor(this, R.color.color_blue_light))
                .secondTrackColor(ContextCompat.getColor(this, R.color.color_blue))
                //.seekBySection()
                .showSectionText()
                .showThumbText()
                .sectionTextPosition(BubbleSeekBar.TextPosition.SIDES)
                .build();
        mSBPlayDelayBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress, float progressFloat) {
                iDelayValue = progressFloat;
                Log.i("RayTest","onProgressChanged: "+ progressFloat);
                InitDelayMV(mSBPlayDelayBar.getId());
            }

            @Override
            public void getProgressOnActionUp(int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(int progress, float progressFloat) {
                //InitDelayMV(mSBPlayDelayBar.getId());
            }
        });

        Log.i("RayTest","set progress iSoundValue: "+iSoundValue);
        mSBPlaySoundBar.getConfigBuilder()
                .min(min_sound_value)
                .max(max_sound_value)
                .progress(iSoundValue)
                .sectionCount(1)
                .floatType()
                //.sectionTextInterval(1)
                .trackColor(ContextCompat.getColor(this, R.color.color_blue_light))
                .secondTrackColor(ContextCompat.getColor(this, R.color.color_blue))
                //.seekBySection()
                //.showSectionText()
                .showThumbText()
                .sectionTextPosition(BubbleSeekBar.TextPosition.SIDES)
                .build();

        mSBPlaySoundBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress, float progressFloat) {
                iSoundValue = progressFloat;
                Log.i("RayTest","mSBPlaySoundBar onProgressChanged: "+ progressFloat);
                InitDelayMV(mSBPlaySoundBar.getId());
            }

            @Override
            public void getProgressOnActionUp(int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(int progress, float progressFloat) {
                //InitDelayMV(mSBPlaySoundBar.getId());
            }
        });
    }

    private void setAnim() {

 /*       final int left = ((RelativeLayout.LayoutParams) mBack_tip.getLayoutParams()).leftMargin;
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

        animator.start();*/

    }

    @Override
    public void initEventListener() {
        setSeekBar();
        mPlayMergeMv.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        recoding_re.setOnClickListener(this);
        mFinishResult.setOnClickListener(this);
        Log.i("RayTest","setRecordView2");
        presenter.setRecordView(mVideoView,mSBPlaySoundBar.getProgressFloat());
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

        for(EventActivity.EventItem eventItem :eventActivity.getEvents()){
            Log.i("RayTest","eventItem id: "+eventItem.getId()+" Name: "+eventItem.getName()+" url:"+eventItem.getEventUrl());
            if(eventItem.getId()==HELP_ID && !TextUtils.isEmpty(eventItem.getEventUrl())){
                dialogFragment.showHelpView(getSupportFragmentManager(),eventItem.getEventUrl(),CreateViewDialogFragment.TYPE_HELPER_WEB);
            }
        }
    }

    @Override
    public void OnResumeReady() {
        if(isfirstPlay) {
            isfirstPlay = false;
            return;
        }
        if(!mVideoView.isPlaying()||!mMvVideo.isPlaying() ){
            mMvVideo.start();
            mVideoView.start();
            isVideoPause = false;
            isVideoPause = false;
            mPlayMergeMv.setVisibility(View.GONE);
        }
    }

    @Override
    public void stopPlayMv() {
        InitDelayMV(mSBPlayDelayBar.getId());
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

    @Override
    public void onOKDialogcheck(Bundle bundle) {
        int type = bundle.getInt("type");
        switch (type){
            case CreateViewDialogFragment.TYPE_FINISH_MEDIA_PALYER:
                this.finish();
                break;
            case CreateViewDialogFragment.TYPE_PAUSE_MEDIA_RECORD:
                this.finish();
                break;
            case CreateViewDialogFragment.TYPE_SPACE_NOT_ENOUGH:
                dialogFragment.dismiss();
                this.finish();
                break;
            case CreateViewDialogFragment.TYPE_CHECK_WIFI:
                presenter.downLoadMv();
                break;
            case CreateViewDialogFragment.TYPE_MV_DOWNLOAD_ERROR:
                MediaPlayerActivity.this.finish();
                break;
            case CreateViewDialogFragment.TYPE_CHECK_SEND:
                finishThis();
                break;
            case CreateViewDialogFragment.TYPE_RECORD_MEDIA:
                reCording();
                break;
            default:
                break;
        }
    }

    @Override
    public void onCancelDialogcheck(Bundle mArgs) {

    }


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



