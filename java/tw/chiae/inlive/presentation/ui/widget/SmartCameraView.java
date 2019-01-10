package tw.chiae.inlive.presentation.ui.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import tw.chiae.inlive.presentation.ui.widget.magicfilter.base.gpuimage.GPUImageFilter;
import tw.chiae.inlive.presentation.ui.widget.magicfilter.utils.MagicFilterFactory;
import tw.chiae.inlive.presentation.ui.widget.magicfilter.utils.MagicFilterType;
import tw.chiae.inlive.presentation.ui.widget.magicfilter.utils.OpenGLUtils;

public class SmartCameraView extends GLSurfaceView implements GLSurfaceView.Renderer {

    private GPUImageFilter magicFilter;
    private SurfaceTexture surfaceTexture;
    private int mOESTextureId = OpenGLUtils.NO_TEXTURE;
    private int mSurfaceWidth;
    private int mSurfaceHeight;
    private int mPreviewWidth;
    private int mPreviewHeight;

    private float mInputAspectRatio;
    private float mOutputAspectRatio;
    private float[] mProjectionMatrix = new float[16];
    private float[] mSurfaceMatrix = new float[16];
    private float[] mTransformMatrix = new float[16];

    private Camera mCamera;
    private ByteBuffer mGLPreviewBuffer;
    private int mCamId = -1;
    private int mPreviewRotation = 90;
    private int mPreviewOrientation = Configuration.ORIENTATION_PORTRAIT;

    private Thread worker;
    private final Object writeLock = new Object();
    private ConcurrentLinkedQueue<IntBuffer> mGLIntBufferCache = new ConcurrentLinkedQueue<>();
    private PreviewCallback mPrevCb;

    private final String TAG = "SmartCameraView";
    private SmartCameraCallback mCallback;

    public SmartCameraView(Context context) {
        this(context, null);
    }

    public SmartCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setEGLContextClientVersion(2);
        setRenderer(this);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.e(TAG, "Run into onSurfaceCreated...");

        GLES20.glDisable(GL10.GL_DITHER);
        GLES20.glClearColor(0, 0, 0, 0);

        magicFilter = new GPUImageFilter(MagicFilterType.NONE);
        magicFilter.init(getContext().getApplicationContext());
        magicFilter.onInputSizeChanged(mPreviewWidth, mPreviewHeight);

        mOESTextureId = OpenGLUtils.getExternalOESTextureID();

