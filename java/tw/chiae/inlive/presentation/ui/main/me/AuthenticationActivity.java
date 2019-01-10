package tw.chiae.inlive.presentation.ui.main.me;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import tw.chiae.inlive.BeautyLiveApplication;
import tw.chiae.inlive.R;
import tw.chiae.inlive.data.bean.me.UserInfo;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.presentation.ui.base.BaseActivity;
import tw.chiae.inlive.presentation.ui.photoselect.PickPhotoUtil;
import tw.chiae.inlive.util.Const;
import tw.chiae.inlive.util.L;
import com.wx.wheelview.adapter.ArrayWheelAdapter;
import com.wx.wheelview.widget.WheelView;
import com.yalantis.ucrop.UCrop;
import com.yolanda.nohttp.Binary;
import com.yolanda.nohttp.FileBinary;
import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuthenticationActivity extends BaseActivity implements View.OnClickListener {

    private EditText nameEt,phoneEt,idEt;
    private TextView typeTv;
    private ImageView faceImg,backImg;
    private Button submitBtn;
    private boolean nameOk,phoneOk,idOk,typeOk,faceOk,backOk;
    private PopupWindow mImgPopup,mTypePopup;
    private View mImgPopupView,mTypePopupView;
    //    屏幕高度 状态栏高度
    private int stateh, xunih;
    private LinearLayout authentication_root_layout;
//    泡泡的
    private Button popuPhoto,popuCamera,popuCancel;
//    泡泡背景
    private View popubg;
    private final int IMGFACE=1;
    private final int IMGBACK=2;
    private int IMGTYPE;
    private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 裁剪结果
// 图片剪切工具
    private PickPhotoUtil mPickUtil;
//    wheelw
    private WheelView wheel;
//    记录type的位置
    private int typeOsiton;
    //    文本监听的
    private CharSequence temp;//监听前的文本
    private int editStart;//光标开始位置
    private int editEnd;//光标结束位置
    //    记录输入前的标题字数
    private int editbefor;
//  字数限制
    private final int charMaxNum = 16;
//    正面file 和背面file
    private File faceFile,backFile;
    private File tempFile;

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, AuthenticationActivity.class);
        return intent;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_authentication;
    }

    @Override
    protected void findViews(Bundle savedInstanceState) {
        mPickUtil = new PickPhotoUtil(this);
        nameEt=$(R.id.authentication_et_name);
        phoneEt=$(R.id.authentication_et_phone);
        typeTv=$(R.id.authentication_tv_type);
        idEt=$(R.id.authentication_et_id);
        faceImg=$(R.id.authentication_img_face_id);
        backImg=$(R.id.authentication_img_back_id);
        submitBtn=$(R.id.authentication_btn_submit);
        popubg=$(R.id.popo_bg);
        authentication_root_layout=$(R.id.authentication_root_layout);
        tempFile = new File(this.getExternalCacheDir(), getPhotoFileName());
        initNameEt();
        initPhoneEt();
        initIdEt();
        initView();
    }

    private void initView() {
        getdata();
        typeTv.setOnClickListener(this);
        faceImg.setOnClickListener(this);
        backImg.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
        LayoutInflater relativeLayout = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mTypePopupView = relativeLayout.inflate(R.layout.authen_popu_type_layout, null);
        wheel= (WheelView) mTypePopupView.findViewById(R.id.type_popu_wheel);
        getSortJson();
    }

    private void initIdEt() {
        idEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!idRegular(s.toString())){
                    idOk=false;
                }else {
                    idOk=true;
                }
            }
        });
    }

    private void initPhoneEt() {
        phoneEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!phoneRegular(s.toString())){
                    phoneOk=false;
                }else {
                    phoneOk=true;
                }
            }
        });
    }

    private void initNameEt() {
        nameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                temp = s;
                editbefor=s.length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!nameRegular(s.toString())){
                    nameOk=false;
                }else{
                    nameOk=true;
                }
                /** 得到光标开始和结束位置 ,超过最大数后记录刚超出的数字索引进行控制 */
                editStart = nameEt.getSelectionStart();
                editEnd = nameEt.getSelectionEnd();
