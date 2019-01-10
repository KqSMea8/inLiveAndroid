package tw.chiae.inlive.presentation.ui.chatting;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.DownloadCompletionCallback;
import cn.jpush.im.android.api.callback.ProgressUpdateCallback;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import io.jchat.android.tools.BitmapLoader;
import io.jchat.android.tools.NativeImageLoader;
import tw.chiae.inlive.BeautyLiveApplication;
import tw.chiae.inlive.R;
import tw.chiae.inlive.presentation.ui.chatting.photoview.PhotoView;
import tw.chiae.inlive.presentation.ui.chatting.utils.HandleResponseCode;

/**
 * Created by rayyeh on 2017/7/27.
 */

public class BrowserViewPagerActivity extends BaseActivity{
    private static final String MSGID ="msgid" ;
    private static final String MSGPATH = "path";
    private static String TAG = BrowserViewPagerActivity.class.getSimpleName();
    private PhotoView photoView;
    private ImgBrowserViewPager mViewPager;
    private ProgressDialog mProgressDialog;
    //存放所有图片的路径
    private List<HashMap<String,Object>> mPathList = new ArrayList<>();
    //存放图片消息的ID
    private List<Integer> mMsgIdList = new ArrayList<Integer>();

    private int mPosition;
    private Conversation mConv;
    private Message mMsg;
    private String mTargetId;
    private boolean mFromChatActivity = true;
    //当前消息数
    private int mStart;
    private int mOffset = 18;
    private Context mContext;
    private boolean mDownloading = false;
    private Long mGroupId;
    private int[] mMsgIds;
    private int mIndex = 0;
    private final MyHandler myHandler = new MyHandler(this);
    private final static int DOWNLOAD_ORIGIN_IMAGE_SUCCEED = 1;
    private final static int DOWNLOAD_PROGRESS = 2;
    private final static int DOWNLOAD_COMPLETED = 3;
    private final static int SEND_PICTURE = 5;
    private final static int DOWNLOAD_ORIGIN_PROGRESS = 6;
    private final static int DOWNLOAD_ORIGIN_COMPLETED = 7;

    /**
     * 用来存储图片的选中情况
     */
    private SparseBooleanArray mSelectMap = new SparseBooleanArray();
    private PagerAdapter pagerAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageButton returnBtn;


        mContext = this;
        setContentView(R.layout.activity_image_browser);
        mViewPager = (ImgBrowserViewPager) findViewById(R.id.img_browser_viewpager);
        //returnBtn = (ImageButton) findViewById(R.id.return_btn);


        final Intent intent = this.getIntent();
        mGroupId = intent.getLongExtra(ChatActivity.GROUP_ID, 0);
        if (mGroupId != 0) {
            mConv = JMessageClient.getGroupConversation(mGroupId);
        } else {
            mTargetId = intent.getStringExtra(ChatActivity.TARGET_ID);
            if (mTargetId != null) {
                mConv = JMessageClient.getSingleConversation(mTargetId);
            }
        }
        mStart = intent.getIntExtra("msgCount", 0);
        mPosition = intent.getIntExtra(BeautyLiveApplication.POSITION, 0);
        mFromChatActivity = intent.getBooleanExtra("fromChatActivity", true);
        boolean browserAvatar = intent.getBooleanExtra("browserAvatar", false);

