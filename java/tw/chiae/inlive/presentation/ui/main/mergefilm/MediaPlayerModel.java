package tw.chiae.inlive.presentation.ui.main.mergefilm;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.*;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

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
import java.util.Locale;

import tw.chiae.inlive.R;
import tw.chiae.inlive.nohttp.CallServer;

import static tw.chiae.inlive.presentation.ui.main.mergefilm.MediaPlayerActivity.API_URL;
import static tw.chiae.inlive.presentation.ui.main.mergefilm.MediaPlayerActivity.FILE_PATH;
import static tw.chiae.inlive.presentation.ui.main.mergefilm.MediaPlayerActivity.MV_NAME;
import static tw.chiae.inlive.presentation.ui.main.mergefilm.MediaPlayerActivity.STAR_ID;

/**
 * Created by rayyeh on 2017/4/13.
 */

public class MediaPlayerModel {
    private final MediaPlayerPresenter presenter;
    private boolean RecordStatus = false;
    private boolean MvStatus = false;
    private Intent itData;
    private DownloadRequest mDownloadRequest;
    private boolean isPlaying = false;

    private DownloadListener downloadListener = new DownloadListener() {

        @Override
        public void onStart(int what, boolean isResume, long beforeLength, Headers headers, long allCount) {
        }

        @Override
        public void onDownloadError(int what, Exception exception) {
            presenter.hideProgress();
            android.util.Log.i("mrl", "onDownloadError 下載出現異常" + exception);
            presenter.showErrorMvDialog();
        }

        @Override
        public void onProgress(int what, int progress, long fileCount) {
            android.util.Log.i("mrl", "當前進度" + progress);
            String str = presenter.getString(R.string.loading_progress_text, Integer.toString(progress));
            presenter.setLoadingProgress(str);
        }

        @Override
        public void onFinish(int what, String filePath) {
            //下載結束了
            presenter.hideProgress();

        }

        @Override
        public void onCancel(int what) {
        }

        private void updateProgress(int progress) {
        }
    };

    private DownloadListener downloadListenerLrc = new DownloadListener() {

        @Override
        public void onStart(int what, boolean isResume, long beforeLength, Headers headers, long allCount) {
        }

        @Override
        public void onProgress(int what, int progress, long fileCount) {

        }

        @Override
        public void onFinish(int what, String filePath) {
         /*   lrcPath = filePath;
            File file = new File(lrcPath);
            if (file != null)
                mLrc.loadLrc(file);
            mLrc.onDrag(0);*/
        }

        @Override
        public void onCancel(int what) {

        }

        @Override
        public void onDownloadError(int what, Exception exception) {

            String message = presenter.getString(R.string.download_error);
            String messageContent;
            if (exception instanceof ServerError) {
                messageContent = presenter.getString(R.string.download_error_server);
            } else if (exception instanceof NetworkError) {
                messageContent = presenter.getString(R.string.download_error_network);
            } else if (exception instanceof StorageReadWriteError) {
                messageContent = presenter.getString(R.string.download_error_storage);
            } else if (exception instanceof StorageSpaceNotEnoughError) {
                messageContent = presenter.getString(R.string.download_error_space);
            } else if (exception instanceof TimeoutError) {
                messageContent = presenter.getString(R.string.download_error_timeout);
            } else if (exception instanceof UnKnownHostError) {
                messageContent = presenter.getString(R.string.download_error_un_know_host);
            } else if (exception instanceof URLError) {
                messageContent = presenter.getString(R.string.download_error_url);
            } else {
                messageContent = presenter.getString(R.string.download_error_un);
            }
            message = String.format(Locale.getDefault(), message, messageContent);
//            mTvResult.setText(message);
        }

    };

    public MediaPlayerModel(MediaPlayerPresenter mediaPlayerPresenter) {
        this.presenter = mediaPlayerPresenter;
    }

    public void setRecordViewIsReady(boolean status) {
        this.RecordStatus = status;
    }

    public void setMvIsReady(boolean status) {
        this.MvStatus = status;
    }

    public String getRecordPath() {
        String recordPath = itData.getStringExtra(FILE_PATH);
        return recordPath;
    }

    public void setIntent(Intent intent) {
        this.itData = intent;
    }

    public String getMvPath() {
        return VCamera.getVideoCachePath()+itData.getStringExtra(MV_NAME)+".mp4";
    }

    public String getServerFile() {
        return itData.getStringExtra(API_URL)+itData.getStringExtra(MV_NAME)+".mp4";
    }

    public boolean getNetWorkType(Context context) {
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
            MediaPlayerActivity activity = (MediaPlayerActivity) context;
            activity.toastShort("當前無網絡，請檢查網絡");
            return false;
        }
        return false;
    }

    public void setPlayStatus(boolean b) {
        isPlaying = b;
    }

    public void downLoadMv() {

        // 开始下载了，但是任务没有完成，代表正在下载，那么暂停下载。
        if (mDownloadRequest != null && mDownloadRequest.isStarted() && !mDownloadRequest.isFinished()) {
            // 暂停下载。
            mDownloadRequest.cancel();
        } else if (mDownloadRequest == null || mDownloadRequest.isFinished()) {// 没有开始或者下载完成了，就重新下载。
            String[] fileName = getMvPath().split("/");
            mDownloadRequest = NoHttp.createDownloadRequest( getServerFile(), VCamera.getVideoCachePath(), fileName[fileName.length - 1], true, false);
            CallServer.getDownloadInstance().add(0, mDownloadRequest, downloadListener);
            // 添加到队列，在没响应的时候让按钮不可用。這裡取消所有動作除了強制退出
        }
    }
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

        //bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
        return bitmap;
    }

    public void setThumbnail(final ImageView view, final boolean isVisible) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(view.getId() == R.id.onpauseimg) {

                    if(!isPlaying)
                        view.setImageBitmap(createVideoThumbnail(getMvPath(), view.getWidth(), view.getHeight()));
                }else {

                    view.setImageBitmap(createVideoThumbnail(getRecordPath(), view.getWidth(), view.getHeight()));

                }
                if(isVisible)
                    view.setVisibility(View.VISIBLE);
                else
                    view.setVisibility(View.GONE);
            }
        });

    }


    public void CancleRequest() {
        if(mDownloadRequest!=null)
            mDownloadRequest.cancel();
    }

    public boolean getStatus() {
        Log.i("RayTest","MvStatus:"+MvStatus+" RecordStatus:"+RecordStatus);
        if(MvStatus && RecordStatus)
            return true;
        else
            return false;
    }

    public String getName() {
        return itData.getStringExtra(MV_NAME);
    }
    public String getApiUrl() {
        return itData.getStringExtra(API_URL);
    }
    public int getStarID() {
        return itData.getIntExtra(STAR_ID,-1);
    }
}
