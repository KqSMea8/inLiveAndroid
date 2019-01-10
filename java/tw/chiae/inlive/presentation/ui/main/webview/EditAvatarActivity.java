package tw.chiae.inlive.presentation.ui.main.webview;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import tw.chiae.inlive.BeautyLiveApplication;
import tw.chiae.inlive.R;
import tw.chiae.inlive.data.bean.BaseResponse;
import tw.chiae.inlive.data.repository.SourceFactory;
import tw.chiae.inlive.domain.ProfileManager;
import tw.chiae.inlive.presentation.ui.base.BaseActivity;
import tw.chiae.inlive.presentation.ui.base.BaseObserver;
import tw.chiae.inlive.presentation.ui.photoselect.PickPhotoUtil;
import tw.chiae.inlive.presentation.ui.widget.CustomToast;
import tw.chiae.inlive.util.FrescoUtil;
import tw.chiae.inlive.util.L;
import com.yalantis.ucrop.UCrop;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * @author Muyangmin
 * @since 1.0.0
 * 修改头像
 */
public class EditAvatarActivity extends BaseActivity {
    private static final int RESULT_REQUST_CODE_SELECT= 110;
    private static final int RESULT_REQUST_CODE_CAMERA= 120;
    private static final int RESULT_REQUEST_CODE_GALLERY = 114;

    private static final String EXTRA_AVATAR = "av";
    private static final String EXTRA_AVATAR_URI = "av_uri" ;

    private String mAvatarPath;
    private Uri mAvatarUri = null;

    private SimpleDraweeView draweeAvatar;
    private ImageButton imgbtnBack;
    private Button btnTakePhoto, btnSelectFromAlbum;
    private PickPhotoUtil mPickUtil;


    public static Intent createIntent(Context context, String avatarPath){
        Intent intent = new Intent(context, EditAvatarActivity.class);
        intent.putExtra(EXTRA_AVATAR, avatarPath);
        return intent;
    }

    public static Intent createIntent(Context context, Uri avatarUri){
        Intent intent = new Intent(context, EditAvatarActivity.class);
        intent.putExtra(EXTRA_AVATAR_URI, avatarUri);
        return intent;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_avatar;
    }

    @Override
    protected void parseIntentData(Intent intent, boolean isFromNewIntent) {
        super.parseIntentData(intent, isFromNewIntent);
        mAvatarPath = intent.getStringExtra(EXTRA_AVATAR);
        mAvatarUri = intent.getParcelableExtra(EXTRA_AVATAR_URI);
    }

    @Override
    protected void findViews(Bundle savedInstanceState) {
        draweeAvatar = $(R.id.edit_avatar_drawee_avatar);
        imgbtnBack = $(R.id.edit_avatar_imgbtn_back);
        btnSelectFromAlbum = $(R.id.edit_avatar_btn_select);
        btnTakePhoto = $(R.id.edit_avatar_btn_take);
    }

    @Override
    protected void init() {
        mPickUtil = new PickPhotoUtil(this);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) draweeAvatar.getLayoutParams();
        params.width = metrics.widthPixels;
        //noinspection SuspiciousNameCombination
        params.height = metrics.widthPixels;
        draweeAvatar.setLayoutParams(params);

        //Here force show 1:1 height
        //noinspection SuspiciousNameCombination
        if(!TextUtils.isEmpty(mAvatarPath)) {
            FrescoUtil.frescoResize(SourceFactory.wrapPathToUri(mAvatarPath),
                    metrics.widthPixels, metrics.widthPixels,
                    draweeAvatar);
        }