        pagerAdapter = new PagerAdapter() {

            @Override
            public int getCount() {
                return mPathList.size();
            }

            /**
             * 点击某张图片预览时，系统自动调用此方法加载这张图片左右视图（如果有的话）
             */
            @Override
            public View instantiateItem(ViewGroup container, int position) {
                photoView = new PhotoView(mFromChatActivity, container.getContext());

                photoView.setTag(position);
                String path = (String) mPathList.get(position).get(MSGPATH);
                if (path != null) {
                    File file = new File(path);
                    if (file.exists()) {
                        Bitmap bitmap = BitmapLoader.getBitmapFromFile(path, mWidth, mHeight);
                        if (bitmap != null) {
                            photoView.setImageBitmap(bitmap);
                            setScaleMode(photoView,bitmap);
                        } else {
                            photoView.setImageResource(R.drawable.snap_default);
                        }
                    } else {
                        Bitmap bitmap = NativeImageLoader.getInstance().getBitmapFromMemCache(path);
                        if (bitmap != null) {
                            photoView.setImageBitmap(bitmap);
                            setScaleMode(photoView,bitmap);

                        } else {
                            photoView.setImageResource(R.drawable.snap_default);
                        }
                    }
                } else {
                    photoView.setImageResource(R.drawable.snap_default);
                    int id = (int) mPathList.get(position).get(MSGID);
                    Log.i("RayTest","null img :"+id);
                    downloadImage(id,photoView);

                }
                container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                return photoView;
            }

            @Override
            public int getItemPosition(Object object) {
                View view = (View) object;
                int currentPage = mViewPager.getCurrentItem();
                if (currentPage == (Integer) view.getTag()) {
                    return POSITION_NONE;
                } else {
                    return POSITION_UNCHANGED;
                }
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

        };
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setOnPageChangeListener(onPageChangeListener);

        // 在聊天界面中点击图片
        if (mFromChatActivity) {

            if(mViewPager != null && mViewPager.getAdapter() != null) {
                mViewPager.getAdapter().notifyDataSetChanged();
            }
            //预览头像
            if (browserAvatar) {
                Log.i("RayTest","11111");
                String path = intent.getStringExtra("avatarPath");
                photoView = new PhotoView(mFromChatActivity, mContext);
                try {
                    File file = new File(path);
                    HashMap<String, Object> msginf = new HashMap<String, Object>();
                    msginf.put(MSGPATH,path);
                    mPathList.add(msginf);
                    if (file.exists()) {
                        Picasso.with(mContext).load(file).into(photoView);
                    } else {
                        photoView.setImageBitmap(NativeImageLoader.getInstance().getBitmapFromMemCache(path));
                    }
                } catch (Exception e) {
                    photoView.setImageResource(R.drawable.snap_default);
                    HandleResponseCode.onHandle(mContext, 1001, false);
                }
                //预览聊天界面中的图片
            } else {
                Log.i("RayTest","222222");
                initImgPathList();

                if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                     Toast.makeText(this, this.getString(R.string.local_picture_not_found_toast), Toast.LENGTH_SHORT).show();
                }
                int currentItem = mPosition;
                Log.i("RayTest","mPosition:"+mPosition);
                mViewPager.setCurrentItem(currentItem);
               /* mMsg = mConv.getMessage(intent.getIntExtra("msgId", 0));
                photoView = new PhotoView(mFromChatActivity, this);
                int currentItem = mMsgIdList.indexOf(mMsg.getId());
                ImageContent ic = (ImageContent) mMsg.getContent();
                Log.i("RayTest","getLocalPath:"+ic.getLocalPath());*/
                /*try {
                    ImageContent ic = (ImageContent) mMsg.getContent();
                    //如果点击的是第一张图片并且图片未下载过，则显示大图
                    if (ic.getLocalPath() == null && mMsgIdList.indexOf(mMsg.getId()) == 0) {
                        downloadImage();
                    }
                    String path = mPathList.get(mMsgIdList.indexOf(mMsg.getId()));
                    //如果发送方上传了原图
                    if (ic.getBooleanExtra("originalPicture") != null && ic.getBooleanExtra("originalPicture")) {
                        //mLoadBtn.setVisibility(View.GONE);
                        setLoadBtnText(ic);
                        photoView.setImageBitmap(BitmapLoader.getBitmapFromFile(path, mWidth, mHeight));
                    } else {
                        Picasso.with(mContext).load(new File(path)).into(photoView);
                    }

                    mViewPager.setCurrentItem(currentItem);
                } catch (NullPointerException e) {
                    photoView.setImageResource(R.drawable.snap_default);
                    mViewPager.setCurrentItem(currentItem);
                } finally {
                    if (currentItem == 0) {
                        getImgMsg();
                    }
                }*/
            }
            // 在选择图片时点击预览图片
        }
    }

    private void setScaleMode(PhotoView photoView, Bitmap bitmap) {
        if(bitmap.getHeight()>bitmap.getWidth()){
            //photoView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }

    private void setLoadBtnText(ImageContent ic) {
     /*   NumberFormat ddf1 = NumberFormat.getNumberInstance();
        //保留小数点后两位
        ddf1.setMaximumFractionDigits(2);
        double size = ic.getFileSize() / 1048576.0;
        String loadText = mContext.getString(R.string.load_origin_image) + "(" + ddf1.format(size) + "M" + ")";
        mLoadBtn.setText(loadText);*/
    }

    /**
     * 在图片预览中发送图片，点击选择CheckBox时，触发事件
     *
     * @param currentItem 当前图片索引
     */
    private void checkPictureSelected(final int currentItem) {
      /*  mPictureSelectedCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (mSelectMap.size() + 1 <= 9) {
                    if (isChecked) {
                        mSelectMap.put(currentItem, true);
                    } else {
                        mSelectMap.delete(currentItem);
                    }
                } else if (isChecked) {
                    Toast.makeText(mContext, mContext.getString(R.string.picture_num_limit_toast), Toast.LENGTH_SHORT).show();
                    mPictureSelectedCb.setChecked(mSelectMap.get(currentItem));
                } else {
                    mSelectMap.delete(currentItem);
                }

                showSelectedNum();
                showTotalSize();
            }
        });
*/
    }

    /**
     * 点击发送原图CheckBox，触发事件
     *
     */
    private void checkOriginPictureSelected() {
      /*  mOriginPictureCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    if (mSelectMap.size() < 1) {
                        mPictureSelectedCb.setChecked(true);
                    }
                }
            }
        });*/
    }

    //显示选中的图片总的大小
    private void showTotalSize() {
      /*  if (mSelectMap.size() > 0) {
            List<String> pathList = new ArrayList<String>();
            for (int i=0; i < mSelectMap.size(); i++) {
                pathList.add(mPathList.get(mSelectMap.keyAt(i)));
            }
            String totalSize = BitmapLoader.getPictureSize(pathList);
            String totalText = mContext.getString(R.string.origin_picture)
                    + String.format(mContext.getString(R.string.combine_title), totalSize);
            mTotalSizeTv.setText(totalText);
        } else {
            mTotalSizeTv.setText(mContext.getString(R.string.origin_picture));
        }*/
    }

    //显示选中了多少张图片
    private void showSelectedNum() {
       /* if (mSelectMap.size() > 0) {
            String sendText = mContext.getString(R.string.send) + "(" + mSelectMap.size() + "/" + "9)";
            mSendBtn.setText(sendText);
        } else {
            mSendBtn.setText(mContext.getString(R.string.send));
        }*/
    }

    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        //在滑动的时候更新CheckBox的状态
        @Override
        public void onPageScrolled(final int i, float v, int i2) {
         /*   checkPictureSelected(i);
            checkOriginPictureSelected();
            mPictureSelectedCb.setChecked(mSelectMap.get(i));*/
        }

        @Override
        public void onPageSelected(final int i) {
          /*  Log.d(TAG, "onPageSelected current position: " + i);
            if (mFromChatActivity) {
                mMsg = mConv.getMessage(mMsgIdList.get(i));
                Log.d(TAG, "onPageSelected Image Message ID: " + mMsg.getId());
                ImageContent ic = (ImageContent) mMsg.getContent();
                //每次选择或滑动图片，如果不存在本地图片则下载，显示大图
                if (ic.getLocalPath() == null && i != mPosition) {
//                    mLoadBtn.setVisibility(View.VISIBLE);
                    downloadImage();
                } else if (ic.getBooleanExtra("hasDownloaded") != null && !ic.getBooleanExtra("hasDownloaded")) {
                    setLoadBtnText(ic);
                    mLoadBtn.setVisibility(View.GONE);
                } else {
                    mLoadBtn.setVisibility(View.GONE);
                }
                if (i == 0) {
                    getImgMsg();
                }
            } else {
                String numText = i + 1 + "/" + mPathList.size();
                mNumberTv.setText(numText);
            }*/
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };

    /**
     * 滑动到第一张时，加载上一页消息中的图片
     */
    private void getImgMsg() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ImageContent ic;
                final int msgSize = mMsgIdList.size();
                List<Message> msgList = mConv.getMessagesFromNewest(mStart, mOffset);
                mOffset = msgList.size();
                if (mOffset > 0) {
                    for (Message msg : msgList) {
                        if (msg.getContentType().equals(ContentType.image)) {
                            mMsgIdList.add(0, msg.getId());
                            ic = (ImageContent) msg.getContent();
                            if (!TextUtils.isEmpty(ic.getLocalPath())) {

                                HashMap<String, Object> msgInfo = new HashMap<String, Object>();
                                msgInfo.put(MSGPATH,ic.getLocalPath());
                                mPathList.add(0, msgInfo);
                            } else {
                                HashMap<String, Object> msgInfo = new HashMap<String, Object>();
                                msgInfo.put(MSGPATH,ic.getLocalThumbnailPath());
                                mPathList.add(0, msgInfo);
                            }
                        }
                    }
                    mStart += mOffset;
                    if (msgSize == mMsgIdList.size()) {
                        getImgMsg();
                    } else {
                        //加载完上一页图片后，设置当前图片仍为加载前的那一张图片
                        BrowserViewPagerActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mPosition = mMsgIdList.size() - msgSize;
                                mViewPager.setCurrentItem(mPosition);
                                mViewPager.getAdapter().notifyDataSetChanged();
                            }
                        });
                    }
                }
            }
        });
        thread.start();
    }

    /**
     * 初始化会话中的所有图片路径
     */
    private void initImgPathList() {
        mMsgIdList = this.getIntent().getIntegerArrayListExtra(BeautyLiveApplication.MsgIDs);
        Message msg;
        //ImageContent ic;
        for (int msgID : mMsgIdList) {
            msg = mConv.getMessage(msgID);
            ImageContent imgContent = (ImageContent) msg.getContent();
            String path = imgContent.getLocalPath();
            Log.i("RayTest","path:"+path+" : "+msgID);
            HashMap<String, Object> msgInfo = new HashMap<String, Object>();
            msgInfo.put(MSGPATH,path);
            msgInfo.put(MSGID,msgID);
            mPathList.add(msgInfo);

//            if (msg.getContentType().equals(ContentType.image)) {
//                ic = (ImageContent) msg.getContent();
//                if (!TextUtils.isEmpty(ic.getLocalPath())) {
//                    mPathList.add(ic.getLocalPath());
//                } else {
//                    mPathList.add(ic.getLocalThumbnailPath());
//                }
//            }
        }
        pagerAdapter.notifyDataSetChanged();
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            /*switch (v.getId()) {
                case R.id.return_btn:
                    int pathArray[] = new int[mPathList.size()];
                    for (int i = 0; i < pathArray.length; i++) {
                        pathArray[i] = 0;
                    }
                    for (int j = 0; j < mSelectMap.size(); j++) {
                        pathArray[mSelectMap.keyAt(j)] = 1;
                    }
                    Intent intent = new Intent();
                    intent.putExtra("pathArray", pathArray);
                    setResult(BeautyLiveApplication.RESULT_CODE_SELECT_PICTURE, intent);
                    finish();
                    break;
                case R.id.pick_picture_send_btn:
                    mProgressDialog = new ProgressDialog(mContext);
                    mProgressDialog.setMessage(mContext.getString(R.string.sending_hint));
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.show();
                    mPosition = mViewPager.getCurrentItem();

                    if (mOriginPictureCb.isChecked()) {
                        Log.i(TAG, "發送原圖");
                        getOriginPictures(mPosition);
                    } else {
                        Log.i(TAG, "發送縮圖");
                        getThumbnailPictures(mPosition);
                    }
                    break;
                //点击显示原图按钮，下载原图
                case R.id.load_image_btn:
                    downloadOriginalPicture();
                    break;
            }*/
        }
    };

    private void downloadOriginalPicture() {
       /* final ImageContent imgContent = (ImageContent) mMsg.getContent();
        //如果不存在下载进度
        if (!mMsg.isContentDownloadProgressCallbackExists()) {
            mMsg.setOnContentDownloadProgressCallback(new ProgressUpdateCallback() {
                @Override
                public void onProgressUpdate(double progress) {
                    android.os.Message msg = myHandler.obtainMessage();
                    Bundle bundle = new Bundle();
                    if (progress < 1.0) {
                        msg.what = DOWNLOAD_ORIGIN_PROGRESS;
                        bundle.putInt("progress", (int) (progress * 100));
                        msg.setData(bundle);
                        msg.sendToTarget();
                    } else {
                        msg.what = DOWNLOAD_ORIGIN_COMPLETED;
                        msg.sendToTarget();
                    }
                }
            });
            imgContent.downloadOriginImage(mMsg, new DownloadCompletionCallback() {
                @Override
                public void onComplete(int status, String desc, File file) {
                    if (status == 0) {
                        imgContent.setBooleanExtra("hasDownloaded", true);
                    } else {
                        imgContent.setBooleanExtra("hasDownloaded", false);
                        if (mProgressDialog != null) {
                            mProgressDialog.dismiss();
                        }
                        HandleResponseCode.onHandle(mContext, status, false);
                    }
                }
            });
        }*/
    }


    /**
     * 获得选中图片的原图路径
     *
     * @param position 选中的图片位置
     */
    private void getOriginPictures(int position) {
        if (mSelectMap.size() < 1) {
            mSelectMap.put(position, true);
        }
        mMsgIds = new int[mSelectMap.size()];
        //根据选择的图片路径生成队列
        for (int i = 0; i < mSelectMap.size(); i++) {
            createImageContent((String) mPathList.get(mSelectMap.keyAt(i)).get(MSGPATH), true);
        }
    }

    /**
     * 获得选中图片的缩略图路径
     *
     * @param position 选中的图片位置
     */
    private void getThumbnailPictures(int position) {
        if (mSelectMap.size() < 1) {
            mSelectMap.put(position, true);
        }
        mMsgIds = new int[mSelectMap.size()];
        for (int i = 0; i < mSelectMap.size(); i++) {
            createImageContent((String) mPathList.get(mSelectMap.keyAt(i)).get(MSGPATH), false);
        }
    }

    /**
     * 根据图片路径生成ImageContent
     * @param path 图片路径
     * @param isOriginal 是否发送原图
     */
    private void createImageContent(String path, final boolean isOriginal) {
        Bitmap bitmap;
        if (isOriginal || BitmapLoader.verifyPictureSize(path)) {
            File file = new File(path);
            ImageContent.createImageContentAsync(file, new ImageContent.CreateImageContentCallback() {
                @Override
                public void gotResult(int status, String desc, ImageContent imageContent) {
                    if (status == 0) {
                        if (isOriginal) {
                            imageContent.setBooleanExtra("originalPicture" , true);
                        }
                        Message msg = mConv.createSendMessage(imageContent);
                        mMsgIds[mIndex] = msg.getId();
                        mIndex++;
                        if (mIndex >= mSelectMap.size()) {
                            myHandler.sendEmptyMessage(SEND_PICTURE);
                        }
                    } else {
                        if (mProgressDialog != null) {
                            mProgressDialog.dismiss();
                        }
                        HandleResponseCode.onHandle(mContext, status, false);
                    }
                }
            });
        } else {
            bitmap = BitmapLoader.getBitmapFromFile(path, 720, 1280);
            ImageContent.createImageContentAsync(bitmap, new ImageContent.CreateImageContentCallback() {
                @Override
                public void gotResult(int status, String desc, ImageContent imageContent) {
                    if (status == 0) {
                        Message msg = mConv.createSendMessage(imageContent);
                        mMsgIds[mIndex] = msg.getId();
                        mIndex++;
                        if (mIndex >= mSelectMap.size()) {
                            myHandler.sendEmptyMessage(SEND_PICTURE);
                        }
                    } else {
                        if (mProgressDialog != null) {
                            mProgressDialog.dismiss();
                        }
                        HandleResponseCode.onHandle(mContext, status, false);
                    }
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        if(mViewPager != null && mViewPager.getAdapter() != null) {
            mViewPager.getAdapter().notifyDataSetChanged();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mDownloading) {
            mProgressDialog.dismiss();
            //TODO cancel download image
        }
        int pathArray[] = new int[mPathList.size()];
        for (int i = 0; i < pathArray.length; i++) {
            pathArray[i] = 0;
        }
        for (int i = 0; i < mSelectMap.size(); i++) {
            pathArray[mSelectMap.keyAt(i)] = 1;
        }
        Intent intent = new Intent();
        intent.putExtra("pathArray", pathArray);
        setResult(BeautyLiveApplication.RESULT_CODE_SELECT_PICTURE, intent);
        super.onBackPressed();
    }

    //每次在聊天界面点击图片或者滑动图片自动下载大图
    private void downloadImage(final int msgID, final PhotoView photoView) {
        Log.d(TAG, "Downloading image!");
        Log.d("RayTest", "Downloading image!");
        Message msg = mConv.getMessage(msgID);
        ImageContent imgContent = (ImageContent) msg.getContent();
        imgContent.downloadOriginImage(msg, new DownloadCompletionCallback() {
            @Override
            public void onComplete(int status, String desc, File file) {
                Log.d("RayTest", "Downloading onComplete!");
                Log.d("RayTest", "status "+status+" path:"+file.getPath());
                if (status == 0 && !file.getPath().equals("")) {
                    if (file.exists()) {
                        Log.d("RayTest", "Downloading exists1!"+file.getPath());
                        Bitmap bitmap = BitmapLoader.getBitmapFromFile(file.getPath(), mWidth, mHeight);
                        if (bitmap != null) {
                            photoView.setImageBitmap(bitmap);
                            setScaleMode(photoView,bitmap);
                        } else {
                            photoView.setImageResource(R.drawable.snap_default);
                        }

                    } else {
                        Log.d("RayTest", "Downloading exists2!"+file.getPath());
                        Bitmap bitmap = NativeImageLoader.getInstance().getBitmapFromMemCache(file.getPath());
                        if (bitmap != null) {
                            photoView.setImageBitmap(bitmap);
                            photoView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        } else {
                            photoView.setImageResource(R.drawable.snap_default);

                        }
                    }
                    UpdatePathList(msgID,file.getPath());
                }
            }
        });
    }

    private void UpdatePathList(int msgID, String path) {
        Log.i("RayTest","update:"+msgID+""+ path);
        for(HashMap<String, Object> mPathInfo :mPathList){
            int mId = (int) mPathInfo.get(MSGID);
            if(mId==msgID){
                mPathInfo.put(MSGPATH,path);
                pagerAdapter.notifyDataSetChanged();
            }
        }
    }

    private static class MyHandler extends Handler {
        private final WeakReference<BrowserViewPagerActivity> mActivity;

        public MyHandler(BrowserViewPagerActivity activity){
            mActivity = new WeakReference<BrowserViewPagerActivity>(activity);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            BrowserViewPagerActivity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case DOWNLOAD_ORIGIN_IMAGE_SUCCEED:
                        //更新图片并显示
                        Bundle bundle = msg.getData();
                        HashMap<String, Object> msginfo = new HashMap<String, Object>();
                        msginfo.put(MSGPATH,bundle.getString("path"));
                        activity.mPathList.set(bundle.getInt(BeautyLiveApplication.POSITION), msginfo);
                        activity.mViewPager.getAdapter().notifyDataSetChanged();
                        //activity.mLoadBtn.setVisibility(View.GONE);
                        break;
                    case DOWNLOAD_PROGRESS:
                        activity.mProgressDialog.setProgress(msg.getData().getInt("progress"));
                        break;
                    case DOWNLOAD_COMPLETED:
                        activity.mProgressDialog.dismiss();
                        break;
                    case SEND_PICTURE:
                        Intent intent = new Intent();
                        intent.putExtra(BeautyLiveApplication.TARGET_ID, activity.mTargetId);
                        intent.putExtra(BeautyLiveApplication.GROUP_ID, activity.mGroupId);
                        intent.putExtra(BeautyLiveApplication.MsgIDs, activity.mMsgIds);
                        activity.setResult(BeautyLiveApplication.RESULT_CODE_BROWSER_PICTURE, intent);
                        activity.finish();
                        break;
                    //显示下载原图进度
                    case DOWNLOAD_ORIGIN_PROGRESS:
                        String progress = msg.getData().getInt("progress") + "%";
                        //activity.mLoadBtn.setText(progress);
                        break;
                    case DOWNLOAD_ORIGIN_COMPLETED:
                        //activity.mLoadBtn.setText(activity.getString(R.string.download_completed_toast));
                        //activity.mLoadBtn.setVisibility(View.GONE);
                        break;
                }
            }
        }
    }
}
