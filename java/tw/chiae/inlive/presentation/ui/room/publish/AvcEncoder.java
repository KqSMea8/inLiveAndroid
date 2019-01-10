package tw.chiae.inlive.presentation.ui.room.publish;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.ByteBuffer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Environment;
import android.view.Surface;

import net.majorkernelpanic.streaming.hw.EncoderDebugger;
import net.majorkernelpanic.streaming.hw.NV21Convertor;

import tw.chiae.inlive.presentation.ui.main.mergefilm.Log;

@SuppressLint("NewApi")
public class AvcEncoder {

	private final int videoWidth;
	private final int videoHeight;
	private final int videoFramerate;
	private MediaCodec.BufferInfo mBufferInfo;
	private Context mContext;
	private int bitrate;
	private NV21Convertor mConvertor;
	private MediaCodec mMediaCodec;
	private EncoderCallback mCallback;
	String path = Environment.getExternalStorageDirectory() + "/easy.h264";

	public AvcEncoder(Context context, int width, int height, int framerate, int bitrate) {
		this.mContext  =  context;
		this.videoWidth = width;
		this.videoHeight = height;
		this.videoFramerate = framerate;
		initMediaCodec();
	}

	public void initMediaCodec() {
		Log.i("RayTest","init Codec...");
		int dgree = getDgree();
		bitrate = 2 * videoWidth * videoHeight * videoFramerate / 20;
		EncoderDebugger debugger = EncoderDebugger.debug(mContext, videoWidth, videoHeight);
		mConvertor = debugger.getNV21Convertor();
		try {
			mMediaCodec = MediaCodec.createByCodecName(debugger.getEncoderName());
			MediaFormat mediaFormat;
			if (dgree == 0) {
				mediaFormat = MediaFormat.createVideoFormat("video/avc", videoHeight, videoWidth);
			} else {
				mediaFormat = MediaFormat.createVideoFormat("video/avc", videoWidth, videoHeight);
			}
			mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, bitrate);
			mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, videoFramerate);
			mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT,
					debugger.getEncoderColorFormat());
			mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
			mMediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
			mMediaCodec.start();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private int getDgree() {
		int rotation =((Activity)mContext).getWindowManager().getDefaultDisplay().getRotation();
		int degrees = 0;
		switch (rotation) {
			case Surface.ROTATION_0:
				degrees = 0;
				break; // Natural orientation
			case Surface.ROTATION_90:
				degrees = 90;
				break; // Landscape left
			case Surface.ROTATION_180:
				degrees = 180;
				break;// Upside down
			case Surface.ROTATION_270:
				degrees = 270;
				break;// Landscape right
		}
		return degrees;
	}

	public void close() {
		mMediaCodec.stop();
		mMediaCodec.release();
		mMediaCodec = null;
	}
	byte[] mPpsSps = new byte[0];

	public void EnCodePreviewFrame(byte[] data, Camera camera) {
		if (data == null) {
			return;
		}
		ByteBuffer[] inputBuffers = mMediaCodec.getInputBuffers();
		ByteBuffer[] outputBuffers = mMediaCodec.getOutputBuffers();
		byte[] dst = new byte[data.length];
		Camera.Size previewSize = camera.getParameters().getPreviewSize();
		if (getDgree() == 0) {
			dst = EnCodeUtil.rotateNV21C(data, previewSize.width, previewSize.height, 270);
			//dst = EnCodeUtil.rotateNV21(data, previewSize.width, previewSize.height, 270);
			//dst = data;
/*			dst = EnCodeUtil.rotateNV21Degree90(data, previewSize.width, previewSize.height);
			dst = EnCodeUtil.rotateNV21Degree90(dst, previewSize.width, previewSize.height);
			dst = EnCodeUtil.rotateNV21Degree90(dst, previewSize.width, previewSize.height);*/
		} else {
			dst = data;
		}
		try {
			int bufferIndex = mMediaCodec.dequeueInputBuffer(5000000);
			if (bufferIndex >= 0) {
				inputBuffers[bufferIndex].clear();
				mConvertor.convert(dst, inputBuffers[bufferIndex]);
				long SysTime = System.nanoTime() / 1000;
				mMediaCodec.queueInputBuffer(bufferIndex, 0,
						inputBuffers[bufferIndex].position(),
						SysTime, 0);
				MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
				int outputBufferIndex = mMediaCodec.dequeueOutputBuffer(bufferInfo, 0);
				while (outputBufferIndex >= 0) {
					ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
					Log.i("RayBuffer","size:"+ bufferInfo.size);
					byte[] outData = new byte[bufferInfo.size];
					outputBuffer.get(outData);
					//记录pps和sps
					Log.i("RayTest","outData[4] == "+outData[4] );
					if (outData[0] == 0 && outData[1] == 0 && outData[2] == 0 && outData[3] == 1 && outData[4] == 103) {

						mPpsSps = outData;

					} else if (outData[0] == 0 && outData[1] == 0 && outData[2] == 0 && outData[3] == 1 && outData[4] == 101) {
						//在关键帧前面加上pps和sps数据
						byte[] iframeData = new byte[mPpsSps.length + outData.length];
						System.arraycopy(mPpsSps, 0, iframeData, 0, mPpsSps.length);
						System.arraycopy(outData, 0, iframeData, mPpsSps.length, outData.length);
						outData = iframeData;
						mCallback.EnCodeData(outData,1,SysTime);
					}else
						mCallback.EnCodeData(outData,0, SysTime);

					EnCodeUtil.save(outData, 0, outData.length, path, true);
					mMediaCodec.releaseOutputBuffer(outputBufferIndex, false);
					outputBufferIndex = mMediaCodec.dequeueOutputBuffer(bufferInfo, 0);
					//mCallback.addCallbackBuffer(outData);
				}
			} else {
				android.util.Log.e("easypusher", "No buffer available !");
			}
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String stack = sw.toString();
			android.util.Log.e("save_log", stack);
			e.printStackTrace();
		} finally {
			mCallback.addCallbackBuffer(dst);
		}
	}

	public void setEncodecallback(EncoderCallback callback){this.mCallback = callback;};
	public interface EncoderCallback {

		void addCallbackBuffer(byte[] dst);

		void EnCodeData(byte[] outData, int i, long sysTime);
	}
}
