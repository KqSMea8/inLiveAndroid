package tw.chiae.inlive.presentation.ui.main.mergefilm;

import android.content.Context;
import android.content.res.AssetManager;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.util.Log;

/*import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;*/

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import tw.chiae.inlive.presentation.ui.main.mergefilm.model.FileUtils;
import tw.chiae.inlive.presentation.ui.main.mergefilm.model.MediaObject;
import tw.chiae.inlive.util.ffmpegutil.FFmpegInstance;


/**
 * 使用系统MediaRecorder录制，适合低端机
 *
 * @author yixia.com
 */
public class MediaRecorderSystem extends MediaRecorderBase implements MediaRecorder.OnErrorListener {

    /**
     * 系统MediaRecorder对象
     */
    private MediaRecorder mMediaRecorder;

    private Context mContext;
    /**
     * 水印地址
     */
    protected static final String watermark_left = VCamera.getVideoCachePath() + "left.png";
    protected static final String watermark_right = VCamera.getVideoCachePath() + "right.png";
    /**
     * 水印的名稱
     */
    private static final String WATER_LEFT = "watermarkright.png";
    private static final String WATER_RIGHT = "watermarkright.png";
    /**
     * mv的下載到本地的地址
     */
    private String downloadPath;
    /**
     * 合併出去的地址
     */
    private String mergePath;
    /**
     * ffmpeg工具類
     */
    //private FFmpegUtil fFmpegUtil;
    /**
     * ffmpeg是否加載成功
     */
    private boolean loadFFmpegSuccess;
    /**
     * 片段集合
     */
    private LinkedList<MediaObject.MediaPart> listParts;
    /**
     * 片段合成成功的個數
     */
    private int successPart;
    /**
     * 攝像頭的id
     */
    private int cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;

    public MediaRecorderSystem(Context context) {
        this.mContext = context;
        initWaterFile(context);
//        initFFmpegUtil();
        //testinit();
    }

    private void initWaterFile(Context context) {
        AssetManager asset = context.getAssets();
        try {
            InputStream inputStream = asset.open(WATER_LEFT);
            inputstreamtofile(inputStream, new File(watermark_left));
            inputStream = asset.open(WATER_RIGHT);
            inputstreamtofile(inputStream, new File(watermark_right));
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("mrl", "水印解析錯誤");
        }
    }

    public static void inputstreamtofile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
   /* FFmpeg mFFmpeg;
    public void testinit(){
        mFFmpeg= FFmpegInstance.getInstance(mContext);
        loadFFMpegBinary();
    }
    *//**
     * 加載FFmpeg庫
     *//*
    private void loadFFMpegBinary() {
        try {
            mFFmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
                }

                @Override
                public void onSuccess() {
                    super.onSuccess();
                }
            });
        } catch (FFmpegNotSupportedException e) {
        }
    }*/

    public void setOnMergeListener(OnMergeListener l) {
        this.mOnMergeListener = l;
    }

