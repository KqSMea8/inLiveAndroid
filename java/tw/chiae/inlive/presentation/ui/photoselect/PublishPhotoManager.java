package tw.chiae.inlive.presentation.ui.photoselect;

import android.content.Context;

import tw.chiae.inlive.data.repository.ISource;
import tw.chiae.inlive.data.repository.SourceFactory;

import java.io.File;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by huanzhang on Nov 18, 2015.
 */
public class PublishPhotoManager {
    private static final String FILE_PATH_CACHE_ZIP = "/upload/photo/";
    private static final String SUFFIX_ZIP = ".zip";

    private Context mContext;
    private AlbumHelper mHelper;
    private ISource mUploadPhoto = SourceFactory.create();

    public PublishPhotoManager(Context context) {
        mContext = context;
        mHelper = new AlbumHelper(context);
    }

    public Observable<List<ImageItem>> getPhotos() {
        return Observable.create(new Observable.OnSubscribe<List<ImageItem>>() {
            @Override
            public void call(Subscriber<? super List<ImageItem>> subscriber) {
                subscriber.onNext(mHelper.getImages());
                subscriber.onCompleted();
            }
        });
    }

  /*  public Observable<BaseResponse<CreditResult>> uploadPhotos(final RequestUploadPhoto
                                                                       requestParams) {
        Observable<File> observableZip = getZipFileObservable(requestParams.getImagesPath(),
                                                              new ImageUtils.Config());
        return observableZip.subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread())
                .flatMap(new Func1<File, Observable<BaseResponse<CreditResult>>>() {
                    @Override
                    public Observable<BaseResponse<CreditResult>> call(File file) {
                        requestParams.setZipPath(file.getAbsolutePath());
                        return mUploadPhoto.upload(requestParams);
                    }
                }).doOnNext(new Action1<BaseResponse>() {
                    @Override
                    public void call(BaseResponse baseResponse) {
                        final String dirLocation = getZipFileDir(requestParams);
                        EasyFile.recursionDeleteFile(new File(dirLocation));
                    }
                });
    }

    public Observable<File> getZipFileObservable(final ArrayList<String> selectedImages, final
    ImageUtils.Config config) {
        return Observable.create(new Observable.OnSubscribe<File>() {
            @Override
            public void call(Subscriber<? super File> subscriber) {
                final ArrayList<File> srcFiles = new ArrayList<>();
                for (String selectedImage : selectedImages) {
                    File srcFile = new File(selectedImage);
                    if (!srcFile.exists()) {
                        continue;
                    }
                    srcFiles.add(srcFile);
                }

                try {
                    ArrayList<File> compressedFiles = new ArrayList<>();
                    if (!srcFiles.isEmpty()) {
                        for (File file : srcFiles) {
                            final String dir = getZipFileDir(selectedImages);
                            final File compressedFile = EasyFile.createFile(dir, file.getName());
                            ImageUtils.compressImageFile(file.getAbsolutePath(), compressedFile,
                                                         config);
                            compressedFiles.add(compressedFile);
                        }
                    }

                    final File zipFile = getZipFile(selectedImages);
                    if (zipFile != null && !compressedFiles.isEmpty()) {
                        ZipUtil.zipFiles(compressedFiles, zipFile);
                    }
                    subscriber.onNext(zipFile);
                    subscriber.onCompleted();
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        });
    }


    private String getZipFileName(final Object selectedImages) {
        return new MD5().getMD5ofStr(new Gson().toJson(selectedImages)) + SUFFIX_ZIP;
    }


    public String getZipFileDir(final Object selectedImages) {
        return StorageUtils.getDiskCacheDir(mContext) + FILE_PATH_CACHE_ZIP + getZipFileName(
                selectedImages) + File.separator;
    }

    private File getZipFile(final Object selectedImages) {
        final String dir = getZipFileDir(selectedImages);
        return EasyFile.createFile(dir, getZipFileName(selectedImages));
    }*/

}