        surfaceTexture = new SurfaceTexture(mOESTextureId);
        surfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                requestRender();
            }
        });

        // For camera preview on activity creation
        if (mCamera != null) {
            try {
                mCamera.setPreviewTexture(surfaceTexture);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        Log.i(TAG, "Run out of onSurfaceCreated...");
        if(mCallback!=null){
            mCallback.SmartCameraSurfaceCreated(gl,config);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        mSurfaceWidth = width;
        mSurfaceHeight = height;
        magicFilter.onDisplaySizeChanged(width, height);

        mOutputAspectRatio = width > height ? (float) width / height : (float) height / width;
        float aspectRatio = mOutputAspectRatio / mInputAspectRatio;

        if (width > height) {
            Matrix.orthoM(mProjectionMatrix, 0, -1.0f, 1.0f, -aspectRatio, aspectRatio, -1.0f, 1.0f);
        } else {
            Matrix.orthoM(mProjectionMatrix, 0, -aspectRatio, aspectRatio, -1.0f, 1.0f, -1.0f, 1.0f);
        }

        Log.i(TAG, "Run out onSurfaceChanged--");
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        surfaceTexture.updateTexImage();
        surfaceTexture.getTransformMatrix(mSurfaceMatrix);

        Matrix.multiplyMM(mTransformMatrix, 0, mSurfaceMatrix, 0, mProjectionMatrix, 0);
        magicFilter.setTextureTransformMatrix(mTransformMatrix);

        magicFilter.onDrawFrame(mOESTextureId);

        mGLIntBufferCache.add(magicFilter.getGLFboBuffer());
        synchronized (writeLock) {
            writeLock.notifyAll();
        }
    }

    public void setPreviewCallback(PreviewCallback cb) {
        mPrevCb = cb;
    }

    public int[] setPreviewResolution(int width, int height) {
        Log.i("RayTest","getHolder: w:"+width+" h:"+height);
        getHolder().setFixedSize(width, height);

        mCamera = openCamera();
        mPreviewWidth = width;
        mPreviewHeight = height;

        mCamera.getParameters().setPreviewSize(mPreviewWidth, mPreviewHeight);

        mGLPreviewBuffer = ByteBuffer.allocate(mPreviewWidth * mPreviewHeight * 4);

        mInputAspectRatio = mPreviewWidth > mPreviewHeight ?
                (float) mPreviewWidth / mPreviewHeight : (float) mPreviewHeight / mPreviewWidth;

        return new int[] { mPreviewWidth, mPreviewHeight };
    }



    public boolean setFilter(final MagicFilterType type) {
        if (mCamera == null) {
            Log.i("RayTest","setFilter false");
            return false;
        }

        queueEvent(new Runnable() {
            @Override
            public void run() {
                if (magicFilter != null) {
                    magicFilter.destroy();
                }
                magicFilter = MagicFilterFactory.initFilters(type);
                if (magicFilter != null) {
                    magicFilter.init(getContext().getApplicationContext());
                    magicFilter.onInputSizeChanged(mPreviewWidth, mPreviewHeight);
                    magicFilter.onDisplaySizeChanged(mSurfaceWidth, mSurfaceHeight);
                }
            }
        });
        requestRender();
        return true;
    }

    private void deleteTextures() {
        if (mOESTextureId != OpenGLUtils.NO_TEXTURE) {
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    GLES20.glDeleteTextures(1, new int[]{ mOESTextureId }, 0);
                    mOESTextureId = OpenGLUtils.NO_TEXTURE;
                }
            });
        }
    }

    public void setCameraId(int id) {
        mCamId = id;
    }

    public void setPreviewOrientation(int orientation, int degree) {
        mPreviewOrientation = orientation;
        mPreviewRotation = degree;
        Log.i("RayTest","degree:"+degree);
        mCamera.setDisplayOrientation(degree);

    }

    public int getCameraId() {
        return mCamId;
    }

    public boolean startCamera() {
        worker = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.interrupted()) {
                    while (!mGLIntBufferCache.isEmpty()) {
                        IntBuffer picture = mGLIntBufferCache.poll();
                        mGLPreviewBuffer.asIntBuffer().put(picture.array());
                        mPrevCb.onGetRgbaFrame(mGLPreviewBuffer.array(), mPreviewWidth, mPreviewHeight);
                    }
                    // Waiting for next frame
                    synchronized (writeLock) {
                        try {
                            // isEmpty() may take some time, so we set timeout to detect next frame
                            writeLock.wait(500);
                        } catch (InterruptedException ie) {
                            worker.interrupt();
                        }
                    }
                }
            }
        });
        worker.start();

        if (mCamera == null) {
            mCamera = openCamera();
            if (mCamera == null) {
                return false;
            }
        }

        Camera.Parameters params = mCamera.getParameters();

        List<String> supportedFocusModes = params.getSupportedFocusModes();
        if (!supportedFocusModes.isEmpty()) {
            if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            } else {
                params.setFocusMode(supportedFocusModes.get(0));
            }
        }
        Camera.Size size = selectCameraSize(false, mCamera);
        //params.setPictureSize(size.width, size.height);
        params.setPreviewSize(size.width, size.height);
        SetCameraFPS(params);
        //int VFPS = 15;
        //int[] range = adaptFpsRange(VFPS, params.getSupportedPreviewFpsRange());
        //params.setPreviewFpsRange(range[0], range[1]);
        //params.setPreviewFormat(ImageFormat.NV21);
        params.setPreviewFormat(PixelFormat.YCbCr_420_SP);
        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        //params.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
        params.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
        if (!params.getSupportedFocusModes().isEmpty()) {
            params.setFocusMode(params.getSupportedFocusModes().get(0));
        }
        mCamera.setParameters(params);

        mCamera.setDisplayOrientation(mPreviewRotation);

        try {
            mCamera.setPreviewTexture(surfaceTexture);
        } catch (IOException e) {
            e.printStackTrace();
        }

        GetParameters(mCamera);

        mCamera.startPreview();

        return true;
    }


    private void SetCameraFPS(Camera.Parameters parameters)
    {
        if ( parameters == null )
            return;

        int[] findRange = null;

        int defFPS = 20*1000;

        List<int[]> fpsList = parameters.getSupportedPreviewFpsRange();
        if ( fpsList != null && fpsList.size() > 0 )
        {
            for ( int i = 0; i < fpsList.size(); ++i )
            {
                int[] range = fpsList.get(i);
                if ( range != null
                        && Camera.Parameters.PREVIEW_FPS_MIN_INDEX <  range.length
                        && Camera.Parameters.PREVIEW_FPS_MAX_INDEX < range.length )
                {
                    Log.i(TAG, "Camera index:" + i + " support min fps:" + range[Camera.Parameters.PREVIEW_FPS_MIN_INDEX]);

                    Log.i(TAG, "Camera index:" + i + " support max fps:" + range[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]);

                    if ( findRange == null )
                    {
                        if ( defFPS <= range[Camera.Parameters.PREVIEW_FPS_MAX_INDEX] )
                        {
                            findRange = range;

                            Log.i(TAG, "Camera found appropriate fps, min fps:" + range[Camera.Parameters.PREVIEW_FPS_MIN_INDEX]
                                    + " ,max fps:" + range[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]);
                        }
                    }
                }
            }
        }

        if ( findRange != null  )
        {
            parameters.setPreviewFpsRange(findRange[Camera.Parameters.PREVIEW_FPS_MIN_INDEX], findRange[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]);
        }
    }

    public void stopCamera() {
        if (worker != null) {
            worker.interrupt();
            try {
                worker.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                worker.interrupt();
            }
            mGLIntBufferCache.clear();
            worker = null;
        }

        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    public void GetParameters(Camera camera) {
        List<Camera.Size> pictureSizes = camera.getParameters().getSupportedPictureSizes();
        List<Camera.Size> previewSizes = camera.getParameters().getSupportedPreviewSizes();
        Camera.Size psize;
        for (int i = 0; i < pictureSizes.size(); i++) {
            psize = pictureSizes.get(i);
            Log.i(TAG,psize.width+" x "+psize.height);
        }
        for (int i = 0; i < previewSizes.size(); i++) {
            psize = previewSizes.get(i);
            Log.i(TAG,psize.width+" x "+psize.height);
        }
    }

    private Camera openCamera() {
        Camera camera;
        if (mCamId < 0) {
            Camera.CameraInfo info = new Camera.CameraInfo();

            int numCameras = Camera.getNumberOfCameras();
            int frontCamId = -1;
            int backCamId = -1;
            for (int i = 0; i < numCameras; i++) {
                Camera.getCameraInfo(i, info);
                if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    backCamId = i;
                } else if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    frontCamId = i;
                    break;
                }
            }
            if (frontCamId != -1) {
                mCamId = frontCamId;
            } else if (backCamId != -1) {
                mCamId = backCamId;
            } else {
                mCamId = 0;
            }
        }
        camera = Camera.open(mCamId);

        return camera;
    }

    private int[] adaptFpsRange(int expectedFps, List<int[]> fpsRanges) {
        expectedFps *= 1000;
        int[] closestRange = fpsRanges.get(0);
        int measure = Math.abs(closestRange[0] - expectedFps) + Math.abs(closestRange[1] - expectedFps);
        for (int[] range : fpsRanges) {
            if (range[0] <= expectedFps && range[1] >= expectedFps) {
                int curMeasure = Math.abs(range[0] - expectedFps) + Math.abs(range[1] - expectedFps);
                if (curMeasure < measure) {
                    closestRange = range;
                    measure = curMeasure;
                }
            }
        }
        return closestRange;
    }

    public Camera getCamera() {
        return mCamera;
    }

    public interface PreviewCallback {

        void onGetRgbaFrame(byte[] data, int width, int height);
    }


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

    public void setCameraCallbcak(SmartCameraCallback callback){this.mCallback = callback;}
    public interface SmartCameraCallback{

        void SmartCameraSurfaceCreated(GL10 gl, EGLConfig config);
    }

    public boolean onFocus(Point point, Camera.AutoFocusCallback callback) {
        if (mCamera == null) {
            return false;
        }

        Camera.Parameters parameters = null;
        try {
            parameters = mCamera.getParameters();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        //不支持设置自定义聚焦，则使用自动聚焦，返回

        if(Build.VERSION.SDK_INT >= 14) {

            if (parameters.getMaxNumFocusAreas() <= 0) {
                return focus(callback);
            }

            Log.i(TAG, "onCameraFocus:" + point.x + "," + point.y);

            //定点对焦
            List<Camera.Area> areas = new ArrayList<Camera.Area>();
            int left = point.x - 300;
            int top = point.y - 300;
            int right = point.x + 300;
            int bottom = point.y + 300;
            left = left < -1000 ? -1000 : left;
            top = top < -1000 ? -1000 : top;
            right = right > 1000 ? 1000 : right;
            bottom = bottom > 1000 ? 1000 : bottom;
            areas.add(new Camera.Area(new Rect(left, top, right, bottom), 100));
            parameters.setFocusAreas(areas);
            try {
                //本人使用的小米手机在设置聚焦区域的时候经常会出异常，看日志发现是框架层的字符串转int的时候出错了，
                //目测是小米修改了框架层代码导致，在此try掉，对实际聚焦效果没影响
                mCamera.setParameters(parameters);
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return false;
            }
        }


        return focus(callback);
    }

    private boolean focus(Camera.AutoFocusCallback callback) {
        try {
            mCamera.autoFocus(callback);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
