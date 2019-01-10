package tw.chiae.inlive.presentation.ui.main.mergefilm;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.*;
import android.widget.ImageView;

import java.io.IOException;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import tw.chiae.inlive.R;
import tw.chiae.inlive.presentation.ui.main.EventManager;
import tw.chiae.inlive.presentation.ui.main.mergefilm.model.FileUtils;

import static android.R.attr.path;
import static tw.chiae.inlive.presentation.ui.main.mergefilm.MediaPlayerActivity.API_URL;
import static tw.chiae.inlive.presentation.ui.main.mergefilm.MediaPlayerActivity.MV_NAME;

/**
 * Created by rayyeh on 2017/4/13.
 */

public class MediaPlayerPresenter {

    private final CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    private final MediaPlayerModel mModel;
    private final EventManager eventManager;
    private MediaPlayerActivity mActivity;
    public Object getTimeOffset;

    public MediaPlayerPresenter(Context context) {
        this.mActivity = (MediaPlayerActivity) context;
        mModel = new MediaPlayerModel(this);
        eventManager = new EventManager();
        this.mUiInterface = (MediaPlayerActivity) context;
    }

    public void isCompletedInitView() {
        mActivity.initEventListener();
    }

    public void isCompleteInitListener() {
        mActivity.setRecordViewSize();
        mActivity.setMvViewSize();
    }

    public void setRecordView(final MvVideoView mVideoView, float soundVal  ) {
        //Note that the passed volume values are raw scalars in range 0.0 to 1.0.
        Log.i("RayTest","set sound val:"+soundVal);
        try {
           Log.i("RayTest","path:"+mModel.getRecordPath());
            // set record view
            mVideoView.setDataSource(mModel.getRecordPath());
            mVideoView.setVolume(soundVal, soundVal);
            mVideoView.setLooping(false);
            // set mv view
            mVideoView.prepare(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {
                    // mp.start();
                    mVideoView.seekTo(0);
                    mModel.setRecordViewIsReady(true);
                    mActivity.OnResumeReady();
                }
            });

            mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                   Log.i("RayTest", "mVideoView 網絡傲嬌了，請等待視頻下載");
                    return false;
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
            android.util.Log.i("RayTest", "網絡傲嬌了，請等待視頻下載");
        } catch (IllegalStateException e){
            android.util.Log.i("RayTest", "網絡傲嬌了，IllegalStateException");
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
                    android.util.Log.i("RayTest", "mMvVideo 網絡傲嬌了，請等待視頻下載");
                    return false;
                }
            });

            mMvVideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.i("RayTest","onCompletion"+mp.getDuration()+"   "+mp.getCurrentPosition());
                    mActivity.isCompletion();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
            android.util.Log.i("RayTest", "網絡傲嬌了，請等待視頻下載2");
        }
    }

    public void ParseIntent(Intent intent) {
        mModel.setIntent(intent);
        //mvPath ="/storage/emulated/0/Android/data/tw.chiae.inlive/cache/inlive/rec/stars-003.mp4";
        if(!FileUtils.checkFile(mModel.getMvPath())){
            initplayerNetworkMV();
        }
        else {
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

    public void checkEventUrl() {
        Subscription subscription = eventManager.checkActivateEvent()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<EventActivity>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(EventActivity eventActivity) {
                        mActivity.UpdateActivateEvent(eventActivity);
                    }
                });
        addSubscription(subscription);
    }

    public void hideProgress() {
        mActivity.hideProgress();
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
        unsubscribeTasks();
    }

    public void setThumbnail(ImageView view, boolean b) {
        mModel.setThumbnail(view,b);
    }

    public boolean getStatus() {
        return mModel.getStatus();
    }

    public String getName() {
        return mModel.getName();
    }

    public String getApiUrl() {
        return mModel.getApiUrl();
    }

    public int getStarID() {
        return mModel.getStarID();
    }

    public String getMvPath() {
        return mModel.getRecordPath();
    }




    private MediaPlayerActivity mUiInterface;


    public void unsubscribeTasks() {
        mCompositeSubscription.unsubscribe();

    }

    /**
     * 每次发起时加入CompositeSubscription,这个方法应该内部调用，所以使用protected.
     */
    protected void addSubscription(Subscription subscription) {
        mCompositeSubscription.add(subscription);
    }

    protected MediaPlayerActivity getUiInterface() {
        if (mUiInterface == null) {
            throw new IllegalStateException("UiInterface is not initialized correctly.");
        }
        return mUiInterface;
    }

    /**
     * Usage:
     * Observable.compose(applySchedulers)
     */
    protected final <T> Observable.Transformer<T, T> applyAsySchedulers() {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> observable) {
                return observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public void setVol(MvVideoView mVideoView , float iSoundValue) {
        Log.i("RayTest","setVol: "+iSoundValue);
        mVideoView.setVolume(iSoundValue, iSoundValue);
    }

    public interface MediaPlayerInterface{

        void initView();

        void initEventListener();

        void setRecordViewSize();

        void setMvViewSize();

        void showProgress();

        void setMediaDuration(int duration);

        void isCompletion();

        void UpdateActivateEvent(EventActivity eventActivity);

        void OnResumeReady();

        void stopPlayMv();
    }
}