//                字数限制
                if (temp.length() > charMaxNum) {
                    toastShort(getString(R.string.authentication_errormax));
                    s.delete(editStart - 1, editEnd);
                    int tempSelection = editEnd;
                    nameEt.setText(s);
                    nameEt.setSelection(tempSelection);
                }
            }
        });
    }

    @Override
    protected void init() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.authentication_tv_type:
                showTypePopu();
                break;
            case R.id.authentication_btn_submit:
                submitMessage();
                break;
            case R.id.authentication_img_face_id:
                closeKeyboard();
                showImgPopu(IMGFACE);
                break;
            case R.id.authentication_img_back_id:
                closeKeyboard();
                showImgPopu(IMGBACK);
                break;
            case R.id.authen_popu_camera:
                Intent cameraintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // 指定调用相机拍照后照片的储存路径
                cameraintent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(tempFile));
                startActivityForResult(cameraintent,
                        PHOTO_REQUEST_TAKEPHOTO);
                mImgPopup.dismiss();
                break;
            case R.id.authen_popu_photo:
                Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
                getAlbum.setType("image/*");
                startActivityForResult(getAlbum, PHOTO_REQUEST_GALLERY);
                mImgPopup.dismiss();
                break;
            case R.id.authen_popu_cancel:
                mImgPopup.dismiss();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case PHOTO_REQUEST_TAKEPHOTO:
                if (tempFile.exists()) {
                    mPickUtil.startCropActivity(Uri.fromFile(tempFile));
                }
                break;
            case PHOTO_REQUEST_GALLERY:
                // 做非空判断，当我们觉得不满意想重新剪裁的时候便不会报异常，下同
                if (data!=null&&data.getData()!=null) {
                    Uri selectedUri = data.getData();
                    if (selectedUri != null) {
                        mPickUtil.startCropActivity(data.getData());
                    } else {
                        toastShort(R.string.toast_cannot_retrieve_selected_image);
                    }
                }
                break;
            case UCrop.REQUEST_CROP:// 返回的结果
                if (data != null) {
                    Uri uri = UCrop.getOutput(data);
                    if (IMGTYPE==IMGFACE) {
                        faceImg.setImageURI(uri);
                        faceOk=true;
                        faceFile = new File(uri.getPath());
                    }else {
                        backImg.setImageURI(uri);
                        backOk=true;
                        backFile=new File(uri.getPath());
                    }
                }else {
                    toastShort(getString(R.string.toast_cannot_retrieve_cropped_image));
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showImgPopu(int type) {
        if (type==IMGFACE){
            IMGTYPE=IMGFACE;
        }else {
            IMGTYPE=IMGBACK;
        }
        if (mImgPopup == null) {
            getXuNiDpi();
            LayoutInflater relativeLayout = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mImgPopupView = relativeLayout.inflate(R.layout.authen_popu_img_layout, null);
            popuCamera= (Button) mImgPopupView.findViewById(R.id.authen_popu_camera);
            popuCamera.setOnClickListener(this);
            popuPhoto= (Button) mImgPopupView.findViewById(R.id.authen_popu_photo);
            popuPhoto.setOnClickListener(this);
            popuCancel= (Button) mImgPopupView.findViewById(R.id.authen_popu_cancel);
            popuCancel.setOnClickListener(this);
            mImgPopup = new PopupWindow(mImgPopupView,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            // 使其聚集
            mImgPopup.setFocusable(true);
            // 设置允许在外点击消失
            mImgPopup.setOutsideTouchable(true);
            // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
            mImgPopup.setBackgroundDrawable(new BitmapDrawable());
            mImgPopup.setAnimationStyle(R.style.popwin_anim_style_authen);
            mImgPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    popubg.setVisibility(View.GONE);
                }
            });
            if (xunih==0){
                popubg.setVisibility(View.VISIBLE);
                mImgPopup.showAtLocation(authentication_root_layout, Gravity.BOTTOM| Gravity.CENTER_HORIZONTAL, 0,0);
            }else {
                popubg.setVisibility(View.VISIBLE);
                mImgPopup.showAtLocation(authentication_root_layout, Gravity.BOTTOM| Gravity.CENTER_HORIZONTAL, 0, xunih);
            }
        }else{
            if (xunih==0){
                popubg.setVisibility(View.VISIBLE);
                mImgPopup.showAtLocation(authentication_root_layout, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
            }else {
                popubg.setVisibility(View.VISIBLE);
                mImgPopup.showAtLocation(authentication_root_layout, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, xunih);
            }
        }
    }

    private void showTypePopu() {
        if (mTypePopup == null) {
            getXuNiDpi();
            mTypePopup = new PopupWindow(mTypePopupView,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            wheel.setWheelSize(3);
            wheel.setWheelAdapter(new ArrayWheelAdapter(this)); // 文本数据源
            wheel.setSkin(WheelView.Skin.Holo); // common皮肤/**/
            wheel.setWheelClickable(true);
            wheel.setOnWheelItemSelectedListener(new WheelView.OnWheelItemSelectedListener() {
                @Override
                public void onItemSelected(int position, Object o) {
                    typeOsiton=position;
                }
            });
            wheel.setOnWheelItemClickListener(new WheelView.OnWheelItemClickListener() {
                @Override
                public void onItemClick(int position, Object o) {
                    typeOsiton=position;
                    mTypePopup.dismiss();
                }
            });
            WheelView.WheelViewStyle style=new WheelView.WheelViewStyle();
            style.selectedTextColor=0xffff59a5;
            style.backgroundColor=0xffffff;
            style.textColor=0xffbbbbbb;
            style.selectedTextZoom=1.3f;
            style.holoBorderColor=0xffff59a5;
            wheel.setStyle(style);
            wheel.setWheelData(list);  // 数据集合
            wheel.setSelection(typeOsiton);
            mTypePopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    popubg.setVisibility(View.GONE);
                    typeTv.setText(list.get(typeOsiton));
                    typeOk=true;
                }
            });
            // 使其聚集
            mTypePopup.setFocusable(true);
            // 设置允许在外点击消失
            mTypePopup.setOutsideTouchable(true);
            // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
            mTypePopup.setBackgroundDrawable(new BitmapDrawable());
            mTypePopup.setAnimationStyle(R.style.popwin_anim_style_authen);
            if (xunih==0){
                popubg.setVisibility(View.VISIBLE);
                mTypePopup.showAtLocation(authentication_root_layout, Gravity.BOTTOM| Gravity.CENTER_HORIZONTAL, 0,0);
            }else {
                popubg.setVisibility(View.VISIBLE);
                mTypePopup.showAtLocation(authentication_root_layout, Gravity.BOTTOM| Gravity.CENTER_HORIZONTAL, 0, xunih);
            }
        }else{
            wheel.setSelection(typeOsiton);
            if (xunih==0){
                popubg.setVisibility(View.VISIBLE);
                mTypePopup.showAtLocation(authentication_root_layout, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
            }else {
                popubg.setVisibility(View.VISIBLE);
                mTypePopup.showAtLocation(authentication_root_layout, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, xunih);
            }
        }
    }

    //    提交了
    private void submitMessage() {
        if (!nameOk){
            toastShort(getString(R.string.authentication_errorname));
            return;
        }else if (!phoneOk){
            toastShort(getString(R.string.authentication_errorphone));
            return;
        }else if (false){
            toastShort(getString(R.string.authentication_errortype));
            return;
        }else if (!idOk){
            toastShort(getString(R.string.authentication_errorid));
            return;
        }else if (!faceOk){
            toastShort(getString(R.string.authentication_errorphoto));
            return;
        }else if (!backOk){
            toastShort(getString(R.string.authentication_errorphoto));
            return;
        }else {
            showLoadingDialog();
            upLoad();
        }
    }

