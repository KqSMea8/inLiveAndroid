package tw.chiae.inlive.presentation.ui.photoselect;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import tw.chiae.inlive.R;
import tw.chiae.inlive.presentation.ui.base.BaseActivity;
import tw.chiae.inlive.presentation.ui.main.me.profile.EditProfileActivity;
import tw.chiae.inlive.util.L;
import com.yalantis.ucrop.UCrop;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ymlong on Nov 17, 2015.
 */
public class PhotoSelectActivity extends BaseActivity implements IPhotoSelect {
    public static final String INTENT_KEY_PHOTO_PATH = "PhotoPath";
    public static final String INTENT_KEY_MAXPHOTO_LIMIT = "PhotoLimit";
    public static final String INTENT_KEY_DES = "SelectDescription";
    public static final int RESULT_CODE_SELECTED = 1986;
    private static final int GRIDVIEW_SPAN_NUM = 4;
    private final ArrayList<String> mSelectedImages = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private PhotoSelectAdapter mPhotoSelectAdapter;
    private PhotoSelectPresenter mPublishPhotoPresenter;
    private int mSelectedPhotoLimit;
    private String mDes;


    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, PhotoSelectActivity.class);
        return intent;
    }
    @Override
    protected int getLayoutId() {
        return R.layout.activity_photo;
    }

    @Override
    protected void findViews(Bundle savedInstanceState) {
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_publish_photo);

    }

    @Override
    protected void init() {
        mSelectedPhotoLimit = getIntent().getIntExtra(INTENT_KEY_MAXPHOTO_LIMIT, 1);
        mDes = getIntent().getStringExtra(INTENT_KEY_DES);

        mPublishPhotoPresenter = new PhotoSelectPresenter(this, this);

        initViews();
    }

    protected void initViews() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, GRIDVIEW_SPAN_NUM));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mPhotoSelectAdapter = new PhotoSelectAdapter(this, this, mSelectedPhotoLimit);
        mRecyclerView.setAdapter(mPhotoSelectAdapter);
        mPublishPhotoPresenter.showPhotos();
    }


    @Override
    public void onSelected(ImageItem selectedPhotos) {
        L.e("huan","image path:" + selectedPhotos.getImagePath());

        Intent intent = new Intent();
        intent.setData(Uri.parse("file://"+selectedPhotos.getImagePath()));
        setResult(RESULT_OK, intent);
        finish();
//        mPickUtil.startCropActivity(Uri.parse("file://"+selectedPhotos.getImagePath()));
    }

    @Override
    public void showPhotos(List<ImageItem> photoList) {
        mPhotoSelectAdapter.addPhotos(photoList);
    }

    @Override
    public void setCoinData(int currencyItemSum) {

    }
}