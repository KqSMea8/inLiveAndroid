package tw.chiae.inlive.presentation.ui.room.pubmsg;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.CheckResult;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.BitmapCompat;
import android.support.v7.widget.ViewUtils;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.MetricAffectingSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.ReplacementSpan;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import tw.chiae.inlive.BeautyLiveApplication;
import tw.chiae.inlive.R;
import tw.chiae.inlive.data.bean.websocket.UserPublicMsg;
import tw.chiae.inlive.presentation.ui.main.mergefilm.Log;
import tw.chiae.inlive.presentation.ui.widget.heardAnim.HeartUtil;
import tw.chiae.inlive.presentation.ui.widget.roomanim.GitfSpecialsStop;
import tw.chiae.inlive.presentation.ui.widget.roomanim.PlaneImagerView;
import tw.chiae.inlive.util.Const;
import tw.chiae.inlive.util.L;
import tw.chiae.inlive.util.PicUtil;
import tw.chiae.inlive.util.Spans;

/**
 * @author Muyangmin
 * @since 1.0.0
 * 发送的信息工具
 */
public final class MsgUtils implements GitfSpecialsStop {

    public static final String LOG_TAG = "MsgUtils";

    private static MsgUtils instance;
    private final Context mContext;
    private final int colorSystemTip;

    //private SparseArray<CharSequence> levelSequence = new SparseArray<>(128);
    private static final boolean CACH_IMAGE_ENABLED = false;

    //   用户名称颜色
    @ColorInt
    private int colorUsername;
    //    用户发送的信息颜色
    @ColorInt
    private int colorPublicMsgContent;
    //  系统发出的警告颜色
    @ColorInt
    private int colorPublicSysMsgContent;
    //  系统发出的欢迎颜色
    private int colorPrvMsgContent;
    @ColorInt
    private int colorPublicSysMsgWelcome;

    private int[] heartColorArray;

    private String[] sArtistType_cn = {"無","星級藝人","普通藝人","金牌藝人","官方","特約藝人"};
    private String[] sArtistType_tw = {"无","星级艺人","普通艺人","金牌艺人","官方","特约艺人"};
    private int[] starticon = new int[]{0, R.drawable.id_star, R.drawable.id_vip, R.drawable.id_gold,R.drawable.id_in,R.drawable.id_specialicon};

    private MsgUtils(Context context) {
        this.mContext = context;
        colorSystemTip = getColor(context, R.color.yunkacolor_system);
        colorUsername = getColor(context, R.color.yunkacolor_name);
        colorPublicMsgContent = getColor(context, R.color.item_public_msg_content);
        colorPublicSysMsgContent = getColor(context, R.color.continue_gift_x_border);
        colorPublicSysMsgWelcome = getColor(context, R.color.item_public_sys_welcom);
        colorPrvMsgContent = getColor(context, R.color.black);
        heartColorArray = context.getResources().getIntArray(R.array.room_heart_colors);
    }

    @ColorInt
    private int getColor(Context context, @ColorRes int color) {
        return ContextCompat.getColor(context, color);
    }

    public static MsgUtils getInstance() {
        if (instance == null) {
            synchronized (MsgUtils.class) {
                if (instance == null) {
                    instance = new MsgUtils(BeautyLiveApplication.getContextInstance());
                }
            }
        }
        return instance;
    }

    public CharSequence buildUserName(@NonNull String username) {
        return Spans.createSpan("", String.format("%s", username),
                new ForegroundColorSpan(colorUsername));
    }

    public CharSequence buildPublicMsgContent(@NonNull String msg) {
        return Spans.createSpan("", msg, new ForegroundColorSpan(colorPublicMsgContent));
    }

    public CharSequence buildPublicSysMsgContent(@NonNull String msg) {
        return Spans.createSpan("", msg, new ForegroundColorSpan(colorPublicSysMsgContent));
    }