//    上传服务器
    private void upLoad() {
        uploadJson();
    }

// 适配屏幕
    private void getXuNiDpi() {   int dpi = 0;
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        @SuppressWarnings("rawtypes")
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics",DisplayMetrics.class);
            method.invoke(display, dm);
            dpi=dm.heightPixels;
        }catch(Exception e){
            e.printStackTrace();
        }
        xunih=dpi-getWindowManager().getDefaultDisplay().getHeight();
        Rect rect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        stateh=rect.top;
        Display disp = this.getWindowManager().getDefaultDisplay();
        Point outP = new Point();
        disp.getSize(outP);
    }

    // 使用系统当前日期加以调整作为照片的名称
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
    }

    List<String> list;
    public List<String> getdata(){
        list=new ArrayList<>();
//        list.add("御姐");
//        list.add("女王");
//        list.add("萝莉");
//        list.add("三无少女");
//        list.add("大叔");
//        list.add("宅男");
//        list.add("购物狂");
//        list.add("游戏达人");
        return list;
    }

    public  boolean startCheck(String reg,String string) {
        boolean tem=false;
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher=pattern.matcher(string);
        tem=matcher.matches();
        return tem;
    }

    public boolean nameRegular(String name){
        String regex="^[\\u4e00-\\u9fa5]+$";
//        return  startCheck(regex,name);
        return true;
    }

    public boolean phoneRegular(String phone){
        String regex="^1[3|4|5|7|8]\\d{9}$";
//        return  startCheck(regex,phone);
        return true;
    }

    public boolean idRegular(String id){
        String regex= "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X)$";
//        return  startCheck(regex,id);
        return true;
    }

    int UPLOAD = 1;
    /**
     * 用户实名认证
     *  token string
     *  name string
     *  phone string
     * sid string
     * IDCard string
     * authorpic file
     *  beforepic file
     */
    public void uploadJson() {
        Request<JSONObject> request = NoHttp.createJsonObjectRequest(Const.WEB_BASE_URL +"User/approveCheck", RequestMethod.POST);
        request.add("token", LocalDataManager.getInstance().getLoginInfo().getToken());
        request.add("name",nameEt.getText().toString());
        request.add("phone",phoneEt.getText().toString());
        request.add("sid","直播達人");
        request.add("IDCard",idEt.getText().toString());
        FileBinary facebin=new FileBinary(faceFile);
        FileBinary backbin=new FileBinary(backFile);
        request.add("authorpic",facebin);
        request.add("beforepic",backbin);

        BeautyLiveApplication.getRequestQueue().add(UPLOAD, request, OnResponse);
    }

    private OnResponseListener<JSONObject> OnResponse = new OnResponseListener<JSONObject>() {
        @Override
        public void onStart(int i) {
        }

        @Override
        public void onSucceed(int i, Response<JSONObject> response) {
            if (i == UPLOAD) {// 判断what是否是刚才指定的请求
                //                {"code":0,"msg":"ok","data":"320200"}这个是什么
                // 请求成功
                JSONObject result = response.get();// 响应结果
                Log.i("mrl","這尼瑪"+result.toString());
                try {
                    logs=result.getString("data");
                    handler.sendEmptyMessage(1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // 响应头
                Headers headers = response.getHeaders();
                headers.getResponseCode();// 响应码
                response.getNetworkMillis();// 请求花费的时间
            }
        }

        @Override
        public void onFailed(int i, String s, Object o, Exception e, int i1, long l) {
            dismissLoadingDialog();
        }

        @Override
        public void onFinish(int i) {
        }
    };

    String logs;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==1){
                dismissLoadingDialog();
                AuthenticationActivity.this.toastShort(logs);
                authentication_root_layout.setVisibility(View.GONE);
            }else if (msg.what==SORT){
                wheel.setWheelData(list);
            }
        }
    };


    final int SORT=12;
    public void getSortJson() {
        Request<JSONObject> request = NoHttp.createJsonObjectRequest(Const.WEB_BASE_URL +"User/getUsersort", RequestMethod.POST);
        request.add("token", LocalDataManager.getInstance().getLoginInfo().getToken());
        BeautyLiveApplication.getRequestQueue().add(SORT, request, OnSortResponse);
    }

    private OnResponseListener<JSONObject> OnSortResponse = new OnResponseListener<JSONObject>() {
        @Override
        public void onStart(int i) {
        }

        @Override
        public void onSucceed(int i, Response<JSONObject> response) {
            if (i == SORT) {// 判断what是否是刚才指定的请求
                //                {"code":0,"msg":"ok","data":"320200"}这个是什么
                // 请求成功
                JSONObject result = response.get();// 响应结果
                try {
                    JSONArray jsonArray=result.getJSONArray("data");
                    for (int j=0;j<jsonArray.length();j++){
                        list.add(jsonArray.getJSONObject(j).getString("sortname"));
                    }
                    handler.sendEmptyMessage(SORT);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // 响应头
                Headers headers = response.getHeaders();
                headers.getResponseCode();// 响应码
                response.getNetworkMillis();// 请求花费的时间
            }
        }

        @Override
        public void onFailed(int i, String s, Object o, Exception e, int i1, long l) {
        }

        @Override
        public void onFinish(int i) {
        }
    };

    private void closeKeyboard() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void setCoinData(int currencyItemSum) {

    }
}