    /**
     * 开始录制
     */
    @Override
    public MediaObject.MediaPart startRecord() {
        Log.i("RayTest","startRecord2");
        //Camera.Size iSize = selectCameraSize(false, camera);
        //camera.setDisplayOrientation(90);

        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
   /*     int orientation_result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            orientation_result = (info.orientation + 270) % 360;
            orientation_result = (360 - orientation_result) % 360;  // compensate the mirror
        } else {  // back-facing
            orientation_result = (info.orientation - 270 + 360) % 360;
        }*/

        if(camera==null) {
            Log.i("RayTest", "camera null");
            return null;
        }
        camera.setDisplayOrientation(90);
        if(mMediaObject==null)
            Log.i("RayTest", "mMediaObject null");
        if(mSurfaceHolder==null)
            Log.i("RayTest", "mSurfaceHolder null");

        Log.i("RayTest", "mRecording:"+mRecording);
        if (mMediaObject != null && mSurfaceHolder != null && !mRecording) {
            Log.i("RayTest","MediaPart ＯＫ");
            MediaObject.MediaPart result = mMediaObject.buildMediaPart(mCameraId, ".mp4");

            try {
                if (mMediaRecorder == null) {
                    mMediaRecorder = new MediaRecorder();
                    mMediaRecorder.setOnErrorListener(this);
                } else {
                    mMediaRecorder.reset();
                }

                // Step 1: Unlock and set camera to MediaRecorder
                camera.unlock();
                mMediaRecorder.setCamera(camera);
                mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
                mMediaRecorder.setOrientationHint(270);
                // Step 2: Set sources
                mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);//before setOutputFormat()
                mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);//before setOutputFormat()

                mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                //设置视频输出的格式和编码
                CamcorderProfile mProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
                //Camera.Size iSize = selectCameraSize(false, camera);
                //                mMediaRecorder.setProfile(mProfile);
                mMediaRecorder.setVideoSize(640, 480);//after setVideoSource(),after setOutFormat()
                //mMediaRecorder.setAudioEncodingBitRate(44100);
                //mMediaRecorder.setAudioSamplingRate( samplingRate);

                mMediaRecorder.setAudioEncodingBitRate(384 * 1024);
                mMediaRecorder.setAudioSamplingRate(44100);
                mMediaRecorder.setVideoEncodingBitRate(665 * 1024);
                Log.i("RayTest","videoBitRate:"+mProfile.videoBitRate + " "+2 * 1024 * 1024);
               /* if (mProfile.videoBitRate > 2 * 1024 * 1024)
                    mMediaRecorder.setVideoEncodingBitRate(1 * 1024 * 1024);
                else
                    mMediaRecorder.setVideoEncodingBitRate(mProfile.videoBitRate);*/
                mMediaRecorder.setVideoFrameRate(mProfile.videoFrameRate);//after setVideoSource(),after setOutFormat()

                mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);//after setOutputFormat()
                mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);//after setOutputFormat()

                //mMediaRecorder.setVideoEncodingBitRate(800);

                // Step 4: Set output file
                mMediaRecorder.setOutputFile(result.mediaPath);

                // Step 5: Set the preview output
                //				mMediaRecorder.setOrientationHint(90);//加了HTC的手机会有问题

                Log.e("Yixia", "OutputFile:" + result.mediaPath);

                mMediaRecorder.prepare();
                mMediaRecorder.start();
                mRecording = true;
                return result;
            } catch (IllegalStateException e) {
                e.printStackTrace();
                Log.e("Yixia", "startRecord IllegalStateException", e);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Yixia", "startRecord IOException", e);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Yixia", "startRecord Exception", e);
            }
        }
        return null;
    }

    /**
     * 停止录制
     */
    @Override
    public void stopRecord() {
        Log.i("RayTest","System stopRecord");
        long endTime = System.currentTimeMillis();
        if (mMediaRecorder != null) {
            //设置后不会崩
            mMediaRecorder.setOnErrorListener(null);
            mMediaRecorder.setPreviewDisplay(null);
            try {
                mMediaRecorder.stop();
            } catch (IllegalStateException e) {
                Log.w("Yixia", "stopRecord", e);
            } catch (RuntimeException e) {
                Log.w("Yixia", "stopRecord", e);
            } catch (Exception e) {
                Log.w("Yixia", "stopRecord", e);
            }
        }

        if (camera != null) {
            try {
                camera.lock();
            } catch (RuntimeException e) {
                Log.e("Yixia", "stopRecord", e);
            }
        }

        // 判断数据是否处理完，处理完了关闭输出流
        if (mMediaObject != null) {
            MediaObject.MediaPart part = mMediaObject.getCurrentPart();
            if (part != null && part.recording) {
                part.recording = false;
                part.endTime = endTime;
                part.duration = (int) (part.endTime - part.startTime);
                part.cutStartTime = 0;
                part.cutEndTime = part.duration;
            }
        }
        mRecording = false;
    }

    /**
     * 释放资源
     */
    @Override
    public void release() {
        super.release();
        if (mMediaRecorder != null) {
            mMediaRecorder.setOnErrorListener(null);
            try {
                mMediaRecorder.release();
            } catch (IllegalStateException e) {
                Log.w("Yixia", "stopRecord", e);
            } catch (Exception e) {
                Log.w("Yixia", "stopRecord", e);
            }
        }
        mMediaRecorder = null;
    }

    @Override
    public void onError(MediaRecorder mr, int what, int extra) {
        try {
            if (mr != null)
                mr.reset();
        } catch (IllegalStateException e) {
            Log.w("Yixia", "stopRecord", e);
        } catch (Exception e) {
            Log.w("Yixia", "stopRecord", e);
        }
        if (mOnErrorListener != null)
            mOnErrorListener.onVideoError(what, extra);
    }

    /**
     * 不需要视频数据回调
     */
    @Override
    protected void setPreviewCallback() {
        //super.setPreviewCallback();
    }

    /**
     * 轉換片段視頻格式
     */
    @Override
    protected void concatVideoParts() {
        Log.i("mrl", "開始合成視頻片段");
        mOnEncodeListener.onEncodeStart();
        listParts = mMediaObject.getMedaParts();
        //将mp4转成ts
        Log.i("RayTest", "getMedaParts Size: "+mMediaObject.getMedaParts().size());
        //mOnMergeListener.onMergeComplete(mergePath);
        for (int i = 0, j = mMediaObject.getMedaParts().size(); i < j; i++) {
            //concatVideoParts(mMediaObject.getPart(i));
            //transcodingPart();
            Log.i("RayTest", "i: "+i+" "+ mMediaObject.getPart(i).mediaPath);
            if (FileUtils.checkFile(mMediaObject.getPart(i).mediaPath)) {
                Log.i("RayTest","File is exists!");
                mOnMergeListener.onMergeComplete(mMediaObject.getPart(i).mediaPath);
            }else{
                Log.i("RayTest","File is not exists!");
            }
        }
    }

    /**
     * 這裡處理ts文件,從錄製的mp4-》ts
     *
     * @param part
     */
    /*public void concatVideoParts(final MediaObject.MediaPart part) {
        String cmd = "";
        if (FileUtils.checkFile(part.mediaPath)) {
            final String ts = part.mediaPath.replace(".mp4", ".ts");
            FileUtils.deleteFile(ts);//删除
            cameraId = part.cameraId;
            cmd = String.format("-i %s -r 25 -vcodec copy -acodec copy -vbsf h264_mp4toannexb %s", part.mediaPath, ts);
//            cmd =String.format("-i /storage/3765-3331/Android/data/tw.chiae.inlive/cache/inlive/rec/test.mp4 -r 25 -vcodec copy -acodec copy -vbsf h264_mp4toannexb %s",ts);
//            cmd="-i /storage/3765-3331/Android/data/tw.chiae.inlive/cache/inlive/rec/test.mp4 -r 25 -vcodec copy -acodec copy -vbsf h264_mp4toannexb /storage/3765-3331/Android/data/tw.chiae.inlive/cache/inlive/rec/tesddt.ts";
            Log.i("mrl",cmd);
            String[] command = cmd.split(" ");
            try {
                mFFmpeg.execute(command, new FFmpegExecuteResponseHandler() {
                    @Override
                    public void onSuccess(String s) {
                        part.mediaPath = ts;//修改后缀名d
                        successPart++;
                        if (listParts.size() == successPart) {
                            transcodingPart();
                        }
                        //這裡不判斷為成功 因為整個操作都木有完成
                    }

                    @Override
                    public void onProgress(String s) {
                        Log.i("jindu", s);
//                        mOnEncodeListener.onEncodeProgress(s);
                    }

                    @Override
                    public void onFailure(String s) {
                        mOnEncodeListener.onEncodeError(s);
                        Log.i("mrl", "轉-》mp4出錯了" + s+"什麼鬼");
                    }

                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onFinish() {

                    }
                });
            } catch (FFmpegCommandAlreadyRunningException e) {
                e.printStackTrace();
                mOnEncodeListener.onEncodeError(e.toString());
                Log.i("mrl","FFmpegCommandAlreadyRunningException"+e.toString());
            }
        }
        //文件不存在或者转码失败，直接跳过
        part.mediaPath = "";
    }
*/

    /**
     * 處理合成mp4視頻 處理翻轉
     */
    public void transcodingPart() {
        String cmd = "";
        //处理翻转信息
        String vf = cameraId == Camera.CameraInfo.CAMERA_FACING_BACK ? "transpose=1" : "transpose=2,hflip";
        //合并ts流
        //-i /sdcard/file.mp4 -vf hflip -c:a copy /sdcard/flipped.mp4
        cmd = String.format("-i %s -vf hflip -c:a copy %s ", mMediaObject.getConcatYUV(),  mMediaObject.getOutputTempVideoPath());
        //cmd = String.format("-i %s -vf %s %s -acodec copy -absf aac_adtstoasc -f mp4 -movflags faststart %s", mMediaObject.getConcatYUV(), vf, FFMPEG_COMMAND_VCODEC, mMediaObject.getOutputTempVideoPath());
        String[] command = cmd.split(" ");
        Log.i("RayTest","command:"+cmd);
     /*   try {
            mFFmpeg.execute(command, new FFmpegExecuteResponseHandler() {
                @Override
                public void onSuccess(String s) {
                    Log.i("RayTest", "onSuccess: "+s);
                    //mOnEncodeListener.onEncodeComplete();
                }

                @Override
                public void onProgress(String s) {
                    Log.i("RayTest", "onProgress: "+s);
//                    mOnEncodeListener.onEncodeProgress(s);
                }

                @Override
                public void onFailure(String s) {
                    mOnEncodeListener.onEncodeError(s);
                    Log.i("mrl", "合成視頻" + s);
                    Log.i("RayTest", "合成視頻: "+s);
                }

                @Override
                public void onStart() {
                    //這裡分段視頻合併跟 mp4轉ts為同一個操作 公用一個回調監聽
                }

                @Override
                public void onFinish() {

                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            e.printStackTrace();
            mOnEncodeListener.onEncodeError(e.toString());
        }*/
    }
    /**
     * 主要是合并Mv
     */
   /* public void startMerge() {
        if (mMediaObject == null)
            return;
        mergeMvMedia();
        Log.i("RayTest","開始合成");
    }*/

    /**
     * 合并mv，這裡我為了提高用戶體驗感也是為了降低視頻的多次有損處理操作 所以一條命令走完 1.視頻切割2.創建模板 3.覆蓋視頻 4.水印添加 5.音頻合併
     */
    /*protected void mergeMvMedia() {
        mergePath = VCamera.getVideoCachePath() + System.currentTimeMillis() / 10000 + ".mp4";
        String cmd = String.format("-y -i %s -i %s -i %s -i %s -filter_complex [0]crop=iw/2:480:iw/2[c];[1]pad=iw*2[d];[d][c]overlay=w[e];[e][2]overlay=24:18[f];[f][3]overlay=W-w-24:18;amix %s"
                , mMediaObject.getOutputTempVideoPath()
                , downloadPath
                , watermark_left
                , watermark_right
                , mergePath);
        String[] commend = cmd.split(" ");
        try {
            mFFmpeg.execute(commend, new FFmpegExecuteResponseHandler() {
                @Override
                public void onSuccess(String s) {
                    mOnMergeListener.onMergeComplete(mergePath);
                }

                @Override
                public void onProgress(String s) {
                    Log.i("jindu", s);
                }

                @Override
                public void onFailure(String s) {
                    mOnMergeListener.onMergeError();
                    Log.i("mrl","合併視頻出現了錯誤"+s);
                }

                @Override
                public void onStart() {
                }

                @Override
                public void onFinish() {

                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            e.printStackTrace();
            mOnMergeListener.onMergeError();
        }
    }*/

/*
    public Camera.Size selectCameraSize(boolean is43, Camera camera) {
        List<Camera.Size> sizess = camera.getParameters().getSupportedPreviewSizes();
        List<Camera.Size> newSize = new ArrayList<>();
        for (Camera.Size size : sizess) {
            Log.i("mrlheight", "PreviewSizes" + size.width + "  " + size.height);
            if (is43) {
                if (size.width * 3 == size.height * 4) {
                    newSize.add(size);
                    return size;
                }
            } else {
                if (size.width * 9 == size.height * 16) {
                    newSize.add(size);
                    Log.i("RayTest", "add size:" + size.height + " w: "+size.width);
                    return size;
                }
            }
        }
        Log.i("RayTest", "selectCameraSize size:" +  sizess.get(0).height);
        return sizess.get(0);
    }
*/



    /**
     * 設置下載到的視頻的mv地址 請調用方法前判空
     *
     * @param downloadPath
     */
    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }

    public void setSuccessPart(int successPart) {
        this.successPart = successPart;
    }

}