        if(mAvatarUri != null) {
            FrescoUtil.frescoResize(mAvatarUri,
                    metrics.widthPixels, metrics.widthPixels,
                    draweeAvatar);
        }
        subscribeClick(imgbtnBack, new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                onBackPressed();
            }
        });

        subscribeClick(btnSelectFromAlbum, new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                getPictureFromGallery();
//                startActivityForResult(PhotoSelectActivity.createIntent(EditAvatarActivity.this),RESULT_REQUST_CODE_SELECT);
            }
        });
        subscribeClick(btnTakePhoto, new Action1<Void>() {
            @Override
            public void call(Void aVoid) {

                try {
                    String path = Environment.getExternalStorageDirectory()
                            + "/beautyLive";
                    File f = new File(path);
                    if (!f.exists()) {
                        f.mkdirs();
                    }
                    File file=new File(f,"beautylive.jpg");
                  /*  if(!file.exists()){
                        file.createNewFile();}
*/
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    Uri u=Uri.fromFile(file);
                    intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, u);
                    startActivityForResult(intent,RESULT_REQUST_CODE_CAMERA);
                } catch (Exception e) {
                      toastShort(getString(R.string.open_camera_error));
//                    Toast.makeText(ImpromptuActivity.this, "没有找到储存目录",Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == RESULT_REQUST_CODE_SELECT ||requestCode == RESULT_REQUEST_CODE_GALLERY) {
                final Uri selectedUri = data.getData();
                if (selectedUri != null) {
                    mPickUtil.startCropActivity(data.getData());
                } else {
                    toastShort(R.string.toast_cannot_retrieve_selected_image);
                }
            } else if (requestCode == UCrop.REQUEST_CROP) {
               handleCropResult(data);
            }else if(requestCode == RESULT_REQUST_CODE_CAMERA){
                String path = Environment.getExternalStorageDirectory()
                        + "/beautyLive";
                File f = new File(path);
                if (!f.exists()) {
                    f.mkdirs();
                }
                File file=new File(f,"beautylive.jpg");
                if(file.exists()) {
                    try {
                        Uri u = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(),
                                file.getAbsolutePath(), null, null));
                        mPickUtil.startCropActivity(u);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }else{
                    toastShort(R.string.toast_cannot_retrieve_selected_image);
                }
            }
        }

        if (resultCode == UCrop.RESULT_ERROR) {
            mPickUtil.handleCropError(data);
        }
    }
    public void handleCropResult(@NonNull Intent result) {
        final Uri resultUri = UCrop.getOutput(result);
        if (resultUri != null) {
            updateAvatar(resultUri.getPath());
            final String path=resultUri.getPath();
            if (!path.equals("")&&path!=null){
                try{
                    cn.jpush.im.android.api.model.UserInfo myInfo = JMessageClient.getMyInfo();
                    BeautyLiveApplication.setPicturePath(myInfo.getAppKey());
                    JMessageClient.updateUserAvatar(new File(path), new BasicCallback() {
                        @Override
                        public void gotResult(int status, final String desc) {
                            if (status == 0) {
                            }
                        }
                    });
                }catch (NullPointerException e){
                }
            }

        } else {
            CustomToast.makeCustomText(this, R.string.toast_cannot_retrieve_cropped_image, Toast.LENGTH_SHORT).show();
        }
    }

    private String getFilePath(Bitmap bitmap){

        try {
            File file = new File(this.getCacheDir(), "beautyLive.jpg");
            L.i(LOG_TAG, "Writing to %s", file.getAbsolutePath());
            if (!file.exists()) {
                if(!file.createNewFile()){
                    //创建文件失败返回null
                    return null;
                }
            }
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream
                    (file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            bos.flush();
            bos.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    private void getPictureFromGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.photo_select)), RESULT_REQUEST_CODE_GALLERY);
    }

    private void updateAvatar(String path){
        showProgressDialog();
              Observable.just(path)
                .doOnNext(new Action1<String>() {
                    @Override
                    public void call(String s) {
//                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bkg_login_select);

//                        try{
//                            File file = new File(s);
//                            L.i(LOG_TAG, "Writing to %s", file.getAbsolutePath());
//                            if (!file.exists()){
//                                file.createNewFile();
//                            }
//                            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream
//                                    (file));
//                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
//                            bos.flush();
//                            bos.close();

                            L.i(LOG_TAG, "Uploading file...");
                            new ProfileManager().uploadAvatar(s)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new BaseObserver<BaseResponse<String>>(EditAvatarActivity
                                    .this) {
//                                @Override
//                                public void onCompleted() {
//                                    L.i(LOG_TAG, "onCompleted");
//                                }
//
//                                @Override
//                                public void onError(Throwable e) {
//                                    L.e(LOG_TAG, "upload error!", e);
//                                }
//
//                                @Override
//                                public void onNext(BaseResponse<String> stringBaseResponse) {
//                                    Log.i(LOG_TAG, "Upload success!");
//                                }

                                @Override
                                public void onSuccess(BaseResponse<String> response) {
                                    Log.i(LOG_TAG, "Upload success!");
                                }
                            })
                            ;
//
//                        }catch (IOException e){
//                            e.printStackTrace();
//                        }

                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(final String s) {
                        if(mProgressDialog != null && mProgressDialog.isShowing()){
                            mProgressDialog.dismiss();
                        }
                        L.i(LOG_TAG, "on post result:%s", s);
                        final File file = new File(s);
                        final Uri uri = Uri.parse("file://"+file.getAbsolutePath());
                        FrescoUtil.clearCache(uri);
                        Intent intent = new Intent();
                        intent.setData(uri);
                        setResult(RESULT_OK, intent);
                        finish();
                   /*     L.i(LOG_TAG, "on hack Pic selected:%s", s);

                        L.i(LOG_TAG, "Uri=%s", uri);
                        draweeAvatar.setImageURI(uri);

                        draweeAvatar.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                            }
                        }, 2000);*/

                    }
                });
    }

    private ProgressDialog mProgressDialog;

    private void showProgressDialog(){
        if(mProgressDialog== null) {
            //创建ProgressDialog对象
            mProgressDialog = new ProgressDialog(this);
        }
        //设置进度条风格，风格为圆形，旋转的
        mProgressDialog.setProgressStyle(
                ProgressDialog.STYLE_SPINNER);
        //设置ProgressDialog 提示信息
        mProgressDialog.setMessage(getString(R.string.avatar_upload_loading));
        //设置ProgressDialog 标题图标
        mProgressDialog.setIcon(android.R.drawable.btn_star);
        //设置ProgressDialog 的进度条是否不明确
        mProgressDialog.setIndeterminate(false);
        //设置ProgressDialog 是否可以按退回按键取消
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
    }

    @Override
    public void setCoinData(int currencyItemSum) {

    }
}