    public CharSequence buildPublicSysMsgWelcome(@NonNull String welcome) {
        return Spans.createSpan("", welcome, new ForegroundColorSpan(colorPublicSysMsgWelcome));
    }

    public CharSequence buildPublicSysMsgName(@NonNull String msg) {
        return Spans.createSpan("", msg, new ForegroundColorSpan(colorUsername));
    }

    public CharSequence buildPrvMsgContent(@NonNull String msg) {
        return Spans.createSpan("", msg, new ForegroundColorSpan(colorPrvMsgContent));
    }

    public CharSequence buildVipContent(@NonNull int level,@NonNull CharSequence name,int size , String approveid) {
        int VipresId = PicUtil.getVipLevelImageId(mContext, level);
        CharSequence isVip="";
        size = size-10;
        if(approveid!=null && approveid.contains("貴賓")){
            Drawable drawableCrown = mContext.getResources().getDrawable(R.drawable.crown);
            int width = size;
            int height = size;
            drawableCrown.setBounds(0, 0, width, height);
            NameImageSpan imageSpan = new NameImageSpan(drawableCrown,ImageSpan.ALIGN_BASELINE);
            SpannableStringBuilder VipstringBuilder = new SpannableStringBuilder("vipApproveid");
            VipstringBuilder.setSpan(imageSpan,0,"vipApproveid".length(),Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            isVip = VipstringBuilder;
        }

        if(VipresId<=0 || Const.LevelEnterSW==0)
            return TextUtils.concat(" ",isVip,name);
        else{
            Drawable d = mContext.getResources().getDrawable(VipresId);
            int width = (d.getIntrinsicWidth()*size)/d.getIntrinsicHeight();
            int height = size;
            d.setBounds(0, 0, width, height);
            NameImageSpan imageSpan = new NameImageSpan(d,ImageSpan.ALIGN_BASELINE);
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder("level");
            stringBuilder.setSpan(imageSpan,0,"level".length(),Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            return TextUtils.concat(stringBuilder," ",isVip,name);
        }

    }

    @Nullable
    @CheckResult
    public CharSequence buildLevel(int level) {
        if (level <= 0) {
            //not supported at all
            level = 1;
        } if (level >=Const.MaxLevel) {
            //not supported at all
            level = Const.MaxLevel;
        }
        /*if (CACH_IMAGE_ENABLED) {
            CharSequence cached = levelSequence.get(level);
            //Cache hit
            if (cached != null) {
                L.v(false, LOG_TAG, "Use cached sequence for this level.");
                return cached;
            }
        }*/
        if(level<=0)
            level = 1;
        //not hit
        Context context = BeautyLiveApplication.getContextInstance();
        int resId = PicUtil.getLevelImageId(context, level);
        L.i(LOG_TAG, "drawable resource id for level %d is %d", level, resId);

        //use customize size
        int width = context.getResources().getDimensionPixelSize(R.dimen.user_level_width);
        int height = context.getResources().getDimensionPixelSize(R.dimen.user_level_height);
  /*      Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
        if (bitmap == null) {
            L.e(LOG_TAG, "Cannot decode bitmap for level %d!", level);
            return null;
        }
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        L.i(LOG_TAG, "scaled bitmap：w=%d, h=%d, size=%d", scaledBitmap.getWidth(),
                scaledBitmap.getHeight(), BitmapCompat.getAllocationByteCount(scaledBitmap));*/
        if(resId<=0)
            resId = PicUtil.getLevelImageId(context, 1);
        Drawable d = context.getResources().getDrawable(resId);
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());


        ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
        SpannableStringBuilder ssb = new SpannableStringBuilder(String.valueOf(level));
        ssb.setSpan(span, 0, ssb.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        //levelSequence.put(level, ssb);
        return ssb;
    }

/*    public CharSequence buildHeart(int colorIndex) {
        int color = heartColorArray[colorIndex];

        Context context = BeautyLiveApplication.getContextInstance();
        int width = context.getResources().getDimensionPixelSize(R.dimen.room_heart_fixed_size);
        int height = context.getResources().getDimensionPixelSize(R.dimen.room_heart_fixed_size);
        //Must use a BitmapConfig with Alpha channel!
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
//        canvas.drawColor(color);
        //HeartUtil.drawHeart(canvas, 0.72F, color);

        ImageSpan span = new ImageSpan(context, bitmap);
        SpannableStringBuilder ssb = new SpannableStringBuilder(String.valueOf(colorIndex));
        ssb.setSpan(span, 0, ssb.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        levelSequence.put(colorIndex, ssb);
        return ssb;
    }*/

    @Override
    public void animend() {

    }

    public CharSequence buildPublicSysMsgTip(String msg) {
        return Spans.createSpan("", msg, new ForegroundColorSpan(colorSystemTip));
    }

    public CharSequence buildChatNameContent(UserPublicMsg msg, int Size, String IdentityId) {
        Context context = BeautyLiveApplication.getContextInstance();
        View view = LayoutInflater.from(context).inflate(R.layout.item_room_public_chat_tag, null);
        TextView tvName = (TextView) view.findViewById(R.id.tv_pub_msg);
        int resId = PicUtil.getLevelImageId(context, msg.getLevel());
        ImageView ivLevel =(ImageView) view.findViewById(R.id.iv_pub_msg);
        ImageView iv_pub_approeid =(ImageView) view.findViewById(R.id.id_icon);
        CharSequence nameContent = buildVipContent(msg.getLevel(),msg.getFromClientName(),Size+10,msg.getApproveid());
        tvName.setText(nameContent);

        ivLevel.setImageResource(resId);
        setApproveidIcon(iv_pub_approeid,IdentityId);
        Drawable mDrawable = ViewToDrawable(view, tvName.getText().length() + 3);

        float ratio = (float) Size / (float) mDrawable.getIntrinsicHeight()+0.3f;
        int imgWidth = (int) ((float) mDrawable.getIntrinsicWidth() * ratio);
        int imgHeight = (int) ((float) mDrawable.getIntrinsicHeight() * ratio);
        mDrawable.setBounds(0, 0, imgWidth, imgHeight);
        CustomImageSpan span = new CustomImageSpan(mDrawable, ImageSpan.ALIGN_BASELINE);
        CustomSpannableStringBuilder ssb = new CustomSpannableStringBuilder(String.valueOf(msg.getLevel()));

        ssb.setupSpan(span, 0, ssb.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        //levelSequence.put(msg.getLevel(), ssb);
        return ssb;
    }

    public CharSequence buildEnterEffectContent(String name, int Size, String IdentityId , int level , int textcolor) {
        Log.i("RayTest","跑進場特效！");
        Context context = BeautyLiveApplication.getContextInstance();
        View view = LayoutInflater.from(context).inflate(R.layout.item_room_entereffect_tag, null);
        TextView tvName = (TextView) view.findViewById(R.id.tv_pub_msg);
        tvName.setTextColor(textcolor);
        int resId = PicUtil.getLevelImageId(context, level);
        ImageView ivLevel =(ImageView) view.findViewById(R.id.iv_pub_msg);
        ImageView iv_vip =(ImageView) view.findViewById(R.id.id_vip);
        tvName.setText(name);
        int VipresId = PicUtil.getVipLevelImageId(context, level);
        if(VipresId>0){
            iv_vip.setVisibility(View.VISIBLE);
            iv_vip.setImageResource(VipresId);
        }else
            iv_vip.setVisibility(View.GONE);
        ivLevel.setImageResource(resId);
        Drawable mDrawable = ViewToDrawable(view, tvName.getText().length() + 3);
        float ratio = (float) Size / (float) mDrawable.getIntrinsicHeight()+0.3f;
        int imgWidth = (int) ((float) mDrawable.getIntrinsicWidth() * ratio);
        int imgHeight = (int) ((float) mDrawable.getIntrinsicHeight() * ratio);
        mDrawable.setBounds(0, 0, imgWidth, imgHeight);
        CustomImageSpan span = new CustomImageSpan(mDrawable, ImageSpan.ALIGN_BASELINE);
        SpannableStringBuilder ssb = new SpannableStringBuilder(String.valueOf(level));
        ssb.setSpan(span, 0, ssb.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        //levelSequence.put(level, ssb);
        return ssb;
    }

 /*   public CharSequence buildNameContent(String name,   int level ) {
        Context context = BeautyLiveApplication.getContextInstance();
        int VipresId = PicUtil.getVipLevelImageId(context, level);
        Drawable
        ImageSpan span =new NameImageSpan(mContext, VipresId);
        SpannableStringBuilder ssb = new SpannableStringBuilder("drawble "+name);

        ssb.setSpan(span, 0, "drawble ".length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        //levelSequence.put(level, ssb);
        return ssb;
    }*/

    private void setVipIcon(ImageView iv_vip, int level) {

    }

    public void setApproveidIcon(ImageView icon, String approveid) {
        int approveidflag = -1;
        if(approveid!=null) {
            if (approveid.equals(sArtistType_cn[0]) || approveid.equals(sArtistType_tw[0]))
                approveidflag = 0;
            if (approveid.equals(sArtistType_cn[1]) || approveid.equals(sArtistType_tw[1]))
                approveidflag = 1;
            if (approveid.equals(sArtistType_cn[2]) || approveid.equals(sArtistType_tw[2]))
                approveidflag = 2;
            if (approveid.equals(sArtistType_cn[3]) || approveid.equals(sArtistType_tw[3]))
                approveidflag = 3;
            if (approveid.contains(sArtistType_cn[4]) || approveid.contains(sArtistType_tw[4]))
                approveidflag = 4;
            if (approveid.contains(sArtistType_cn[5]) || approveid.contains(sArtistType_tw[5]))
                approveidflag = 5;
        }
        switch (approveidflag)
        {
            case 0:
                icon.setVisibility(View.GONE);
                break;
            case 1:
                icon.setVisibility(View.VISIBLE);
                icon.setImageResource(starticon[1]);
                break;
            case 2:
                icon.setVisibility(View.VISIBLE);
                icon.setImageResource(starticon[2]);
                break;
            case 3:
                icon.setVisibility(View.VISIBLE);
                icon.setImageResource(starticon[3]);
                break;
            case 4:
                icon.setVisibility(View.VISIBLE);
                icon.setImageResource(starticon[4]);
                break;
            case 5:
                icon.setVisibility(View.VISIBLE);
                icon.setImageResource(starticon[5]);
                break;
            default:
                icon.setVisibility(View.GONE);
                break;

        }
    }


    public Drawable ViewToDrawable(View view, int size) {

        Bitmap snapshot = convertViewToBitmap(view, size);
        Drawable drawable = (Drawable) new BitmapDrawable(snapshot);
        return drawable;
    }

    public static Bitmap convertViewToBitmap(View view, int size) {
 /*       view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        int width = size*40;
        Log.i("RayTest","convertViewToBitmap w:"+width+"  h:"+view.getMeasuredHeight());
        view.layout(0, 0, width, view.getMeasuredHeight());  //根据字符串的长度显示view的宽度*/

        view.setDrawingCacheEnabled(true);
        view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(),
                view.getMeasuredHeight());

        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }

    public CharSequence buildTag(String sTag,int level, int Size) {


        Context context = BeautyLiveApplication.getContextInstance();
        View view = LayoutInflater.from(context).inflate(R.layout.item_room_public_chat_systag, null);
        TextView tvFlag = (TextView) view.findViewById(R.id.item_public_chat_flag_tv);
        int resId = PicUtil.getLevelImageId(context, level);
        ImageView ivLevel =(ImageView) view.findViewById(R.id.iv_pub_msg);
        tvFlag.setText(sTag);
        if(level==0)
            ivLevel.setVisibility(View.GONE);
        else {
            ivLevel.setVisibility(View.VISIBLE);
            ivLevel.setImageResource(resId);
        }

        Drawable mDrawable = ViewToDrawable(view, tvFlag.getText().length() + 3);
        float ratio = (float) Size / (float) mDrawable.getIntrinsicHeight()+0.3f;
        int imgWidth = (int) ((float) mDrawable.getIntrinsicWidth() * ratio);
        int imgHeight = (int) ((float) mDrawable.getIntrinsicHeight() * ratio);
        mDrawable.setBounds(0, 0, imgWidth, imgHeight);
        CustomImageSpan span = new CustomImageSpan(mDrawable, ImageSpan.ALIGN_BASELINE);
        SpannableStringBuilder ssb = new SpannableStringBuilder(String.valueOf(level));
        ssb.setSpan(span, 0, ssb.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        //levelSequence.put(level, ssb);
        return ssb;
    }

    public class CustomImageSpan extends ImageSpan {
        public CustomImageSpan(Drawable d, int verticalAlignment) {
            super(d, verticalAlignment);
        }

        public CustomImageSpan(Context arg0, int arg1) {
            super(arg0, arg1);
        }

        public int getSize(Paint paint, CharSequence text, int start, int end,
                           Paint.FontMetricsInt fm) {
            Drawable d = getDrawable();
            Rect rect = d.getBounds();
            if (fm != null) {
                Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
                int fontHeight = fmPaint.bottom - fmPaint.top;
                int drHeight = rect.bottom - rect.top;

                int top = drHeight / 2 - fontHeight / 4;
                int bottom = drHeight / 2 + fontHeight / 4;

                fm.ascent = -bottom;
                fm.top = -bottom;
                fm.bottom = top;
                fm.descent = top;
            }
            return rect.right;
        }

        public void recycleBitmapView(){
            BitmapDrawable bitmapDrawable = (BitmapDrawable) getDrawable();
            bitmapDrawable.getBitmap().recycle();
        }

        @Override
        public void draw(Canvas canvas, CharSequence text, int start, int end,
                         float x, int top, int y, int bottom, Paint paint) {
            if(getDrawable() instanceof BitmapDrawable){
                if (((BitmapDrawable)getDrawable()).getBitmap().isRecycled()) {
                    return;
                }
            }

            Drawable b = getDrawable();
            BitmapDrawable bd = (BitmapDrawable) getDrawable();
            if(b!=null|| !bd.getBitmap().isRecycled()){
                try {
                    canvas.save();
                    int transY = 0;
                    transY = ((bottom - top) - b.getBounds().bottom) / 2 + top;
                    canvas.translate(x, transY);
                    b.draw(canvas);
                }catch (Exception e){
                    Log.i("RayBitmap","bitmap is isRecycled 1!");
                }



            }else{
                Log.i("RayBitmap","bitmap is isRecycled! 2");
            }
            canvas.restore();
        }
    }

    public class CustomSpannableStringBuilder extends SpannableStringBuilder {


        private CustomImageSpan mImageSpan = null;
        private RemoveListener mListener;

        public CustomSpannableStringBuilder(CharSequence text) {
            super(text);
        }

        public void setupSpan(Object what, int i, int length, int spanInclusiveExclusive) {
            if(what instanceof CustomImageSpan)
                this.mImageSpan = (CustomImageSpan) what;
            setSpan(what, 0, length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }

        public CustomImageSpan getImageSpan (){
            if(mImageSpan!=null){
                return mImageSpan;
            }
            return null;
        }

        public void setRemoveListener(RemoveListener listener){
            this.mListener = listener;
        }
    }

    public interface RemoveListener{}


}
