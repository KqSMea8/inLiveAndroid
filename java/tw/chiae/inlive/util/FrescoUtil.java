package tw.chiae.inlive.util;

import com.facebook.binaryresource.BinaryResource;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.CacheKey;
import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.logging.FLog;
import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;
import com.facebook.datasource.DataSubscriber;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import tw.chiae.inlive.presentation.ui.chatting.CircleImageView;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RelativeLayout;

import java.io.File;

/**
 * Created by ymlong on Nov 24, 2015.
 */
public final class FrescoUtil {

    public static final int PAGE_PADDING_IN_DP = 16;

    private FrescoUtil() {

    }

    public static void frescoResize(Uri uri, int width, int height, SimpleDraweeView sdv) {
        ImageRequest request = ImageRequestBuilder
                .newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(width, height))
                .build();

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setOldController(sdv.getController())
                .setImageRequest(request)
                .build();
        sdv.setController(controller);
    }

    public static DraweeController createResizeController(final Context context, final int
            parentWidth, final SimpleDraweeView imageView, String url) {
        ControllerListener controllerListener = new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(
                    String id,
                    @Nullable ImageInfo imageInfo,
                    @Nullable Animatable anim) {
                if (imageInfo == null) {
                    return;
                }
                Log.d("FrescoActivity",
                        "Final image received! " + imageInfo.getWidth() + imageInfo.getHeight());
                setSize(context, parentWidth, imageView, imageInfo.getWidth(),
                        imageInfo.getHeight());
            }

            @Override
            public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {
                FLog.d(getClass(), "Intermediate image received");

            }

            @Override
            public void onFailure(String id, Throwable throwable) {
                FLog.e(getClass(), throwable, "Error loading %s", id);
            }
        };
        return Fresco.newDraweeControllerBuilder()
                .setControllerListener(controllerListener)
                .setUri(Uri.parse(url))
                .build();
    }

    private static void setSize(Context context, int parentWidth, SimpleDraweeView imageView, int
            width, int height) {
        //窗口的宽度
        int size = parentWidth - PixelUtil.dp2px(context, PAGE_PADDING_IN_DP);
        height = (int) (height * (((double) size) / width));
        width = size;
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(width,
                height);
        param.addRule(RelativeLayout.CENTER_IN_PARENT);
        imageView.setLayoutParams(param);
    }

    public static void clearCache(Uri uri) {
        if (uri == null) {
            return;
        }
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.evictFromMemoryCache(uri);
        //imagePipeline.evictFromDiskCache(uri);
    }

    public static void CacheImgToDisk(String imgURL , CacheCallbacek callback , boolean Refresh) {
        Log.i("RayTest","CacheImgToDisk : "+imgURL);
        if(!imgURL.contains(Const.MAIN_HOST_URL))
            imgURL = Const.MAIN_HOST_URL+imgURL;
        ImageRequest downloadRequest = ImageRequest.fromUri(Uri.parse(imgURL));
        CacheKey cacheKey = DefaultCacheKeyFactory.getInstance().getEncodedCacheKey(downloadRequest,null);
        if (ImagePipelineFactory.getInstance().getMainFileCache().hasKey(cacheKey)) {
            Log.i("RayTest","不需下載");
            BinaryResource resource = ImagePipelineFactory.getInstance().getMainFileCache().getResource(cacheKey);
            File cacheFile = ((FileBinaryResource) resource).getFile();
            callback.cachePath(cacheFile.getPath());
        }else{
            if(Refresh) {
                //CreateImgToDisk(imgURL,callback);
                Log.i("RayTest","需下載");
                Fresco.getImagePipeline().prefetchToDiskCache(downloadRequest, null);
                callback.cachePath(imgURL);
            }
        }
    }

    private static void CreateImgToDisk(final String imgURL, final CacheCallbacek callback) {
        final DataSource<Boolean> inDiskCacheSource = Fresco.getImagePipeline().isInDiskCache(Uri.parse(imgURL));
        Log.i("RayTest","下載"+imgURL);
        final DataSubscriber<Boolean> subscriber = new BaseDataSubscriber<Boolean>() {
            @Override
            protected void onNewResultImpl(DataSource<Boolean> dataSource) {
                if (!dataSource.isFinished()) {
                    return;
                }
                boolean isInCache = dataSource.getResult();
                if(isInCache){
                    CacheImgToDisk(imgURL,callback,false);
                }else{
                    Log.i("RayTest","下載失敗");
                    callback.cachePath("下載失敗"+imgURL);

                }
                // your code here
            }

            @Override
            protected void onFailureImpl(DataSource<Boolean> dataSource) {
            }
        };
        inDiskCacheSource.subscribe(subscriber, CallerThreadExecutor.getInstance());
    }

    public static Uri getCache(String url) {
        ImageRequest downloadRequest = ImageRequest.fromUri(Uri.parse(url));
        CacheKey cacheKey = DefaultCacheKeyFactory.getInstance().getEncodedCacheKey(downloadRequest,null);

        if (ImagePipelineFactory.getInstance().getMainFileCache().hasKey(cacheKey)) {
            BinaryResource resource = ImagePipelineFactory.getInstance().getMainFileCache().getResource(cacheKey);
            File cacheFile = ((FileBinaryResource) resource).getFile();
            return Uri.fromFile(cacheFile);
        }else{
            return null;
        }
    }

    public static boolean isCache(Uri uri) {
        ImageRequest downloadRequest = ImageRequest.fromUri(uri);
        CacheKey cacheKey = DefaultCacheKeyFactory.getInstance().getEncodedCacheKey(downloadRequest,null);

        if (ImagePipelineFactory.getInstance().getMainFileCache().hasKey(cacheKey)) {
            BinaryResource resource = ImagePipelineFactory.getInstance().getMainFileCache().getResource(cacheKey);
            File cacheFile = ((FileBinaryResource) resource).getFile();
            return true;
        }else{
            return false;
        }
    }

    public static void removeDiskCache(Uri uri) {
        if (uri == null) {
            return;
        }
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.evictFromMemoryCache(uri);
        imagePipeline.evictFromDiskCache(uri);
    }

    public interface CacheCallbacek{
        void cachePath(String path);
    };
}
