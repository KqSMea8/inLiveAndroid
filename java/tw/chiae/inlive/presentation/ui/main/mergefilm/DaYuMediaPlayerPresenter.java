package tw.chiae.inlive.presentation.ui.main.mergefilm;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;

import tw.chiae.inlive.presentation.ui.main.mergefilm.MediaPlayerActivity;
import tw.chiae.inlive.presentation.ui.main.mergefilm.MediaPlayerModel;
import tw.chiae.inlive.presentation.ui.main.mergefilm.MvVideoView;
import tw.chiae.inlive.presentation.ui.main.mergefilm.model.FileUtils;

import static tw.chiae.inlive.presentation.ui.main.mergefilm.DaYuMediaPlayerActivity.STAR_ID;

/**
 * Created by rayyeh on 2017/4/13.
 */

public class DaYuMediaPlayerPresenter {

    private final DaYuMediaPlayerModel mModel;
    private DaYuMediaPlayerActivity mActivity;
    private String UserID;

    public DaYuMediaPlayerPresenter(Context context) {
        this.mActivity = (DaYuMediaPlayerActivity) context;
        mModel = new DaYuMediaPlayerModel(this);

    }

    public void isCompletedInitView() {
        mActivity.initEventListener();
    }

    public void isCompleteInitListener() {
        mActivity.setRecordViewSize();
        mActivity.setMvViewSize();
    }

    public void setRecordView(final MvVideoView mVideoView  ) {
        try {
           Log.i("RayTest","RecordPath path:"+mModel.getRecordPath());
            // set record view
            mVideoView.setDataSource(mModel.getRecordPath());
            mVideoView.setVolume(1, 1);
            mVideoView.setLooping(false);
            // set mv view
            mVideoView.prepare(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {
                    // mp.start();
                    mVideoView.seekTo(0);
                    mModel.setRecordViewIsReady(true);
                }
            });

            mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                   tw.chiae.inlive.presentation.ui.main.mergefilm.Log.i("RayTest", "mVideoView 網絡傲嬌了，請等待視頻下載");
                    return false;
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
            Log.i("RayTest", "網絡傲嬌了，請等待視頻下載");
        }
    }

    public void setMvView(final MvVideoView mMvVideo) {
        try {
            Log.i("RayTest","mvPath:"+mModel.getMvPath());
            mMvVideo.setDataSource(mModel.getMvPath());
            mMvVideo.setVolume(0.4f, 0.4f);
            mMvVideo.setLooping(false);
            mMvVideo.prepare(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mModel.setMvIsReady(true);
                    mMvVideo.seekTo(0);
                    mActivity.setMediaDuration(mp.getDuration());
                }
            });

            mMvVideo.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
//                    toastShort("網絡傲嬌了，請等待視頻下載");
                    Log.i("RayTest", "mMvVideo 網絡傲嬌了，請等待視頻下載");
                    return false;
                }
            });

            mMvVideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    tw.chiae.inlive.presentation.ui.main.mergefilm.Log.i("RayTest","onCompletion"+mp.getDuration()+"   "+mp.getCurrentPosition());
                    mActivity.isCompletion();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
            Log.i("RayTest", "網絡傲嬌了，請等待視頻下載2");
        }
    }

    public void ParseIntent(Intent intent) {
        this.UserID = intent.getStringExtra(STAR_ID);
        Log.i("RayTest","UserID:"+UserID);
        mModel.setIntent(intent);
        //mvPath ="/storage/emulated/0/Android/data/tw.chiae.inlive/cache/inlive/rec/stars-003.mp4";
        if(!FileUtils.checkFile(mModel.getMvPath())){
            Log.i("RayTest", "initplayerNetworkMV1"+mModel.getMvPath());
            initplayerNetworkMV();
        }
        else {
            Log.i("RayTest", "initplayerNetworkMV2");
            mActivity.initView();
            mActivity.initVideoView();
        }


        Log.i("RayTest","mvPath"+mModel.getMvPath());

    }

    private void initplayerNetworkMV() {
        if (mModel.getNetWorkType(mActivity)) {
            mActivity.showProgress();
            mModel.downLoadMv();
            //downLoadLrc(getIntent().getStringExtra(LRC_BT_PATH));
        } else {
            mActivity.showYesidoDialog();
        }
    }

    public void hideProgress() {
        mActivity.hideProgress();
        mActivity.initView();
    }

    public String getString(int res) {
       return mActivity.getString(res);
    }

    public void showErrorMvDialog() {
        mActivity.showErrorMvDialog();
    }

    public String getString(int res, Object obj) {
       return mActivity.getString(res,obj);
    }

    public void downLoadMv() {
        mModel.downLoadMv();
    }

    public void setLoadingProgress(String string) {
        mActivity.setLoadingProgress(string);
    }

    public void setPlayStatus(boolean b) {
        mModel.setPlayStatus(b);
    }

    public void CancleRequest() {
        mModel.CancleRequest();
    }

    public void setThumbnail(ImageView view, boolean b) {
        mModel.setThumbnail(view,b);
    }

    public boolean getStatus() {
        return mModel.getStatus();
    }

    public int getStarID() {
        return mModel.getStarID();
    }

    public String getMvPath() {
        return mModel.getRecordPath();
    }
    public String getRecordFileName() {
        return mModel.getRecordName();
    }

    public interface MediaPlayerInterface{

        void initView();

        void initEventListener();

        void setRecordViewSize();

        void setMvViewSize();

        void showProgress();

        void setMediaDuration(int duration);

        void isCompletion();
    }
}
